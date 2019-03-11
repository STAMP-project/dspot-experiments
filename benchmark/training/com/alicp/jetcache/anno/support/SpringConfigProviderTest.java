/**
 * Created on 2018/3/28.
 */
package com.alicp.jetcache.anno.support;


import FastjsonKeyConvertor.INSTANCE;
import com.alicp.jetcache.anno.SerialPolicy;
import com.alicp.jetcache.support.JavaValueEncoder;
import com.alicp.jetcache.support.KryoValueDecoder;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.StaticApplicationContext;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class SpringConfigProviderTest {
    private StaticApplicationContext context;

    private DefaultListableBeanFactory beanFactory;

    private SpringConfigProvider cp;

    @Test
    public void testParseValueEncoder() {
        Assertions.assertEquals(JavaValueEncoder.class, cp.parseValueEncoder("java").getClass());
        Function<Object, byte[]> func = ( o) -> null;
        beanFactory.registerSingleton("myBean", func);
        Assertions.assertSame(func, cp.parseValueEncoder("bean:myBean"));
        SerialPolicy sp = new SerialPolicy() {
            @Override
            public Function<Object, byte[]> encoder() {
                return func;
            }

            @Override
            public Function<byte[], Object> decoder() {
                return null;
            }
        };
        beanFactory.registerSingleton("sp", sp);
        Assertions.assertSame(func, cp.parseValueEncoder("bean:sp"));
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> cp.parseValueEncoder("bean:not_exists"));
    }

    @Test
    public void testParseValueDecoder() {
        Assertions.assertEquals(KryoValueDecoder.class, cp.parseValueDecoder("kryo").getClass());
        Function<byte[], Object> func = ( o) -> null;
        beanFactory.registerSingleton("myBean", func);
        Assertions.assertSame(func, cp.parseValueDecoder("bean:myBean"));
        SerialPolicy sp = new SerialPolicy() {
            @Override
            public Function<Object, byte[]> encoder() {
                return null;
            }

            @Override
            public Function<byte[], Object> decoder() {
                return func;
            }
        };
        beanFactory.registerSingleton("sp", sp);
        Assertions.assertSame(func, cp.parseValueDecoder("bean:sp"));
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> cp.parseValueDecoder("bean:not_exists"));
    }

    @Test
    public void testParseKeyConvertor() {
        Assertions.assertSame(INSTANCE, cp.parseKeyConvertor("fastjson"));
        Function<Object, Object> func = ( o) -> null;
        beanFactory.registerSingleton("cvt", func);
        Assertions.assertSame(func, cp.parseKeyConvertor("bean:cvt"));
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> cp.parseKeyConvertor("bean:not_exists"));
    }
}

