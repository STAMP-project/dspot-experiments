package jadx.tests.integration.arrays;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrays2 extends IntegrationTest {
    public static class TestCls {
        private static Object test4(int type) {
            if (type == 1) {
                return new int[]{ 1, 2 };
            } else
                if (type == 2) {
                    return new float[]{ 1, 2 };
                } else
                    if (type == 3) {
                        return new short[]{ 1, 2 };
                    } else
                        if (type == 4) {
                            return new byte[]{ 1, 2 };
                        } else {
                            return null;
                        }



        }

        public void check() {
            Assert.assertThat(TestArrays2.TestCls.test4(4), CoreMatchers.instanceOf(byte[].class));
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestArrays2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("new int[]{1, 2}"));
    }
}

