package jadx.tests.integration.synchronize;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSynchronized extends IntegrationTest {
    public static class TestCls {
        public boolean f = false;

        public final Object o = new Object();

        public int i = 7;

        public synchronized boolean test1() {
            return this.f;
        }

        public int test2() {
            synchronized(this.o) {
                return i;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSynchronized.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("synchronized (this) {")));
        Assert.assertThat(code, CoreMatchers.containsString("public synchronized boolean test1() {"));
        Assert.assertThat(code, CoreMatchers.containsString("return this.f"));
        Assert.assertThat(code, CoreMatchers.containsString("synchronized (this.o) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString(((TestUtils.indent(3)) + ";"))));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("try {")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("} catch (Throwable th) {")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("throw th;")));
    }
}

