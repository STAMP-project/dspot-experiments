package us.codecraft.tinyioc.aop;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author yihua.huang@dianping.com
 */
public class AspectJExpressionPointcutTest {
    @Test
    public void testClassFilter() throws Exception {
        String expression = "execution(* us.codecraft.tinyioc.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut.getClassFilter().matches(us.codecraft.tinyioc.HelloWorldService.class);
        Assert.assertTrue(matches);
    }

    @Test
    public void testMethodInterceptor() throws Exception {
        String expression = "execution(* us.codecraft.tinyioc.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut.getMethodMatcher().matches(us.codecraft.tinyioc.HelloWorldServiceImpl.class.getDeclaredMethod("helloWorld"), us.codecraft.tinyioc.HelloWorldServiceImpl.class);
        Assert.assertTrue(matches);
    }
}

