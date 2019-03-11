package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


public class TestBreakWithLabel extends IntegrationTest {
    public static class TestCls {
        public boolean test(int[][] arr, int b) {
            boolean found = false;
            loop0 : for (int i = 0; i < (arr.length); i++) {
                for (int j = 0; j < (arr[i].length); j++) {
                    if ((arr[i][j]) == b) {
                        found = true;
                        break loop0;
                    }
                }
            }
            System.out.println(("found: " + found));
            return found;
        }
    }

    @Test
    public void test() throws Exception {
        ClassNode cls = getClassNode(TestBreakWithLabel.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("loop0:"));
        Assert.assertThat(code, JadxMatchers.containsOne("break loop0;"));
        Method test = getReflectMethod("test", int[][].class, int.class);
        int[][] testArray = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 } };
        Assert.assertTrue(((Boolean) (invoke(test, testArray, 3))));
        Assert.assertFalse(((Boolean) (invoke(test, testArray, 5))));
    }
}

