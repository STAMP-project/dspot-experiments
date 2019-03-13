package com.orientechnologies.orient.core.sql.method.misc;


import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class OSQLMethodValuesTest {
    private OSQLMethodValues function;

    @Test
    public void testWithOResult() {
        OResultInternal resultInternal = new OResultInternal();
        resultInternal.setProperty("name", "Foo");
        resultInternal.setProperty("surname", "Bar");
        Object result = function.execute(null, null, null, resultInternal, null);
        Assert.assertEquals(Arrays.asList("Foo", "Bar"), result);
    }
}

