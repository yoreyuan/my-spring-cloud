package yore.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by yore on 2019/7/14 6:14
 */
@Configuration
public class ModifyRequestEntityFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;// =6
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        try {
            com.netflix.zuul.context.RequestContext context = com.netflix.zuul.context.RequestContext.getCurrentContext();
            String charset = context.getRequest().getCharacterEncoding();
            InputStream in = (InputStream)context.get("requestEntity");
            if(in == null){
                in = context.getRequest().getInputStream();
                String body = StreamUtils.copyToString(in, Charset.forName(charset));
                // 新增参数
                body += "&weight=140";
                byte[] bytes = body.getBytes(charset);
                context.setRequest(new com.netflix.zuul.http.HttpServletRequestWrapper(context.getRequest()) {

                    @Override
                    public javax.servlet.ServletInputStream getInputStream() throws IOException {
                        return new com.netflix.zuul.http.ServletInputStreamWrapper(bytes);
                    }

                    @Override
                    public int getContentLength() {
                        return bytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return bytes.length;
                    }
                });
            }

        }catch (IOException e){
            ReflectionUtils.rethrowRuntimeException(e);
        }
        return null;
    }

}
