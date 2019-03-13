package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInnerClass3 extends IntegrationTest {
    public static class TestCls {
        private String c;

        private void setC(String c) {
            this.c = c;
        }

        public class C {
            public String c() {
                setC("c");
                return c;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInnerClass3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("synthetic")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("access$")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("x0")));
        Assert.assertThat(code, CoreMatchers.containsString("setC(\"c\");"));
    }
}

