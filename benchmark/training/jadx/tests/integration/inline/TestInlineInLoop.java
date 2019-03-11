package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInlineInLoop extends IntegrationTest {
    public static class TestCls {
        public static void main(String[] args) throws Exception {
            int a = 0;
            int b = 4;
            int c = 0;
            while (a < 12) {
                if (((b + a) < 9) && (b < 8)) {
                    if (((b >= 2) && (a > (-1))) && (b < 6)) {
                        System.out.println("OK");
                        c = b + 1;
                    }
                    c = b;
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
        ClassNode cls = getClassNode(TestInlineInLoop.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("int c"));
        Assert.assertThat(code, JadxMatchers.containsOne("c = b + 1"));
        Assert.assertThat(code, JadxMatchers.countString(2, "c = b;"));
        Assert.assertThat(code, JadxMatchers.containsOne("b++;"));
        Assert.assertThat(code, JadxMatchers.containsOne("b = c"));
        Assert.assertThat(code, JadxMatchers.containsOne("a++;"));
    }
}

