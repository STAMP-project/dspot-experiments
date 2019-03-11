package us.codecraft.tinyioc.aop;


import org.junit.Test;
import us.codecraft.tinyioc.HelloWorldService;
import us.codecraft.tinyioc.HelloWorldServiceImpl;
import us.codecraft.tinyioc.context.ApplicationContext;
import us.codecraft.tinyioc.context.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author yihua.huang@dianping.com
 */
public class JdkDynamicAopProxyTest {
    @Test
    public void testInterceptor() throws Exception {
        // --------- helloWorldService without AOP
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc.xml");
        HelloWorldService helloWorldService = ((HelloWorldService) (applicationContext.getBean("helloWorldService")));
        helloWorldService.helloWorld();
        // --------- helloWorldService with AOP
        // 1. ???????(Joinpoint)
        AdvisedSupport advisedSupport = new AdvisedSupport();
        TargetSource targetSource = new TargetSource(helloWorldService, HelloWorldServiceImpl.class, HelloWorldService.class);
        advisedSupport.setTargetSource(targetSource);
        // 2. ?????(Advice)
        TimerInterceptor timerInterceptor = new TimerInterceptor();
        advisedSupport.setMethodInterceptor(timerInterceptor);
        // 3. ????(Proxy)
        JdkDynamicAopProxy jdkDynamicAopProxy = new JdkDynamicAopProxy(advisedSupport);
        HelloWorldService helloWorldServiceProxy = ((HelloWorldService) (jdkDynamicAopProxy.getProxy()));
        // 4. ??AOP???
        helloWorldServiceProxy.helloWorld();
    }
}

