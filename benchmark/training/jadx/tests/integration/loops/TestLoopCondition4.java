package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopCondition4 extends IntegrationTest {
    public static class TestCls {
        public static void test() {
            int n = -1;
            while (n < 0) {
                n += 12;
            } 
            while (n > 11) {
                n -= 12;
            } 
            System.out.println(n);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopCondition4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("int n = -1;"));
        Assert.assertThat(code, JadxMatchers.containsOne("while (n < 0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("n += 12;"));
        Assert.assertThat(code, JadxMatchers.containsOne("while (n > 11) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("n -= 12;"));
        Assert.assertThat(code, JadxMatchers.containsOne("System.out.println(n);"));
    }
}

