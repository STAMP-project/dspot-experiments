package org.slf4j.log4j12;


import java.util.Random;
import org.apache.log4j.MDC;
import org.apache.log4j.MDCFriend;
import org.junit.Assert;
import org.junit.Test;


public class MDCFriendTest {
    private static Random random = new Random();

    int diff = MDCFriendTest.random.nextInt((1024 * 8));

    @Test
    public void smoke() {
        if ((VersionUtil.getJavaMajorVersion()) < 9)
            return;

        MDCFriend.fixForJava9();
        String key = "MDCFriendTest.smoke" + (diff);
        String val = "val" + (diff);
        MDC.put(key, val);
        Assert.assertEquals(val, MDC.get(key));
        MDC.clear();
        Assert.assertNull(MDC.get(key));
    }
}

