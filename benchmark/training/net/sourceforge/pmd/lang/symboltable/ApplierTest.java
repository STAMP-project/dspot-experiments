/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.symboltable;


import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.util.SearchFunction;
import org.junit.Assert;
import org.junit.Test;


public class ApplierTest {
    private static class MyFunction implements SearchFunction<Object> {
        private int numCallbacks = 0;

        private final int maxCallbacks;

        MyFunction(int maxCallbacks) {
            this.maxCallbacks = maxCallbacks;
        }

        @Override
        public boolean applyTo(Object o) {
            (this.numCallbacks)++;
            return (numCallbacks) < (maxCallbacks);
        }

        public int getNumCallbacks() {
            return this.numCallbacks;
        }
    }

    @Test
    public void testSimple() {
        ApplierTest.MyFunction f = new ApplierTest.MyFunction(Integer.MAX_VALUE);
        List<Object> l = new ArrayList<>();
        l.add(new Object());
        l.add(new Object());
        l.add(new Object());
        Applier.apply(f, l.iterator());
        Assert.assertEquals(l.size(), f.getNumCallbacks());
    }

    @Test
    public void testLimit() {
        ApplierTest.MyFunction f = new ApplierTest.MyFunction(2);
        List<Object> l = new ArrayList<>();
        l.add(new Object());
        l.add(new Object());
        l.add(new Object());
        Applier.apply(f, l.iterator());
        Assert.assertEquals(2, f.getNumCallbacks());
    }
}

