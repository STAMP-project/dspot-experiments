package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestNestedLoops extends IntegrationTest {
    public static class TestCls {
        private void test(List<String> l1, List<String> l2) {
            for (String s1 : l1) {
                for (String s2 : l2) {
                    if (s1.equals(s2)) {
                        if ((s1.length()) == 5) {
                            l2.add(s1);
                        } else {
                            l1.remove(s2);
                        }
                    }
                }
            }
            if ((l2.size()) > 0) {
                l1.clear();
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestNestedLoops.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (String s1 : l1) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("for (String s2 : l2) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (s1.equals(s2)) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("l2.add(s1);"));
        Assert.assertThat(code, JadxMatchers.containsOne("l1.remove(s2);"));
    }
}

