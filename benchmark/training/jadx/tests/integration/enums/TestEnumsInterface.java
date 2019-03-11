package jadx.tests.integration.enums;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestEnumsInterface extends IntegrationTest {
    public static class TestCls {
        public enum Operation implements TestEnumsInterface.TestCls.IOperation {

            PLUS() {
                public int apply(int x, int y) {
                    return x + y;
                }
            },
            MINUS() {
                public int apply(int x, int y) {
                    return x - y;
                }
            };}

        public interface IOperation {
            int apply(int x, int y);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestEnumsInterface.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(1, "public enum Operation implements IOperation {", ((TestUtils.indent(1)) + "PLUS {"), ((TestUtils.indent(2)) + "public int apply(int x, int y) {"), ((TestUtils.indent(3)) + "return x + y;"), ((TestUtils.indent(2)) + "}"), ((TestUtils.indent(1)) + "},"), ((TestUtils.indent(1)) + "MINUS {"), ((TestUtils.indent(2)) + "public int apply(int x, int y) {"), ((TestUtils.indent(3)) + "return x - y;"), ((TestUtils.indent(2)) + "}"), ((TestUtils.indent(1)) + "}"), "}"));
    }
}

