package jadx.tests.integration.enums;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestEnums extends IntegrationTest {
    public static class TestCls {
        public enum EmptyEnum {
            ;
        }

        public enum EmptyEnum2 {
            ;

            public static void mth() {
            }
        }

        public enum Direction {

            NORTH,
            SOUTH,
            EAST,
            WEST;}

        public enum Singleton {

            INSTANCE;
            public String test() {
                return "";
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestEnums.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(1, "public enum EmptyEnum {", "}"));
        Assert.assertThat(code, JadxMatchers.containsLines(1, "public enum EmptyEnum2 {", ((TestUtils.indent(1)) + ";"), "", ((TestUtils.indent(1)) + "public static void mth() {"), ((TestUtils.indent(1)) + "}"), "}"));
        Assert.assertThat(code, JadxMatchers.containsLines(1, "public enum Direction {", ((TestUtils.indent(1)) + "NORTH,"), ((TestUtils.indent(1)) + "SOUTH,"), ((TestUtils.indent(1)) + "EAST,"), ((TestUtils.indent(1)) + "WEST"), "}"));
        Assert.assertThat(code, JadxMatchers.containsLines(1, "public enum Singleton {", ((TestUtils.indent(1)) + "INSTANCE;"), "", ((TestUtils.indent(1)) + "public String test() {"), ((TestUtils.indent(2)) + "return \"\";"), ((TestUtils.indent(1)) + "}"), "}"));
    }
}

