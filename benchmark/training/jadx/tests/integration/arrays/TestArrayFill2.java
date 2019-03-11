package jadx.tests.integration.arrays;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrayFill2 extends IntegrationTest {
    // TODO
    // public int[] test2(int a) {
    // return new int[]{1, a++, a * 2};
    // }
    public static class TestCls {
        public int[] test(int a) {
            return new int[]{ 1, a + 1, 2 };
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestArrayFill2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return new int[]{1, a + 1, 2};"));
        // TODO
        // assertThat(code, containsString("return new int[]{1, a++, a * 2};"));
    }
}

