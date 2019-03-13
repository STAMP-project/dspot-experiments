package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInline extends IntegrationTest {
    public static class TestCls {
        public static void main(String[] args) throws Exception {
            System.out.println(("Test: " + (new TestInline.TestCls().testRun())));
        }

        private boolean testRun() {
            return false;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInline.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("System.out.println(\"Test: \" + new TestInline$TestCls().testRun());"));
    }
}

