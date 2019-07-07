package yore;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by yore on 2019/7/6 23:11
 */
public class HttpUtils {

    public static String doFormFile(String url, HttpEntity entity) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity resEntity = response.getEntity();
        String resultString = EntityUtils.toString(resEntity, "utf-8");
        return resultString;
    }

}
