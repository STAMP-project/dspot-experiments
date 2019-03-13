package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSequentialLoops extends IntegrationTest {
    public static class TestCls {
        public int test(int a, int b) {
            int c = b;
            int z;
            while (true) {
                z = c + a;
                if (z >= 7) {
                    break;
                }
                c = z;
            } 
            while ((z = c + a) >= 7) {
                c = z;
            } 
            return c;
        }
    }

    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNode(TestSequentialLoops.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(2, "while ("));
        Assert.assertThat(code, JadxMatchers.containsOne("break;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return c;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
    }
}

