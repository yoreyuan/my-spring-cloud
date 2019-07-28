package yore.common.util;

import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import yore.common.vo.User;

/**
 * Created by yore on 2019/7/27 19:59
 */
public class AuthUtil {

    public static boolean authUser(User user, HttpServletResponse respone) throws Exception{
        if(StringUtils.isEmpty(user.getUserId())) {
            return false;
        }else {
            return true;
        }
    }

}
