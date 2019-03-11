package com.test;


import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @unknown jiayu.qiu
 */
public class BeanTest {
    private static final ConcurrentHashMap<String, Boolean> PROCESSING = new ConcurrentHashMap<String, Boolean>();

    @Test
    public void testputIfAbsent() {
        Boolean isProcessing = BeanTest.PROCESSING.putIfAbsent("k1", Boolean.TRUE);// ??????????????????

        Assert.assertNull(isProcessing);
        System.out.println(("isProcessing1==" + isProcessing));
        isProcessing = BeanTest.PROCESSING.putIfAbsent("k1", Boolean.TRUE);// ??????????????????

        System.out.println(("isProcessing2==" + isProcessing));
        Assert.assertEquals(isProcessing, Boolean.TRUE);
    }
}

