/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression;


import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author tw
 */
public class LongValueTest {
    public LongValueTest() {
    }

    @Test
    public void testSimpleNumber() {
        LongValue value = new LongValue("123");
        Assert.assertEquals("123", value.getStringValue());
        Assert.assertEquals(123L, value.getValue());
        Assert.assertEquals(new BigInteger("123"), value.getBigIntegerValue());
    }

    @Test
    public void testLargeNumber() {
        final String largeNumber = "20161114000000035001";
        LongValue value = new LongValue(largeNumber);
        Assert.assertEquals(largeNumber, value.getStringValue());
        try {
            value.getValue();
            Assert.fail("should not work");
        } catch (Exception e) {
            // expected to fail
        }
        Assert.assertEquals(new BigInteger(largeNumber), value.getBigIntegerValue());
    }
}

