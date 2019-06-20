import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/6/1 08:45
  * @类描述:
  */
object DenseRankTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Test")
      .master("local")
      .getOrCreate()

    import spark.implicits._
    val ds: Dataset[String] = spark.read.textFile("dir/version")
    val sourceDF: DataFrame = ds.rdd.map(row => {
      (row, fun(row))
    }).toDF("version", "source")

    //创建临时表
    sourceDF.createOrReplaceTempView("tab")
    val sql = "select version,DENSE_RANk() OVER(ORDER BY source DESC) AS rank from tab"
    //排名后的结果
    val result: DataFrame = spark.sql(sql)
    result.take(1).foreach(row => println("最大版本号为: " + row.getString(0)))

    println("排名结果为:")
//    result.transform(ds => ds.map(row => (row.getString(0),row.getInt(1)-1))).show(false)
    result.rdd.map(row => (row.getString(0),row.getInt(1)-1)).toDF("版本号","序号").show(false)
    spark.close()
  }

  def fun(str:String): Int ={
    val arr = str.split("\\.")
    val len = arr.length
    //根据(?:[1-9]\d|[1-9])(\.(?:[1-9]\d|\d)){1,2}可知每段版本号为两位数
    if(len < 3)
      Integer.valueOf(arr(0))*1000 + Integer.valueOf(arr(1))*100
    else
      Integer.valueOf(arr(0))*1000 + Integer.valueOf(arr(1))*100 + Integer.valueOf(arr(2))
  }

}
