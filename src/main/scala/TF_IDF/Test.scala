package TF_IDF

import java.net.URLEncoder

import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.matching.Regex


/**
  * @创建用户: 阿宇
  * @创建时间: 2019/4/17 15:30
  * @类描述:
  */
object Test {
  def main(args: Array[String]): Unit = {

//    val title = "Rosie Wyatt and More to Star in IN EVENT OF MOONE DISASTER at Theatre503"
//    val art =
//      """
//        |<p>The Indian television has today become a big business, with its popularity graph reaching as high as that of Bollywood.&nbsp;But, in every race, there is a winner and that makes life interesting.&nbsp;There are lots of actresses to choose from, but these great stars made it to the top. Check out these&nbsp;<strong>top&nbsp;5 most beautiful Indian TV serial actresses in 2018.&nbsp;</strong>Without further delay, let us look at our number&nbsp;5 in the list.</p><p><strong>5. Jennifer Winget</strong></p><p class="ql-align-center"><img src="http://hl-img.peco.uodoo.com/hubble/app/sm/3b5138e2b127510824fcb617723e5e19.jpg;,70,JPEGX;3,690x"></p><p class="ql-align-center"><em style="color: rgb(155, 155, 155);">Third party image reference</em></p><p>Indian actress Jennifer Winget was born on May 30, 1985. Winget started her career when she was 12 years old as a child star in the film Raja Jo Rani Se Pyar Ho Gaya and another film on the same role for the movie Kuch Naa Kaho at the age of 14. Likewise, Eastern Eye listed her at the 21st position on the World’s Sexiest Asian Women for 2012.</p><p><strong>4. Surbhi Jyoti</strong></p><p class="ql-align-center"><img src="http://hl-img.peco.uodoo.com/hubble/app/sm/983a9ab1c4ddea0914161c3402609bd5.jpg;,70,JPEGX;3,690x"></p><p class="ql-align-center"><em style="color: rgb(155, 155, 155);">Third party image reference</em></p><p>Surbhi Jyoti is another pretty face on the Indian TV, with her lead role in the Zee TV serial “Qubool Hai”. Her attractive face and captivating eyes have won her huge fan following.</p><p><strong>3. Hina Khan</strong></p><p class="ql-align-center"><img src="http://hl-img.peco.uodoo.com/hubble/app/sm/26e50e33601ef2b904cd7ea849f5c810.jpg;,70,JPEGX;3,690x"></p><p class="ql-align-center"><em style="color: rgb(155, 155, 155);">Third party image reference</em></p><p>Bigg Boss 11 finalist&nbsp;Hina Khan&nbsp;first rose to fame with the longest running soap on Indian Television Yeh Rishta Kya Kehlata Hai, where she played the character of Akshara. Post Yeh Rishta, the actress participated in adventure-based reality show, Khatron Ke Khiladi.</p><p><strong>2.&nbsp;Surbhi Chandana</strong></p><p class="ql-align-center"><img src="http://hl-img.peco.uodoo.com/hubble/app/sm/71e6c1780ec5cf2fd287ddc93b915133.webp;,70,JPEGX;3,690x"></p><p class="ql-align-center"><em style="color: rgb(155, 155, 155);">Third party image reference</em></p><p>Surbhi Chandana aka Annika has flattered us not only by her chemistry with Nakuul Mehta aka Shivay Singh Oberoi but has also given us some cool slangs like Tadi, Michmichi, Raita Fail Gaya, Khidki Tod Idea.</p><p><strong>1.&nbsp;Mouni Roy</strong></p><p class="ql-align-center"><img src="http://hl-img.peco.uodoo.com/hubble/app/sm/92e7b1b452830fe562bcaf1286532b54.jpg;,70,JPEGX;3,690x"></p><p class="ql-align-center"><em style="color: rgb(155, 155, 155);">Third party image reference</em></p><p><br></p>
//      """.stripMargin
//
    val str = "Likewise, Eastern Eye listed her at the 21st. position on the World’s Sexiest Asian Women for 2012"
    val str2 = "Surbhi Jyoti is another pretty face on the Indian TV, with her lead role in the Zee TV serial “Qubool Hai”."
//    val regex = "[\\u3002|\\uff1f|\\uff01|\\uff0c|\\u3001|\\uff1b|\\uff1a|\\u201c|\\u201d|\\u2018|\\u2019|\\uff08|\\uff09|\\u300a|\\u300b|\\u3008|\\u3009|\\u3010|\\u3011|\\u300e|\\u300f|\\u300c|\\u300d|\\ufe43|\\ufe44|\\u3014|\\u3015|\\u2026|\\u2014|\\uff5e|\\ufe4f|\\uffe5]"
//    //将所有中文标点符号替换成 空格
//    val query = str.replaceAll(regex," ")
    val query = "q=".concat(URLEncoder.encode(str,"UTF8"))
    println(query)

//    val session = SparkSession.builder().appName("").master("local[2]").getOrCreate()
//    val data: DataFrame = session.read.csv("dir/data.csv")
//
//    data.foreach(row => {
//      val uuid = row.getString(0)
//      val title = row.getString(1)
//      val ocontent = row.getString(2)
//      println(s"uuid:$uuid  \n标题:$title  \n文章主体:$ocontent \n")
//    })

  }


}
