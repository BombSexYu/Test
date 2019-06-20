package Test

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/6/18 16:56
  * @类描述:
  */
object NumberTest {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Number Test").setMaster("local")
    val sc = new SparkContext(conf)
    //模拟生成数据RDD
    val data: RDD[Int] = sc.parallelize(List(1,1,2,2,3,3,1,1,3)).cache()

    //1、统计每个数字出现的频率
    data.map((_,1)).reduceByKey(_+_).foreach(w => println(w._1,w._2))

    //2、对数字进行排序
    data.sortBy(+_).foreach(w => print(w + "  "))
    println()

    //3、对数字进行去重
    data.distinct().foreach(w =>print(w + "  "))
    println()

  }
}
