package org.robovm.rt.lambdas.test003;


import org.junit.Assert;
import org.junit.Test;


public class CaptureFieldsLocalsTest {
    @Test
    public void test003() {
        for (int i = 0; i < 10; i++) {
            Locals l = new Locals();
            Assert.assertEquals(i, l.add(i));
        }
        for (int i = 0; i < 10; i++) {
            Fields f = new Fields(i);
            Assert.assertEquals(i, f.add());
        }
        for (int i = 0; i < 10; i++) {
            LocalsAndFields laf = new LocalsAndFields(i);
            Assert.assertEquals((i + i), laf.add(i));
        }
    }
}

