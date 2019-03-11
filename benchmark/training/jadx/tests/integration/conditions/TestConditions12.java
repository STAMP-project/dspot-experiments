package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions12 extends IntegrationTest {
    public static class TestCls {
        static boolean autoStop = true;

        static boolean qualityReading = false;

        static int lastValidRaw = -1;

        public static void main(String[] args) throws Exception {
            int a = 5;
            int b = 30;
            TestConditions12.TestCls.dataProcess(a, b);
        }

        public static void dataProcess(int raw, int quality) {
            if ((quality >= 10) && (raw != 0)) {
                System.out.println(("OK" + raw));
                TestConditions12.TestCls.qualityReading = false;
            } else
                if (((raw == 0) || (quality < 6)) || (!(TestConditions12.TestCls.qualityReading))) {
                    System.out.println(("Not OK" + raw));
                } else {
                    System.out.println(("Quit OK" + raw));
                }

            if (quality < 30) {
                int timeLeft = 30 - quality;
                if (quality >= 10) {
                    System.out.println(("Processing" + timeLeft));
                }
            } else {
                System.out.println("Finish Processing");
                if (raw > 0) {
                    TestConditions12.TestCls.lastValidRaw = raw;
                }
            }
            if ((quality >= 30) && (TestConditions12.TestCls.autoStop)) {
                System.out.println("Finished");
            }
            if (((!(TestConditions12.TestCls.autoStop)) && ((TestConditions12.TestCls.lastValidRaw) > (-1))) && (quality < 10)) {
                System.out.println("Finished");
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions12.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (quality >= 10 && raw != 0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} else if (raw == 0 || quality < 6 || !qualityReading) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (quality < 30) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (quality >= 10) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (raw > 0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (quality >= 30 && autoStop) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (!autoStop && lastValidRaw > -1 && quality < 10) {"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return")));
    }
}

