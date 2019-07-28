package yore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import yore.common.context.UserContextHolder;
import yore.config.DataConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yore on 2019/7/27 20:44
 */
@RestController
public class DataController {

    @Autowired
    private DataConfig dataConfig;

    @GetMapping("/getContextUserId")
    public String getContextUserId(){
        return UserContextHolder.currentUser().getUserId();
    }

    @GetMapping("/getDefaultUser")
    public String getDefaultUser(){
        return dataConfig.getDefaultUser();
    }

    @GetMapping("/getProviderData")
    public List<String> getProviderData(){
        List<String> provider = new ArrayList<String>();
        provider.add("Beijing Company");
        provider.add("Shanghai Company");
        provider.add("Shenzhen Company");
        return provider;
    }

}
