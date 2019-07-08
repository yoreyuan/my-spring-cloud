package yore.service.dataservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * PSFallbackBadRequestExpcetion 类抛出 HystrixBadRequestException
 *
 * Created by yore on 2019/7/7 10:34
 */
public class PSFallbackBadRequestExpcetion extends HystrixCommand<String>{

    private static Logger log = LoggerFactory.getLogger(PSFallbackBadRequestExpcetion.class);

    public PSFallbackBadRequestExpcetion() {
        super(HystrixCommandGroupKey.Factory.asKey("GroupBRE"));
    }

    @Override
    protected String run() throws Exception {
        throw new HystrixBadRequestException("HystrixBadRequestException error");
    }

    @Override
    protected String getFallback() {
        System.out.println(getFailedExecutionException().getMessage());
        return "invoke HystrixBadRequestException fallback method:  ";
    }

}
