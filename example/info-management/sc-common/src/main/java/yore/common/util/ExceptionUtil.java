package yore.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * Created by yore on 2019/7/27 20:00
 */
public class ExceptionUtil {

    /**
     * 异常枚举转类型换为英文code
     * @param error 异常枚举
     * @return String
     */
    public static String errorToCodeEN(Enum<?> error) {
        String errorName = error.name().toLowerCase();
        String[] sp = errorName.split("_");
        StringBuffer code = new StringBuffer();
        for (String s : sp) {
            code.append(StringUtils.capitalize(s));
        }
        return code.toString();
    }

}
