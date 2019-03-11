package com.orientechnologies.orient.core.sql.method.misc;


import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.junit.Assert;
import org.junit.Test;


public class OSQLMethodKeysTest {
    private OSQLMethodKeys function;

    @Test
    public void testWithOResult() {
        OResultInternal resultInternal = new OResultInternal();
        resultInternal.setProperty("name", "Foo");
        resultInternal.setProperty("surname", "Bar");
        Object result = function.execute(null, null, null, resultInternal, null);
        Assert.assertEquals(new LinkedHashSet(Arrays.asList("name", "surname")), result);
    }
}

