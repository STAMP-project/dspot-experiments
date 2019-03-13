package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions14 extends IntegrationTest {
    public static class TestCls {
        public static boolean test(Object a, Object b) {
            boolean r = (a == null) ? b != null : !(a.equals(b));
            if (r) {
                return false;
            }
            System.out.println("1");
            return true;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions14.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("boolean r = a == null ? b != null : !a.equals(b);"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (r) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("System.out.println(\"1\");"));
    }
}

