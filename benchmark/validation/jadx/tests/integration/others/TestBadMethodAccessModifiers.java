package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestBadMethodAccessModifiers extends SmaliTest {
    /* public static class TestCls {

    public abstract class A {
    public abstract void test();
    }

    public class B extends A {
    protected void test() {
    }
    }
    }
     */
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliFiles("others", "TestBadMethodAccessModifiers", "TestCls", "TestCls$A", "TestCls$B", "TestCls");
        String code = cls.getCode().toString();
        Assert.assertThat(code, Matchers.not(Matchers.containsString("protected void test() {")));
        Assert.assertThat(code, JadxMatchers.containsOne("public void test() {"));
    }
}

