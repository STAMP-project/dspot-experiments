package jadx.tests.integration.annotations;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnnotations extends IntegrationTest {
    public static class TestCls {
        private static @interface A {
            int a();
        }

        @TestAnnotations.TestCls.A(a = -1)
        public void methodA1() {
        }

        @TestAnnotations.TestCls.A(a = -253)
        public void methodA2() {
        }

        @TestAnnotations.TestCls.A(a = -11253)
        public void methodA3() {
        }

        private static @interface V {
            boolean value();
        }

        @TestAnnotations.TestCls.V(false)
        public void methodV() {
        }

        private static @interface D {
            float value() default 1.1F;
        }

        @TestAnnotations.TestCls.D
        public void methodD() {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnnotations.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("@A(a = 255)")));
        Assert.assertThat(code, JadxMatchers.containsOne("@A(a = -1)"));
        Assert.assertThat(code, JadxMatchers.containsOne("@A(a = -253)"));
        Assert.assertThat(code, JadxMatchers.containsOne("@A(a = -11253)"));
        Assert.assertThat(code, JadxMatchers.containsOne("@V(false)"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("@D()")));
        Assert.assertThat(code, JadxMatchers.containsOne("@D"));
        Assert.assertThat(code, JadxMatchers.containsOne("int a();"));
        Assert.assertThat(code, JadxMatchers.containsOne("float value() default 1.1f;"));
    }
}

