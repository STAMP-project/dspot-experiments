package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestNestedLoops2 extends IntegrationTest {
    public static class TestCls {
        private boolean test(List<String> list) {
            int j = 0;
            for (int i = 0; i < (list.size()); i++) {
                String s = list.get(i);
                while (j < (s.length())) {
                    j++;
                } 
            }
            return j > 10;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestNestedLoops2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (int i = 0; i < list.size(); i++) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("while (j < ((String) list.get(i)).length()) {"));
    }
}

