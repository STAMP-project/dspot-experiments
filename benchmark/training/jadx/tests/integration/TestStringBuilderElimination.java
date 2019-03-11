package jadx.tests.integration;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestStringBuilderElimination extends IntegrationTest {
    public static class MyException extends Exception {
        private static final long serialVersionUID = 4245254480662372757L;

        public MyException(String str, Exception e) {
            super(("msg:" + str), e);
        }

        public void method(int k) {
            System.out.println(("k=" + k));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestStringBuilderElimination.MyException.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("MyException(String str, Exception e) {"));
        Assert.assertThat(code, CoreMatchers.containsString("super(\"msg:\" + str, e);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("new StringBuilder")));
        Assert.assertThat(code, CoreMatchers.containsString("System.out.println(\"k=\" + k);"));
    }
}

