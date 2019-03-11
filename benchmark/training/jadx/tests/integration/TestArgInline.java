package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArgInline extends IntegrationTest {
    public static class TestCls {
        public void test(int a) {
            while (a < 10) {
                int b = a + 1;
                a = b;
            } 
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestArgInline.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("i++;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("i = i + 1;")));
    }
}

