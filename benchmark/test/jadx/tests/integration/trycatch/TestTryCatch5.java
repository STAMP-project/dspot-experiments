package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch5 extends IntegrationTest {
    public static class TestCls {
        private Object test(Object obj) {
            File file = new File("r");
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(file);
                if (obj.equals("a")) {
                    return new Object();
                } else {
                    return null;
                }
            } catch (IOException e) {
                System.out.println("Exception");
                return null;
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        // Ignored
                    }
                }
                file.delete();
            }
        }
    }

    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNode(TestTryCatch5.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("try {"));
        // TODO:
        // assertThat(code, containsString("output = new FileOutputStream(file);"));
        // assertThat(code, containsString("} catch (IOException e) {"));
        Assert.assertThat(code, CoreMatchers.containsString("file.delete();"));
    }
}

