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


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author toben
 */
public class StringValueTest {
    public StringValueTest() {
    }

    @Test
    public void testGetValue() {
        StringValue instance = new StringValue("'*'");
        String expResult = "*";
        String result = instance.getValue();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testGetValue2_issue329() {
        StringValue instance = new StringValue("*");
        String expResult = "*";
        String result = instance.getValue();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getNotExcapedValue method, of class StringValue.
     */
    @Test
    public void testGetNotExcapedValue() {
        StringValue instance = new StringValue("'*''*'");
        String expResult = "*'*";
        String result = instance.getNotExcapedValue();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testPrefixes() {
        checkStringValue("E'test'", "test", "E");
        checkStringValue("'test'", "test", null);
    }
}

