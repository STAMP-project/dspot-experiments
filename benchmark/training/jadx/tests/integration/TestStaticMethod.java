package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestStaticMethod extends IntegrationTest {
    public static class TestCls {
        static {
            TestStaticMethod.TestCls.f();
        }

        private static void f() {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestStaticMethod.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("static {"));
        Assert.assertThat(code, CoreMatchers.containsString("private static void f() {"));
    }
}

