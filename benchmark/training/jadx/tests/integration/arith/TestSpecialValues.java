package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSpecialValues extends IntegrationTest {
    public static class TestCls {
        public void test() {
            shorts(Short.MIN_VALUE, Short.MAX_VALUE);
            bytes(Byte.MIN_VALUE, Byte.MAX_VALUE);
            ints(Integer.MIN_VALUE, Integer.MAX_VALUE);
            longs(Long.MIN_VALUE, Long.MAX_VALUE);
            floats(Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_NORMAL);
            doubles(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_NORMAL);
        }

        private void shorts(short... v) {
        }

        private void bytes(byte... v) {
        }

        private void ints(int... v) {
        }

        private void longs(long... v) {
        }

        private void floats(float... v) {
        }

        private void doubles(double... v) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSpecialValues.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne(("Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, " + "Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_NORMAL")));
        Assert.assertThat(code, JadxMatchers.containsOne(("Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, " + "Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_NORMAL")));
        Assert.assertThat(code, JadxMatchers.containsOne("Short.MIN_VALUE, Short.MAX_VALUE"));
        Assert.assertThat(code, JadxMatchers.containsOne("Byte.MIN_VALUE, Byte.MAX_VALUE"));
        Assert.assertThat(code, JadxMatchers.containsOne("Integer.MIN_VALUE, Integer.MAX_VALUE"));
        Assert.assertThat(code, JadxMatchers.containsOne("Long.MIN_VALUE, Long.MAX_VALUE"));
    }
}

