package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;


public class TestFieldInitInTryCatch extends IntegrationTest {
    public static class TestCls {
        private static final URL a;

        static {
            try {
                a = new URL("http://www.example.com/");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class TestCls2 {
        private static final URL[] a;

        static {
            try {
                a = new URL[]{ new URL("http://www.example.com/") };
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class TestCls3 {
        private static final String[] a;

        static {
            try {
                a = new String[]{ "a" };
                // Note: follow code will not be extracted:
                // a = new String[]{new String("a")};
                new URL("http://www.example.com/");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFieldInitInTryCatch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("private static final URL a;"));
        Assert.assertThat(code, JadxMatchers.containsOne("a = new URL(\"http://www.example.com/\");"));
        Assert.assertThat(code, JadxMatchers.containsLines(2, "try {", ((TestUtils.indent(1)) + "a = new URL(\"http://www.example.com/\");"), "} catch (MalformedURLException e) {"));
    }

    @Test
    public void test2() {
        ClassNode cls = getClassNode(TestFieldInitInTryCatch.TestCls2.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(2, "try {", ((TestUtils.indent(1)) + "a = new URL[]{new URL(\"http://www.example.com/\")};"), "} catch (MalformedURLException e) {"));
    }

    @Test
    public void test3() {
        ClassNode cls = getClassNode(TestFieldInitInTryCatch.TestCls3.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("private static final String[] a = new String[]{\"a\"};"));
    }
}

