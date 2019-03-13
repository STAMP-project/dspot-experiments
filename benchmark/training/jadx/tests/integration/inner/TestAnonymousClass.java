package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.io.File;
import java.io.FilenameFilter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass extends IntegrationTest {
    public static class TestCls {
        public int test() {
            String[] files = new File("a").list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals("a");
                }
            });
            return files.length;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("new File(\"a\").list(new FilenameFilter()"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("synthetic")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("this")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("null")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("AnonymousClass_")));
    }
}

