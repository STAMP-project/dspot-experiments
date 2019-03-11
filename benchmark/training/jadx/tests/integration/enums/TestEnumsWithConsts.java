package jadx.tests.integration.enums;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestEnumsWithConsts extends IntegrationTest {
    public static class TestCls {
        public static final int C1 = 1;

        public static final int C2 = 2;

        public static final int C4 = 4;

        public static final String S = "NORTH";

        public enum Direction {

            NORTH,
            SOUTH,
            EAST,
            WEST;}
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestEnumsWithConsts.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(1, "public enum Direction {", ((TestUtils.indent(1)) + "NORTH,"), ((TestUtils.indent(1)) + "SOUTH,"), ((TestUtils.indent(1)) + "EAST,"), ((TestUtils.indent(1)) + "WEST"), "}"));
    }
}

