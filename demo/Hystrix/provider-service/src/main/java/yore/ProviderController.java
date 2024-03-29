package yore;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yore on 2019/7/7 22:16
 */
@RestController
public class ProviderController {
    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/getDashboard")
    public List<String> getProviderData(){
        List<String> provider = new ArrayList<String>();
        provider.add("hystrix dashboard");
        return provider;
    }


    @GetMapping("/getHelloService")
    public String getHelloService(){
        return consumerService.getHelloServiceData();
    }
}
