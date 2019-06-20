package Test

import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession

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

    val train = spark.read.format("libsvm").load("dir/test.data")

    val lr = new LinearRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    // Fit the model
    val lrModel = lr.fit(train)

    //打印线性回归的系数和截距
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    //5出现的频率应该是 1到n的累加除以5
    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")

  }
}
