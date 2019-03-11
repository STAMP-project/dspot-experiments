package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


public class TestFinally2 extends IntegrationTest {
    public static class TestCls {
        public TestFinally2.TestCls.Result test(byte[] data) throws IOException {
            InputStream inputStream = null;
            try {
                inputStream = getInputStream(data);
                decode(inputStream);
                return new TestFinally2.TestCls.Result(400);
            } finally {
                closeQuietly(inputStream);
            }
        }

        public static final class Result {
            private final int mCode;

            public Result(int code) {
                mCode = code;
            }

            public int getCode() {
                return mCode;
            }
        }

        private InputStream getInputStream(byte[] data) throws IOException {
            return new ByteArrayInputStream(data);
        }

        private int decode(InputStream inputStream) throws IOException {
            return inputStream.available();
        }

        private void closeQuietly(InputStream is) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFinally2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("decode(inputStream);"));
        // TODO
        // assertThat(code, not(containsOne("result =")));
    }
}

