package com.github.dockerjava.api.model;


import AccessMode.DEFAULT;
import AccessMode.rw;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AccessModeTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void defaultAccessMode() {
        Assert.assertEquals(DEFAULT, AccessMode.rw);
    }

    @Test
    public void stringify() {
        Assert.assertEquals(rw.toString(), "rw");
    }

    @Test
    public void fromString() {
        Assert.assertEquals(AccessMode.valueOf("rw"), AccessMode.rw);
    }

    @Test
    public void fromIllegalString() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("No enum const");
        AccessMode.valueOf("xx");
    }
}

