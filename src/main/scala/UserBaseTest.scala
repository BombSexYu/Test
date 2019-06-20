import org.apache.spark.mllib.linalg.distributed._
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/1 14:43
  * @类描述:
  */
object UserBaseTest {
  def main(args:Array[String]): Unit = {

    val conf = new SparkConf().setAppName("UserBaseModel").setMaster("local").set("spark.sql.warehouse.dir","spark-warehouse")
    val sc = new SparkContext(conf)

    //test.data是用户_物品_评分样本，且用户为Int，物品为int型
    val data = sc.textFile("dir/test.data")
    val parseData= data.map(_.split(",") match {case Array(user,item,rate)=>MatrixEntry(user.toLong-1,item.toLong-1,rate.toDouble)})
    /*parseData.collect().map(x=>{
    println(x.i+"->"+x.j+"->"+x.value)
    })*/
    //coordinateMatrix是专门保存user_item_rating这种数据样本的
    println("ratings:")
    val ratings = new CoordinateMatrix(parseData)
    ratings.entries.collect().map(x=>{
      println(x.i+","+x.j+","+x.value)
    })

    //把CoordinateMatrix转换成RowMatrix计算两个用户之间的cos相似性，且行表示用户，列表示物品
    //RowMatrix的方法，columnSimilarities是计算，列与列的相似度，现在是user_item_rating，需要转置(transpose)成item_user_rating,这样才是用户的相似
    //toRowMatrix()之后，物品的顺序不是从小到大排序的，但是相似度是Ok的
    val matrix= ratings.transpose().toRowMatrix()

    println("toRowMatrix之后的结果：")
    matrix.rows.collect().map(x=>{
      //每一行代表某个产品的评分
      x.toArray.map(x=>{print(x+",")})
      println("")
    })

    val similarities = matrix.columnSimilarities()
    //相似性是对的
    println("相似性")
    similarities.entries.collect().sortBy(-_.value).map(x=>{

      println(x.i+"->"+x.j+"=>"+x.value)
    })

    /*
         similarities.entries.filter(_.i==0).sortBy(_.value,false).collect().map(x=>{
           println(x.i+"->"+x.j+"->"+x.value)
         })*/

    //计算用户1对物品1的评分，预测结果为，用户1的评价分+其他相似用户对其的加权平均值，相似度为权重
    // val ratingOfUser1 = ratings.toRowMatrix().rows.collect()(3).toArray ,这个就是数字不能代表user的下标
    // toRowMatrix()好像有点问题
    val ratingOfUser1 = ratings.entries.filter(_.i==0).map(x=>{(x.j,x.value)}).sortBy(_._1).collect().map(_._2).toList.toArray


    val avgRatingOfUser1 = ratingOfUser1.sum/ratingOfUser1.size
    //println(avgRatingOfUser1)

    //计算其他用户对物品1的加权平均值,matrix是物品_用户_评分
    //matrix的一行，就是物品的所有用户评分,drop(1)表示删除自己的评分哈
    //matrix的(n)不能表示用户的下标啊

    val ratingsToItem1=matrix.rows.collect()(0).toArray.drop(1)
    //ratingsToItem1.map(x=>print(x))


    //权重_.i==0选择第一个用户,sortBy(_.j)表示根据用户的下标作为Key，value降序（value越大，表示相似度越高），所以，越前相似度越高
    val weights =similarities.entries.filter(_.i==0).sortBy(_.j).map(_.value).collect()
    //val weights =similarities.entries.filter(_.i==0).sortBy(_.value,false).map(_.value).collect()

    //(0 to 2)表示从0到2，默认步长1，这里表示，去top2相似的用户作为预测用户评分，真实情况，topn太少了哈
    //sum(权重*用户评分)/sum(weights)
    var weightedR  = (0 to 2).map(t=>weights(t) * ratingsToItem1(t)).sum/weights.sum

    //把平均值+top2相似用户的加权平均值
    println("rating of uses1 to item1 is "+(avgRatingOfUser1))
    println("rating of uses1 to item1 is "+(weightedR))
    println("rating of uses1 to item1 is "+(avgRatingOfUser1+weightedR))


  }

}
