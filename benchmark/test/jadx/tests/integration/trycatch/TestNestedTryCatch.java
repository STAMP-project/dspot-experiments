package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestNestedTryCatch extends IntegrationTest {
    public static class TestCls {
        private void f() {
            try {
                Thread.sleep(1);
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                }
            } catch (Exception e) {
            }
            return;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestNestedTryCatch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("try {"));
        Assert.assertThat(code, CoreMatchers.containsString("Thread.sleep(1);"));
        Assert.assertThat(code, CoreMatchers.containsString("Thread.sleep(2);"));
        Assert.assertThat(code, CoreMatchers.containsString("} catch (InterruptedException e) {"));
        Assert.assertThat(code, CoreMatchers.containsString("} catch (Exception e2) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return")));
    }
}

