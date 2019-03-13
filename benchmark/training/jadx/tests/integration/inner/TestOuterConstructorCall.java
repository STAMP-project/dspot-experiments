package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestOuterConstructorCall extends IntegrationTest {
    public static class TestCls {
        private TestCls(TestOuterConstructorCall.TestCls.Inner inner) {
            System.out.println(inner);
        }

        private class Inner {
            private TestOuterConstructorCall.TestCls test() {
                return new TestOuterConstructorCall.TestCls(this);
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestOuterConstructorCall.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, Matchers.containsString("private class Inner {"));
        Assert.assertThat(code, Matchers.containsString("return new TestOuterConstructorCall$TestCls(this);"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

