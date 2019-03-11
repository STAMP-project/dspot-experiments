package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions6 extends IntegrationTest {
    public static class TestCls {
        public boolean test(List<String> l1, List<String> l2) {
            if ((l2.size()) > 0) {
                l1.clear();
            }
            return (l1.size()) == 0;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions6.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return l1.size() == 0;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
    }
}

