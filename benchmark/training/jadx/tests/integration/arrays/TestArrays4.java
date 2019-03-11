package jadx.tests.integration.arrays;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrays4 extends SmaliTest {
    public static class TestCls {
        char[] payload;

        public TestCls(byte[] bytes) {
            char[] a = TestArrays4.TestCls.toChars(bytes);
            this.payload = new char[a.length];
            System.arraycopy(a, 0, this.payload, 0, bytes.length);
        }

        private static char[] toChars(byte[] bArr) {
            return new char[bArr.length];
        }
    }

    @Test
    public void testArrayTypeInference() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestArrays4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("char[] toChars = toChars(bArr);"));
    }
}

