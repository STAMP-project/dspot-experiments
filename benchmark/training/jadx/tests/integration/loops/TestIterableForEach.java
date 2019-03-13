package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestIterableForEach extends IntegrationTest {
    public static class TestCls {
        private String test(Iterable<String> a) {
            StringBuilder sb = new StringBuilder();
            for (String s : a) {
                sb.append(s);
            }
            return sb.toString();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIterableForEach.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(2, "StringBuilder sb = new StringBuilder();", "for (String s : a) {", ((TestUtils.indent(1)) + "sb.append(s);"), "}", "return sb.toString();"));
    }
}

