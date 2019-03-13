/**
 * Created on 2018/1/19.
 */
package com.alicp.jetcache.anno.method;


import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class SpelEvaluatorTest {
    @Test
    public void test() throws Exception {
        SpelEvaluatorTest.RootObject root = new SpelEvaluatorTest.RootObject();
        setArgs(new Object[]{ "123", 456 });
        Method m = SpelEvaluatorTest.class.getMethod("targetMethod", String.class, int.class);
        SpelEvaluator e = new SpelEvaluator("bean('a')", m);
        Assertions.assertEquals("a_bean", e.apply(root));
        e = new SpelEvaluator("#p1", m);
        Assertions.assertEquals("123", e.apply(root));
        root = new SpelEvaluatorTest.RootObject();
        setArgs(new Object[]{ null, 456 });
        Assertions.assertNull(e.apply(root));
    }

    public static class RootObject extends CacheInvokeContext {
        public String bean(String name) {
            return name + "_bean";
        }
    }
}

