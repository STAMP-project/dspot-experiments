package com.test.fastjson;


import com.jarvis.cache.clone.Cloning;
import com.jarvis.cache.serializer.FastjsonSerializer;
import com.jarvis.cache.serializer.HessianSerializer;
import com.jarvis.cache.serializer.JacksonJsonSerializer;
import com.jarvis.cache.serializer.JacksonMsgpackSerializer;
import com.jarvis.cache.serializer.JdkSerializer;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * ?????? Method????
 *
 * @author jiayu.qiu
 */
public class DeepCloneMethodArgsTest {
    private static int hot = 10000;

    private static int run = 100000;

    @Test
    public void testDeepClone() throws Exception {
        Method[] methods = DeepCloneMethodArgsTest.class.getDeclaredMethods();
        String testName = "getUserList";
        Method method = null;
        for (Method m : methods) {
            if (m.getName().equals(testName)) {
                method = m;
                break;
            }
        }
        Assert.assertNotNull(method);
        Object[] args = DeepCloneMethodArgsTest.getArgs();
        testSerializer(new JdkSerializer(), method, args);
        testSerializer(new HessianSerializer(), method, args);
        testSerializer(new FastjsonSerializer(), method, args);
        testSerializer(new JacksonJsonSerializer(), method, args);
        testSerializer(new JacksonMsgpackSerializer(), method, args);
        testSerializer(new Cloning(), method, args);
    }
}

