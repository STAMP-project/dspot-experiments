package org.nd4j.util;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class OneTimeLoggerTest {
    @Test
    public void testLogger1() throws Exception {
        OneTimeLogger.info(log, "Format: {}; Pew: {};", 1, 2);
    }

    @Test
    public void testBuffer1() throws Exception {
        Assert.assertTrue(OneTimeLogger.isEligible("Message here"));
        Assert.assertFalse(OneTimeLogger.isEligible("Message here"));
        Assert.assertTrue(OneTimeLogger.isEligible("Message here 23"));
    }
}

