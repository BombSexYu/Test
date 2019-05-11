package Graduation_Project

import java.net.URI
import java.util.UUID

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable
import scala.util.Random

/**
  * @类描述: 模拟数据生成类
  */
object DataGenerate {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Data Generate")
      .master("local").getOrCreate()

    //存储用户id
    var userList = List[String]()
    //存储游戏id
    var gameList = List[String]()

    //存储每个用户玩过的游戏id
    val set = new mutable.HashSet[String]()

    while(userList.size < 10000){
      userList :+= UUID.randomUUID().toString
    }
    while(gameList.size < 100){
      gameList :+= UUID.randomUUID().toString
    }

    val data: RDD[Int] = spark.sparkContext.parallelize(0 until 10000)

    //生成的用户数据
    val userData: RDD[GameData] = data.map(i => {
      //清空上一个用户的数据
      set.clear()
      //用户id
      val id = userList(i)
      //用户玩过的游戏数
      val x = Random.nextInt(5)
      while (set.size < x) {
        val k = Random.nextInt(gameList.size)
        set.add(gameList(k))
      }
      new GameData(id, set.mkString(","))
    })

    //如果文件已经存在，则需要先删除
    val hdfs: FileSystem = FileSystem.get(new URI("hdfs://hadoop:8020"),new Configuration())
    if(hdfs.exists(new Path("/Game/userdata"))){
      println("删除上一次用户模拟数据!")
      hdfs.delete(new Path("/Game/userdata"),true)
    }

    //将用户游戏数据转换为DataFrame
    val userDF: DataFrame = spark.createDataFrame(userData)
    //输出10行数据查看是否正确
    userDF.show(false)
    //数据写出到HDFS
    userDF.write.json("hdfs://hadoop:8020/Game/userdata")

    println("模拟数据写入HDFS完成!")

    spark.close()
  }
  case class GameData(id:String,games:String)
}
