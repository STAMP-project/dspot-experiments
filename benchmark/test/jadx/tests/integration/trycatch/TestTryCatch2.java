package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch2 extends IntegrationTest {
    public static class TestCls {
        private static final Object obj = new Object();

        private static boolean test() {
            try {
                synchronized(TestTryCatch2.TestCls.obj) {
                    TestTryCatch2.TestCls.obj.wait(5);
                }
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatch2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("try {"));
        Assert.assertThat(code, CoreMatchers.containsString("synchronized (obj) {"));
        Assert.assertThat(code, CoreMatchers.containsString("obj.wait(5);"));
        Assert.assertThat(code, CoreMatchers.containsString("return true;"));
        Assert.assertThat(code, CoreMatchers.containsString("} catch (InterruptedException e) {"));
        Assert.assertThat(code, CoreMatchers.containsString("return false;"));
    }
}

