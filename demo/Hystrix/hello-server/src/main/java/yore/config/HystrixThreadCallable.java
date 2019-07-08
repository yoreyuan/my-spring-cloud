package yore.config;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by yore on 2019/7/6 17:42
 */
public class HystrixThreadCallable <T> implements Callable<T>{

    private final RequestAttributes requestAttributes;
    private final Callable<T> delegate;
    private String params;

    public HystrixThreadCallable(Callable<T> callable, RequestAttributes requestAttributes,String params) {
        this.delegate = callable;
        this.requestAttributes = requestAttributes;
        this.params = params;
    }

    @Override
    public T call() throws Exception {
        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            HystrixThreadLocal.threadLocal.set(params);
            return delegate.call();
        } finally {
            RequestContextHolder.resetRequestAttributes();
            HystrixThreadLocal.threadLocal.remove();
        }
    }

}
