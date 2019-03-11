package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchFinally6 extends IntegrationTest {
    public static class TestCls {
        public static void test() throws IOException {
            InputStream is = null;
            try {
                is = new FileInputStream("1.txt");
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchFinally6.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(2, "InputStream is = null;", "try {", ((TestUtils.indent(1)) + "is = new FileInputStream(\"1.txt\");"), "} finally {", ((TestUtils.indent(1)) + "if (is != null) {"), ((TestUtils.indent(2)) + "is.close();"), ((TestUtils.indent(1)) + "}"), "}"));
    }
}

