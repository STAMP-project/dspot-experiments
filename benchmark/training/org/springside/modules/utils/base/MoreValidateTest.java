package org.springside.modules.utils.base;


import org.junit.Test;


public class MoreValidateTest {
    @Test
    public void test() {
        // int
        int a = MoreValidate.nonNegative("x", 0);
        assertThat(a).isEqualTo(0);
        a = MoreValidate.nonNegative("x", 1);
        assertThat(a).isEqualTo(1);
        a = MoreValidate.positive("x", 1);
        assertThat(a).isEqualTo(1);
        // Integer
        Integer c = MoreValidate.nonNegative("x", Integer.valueOf(0));
        assertThat(c).isEqualTo(0);
        c = MoreValidate.nonNegative("x", Integer.valueOf(21));
        assertThat(c).isEqualTo(21);
        c = MoreValidate.positive("x", Integer.valueOf(1));
        assertThat(c).isEqualTo(1);
        // long
        long b = MoreValidate.nonNegative("x", 0L);
        assertThat(b).isEqualTo(0);
        b = MoreValidate.nonNegative("x", 11L);
        assertThat(b).isEqualTo(11);
        b = MoreValidate.positive("x", 1L);
        assertThat(b).isEqualTo(1);
        double e = MoreValidate.nonNegative("x", 0L);
        assertThat(e).isEqualTo(0);
        e = MoreValidate.nonNegative("x", 11.0);
        assertThat(e).isEqualTo(11);
        e = MoreValidate.positive("x", 1.1);
        assertThat(e).isEqualTo(1.1);
        // Long
        Long d = MoreValidate.nonNegative("x", Long.valueOf(0));
        assertThat(d).isEqualTo(0);
        d = MoreValidate.positive("x", Long.valueOf(1));
        assertThat(d).isEqualTo(1);
        d = MoreValidate.nonNegative("x", Long.valueOf(11));
        assertThat(d).isEqualTo(11);
        // int
        try {
            MoreValidate.nonNegative("x", (-1));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).hasMessage("x (-1) must be >= 0");
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.nonNegative(null, (-1));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).hasMessage("null (-1) must be >= 0");
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", (-1));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", 0);
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        // long
        try {
            MoreValidate.nonNegative("x", (-1L));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", (-1L));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", 0L);
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        // Long
        try {
            MoreValidate.nonNegative("x", Long.valueOf((-1)));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", Long.valueOf((-1)));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", Long.valueOf(0));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        // Integer
        try {
            MoreValidate.nonNegative("x", Integer.valueOf((-1)));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", Integer.valueOf((-1)));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", Integer.valueOf(0));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        // double
        try {
            MoreValidate.nonNegative("x", (-9999.2));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", (-1.2));
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            MoreValidate.positive("x", 0.0);
            fail("fail");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
    }
}

