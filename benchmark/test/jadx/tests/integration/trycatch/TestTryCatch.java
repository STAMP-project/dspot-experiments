package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch extends IntegrationTest {
    public static class TestCls {
        private void f() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("try {"));
        Assert.assertThat(code, CoreMatchers.containsString("Thread.sleep(50);"));
        Assert.assertThat(code, CoreMatchers.containsString("} catch (InterruptedException e) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return")));
    }
}

