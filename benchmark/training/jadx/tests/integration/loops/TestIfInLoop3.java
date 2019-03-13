package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestIfInLoop3 extends IntegrationTest {
    public static class TestCls {
        static boolean[][] occupied = new boolean[70][70];

        static boolean placingStone = true;

        private static boolean test(int xx, int yy) {
            int[] extraArray = new int[]{ 10, 45, 50, 50, 20, 20 };
            if ((extraArray != null) && (TestIfInLoop3.TestCls.placingStone)) {
                for (int i = 0; i < (extraArray.length); i += 2) {
                    int tX;
                    int tY;
                    if ((yy % 2) == 0) {
                        if (((extraArray[(i + 1)]) % 2) == 0) {
                            tX = xx + (extraArray[i]);
                        } else {
                            tX = ((extraArray[i]) + xx) - 1;
                        }
                        tY = yy + (extraArray[(i + 1)]);
                    } else {
                        tX = xx + (extraArray[i]);
                        tY = yy + (extraArray[(i + 1)]);
                    }
                    if (((((tX < 0) || (tY < 0)) || (((tY % 2) != 0) && (tX > 28))) || (tY > 70)) || (TestIfInLoop3.TestCls.occupied[tX][tY])) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void check() {
            Assert.assertTrue(TestIfInLoop3.TestCls.test(14, 2));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIfInLoop3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (int i = 0; i < extraArray.length; i += 2) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (extraArray != null && placingStone) {"));
    }
}

