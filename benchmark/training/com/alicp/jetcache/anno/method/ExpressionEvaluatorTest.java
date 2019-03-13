/**
 * Created on 2018/1/19.
 */
package com.alicp.jetcache.anno.method;


import com.alicp.jetcache.CacheConfigException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class ExpressionEvaluatorTest {
    @Test
    public void test() throws Exception {
        Method m = ExpressionEvaluatorTest.class.getMethod("targetMethod", String.class, int.class);
        ExpressionEvaluator e = new ExpressionEvaluator("1+1", m);
        Assertions.assertTrue(((e.getTarget()) instanceof SpelEvaluator));
        e = new ExpressionEvaluator("spel{1+1}", m);
        Assertions.assertTrue(((e.getTarget()) instanceof SpelEvaluator));
        e = new ExpressionEvaluator("mvel{1+1}", m);
        Assertions.assertTrue(((e.getTarget()) instanceof MvelEvaluator));
        Assertions.assertThrows(CacheConfigException.class, () -> new ExpressionEvaluator("xxx{1+1}", m));
    }
}

