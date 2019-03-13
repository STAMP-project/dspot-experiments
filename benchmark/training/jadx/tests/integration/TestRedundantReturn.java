package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestRedundantReturn extends IntegrationTest {
    public static class TestCls {
        public void test(int num) {
            if (num == 4) {
                Assert.fail();
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestRedundantReturn.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return;")));
    }
}

