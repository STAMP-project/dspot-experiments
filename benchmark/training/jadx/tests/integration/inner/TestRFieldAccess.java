package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestRFieldAccess extends IntegrationTest {
    public static class R {
        public static final class id {
            public static final int Button01 = 2131230730;
        }
    }

    public static class TestR {
        public int test() {
            return TestRFieldAccess.R.id.Button01;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestRFieldAccess.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(2, "return R.id.Button01;"));
    }
}

