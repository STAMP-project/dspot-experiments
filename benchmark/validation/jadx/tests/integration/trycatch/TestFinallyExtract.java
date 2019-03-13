package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestFinallyExtract extends IntegrationTest {
    public static class TestCls {
        public String test() throws IOException {
            boolean success = false;
            try {
                String value = test();
                success = true;
                return value;
            } finally {
                if (!success) {
                    test();
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFinallyExtract.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("success = true;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return value;"));
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} finally {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (!success) {"));
    }
}

