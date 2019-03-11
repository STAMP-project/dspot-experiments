/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class InfoObjectProxyTest {
    @Test
    public void test() throws Exception {
        InfoObjectProxyTest.BeanImpl bean = new InfoObjectProxyTest.BeanImpl();
        ModificationProxy handler = new ModificationProxy(bean);
        Class proxyClass = Proxy.getProxyClass(InfoObjectProxyTest.Bean.class.getClassLoader(), new Class[]{ InfoObjectProxyTest.Bean.class });
        InfoObjectProxyTest.Bean proxy = ((InfoObjectProxyTest.Bean) (proxyClass.getConstructor(new Class[]{ InvocationHandler.class }).newInstance(new Object[]{ handler })));
        bean.setFoo("one");
        bean.setBar(1);
        proxy.setFoo("two");
        proxy.setBar(2);
        proxy.getScratch().add("x");
        proxy.getScratch().add("y");
        Assert.assertEquals("one", bean.getFoo());
        Assert.assertEquals(Integer.valueOf(1), bean.getBar());
        Assert.assertTrue(bean.getScratch().isEmpty());
        Assert.assertEquals("two", proxy.getFoo());
        Assert.assertEquals(Integer.valueOf(2), proxy.getBar());
        Assert.assertEquals(2, proxy.getScratch().size());
        handler.commit();
        Assert.assertEquals("two", bean.getFoo());
        Assert.assertEquals(Integer.valueOf(2), bean.getBar());
        Assert.assertEquals(2, bean.getScratch().size());
    }

    static interface Bean {
        String getFoo();

        void setFoo(String foo);

        Integer getBar();

        void setBar(Integer bar);

        List getScratch();
    }

    static class BeanImpl implements InfoObjectProxyTest.Bean {
        String foo;

        Integer bar;

        List scratch = new ArrayList();

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public Integer getBar() {
            return bar;
        }

        public void setBar(Integer bar) {
            this.bar = bar;
        }

        public List getScratch() {
            return scratch;
        }
    }
}

