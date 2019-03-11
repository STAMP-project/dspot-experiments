package jadx.tests.integration.debuginfo;


import jadx.core.codegen.CodeWriter;
import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestLineNumbers2 extends IntegrationTest {
    public static class TestCls {
        private WeakReference<TestLineNumbers2.TestCls> f;

        public TestCls(TestLineNumbers2.TestCls s) {
        }

        public TestLineNumbers2.TestCls test(TestLineNumbers2.TestCls s) {
            TestLineNumbers2.TestCls store = ((f) != null) ? f.get() : null;
            if (store == null) {
                store = new TestLineNumbers2.TestCls(s);
                f = new WeakReference<>(store);
            }
            return store;
        }

        public Object test2() {
            return new Object();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLineNumbers2.TestCls.class);
        CodeWriter codeWriter = cls.getCode();
        String code = codeWriter.toString();
        Map<Integer, Integer> lineMapping = codeWriter.getLineMapping();
        Assert.assertEquals("{8=18, 11=22, 12=23, 13=24, 14=28, 16=25, 17=26, 18=28, 21=31, 22=32}", lineMapping.toString());
    }
}

