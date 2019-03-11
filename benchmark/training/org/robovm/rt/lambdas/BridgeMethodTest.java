package org.robovm.rt.lambdas;


import org.junit.Assert;
import org.junit.Test;


public class BridgeMethodTest {
    @Test
    public void testBridgeMethods() {
        B b = ( s) -> {
            return "Hello " + s;
        };
        Assert.assertEquals("Hello there", b.m("there"));
        A a = b;
        Assert.assertEquals("Hello there", a.m("there"));
    }
}

