package yore.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * 该过滤器，是将header里面的gray_mark作为指标，如果gray_mark等于enable时，将该请求路由到灰度节点gray-host上，
 * 如果不等于或者没有这个标记，就路由到其他节点。
 * RibbonFilterContextHolder是我们引入的依赖中的核心类，它定义了基于metadata的一种负载均衡机制。
 *
 * Created by yore on 2019/7/13 23:59
 */
public class GrayFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return !ctx.containsKey(FilterConstants.FORWARD_TO_KEY) && !ctx.containsKey(FilterConstants.SERVICE_ID_KEY);
    }

    @Override
    public Object run() throws ZuulException {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String mark = request.getHeader("gray_mark");
        if (!StringUtils.isEmpty(mark) && "enable".equals(mark)) {
            RibbonFilterContextHolder.getCurrentContext()
                    .add("host-mark", "gray-host");
        } else {
            RibbonFilterContextHolder.getCurrentContext()
                    .add("host-mark", "running-host");
        }
        return null;
    }

}
