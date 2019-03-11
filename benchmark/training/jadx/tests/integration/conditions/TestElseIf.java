package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestElseIf extends IntegrationTest {
    public static class TestCls {
        public int testIfElse(String str) {
            int r;
            if (str.equals("a")) {
                r = 1;
            } else
                if (str.equals("b")) {
                    r = 2;
                } else
                    if (str.equals("3")) {
                        r = 3;
                    } else
                        if (str.equals("$")) {
                            r = 4;
                        } else {
                            r = -1;
                        }



            r = r * 10;
            return Math.abs(r);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestElseIf.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("} else if (str.equals(\"b\")) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} else {"));
        Assert.assertThat(code, JadxMatchers.containsOne("int r;"));
        Assert.assertThat(code, JadxMatchers.containsOne("r = 1;"));
        Assert.assertThat(code, JadxMatchers.containsOne("r = -1;"));
        // no ternary operator
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("?")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString(":")));
    }
}

