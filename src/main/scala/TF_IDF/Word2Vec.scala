package TF_IDF

import org.apache.hadoop.hive.ql.exec.UDF
import org.apache.spark.SparkConf
import org.apache.spark.ml.feature._
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/16 14:03
  * @类描述:
  */
object Word2Vec {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder().appName("Word to Vec").master("local[2]").getOrCreate()
    //输入数据，每行作为一个词袋
    val documentDF: DataFrame = session.createDataFrame(Seq(
      "Hi I heard about Spark",
      "I wish Java could use case class",
      "Logistic,regression models are neat"
    ).map(Tuple1.apply)).toDF("text")

//    val word2Vec: Word2Vec = new Word2Vec()
//      .setInputCol("text")
//      .setOutputCol("result")
//      .setVectorSize(3)
//      .setMinCount(0)
//    val model: Word2VecModel = word2Vec.fit(documentDF)
//    val result: DataFrame = model.transform(documentDF)
//    result.show()

    //通过正则来进行分词
    val regexTokenizer = new RegexTokenizer()
      .setInputCol("text")
      .setOutputCol("words")
      .setPattern("\\W")

    val countTokens = new UDF { (words:Seq[String]) => words.length}


    val regexTokenized: DataFrame = regexTokenizer.transform(documentDF)
//    regexTokenized
//      .select("text","words")
//      .show()

    //加载停用词库
    val stopWords: Array[String] = StopWordsRemover.loadDefaultStopWords("english")

    val remover: StopWordsRemover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("filtered")
    remover.transform(regexTokenized).show(false)

  }
}
