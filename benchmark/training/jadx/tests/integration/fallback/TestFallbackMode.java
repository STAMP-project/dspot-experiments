package jadx.tests.integration.fallback;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFallbackMode extends IntegrationTest {
    public static class TestCls {
        public int test(int a) {
            while (a < 10) {
                a++;
            } 
            return a;
        }
    }

    @Test
    public void test() {
        setFallback();
        disableCompilation();
        ClassNode cls = getClassNode(TestFallbackMode.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("public int test(int r2) {"));
        Assert.assertThat(code, CoreMatchers.containsString("r1 = this;"));
        Assert.assertThat(code, CoreMatchers.containsString("L_0x0004:"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("throw new UnsupportedOperationException")));
    }
}

