package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestEndlessLoop extends IntegrationTest {
    public static class TestCls {
        void test1() {
            while ((this) == (this)) {
            } 
        }

        void test2() {
            do {
            } while ((this) == (this) );
        }

        void test3() {
            while (true) {
                if ((this) != (this)) {
                    return;
                }
            } 
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestEndlessLoop.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, Matchers.containsString("while (this == this)"));
    }
}

