package com.alibaba.json.bvt.cglib;


import com.alibaba.fastjson.JSON;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Assert;


public class TestCglib extends TestCase {
    public void test_cglib() throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TestCglib.Entity.class);
        enhancer.setCallback(new TestCglib.Proxy());
        TestCglib.Entity entity = ((TestCglib.Entity) (enhancer.create()));
        entity.setId(3);
        entity.setName("Jobs");
        String text = JSON.toJSONString(entity);
        Assert.assertEquals("{\"id\":3,\"name\":\"Jobs\"}", text);
    }

    public static class Proxy implements MethodInterceptor {
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return proxy.invokeSuper(obj, args);
        }
    }

    public static class Entity {
        private int id;

        private String name;

        public Entity() {
        }

        public Entity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

