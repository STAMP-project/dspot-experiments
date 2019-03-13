package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions7 extends IntegrationTest {
    public static class TestCls {
        public void test(int[] a, int i) {
            if ((i >= 0) && (i < (a.length))) {
                (a[i])++;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions7.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("if (i >= 0 && i < a.length) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("||")));
    }
}

