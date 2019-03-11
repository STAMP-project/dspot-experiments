package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchLabels extends IntegrationTest {
    public static class TestCls {
        public static final int CONST_ABC = 2748;

        public static final int CONST_CDE = 3294;

        public static class Inner {
            private static final int CONST_CDE_PRIVATE = 3294;

            public int f1(int arg0) {
                switch (arg0) {
                    case TestSwitchLabels.TestCls.Inner.CONST_CDE_PRIVATE :
                        return TestSwitchLabels.TestCls.CONST_ABC;
                }
                return 0;
            }
        }

        public static int f1(int arg0) {
            switch (arg0) {
                case TestSwitchLabels.TestCls.CONST_ABC :
                    return TestSwitchLabels.TestCls.CONST_CDE;
            }
            return 0;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchLabels.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("case CONST_ABC"));
        Assert.assertThat(code, CoreMatchers.containsString("return CONST_CDE;"));
        cls.addInnerClass(getClassNode(TestSwitchLabels.TestCls.Inner.class));
        Assert.assertThat(code, Matchers.not(CoreMatchers.containsString("case CONST_CDE_PRIVATE")));
        Assert.assertThat(code, CoreMatchers.containsString(".CONST_ABC;"));
    }

    @Test
    public void testWithDisabledConstReplace() {
        getArgs().setReplaceConsts(false);
        ClassNode cls = getClassNode(TestSwitchLabels.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, Matchers.not(CoreMatchers.containsString("case CONST_ABC")));
        Assert.assertThat(code, CoreMatchers.containsString("case 2748"));
        Assert.assertThat(code, Matchers.not(CoreMatchers.containsString("return CONST_CDE;")));
        Assert.assertThat(code, CoreMatchers.containsString("return 3294;"));
        cls.addInnerClass(getClassNode(TestSwitchLabels.TestCls.Inner.class));
        Assert.assertThat(code, Matchers.not(CoreMatchers.containsString("case CONST_CDE_PRIVATE")));
        Assert.assertThat(code, Matchers.not(CoreMatchers.containsString(".CONST_ABC;")));
    }
}

