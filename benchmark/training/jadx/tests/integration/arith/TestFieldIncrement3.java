package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFieldIncrement3 extends IntegrationTest {
    public static class TestCls {
        static int tileX;

        static int tileY;

        static TestFieldIncrement3.TestCls.Vector2 targetPos = new TestFieldIncrement3.TestCls.Vector2();

        static TestFieldIncrement3.TestCls.Vector2 directVect = new TestFieldIncrement3.TestCls.Vector2();

        static TestFieldIncrement3.TestCls.Vector2 newPos = new TestFieldIncrement3.TestCls.Vector2();

        private static void test() {
            Random rd = new Random();
            int direction = rd.nextInt(7);
            switch (direction) {
                case 0 :
                    TestFieldIncrement3.TestCls.targetPos.x = ((float) ((((TestFieldIncrement3.TestCls.tileX) + 1) * 55) + 55));
                    TestFieldIncrement3.TestCls.targetPos.y = ((float) ((((TestFieldIncrement3.TestCls.tileY) + 1) * 35) + 35));
                    break;
                case 2 :
                    TestFieldIncrement3.TestCls.targetPos.x = ((float) ((((TestFieldIncrement3.TestCls.tileX) + 1) * 55) + 55));
                    TestFieldIncrement3.TestCls.targetPos.y = ((float) ((((TestFieldIncrement3.TestCls.tileY) - 1) * 35) + 35));
                    break;
                case 4 :
                    TestFieldIncrement3.TestCls.targetPos.x = ((float) ((((TestFieldIncrement3.TestCls.tileX) - 1) * 55) + 55));
                    TestFieldIncrement3.TestCls.targetPos.y = ((float) ((((TestFieldIncrement3.TestCls.tileY) - 1) * 35) + 35));
                    break;
                case 6 :
                    TestFieldIncrement3.TestCls.targetPos.x = ((float) ((((TestFieldIncrement3.TestCls.tileX) - 1) * 55) + 55));
                    TestFieldIncrement3.TestCls.targetPos.y = ((float) ((((TestFieldIncrement3.TestCls.tileY) + 1) * 35) + 35));
                    break;
                default :
                    break;
            }
            TestFieldIncrement3.TestCls.directVect.x = (TestFieldIncrement3.TestCls.targetPos.x) - (TestFieldIncrement3.TestCls.newPos.x);
            TestFieldIncrement3.TestCls.directVect.y = (TestFieldIncrement3.TestCls.targetPos.y) - (TestFieldIncrement3.TestCls.newPos.y);
            float hPos = ((float) (Math.sqrt(((double) (((TestFieldIncrement3.TestCls.directVect.x) * (TestFieldIncrement3.TestCls.directVect.x)) + ((TestFieldIncrement3.TestCls.directVect.y) * (TestFieldIncrement3.TestCls.directVect.y)))))));
            TestFieldIncrement3.TestCls.directVect.x /= hPos;
            TestFieldIncrement3.TestCls.directVect.y /= hPos;
        }

        static class Vector2 {
            public float x;

            public float y;

            public Vector2() {
                this.x = 0.0F;
                this.y = 0.0F;
            }

            public boolean equals(TestFieldIncrement3.TestCls.Vector2 other) {
                return ((this.x) == (other.x)) && ((this.y) == (other.y));
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFieldIncrement3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("directVect.x = targetPos.x - newPos.x;"));
    }
}

