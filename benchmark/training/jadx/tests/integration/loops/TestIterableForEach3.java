package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TestIterableForEach3 extends IntegrationTest {
    public static class TestCls<T extends String> {
        private Set<T> a;

        private Set<T> b;

        private void test(T str) {
            Set<T> set = ((str.length()) == 1) ? a : b;
            for (T s : set) {
                if ((s.length()) == (str.length())) {
                    if ((str.length()) == 0) {
                        set.remove(s);
                    } else {
                        set.add(str);
                    }
                    return;
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIterableForEach3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (T s : set) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (str.length() == 0) {"));
        // TODO move return outside 'if'
    }
}

