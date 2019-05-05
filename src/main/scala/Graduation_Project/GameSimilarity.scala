package Graduation_Project

import breeze.numerics.sqrt
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable

/**
  * @类描述: 计算游戏相似度矩阵
  */
object GameSimilarity {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Game Similarity")
      .master("local").getOrCreate()
    //获取用户游戏记录
    val userDF: DataFrame = spark.read.json("Game/userdata")
    //过滤掉没有游戏数据的用户
    val filterDF: Dataset[Row] = userDF
      .select("id","games").filter("games!=''")
    import spark.implicits._
    //游戏对应的用户数据
    val gameDF: Dataset[Row] = filterDF.transform(ds => {
      ds.rdd.flatMap(row => {
        val set = new mutable.HashSet[(String,String)]()
        val id = row.getString(0)
        val games = row.getString(1).split(",")
        for (uid <- games)
          set.add((uid,id))
        set
      }).toDF("gameid", "id")
    })
    //将游戏数据转换为RDD进行操作
    val gameRDD: RDD[Row] = gameDF.rdd
    //按游戏对数据进行分组
    val groupRDD: RDD[(String, Iterable[Row])] = gameRDD.groupBy(_.getString(0))
    //每款游戏玩过的用户数据
    val gameListRDD: RDD[(String, mutable.HashSet[String])] = groupRDD.map(row => {
      val id = row._1
      val set = new mutable.HashSet[String]()
      for (r <- row._2) {
        val userID = r.getString(1)
        set.add(userID)
      }
      (id, set)
    }).persist(StorageLevel.MEMORY_AND_DISK_SER)
    //对数据进行广播
    val broad: Broadcast[Map[String, mutable.HashSet[String]]] = spark.
      sparkContext.broadcast(gameListRDD.collect().toMap)
    //只保留游戏相似度最高的记录信息，并写出到文件
    gameListRDD.map(row => {
      val id = row._1
      val userID = row._2
      //相似度最高的游戏ID
      var maxID = ""
      //相似度最高分
      var max = 0.0
      for(r <- broad.value){
        val uid = r._1
        if(id != uid) {
          val userSet = r._2
          //求两个游戏用户的交集
          val x = (userID & userSet).size
          val y: Double = sqrt((userID | userSet).size)
          if(x/y > max){
            max = x/y
            maxID = uid
          }
        }
      }
      (id,maxID,max)
    }).toDF("id","uid","source").write.json("Game/matrix")
    //停止任务
    spark.close()
  }
}
