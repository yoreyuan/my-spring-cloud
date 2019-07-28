package yore.common.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yore on 2019/7/27 19:33
 */
public class HttpConvertUtil {

    /**
     * convert the httpServletRequest headers to headers map
     * @param request request
     * @return Map<String, String>
     */
    public static Map<String, String> httpRequestToMap(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

}
