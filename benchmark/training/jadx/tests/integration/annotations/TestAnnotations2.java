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


public class TestAnnotations2 extends IntegrationTest {
    public static class TestCls {
        @Target({ ElementType.TYPE })
        @Retention(RetentionPolicy.RUNTIME)
        public @interface A {
            int i();

            float f();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnnotations2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("@Target({ElementType.TYPE})"));
        Assert.assertThat(code, CoreMatchers.containsString("@Retention(RetentionPolicy.RUNTIME)"));
        Assert.assertThat(code, CoreMatchers.containsString("public @interface A {"));
        Assert.assertThat(code, CoreMatchers.containsString("float f();"));
        Assert.assertThat(code, CoreMatchers.containsString("int i();"));
    }
}

