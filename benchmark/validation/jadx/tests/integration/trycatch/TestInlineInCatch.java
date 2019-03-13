package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class TestInlineInCatch extends IntegrationTest {
    public static class TestCls {
        private File dir;

        public int test() {
            File output = null;
            try {
                output = File.createTempFile("f", "a", dir);
                return 0;
            } catch (Exception e) {
                if (output != null) {
                    output.delete();
                }
                return 2;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInlineInCatch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("File output = null;"));
        Assert.assertThat(code, JadxMatchers.containsOne("output = File.createTempFile(\"f\", \"a\", "));
        Assert.assertThat(code, JadxMatchers.containsOne("return 0;"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (output != null) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("output.delete();"));
        Assert.assertThat(code, JadxMatchers.containsOne("return 2;"));
    }
}

