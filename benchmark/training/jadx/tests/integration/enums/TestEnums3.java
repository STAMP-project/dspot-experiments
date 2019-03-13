package jadx.tests.integration.enums;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestEnums3 extends IntegrationTest {
    public static class TestCls {
        private static int three = 3;

        public enum Numbers {

            ONE(1),
            TWO(2),
            THREE(TestEnums3.TestCls.three),
            FOUR(((TestEnums3.TestCls.three) + 1));
            private final int num;

            private Numbers(int n) {
                this.num = n;
            }

            public int getNum() {
                return num;
            }
        }

        public void check() {
            Assert.assertTrue(((TestEnums3.TestCls.Numbers.ONE.getNum()) == 1));
            Assert.assertTrue(((TestEnums3.TestCls.Numbers.THREE.getNum()) == 3));
            Assert.assertTrue(((TestEnums3.TestCls.Numbers.FOUR.getNum()) == 4));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestEnums3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("ONE(1)"));
        // assertThat(code, containsOne("THREE(three)"));
        // assertThat(code, containsOne("assertTrue(Numbers.ONE.getNum() == 1);"));
        Assert.assertThat(code, JadxMatchers.containsOne("private Numbers(int n) {"));
    }
}

