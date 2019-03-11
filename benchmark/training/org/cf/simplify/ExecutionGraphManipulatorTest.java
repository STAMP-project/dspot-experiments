package org.cf.simplify;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.ClassManager;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.junit.Assert;
import org.junit.Test;


public class ExecutionGraphManipulatorTest {
    private static final String CLASS_NAME = "Lexecution_graph_manipulator_test;";

    private ExecutionGraphManipulator manipulator;

    @Test
    public void addingInstructionModifiesStateCorrectly() {
        // @formatter:off
        Object[][] expected = new Object[][]{ new Object[]{ 0, Opcode.NOP, new Object[][][]{ new Object[][]{ new Object[]{ 1, Opcode.CONST_4 } } } }, new Object[]{ 1, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 2, Opcode.CONST_4 } } } }, new Object[]{ 2, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 3, Opcode.CONST_4 } } } }, new Object[]{ 3, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 4, Opcode.CONST_4 } } } }, new Object[]{ 4, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 5, Opcode.CONST_4 } } } }, new Object[]{ 5, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 6, Opcode.RETURN_VOID } } } }, new Object[]{ 6, Opcode.RETURN_VOID, new Object[1][0][0] } };
        // @formatter:on
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "verySimple()V");
        BuilderInstruction addition = new org.jf.dexlib2.builder.instruction.BuilderInstruction10x(Opcode.NOP);
        manipulator.addInstruction(0, addition);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 0);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 1);
    }

    @Test
    public void addingInstructionThatCausesNopPaddingToBeAddedModifiesStateCorrectly() {
        // @formatter:off
        Object[][] expected = new Object[][]{ new Object[]{ 0, Opcode.CONST_16, new Object[][][]{ new Object[][]{ new Object[]{ 2, Opcode.NEW_ARRAY } } } }, new Object[]{ 2, Opcode.NEW_ARRAY, new Object[][][]{ new Object[][]{ new Object[]{ 4, Opcode.NOP } } } }, new Object[]{ 4, Opcode.NOP, new Object[][][]{ new Object[][]{ new Object[]{ 5, Opcode.FILL_ARRAY_DATA } } } }, new Object[]{ 5, Opcode.FILL_ARRAY_DATA, new Object[][][]{ new Object[][]{ new Object[]{ 10, Opcode.ARRAY_PAYLOAD } } } }, new Object[]{ 8, Opcode.RETURN_VOID, new Object[1][0][0] }, new Object[]{ 9, Opcode.NOP, new Object[0][0][0] }, new Object[]{ 10, Opcode.ARRAY_PAYLOAD, new Object[][][]{ new Object[][]{ new Object[]{ 8, Opcode.RETURN_VOID } } } } };
        // @formatter:on
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "hasNoNopPadding()V");
        BuilderInstruction addition = new org.jf.dexlib2.builder.instruction.BuilderInstruction10x(Opcode.NOP);
        manipulator.addInstruction(4, addition);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 2);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 4);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 5);
    }

    @Test
    public void addingManyNopsAfterGotoModifiesStateCorrectly() {
        int nops_to_insert = 127;
        Object[][] expected = new Object[3 + nops_to_insert][];
        expected[0] = new Object[]{ 0, Opcode.GOTO_16, new Object[][][]{ new Object[][]{ new Object[]{ (2 + 1) + 127, Opcode.RETURN_VOID } } } };
        // No children, no node pile, these nops are dead code and never get executed
        expected[1] = new Object[]{ 2, Opcode.NOP, new Object[0][0][0] };
        for (int i = 0; i < nops_to_insert; i++) {
            int index = i + 2;
            expected[index] = new Object[]{ index + 1, Opcode.NOP, new Object[0][0][0] };
        }
        expected[129] = new Object[]{ 130, Opcode.RETURN_VOID, new Object[1][0][0] };
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "hasGotoAndOneNop()V");
        // Adding 126 bytes (nop) between goto and target offset causes dexlib to "fix" goto into goto/16
        for (int i = 0; i < (nops_to_insert - 1); i++) {
            manipulator.addInstruction(1, new org.jf.dexlib2.builder.instruction.BuilderInstruction10x(Opcode.NOP));
        }
        // Addresses 0 and 1 are now goto/16, need to insert at 2
        manipulator.addInstruction(2, new org.jf.dexlib2.builder.instruction.BuilderInstruction10x(Opcode.NOP));
        ExecutionGraphManipulatorTest.test(expected, manipulator);
    }

    @Test
    public void addingThenRemovingManyNopsAfterGotoModifiesStateCorrectly() {
        Object[][] expected = new Object[3][];
        expected[0] = new Object[]{ 0, Opcode.GOTO_16, new Object[][][]{ new Object[][]{ new Object[]{ 3, Opcode.RETURN_VOID } } } };
        expected[1] = new Object[]{ 2, Opcode.NOP, new Object[0][0][0] };
        expected[2] = new Object[]{ 3, Opcode.RETURN_VOID, new Object[1][0][0] };
        int nops_to_insert = 127;
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "hasGotoAndOneNop()V");
        // Adding 126 bytes (nop) between goto and target offset causes dexlib to "fix" goto into goto/16
        for (int i = 0; i < (nops_to_insert - 1); i++) {
            manipulator.addInstruction(1, new org.jf.dexlib2.builder.instruction.BuilderInstruction10x(Opcode.NOP));
        }
        // Addresses 0 and 1 are now goto/16, need to insert at 2
        manipulator.addInstruction(2, new org.jf.dexlib2.builder.instruction.BuilderInstruction10x(Opcode.NOP));
        List<Integer> removeList = new LinkedList<Integer>();
        // for (int removeAddress = 2, i = 0; i < nops_to_insert; removeAddress++, i++) {
        for (int i = 1; i < nops_to_insert; i++) {
            int removeAddress = i + 2;
            removeList.add(removeAddress);
        }
        manipulator.removeInstructions(removeList);
        manipulator.removeInstruction(2);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
    }

    @Test
    public void hasEveryRegisterAvailableAtEveryAddress() {
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "verySimple()V");
        int[] addresses = manipulator.getAddresses();
        for (int address : addresses) {
            int[] actualAvailable = manipulator.getAvailableRegisters(address);
            Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4 }, actualAvailable);
        }
    }

    @Test
    public void hasExpectedBasicProperties() {
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "verySimple()V");
        int[] expectedAddresses = new int[]{ 0, 1, 2, 3, 4, 5 };
        int[] actualAddresses = manipulator.getAddresses();
        Assert.assertArrayEquals(expectedAddresses, actualAddresses);
    }

    @Test
    public void removeInstructionThatCausesNopPaddingToBeRemovedAndHasParentWhichWModifiesStateCorrectly() {
        // @formatter:off
        Object[][] expected = new Object[][]{ new Object[]{ 0, Opcode.CONST_16, new Object[][][]{ new Object[][]{ new Object[]{ 2, Opcode.NEW_ARRAY } } } }, new Object[]{ 2, Opcode.NEW_ARRAY, new Object[][][]{ new Object[][]{ new Object[]{ 4, Opcode.FILL_ARRAY_DATA } } } }, new Object[]{ 4, Opcode.FILL_ARRAY_DATA, new Object[][][]{ new Object[][]{ new Object[]{ 8, Opcode.ARRAY_PAYLOAD } } } }, new Object[]{ 7, Opcode.RETURN_VOID, new Object[1][0][0] }, new Object[]{ 8, Opcode.ARRAY_PAYLOAD, new Object[][][]{ new Object[][]{ new Object[]{ 7, Opcode.RETURN_VOID } } } } };
        // @formatter:on
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "hasNopPadding()V");
        manipulator.removeInstruction(4);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 2);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 4);
    }

    @Test
    public void removeInstructionWithNoParentModifiesStateCorrectly() {
        // @formatter:off
        Object[][] expected = new Object[][]{ new Object[]{ 0, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 1, Opcode.CONST_4 } } } }, new Object[]{ 1, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 2, Opcode.CONST_4 } } } }, new Object[]{ 2, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 3, Opcode.CONST_4 } } } }, new Object[]{ 3, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 4, Opcode.RETURN_VOID } } } }, new Object[]{ 4, Opcode.RETURN_VOID, new Object[1][0][0] } };
        // @formatter:on
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "verySimple()V");
        manipulator.removeInstruction(0);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 0);
    }

    @Test
    public void removeInstructionWithParentModifiesStateCorrectly() {
        // @formatter:off
        Object[][] expected = new Object[][]{ new Object[]{ 0, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 1, Opcode.CONST_4 } } } }, new Object[]{ 1, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 2, Opcode.CONST_4 } } } }, new Object[]{ 2, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 3, Opcode.CONST_4 } } } }, new Object[]{ 3, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 4, Opcode.RETURN_VOID } } } }, new Object[]{ 4, Opcode.RETURN_VOID, new Object[1][0][0] } };
        // @formatter:on
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "verySimple()V");
        manipulator.removeInstruction(1);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 0);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 1);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 2);
        MethodState parentState = manipulator.getNodePile(0).get(0).getContext().getMethodState();
        Assert.assertArrayEquals(new int[]{ 0 }, parentState.getRegistersAssigned());
        MethodState childState = manipulator.getNodePile(1).get(0).getContext().getMethodState();
        Assert.assertArrayEquals(new int[]{ 2 }, childState.getRegistersAssigned());
        MethodState grandchildState = manipulator.getNodePile(2).get(0).getContext().getMethodState();
        Assert.assertArrayEquals(new int[]{ 3 }, grandchildState.getRegistersAssigned());
    }

    @Test
    public void replaceInstructionExecutesNewNodeCorrectly() {
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "constantPredicate()I");
        BuilderInstruction returnVoid = manipulator.getNodePile(4).get(0).getOp().getInstruction();
        Label target = returnVoid.getLocation().addNewLabel();
        // GOTO_32 shifts addresses around so mappings could break
        BuilderInstruction replacement = new org.jf.dexlib2.builder.instruction.BuilderInstruction30t(Opcode.GOTO_32, target);
        manipulator.replaceInstruction(1, replacement);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 0);
    }

    @Test
    public void emptyingATryBlockWithTwoHandlersWhichCreatesNullStartAndEndLocationsIsRemovedWithoutIncident() throws IOException {
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "tryBlockWithTwoCatches()V");
        Assert.assertEquals(2, manipulator.getTryBlocks().size());
        manipulator.removeInstruction(0);
        Assert.assertEquals(0, manipulator.getTryBlocks().size());
        // Exception is thrown when saving. Make sure doesn't happen.
        ClassManager classManager = manipulator.getVM().getClassManager();
        File out = File.createTempFile("test", "simplify");
        classManager.getDexBuilder().writeTo(new FileDataStore(out));
        out.delete();
    }

    @Test
    public void replacingInstructionWithDifferentOpcodeWidthModifiesStateCorrectly() {
        // @formatter:off
        Object[][] expected = new Object[][]{ new Object[]{ 0, Opcode.CONST_16, new Object[][][]{ new Object[][]{ new Object[]{ 2, Opcode.CONST_4 } } } }, new Object[]{ 2, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 3, Opcode.CONST_4 } } } }, new Object[]{ 3, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 4, Opcode.CONST_4 } } } }, new Object[]{ 4, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 5, Opcode.CONST_4 } } } }, new Object[]{ 5, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 6, Opcode.RETURN_VOID } } } }, new Object[]{ 6, Opcode.RETURN_VOID, new Object[1][0][0] } };
        // @formatter:on
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "verySimple()V");
        BuilderInstruction replacement = new org.jf.dexlib2.builder.instruction.BuilderInstruction21s(Opcode.CONST_16, 0, 0);
        manipulator.replaceInstruction(0, replacement);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
        HeapItem consensus = manipulator.getRegisterConsensus(0, 0);
        Assert.assertEquals(0, consensus.getValue());
    }

    @Test
    public void replaceInstructionWithMultipleModifiesStateCorrectly() {
        // @formatter:off
        Object[][] expected = new Object[][]{ new Object[]{ 0, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 1, Opcode.CONST_16 } } } }, new Object[]{ 1, Opcode.CONST_16, new Object[][][]{ new Object[][]{ new Object[]{ 3, Opcode.CONST_16 } } } }, new Object[]{ 3, Opcode.CONST_16, new Object[][][]{ new Object[][]{ new Object[]{ 5, Opcode.CONST_4 } } } }, new Object[]{ 5, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 6, Opcode.CONST_4 } } } }, new Object[]{ 6, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 7, Opcode.CONST_4 } } } }, new Object[]{ 7, Opcode.CONST_4, new Object[][][]{ new Object[][]{ new Object[]{ 8, Opcode.RETURN_VOID } } } }, new Object[]{ 8, Opcode.RETURN_VOID, new Object[1][0][0] } };
        // @formatter:on
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "verySimple()V");
        BuilderInstruction replacement1 = new org.jf.dexlib2.builder.instruction.BuilderInstruction21s(Opcode.CONST_16, 1, 1);
        BuilderInstruction replacement2 = new org.jf.dexlib2.builder.instruction.BuilderInstruction21s(Opcode.CONST_16, 2, 2);
        List<BuilderInstruction> replacements = new LinkedList<BuilderInstruction>();
        replacements.add(replacement1);
        replacements.add(replacement2);
        manipulator.replaceInstruction(1, replacements);
        ExecutionGraphManipulatorTest.test(expected, manipulator);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 0);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 1);
        ExecutionGraphManipulatorTest.testHeritage(manipulator, 3);
        HeapItem consensus;
        consensus = manipulator.getRegisterConsensus(1, 1);
        Assert.assertEquals(1, consensus.getValue());
        consensus = manipulator.getRegisterConsensus(3, 2);
        Assert.assertEquals(2, consensus.getValue());
    }

    @Test
    public void replacingInstructionGetsLabelsAtInsertionAddress() {
        manipulator = OptimizerTester.getGraphManipulator(ExecutionGraphManipulatorTest.CLASS_NAME, "hasLabelOnConstantizableOp(I)I");
        BuilderInstruction addition = new org.jf.dexlib2.builder.instruction.BuilderInstruction11n(Opcode.CONST_4, 0, 2);
        Assert.assertEquals(1, manipulator.getInstruction(3).getLocation().getLabels().size());
        manipulator.replaceInstruction(3, addition);
        Assert.assertEquals(1, manipulator.getInstruction(3).getLocation().getLabels().size());
    }
}

