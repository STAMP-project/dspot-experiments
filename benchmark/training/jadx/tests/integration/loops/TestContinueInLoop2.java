package jadx.tests.integration.loops;


import AType.CATCH_BLOCK;
import AType.EXC_HANDLER;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.trycatch.CatchAttr;
import jadx.core.dex.trycatch.ExcHandlerAttr;
import jadx.core.dex.trycatch.ExceptionHandler;
import jadx.core.dex.trycatch.TryCatchBlock;
import jadx.core.utils.BlockUtils;
import jadx.core.utils.InstructionRemover;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestContinueInLoop2 extends IntegrationTest {
    public static class TestCls {
        private static void test(MethodNode mth, BlockNode block) {
            ExcHandlerAttr handlerAttr = block.get(EXC_HANDLER);
            if (handlerAttr != null) {
                ExceptionHandler excHandler = handlerAttr.getHandler();
                excHandler.addBlock(block);
                for (BlockNode node : BlockUtils.collectBlocksDominatedBy(block, block)) {
                    excHandler.addBlock(node);
                }
                for (BlockNode excBlock : excHandler.getBlocks()) {
                    InstructionRemover remover = new InstructionRemover(mth, excBlock);
                    for (InsnNode insn : excBlock.getInstructions()) {
                        if ((insn.getType()) == (InsnType.MONITOR_ENTER)) {
                            break;
                        }
                        if ((insn.getType()) == (InsnType.MONITOR_EXIT)) {
                            remover.add(insn);
                        }
                    }
                    remover.perform();
                    for (InsnNode insn : excBlock.getInstructions()) {
                        if ((insn.getType()) == (InsnType.THROW)) {
                            CatchAttr catchAttr = insn.get(CATCH_BLOCK);
                            if (catchAttr != null) {
                                TryCatchBlock handlerBlock = handlerAttr.getTryBlock();
                                TryCatchBlock catchBlock = catchAttr.getTryBlock();
                                if (handlerBlock != catchBlock) {
                                    handlerBlock.merge(mth, catchBlock);
                                    catchBlock.removeInsn(mth, insn);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestContinueInLoop2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("break;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("continue;")));
    }
}

