package TF_IDF;

import com.baidu.aip.nlp.AipNlp;
import org.json.JSONObject;

public class BaiDuAPI {
    //设置APPID/AK/SK
    public static final String APP_ID = "16088244";
    public static final String API_KEY = "pupFVMuugGFCIoHfhNDk47DN";
    public static final String SECRET_KEY = "okIwdmWZ0l7aWe634g37facwpOm8hZym";

    public static void main(String[] args) {
        // 初始化一个AipNlp
        AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 调用接口
        String text = "百度是一家高科技公司";
        String text2 = "The Indian television has today become a big business, with its popularity graph reaching as high as that of Bollywood.But, in every race, there is a winner and that makes life interesting.There are lots of actresses to choose from, but these great stars made it to the top. Check out thesetop5 most beautiful Indian TV serial actresses in 2018.Without further delay, let us look at our number5 in the list.5. Jennifer WingetThird party image referenceIndian actress Jennifer Winget was born on May 30, 1985. Winget started her career when she was 12 years old as a child star in the film Raja Jo Rani Se Pyar Ho Gaya and another film on the same role for the movie Kuch Naa Kaho at the age of 14. Likewise, Eastern Eye listed her at the 21st position on the World’s Sexiest Asian Women for 2012.4. Surbhi JyotiThird party image referenceSurbhi Jyoti is another pretty face on the Indian TV, with her lead role in the Zee TV serial “Qubool Hai”. Her attractive face and captivating eyes have won her huge fan following.3. Hina KhanThird party image referenceBigg Boss 11 finalistHina Khanfirst rose to fame with the longest running soap on Indian Television Yeh Rishta Kya Kehlata Hai, where she played the character of Akshara. Post Yeh Rishta, the actress participated in adventure-based reality show, Khatron Ke Khiladi.2.Surbhi ChandanaThird party image referenceSurbhi Chandana aka Annika has flattered us not only by her chemistry with Nakuul Mehta aka Shivay Singh Oberoi but has also given us some cool slangs like Tadi, Michmichi, Raita Fail Gaya, Khidki Tod Idea.1.Mouni RoyThird party image reference\n";
        String text3 = "भारतीय टीम ने डबलिन में खेले गए पहले टी20 मैच में आयरलैंड को 75 रन से हराकर 2 टी20 मैचों की सीरीज़ में 1-0 कई बढ़त बना ली थी।डबलिन:आज भारत और आयरलैंड के बीच डबलिन में दो टी-20 मैचों की सीरीज का दूसरा T20 मैच खेला जाएगा। भारतीय टीम बुधवार को खेले गए पहले टी-20 मैच को जीत कर इस श्रंखला में एक सोने की बढ़त बनाए हुए हैं और आज होने वाले दूसरे टी20 मैच को भी जीतकर भारतीय टीम इस सीरीज को 2-0 से अपने नाम करना चहेगी।भारतीय टीम को अगले सप्ताह से इंग्लैंड के मुश्किल दौरे पर क्रिकेट खेलना है। लेकिन इंग्लैंड दौरे से पहले आज भारतीय टीम आयरलैंड को हराकर इस टी20 सीरीज को 2-0 से जीत होती है तो निश्चित रुप से भारतीय टीम के खिलाड़ियों का मनोबल बढ़ेगा।पिच रिपोर्ट और मौसमआज डबलिन में सुबह से ही धूप खिली रहेगी और आज डबलिन का औसत तापमान 14°C रहने की उम्मीद है।भारतीय प्लेइंग इलेवन में हो सकता है बदलावबुधवार को खेले गए पहले टी-20 मैच में जीत के बाद प्रेस कॉन्फ्रेंस में कप्तान कोहली ने कहा था कि वह दूसरे T20 मैच में भारतीय टीम में जरूर बदलाव करना चाहेंगे। विराट कोहली के इस बयान के लिहाज से देखा जाए तो दूसरे टी-20 मैच में भारतीय टीम के प्लेइंग इलेवन में बदलाव हो सकता है।यह भी पढ़ेअंतर्राष्ट्रीय टी-20 क्रिकेट में एक पारी में सर्वाधिक 200 रन बनाने वाली टीमें,…केएल राहुल को मिल सकती है भारतीय टीम में जगहPhoto Source: BCCIआज होने वाले दूसरे टी-20 मैच में IPL में शानदार प्रदर्शन करने वाले लोकेश राहुल को भारतीय प्लेइंग इलेवन में जगह मिल सकती है। पहले टी-20 मैच में सभी भारतीय खिलाड़ियों का प्रदर्शन आशा के अनुरूप ही रहा था। लेकिन इसके बावजूद दूसरे T20 मैच में सुरेश रैना या मनीष पांडे की जगह केएल राहुल को टीम में जगह मिल सकती है।एक बार फिर स्पिन पर रहेगा दारोमदारPhoto Source: BCCIआयरलैंड के खिलाफ पहले टी-20 मैच भारतीय टीम के स्पिन गेंदबाज कुलदीप यादव और युजवेंद्र चहल गेंदबाजी की थी और इन दोनों ने मिलकर कुल 7 विकेट्स लिए थे। आज होने वाले दूसरे टी20 मैच में भी इन दोनों स्पिन गेंदबाजो से प्रदर्शन दोहराने की उम्मीद होगी।भारतीय टीम की संभावित प्लेइंग इलेवन:विराट कोहली, रोहित शर्मा, शिखर धवन, सुरेश रैना, महेंद्र सिंह धोनी, हार्दिक पांड्या, मनीष पांडे/केएल राहुल, यजुवेंद्र चहल, कुलदीप यादव, भुवनेश्वर कुमार और जसप्रीत बुमराह।\n";
        //        JSONObject res = client.lexer(text2, null);
        JSONObject res = client.sentimentClassify(text3, null);
        System.out.println(res.toString(2));

    }
}
