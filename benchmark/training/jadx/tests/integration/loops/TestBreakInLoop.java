package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestBreakInLoop extends IntegrationTest {
    public static class TestCls {
        private int f;

        private void test(int[] a, int b) {
            for (int i = 0; i < (a.length); i++) {
                (a[i])++;
                if (i < b) {
                    break;
                }
            }
            (this.f)++;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestBreakInLoop.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (int i = 0; i < a.length; i++) {"));
        // assertThat(code, containsOne("a[i]++;"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (i < b) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("break;"));
        Assert.assertThat(code, JadxMatchers.containsOne("this.f++;"));
        Assert.assertThat(code, JadxMatchers.countString(0, "else"));
    }
}

