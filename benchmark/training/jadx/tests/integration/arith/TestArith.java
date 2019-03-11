package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArith extends IntegrationTest {
    public static class TestCls {
        public void method(int a) {
            a += 2;
        }

        public void method2(int a) {
            a++;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestArith.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("a += 2;"));
        Assert.assertThat(code, CoreMatchers.containsString("a++;"));
    }
}

