package yore.entity;

import lombok.Data;

/**
 * Created by yore on 2019/7/13 22:11
 */
@Data
public class ZuulRouteEntity {
    private String id;

    private String path;

    private String serviceId;

    private String url;

    private boolean stripPrefix = true;

    private Boolean retryable;

    private Boolean enabled;

    private String description;

}
