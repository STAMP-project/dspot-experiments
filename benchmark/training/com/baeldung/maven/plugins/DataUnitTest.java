package com.baeldung.maven.plugins;


import org.junit.Assert;
import org.junit.Test;


public class DataUnitTest {
    @Test
    public void whenDataObjectIsNotCreated_thenItIsNull() {
        Data data = null;
        Assert.assertNull(data);
    }
}

