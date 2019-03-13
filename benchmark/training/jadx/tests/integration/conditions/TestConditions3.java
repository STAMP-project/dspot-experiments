package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.util.List;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions3 extends IntegrationTest {
    public static class TestCls {
        private static final Pattern PATTERN = Pattern.compile("[a-f0-9]{20}");

        public static Object test(final TestConditions3.TestCls.A a) {
            List<String> list = a.getList();
            if (list == null) {
                return null;
            }
            if ((list.size()) != 1) {
                return null;
            }
            String s = list.get(0);
            if (TestConditions3.TestCls.isEmpty(s)) {
                return null;
            }
            if (TestConditions3.TestCls.isDigitsOnly(s)) {
                return new TestConditions3.TestCls.A().set(s);
            }
            if (TestConditions3.TestCls.PATTERN.matcher(s).matches()) {
                return new TestConditions3.TestCls.A().set(s);
            }
            return null;
        }

        private static boolean isDigitsOnly(String s) {
            return false;
        }

        private static boolean isEmpty(String s) {
            return false;
        }

        private static class A {
            public Object set(String s) {
                return null;
            }

            public List<String> getList() {
                return null;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return null;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
        // TODO: fix constant inline
        // assertThat(code, not(containsString("AnonymousClass_1")));
    }
}

