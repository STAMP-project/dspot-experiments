package jadx.tests.integration.arrays;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrays3 extends IntegrationTest {
    public static class TestCls {
        private Object test(byte[] bArr) {
            return new Object[]{ bArr };
        }

        public void check() {
            Assert.assertThat(test(new byte[]{ 1, 2 }), CoreMatchers.instanceOf(Object[].class));
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestArrays3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return new Object[]{bArr};"));
    }
}

