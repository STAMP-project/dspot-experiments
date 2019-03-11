package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopDetection2 extends IntegrationTest {
    public static class TestCls {
        public int test(int a, int b) {
            int c = a + b;
            for (int i = a; i < b; i++) {
                if (i == 7) {
                    c += 2;
                } else {
                    c *= 2;
                }
            }
            c--;
            return c;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopDetection2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("int c = a + b;"));
        Assert.assertThat(code, JadxMatchers.containsOne("for (int i = a; i < b; i++) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("c_2")));
    }
}

