/**
 * Created on 2018/1/19.
 */
package com.alicp.jetcache.anno.method;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class MvelEvaluatorTest {
    @Test
    public void test() {
        MvelEvaluator e = new MvelEvaluator("bean('a')");
        Assertions.assertEquals("a_bean", e.apply(new MvelEvaluatorTest.RootObject()));
    }

    public static class RootObject {
        public String bean(String name) {
            return name + "_bean";
        }
    }
}

