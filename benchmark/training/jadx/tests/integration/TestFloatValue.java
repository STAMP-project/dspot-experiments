package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFloatValue extends IntegrationTest {
    public static class TestCls {
        public float[] method() {
            float[] fa = new float[]{ 0.55F };
            fa[0] /= 2;
            return fa;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFloatValue.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("1073741824")));
        Assert.assertThat(code, CoreMatchers.containsString("0.55f"));
        Assert.assertThat(code, CoreMatchers.containsString("fa[0] = fa[0] / 2.0f;"));
    }
}

