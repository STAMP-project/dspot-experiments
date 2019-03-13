package jadx.tests.integration.invoke;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSuperInvoke extends IntegrationTest {
    public class A {
        public int a() {
            return 1;
        }
    }

    public class B extends TestSuperInvoke.A {
        @Override
        public int a() {
            return (super.a()) + 2;
        }

        public int test() {
            return a();
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestSuperInvoke.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(2, "return super.a() + 2;"));
    }
}

