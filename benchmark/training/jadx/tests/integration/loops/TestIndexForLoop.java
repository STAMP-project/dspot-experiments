package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestIndexForLoop extends IntegrationTest {
    public static class TestCls {
        private int test(int[] a, int b) {
            int sum = 0;
            for (int i = 0; i < b; i++) {
                sum += a[i];
            }
            return sum;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIndexForLoop.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(2, "int sum = 0;", "for (int i = 0; i < b; i++) {", ((TestUtils.indent(1)) + "sum += a[i];"), "}", "return sum;"));
    }
}

