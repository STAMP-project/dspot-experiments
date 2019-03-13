package jadx.tests.integration.conditions;


import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.Named;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTernary3 extends IntegrationTest {
    public static class TestCls {
        public boolean isNameEquals(InsnArg arg) {
            String n = getName(arg);
            if ((n == null) || (!(arg instanceof Named))) {
                return false;
            }
            return n.equals(((Named) (arg)).getName());
        }

        private String getName(InsnArg arg) {
            if (arg instanceof RegisterArg) {
                return "r";
            }
            if (arg instanceof Named) {
                return "n";
            }
            return arg.toString();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTernary3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("if (n == null || !(arg instanceof Named)) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("return n.equals(((Named) arg).getName());"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("if ((arg instanceof RegisterArg)) {")));
    }
}

