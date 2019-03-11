package jadx.tests.integration.variables;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestVariables3 extends IntegrationTest {
    public static class TestCls {
        String test(Object s) {
            int i;
            if (s == null) {
                i = 2;
            } else {
                i = 3;
                s = null;
            }
            return (s + " ") + i;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestVariables3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("int i;"));
        Assert.assertThat(code, CoreMatchers.containsString("i = 2;"));
        Assert.assertThat(code, CoreMatchers.containsString("i = 3;"));
        Assert.assertThat(code, CoreMatchers.containsString("s = null;"));
        Assert.assertThat(code, CoreMatchers.containsString("return s + \" \" + i;"));
    }
}

