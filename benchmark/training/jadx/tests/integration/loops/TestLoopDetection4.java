package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopDetection4 extends IntegrationTest {
    public static class TestCls {
        private Iterator<String> iterator;

        private TestLoopDetection4.TestCls.SomeCls filter;

        private String test() {
            while (iterator.hasNext()) {
                String next = iterator.next();
                String filtered = filter.filter(next);
                if (filtered != null) {
                    return filtered;
                }
            } 
            return null;
        }

        private class SomeCls {
            public String filter(String str) {
                return str;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopDetection4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("while (this.iterator.hasNext()) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (filtered != null) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("return filtered;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return null;"));
    }
}

