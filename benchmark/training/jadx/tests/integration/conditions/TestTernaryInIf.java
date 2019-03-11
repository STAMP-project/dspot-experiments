package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTernaryInIf extends IntegrationTest {
    public static class TestCls {
        public boolean test1(boolean a, boolean b, boolean c) {
            return a ? b : c;
        }

        public int test2(boolean a, boolean b, boolean c) {
            return a ? b : c ? 1 : 2;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTernaryInIf.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return a ? b : c;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return (a ? b : c) ? 1 : 2;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("if")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
    }
}

