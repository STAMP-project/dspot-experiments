package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestIfTryInCatch extends IntegrationTest {
    public static class TestCls {
        private Exception exception;

        private Object data;

        public Object test(final Object obj) {
            exception = null;
            try {
                return f();
            } catch (Exception e) {
                if ((TestIfTryInCatch.TestCls.a(e)) && (TestIfTryInCatch.TestCls.b(obj))) {
                    try {
                        return f();
                    } catch (Exception e2) {
                        e = e2;
                    }
                }
                System.out.println(("Exception" + e));
                exception = e;
                return data;
            }
        }

        private static boolean b(Object obj) {
            return obj == null;
        }

        private static boolean a(Exception e) {
            return e instanceof RuntimeException;
        }

        private Object f() {
            return null;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIfTryInCatch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(2, "try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if ("));
        Assert.assertThat(code, JadxMatchers.countString(2, "return f();"));
    }
}

