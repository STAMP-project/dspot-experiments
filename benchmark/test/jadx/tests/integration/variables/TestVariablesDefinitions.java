package jadx.tests.integration.variables;


import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.visitors.DepthTraversal;
import jadx.core.dex.visitors.IDexTreeVisitor;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;


public class TestVariablesDefinitions extends IntegrationTest {
    public static class TestCls {
        private static Logger LOG;

        private ClassNode cls;

        private List<IDexTreeVisitor> passes;

        public void run() {
            try {
                cls.load();
                for (IDexTreeVisitor pass : this.passes) {
                    DepthTraversal.visit(pass, cls);
                }
            } catch (Exception e) {
                TestVariablesDefinitions.TestCls.LOG.error("Decode exception: {}", cls, e);
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestVariablesDefinitions.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne(((TestUtils.indent(3)) + "for (IDexTreeVisitor pass : this.passes) {")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("iterator;")));
    }
}

