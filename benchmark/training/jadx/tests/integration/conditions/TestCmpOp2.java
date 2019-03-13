package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestCmpOp2 extends IntegrationTest {
    public static class TestCls {
        public boolean testGT(float a, float b) {
            return a > b;
        }

        public boolean testLT(float c, double d) {
            return c < d;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCmpOp2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return a > b;"));
        Assert.assertThat(code, CoreMatchers.containsString("return ((double) c) < d;"));
    }
}

