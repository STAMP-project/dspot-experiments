package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFieldIncrement extends IntegrationTest {
    public static class TestCls {
        public int instanceField = 1;

        public static int staticField = 1;

        public static String result = "";

        public void method() {
            (instanceField)++;
        }

        public void method2() {
            (TestFieldIncrement.TestCls.staticField)--;
        }

        public void method3(String s) {
            TestFieldIncrement.TestCls.result += s + '_';
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFieldIncrement.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("instanceField++;"));
        Assert.assertThat(code, CoreMatchers.containsString("staticField--;"));
        Assert.assertThat(code, CoreMatchers.containsString("result += s + '_';"));
    }
}

