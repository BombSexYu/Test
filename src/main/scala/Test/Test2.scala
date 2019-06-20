package Test

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/6/18 17:06
  * @类描述:
  */
object Test2 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Test2")
      .master("local[2]")
      .config("spark.sql.warehouse.dir","warehouse")
      .getOrCreate()

    val frame = spark.read.format("jdbc")
      .option("url", "jdbc:mysql://rm-bp175xy49lbhr9oe78o.mysql.rds.aliyuncs.com:3306/sitech_id?useUnicode=true&characterEncoding=utf8")
      .option("dbtable", "t_user")
      .option("user", "root")
      .option("password", "Xtev@2018!@#$")
      .load()

//    val spark = SparkSession.builder().appName(this.getClass.getName).master("local").getOrCreate()
//    var frame = spark.read.format("jdbc")
//      .option("url", "jdbc:mysql://rm-bp1c6a07aypw1u95uoo.mysql.rds.aliyuncs.com:3306/sitech_id?useUnicode=true&characterEncoding=utf8")
//      .option("dbtable", "t_user")
//      .option("user", "datesync")
//      .option("password", "Xtev#33d2")
//      .load()


    import spark.implicits._

    frame.select("education","create_time").rdd.map(row => {
      if(row.isNullAt(0) || row.getString(0).size==0)
        ("null",row.getLong(1))
      else {
        (row.getString(0), row.getLong(1))
      }
    }).toDF("education","create_time").createOrReplaceTempView("tmp")

    spark.udf.register("cdate",(time:String)=>CDate(time))
    spark.udf.register("dateToLong",(time:String)=>dateToLong(time))
    val sql =
      """
        |select education,dateToLong(cdate(create_time)) date,count(1) net_increase from tmp group by education,cdate(create_time)
      """.stripMargin
//
    spark.sql(sql).show(false)
//
    val prop = new Properties()
    prop.setProperty("user","root")
    prop.setProperty("password","Xtev@2018!@#$")
    spark.sql(sql).write.mode(SaveMode.Append).jdbc("jdbc:mysql://rm-bp175xy49lbhr9oe78o.mysql.rds.aliyuncs.com:3306/sitech_bigdata_operation?useUnicode=true&characterEncoding=utf8","t_basic_user_education",prop)


    spark.stop()



  }

  def CDate(time:String):String={
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val date = dateFormat.format(new Date(time.toLong))
    date
  }

  def dateToLong(tm:String) :Long={
    val fm = new SimpleDateFormat("yyyy-MM-dd")
    val dt = fm.parse(tm)
    val tim: Long = dt.getTime()
    tim
  }



}
