package com.alibaba.csp.sentinel;


import Constants.TIME_DROP_VALVE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link Constants}.
 *
 * @author cdfive
 */
public class ConstantsTest {
    @Test
    public void testDefaultTimeDropValue() {
        Assert.assertEquals(4900, TIME_DROP_VALVE);
    }
}

