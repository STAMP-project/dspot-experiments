package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch7 extends IntegrationTest {
    public static class TestCls {
        private Exception test() {
            Exception e = new Exception();
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
                e = ex;
            }
            e.printStackTrace();
            return e;
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestTryCatch7.TestCls.class);
        String code = cls.getCode().toString();
        String excVarName = "e";
        String catchExcVarName = "e2";
        Assert.assertThat(code, JadxMatchers.containsOne((("Exception " + excVarName) + " = new Exception();")));
        Assert.assertThat(code, JadxMatchers.containsOne((("} catch (Exception " + catchExcVarName) + ") {")));
        Assert.assertThat(code, JadxMatchers.containsOne((((excVarName + " = ") + catchExcVarName) + ";")));
        Assert.assertThat(code, JadxMatchers.containsOne((excVarName + ".printStackTrace();")));
        Assert.assertThat(code, JadxMatchers.containsOne((("return " + excVarName) + ";")));
    }
}

