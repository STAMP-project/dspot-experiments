package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions9 extends IntegrationTest {
    public static class TestCls {
        public void test(boolean a, int b) throws Exception {
            if ((!a) || ((b >= 0) && (b <= 11))) {
                System.out.println('1');
            } else {
                System.out.println('2');
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions9.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (!a || (b >= 0 && b <= 11)) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("System.out.println('1');"));
        Assert.assertThat(code, JadxMatchers.containsOne("} else {"));
        Assert.assertThat(code, JadxMatchers.containsOne("System.out.println('2');"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return;")));
    }
}

