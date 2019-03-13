package com.querydsl.apt.domain;


import QInterfaceType3Test_C.c1.a;
import QInterfaceType3Test_C.c1.b;
import QInterfaceType3Test_C.c1.c;
import com.querydsl.core.annotations.QueryEntity;
import org.junit.Assert;
import org.junit.Test;


public class InterfaceType3Test {
    @QueryEntity
    public interface A {
        String getA();
    }

    @QueryEntity
    public interface B {
        String getB();
    }

    @QueryEntity
    public interface C extends InterfaceType3Test.A , InterfaceType3Test.B {
        String getC();
    }

    @Test
    public void test() {
        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        Assert.assertNotNull(c);
    }
}

