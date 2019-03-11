package com.github.dockerjava.api.model;


import org.junit.Assert;
import org.junit.Test;


public class VolumeTest {
    @Test
    public void getPath() {
        Assert.assertEquals(new Volume("/path").getPath(), "/path");
    }
}

