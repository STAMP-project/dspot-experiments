package jadx.tests.integration.arrays;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrays extends IntegrationTest {
    public static class TestCls {
        public int test1(int i) {
            int[] a = new int[]{ 1, 2, 3, 5 };
            return a[i];
        }

        public int test2(int i) {
            int[][] a = new int[i][i + 1];
            return a.length;
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestArrays.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return new int[]{1, 2, 3, 5}[i];"));
    }
}

