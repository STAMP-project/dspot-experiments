package jadx.tests.integration.types;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTypeResolver3 extends IntegrationTest {
    public static class TestCls {
        public int test(String s1, String s2) {
            int cmp = s2.compareTo(s1);
            if (cmp != 0) {
                return cmp;
            }
            return (s1.length()) == (s2.length()) ? 0 : (s1.length()) < (s2.length()) ? -1 : 1;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTypeResolver3.TestCls.class);
        String code = cls.getCode().toString();
        // TODO inline into return
        Assert.assertThat(code, JadxMatchers.containsOne("s1.length() == s2.length() ? 0 : s1.length() < s2.length() ? -1 : 1;"));
    }

    @Test
    public void test2() {
        noDebugInfo();
        getClassNode(TestTypeResolver3.TestCls.class);
    }
}

