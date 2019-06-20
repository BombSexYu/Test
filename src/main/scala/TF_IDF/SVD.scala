package TF_IDF


import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.LDA
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.matching.Regex

object lda {

  //对语句进行分词
  def splitWork(work:String): List[String] ={
    val r: Regex = "\\w+".r
    r.findAllIn(work).toList
  }

  def main(args: Array[String]) {
    //0 构建Spark对象
    val conf = new SparkConf().setAppName("lda").setMaster("local")
    val sc = new SparkContext(conf)
    val session = SparkSession.builder().config(conf).getOrCreate()
    Logger.getRootLogger.setLevel(Level.WARN)

    //1 加载数据，返回的数据格式为：documents: RDD[(Long, Vector)]
    // 其中：Long为文章ID，Vector为文章分词后的词向量
    // 可以读取指定目录下的数据，通过分词以及数据格式的转换，转换成RDD[(Long, Vector)]即可
    val data = sc.textFile("dir/data.csv")
    val frame: DataFrame = session.read.csv("dir/data.csv")
    val trainRDD: RDD[(String,List[String])] = frame.rdd.map(row => {
      val uuid = row.getString(0)
      val art = row.getString(1).replaceAll("<.*?>|\\&\\w+|;|\n|\r", "")
      val words: List[String] = splitWork(art)
      (uuid, words)
    })

    val hashingTF: HashingTF = new HashingTF(Math.pow(2,18).toInt)
    val newSTF: RDD[String] = trainRDD.map {
      case (num, seq) => val tf: linalg.Vector = hashingTF.transform(seq)
        num + " " + tf
    }


    val parsedData = newSTF.map(s => Vectors.dense(s.trim.split(' ').map(_.toDouble)))
    // Index documents with unique IDs
    val corpus = parsedData.zipWithIndex.map(_.swap).cache()

    //2 建立模型，设置训练参数，训练模型
    /**
      * k: 主题数，或者聚类中心数
      * DocConcentration：文章分布的超参数(Dirichlet分布的参数)，必需>1.0
      * TopicConcentration：主题分布的超参数(Dirichlet分布的参数)，必需>1.0
      * MaxIterations：迭代次数
      * setSeed：随机种子
      * CheckpointInterval：迭代计算时检查点的间隔
      * Optimizer：优化计算方法，目前支持"em", "online"
      */
    val ldaModel = new LDA().
      setK(3).
      setDocConcentration(5).
      setTopicConcentration(5).
      setMaxIterations(20).
      setSeed(0L).
      setCheckpointInterval(10).
      setOptimizer("em").
      run(corpus)

    //3 模型输出，模型参数输出，结果输出
    // Output topics. Each is a distribution over words (matching word count vectors)
    println("Learned topics (as distributions over vocab of " + ldaModel.vocabSize + " words):")
    val topics = ldaModel.topicsMatrix
    for (topic <- Range(0, 3)) {
      print("Topic " + topic + ":")
      for (word <- Range(0, ldaModel.vocabSize)) { print(" " + topics(word, topic)); }
      println()
    }

  }

}