package yore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import yore.DynamicZuulRouteLocator;

/**
 * Created by yore on 2019/7/13 22:32
 */
@Configuration
public class DynamicZuulConfig {

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private ServerProperties serverProperties;

    @Bean
    public DynamicZuulRouteLocator routeLocator() {
        DynamicZuulRouteLocator routeLocator = new DynamicZuulRouteLocator(
                serverProperties.getServlet().getServletPrefix(), zuulProperties);
        return routeLocator;
    }

}
