package TF_IDF


import java.net.URLEncoder

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod

import scala.collection.mutable
import scala.util.Random
import scala.util.matching.Regex

/**
  * @类描述: 在数据库查不到的情况下，去谷歌API查询
  */
object GoogleAPI {

  //返回相似度与URLs
  def googleAPI(title:String, paragraph:String): String ={
    val sentences: Array[String] = splitSentence(title,paragraph)
    var coverrate_url: List[String] = List()

    for(query <- sentences){
      val url = query2URL(query)
      coverrate_url +:= customSearch(url, query)
    }

    //统计段落相似度的平均值
    var avr_cover_rate :Float = 0
    //相似度最高的记录
    var max_cover_rate :Float = 0
    var max_url = ""
    //段落出现的URL
    var urls = ""
    for(str <- coverrate_url){
      if(!"null".equals(str)) {
        val splits: Array[String] = str.split("###")
        avr_cover_rate += splits(0).toFloat
        urls = urls + splits(1) + "\t"
        if(splits(0).toFloat > max_cover_rate) {
          max_cover_rate = splits(0).toFloat
          max_url = splits(1)
        }
      }
    }
    //计算其相似度的平均值
    avr_cover_rate /= coverrate_url.size
//    println(s"最大相似度: $max_cover_rate, url: $max_url \n关键词平均相似度: cover_rate = $avr_cover_rate, urls = $urls")
    return avr_cover_rate + "###" + urls + "###" + max_cover_rate + "###" + max_url

  }

  def query2URL(str: String): String ={
    val query = "q=".concat(URLEncoder.encode(str,"UTF8"))
    val url = List("https://www.googleapis.com/customsearch/v1?num=10&cx=000576016485080739186:qm_wxpwbn_8&key=AIzaSyBQ2E2TThZlPaeQKa-Ll4npTVyeQMzU_a8&" + query,
      "https://www.googleapis.com/customsearch/v1?num=10&cx=005813168417456358560:qkkyqk7x3xs&key=AIzaSyANQgl0qKkMgwdHajruy8g6PZQZUGymLjs&" + query)
    val rand: Int = Random.nextInt(2)
    return url(rand)
  }

  //清除文章中无效的数据，对文章进行分句，并按长度进行排序
  def splitSentence(title:String,paragraph:String): Array[String] ={
    //re.sub(r'&.{,8}?;|<.*?>', ' ', paragraph)
    val par = paragraph.replaceAll("<.*?>|\\&\\w+|;|\n|\r","")
    val splits: Array[String] = par.split("(\\.)|(\\?)|(!)")
    var sorts: Array[String] = splits.sortWith((s1, s2) => s1.length > s2.length)
    //将文章标题插入前面
    sorts = title +: sorts
    sorts
  }

  //求finded在searched中的包含率
  def sentence2Vec(finded:String, searched:String): Float ={
    val str1: String = finded.toLowerCase
    val str2: String = searched.toLowerCase

    val words_finded: List[String] = splitWork(str1)
    val words_searched: List[String] = splitWork(str2)

    if(words_finded.size == 0)
      return -1
    //求两个单词集合的交集，然后用交集的个数除以搜索内容的单词数
    return words_finded.intersect(words_searched).length.toFloat / words_searched.length

  }

  //对语句进行分词
  def splitWork(work:String): List[String] ={
    val r: Regex = "\\w+".r
    r.findAllIn(work).toList
  }

  //具体的谷歌搜索结果处理
  def customSearch(url:String, source_query:String):String={
    var max_cover:Float = 0
    var request_link = "null"

//    val client: HttpClient = new HttpClient()
//    val method: GetMethod = new GetMethod(url)
//    //URL请求返回的状态码，200代表请求成功
//    val code: Int = client.executeMethod(method)
////    println("----> " + code) //200 代表请求成功
//    if(code != 200){
//      println(s"code=$code, 网络请求失败！！！")
//    }
//    val res: String = method.getResponseBodyAsString
//    println("result " + res)

    val res =
      """
        |{
        | "kind": "customsearch#search",
        | "url": {
        |  "type": "application/json",
        |  "template": "https://www.googleapis.com/customsearch/v1?q={searchTerms}&num={count?}&start={startIndex?}&lr={language?}&safe={safe?}&cx={cx?}&sort={sort?}&filter={filter?}&gl={gl?}&cr={cr?}&googlehost={googleHost?}&c2coff={disableCnTwTranslation?}&hq={hq?}&hl={hl?}&siteSearch={siteSearch?}&siteSearchFilter={siteSearchFilter?}&exactTerms={exactTerms?}&excludeTerms={excludeTerms?}&linkSite={linkSite?}&orTerms={orTerms?}&relatedSite={relatedSite?}&dateRestrict={dateRestrict?}&lowRange={lowRange?}&highRange={highRange?}&searchType={searchType}&fileType={fileType?}&rights={rights?}&imgSize={imgSize?}&imgType={imgType?}&imgColorType={imgColorType?}&imgDominantColor={imgDominantColor?}&alt=json"
        | },
        | "queries": {
        |  "request": [
        |   {
        |    "title": "Google Custom Search - Rosie Wyatt and More to Star in IN EVENT OF MOONE DISASTER at Theatre503",
        |    "totalResults": "48",
        |    "searchTerms": "Rosie Wyatt and More to Star in IN EVENT OF MOONE DISASTER at Theatre503",
        |    "count": 10,
        |    "startIndex": 1,
        |    "inputEncoding": "utf8",
        |    "outputEncoding": "utf8",
        |    "safe": "off",
        |    "cx": "000576016485080739186:qm_wxpwbn_8"
        |   }
        |  ],
        |  "nextPage": [
        |   {
        |    "title": "Google Custom Search - Rosie Wyatt and More to Star in IN EVENT OF MOONE DISASTER at Theatre503",
        |    "totalResults": "48",
        |    "searchTerms": "Rosie Wyatt and More to Star in IN EVENT OF MOONE DISASTER at Theatre503",
        |    "count": 10,
        |    "startIndex": 11,
        |    "inputEncoding": "utf8",
        |    "outputEncoding": "utf8",
        |    "safe": "off",
        |    "cx": "000576016485080739186:qm_wxpwbn_8"
        |   }
        |  ]
        | },
        | "context": {
        |  "title": "mkit_search"
        | },
        | "searchInformation": {
        |  "searchTime": 0.531749,
        |  "formattedSearchTime": "0.53",
        |  "totalResults": "48",
        |  "formattedTotalResults": "48"
        | },
        | "items": [
        |  {
        |   "kind": "customsearch#result",
        |   "title": "Theatre503 - Catch Theatre503 alumni at Vault Festival!... | Facebook",
        |   "htmlTitle": "\u003cb\u003eTheatre503\u003c/b\u003e - Catch \u003cb\u003eTheatre503\u003c/b\u003e alumni at Vault Festival!... | Facebook",
        |   "link": "https://www.facebook.com/theatre503/posts/catch-theatre503-alumni-at-vault-festivalrosie-wyatt-sylvia-in-in-event-of-moone/10156197519082648/",
        |   "displayLink": "www.facebook.com",
        |   "snippet": "Catch Theatre503 alumni at Vault Festival! Rosie Wyatt (Sylvia in In Event of \nMoone Disaster) stars as the female lead of Michelle Barnette's new play...",
        |   "htmlSnippet": "Catch \u003cb\u003eTheatre503\u003c/b\u003e alumni at Vault Festival! \u003cb\u003eRosie Wyatt\u003c/b\u003e (Sylvia in In \u003cb\u003eEvent of\u003c/b\u003e \u003cbr\u003e\n\u003cb\u003eMoone Disaster\u003c/b\u003e) \u003cb\u003estars\u003c/b\u003e as the female lead of Michelle Barnette&#39;s new play...",
        |   "cacheId": "wzXcdpYAE7AJ",
        |   "formattedUrl": "https://www.facebook.com/theatre503/...theatre503...festivalrosie-wyatt... event-of-moone/10156197519082648/",
        |   "htmlFormattedUrl": "https://www.facebook.com/\u003cb\u003etheatre503\u003c/b\u003e/...\u003cb\u003etheatre503\u003c/b\u003e...festival\u003cb\u003erosie\u003c/b\u003e-\u003cb\u003ewyatt\u003c/b\u003e... \u003cb\u003eevent-of-moone\u003c/b\u003e/10156197519082648/",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "245",
        |      "height": "206",
        |      "src": "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcR7nel3TwVtC2ZIR_rxMoNUrpPhlTqDVi3NH3FZ82-0CL1zRQ0lxPLIbdQ"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "referrer": "default",
        |      "og:title": "Theatre503",
        |      "og:description": "Catch Theatre503 alumni at Vault Festival!\n\nRosie Wyatt (Sylvia in In Event of Moone Disaster) stars as the female lead of Michelle Barnette’s new play The Last Nine Months. Michelle was producer on...",
        |      "og:image": "https://lookaside.fbsbx.com/lookaside/crawler/media/?media_id=10156197516072648",
        |      "og:url": "https://www.facebook.com/theatre503/posts/10156197519082648"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://lookaside.fbsbx.com/lookaside/crawler/media/?media_id=10156197516072648"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "In Event of Moone Disaster review – intimate epic hurtles from 1969 ...",
        |   "htmlTitle": "In \u003cb\u003eEvent of Moone Disaster\u003c/b\u003e review – intimate epic hurtles from 1969 ...",
        |   "link": "https://www.theguardian.com/stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london",
        |   "displayLink": "www.theguardian.com",
        |   "snippet": "Oct 13, 2017 ... Theatre503, London ... Rosie Wyatt In Event of Moone Disaster. ... beat more than \n1,600 submissions to the 2016 Theatre503 playwriting award, .... It's not just the \nparents who look at the stars and the children who strive to get ...",
        |   "htmlSnippet": "Oct 13, 2017 \u003cb\u003e...\u003c/b\u003e \u003cb\u003eTheatre503\u003c/b\u003e, London ... \u003cb\u003eRosie Wyatt\u003c/b\u003e In \u003cb\u003eEvent of Moone Disaster\u003c/b\u003e. ... beat \u003cb\u003emore\u003c/b\u003e than \u003cbr\u003e\n1,600 submissions to the 2016 \u003cb\u003eTheatre503\u003c/b\u003e playwriting award, .... It&#39;s not just the \u003cbr\u003e\nparents who look at the \u003cb\u003estars\u003c/b\u003e and the children who strive to get&nbsp;...",
        |   "formattedUrl": "https://www.theguardian.com/.../in-event-of-moone-disaster-review-theatre- 503-london",
        |   "htmlFormattedUrl": "https://www.theguardian.com/.../in-\u003cb\u003eevent-of-moone\u003c/b\u003e-\u003cb\u003edisaster\u003c/b\u003e-review-\u003cb\u003etheatre- 503\u003c/b\u003e-london",
        |   "pagemap": {
        |    "thumbnail": [
        |     {
        |      "src": "https://i.guim.co.uk/img/media/cd13c22a3a69ec8004c23b346b37bb0fa79c43ca/0_0_4000_2400/master/4000.jpg?width=620&quality=85&auto=format&fit=max&s=67d7dc2a2293c78cd4f1ceba6289860d"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "format-detection": "telephone=no",
        |      "handheldfriendly": "True",
        |      "viewport": "width=device-width,minimum-scale=1,initial-scale=1",
        |      "apple-mobile-web-app-title": "Guardian",
        |      "application-name": "The Guardian",
        |      "msapplication-tilecolor": "#052962",
        |      "theme-color": "#052962",
        |      "msapplication-tileimage": "https://assets.guim.co.uk/images/favicons/023dafadbf5ef53e0865e4baaaa32b3b/windows_tile_144_b.png",
        |      "apple-itunes-app": "app-id=409128287, app-argument=https://www.theguardian.com/stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london, affiliate-data=ct=newsmartappbanner&pt=304191",
        |      "author": "Corrie Tan",
        |      "thumbnail": "https://i.guim.co.uk/img/media/cd13c22a3a69ec8004c23b346b37bb0fa79c43ca/0_0_4000_2400/master/4000.jpg?width=620&quality=85&auto=format&fit=max&s=67d7dc2a2293c78cd4f1ceba6289860d",
        |      "news_keywords": "Theatre,Stage,Culture,Space,Science,The moon",
        |      "og:url": "http://www.theguardian.com/stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london",
        |      "article:author": "https://www.theguardian.com/profile/corrie-tan",
        |      "og:image:height": "720",
        |      "og:description": "Inspired by a speech to be read if Armstrong and Aldrin hadn’t returned to Earth, Andrew Thompson’s ambitious debut is a sprawling family drama",
        |      "og:image:width": "1200",
        |      "og:image": "https://i.guim.co.uk/img/media/cd13c22a3a69ec8004c23b346b37bb0fa79c43ca/0_0_4000_2400/master/4000.jpg?width=1200&height=630&quality=85&auto=format&fit=crop&overlay-align=bottom%2Cleft&overlay-width=100p&overlay-base64=L2ltZy9zdGF0aWMvb3ZlcmxheXMvdGctcmV2aWV3LTQucG5n&s=76c922f9a7be02bada994d06092c3bb7",
        |      "al:ios:url": "gnmguardian://stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london?contenttype=Article&source=applinks",
        |      "article:publisher": "https://www.facebook.com/theguardian",
        |      "og:type": "article",
        |      "al:ios:app_store_id": "409128287",
        |      "article:section": "Stage",
        |      "article:published_time": "2017-10-13T09:30:29.000Z",
        |      "og:title": "In Event of Moone Disaster review – intimate epic hurtles from 1969 to 2055",
        |      "fb:app_id": "180444840287",
        |      "article:tag": "Theatre,Stage,Culture,Space,Science,The moon",
        |      "al:ios:app_name": "The Guardian",
        |      "og:site_name": "the Guardian",
        |      "article:modified_time": "2018-02-14T21:33:29.000Z",
        |      "twitter:app:id:iphone": "409128287",
        |      "twitter:app:name:googleplay": "The Guardian",
        |      "twitter:app:name:ipad": "The Guardian",
        |      "twitter:image": "https://i.guim.co.uk/img/media/cd13c22a3a69ec8004c23b346b37bb0fa79c43ca/0_0_4000_2400/master/4000.jpg?width=1200&height=630&quality=85&auto=format&fit=crop&overlay-align=bottom%2Cleft&overlay-width=100p&overlay-base64=L2ltZy9zdGF0aWMvb3ZlcmxheXMvdGctcmV2aWV3LTQucG5n&s=76c922f9a7be02bada994d06092c3bb7",
        |      "twitter:site": "@guardian",
        |      "twitter:app:url:ipad": "gnmguardian://stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london?contenttype=Article&source=twitter",
        |      "twitter:card": "summary_large_image",
        |      "twitter:app:name:iphone": "The Guardian",
        |      "twitter:app:id:ipad": "409128287",
        |      "twitter:app:id:googleplay": "com.guardian",
        |      "twitter:app:url:googleplay": "guardian://www.theguardian.com/stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london",
        |      "twitter:app:url:iphone": "gnmguardian://stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london?contenttype=Article&source=twitter",
        |      "twitter:dnt": "on",
        |      "fb:pages": "10513336322"
        |     }
        |    ],
        |    "imageobject": [
        |     {
        |      "url": "https://uploads.guim.co.uk/2018/01/31/TheGuardian_AMP.png",
        |      "width": "190",
        |      "height": "60"
        |     },
        |     {
        |      "representativeofpage": "true",
        |      "url": "https://i.guim.co.uk/img/media/a2f176793d30a6a11d31b9ec647e7feda7187329/0_0_3275_2339/master/3275.jpg?width=700&quality=85&auto=format&fit=max&s=7d4f0d0cbc62d0becc8eecd54095d5c0",
        |      "width": "3275",
        |      "height": "2339",
        |      "contenturl": "https://i.guim.co.uk/img/media/a2f176793d30a6a11d31b9ec647e7feda7187329/0_0_3275_2339/master/3275.jpg?width=300&quality=85&auto=format&fit=max&s=33a7aad1a870fcd0494f67a93cb2ecff",
        |      "description": "Grand narratives … In Event of Moone Disaster. Photograph: Jack Sain"
        |     },
        |     {
        |      "url": "https://i.guim.co.uk/img/media/4e87603c233ca17c266b94300d068b58362b2425/0_48_2001_1201/master/2001.jpg?width=700&quality=85&auto=format&fit=max&s=365b43e6d67d3afe7353a444f03dea7b",
        |      "width": "2001",
        |      "height": "1201",
        |      "contenturl": "https://i.guim.co.uk/img/media/4e87603c233ca17c266b94300d068b58362b2425/0_48_2001_1201/master/2001.jpg?width=300&quality=85&auto=format&fit=max&s=7d4664921108e91fdf9a95a321057ab5",
        |      "description": "Free-spirited … Rosie Wyatt in Andrew Thompson’s In Event of Moone Disaster. Photograph: Jack Sain"
        |     }
        |    ],
        |    "review": [
        |     {
        |      "mainentityofpage": "https://www.theguardian.com/stage/2017/oct/13/in-event-of-moone-disaster-review-theatre-503-london",
        |      "headline": "In Event of Moone Disaster review – intimate epic hurtles from 1969 to 2055",
        |      "description": "Inspired by a speech to be read if Armstrong and Aldrin hadn’t returned to Earth, Andrew Thompson’s ambitious debut is a sprawling family drama.",
        |      "datepublished": "2017-10-13T05:30:29-0400",
        |      "datemodified": "2018-02-14T16:33:29-0500",
        |      "reviewbody": "Andrew Thompson’s ambitious debut is a multigenerational, interplanetary family drama of humanity’s desire to conquer the universe but still have a place to call home. It considers the...",
        |      "contenturl": "https://i.guim.co.uk/img/media/d119e44ed2dbecf09605c9ab2c4d1bf13a64ca50/128_0_3840_2304/master/3840.jpg?width=460&quality=85&auto=format&fit=max&s=9f3c37eaff1ac9a85615ab097728b248"
        |     }
        |    ],
        |    "person": [
        |     {
        |      "sameas": "Corrie Tan",
        |      "name": "Corrie Tan"
        |     },
        |     {
        |      "url": "Megalomax",
        |      "givenname": "Megalomax"
        |     },
        |     {
        |      "url": "Gary Morgan",
        |      "givenname": "Gary Morgan"
        |     },
        |     {
        |      "url": "arghbee",
        |      "givenname": "arghbee"
        |     }
        |    ],
        |    "organization": [
        |     {
        |      "name": "The Guardian",
        |      "sameas": "https://www.theguardian.com/"
        |     }
        |    ],
        |    "comment": [
        |     {
        |      "datecreated": "2017-10-13T11:32:11Z",
        |      "text": "Sounds great :-)"
        |     },
        |     {
        |      "datecreated": "2017-10-14T23:24:46Z",
        |      "text": "If it's like Alice's Birch's last it'll be worth a punt."
        |     },
        |     {
        |      "datecreated": "2017-10-20T09:40:52Z",
        |      "text": "I was enthusiastic until I reached 'strident and abrasive'. I get enough of that at home."
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://i.guim.co.uk/img/media/cd13c22a3a69ec8004c23b346b37bb0fa79c43ca/0_0_4000_2400/master/4000.jpg?width=1200&height=630&quality=85&auto=format&fit=crop&overlay-align=bottom%2Cleft&overlay-width=100p&overlay-base64=L2ltZy9zdGF0aWMvb3ZlcmxheXMvdGctcmV2aWV3LTQucG5n&s=76c922f9a7be02bada994d06092c3bb7"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "In Event of Moone Disaster review at Theatre503, London ...",
        |   "htmlTitle": "In \u003cb\u003eEvent of Moone Disaster\u003c/b\u003e review at \u003cb\u003eTheatre503\u003c/b\u003e, London ...",
        |   "link": "https://www.thestage.co.uk/reviews/2017/in-event-of-moone-disaster-review-at-theatre503-london/",
        |   "displayLink": "www.thestage.co.uk",
        |   "snippet": "Oct 10, 2017 ... Rosie Wyatt In Event of Moone Disaster at Theatre503, London. ... to celebrate \nthe moon landing, we meet main protagonist Sylvia Moone.",
        |   "htmlSnippet": "Oct 10, 2017 \u003cb\u003e...\u003c/b\u003e \u003cb\u003eRosie Wyatt\u003c/b\u003e In \u003cb\u003eEvent of Moone Disaster at Theatre503\u003c/b\u003e, London. ... to celebrate \u003cbr\u003e\nthe moon landing, we meet main protagonist Sylvia \u003cb\u003eMoone\u003c/b\u003e.",
        |   "cacheId": "VMyMdBb9TO8J",
        |   "formattedUrl": "https://www.thestage.co.uk/.../in-event-of-moone-disaster-review-at- theatre503-london/",
        |   "htmlFormattedUrl": "https://www.thestage.co.uk/.../in-\u003cb\u003eevent-of-moone\u003c/b\u003e-\u003cb\u003edisaster\u003c/b\u003e-review-at- \u003cb\u003etheatre503\u003c/b\u003e-london/",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "279",
        |      "height": "181",
        |      "src": "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcT9xzVHRkxlVOv4fhVxx3k1V364S1ZGiJtWxO9PVpeb18-CxiBqCpUCfvUc"
        |     }
        |    ],
        |    "rating": [
        |     {
        |      "worstrating": "1",
        |      "bestrating": "5",
        |      "ratingvalue": "4"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "viewport": "width=device-width, initial-scale=1.0",
        |      "fb:pages": "7383655823",
        |      "referrer": "always",
        |      "og:image": "https://cdn.thestage.co.uk/wp-content/uploads/2017/10/10115019/In-Event-of-Moone-Disaster_credit_Jack-Sain.jpg",
        |      "news_keywords": "reviews, theatre503",
        |      "original-source": "https://www.thestage.co.uk/reviews/2017/in-event-of-moone-disaster-review-at-theatre503-london/",
        |      "og:locale": "en_US",
        |      "og:type": "article",
        |      "og:title": "In Event of Moone Disaster review at Theatre503, London – 'wonderfully acted'",
        |      "og:description": "Wonderfully acted play that cleverly combines themes of space travel and sexual freedom",
        |      "og:url": "https://www.thestage.co.uk/reviews/2017/in-event-of-moone-disaster-review-at-theatre503-london/",
        |      "og:site_name": "The Stage",
        |      "article:publisher": "https://www.facebook.com/thestage",
        |      "article:tag": "Reviews",
        |      "article:section": "Theatre",
        |      "og:image:secure_url": "https://cdn.thestage.co.uk/wp-content/uploads/2017/10/10115019/In-Event-of-Moone-Disaster_credit_Jack-Sain.jpg",
        |      "og:image:width": "700",
        |      "og:image:height": "455",
        |      "twitter:card": "summary",
        |      "twitter:description": "Wonderfully acted play that cleverly combines themes of space travel and sexual freedom",
        |      "twitter:title": "In Event of Moone Disaster review at Theatre503, London – 'wonderfully acted'",
        |      "twitter:site": "@TheStage",
        |      "twitter:image": "https://cdn.thestage.co.uk/wp-content/uploads/2017/10/10115019/In-Event-of-Moone-Disaster_credit_Jack-Sain.jpg",
        |      "twitter:creator": "@TheStage",
        |      "msapplication-tileimage": "https://cdn.thestage.co.uk/wp-content/uploads/2017/11/22103103/cropped-siteicon-270x270.png"
        |     }
        |    ],
        |    "sitenavigationelement": [
        |     {
        |      "image": "https://cdn.thestage.co.uk/wp-content/uploads/2019/04/12173521/shutterstock_488107402-198x143.jpg",
        |      "name": "Theatre employers blame gender pay gap on imbalance in technical departments",
        |      "url": "Theatre employers blame gender pay gap on imbalance in technical departments"
        |     }
        |    ],
        |    "review": [
        |     {
        |      "reviewer": "Anna Winter",
        |      "reviewdate": "2017-10-10T11:52:38+00:00",
        |      "ratingstars": "4.0"
        |     },
        |     {
        |      "author": "Anna Winter",
        |      "interactioncount": "UserComments:0",
        |      "datecreated": "2017-10-10T11:52:38+00:00",
        |      "datepublished": "2017-10-10T11:52:38+00:00",
        |      "about": "In Event of Moone Disaster, the 2016 winner of the Theatre503 Playwriting Award, heralds a triumphant launch to the career of its writer Andrew...",
        |      "itemreviewed": "In Event of Moone Disaster review at Theatre503, London – ‘wonderfully acted’"
        |     }
        |    ],
        |    "hreview": [
        |     {
        |      "dtreviewed": "2017-10-10T11:52:38+00:00"
        |     }
        |    ],
        |    "organization": [
        |     {
        |      "url": "https://www.thestage.co.uk/",
        |      "name": "The Stage"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://cdn.thestage.co.uk/wp-content/uploads/2017/10/10115019/In-Event-of-Moone-Disaster_credit_Jack-Sain.jpg"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "Rosie Wyatt Theatre Credits, News, Bio and Photos",
        |   "htmlTitle": "\u003cb\u003eRosie Wyatt\u003c/b\u003e Theatre Credits, News, Bio and Photos",
        |   "link": "https://www.broadwayworld.com/people/Rosie-Wyatt/",
        |   "displayLink": "www.broadwayworld.com",
        |   "snippet": "Rosie Wyatt Bio, Photos, Theatre Credits, Stage History - ... Wyatt. BWW Review: \nIN EVENT OF MOONE DISASTER, Theatre503 (Oct 18, 2017) ... more news.",
        |   "htmlSnippet": "\u003cb\u003eRosie Wyatt\u003c/b\u003e Bio, Photos, Theatre Credits, Stage History - ... Wyatt. BWW Review: \u003cbr\u003e\nIN \u003cb\u003eEVENT OF MOONE DISASTER\u003c/b\u003e, \u003cb\u003eTheatre503\u003c/b\u003e (Oct 18, 2017) ... \u003cb\u003emore\u003c/b\u003e news.",
        |   "cacheId": "BUTJPoAYS-IJ",
        |   "formattedUrl": "https://www.broadwayworld.com/people/Rosie-Wyatt/",
        |   "htmlFormattedUrl": "https://www.broadwayworld.com/people/\u003cb\u003eRosie\u003c/b\u003e-\u003cb\u003eWyatt\u003c/b\u003e/",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "101",
        |      "height": "127",
        |      "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-CCdKJ6LWATE2YD44rc7pW6kLgkRd1M0x0WajjmwDa4Y29GE_Wpki_g"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "sppc-site-verification": "7b427ebce326f82c28d2344fb76fec2b",
        |      "og:title": "Rosie Wyatt Theatre Credits, News, Bio and Photos",
        |      "og:description": "Rosie Wyatt Bio, Photos, Theatre Credits, Stage History -",
        |      "fb:app_id": "139648586265",
        |      "og:image": "https://images.bwwstatic.com/headshots/429273sm.jpg",
        |      "og:image:url": "https://images.bwwstatic.com/headshots/429273sm.jpg",
        |      "fb:pages": "126634689416",
        |      "apple-itunes-app": "app-id=530770227",
        |      "viewport": "width=device-width, initial-scale=1, maximum-scale=1",
        |      "application-name": " ",
        |      "msapplication-tilecolor": "#FFFFFF",
        |      "msapplication-tileimage": "https://newimages.bwwstatic.com/2017/mstile-144x144.png",
        |      "msapplication-square70x70logo": "https://newimages.bwwstatic.com/2017/mstile-70x70.png",
        |      "msapplication-square150x150logo": "https://newimages.bwwstatic.com/2017/mstile-150x150.png",
        |      "msapplication-wide310x150logo": "https://newimages.bwwstatic.com/2017/mstile-310x150.png",
        |      "msapplication-square310x310logo": "https://newimages.bwwstatic.com/2017/mstile-310x310.png"
        |     }
        |    ],
        |    "hcard": [
        |     {
        |      "fn": "Rosie Wyatt",
        |      "photo": "https://images.bwwstatic.com/headshots/429273sm.jpg"
        |     },
        |     {
        |      "fn": "Rosie Wyatt",
        |      "photo": "https://images.bwwstatic.com/headshots/429273sm.jpg"
        |     }
        |    ],
        |    "person": [
        |     {
        |      "name": "Rosie Wyatt",
        |      "image": "https://images.bwwstatic.com/headshots/429273sm.jpg"
        |     },
        |     {
        |      "name": "Rosie Wyatt",
        |      "image": "https://images.bwwstatic.com/headshots/429273sm.jpg"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://images.bwwstatic.com/headshots/429273sm.jpg"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "News: Casting Announced for In Event Of Moone Disaster – Culture ...",
        |   "htmlTitle": "News: Casting Announced for In \u003cb\u003eEvent Of Moone Disaster\u003c/b\u003e – Culture ...",
        |   "link": "https://danielperks13.wordpress.com/2017/09/13/news-casting-announced-for-in-event-of-moone-disaster/",
        |   "displayLink": "danielperks13.wordpress.com",
        |   "snippet": "Sep 13, 2017 ... The Stage Award-winning Rosie Wyatt stars as Sylvia, with the cast also ... debut \nplay, In Event Of Moone Disaster, winner of the 2016 Theatre503 Playwriting ... \nFor more information or to buy tickets, please visit the website.",
        |   "htmlSnippet": "Sep 13, 2017 \u003cb\u003e...\u003c/b\u003e The Stage Award-winning \u003cb\u003eRosie Wyatt stars\u003c/b\u003e as Sylvia, with the cast also ... debut \u003cbr\u003e\nplay, In \u003cb\u003eEvent Of Moone Disaster\u003c/b\u003e, winner of the 2016 \u003cb\u003eTheatre503\u003c/b\u003e Playwriting ... \u003cbr\u003e\nFor \u003cb\u003emore\u003c/b\u003e information or to buy tickets, please visit the website.",
        |   "cacheId": "dOpBUx9dZPQJ",
        |   "formattedUrl": "https://danielperks13.wordpress.com/.../news-casting-announced-for-in-event -of-moone-disaster/",
        |   "htmlFormattedUrl": "https://danielperks13.wordpress.com/.../news-casting-announced-for-in-\u003cb\u003eevent -of-moone\u003c/b\u003e-\u003cb\u003edisaster\u003c/b\u003e/",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "275",
        |      "height": "183",
        |      "src": "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRndMJ_rJZR4A88FgSUTjYLFW7ax43aXudS7GReSIocwAmhZfWXGJ7ldDWP"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "viewport": "width=device-width, initial-scale=1",
        |      "og:type": "article",
        |      "og:title": "News: Casting Announced for In Event Of Moone Disaster",
        |      "og:url": "https://danielperks13.wordpress.com/2017/09/13/news-casting-announced-for-in-event-of-moone-disaster/",
        |      "og:description": "Casting has been announced for Andrew Thompson’s debut play, In Event Of Moone Disaster, winner of the 2016 Theatre503 Playwriting Award and the first show to be directed by Lisa Spirling since she…",
        |      "article:published_time": "2017-09-13T15:03:37+00:00",
        |      "article:modified_time": "2017-09-13T15:03:37+00:00",
        |      "og:site_name": "Culture by night",
        |      "og:image": "https://danielperks13.files.wordpress.com/2017/09/in-event-of-moone-disaster.jpg",
        |      "og:image:width": "798",
        |      "og:image:height": "532",
        |      "og:locale": "en_US",
        |      "twitter:creator": "@dperks13",
        |      "twitter:site": "@dperks13",
        |      "twitter:text:title": "News: Casting Announced for In Event Of Moone Disaster",
        |      "twitter:image": "https://danielperks13.files.wordpress.com/2017/09/in-event-of-moone-disaster.jpg?w=640",
        |      "twitter:card": "summary_large_image",
        |      "fb:app_id": "249643311490",
        |      "article:publisher": "https://www.facebook.com/WordPresscom",
        |      "theme-color": "#cfe0e6",
        |      "application-name": "Culture by night",
        |      "msapplication-window": "width=device-width;height=device-height",
        |      "msapplication-tooltip": "Daniel Perks - Freelance Journalist",
        |      "msapplication-task": "name=Subscribe;action-uri=https://danielperks13.wordpress.com/feed/;icon-uri=https://s1.wp.com/i/favicon.ico"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://danielperks13.files.wordpress.com/2017/09/in-event-of-moone-disaster.jpg"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "Drew Thompson Theatre Credits",
        |   "htmlTitle": "Drew Thompson Theatre Credits",
        |   "link": "https://www.broadwayworld.com/people/Drew-Thompson/",
        |   "displayLink": "www.broadwayworld.com",
        |   "snippet": "Photo Flash: In Rehearsals for IN EVENT OF MOONE DISASTER at Theatre503 (\nSep 29, 2017). Rosie Wyatt and More to Star in IN EVENT OF MOONE ...",
        |   "htmlSnippet": "Photo Flash: In Rehearsals for IN \u003cb\u003eEVENT OF MOONE DISASTER at Theatre503\u003c/b\u003e (\u003cbr\u003e\nSep 29, 2017). \u003cb\u003eRosie Wyatt and More to Star in IN EVENT OF MOONE\u003c/b\u003e&nbsp;...",
        |   "cacheId": "UOcwbwVw7DsJ",
        |   "formattedUrl": "https://www.broadwayworld.com/people/Drew-Thompson/",
        |   "htmlFormattedUrl": "https://www.broadwayworld.com/people/Drew-Thompson/",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "350",
        |      "height": "144",
        |      "src": "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcSsxvlW1JTUsP-QEUJzV3hkerPSUqSb-9U9dQltn7U6heDaPvWytZDEdKx9"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "sppc-site-verification": "7b427ebce326f82c28d2344fb76fec2b",
        |      "apple-itunes-app": "app-id=530770227",
        |      "og:title": "Drew Thompson Theatre Credits",
        |      "og:description": "Drew Thompson Theatre Credits, Stage History and theater resume -",
        |      "fb:app_id": "139648586265",
        |      "og:image": "https://images.bwwstatic.com/headshots/3737sm.jpg",
        |      "og:image:url": "https://images.bwwstatic.com/headshots/3737sm.jpg",
        |      "fb:pages": "126634689416",
        |      "viewport": "width=device-width, initial-scale=1, maximum-scale=1",
        |      "application-name": " ",
        |      "msapplication-tilecolor": "#FFFFFF",
        |      "msapplication-tileimage": "https://newimages.bwwstatic.com/2017/mstile-144x144.png",
        |      "msapplication-square70x70logo": "https://newimages.bwwstatic.com/2017/mstile-70x70.png",
        |      "msapplication-square150x150logo": "https://newimages.bwwstatic.com/2017/mstile-150x150.png",
        |      "msapplication-wide310x150logo": "https://newimages.bwwstatic.com/2017/mstile-310x150.png",
        |      "msapplication-square310x310logo": "https://newimages.bwwstatic.com/2017/mstile-310x310.png"
        |     }
        |    ],
        |    "hcard": [
        |     {
        |      "fn": "Drew Thompson",
        |      "photo": "https://images.bwwstatic.com/headshots/noimage.jpg"
        |     },
        |     {
        |      "fn": "Drew Thompson",
        |      "photo": "https://images.bwwstatic.com/headshots/noimage.jpg"
        |     }
        |    ],
        |    "person": [
        |     {
        |      "name": "Drew Thompson",
        |      "image": "https://images.bwwstatic.com/headshots/noimage.jpg"
        |     },
        |     {
        |      "name": "Drew Thompson",
        |      "image": "https://images.bwwstatic.com/headshots/noimage.jpg"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://images.bwwstatic.com/columnpic10/x340175415EB-C965-7899-7240E853481FBE7C.jpg.pagespeed.ic.dRhM8zKjh8.jpg"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "Rosie Wyatt sparkled in In Event of Moone Disaster at Theatre503",
        |   "htmlTitle": "\u003cb\u003eRosie Wyatt\u003c/b\u003e sparkled in In \u003cb\u003eEvent of Moone Disaster at Theatre503\u003c/b\u003e",
        |   "link": "http://hamlife.blogspot.com/2017/10/rosie-wyatt-sparkled-in-in-event-of.html",
        |   "displayLink": "hamlife.blogspot.com",
        |   "snippet": "Oct 13, 2017 ... Rosie Wyatt sparkled in In Event of Moone Disaster at Theatre503 ... And it \nstarred Rosie Wyatt who qualifies for a tag (Wyatt) in this blog as I have ... was \nmade more complicated, and more interesting, by Rosie Wyatt playing ...",
        |   "htmlSnippet": "Oct 13, 2017 \u003cb\u003e...\u003c/b\u003e \u003cb\u003eRosie Wyatt\u003c/b\u003e sparkled in In \u003cb\u003eEvent of Moone Disaster at Theatre503\u003c/b\u003e ... And it \u003cbr\u003e\n\u003cb\u003estarred Rosie Wyatt\u003c/b\u003e who qualifies for a tag (Wyatt) in this blog as I have ... was \u003cbr\u003e\nmade \u003cb\u003emore\u003c/b\u003e complicated, and \u003cb\u003emore\u003c/b\u003e interesting, by \u003cb\u003eRosie Wyatt\u003c/b\u003e playing&nbsp;...",
        |   "cacheId": "sP45WRMesf8J",
        |   "formattedUrl": "hamlife.blogspot.com/2017/.../rosie-wyatt-sparkled-in-in-event-of.html",
        |   "htmlFormattedUrl": "hamlife.blogspot.com/2017/.../\u003cb\u003erosie\u003c/b\u003e-\u003cb\u003ewyatt\u003c/b\u003e-sparkled-in-in-\u003cb\u003eevent\u003c/b\u003e-of.html",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "310",
        |      "height": "163",
        |      "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTsAv1FAItzOYq-k_3bjp5uOGFVe_aGQXL9yS1zhb0sMONBXVYAqsS4ZvQ"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "og:url": "http://hamlife.blogspot.com/2017/10/rosie-wyatt-sparkled-in-in-event-of.html",
        |      "og:title": "Rosie Wyatt sparkled in In Event of Moone Disaster at Theatre503",
        |      "og:description": "I had high hopes for In Event of Moone Disaster. I love the programme at Theatre503 and this play was selected by them in an internation...",
        |      "og:image": "https://4.bp.blogspot.com/-hMPW0cMNmwc/WePEkwaHfXI/AAAAAAAAfsg/E2XApjHtBvUZg6N8yxscVuP0epKLsgfSACLcBGAs/w1200-h630-p-k-no-nu/21768157_10155093039492648_7812357069806942668_n.jpg"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://4.bp.blogspot.com/-hMPW0cMNmwc/WePEkwaHfXI/AAAAAAAAfsg/E2XApjHtBvUZg6N8yxscVuP0epKLsgfSACLcBGAs/w1200-h630-p-k-no-nu/21768157_10155093039492648_7812357069806942668_n.jpg"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "Review: In Event of Moone Disaster at Theatre 503 - Exeunt Magazine",
        |   "htmlTitle": "Review: In \u003cb\u003eEvent of Moone Disaster at Theatre 503\u003c/b\u003e - Exeunt Magazine",
        |   "link": "http://exeuntmagazine.com/reviews/review-event-moone-disaster-theatre-503/",
        |   "displayLink": "exeuntmagazine.com",
        |   "snippet": "Oct 18, 2017 ... Francesca Peschier reviews In Event of Moone Disaster at Theatre 503. ... \nThere's more to life than life on earth: Francesca Peschier reviews ... It's an \nevening of possibility and potential catastrophe also for Sylvia Moone (Rose \nWyatt) and ... Though they might be very different star children (one likes drugs ...",
        |   "htmlSnippet": "Oct 18, 2017 \u003cb\u003e...\u003c/b\u003e Francesca Peschier reviews In \u003cb\u003eEvent of Moone Disaster at Theatre 503\u003c/b\u003e. ... \u003cbr\u003e\nThere&#39;s \u003cb\u003emore\u003c/b\u003e to life than life on earth: Francesca Peschier reviews ... It&#39;s an \u003cbr\u003e\nevening of possibility and potential catastrophe also for Sylvia \u003cb\u003eMoone\u003c/b\u003e (Rose \u003cbr\u003e\n\u003cb\u003eWyatt\u003c/b\u003e) and ... Though they might be very different \u003cb\u003estar\u003c/b\u003e children (one likes drugs&nbsp;...",
        |   "cacheId": "PUgKjrotPD0J",
        |   "formattedUrl": "exeuntmagazine.com/.../review-event-moone-disaster-theatre-503/",
        |   "htmlFormattedUrl": "exeuntmagazine.com/.../review-\u003cb\u003eevent\u003c/b\u003e-\u003cb\u003emoone\u003c/b\u003e-\u003cb\u003edisaster\u003c/b\u003e-\u003cb\u003etheatre-503\u003c/b\u003e/",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "275",
        |      "height": "183",
        |      "src": "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRnZK4J6azeVvzaeA8zTK5kYsyTGkPK0K3qRVkVyf-63qc5ahSjibYBe56n"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "viewport": "width=device-width, initial-scale=1",
        |      "og:url": "http://exeuntmagazine.com/reviews/review-event-moone-disaster-theatre-503/",
        |      "og:type": "Reviews",
        |      "og:title": "Review: In Event of Moone Disaster at Theatre 503 - Exeunt Magazine",
        |      "og:description": "Francesca Peschier reviews Review: In Event of Moone Disaster at Theatre 503 at",
        |      "og:image": "http://exeuntmagazine.com/wp-content/uploads/In-Event-of-Moone-Disaster_credit_Jack-Sain_6-600x400.jpg",
        |      "og:locale": "en_US",
        |      "og:site_name": "Exeunt Magazine",
        |      "article:section": "OWE & Fringe",
        |      "og:image:width": "4000",
        |      "og:image:height": "2667",
        |      "twitter:card": "summary",
        |      "twitter:description": "Francesca Peschier reviews In Event of Moone Disaster at Theatre 503.",
        |      "twitter:title": "Review: In Event of Moone Disaster at Theatre 503 - Exeunt Magazine",
        |      "twitter:image": "http://exeuntmagazine.com/wp-content/uploads/In-Event-of-Moone-Disaster_credit_Jack-Sain_6.jpg"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "http://exeuntmagazine.com/wp-content/uploads/In-Event-of-Moone-Disaster_credit_Jack-Sain_6-600x400.jpg"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "In Event of Moone Disaster, theatre review: Get lost in space with ...",
        |   "htmlTitle": "In \u003cb\u003eEvent of Moone Disaster\u003c/b\u003e, theatre review: Get lost in space with ...",
        |   "link": "https://www.standard.co.uk/go/london/theatre/in-event-of-moone-disaster-theatre-review-get-lost-in-space-with-highly-original-passionate-debut-a3683306.html",
        |   "displayLink": "www.standard.co.uk",
        |   "snippet": "Oct 10, 2017 ... Michelin stars ... Ground control: In Event of Moone Disaster is at Theatre503 ( \nJack Sain ) ... Read more ... Rosie Wyatt is excellent as the two Sylvias — the first \nseen in both callow youth and crabby old age, the second ...",
        |   "htmlSnippet": "Oct 10, 2017 \u003cb\u003e...\u003c/b\u003e Michelin \u003cb\u003estars\u003c/b\u003e ... Ground control: In \u003cb\u003eEvent of Moone Disaster\u003c/b\u003e is at \u003cb\u003eTheatre503\u003c/b\u003e ( \u003cbr\u003e\nJack Sain ) ... Read \u003cb\u003emore\u003c/b\u003e ... \u003cb\u003eRosie Wyatt\u003c/b\u003e is excellent as the two Sylvias — the first \u003cbr\u003e\nseen in both callow youth and crabby old age, the second&nbsp;...",
        |   "cacheId": "fisLCTM4ZskJ",
        |   "formattedUrl": "https://www.standard.co.uk/.../in-event-of-moone-disaster-theatre-review-get- lost-in-space-with-highly-original-passionate-debut-a3683306.html",
        |   "htmlFormattedUrl": "https://www.standard.co.uk/.../in-\u003cb\u003eevent-of-moone\u003c/b\u003e-\u003cb\u003edisaster\u003c/b\u003e-theatre-review-get- lost-in-space-with-highly-original-passionate-debut-a3683306.html",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "266",
        |      "height": "190",
        |      "src": "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQz_06vmoCgQ7u5tLbTI9L680MzHstT_z6jYQVwvegSoCk5l88N-OJhyT-b"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "viewport": "width=device-width,minimum-scale=1,initial-scale=1",
        |      "theme-color": "#dc062b",
        |      "og:url": "https://www.standard.co.uk/go/london/theatre/in-event-of-moone-disaster-theatre-review-get-lost-in-space-with-highly-original-passionate-debut-a3683306.html",
        |      "fb:pages": "165348596842143",
        |      "ia:markup_url": "https://www.standard.co.uk/pwamp/fbi/3683306",
        |      "og:title": "In Event of Moone Disaster review: Get lost in space with strong debut",
        |      "og:description": "On the night of the moon landing in July 1969, in a village somewhere in the north of England, childlike and awestruck Sylvia Moone meets an astronaut at a party. The atmosphere is woozily sexy, but hovering in the background is an anxiety that the evening’s rapturous mood will take a tragic turn.",
        |      "og:updated_time": "2017-11-30T11:10:03+00:00",
        |      "og:image": "https://static.standard.co.uk/s3fs-public/thumbnails/image/2017/10/10/13/in-event-of-moone-disaster-credit-jack-sain.jpg",
        |      "article:published_time": "2017-10-10T14:00:00Z",
        |      "article:modified_time": "2017-11-30T11:10:03Z",
        |      "twitter:title": "In Event of Moone Disaster review: Get lost in space with strong debut",
        |      "twitter:description": "On the night of the moon landing in July 1969, in a village somewhere in the north of England, childlike and awestruck Sylvia Moone meets an astronaut at a party. The atmosphere is woozily sexy, but",
        |      "og:site_name": "Evening Standard",
        |      "og:type": "article",
        |      "twitter:card": "summary_large_image",
        |      "og:locale": "en_GB",
        |      "article:publisher": "165348596842143",
        |      "fb:app_id": "193840110715213",
        |      "fb:admins": "36903991",
        |      "twitter:site": "@standardnews",
        |      "article:section": "Theatre",
        |      "article:subsection": "Arts",
        |      "article:author_name": "Henry Hitchings",
        |      "article:content_type": "story",
        |      "article:word_count": "486",
        |      "article:image_count": "2",
        |      "article:video_count": "0",
        |      "article:embed_count": "0",
        |      "article:internal_link_count": "4",
        |      "article:external_link_count": "1",
        |      "apple-mobile-web-app-capable": "yes",
        |      "apple-mobile-web-app-title": "Evening Standard"
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://static.standard.co.uk/s3fs-public/thumbnails/image/2017/10/10/13/in-event-of-moone-disaster-credit-jack-sain.jpg"
        |     }
        |    ]
        |   }
        |  },
        |  {
        |   "kind": "customsearch#result",
        |   "title": "In Event of Moone Disaster at Theatre503 - Theatre Bubble",
        |   "htmlTitle": "In \u003cb\u003eEvent of Moone Disaster at Theatre503\u003c/b\u003e - Theatre Bubble",
        |   "link": "http://www.theatrebubble.com/2017/10/event-moone-disaster-theatre503/",
        |   "displayLink": "www.theatrebubble.com",
        |   "snippet": "Oct 11, 2017 ... Among such a talented company, Rosie Wyatt (Mumburger, The Old ... track of \nthe timelines, and more importantly the generational content, ... In Event of \nMoone Disaster will continue its run at Theatre503 until ... Narnia star Georgie \nHenley to star in new Philip Ridley monologues at Southwark Playhouse ...",
        |   "htmlSnippet": "Oct 11, 2017 \u003cb\u003e...\u003c/b\u003e Among such a talented company, \u003cb\u003eRosie Wyatt\u003c/b\u003e (Mumburger, The Old ... track of \u003cbr\u003e\nthe timelines, and \u003cb\u003emore\u003c/b\u003e importantly the generational content, ... In \u003cb\u003eEvent of\u003c/b\u003e \u003cbr\u003e\n\u003cb\u003eMoone Disaster\u003c/b\u003e will continue its run at \u003cb\u003eTheatre503\u003c/b\u003e until ... Narnia \u003cb\u003estar\u003c/b\u003e Georgie \u003cbr\u003e\nHenley to \u003cb\u003estar\u003c/b\u003e in new Philip Ridley monologues at Southwark Playhouse&nbsp;...",
        |   "cacheId": "3v2qyGwEI_0J",
        |   "formattedUrl": "www.theatrebubble.com/2017/10/event-moone-disaster-theatre503/",
        |   "htmlFormattedUrl": "www.theatrebubble.com/2017/10/\u003cb\u003eevent\u003c/b\u003e-\u003cb\u003emoone\u003c/b\u003e-\u003cb\u003edisaster\u003c/b\u003e-\u003cb\u003etheatre503\u003c/b\u003e/",
        |   "pagemap": {
        |    "cse_thumbnail": [
        |     {
        |      "width": "299",
        |      "height": "168",
        |      "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTVdH2dtJpZgcXRiwEcFFnbjLf_8HhkiIkGG3esITUviKSaqTTB2POlAoc"
        |     }
        |    ],
        |    "product": [
        |     {
        |      "name": "In Event of Moone Disaster"
        |     }
        |    ],
        |    "rating": [
        |     {
        |      "worstrating": "1",
        |      "ratingvalue": "4",
        |      "bestrating": "5"
        |     }
        |    ],
        |    "metatags": [
        |     {
        |      "viewport": "width=device-width, initial-scale=1",
        |      "og:locale": "en_GB",
        |      "og:type": "article",
        |      "og:title": "In Event of Moone Disaster at Theatre503 - Theatre Bubble",
        |      "og:description": "Review of: In Event of Moone DisasterPrice:£15 (£12 conc)Reviewed by: Olivia CoxheadRating:4On October 11, 2017Last modified:October 12, 2017Summary:A triumph of [Read More]",
        |      "og:url": "http://www.theatrebubble.com/2017/10/event-moone-disaster-theatre503/",
        |      "og:site_name": "Theatre Bubble",
        |      "article:publisher": "https://www.facebook.com/theatrebubble",
        |      "article:tag": "Andrew Thompson",
        |      "article:section": "Review",
        |      "article:published_time": "2017-10-11T11:10:45+00:00",
        |      "article:modified_time": "2017-10-12T16:11:47+00:00",
        |      "og:updated_time": "2017-10-12T16:11:47+00:00",
        |      "og:image": "https://i1.wp.com/www.theatrebubble.com/wp-content/uploads/2017/10/NEWBANNER.jpg?fit=1416%2C798",
        |      "og:image:secure_url": "https://i1.wp.com/www.theatrebubble.com/wp-content/uploads/2017/10/NEWBANNER.jpg?fit=1416%2C798",
        |      "og:image:width": "1416",
        |      "og:image:height": "798",
        |      "twitter:card": "summary",
        |      "twitter:description": "Review of: In Event of Moone DisasterPrice:£15 (£12 conc)Reviewed by: Olivia CoxheadRating:4On October 11, 2017Last modified:October 12, 2017Summary:A triumph of [Read More]",
        |      "twitter:title": "In Event of Moone Disaster at Theatre503 - Theatre Bubble",
        |      "twitter:site": "@theatrebubble",
        |      "twitter:image": "https://i1.wp.com/www.theatrebubble.com/wp-content/uploads/2017/10/NEWBANNER.jpg?fit=1416%2C798",
        |      "twitter:creator": "@theatrebubble",
        |      "shareaholic:site_name": "Theatre Bubble",
        |      "shareaholic:language": "en-GB",
        |      "shareaholic:url": "http://www.theatrebubble.com/2017/10/event-moone-disaster-theatre503/",
        |      "shareaholic:keywords": "andrew thompson, drama, lisa sprirling, london, playwrighting award, playwriting award, theatre 503, moone, featured, review, post",
        |      "shareaholic:article_published_time": "2017-10-11T11:10:45+00:00",
        |      "shareaholic:article_modified_time": "2017-10-12T16:11:47+00:00",
        |      "shareaholic:shareable_page": "true",
        |      "shareaholic:article_author_name": "Olivia Coxhead",
        |      "shareaholic:site_id": "3a5e16bfe4f202358c1cb1edfb5fa9cb",
        |      "shareaholic:wp_version": "8.11.0",
        |      "shareaholic:image": "https://i1.wp.com/www.theatrebubble.com/wp-content/uploads/2017/10/NEWBANNER.jpg?fit=300%2C169"
        |     }
        |    ],
        |    "creativework": [
        |     {
        |      "headline": "In Event of Moone Disaster at Theatre503",
        |      "datepublished": "2017-10-11T11:10:45+00:00",
        |      "text": "Review of: In Event of Moone DisasterPrice:£15 (£12 conc)Reviewed by: Olivia CoxheadRating:4On October 11, 2017Last modified:October 12, 2017Summary:A triumph of emotional rangeMore DetailsWinner..."
        |     }
        |    ],
        |    "sitenavigationelement": [
        |     {
        |      "url": "Advertise",
        |      "name": "Advertise"
        |     },
        |     {
        |      "url": "Reviews",
        |      "name": "Reviews"
        |     }
        |    ],
        |    "hproduct": [
        |     {
        |      "fn": "In Event of Moone Disaster",
        |      "currency": "GBP",
        |      "currency_iso4217": "826"
        |     }
        |    ],
        |    "hcard": [
        |     {
        |      "fn": "Olivia Coxhead"
        |     }
        |    ],
        |    "review": [
        |     {
        |      "reviewer": "Olivia Coxhead",
        |      "ratingstars": "4.0"
        |     },
        |     {
        |      "name": "In Event of Moone Disaster",
        |      "author": "Olivia Coxhead",
        |      "datemodified": "October 12, 2017",
        |      "description": "A triumph of emotional range",
        |      "url": "More Details",
        |      "reviewbody": "Winner of the Theatre503 Playwriting Award 2016, Andrew Thompson’s In Event of Moone Disaster was chosen from over 1500 script submissions to be the first play directed by new Theatre503..."
        |     }
        |    ],
        |    "person": [
        |     {
        |      "url": "Olivia Coxhead",
        |      "name": "Olivia Coxhead"
        |     }
        |    ],
        |    "hreview": [
        |     {
        |      "summary": "In Event of Moone Disaster",
        |      "description": "Winner of the Theatre503 Playwriting Award 2016, Andrew Thompson’s In Event of Moone Disaster was chosen from over 1500 script submissions to be the first play directed by new Theatre503..."
        |     }
        |    ],
        |    "cse_image": [
        |     {
        |      "src": "https://i1.wp.com/www.theatrebubble.com/wp-content/uploads/2017/10/NEWBANNER.jpg?fit=1416%2C798"
        |     }
        |    ],
        |    "wpheader": [
        |     {
        |      "headline": "Theatre Bubble",
        |      "description": "The UK Theatre Network"
        |     }
        |    ],
        |    "thing": [
        |     {
        |      "name": "In Event of Moone Disaster"
        |     }
        |    ],
        |    "wpsidebar": [
        |     {
        |      "image": "https://i2.wp.com/www.theatrebubble.com/wp-content/uploads/2019/04/image.jpg?resize=100%2C100",
        |      "headline": "Fiddler on the Roof at the Playhouse Theatre"
        |     }
        |    ],
        |    "searchaction": [
        |     {
        |      "query-input": "name=s",
        |      "target": "http://www.theatrebubble.com/?s={s}"
        |     }
        |    ]
        |   }
        |  }
        | ]
        |}
      """.stripMargin
    val json: JSONObject = JSON.parseObject(res)

    val items: JSONArray = json.getJSONArray("items")
    val len = items.size()
    if(len == 0){
      println("没有搜索到相关文章")
    }else{
      for(i <- 0 until len){
        //遍历搜索出来的每一个结果
        val item: JSONObject = items.getJSONObject(i)
        //文章标题
        var htmlTitle = item.getString("htmlTitle")
        //文章片段， <b>~</b>
        var htmlSnippet = item.getString("htmlSnippet")
        //文章链接
        val link = item.getString("link")

        //取出具体的标题
        htmlTitle = findHighLight(htmlTitle)
        //文章的内容
        htmlSnippet = findHighLight(htmlSnippet)

        //htmlTilte分词后在查询的词中所占比
        val title_cover: Float = sentence2Vec(htmlTitle,source_query)
        val snippet_cover: Float = sentence2Vec(htmlSnippet,source_query)

        if(similarMax(title_cover,snippet_cover,max_cover) > max_cover){
          max_cover = similarMax(title_cover)
          //保存相似度最高的文章链接
          request_link = link
        }
      }
    }
    if(max_cover > 0 ) {
//      println("搜索条件: " + source_query + "   相似度: " + max_cover)
//      println(max_cover + "\t" + request_link)
      return max_cover + "###" + request_link
    }else{
      return "null"
    }
  }

  //求最大值
  def similarMax(values:Float*): Float ={
    var max = values(0)
    for(v <- values)
      if(v > max)
        max = v
    return max
  }

  //通过正则解析出具体的数据
  def findHighLight(str:String): String ={
    val r: Regex = "<b>(.*?)</b>".r
    val list: List[String] = r.findAllIn(str).toList
    var res: String = ""
    for(s <- list) {
      //      val tmp = s.substring(3,s.length-4)
      //去除多余的字符
      res = res + s.replaceAll("<b>|</b>|\\&\\w+;","") + " "
    }
    res
  }
}
