package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestRFieldRestore2 extends IntegrationTest {
    public static class TestCls {
        public static class R {}

        public int test() {
            return 2131230730;
        }
    }

    @Test
    public void test() {
        Map<Integer, String> map = new HashMap<>();
        map.put(2131230730, "id.Button");
        setResMap(map);
        ClassNode cls = getClassNode(TestRFieldRestore2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("R.id.Button;"));
    }
}

