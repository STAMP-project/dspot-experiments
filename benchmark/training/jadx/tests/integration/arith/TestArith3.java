package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArith3 extends IntegrationTest {
    public static class TestCls {
        private int vp;

        private void test(byte[] buffer) {
            int n = (((buffer[3]) & 255) + 4) + (((buffer[2]) & 15) << 8);
            while ((n + 4) < (buffer.length)) {
                int c = (buffer[n]) & 255;
                int p = ((buffer[(n + 2)]) & 255) + (((buffer[(n + 1)]) & 31) << 8);
                int len = ((buffer[(n + 4)]) & 255) + (((buffer[(n + 3)]) & 15) << 8);
                switch (c) {
                    case 27 :
                        this.vp = p;
                        break;
                }
                n += len + 5;
            } 
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestArith3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("while (n + 4 < buffer.length) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("n += len + 5;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("; n += len + 5) {")));
    }
}

