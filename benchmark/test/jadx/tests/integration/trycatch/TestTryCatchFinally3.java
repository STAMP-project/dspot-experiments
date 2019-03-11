package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.visitors.DepthTraversal;
import jadx.core.dex.visitors.IDexTreeVisitor;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTryCatchFinally3 extends IntegrationTest {
    public static class TestCls {
        private static final Logger LOG = LoggerFactory.getLogger(TestTryCatchFinally3.TestCls.class);

        public static void process(ClassNode cls, List<IDexTreeVisitor> passes) {
            try {
                cls.load();
                for (IDexTreeVisitor visitor : passes) {
                    DepthTraversal.visit(visitor, cls);
                }
            } catch (Exception e) {
                TestTryCatchFinally3.TestCls.LOG.error("Class process exception: {}", cls, e);
            } finally {
                cls.unload();
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchFinally3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (IDexTreeVisitor visitor : passes) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("LOG.error(\"Class process exception: {}\", cls, e);"));
        Assert.assertThat(code, JadxMatchers.containsOne("} finally {"));
        Assert.assertThat(code, JadxMatchers.containsOne("cls.unload();"));
    }
}

