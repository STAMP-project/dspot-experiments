package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSyntheticInline extends IntegrationTest {
    public static class TestCls {
        private int f;

        private int func() {
            return -1;
        }

        public class A {
            public int getF() {
                return f;
            }

            public void setF(int v) {
                f = v;
            }

            public int callFunc() {
                return func();
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSyntheticInline.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("synthetic")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("access$")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("x0")));
        Assert.assertThat(code, CoreMatchers.containsString("f = v;"));
        // assertThat(code, containsString("return f;"));
        // assertThat(code, containsString("return func();"));
        // Temporary solution
        Assert.assertThat(code, CoreMatchers.containsString("return TestSyntheticInline$TestCls.this.f;"));
        Assert.assertThat(code, CoreMatchers.containsString("return TestSyntheticInline$TestCls.this.func();"));
    }
}

