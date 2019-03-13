package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch3 extends IntegrationTest {
    public static class TestCls {
        private int f = 0;

        private boolean test(Object obj) {
            boolean res;
            try {
                res = exc(obj);
            } catch (Exception e) {
                res = false;
            } finally {
                (f)++;
            }
            return res;
        }

        private boolean exc(Object obj) throws Exception {
            if ("r".equals(obj)) {
                throw new AssertionError();
            }
            return true;
        }

        public void check() {
            f = 0;
            Assert.assertTrue(test(null));
            Assert.assertEquals(1, f);
            f = 0;
            try {
                test("r");
            } catch (AssertionError e) {
                // pass
            }
            Assert.assertEquals(1, f);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatch3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("try {"));
        Assert.assertThat(code, CoreMatchers.containsString("exc(obj);"));
        Assert.assertThat(code, CoreMatchers.containsString("} catch (Exception e) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("throw th;")));
    }

    @Test
    public void test2() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestTryCatch3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("throw th;")));
    }
}

