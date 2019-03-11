package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopCondition3 extends IntegrationTest {
    public static class TestCls {
        public static void test(int a, int b, int c) {
            while (a < 12) {
                if (((b + a) < 9) && (b < 8)) {
                    if (((b >= 2) && (a > (-1))) && (b < 6)) {
                        System.out.println("OK");
                        c = b + 1;
                    }
                    b = a;
                }
                c = b;
                b++;
                b = c;
                a++;
            } 
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopCondition3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("while (a < 12) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (b + a < 9 && b < 8) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (b >= 2 && a > -1 && b < 6) {"));
    }
}

