package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchInIf extends IntegrationTest {
    public static class TestCls {
        private String test(String name, String value) {
            if (value != null) {
                try {
                    int key;
                    if (value.startsWith("0x")) {
                        value = value.substring(2);
                        key = Integer.parseInt(value, 16);
                    } else {
                        key = Integer.parseInt(value);
                    }
                    return (name + "=") + key;
                } catch (NumberFormatException e) {
                    return "Failed to parse number";
                }
            }
            System.out.println("?");
            return null;
        }

        public void check() {
            Assert.assertEquals(null, test("n", null));
            Assert.assertEquals("n=7", test("n", "7"));
            Assert.assertEquals("n=77", test("n", ("0x" + (Integer.toHexString(77)))));
            Assert.assertEquals("Failed to parse number", test("n", "abc"));
            Assert.assertEquals("Failed to parse number", test("n", "0xabX"));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchInIf.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (NumberFormatException e) {"));
    }
}

