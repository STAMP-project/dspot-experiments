package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.security.ProviderException;
import java.time.DateTimeException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestMultiExceptionCatch extends IntegrationTest {
    public static class TestCls {
        public void test() {
            try {
                System.out.println("Test");
            } catch (ProviderException | DateTimeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestMultiExceptionCatch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (ProviderException | DateTimeException e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("throw new RuntimeException(e);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("RuntimeException e;")));
    }
}

