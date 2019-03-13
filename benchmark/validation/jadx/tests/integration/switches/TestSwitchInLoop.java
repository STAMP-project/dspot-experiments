package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchInLoop extends IntegrationTest {
    public static class TestCls {
        public int test(int k) {
            int a = 0;
            while (true) {
                switch (k) {
                    case 0 :
                        return a;
                    default :
                        a++;
                        k >>= 1;
                }
            } 
        }

        public void check() {
            Assert.assertEquals(1, test(1));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchInLoop.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("switch (k) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("case 0:"));
        Assert.assertThat(code, JadxMatchers.containsOne("return a;"));
        Assert.assertThat(code, JadxMatchers.containsOne("default:"));
        Assert.assertThat(code, JadxMatchers.containsOne("a++;"));
        Assert.assertThat(code, JadxMatchers.containsOne("k >>= 1;"));
    }
}

