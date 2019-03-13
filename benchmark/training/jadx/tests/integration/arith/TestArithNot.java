package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArithNot extends SmaliTest {
    /* Smali Code equivalent:
    public static class TestCls {
    public int test1(int a) {
    return ~a;
    }

    public long test2(long b) {
    return ~b;
    }
    }
     */
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliWithPath("arith", "TestArithNot");
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return ~a;"));
        Assert.assertThat(code, CoreMatchers.containsString("return ~b;"));
        Assert.assertThat(code, Matchers.not(CoreMatchers.containsString("^")));
    }
}

