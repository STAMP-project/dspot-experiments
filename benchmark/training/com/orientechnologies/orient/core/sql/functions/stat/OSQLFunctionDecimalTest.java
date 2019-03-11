package com.orientechnologies.orient.core.sql.functions.stat;


import com.orientechnologies.orient.core.sql.functions.math.OSQLFunctionDecimal;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


public class OSQLFunctionDecimalTest {
    private OSQLFunctionDecimal function;

    @Test
    public void testEmpty() {
        Object result = function.getResult();
        Assert.assertNull(result);
    }

    @Test
    public void testFromInteger() {
        function.execute(null, null, null, new Object[]{ 12 }, null);
        Object result = function.getResult();
        Assert.assertEquals(result, new BigDecimal(12));
    }

    @Test
    public void testFromLong() {
        function.execute(null, null, null, new Object[]{ 1287623847384L }, null);
        Object result = function.getResult();
        Assert.assertEquals(result, new BigDecimal(1287623847384L));
    }

    @Test
    public void testFromString() {
        String initial = "12324124321234543256758654.76543212345676543254356765434567654";
        function.execute(null, null, null, new Object[]{ initial }, null);
        Object result = function.getResult();
        Assert.assertEquals(result, new BigDecimal(initial));
    }
}

