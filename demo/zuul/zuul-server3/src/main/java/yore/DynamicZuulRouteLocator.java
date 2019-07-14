package yore;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.util.StringUtils;

import yore.dao.PropertiesDao;

/**
 * Created by yore on 2019/7/13 22:19
 */
public class DynamicZuulRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    @Autowired
    private ZuulProperties properties;

    @Autowired
    private PropertiesDao propertiesDao;

    public DynamicZuulRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.properties = properties;
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    /**
     * 可以参考 {@link org.springframework.cloud.netflix.zuul.filters.discovery} 的方法locateRoutes()
     */
    @Override
    protected Map<String, ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<>();
        routesMap.putAll(super.locateRoutes());
        routesMap.putAll(propertiesDao.getProperties());
        LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
        routesMap.forEach((key, value) -> {
            String path = key;
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.hasText(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, value);
        });
        return values;
    }
}
