/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import org.geotools.jdbc.VirtualTable;
import org.junit.Assert;
import org.junit.Test;


public class ModificationProxyClonerTest {
    @Test
    public void testCloneNull() {
        Object copy = ModificationProxyCloner.clone(null);
        Assert.assertNull(copy);
    }

    @Test
    public void testCloneString() {
        String source = new String("abc");
        String copy = ModificationProxyCloner.clone(source);
        Assert.assertSame(source, copy);
    }

    @Test
    public void testCloneDouble() {
        Double source = new Double(12.56);
        Double copy = ModificationProxyCloner.clone(source);
        Assert.assertSame(source, copy);
    }

    @Test
    public void testCloneCloneable() {
        ModificationProxyClonerTest.TestCloneable source = new ModificationProxyClonerTest.TestCloneable("test");
        ModificationProxyClonerTest.TestCloneable copy = ModificationProxyCloner.clone(source);
        Assert.assertNotSame(source, copy);
        Assert.assertEquals(source, copy);
    }

    @Test
    public void testByCopyConstructor() {
        VirtualTable source = new VirtualTable("test", "select * from tables");
        VirtualTable copy = ModificationProxyCloner.clone(source);
        Assert.assertNotSame(source, copy);
        Assert.assertEquals(source, copy);
    }

    @Test
    public void testNotCloneable() {
        ModificationProxyClonerTest.TestNotCloneable source = new ModificationProxyClonerTest.TestNotCloneable("test");
        ModificationProxyClonerTest.TestNotCloneable copy = ModificationProxyCloner.clone(source);
        Assert.assertNotSame(source, copy);
        Assert.assertEquals(source, copy);
    }

    static class TestNotCloneable {
        private String myState;

        public TestNotCloneable(String myState) {
            this.myState = myState;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((myState) == null ? 0 : myState.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            ModificationProxyClonerTest.TestNotCloneable other = ((ModificationProxyClonerTest.TestNotCloneable) (obj));
            if ((myState) == null) {
                if ((other.myState) != null)
                    return false;

            } else
                if (!(myState.equals(other.myState)))
                    return false;


            return true;
        }
    }

    static class TestCloneable extends ModificationProxyClonerTest.TestNotCloneable {
        public TestCloneable(String myState) {
            super(myState);
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}

