package yore.common.intercepter;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import yore.common.context.UserContextHolder;
import yore.common.vo.User;

/**
 * 在使用 RestTemplate 进行服务间调用时会拦截到请求，
 * 并将用户属性放到header里
 *
 * Created by yore on 2019/7/27 22:16
 */
public class RestTemplateUserContextInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        User user = UserContextHolder.currentUser();
        Map<String, String> headers = user.toHttpHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.getHeaders().add(header.getKey(), header.getValue());
        }
        // 调用
        return execution.execute(request, body);
    }
}