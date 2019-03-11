package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestNestedIf extends IntegrationTest {
    public static class TestCls {
        private boolean a0 = false;

        private int a1 = 1;

        private int a2 = 2;

        private int a3 = 1;

        private int a4 = 2;

        public boolean test1() {
            if (a0) {
                if (((a1) == 0) || ((a2) == 0)) {
                    return false;
                }
            } else
                if (((a3) == 0) || ((a4) == 0)) {
                    return false;
                }

            test1();
            return true;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestNestedIf.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (this.a0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (this.a1 == 0 || this.a2 == 0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} else if (this.a3 == 0 || this.a4 == 0) {"));
        Assert.assertThat(code, JadxMatchers.countString(2, "return false;"));
        Assert.assertThat(code, JadxMatchers.containsOne("test1();"));
        Assert.assertThat(code, JadxMatchers.containsOne("return true;"));
    }
}

