package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopDetection extends IntegrationTest {
    public static class TestCls {
        private void test(int[] a, int b) {
            int i = 0;
            while ((i < (a.length)) && (i < b)) {
                (a[i])++;
                i++;
            } 
            while (i < (a.length)) {
                (a[i])--;
                i++;
            } 
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopDetection.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("while (i < a.length && i < b) {"));
        Assert.assertThat(code, CoreMatchers.containsString("while (i < a.length) {"));
        Assert.assertThat(code, CoreMatchers.containsString("int i = 0;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("i_2")));
        Assert.assertThat(code, CoreMatchers.containsString("i++;"));
    }
}

