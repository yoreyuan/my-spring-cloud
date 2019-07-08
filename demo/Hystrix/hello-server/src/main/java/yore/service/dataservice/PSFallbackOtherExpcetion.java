package yore.service.dataservice;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Created by yore on 2019/7/7 10:36
 */
public class PSFallbackOtherExpcetion extends HystrixCommand<String> {

    public PSFallbackOtherExpcetion() {
        super(HystrixCommandGroupKey.Factory.asKey("GroupOE"));
    }

    @Override
    protected String run() throws Exception {
        throw new Exception("this command will trigger fallback");
    }

    @Override
    protected String getFallback() {
        return "invoke PSFallbackOtherExpcetion fallback method";
    }

}
