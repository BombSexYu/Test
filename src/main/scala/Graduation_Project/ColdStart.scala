package Graduation_Project

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.collection.mutable

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/5/7 09:44
  * @类描述: 冷启动池中的热门游戏数据
  */
object ColdStart {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Cold Start").master("local").getOrCreate()

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
    //统计每款游戏的用户数
    val countDF: DataFrame = gameDF.groupBy("gameid").count()

    //按用户数降序排列，生成热门游戏排名
    countDF.orderBy(-countDF("count")).write.json("Game/coldstart")



  }
}
