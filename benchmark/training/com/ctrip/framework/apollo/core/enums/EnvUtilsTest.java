package com.ctrip.framework.apollo.core.enums;


import Env.DEV;
import Env.FAT;
import Env.UAT;
import Env.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class EnvUtilsTest {
    @Test
    public void testTransformEnv() throws Exception {
        Assert.assertEquals(DEV, EnvUtils.transformEnv(DEV.name()));
        Assert.assertEquals(FAT, EnvUtils.transformEnv(FAT.name().toLowerCase()));
        Assert.assertEquals(UAT, EnvUtils.transformEnv(((" " + (UAT.name().toUpperCase())) + "")));
        Assert.assertEquals(UNKNOWN, EnvUtils.transformEnv("someInvalidEnv"));
    }

    @Test
    public void testFromString() throws Exception {
        Assert.assertEquals(DEV, Env.fromString(DEV.name()));
        Assert.assertEquals(FAT, Env.fromString(FAT.name().toLowerCase()));
        Assert.assertEquals(UAT, Env.fromString(((" " + (UAT.name().toUpperCase())) + "")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromInvalidString() throws Exception {
        Env.fromString("someInvalidEnv");
    }
}

