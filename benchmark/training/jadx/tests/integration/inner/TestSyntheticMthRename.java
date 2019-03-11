package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Issue: https://github.com/skylot/jadx/issues/397
 */
public class TestSyntheticMthRename extends SmaliTest {
    // public class TestCls {
    // public interface I<R, P> {
    // R call(P... p);
    // }
    // 
    // public static final class A implements I<String, Runnable> {
    // public /* synthetic */ /* virtual */ Object call(Object[] objArr) {
    // return renamedCall((Runnable[]) objArr);
    // }
    // 
    // private /* varargs */ /* direct */ String renamedCall(Runnable... p) {
    // return "str";
    // }
    // }
    // }
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliFiles("inner", "TestSyntheticMthRename", "TestCls", "TestCls", "TestCls$I", "TestCls$A");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("public String call(Runnable... p) {"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

