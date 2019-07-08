package yore.service.dataservice;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Created by yore on 2019/7/7 10:40
 */
public class ProviderServiceCommand extends HystrixCommand<String>{

    private final String name;

    public ProviderServiceCommand(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("GroupSC"));
        this.name = name;
    }

    @Override
    protected String run() {
        return "Spring Cloud";
    }

    @Override
    protected String getFallback() {
        return "Failure Spring Cloud";
    }

}
