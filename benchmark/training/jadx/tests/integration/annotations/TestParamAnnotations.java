package jadx.tests.integration.annotations;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestParamAnnotations extends IntegrationTest {
    public static class TestCls {
        @Target({ ElementType.PARAMETER })
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface A {
            int i() default 7;
        }

        void test1(@TestParamAnnotations.TestCls.A
        int i) {
        }

        void test2(int i, @TestParamAnnotations.TestCls.A
        int j) {
        }

        void test3(@TestParamAnnotations.TestCls.A(i = 5)
        int i) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestParamAnnotations.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("void test1(@A int i) {"));
        Assert.assertThat(code, CoreMatchers.containsString("void test2(int i, @A int j) {"));
        Assert.assertThat(code, CoreMatchers.containsString("void test3(@A(i = 5) int i) {"));
    }
}

