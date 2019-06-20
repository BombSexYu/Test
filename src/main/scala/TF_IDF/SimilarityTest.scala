package TF_IDF

import com.github.fommil.netlib.BLAS
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.ml.feature._
import org.apache.spark.sql._

/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/16 16:31
  * @类描述:
  */
object SimilarityTest {
  def main(args: Array[String]): Unit = {
//    val sparkSession = SparkSession
//      .builder()
//      .appName("SimilarityTest")
//      .master("local[2]")
//      .getOrCreate()

    val title = "Rosie Wyatt and More to Star in IN EVENT OF MOONE DISASTER at Theatre503"
    var paragraph =
      """
        |<p><img src="http://img.masala-sg.goldenmob.com/img/ffff2cb0ac86757827a3a48afd2ea67e/i_0_2CB85AB8C90B99E30AEF79AEC334FEE0B-480.jpg"></p> <p>Casting has been announced for An Drew Thompson s debut play, winner of the 2016 Theatre503 Playwriting Award and the first show to be directed by Lisa Spirling since she took over as Artistic Director.</p> <p><br> The Stage Award winning Rosie Wyatt stars as Sylvia, previously seen in Blink and Spine at the Soho Theatre, The Cardinal at Southwark Playhouse and Mumburger at Old Red Lion. The cast also includes actor-playwright Thomas Pickles (winner of the inaugural Adrian Pagan Playwright Award) and Alicya Eyo best known for playing long running characters Ruby Haswell in Emmerdale and Denny Blood Bad Girls. They are joined by Will Norris and Dar Dash.</p> <p>The winner of Theatre503s international playwriting award, and the first show directed by Lisa Spirling since taking over as Artistic Director - themes of space, time, feminism and sex collide in this astonishing debut play by An Drew Thompson .</p> <p>1969 - Man takes his first steps on the Moon, while Sylvia meets an astronaut at a party, who changes her world. 2055 - her granddaughter is set to become the first person to walk on Mars. Witness the adventures of three extraordinary women spanning 80 years, against the backdrop of humanitys journey to the stars.</p> <p>Lisa Spirling will make her directing debut as artistic director of Theatre503 with In Event of Moone Disaster, winner of the theatres International Playwriting Award 2016. Thompsons play was chosen from a shortlist of five, selected out of 1600 scripts from across 52 countries. In Event of Moone Disaster was announced as the winner after extensive debate by an expert panel including Mark Lawson , Timberlake Wertenbaker , Roy Williams , Charlotte Keatley, David Greig , Mel Kenyon, Henry Hitchings and Lily Williams.</p> <p>The award-winning Theatre503 supports and stages more first time writers than any other theatre in the country. At the heart of this commitment is a belief that the most important element in a writers development is to see their work on a stage, in front of an audience, performed to the highest professional standard. In the last 18 months 169 new writers have had their work performed there, with three West End transfers and two national tours. Theatre503 is air conditioned, with new comfortable seats and a first rate gastro-pub downstairs.</p> <p>For more information visit theatre503.com .</p><p> <link rel="amphtml"> <br> <br><br> </p> Related Articles <br> From This Author BWW News Desk
      """.stripMargin
//    paragraph = re.sub(r"&.{,8}?;|<.*?>", ' ', paragraph)
//    paragraph = "<p>(^[<.*?>].*?)</p>".r.findFirstIn(paragraph).getOrElse("")
//    paragraph = paragraph.replaceAll("<.*?>"," ")
//    println(paragraph)
//    println(paragraph.length)

    var list: List[String] = List("aa", "bb", "cc")
    val builder: StringBuilder = list.addString(new StringBuilder("dd"))
    list = list :+ "dd"
    println(list.size)
    for(str <- list)
      println(str)

  }



  def tfidf(df:DataFrame):DataFrame ={
    //对文章进行分词
    val tokenizer: RegexTokenizer = new RegexTokenizer()
      .setInputCol("segment")
      .setOutputCol("words")
    //分词后的DataFrame
    val wordDF: DataFrame = tokenizer.transform(df)

    //计算分词后的 原始特征(词频统计)
    val hashingTF: HashingTF = new HashingTF()
      .setInputCol("words")
      .setOutputCol("rawFeatures")
    val featuredDF: DataFrame = hashingTF.transform(wordDF)

    val idf: IDF = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")
    val idfModel: IDFModel = idf.fit(featuredDF)
    val rescaledDF: DataFrame = idfModel.transform(featuredDF)
    return rescaledDF
  }



}
