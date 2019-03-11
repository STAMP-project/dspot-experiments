package jadx.tests.integration.arrays;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArrayFill extends IntegrationTest {
    public static class TestCls {
        public String[] method() {
            return new String[]{ "1", "2", "3" };
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestArrayFill.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return new String[]{\"1\", \"2\", \"3\"};"));
    }
}

