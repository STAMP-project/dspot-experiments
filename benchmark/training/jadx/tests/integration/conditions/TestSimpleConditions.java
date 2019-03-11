package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSimpleConditions extends IntegrationTest {
    public static class TestCls {
        public boolean test1(boolean[] a) {
            return (((a[0]) && (a[1])) && (a[2])) || ((a[3]) && (a[4]));
        }

        public boolean test2(boolean[] a) {
            return (((a[0]) || (a[1])) || (a[2])) || (a[3]);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSimpleConditions.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return (a[0] && a[1] && a[2]) || (a[3] && a[4]);"));
        Assert.assertThat(code, CoreMatchers.containsString("return a[0] || a[1] || a[2] || a[3];"));
    }
}

