import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/3/23 12:56
  * @类描述:
  */
object SparkTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkTest").setMaster("local")
    val sc = new SparkContext(conf)

    val data: RDD[Int] = sc.parallelize(List(1,4,5,6,7,3))
    data.filter(_ > 4).collect().foreach(print)


  }
}
