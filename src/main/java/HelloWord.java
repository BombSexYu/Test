import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HelloWord {
    public static void main(String[] args) throws IOException {
        String str = "art|tags";
        String url = "https://www.googleapis.com/customsearch/v1?num=10&cx=000576016485080739186:qm_wxpwbn_8&key=AIzaSyBQ2E2TThZlPaeQKa-Ll4npTVyeQMzU_a8&q=Korean+Air+Lines+jet+forced+down+over+Soviet+Union+at+1978";
        String host = "119.102.24.141";
        int port = 9999;
        System.out.println(proxyHttpGetRequest(url, host, port));

    }

    public static  String proxyHttpGetRequest(String url,String host,int port) throws IOException {
        //代理对象
        HttpHost proxy = new HttpHost(host,port,"http");
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        //实例化CloseableHttpClient对象
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        //访问目标地址
        HttpGet httpGet = new HttpGet(url);
        //请求返回
        CloseableHttpResponse httpResp = httpclient.execute(httpGet);
        return EntityUtils.toString(httpResp.getEntity());
    }
}
