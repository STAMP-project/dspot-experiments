package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions13 extends IntegrationTest {
    public static class TestCls {
        static boolean qualityReading;

        public static void dataProcess(int raw, int quality) {
            if ((quality >= 10) && (raw != 0)) {
                System.out.println(("OK" + raw));
                TestConditions13.TestCls.qualityReading = false;
            } else
                if (((raw == 0) || (quality < 6)) || (!(TestConditions13.TestCls.qualityReading))) {
                    System.out.println(("Not OK" + raw));
                } else {
                    System.out.println(("Quit OK" + raw));
                }

        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions13.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (quality >= 10 && raw != 0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("System.out.println(\"OK\" + raw);"));
        Assert.assertThat(code, JadxMatchers.containsOne("qualityReading = false;"));
        Assert.assertThat(code, JadxMatchers.containsOne("} else if (raw == 0 || quality < 6 || !qualityReading) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return")));
    }
}

