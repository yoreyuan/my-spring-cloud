package yore.common.context;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 *
 * Created by yore on 2019/7/27 22:10
 */
public class HystrixThreadCallable<S> implements Callable<S>{
    private final RequestAttributes requestAttributes;
    private final Callable<S> delegate;
    private String params;

    public HystrixThreadCallable(Callable<S> callable, RequestAttributes requestAttributes,String params) {
        this.delegate = callable;
        this.requestAttributes = requestAttributes;
        this.params = params;
    }

    @Override
    public S call() throws Exception {
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

/**
 * ThreadLocal对象保存用户的信息
 */
class HystrixThreadLocal {
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
}