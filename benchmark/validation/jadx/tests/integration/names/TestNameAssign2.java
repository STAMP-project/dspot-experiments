package jadx.tests.integration.names;


import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.visitors.ssa.LiveVarAnalysis;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.BitSet;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestNameAssign2 extends IntegrationTest {
    public static class TestCls {
        private static void test(MethodNode mth, int regNum, LiveVarAnalysis la) {
            List<BlockNode> blocks = mth.getBasicBlocks();
            int blocksCount = blocks.size();
            BitSet hasPhi = new BitSet(blocksCount);
            BitSet processed = new BitSet(blocksCount);
            Deque<BlockNode> workList = new LinkedList<>();
            BitSet assignBlocks = la.getAssignBlocks(regNum);
            for (int id = assignBlocks.nextSetBit(0); id >= 0; id = assignBlocks.nextSetBit((id + 1))) {
                processed.set(id);
                workList.add(blocks.get(id));
            }
            while (!(workList.isEmpty())) {
                BlockNode block = workList.pop();
                BitSet domFrontier = block.getDomFrontier();
                for (int id = domFrontier.nextSetBit(0); id >= 0; id = domFrontier.nextSetBit((id + 1))) {
                    if ((!(hasPhi.get(id))) && (la.isLive(id, regNum))) {
                        BlockNode df = blocks.get(id);
                        TestNameAssign2.TestCls.addPhi(df, regNum);
                        hasPhi.set(id);
                        if (!(processed.get(id))) {
                            processed.set(id);
                            workList.add(df);
                        }
                    }
                }
            } 
        }

        private static void addPhi(BlockNode df, int regNum) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestNameAssign2.TestCls.class);
        String code = cls.getCode().toString();
        // TODO:
        Assert.assertThat(code, JadxMatchers.containsOne("int id;"));
    }
}

