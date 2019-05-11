package Graduation_Project

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.collection.mutable

/**
  * @类描述: 冷启动池中的热门游戏数据
  */
object ColdStart {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Cold Start").master("local").getOrCreate()
    val userDF: DataFrame = spark.read.json("hdfs://hadoop:8020/Game/userdata")
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
    val result: Dataset[Row] = countDF.orderBy(-countDF("count"))

    //如果文件已经存在，则需要先删除
    val hdfs: FileSystem = FileSystem.get(new URI("hdfs://hadoop:8020"),new Configuration())
    if(hdfs.exists(new Path("/Game/coldstart"))){
      println("删除上一次冷启动池数据!")
      hdfs.delete(new Path("/Game/coldstart"),true)
    }

    //输出10行测试数据
    result.show(false)
    //将结果写入HDFS
    result.write.json("hdfs://hadoop:8020/Game/coldstart")
    println("冷启动池中数据写出完成!")
    spark.close()
  }
}
