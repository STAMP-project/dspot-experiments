package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInnerClass4 extends IntegrationTest {
    public static class TestCls {
        public class C {
            public String c;

            private C() {
                this.c = "c";
            }
        }

        private String test() {
            return new TestInnerClass4.TestCls.C().c;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInnerClass4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return new C().c;"));
    }
}

