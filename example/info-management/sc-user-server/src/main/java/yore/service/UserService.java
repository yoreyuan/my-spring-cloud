package yore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by yore on 2019/7/27 19:58
 */
@Component
public class UserService implements IUserService{

    @Autowired
    private DataService dataService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getDefaultUser() {
        return dataService.getDefaultUser();
    }

    @Override
    public String getContextUserId() {
        return dataService.getContextUserId();
    }

    @Override
    public List<String> getProviderData() {
        List<String> result = restTemplate.getForObject("http://sc-data-service/getProviderData", List.class);
        return result;
    }
}
