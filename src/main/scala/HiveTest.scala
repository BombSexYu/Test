import org.apache.spark.sql.SparkSession

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/1 14:23
  * @类描述:
  */
object HiveTest {

  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().appName("Hive Test").master("172.31.236.156").enableHiveSupport().getOrCreate()

    ss.sql(
      """
        |select user_id,target_id,act_type from userlog where user_id='962d7b81f5af4699960a5dbc9468cec1';
      """.stripMargin).show(10)
    ss.close()


  }
}
