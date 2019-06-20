import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithSGD}
import org.apache.spark.mllib.feature.{StandardScaler, StandardScalerModel}
import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/1 17:06
  * @类描述:
  */
object ML_Classification {
  def main(args:Array[String]){

    //代码初始化的一些步骤
    val conf=new SparkConf().setAppName("classification").setMaster("local[2]")
    val sc=new SparkContext(conf)

    val rawData=sc.textFile("dir/train.tsv")
    val records=rawData.map(_.split("\t"))//数据是以\t分割

    val data: RDD[LabeledPoint] = records.map { point =>
      //将数据中的引号全部替换为空
      val replaceData = point.map(_.replaceAll("\"", ""))
      //本数据的头四个字段不会用到，数据的最一个字段代表分类的结果，1为长久，0为短暂
      val label = replaceData(replaceData.size - 1).toInt
      //从第4个字段取到倒数第一个字段(4,size-1]
      val features = replaceData.slice(4, replaceData.size - 1).map(x => if (x == "?") 0.0 else x.toDouble)
      //label存分类结果，features存特征，将其转换为LabeledPoint类型，此类型主要用于监督学习。
      LabeledPoint(label, Vectors.dense(features))
    }

    //进行特征标准化
    val vectors: RDD[linalg.Vector] = data.map(_.features)
    val scaler: StandardScalerModel = new StandardScaler(true,true).fit(vectors)
    val scalerData = data.map(p => LabeledPoint(p.label,scaler.transform(p.features)))

    //使用逻辑回归算法，此算法使用的是随机梯度下降算法进行优化，
    //当然也可以使用其他的优化算法，对于算法原理推荐看看Andrew Ng的视频
    //迭代次数10次
    val lrModel: LogisticRegressionModel = LogisticRegressionWithSGD.train(scalerData, 10)


    //预测并查看有多少预测正确，这里的测试集与训练数据集相同，
    val predictrueData=data.map{point=>
      if(lrModel.predict(point.features)==point.label) 1 else 0
    }.sum()

    //求正确率
    val accuracy=predictrueData/data.count()
    println(accuracy)
  }

}
