package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInline2 extends IntegrationTest {
    public static class TestCls {
        public int test() throws InterruptedException {
            int[] a = new int[]{ 1, 2, 4, 6, 8 };
            int b = 0;
            for (int i = 0; i < (a.length); i += 2) {
                b += a[i];
            }
            for (long i = b; i > 0; i--) {
                b += i;
            }
            return b;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInline2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("int[] a = new int[]{1, 2, 4, 6, 8};"));
        Assert.assertThat(code, JadxMatchers.containsOne("for (int i = 0; i < a.length; i += 2) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("for (long i2 = (long) b; i2 > 0; i2--) {"));
    }
}

