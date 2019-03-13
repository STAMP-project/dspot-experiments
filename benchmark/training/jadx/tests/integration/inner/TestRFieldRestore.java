package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestRFieldRestore extends IntegrationTest {
    public static class TestCls {
        public int test() {
            return 2131230730;
        }
    }

    @Test
    public void test() {
        // unknown R class
        disableCompilation();
        Map<Integer, String> map = new HashMap<>();
        map.put(2131230730, "id.Button");
        setResMap(map);
        ClassNode cls = getClassNode(TestRFieldRestore.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return R.id.Button;"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("import R;")));
    }
}

