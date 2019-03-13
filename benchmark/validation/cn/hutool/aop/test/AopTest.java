package cn.hutool.aop.test;


import cn.hutool.aop.ProxyUtil;
import cn.hutool.aop.aspects.TimeIntervalAspect;
import org.junit.Assert;
import org.junit.Test;


/**
 * AOP??????
 *
 * @author Looly
 */
public class AopTest {
    @Test
    public void aopTest() {
        AopTest.Animal cat = ProxyUtil.proxy(new AopTest.Cat(), TimeIntervalAspect.class);
        String result = cat.eat();
        Assert.assertEquals("???", result);
    }

    @Test
    public void aopByCglibTest() {
        AopTest.Dog dog = ProxyUtil.proxy(new AopTest.Dog(), TimeIntervalAspect.class);
        String result = dog.eat();
        Assert.assertEquals("???", result);
    }

    static interface Animal {
        String eat();
    }

    /**
     * ???
     *
     * @author looly
     */
    static class Cat implements AopTest.Animal {
        @Override
        public String eat() {
            return "???";
        }
    }

    /**
     * ???
     *
     * @author looly
     */
    static class Dog {
        public String eat() {
            return "???";
        }
    }
}

