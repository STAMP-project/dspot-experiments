package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopInTry extends IntegrationTest {
    public static class TestCls {
        private static boolean b = true;

        public int test() {
            try {
                if (TestLoopInTry.TestCls.b) {
                    throw new Exception();
                }
                while (TestLoopInTry.TestCls.f()) {
                    TestLoopInTry.TestCls.s();
                } 
            } catch (Exception e) {
                System.out.println("exception");
                return 1;
            }
            return 0;
        }

        private static void s() {
        }

        private static boolean f() {
            return false;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopInTry.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (b) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("throw new Exception();"));
        Assert.assertThat(code, JadxMatchers.containsOne("while (f()) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("s();"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("return 1;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return 0;"));
    }
}

