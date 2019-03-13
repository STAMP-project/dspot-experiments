package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestNestedIf2 extends IntegrationTest {
    public static class TestCls {
        static int executedCount = 0;

        static boolean finished = false;

        static int repeatCount = 2;

        static boolean test(float delta, Object object) {
            if (((TestNestedIf2.TestCls.executedCount) != (TestNestedIf2.TestCls.repeatCount)) && (TestNestedIf2.TestCls.isRun(delta, object))) {
                if (TestNestedIf2.TestCls.finished) {
                    return true;
                }
                if ((TestNestedIf2.TestCls.repeatCount) == (-1)) {
                    ++(TestNestedIf2.TestCls.executedCount);
                    TestNestedIf2.TestCls.action();
                    return false;
                }
                ++(TestNestedIf2.TestCls.executedCount);
                if ((TestNestedIf2.TestCls.executedCount) >= (TestNestedIf2.TestCls.repeatCount)) {
                    return true;
                }
                TestNestedIf2.TestCls.action();
            }
            return false;
        }

        public static void action() {
        }

        public static boolean isRun(float delta, Object object) {
            return delta == 0;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestNestedIf2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (executedCount != repeatCount && isRun(delta, object)) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (finished) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
    }
}

