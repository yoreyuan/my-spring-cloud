package yore.common.context;

import yore.common.vo.User;

/**
 * 用户上下文
 *
 * Created by yore on 2019/7/28 19:25
 */
public class UserContextHolder {

    public static ThreadLocal<User> context = new ThreadLocal<User>();

    public static User currentUser() {
        return context.get();
    }

    public static void set(User user) {
        context.set(user);
    }

    public static void shutdown() {
        context.remove();
    }

}
