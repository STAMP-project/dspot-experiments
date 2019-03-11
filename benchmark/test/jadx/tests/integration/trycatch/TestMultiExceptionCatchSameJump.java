package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestMultiExceptionCatchSameJump extends SmaliTest {
    /* public static class TestCls {
    public void test() {
    try {
    System.out.println("Test");
    } catch (ProviderException | DateTimeException e) {
    throw new RuntimeException(e);
    }
    }
    }
     */
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliWithPkg("trycatch", "TestMultiExceptionCatchSameJump");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (ProviderException | DateTimeException e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("throw new RuntimeException(e);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("RuntimeException e;")));
    }
}

