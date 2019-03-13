package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInnerClass extends IntegrationTest {
    public static class TestCls {
        public class Inner {
            public class Inner2 extends Thread {}
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInnerClass.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("Inner {"));
        Assert.assertThat(code, CoreMatchers.containsString("Inner2 extends Thread {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("super();")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("this$")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("/* synthetic */")));
    }
}

