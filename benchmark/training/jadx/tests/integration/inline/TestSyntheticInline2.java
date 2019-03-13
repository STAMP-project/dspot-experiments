package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSyntheticInline2 extends IntegrationTest {
    public static class Base {
        protected void call() {
            System.out.println("base call");
        }
    }

    public static class TestCls extends TestSyntheticInline2.Base {
        public class A {
            public void invokeCall() {
                TestSyntheticInline2.TestCls.this.call();
            }

            public void invokeSuperCall() {
                TestSyntheticInline2.TestCls.super.call();
            }
        }

        @Override
        public void call() {
            System.out.println("TestCls call");
        }

        public void check() {
            TestSyntheticInline2.TestCls.A a = new TestSyntheticInline2.TestCls.A();
            a.invokeSuperCall();
            a.invokeCall();
        }
    }

    @Test
    public void test() {
        disableCompilation();// strange java compiler bug

        ClassNode cls = getClassNode(TestSyntheticInline2.TestCls.class);// Base class in unknown

        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("synthetic")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("access$")));
        Assert.assertThat(code, CoreMatchers.containsString("TestSyntheticInline2$TestCls.this.call();"));
        Assert.assertThat(code, CoreMatchers.containsString("TestSyntheticInline2$TestCls.super.call();"));
    }

    @Test
    public void testTopClass() {
        ClassNode cls = getClassNode(TestSyntheticInline2.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(1)) + "TestCls.super.call();")));
    }
}

