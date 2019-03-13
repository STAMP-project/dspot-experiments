package com.blade.ioc;


import com.blade.ioc.reader.ClassPathClassReader;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class DynamicContextTest {
    @Test
    public void testDynamicContext() {
        ClassReader classReader = DynamicContext.getClassReader("com.blade.ioc");
        Assert.assertEquals(ClassPathClassReader.class, classReader.getClass());
        Assert.assertEquals(false, DynamicContext.isJarPackage("com.blade.ioc"));
        Assert.assertNotNull(DynamicContext.getClassReader("io.netty.handler.codec"));
        Assert.assertEquals(true, DynamicContext.isJarPackage("io.netty.handler.codec"));
        Assert.assertEquals(false, DynamicContext.isJarPackage(""));
        Assert.assertEquals(false, DynamicContext.isJarContext());
    }

    @Test
    public void testInit() {
        DynamicContext.init(DynamicContext.class);
    }
}

