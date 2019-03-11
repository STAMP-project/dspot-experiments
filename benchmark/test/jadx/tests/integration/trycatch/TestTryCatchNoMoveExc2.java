package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Issue: https://github.com/skylot/jadx/issues/395
 */
public class TestTryCatchNoMoveExc2 extends SmaliTest {
    // private static void test(AutoCloseable closeable) {
    // if (closeable != null) {
    // try {
    // closeable.close();
    // } catch (Exception unused) {
    // }
    // System.nanoTime();
    // }
    // }
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliWithPath("trycatch", "TestTryCatchNoMoveExc2");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsLines(2, "} catch (Exception unused) {", "}", "System.nanoTime();"));
    }
}

