package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestRedundantBrackets extends IntegrationTest {
    public static class TestCls {
        public boolean method(String str) {
            return (str.indexOf('a')) != (-1);
        }

        public int method2(Object obj) {
            return obj instanceof String ? ((String) (obj)).length() : 0;
        }

        public int method3(int a, int b) {
            if ((a + b) < 10) {
                return a;
            }
            if ((a & b) != 0) {
                return a * b;
            }
            return b;
        }

        public void method4(int num) {
            if ((((num == 4) || (num == 6)) || (num == 8)) || (num == 10)) {
                method2(null);
            }
        }

        public void method5(int[] a, int n) {
            a[1] = n * 2;
            a[(n - 1)] = 1;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestRedundantBrackets.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("(-1)")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("return;")));
        Assert.assertThat(code, CoreMatchers.containsString("return obj instanceof String ? ((String) obj).length() : 0;"));
        Assert.assertThat(code, CoreMatchers.containsString("a + b < 10"));
        Assert.assertThat(code, CoreMatchers.containsString("(a & b) != 0"));
        Assert.assertThat(code, CoreMatchers.containsString("if (num == 4 || num == 6 || num == 8 || num == 10)"));
        Assert.assertThat(code, CoreMatchers.containsString("a[1] = n * 2;"));
        Assert.assertThat(code, CoreMatchers.containsString("a[n - 1] = 1;"));
        // argument type not changed to String
        Assert.assertThat(code, CoreMatchers.containsString("public int method2(Object obj) {"));
        // cast not eliminated
        Assert.assertThat(code, CoreMatchers.containsString("((String) obj).length()"));
    }
}

