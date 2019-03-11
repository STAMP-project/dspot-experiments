package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions11 extends IntegrationTest {
    public static class TestCls {
        public void test(boolean a, int b) {
            if (a || (b > 2)) {
                f();
            }
        }

        private void f() {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions11.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (a || b > 2) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("f();"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
    }
}

