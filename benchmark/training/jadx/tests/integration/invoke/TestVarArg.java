package jadx.tests.integration.invoke;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestVarArg extends IntegrationTest {
    public static class TestCls {
        void test1(int... a) {
        }

        void test2(int i, Object... a) {
        }

        void test3(int[] a) {
        }

        void call() {
            test1(1, 2);
            test2(3, "1", 7);
            test3(new int[]{ 5, 8 });
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestVarArg.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("void test1(int... a) {"));
        Assert.assertThat(code, CoreMatchers.containsString("void test2(int i, Object... a) {"));
        Assert.assertThat(code, CoreMatchers.containsString("test1(1, 2);"));
        Assert.assertThat(code, CoreMatchers.containsString("test2(3, \"1\", Integer.valueOf(7));"));
        // negative case
        Assert.assertThat(code, CoreMatchers.containsString("void test3(int[] a) {"));
        Assert.assertThat(code, CoreMatchers.containsString("test3(new int[]{5, 8});"));
    }
}

