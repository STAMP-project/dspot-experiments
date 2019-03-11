package jadx.tests.integration.types;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestTypeResolver2 extends IntegrationTest {
    public static class TestCls {
        private static boolean test(Object obj) throws IOException {
            if (obj != null) {
                return true;
            }
            throw new IOException();
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestTypeResolver2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (obj != null) {"));
    }
}

