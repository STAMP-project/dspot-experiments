package jadx.tests.integration.synchronize;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSynchronized2 extends IntegrationTest {
    public static class TestCls {
        private static synchronized boolean test(Object obj) {
            return (obj.toString()) != null;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSynchronized2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("private static synchronized boolean test(Object obj) {"));
        Assert.assertThat(code, CoreMatchers.containsString("obj.toString() != null;"));
        // TODO
        // assertThat(code, containsString("return obj.toString() != null;"));
        // assertThat(code, not(containsString("synchronized (")));
    }
}

