package com.baeldung.maven.plugins;


import org.junit.Assert;
import org.junit.Test;


public class DataCheck {
    @Test
    public void whenDataObjectIsCreated_thenItIsNotNull() {
        Data data = new Data();
        Assert.assertNotNull(data);
    }
}

