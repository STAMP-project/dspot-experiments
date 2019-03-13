package com.vaadin.data.provider;


import java.util.function.IntFunction;
import org.junit.Test;


public class ReplaceDataProviderTest {
    public static class BeanWithEquals extends ReplaceDataProviderTest.Bean {
        BeanWithEquals(int id) {
            super(id);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ReplaceDataProviderTest.BeanWithEquals that = ((ReplaceDataProviderTest.BeanWithEquals) (o));
            return (id) == (that.id);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    public static class Bean {
        protected final int id;

        private final String fluff;

        Bean(int id) {
            this.id = id;
            this.fluff = "Fluff #" + id;
        }

        public int getId() {
            return id;
        }

        @SuppressWarnings("unused")
        public String getFluff() {
            return fluff;
        }
    }

    @Test
    public void testBeanEquals() {
        doTest(ReplaceDataProviderTest.BeanWithEquals::new);
    }

    @Test
    public void testBeanSame() {
        doTest(ReplaceDataProviderTest.Bean::new);
    }
}

