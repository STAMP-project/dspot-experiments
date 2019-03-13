package com.vaadin.util;


import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


public class ReflectToolsTest implements Serializable {
    @Test
    public void findCommonBaseType_sameType() {
        Assert.assertSame(Number.class, ReflectTools.findCommonBaseType(Number.class, Number.class));
    }

    @Test
    public void findCommonBaseType_aExtendsB() {
        Assert.assertSame(Number.class, ReflectTools.findCommonBaseType(Integer.class, Number.class));
    }

    @Test
    public void findCommonBaseType_bExtendsA() {
        Assert.assertSame(Number.class, ReflectTools.findCommonBaseType(Number.class, Integer.class));
    }

    @Test
    public void findCommonBaseType_commonBase() {
        Assert.assertSame(Number.class, ReflectTools.findCommonBaseType(Double.class, Integer.class));
    }

    @Test
    public void findCommonBaseType_noCommonBase() {
        Assert.assertSame(Object.class, ReflectTools.findCommonBaseType(String.class, Number.class));
    }
}

