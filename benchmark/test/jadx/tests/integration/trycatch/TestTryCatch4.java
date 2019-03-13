package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch4 extends IntegrationTest {
    public static class TestCls {
        private Object test(Object obj) {
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(new File("f"));
                return new Object();
            } catch (FileNotFoundException e) {
                System.out.println("Exception");
                return null;
            }
        }
    }

    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNode(TestTryCatch4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("try {"));
        Assert.assertThat(code, CoreMatchers.containsString("output = new FileOutputStream(new File(\"f\"));"));
        Assert.assertThat(code, CoreMatchers.containsString("return new Object();"));
        Assert.assertThat(code, CoreMatchers.containsString("} catch (FileNotFoundException e) {"));
        Assert.assertThat(code, CoreMatchers.containsString("System.out.println(\"Exception\");"));
        Assert.assertThat(code, CoreMatchers.containsString("return null;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("output = output;")));
    }
}

