package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchNoMove extends SmaliTest {
    // private static void test(AutoCloseable closeable) {
    // if (closeable != null) {
    // try {
    // closeable.close();
    // } catch (Exception ignored) {
    // }
    // }
    // }
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliWithPath("trycatch", "TestTryCatchNoMove");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (autoCloseable != null) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("autoCloseable.close();"));
    }
}

