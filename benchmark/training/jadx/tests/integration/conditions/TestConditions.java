package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions extends IntegrationTest {
    public static class TestCls {
        private boolean test(boolean a, boolean b, boolean c) {
            return (a && b) || c;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("(!a || !b) && !c")));
        Assert.assertThat(code, CoreMatchers.containsString("return (a && b) || c;"));
    }
}

