import java.text.SimpleDateFormat
import java.util
import java.util.Date
import java.util.regex.{Matcher, Pattern}

import com.google.common.base.Strings
import org.ansj.domain.Term
import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/3 18:00
  * @类描述:
  */
object Test {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("AdsCtrPredictionLR")
      .master("local[2]")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    println(NowDate())

  }

  def NowDate(): String = {
    val now: Date = new Date()
//    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val date = dateFormat.format(now)
    return date
  }



  case class VersionNum(str:String) extends Ordered[VersionNum]{
    override def compare(that: VersionNum): Int = {
      val arr1 = this.str.split("\\.")
      val arr2 = that.str.split("\\.")
      //先比较版本号的前两位
      for(i <- 0 until 2){
        if(arr1(i) != arr2(i))
          return Integer.valueOf(arr1(i)) - Integer.valueOf(arr2(i))
      }
      //当两个版本号长度不一样时，长的大
      if(arr1.size != arr2.size)
        return arr1.size - arr2.size
      //当两个版本号长度都为2时，相等
      if(arr1.size < 3)
        return 0
      //当两个版本号长度都为3时，比较
      return Integer.valueOf(arr1(2)) - Integer.valueOf(arr2(2))
    }
  }
}
