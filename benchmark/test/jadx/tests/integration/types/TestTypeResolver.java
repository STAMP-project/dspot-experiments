package jadx.tests.integration.types;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTypeResolver extends IntegrationTest {
    public static class TestCls {
        public TestCls(int b1, int b2) {
            // test 'this' move and constructor invocation on moved register
            this(b1, b2, 0, 0, 0);
        }

        public TestCls(int a1, int a2, int a3, int a4, int a5) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTypeResolver.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("this(b1, b2, 0, 0, 0);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("= this;")));
    }
}

