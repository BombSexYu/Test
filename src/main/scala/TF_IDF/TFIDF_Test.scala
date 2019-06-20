package TF_IDF

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream, _}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.feature._
import org.apache.spark.sql.SQLContext
/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/16 15:51
  * @类描述:
  */
object TFIDF_Test {
  def main(args: Array[String]): Unit = {

    val masterUrl = "local[2]"
    val appName ="tfidf_test"
    val sparkConf = new SparkConf().setMaster(masterUrl).setAppName(appName)
    @transient val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._
    val df = sc.parallelize(Seq(
      (0, Array("a", "b", "c","a")),
      (1, Array("c", "b", "b", "c", "a")),
      (2, Array("a", "a", "c","d")),
      (3, Array("c", "a", "b", "a", "a")),
      (4, Array("我", "爱", "旅行", "土耳其", "大理","云南")),
      (5, Array("我", "爱", "学习")),
      (6, Array("胡歌", "优秀","演员", "幽默", "责任感"))
    )).map(x => (x._1, x._2)).toDF("id", "words")

    df.show(false)  //展示数据

    val hashModel = new HashingTF()
      .setInputCol("words")
      .setOutputCol("rawFeatures")
      .setNumFeatures(Math.pow(2, 20).toInt)

    val featurizedData = hashModel.transform(df)

    featurizedData.show(false) //展示数据

    val df3 = sc.parallelize(Seq(
      (0, Array("a", "a", "c","d")),
      (1, Array("c", "a", "b", "a", "a"))
    )).map(x => (x._1, x._2)).toDF("id", "words")

    hashModel.transform(df3).show(false)

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel: IDFModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)
    rescaledData.select("words", "features").show(false)

    try {
      val fileOut: FileOutputStream = new FileOutputStream("idf.jserialized")
      val out: ObjectOutputStream = new ObjectOutputStream(fileOut)
      out.writeObject(idfModel)
      out.close()
      fileOut.close()
      System.out.println("\nSerialization Successful... Checkout your specified output file..\n")
    }
    catch {
      case foe: FileNotFoundException => foe.printStackTrace()
      case ioe: IOException => ioe.printStackTrace()
    }

    val fos = new FileOutputStream("model.obj")
    val oos = new ObjectOutputStream(fos)
    oos.writeObject(idfModel)
    oos.close

    val fis = new FileInputStream("model.obj")
    val ois = new ObjectInputStream(fis)
    val newModel = ois.readObject().asInstanceOf[IDFModel]

    val df2 = sc.parallelize(Seq(
      (0, Array("a", "b", "c","a")),
      (1, Array("c", "b", "b", "c", "a")),
      (2, Array("我", "爱", "旅行", "土耳其", "大理","云南")),
      (3, Array("我", "爱", "工作")),
      (4, Array("胡歌", "优秀","演员", "幽默", "责任感"))
    )).map(x => (x._1, x._2)).toDF("id", "words")

    val hashModel2 = new HashingTF()
      .setInputCol("words")
      .setOutputCol("rawFeatures")
      .setNumFeatures(Math.pow(2, 20).toInt)

    val featurizedData2 = hashModel2.transform(df2)


    val rescaledData2 = newModel.transform(featurizedData2)
    rescaledData2.select("words", "features").show(false)


  }

}
