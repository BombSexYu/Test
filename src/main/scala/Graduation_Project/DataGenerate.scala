package Graduation_Project

import java.util.UUID

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.mutable
import scala.util.Random

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/5/5 08:09
  * @类描述: 数据生成类
  */
object DataGenerate {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Data Generate").master("local").getOrCreate()

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

    spark.createDataFrame(userData).write.json("Game/userdata")


    spark.close()
  }
  case class GameData(id:String,games:String)
}
