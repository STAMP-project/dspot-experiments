package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions4 extends IntegrationTest {
    public static class TestCls {
        public int test(int num) {
            boolean inRange = (num >= 59) && (num <= 66);
            return inRange ? num + 1 : num;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("num >= 59 && num <= 66"));
        Assert.assertThat(code, CoreMatchers.containsString("return inRange ? num + 1 : num;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
    }
}

