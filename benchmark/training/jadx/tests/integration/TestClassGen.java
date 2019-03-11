package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestClassGen extends IntegrationTest {
    public static class TestCls {
        public interface I {
            int test();

            public int test3();
        }

        public abstract static class A {
            public abstract int test2();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestClassGen.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("public interface I {"));
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(2)) + "int test();")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("public int test();")));
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(2)) + "int test3();")));
        Assert.assertThat(code, CoreMatchers.containsString("public static abstract class A {"));
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(2)) + "public abstract int test2();")));
    }
}

