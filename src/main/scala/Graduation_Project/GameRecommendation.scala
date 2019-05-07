package Graduation_Project

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/5/7 09:21
  * @类描述: 个性化推荐整合类
  */
object GameRecommendation {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("GameRecommendation").master("local").getOrCreate()
    //游戏相似度矩阵数据
    val matrixDF: DataFrame = spark.read.json("Game/matrix").select("id", "uid", "source")
    //用户游戏记录信息数据
    val userDF: DataFrame = spark.read.json("Game/userdata").select("id","games")
    //冷启动池数据
    val coldDF: DataFrame = spark.read.json("Game/coldstart").select("gameid","count")

    //将游戏相似矩阵转换为Map集合
    val matrixMap: Map[String, String] = matrixDF.rdd.map(row => {
      val id = row.getString(0)
      val uid = row.getString(1)
      val source = row.getDouble(2)
      (id, "【相似id号:" + uid + " 相似度:" + source + "】")
    }).collect().toMap
    //热门游戏Top3
    val top3: Array[String] = coldDF.select("gameid").rdd.map(_.getString(0)).take(5)
    //将相似矩阵数据广播
    val matrixBC: Broadcast[Map[String, String]] = spark.sparkContext.broadcast(matrixMap)

    userDF.rdd.map(row =>{
      //得到游戏相似矩阵数据
      val map = matrixBC.value
      var list = List[String]()
      //用户id
      val id = row.getString(0)
      //用户游戏记录
      val games = row.getString(1)
      if(games.length < 1){
        (id,top3.mkString(" == "))
      }else{
        //将游戏记录切分成每款游戏
        val splits = games.split(",")
        for(game <- splits){
          val value = map.getOrElse(game,"")
          list :+= value
        }
        (id,list.mkString(" == "))
      }
    }).foreach(println)

    spark.close()

  }
  case class Matrix(id:String,uid:String)
}
