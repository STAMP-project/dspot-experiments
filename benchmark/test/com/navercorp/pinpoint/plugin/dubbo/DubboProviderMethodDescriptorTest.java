package com.navercorp.pinpoint.plugin.dubbo;


import MethodType.WEB_REQUEST;
import org.junit.Assert;
import org.junit.Test;


public class DubboProviderMethodDescriptorTest {
    @Test
    public void test() {
        DubboProviderMethodDescriptor descriptor = new DubboProviderMethodDescriptor();
        Assert.assertEquals(descriptor.getApiDescriptor(), "Dubbo Provider Process");
        Assert.assertEquals(descriptor.getApiId(), 0);
        Assert.assertNull(descriptor.getClassName());
        Assert.assertEquals(descriptor.getFullName(), "com.navercorp.pinpoint.plugin.dubbo.DubboProviderMethodDescriptor.invoke()");
        Assert.assertEquals(descriptor.getLineNumber(), (-1));
        Assert.assertNull(descriptor.getMethodName());
        Assert.assertNull(descriptor.getParameterDescriptor());
        Assert.assertArrayEquals(descriptor.getParameterTypes(), new String[0]);
        Assert.assertArrayEquals(descriptor.getParameterVariableName(), new String[0]);
        Assert.assertEquals(descriptor.getType(), WEB_REQUEST);
    }
}

