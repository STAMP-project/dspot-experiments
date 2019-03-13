package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestCmpOp extends IntegrationTest {
    public static class TestCls {
        public boolean testGT(float a) {
            return a > 3.0F;
        }

        public boolean testLT(float b) {
            return b < 2.0F;
        }

        public boolean testEQ(float c) {
            return c == 1.0F;
        }

        public boolean testNE(float d) {
            return d != 0.0F;
        }

        public boolean testGE(float e) {
            return e >= (-1.0F);
        }

        public boolean testLE(float f) {
            return f <= (-2.0F);
        }

        public boolean testGT2(float g) {
            return 4.0F > g;
        }

        public boolean testLT2(long h) {
            return 5 < h;
        }

        public boolean testGE2(double i) {
            return 6.5 < i;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCmpOp.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return a > 3.0f;"));
        Assert.assertThat(code, CoreMatchers.containsString("return b < 2.0f;"));
        Assert.assertThat(code, CoreMatchers.containsString("return c == 1.0f;"));
        Assert.assertThat(code, CoreMatchers.containsString("return d != 0.0f;"));
        Assert.assertThat(code, CoreMatchers.containsString("return e >= -1.0f;"));
        Assert.assertThat(code, CoreMatchers.containsString("return f <= -2.0f;"));
        Assert.assertThat(code, CoreMatchers.containsString("return 4.0f > g;"));
        Assert.assertThat(code, CoreMatchers.containsString("return 5 < h;"));
        Assert.assertThat(code, CoreMatchers.containsString("return 6.5d < i;"));
    }
}

