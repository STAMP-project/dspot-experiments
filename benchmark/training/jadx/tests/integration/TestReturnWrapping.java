package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestReturnWrapping extends IntegrationTest {
    public static class TestCls {
        public static int f1(int arg0) {
            switch (arg0) {
                case 1 :
                    return 255;
            }
            return arg0 + 1;
        }

        public static Object f2(Object arg0, int arg1) {
            Object ret = null;
            int i = arg1;
            if (arg0 == null) {
                return ret + (Integer.toHexString(i));
            } else {
                i++;
                try {
                    ret = new Object().getClass();
                } catch (Exception e) {
                    ret = "Qwerty";
                }
                return i > 128 ? (arg0.toString()) + (ret.toString()) : i;
            }
        }

        public static int f3(int arg0) {
            while (arg0 > 10) {
                int abc = 951;
                if (arg0 == 255) {
                    return arg0 + 2;
                }
                arg0 -= abc;
            } 
            return arg0;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestReturnWrapping.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return 255;"));
        Assert.assertThat(code, CoreMatchers.containsString("return arg0 + 1;"));
        Assert.assertThat(code, CoreMatchers.containsString("return i > 128 ? arg0.toString() + ret.toString() : Integer.valueOf(i);"));
        Assert.assertThat(code, CoreMatchers.containsString("return arg0 + 2;"));
        Assert.assertThat(code, CoreMatchers.containsString("arg0 -= 951;"));
    }
}

