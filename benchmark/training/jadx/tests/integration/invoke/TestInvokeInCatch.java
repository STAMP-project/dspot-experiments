package jadx.tests.integration.invoke;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInvokeInCatch extends IntegrationTest {
    public static class TestCls {
        private static final String TAG = "TAG";

        private void test(int[] a, int b) {
            try {
                exc();
            } catch (IOException e) {
                if (b == 1) {
                    TestInvokeInCatch.TestCls.log(TestInvokeInCatch.TestCls.TAG, "Error: {}", e.getMessage());
                }
            }
        }

        private static void log(String tag, String str, String... args) {
        }

        private void exc() throws IOException {
            throw new IOException();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInvokeInCatch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("exc();"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return;")));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (IOException e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (b == 1) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("log(TAG, \"Error: {}\", e.getMessage());"));
    }
}

