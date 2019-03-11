package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestBreakInComplexIf extends IntegrationTest {
    public static class TestCls {
        private int test(Map<String, TestBreakInComplexIf.TestCls.Point> map, int mapX) {
            int length = 1;
            for (int x = mapX + 1; x < 100; x++) {
                TestBreakInComplexIf.TestCls.Point tile = map.get((x + ""));
                if ((tile == null) || ((tile.y) != 100)) {
                    break;
                }
                length++;
            }
            return length;
        }

        class Point {
            public final int x;

            public final int y;

            Point(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        public void check() {
            Map<String, TestBreakInComplexIf.TestCls.Point> map = new HashMap<>();
            map.put("3", new TestBreakInComplexIf.TestCls.Point(100, 100));
            map.put("4", new TestBreakInComplexIf.TestCls.Point(60, 100));
            Assert.assertThat(test(map, 2), Matchers.is(3));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestBreakInComplexIf.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (tile == null || tile.y != 100) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("break;"));
    }

    @Test
    public void testNoDebug() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestBreakInComplexIf.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("break;"));
    }
}

