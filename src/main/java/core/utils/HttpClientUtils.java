package core.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpClientUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static String doGet(String url) {
        return doGet(url,null);
    }

    public static String doGet(String url, Map<String, String> headers) {
        String htmlStr = "";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36");
        if (headers != null) {
            for (String headKey : headers.keySet()) {
                httpGet.setHeader(headKey, headers.get(headKey));
            }
        }

        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            logger.info("[RESPONSE CODE "+response.getStatusLine().getStatusCode()+"] GET : "+url);
            HttpEntity httpEntity = response.getEntity();
            if(httpEntity!=null){
               InputStream in = httpEntity.getContent();
               BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
               String line = null;
               StringBuilder sb = new StringBuilder();
               while ((line=reader.readLine())!=null){
                   sb.append(line);
               }
               htmlStr=sb.toString();
            }
        } catch (IOException e) {
            logger.error("Create client failed", e);
        } finally {
            try {
                response.close();
                client.close();
            } catch (IOException e) {
                logger.error("Close failed", e);
            }
        }
        return htmlStr;
    }

    public static JSONObject doGet2JSON(String url) {
        return doGet2JSON(url,null);
    }

    public static JSONObject doGet2JSON(String url, Map<String, String> headers) {
        String jsonStr = doGet(url, headers);
        try {
            return JSONObject.parseObject(jsonStr);
        }catch (Exception e){
            return new JSONObject();
        }
    }

    public static String doPost(String url, JSONObject params) {
        return doPost(url,params,null);
    }

    public static String doPost(String url, JSONObject params, Map<String, String> headers) {
        String htmlStr = "";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36");
        if (headers != null) {
            for (String headKey : headers.keySet()) {
                httpPost.setHeader(headKey, headers.get(headKey));
            }
        }

        // 构建消息实体
        StringEntity entity = new StringEntity(params.toJSONString(), Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        // 发送Json格式的数据请求
        entity.setContentType("application/json;charset=UTF-8");
        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            logger.info("RESPONSE CODE["+response.getStatusLine().getStatusCode()+"] POST: "+url+" PARAMS: "+params.toJSONString());
            HttpEntity httpEntity = response.getEntity();
            if(httpEntity!=null){
                InputStream in = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line=reader.readLine())!=null){
                    sb.append(line);
                }
                htmlStr=sb.toString();
            }
        } catch (IOException e) {
            logger.error("Create Client Failed", e);
        } finally {
            try {
                response.close();
                client.close();
            } catch (IOException e) {
                logger.error("Close Failed", e);
            }
        }
        return htmlStr;
    }

    public static JSONObject doPost2JSON(String url, JSONObject params) {
        return doPost2JSON(url,params,null);
    }

    public static JSONObject doPost2JSON(String url, JSONObject params, Map<String, String> headers) {
        String jsonStr = doPost(url,params,headers);
        try {
            return JSONObject.parseObject(jsonStr);
        }catch (Exception e){
            return new JSONObject();
        }
    }
}
