package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestArrayForEach2 extends IntegrationTest {
    public static class TestCls {
        private void test(String str) {
            for (String s : str.split("\n")) {
                String t = s.trim();
                if ((t.length()) > 0) {
                    System.out.println(t);
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestArrayForEach2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(2, "for (String s : str.split(\"\\n\")) {", ((TestUtils.indent(1)) + "String t = s.trim();"), ((TestUtils.indent(1)) + "if (t.length() > 0) {"), ((TestUtils.indent(2)) + "System.out.println(t);"), ((TestUtils.indent(1)) + "}"), "}"));
    }
}

