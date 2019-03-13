package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestIfInTry extends IntegrationTest {
    public static class TestCls {
        private File dir;

        public int test() {
            try {
                int a = f();
                if (a != 0) {
                    return a;
                }
            } catch (Exception e) {
                // skip
            }
            try {
                f();
                return 1;
            } catch (IOException e) {
                return -1;
            }
        }

        private int f() throws IOException {
            return 0;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIfInTry.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (a != 0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception e) {"));
        Assert.assertThat(code, JadxMatchers.countString(2, "try {"));
        Assert.assertThat(code, JadxMatchers.countString(3, "f()"));
        Assert.assertThat(code, JadxMatchers.containsOne("return 1;"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (IOException e"));
        Assert.assertThat(code, JadxMatchers.containsOne("return -1;"));
    }
}

