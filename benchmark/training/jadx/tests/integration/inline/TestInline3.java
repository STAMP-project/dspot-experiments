package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInline3 extends IntegrationTest {
    public static class TestCls {
        public TestCls(int b1, int b2) {
            this(b1, b2, 0, 0, 0);
        }

        public TestCls(int a1, int a2, int a3, int a4, int a5) {
        }

        public class A extends TestInline3.TestCls {
            public A(int a) {
                super(a, a);
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInline3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("this(b1, b2, 0, 0, 0);"));
        Assert.assertThat(code, CoreMatchers.containsString("super(a, a);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("super(a, a).this$0")));
        Assert.assertThat(code, CoreMatchers.containsString("public class A extends TestInline3$TestCls {"));
    }
}

