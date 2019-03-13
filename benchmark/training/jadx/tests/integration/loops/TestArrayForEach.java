package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestArrayForEach extends IntegrationTest {
    public static class TestCls {
        private int test(int[] a) {
            int sum = 0;
            for (int n : a) {
                sum += n;
            }
            return sum;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestArrayForEach.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(2, "int sum = 0;", "for (int n : a) {", ((TestUtils.indent(1)) + "sum += n;"), "}", "return sum;"));
    }
}

