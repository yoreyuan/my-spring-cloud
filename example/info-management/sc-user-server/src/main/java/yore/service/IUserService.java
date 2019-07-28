package yore.service;

import java.util.List;

/**
 * Created by yore on 2019/7/27 19:57
 */
public interface IUserService {

    String getDefaultUser();
    String getContextUserId();
    List<String> getProviderData();

}
