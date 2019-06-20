package SensitiveWords;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SensitveWord{

    private static final Logger logger = LoggerFactory.getLogger(SensitveWord.class);
    //字符编码格式
    private static String encoding = "UTF-8";
    //敏感词库文件路径
    private static String fileName = "sexwords.txt";
    //停用词库文件路径
    private static String stopFile = "EnglishStop";

    //存储敏感词，与权重值
    private static Map<String,Integer> sensitiveWordMap = new HashMap<>();
    //存储停用词库
    private static List<String> stopWords = new ArrayList<>();
    //存储文章的词，单词出现次数作为权重值
    private static Map<String,Integer> artMap = new HashMap<>();

    static{
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            InputStream is2 = SensitiveService.class.getClassLoader().getResourceAsStream(stopFile);
            InputStreamReader read = new InputStreamReader(is, encoding);
            InputStreamReader read2 = new InputStreamReader(is2, encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            BufferedReader bufferedReader2 = new BufferedReader(read2);
            String lineTxt;
            String lineTxt2;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim().toLowerCase();
                //敏感词权重值默认全为1
                sensitiveWordMap.put(lineTxt, 1);
            }
            while ((lineTxt2 = bufferedReader2.readLine()) != null){
                stopWords.add(lineTxt2.trim());
            }
            System.out.println("敏感词库大小为: " + sensitiveWordMap.size());
            System.out.println("停用词库大小为: " + stopWords.size());
            read.close();
            read2.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    /**
     * 计算带有权重的相似度
     * @param dict1：Map<String,Integer>：Map<特征词,权重值>
     * @param dict2：Map<String,Integer>：Map<特征词,权重值>
     * @return
     */
    public double similarity(Map<String,Integer> dict1, Map<String,Integer> dict2){
        double similarity = 0.0, numerator = 0.0, denominator1 = 0.0, denominator2 = 0.0;
        if(dict1.size()==0||dict2.size()==0){
            similarity = 0.0;
            return similarity;
        }
        int value1=0;
        int value2=0;
        int num = 0;
        for(String keyword:dict1.keySet()){
            value1 = dict1.get(keyword);
            if(dict2.containsKey(keyword)){
                value2 = dict2.get(keyword);
//                dict2.remove(keyword);
                num++;
            }else{
                value2 = 0;
            }

            numerator += value1 * value2;
            denominator1 += value1 * value1;
            denominator2 += value2 * value2;
        }

        for(String keyword:dict2.keySet()){
            value2 = dict2.get(keyword);
            denominator2 += value2 * value2;
        }
        similarity = numerator / (Math.sqrt(denominator1 * denominator2));
        return similarity;
    }

    private Map<String,Integer> splitArt(){
        //清空历史记录
        artMap.clear();
        String str = "<p>amature anal impaler The Indian television has today become a big business, with its popularity graph reaching as high as that of Bollywood.&nbsp;But, in every race, there is a winner and that makes life interesting.&nbsp;There are lots of actresses to choose from, but these great stars made it to the top. Check out these&nbsp;<strong>top&nbsp;5 most beautiful Indian TV serial actresses in 2018.&nbsp;</strong>Without further delay, let us look at our number&nbsp;5 in the list.</p><p><strong>5. Jennifer Winget</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/3b5138e2b127510824fcb617723e5e19.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Indian actress Jennifer Winget was born on May 30, 1985. Winget started her career when she was 12 years old as a child star in the film Raja Jo Rani Se Pyar Ho Gaya and another film on the same role for the movie Kuch Naa Kaho at the age of 14. Likewise, Eastern Eye listed her at the 21st position on the World’s Sexiest Asian Women for 2012.</p><p><strong>4. Surbhi Jyoti</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/983a9ab1c4ddea0914161c3402609bd5.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Surbhi Jyoti is another pretty face on the Indian TV, with her lead role in the Zee TV serial “Qubool Hai”. Her attractive face and captivating eyes have won her huge fan following.</p><p><strong>3. Hina Khan</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/26e50e33601ef2b904cd7ea849f5c810.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Bigg Boss 11 finalist&nbsp;Hina Khan&nbsp;first rose to fame with the longest running soap on Indian Television Yeh Rishta Kya Kehlata Hai, where she played the character of Akshara. Post Yeh Rishta, the actress participated in adventure-based reality show, Khatron Ke Khiladi.</p><p><strong>2.&nbsp;Surbhi Chandana</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/71e6c1780ec5cf2fd287ddc93b915133.webp;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Surbhi Chandana aka Annika has flattered us not only by her chemistry with Nakuul Mehta aka Shivay Singh Oberoi but has also given us some cool slangs like Tadi, Michmichi, Raita Fail Gaya, Khidki Tod Idea.</p><p><strong>1.&nbsp;Mouni Roy</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/92e7b1b452830fe562bcaf1286532b54.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p><br></p>";
        String str2 = "";
        String str3 = "cnts";

        String par = str.replaceAll("<.*?>|\\&\\w+|;|\n|\r","").toLowerCase();

        //对文章进行分词
        StringTokenizer st = new StringTokenizer(par,",!' .;");
        List<String> splits = new ArrayList<>();
        while(st.hasMoreElements()){
            splits.add(st.nextElement().toString().trim().toLowerCase());
        }
        System.out.println("分词后数据单词个数为:" + splits.size() + "  数据:" + splits.toString());
        //去除停用词
        splits.removeAll(stopWords);
        System.out.println("去除停用词后数据单词个数为:" + splits.size() + "  数据:" + splits.toString());
        for(String s : splits){
            //若为新单词，则不会存在于map集合中
            artMap.put(s, artMap.getOrDefault(s, 0)+1);
        }
        return artMap;
    }

    public static void main(String[] args) {
        SensitveWord sensitve = new SensitveWord();
        Map<String, Integer> res = sensitve.splitArt();
        System.out.println("size:" + res.size() + "  " + sensitve.similarity(sensitiveWordMap, res));

        System.out.println(sensitiveWordMap.keySet().size() + "  " + sensitiveWordMap.size());
        res.keySet().retainAll(sensitiveWordMap.keySet());
        System.out.println(res.keySet().toString() + "  " + res.size());

    }
//    0.002303511615907002  敏感词权重为1

}

