package jadx.tests.integration.variables;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestVariables2 extends IntegrationTest {
    public static class TestCls {
        Object test(Object s) {
            Object store = (s != null) ? s : null;
            if (store == null) {
                store = new Object();
                s = store;
            }
            return store;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestVariables2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("Object store = s != null ? s : null;"));
    }
}

