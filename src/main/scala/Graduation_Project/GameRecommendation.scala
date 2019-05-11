package Graduation_Project

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @类描述: 个性化推荐整合类
  */
object GameRecommendation {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("GameRecommendation").master("local").getOrCreate()
    //游戏相似度矩阵数据
    val matrixDF: DataFrame = spark.read.json("hdfs://hadoop:8020/Game/matrix").select("id", "uid", "source")
    //用户游戏记录信息数据
    val userDF: DataFrame = spark.read.json("hdfs://hadoop:8020/Game/userdata").select("id","games")
    //冷启动池数据
    val coldDF: DataFrame = spark.read.json("hdfs://hadoop:8020/Game/coldstart").select("gameid","count")

    import spark.implicits._
    //将游戏相似矩阵转换为Map集合
    val matrixMap: Map[String, String] = matrixDF.rdd.map(row => {
      val id = row.getString(0)
      val uid = row.getString(1)
      val source = row.getDouble(2)
      (id, "uid:" + uid + "##source:" + source + "  ")
    }).collect().toMap
    //热门游戏Top3
    val top3: Array[String] = coldDF.select("gameid").rdd.map(_.getString(0)).take(3)
    //将相似矩阵数据广播
    val matrixBC: Broadcast[Map[String, String]] = spark.sparkContext.broadcast(matrixMap)
    //遍历用户数据，进行个性化推荐
    val resultDF: DataFrame = userDF.rdd.map(row => {
      //得到游戏相似矩阵数据
      val map = matrixBC.value
      var list = List[String]()
      //用户id
      val id = row.getString(0)
      //用户游戏记录
      val games = row.getString(1)
      if (games.length < 1) {
        (id, top3.mkString(" "))
      } else {
        //将游戏记录切分成每款游戏
        val splits = games.split(",")
        for (game <- splits) {
          val value = map.getOrElse(game, "")
          list :+= value
        }
        (id, list.mkString(" "))
      }
    }).toDF("id", "uid")

    //如果文件已经存在，则需要先删除
    val hdfs: FileSystem = FileSystem.get(new URI("hdfs://hadoop:8020"),new Configuration())
    if(hdfs.exists(new Path("/Game/result"))){
      println("删除上一次个性化推荐数据!")
      hdfs.delete(new Path("/Game/result"),true)
    }

    //打印10行数据查看是否正确
    resultDF.show(false)
    //将最终推荐结果写出到HDFS
    resultDF.write.json("hdfs://hadoop:8020/Game/result")
    println("个性化推荐结果写出完成!")

    spark.close()

  }
  case class Matrix(id:String,uid:String)
}
