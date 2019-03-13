package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchFinally4 extends IntegrationTest {
    public static class TestCls {
        public void test() throws IOException {
            File file = File.createTempFile("test", "txt");
            OutputStream outputStream = new FileOutputStream(file);
            try {
                outputStream.write(1);
            } finally {
                try {
                    outputStream.close();
                    file.delete();
                } catch (IOException e) {
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchFinally4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("File file = File.createTempFile(\"test\", \"txt\");"));
        Assert.assertThat(code, JadxMatchers.containsOne("OutputStream outputStream = new FileOutputStream(file);"));
        Assert.assertThat(code, JadxMatchers.containsOne("outputStream.write(1);"));
        Assert.assertThat(code, JadxMatchers.containsOne("} finally {"));
        Assert.assertThat(code, JadxMatchers.containsOne("outputStream.close();"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (IOException e) {"));
    }
}

