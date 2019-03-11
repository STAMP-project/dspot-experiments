package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrayForEachNegative extends IntegrationTest {
    public static class TestCls {
        private int test(int[] a, int[] b) {
            int sum = 0;
            for (int i = 0; i < (a.length); i += 2) {
                sum += a[i];
            }
            for (int i = 1; i < (a.length); i++) {
                sum += a[i];
            }
            for (int i = 0; i < (a.length); i--) {
                sum += a[i];
            }
            for (int i = 0; i <= (a.length); i++) {
                sum += a[i];
            }
            for (int i = 0; (i + 1) < (a.length); i++) {
                sum += a[i];
            }
            for (int i = 0; i < (a.length); i++) {
                sum += a[(i - 1)];
            }
            for (int i = 0; i < (b.length); i++) {
                sum += a[i];
            }
            int j = 0;
            for (int i = 0; i < (a.length); j++) {
                sum += a[j];
            }
            return sum;
        }
    }

    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNode(TestArrayForEachNegative.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString(":")));
    }
}

