package SensitiveWords;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述: 敏感词统计
 */
public class SensitiveService {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    /**
     * 默认敏感词替换符
     */
    private static final String DEFAULT_REPLACEMENT = "敏感词";

    /**
     * 根节点
     */
    private static TreeNode rootNode;
    //统计敏感词出现次数
    private static long count = 0;
    //出现的敏感词列表
    private static List<String> wordList = new ArrayList();
    //字符编码格式
    private static String encoding = "UTF-8";
    private static String fileName = "sexwords.txt";

    static{
        rootNode = new TreeNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
//            InputStream is = SensitiveService.class.getClassLoader().getResourceAsStream(fileName);
            InputStreamReader read = new InputStreamReader(is, encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }


    /**
     * 判断是否是一个符号
     */
    private static boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


    /**
     * 敏感词统计
     */
    public Map<String,String> filter(String text) {
        //存放敏感词个数与出现的敏感词
        Map map = new HashMap<String,String>(2);
        int count = 0;
        List<String> list = new ArrayList<String>();
        if (StringUtils.isBlank(text)) {
            logger.error("文章数据为空");
            return null;
        }
//        String replacement = DEFAULT_REPLACEMENT;
//        StringBuilder result = new StringBuilder();

        TreeNode tempNode = rootNode;  //指向树的根节点
        int begin = 0; // 回滚数，指向字符串的指针，与树进行交互的
        int position = 0; // 当前比较的位置，指向字符串

        while (position < text.length()) {
            char c = text.charAt(position);
            // 空格直接跳过
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
//                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            // 当前位置的匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
//                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词， 从begin到position的位置用replacement替换掉
//                result.append(replacement);
                //将出现的敏感词添加到List中
                list.add(text.substring(begin, position+1));
                count++; //敏感词出现次数加一
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }

        //将最后一次的比较结果添加进去
//        result.append(text.substring(begin));
        map.put("count", count);
        map.put("words", list.toString());
        return map;
//        return result.toString();
    }

    private static void addWord(String lineTxt) {
        TreeNode tempNode = rootNode;
        // 循环每个字节
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            TreeNode node = tempNode.getSubNode(c);

            if (node == null) { // 没初始化
                node = new TreeNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // 关键词结束， 设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }

    //对文章进行清洗，过滤掉无用字符
    private static String artClean(String str){
        return str.replaceAll("<.*?>|\\&\\w+|;|\n|\r","");
    }

    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        String str = "<p>The Indian television has today become a big business, with its popularity graph reaching as high as that of Bollywood.&nbsp;But, in every race, there is a winner and that makes life interesting.&nbsp;There are lots of actresses to choose from, but these great stars made it to the top. Check out these&nbsp;<strong>top&nbsp;5 most beautiful Indian TV serial actresses in 2018.&nbsp;</strong>Without further delay, let us look at our number&nbsp;5 in the list.</p><p><strong>5. Jennifer Winget</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/3b5138e2b127510824fcb617723e5e19.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Indian actress Jennifer Winget was born on May 30, 1985. Winget started her career when she was 12 years old as a child star in the film Raja Jo Rani Se Pyar Ho Gaya and another film on the same role for the movie Kuch Naa Kaho at the age of 14. Likewise, Eastern Eye listed her at the 21st position on the World’s Sexiest Asian Women for 2012.</p><p><strong>4. Surbhi Jyoti</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/983a9ab1c4ddea0914161c3402609bd5.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Surbhi Jyoti is another pretty face on the Indian TV, with her lead role in the Zee TV serial “Qubool Hai”. Her attractive face and captivating eyes have won her huge fan following.</p><p><strong>3. Hina Khan</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/26e50e33601ef2b904cd7ea849f5c810.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Bigg Boss 11 finalist&nbsp;Hina Khan&nbsp;first rose to fame with the longest running soap on Indian Television Yeh Rishta Kya Kehlata Hai, where she played the character of Akshara. Post Yeh Rishta, the actress participated in adventure-based reality show, Khatron Ke Khiladi.</p><p><strong>2.&nbsp;Surbhi Chandana</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/71e6c1780ec5cf2fd287ddc93b915133.webp;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p>Surbhi Chandana aka Annika has flattered us not only by her chemistry with Nakuul Mehta aka Shivay Singh Oberoi but has also given us some cool slangs like Tadi, Michmichi, Raita Fail Gaya, Khidki Tod Idea.</p><p><strong>1.&nbsp;Mouni Roy</strong></p><p class=\"ql-align-center\"><img src=\"http://hl-img.peco.uodoo.com/hubble/app/sm/92e7b1b452830fe562bcaf1286532b54.jpg;,70,JPEGX;3,690x\"></p><p class=\"ql-align-center\"><em style=\"color: rgb(155, 155, 155);\">Third party image reference</em></p><p><br></p>";
        Map map = s.filter(str);
        System.out.println("敏感词出现次数:" + map.getOrDefault("count", 0));
        System.out.println(map.getOrDefault("words","null"));
    }

}
