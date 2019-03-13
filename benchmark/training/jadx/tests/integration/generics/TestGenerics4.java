package jadx.tests.integration.generics;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestGenerics4 extends IntegrationTest {
    public static class TestCls {
        public static Class<?> method(int i) {
            Class<?>[] a = new Class<?>[0];
            return a[((a.length) - i)];
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestGenerics4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("Class<?>[] a ="));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("Class[] a =")));
    }
}

