package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestWrongCode extends IntegrationTest {
    public static class TestCls {
        private int test() {
            int[] a = null;
            return a.length;
        }

        private int test2(int a) {
            if (a == 0) {
            }
            return a;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestWrongCode.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return false.length;")));
        Assert.assertThat(code, JadxMatchers.containsOne("int[] a = null;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return a.length;"));
        Assert.assertThat(code, JadxMatchers.containsLines(2, "if (a == 0) {", "}", "return a;"));
    }

    @Test
    public void testNoDebug() {
        noDebugInfo();
        getClassNode(TestWrongCode.TestCls.class);
    }
}

