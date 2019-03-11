package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopDetection3 extends IntegrationTest {
    public static class TestCls {
        private void test(TestLoopDetection3.TestCls parent, int pos) {
            Object item;
            while ((--pos) >= 0) {
                item = parent.get(pos);
                if (item instanceof String) {
                    func(((String) (item)));
                    return;
                }
            } 
        }

        private Object get(int pos) {
            return null;
        }

        private void func(String item) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopDetection3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("while"));
        // TODO
        // assertThat(code, containsString("while (--pos >= 0) {"));
    }
}

