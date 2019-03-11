package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchFinally extends IntegrationTest {
    public static class TestCls {
        public boolean f;

        private boolean test(Object obj) {
            this.f = false;
            try {
                TestTryCatchFinally.TestCls.exc(obj);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                f = true;
            }
            return f;
        }

        private static boolean exc(Object obj) throws Exception {
            if (obj == null) {
                throw new Exception("test");
            }
            return obj instanceof String;
        }

        public void check() {
            Assert.assertTrue(test("a"));
            Assert.assertTrue(test(null));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchFinally.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("exc(obj);"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("e.getMessage();"));
        Assert.assertThat(code, JadxMatchers.containsOne("} finally {"));
        Assert.assertThat(code, JadxMatchers.containsOne("f = true;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return this.f;"));
    }
}

