package org.robovm.rt.lambdas.test001;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Lambda expression test without parameters.
 */
public class NoParametersTest {
    @Test
    public void test001() {
        PrintStream oldStream = System.out;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            V v = () -> {
                System.out.print("Empty lambda");
            };
            v.noop();
            Assert.assertEquals("Empty lambda", new String(out.toByteArray()));
        } finally {
            System.setOut(oldStream);
        }
        B b = () -> {
            return true;
        };
        Assert.assertEquals(true, b.getValue());
        By by = () -> {
            return ((byte) (-127));
        };
        Assert.assertEquals((-127), by.getValue());
        C c = () -> {
            return ((char) (Character.MAX_VALUE));
        };
        Assert.assertEquals(Character.MAX_VALUE, c.getValue());
        S s = () -> {
            return ((short) (Short.MIN_VALUE));
        };
        Assert.assertEquals(Short.MIN_VALUE, s.getValue());
        I i = () -> {
            return 123;
        };
        Assert.assertEquals(123, i.getValue());
        L l = () -> {
            return 456;
        };
        Assert.assertEquals(456, l.getValue());
        F f = () -> {
            return 1.234F;
        };
        Assert.assertEquals(1.234F, f.getValue(), 0);
        D d = () -> {
            return 1.234;
        };
        Assert.assertEquals(1.234, d.getValue(), 0);
        O o = () -> {
            return "Test";
        };
        Assert.assertEquals("Test", o.getValue());
    }
}

