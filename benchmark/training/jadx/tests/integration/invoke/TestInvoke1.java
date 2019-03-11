package jadx.tests.integration.invoke;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestInvoke1 extends IntegrationTest {
    public static class TestCls {
        private TestInvoke1.TestCls.A is;

        private TestInvoke1.TestCls.C test(int start) throws IOException {
            int id = is.readInt32();
            String name = is.readString16Fixed(128);
            long typeStringsOffset = start + (is.readInt32());
            long keyStringsOffset = start + (is.readInt32());
            String[] types = null;
            if (typeStringsOffset != 0) {
                types = strs();
            }
            String[] keys = null;
            if (keyStringsOffset != 0) {
                keys = strs();
            }
            TestInvoke1.TestCls.C pkg = new TestInvoke1.TestCls.C(id, name, types, keys);
            if (id == 127) {
                is.readInt32();
            }
            return pkg;
        }

        private String[] strs() {
            return new String[0];
        }

        private static final class C {
            public C(int id, String name, String[] types, String[] keys) {
            }
        }

        private final class A {
            public int readInt32() {
                return 0;
            }

            public String readString16Fixed(int i) {
                return null;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInvoke1.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("C pkg = new C(id, name, types, keys);"));
    }
}

