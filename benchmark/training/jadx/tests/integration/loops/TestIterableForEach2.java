package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestIterableForEach2 extends IntegrationTest {
    public static class TestCls {
        public static String test(final TestIterableForEach2.TestCls.Service service) throws IOException {
            for (TestIterableForEach2.TestCls.Authorization auth : service.getAuthorizations()) {
                if (TestIterableForEach2.TestCls.isValid(auth)) {
                    return auth.getToken();
                }
            }
            return null;
        }

        private static boolean isValid(TestIterableForEach2.TestCls.Authorization auth) {
            return false;
        }

        private static class Service {
            public List<TestIterableForEach2.TestCls.Authorization> getAuthorizations() {
                return null;
            }
        }

        private static class Authorization {
            public String getToken() {
                return "";
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIterableForEach2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (Authorization auth : service.getAuthorizations()) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (isValid(auth)) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("return auth.getToken();"));
    }
}

