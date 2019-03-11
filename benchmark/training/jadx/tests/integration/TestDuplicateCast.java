package jadx.tests.integration;


import InsnType.CHECK_CAST;
import InsnType.RETURN;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.tests.api.IntegrationTest;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test duplicate 'check-cast' instruction produced because of bug in javac:
 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6246854
 */
public class TestDuplicateCast extends IntegrationTest {
    public static class TestCls {
        public int[] method(Object o) {
            return ((int[]) (o));
        }
    }

    @Test
    public void test() {
        dontUnloadClass();
        ClassNode cls = getClassNode(TestDuplicateCast.TestCls.class);
        MethodNode mth = getMethod(cls, "method");
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("return (int[]) o;"));
        List<InsnNode> insns = mth.getBasicBlocks().get(1).getInstructions();
        Assert.assertThat(insns, Matchers.hasSize(1));
        InsnNode insnNode = insns.get(0);
        Assert.assertThat(insnNode.getType(), Matchers.is(RETURN));
        Assert.assertTrue(insnNode.getArg(0).isInsnWrap());
        InsnNode wrapInsn = getWrapInsn();
        Assert.assertThat(wrapInsn.getType(), Matchers.is(CHECK_CAST));
        Assert.assertFalse(wrapInsn.getArg(0).isInsnWrap());
    }
}

