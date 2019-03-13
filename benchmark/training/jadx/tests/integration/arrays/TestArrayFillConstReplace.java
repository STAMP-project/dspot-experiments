package jadx.tests.integration.arrays;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrayFillConstReplace extends IntegrationTest {
    public static class TestCls {
        public static final int CONST_INT = 65535;

        public int[] test() {
            return new int[]{ 127, 129, TestArrayFillConstReplace.TestCls.CONST_INT };
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestArrayFillConstReplace.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return new int[]{127, 129, CONST_INT};"));
    }
}

