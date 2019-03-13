package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch8 extends IntegrationTest {
    public static class TestCls {
        static class MyException extends Exception {
            private static final long serialVersionUID = 7963400419047287279L;

            MyException() {
            }

            MyException(String msg, Throwable cause) {
                super(msg, cause);
            }
        }

        TestTryCatch8.TestCls.MyException e = null;

        public void test() {
            synchronized(this) {
                try {
                    throw new TestTryCatch8.TestCls.MyException();
                } catch (TestTryCatch8.TestCls.MyException e) {
                    this.e = e;
                } catch (Exception x) {
                    this.e = new TestTryCatch8.TestCls.MyException("MyExc", x);
                }
            }
        }

        public void check() {
            test();
            Assert.assertThat(e, Matchers.notNullValue());
            Assert.assertThat(e, Matchers.isA(TestTryCatch8.TestCls.MyException.class));
            Assert.assertThat(e.getMessage(), Matchers.nullValue());
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatch8.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("synchronized (this) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("throw new MyException();"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (MyException e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("this.e = e;"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception x) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("this.e = new MyException(\"MyExc\", x);"));
    }
}

