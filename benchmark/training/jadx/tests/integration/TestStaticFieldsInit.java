package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestStaticFieldsInit extends IntegrationTest {
    public static class TestCls {
        public static final String s1 = "1";

        public static final String s2 = "12".substring(1);

        public static final String s3 = null;

        public static final String s4;

        public static final String s5 = "5";

        public static String s6 = "6";

        static {
            if (TestStaticFieldsInit.TestCls.s5.equals("?")) {
                s4 = "?";
            } else {
                s4 = "4";
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestStaticFieldsInit.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("public static final String s2 = null;")));
        // TODO:
        // assertThat(code, containsString("public static final String s3 = null;"));
    }
}

