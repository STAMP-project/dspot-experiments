package org.cf.simplify.strategy;


import MethodState.ResultRegister;
import Opcode.INVOKE_DIRECT;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.type.VirtualClass;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeadRemovalStrategyTest {
    private static final String CLASS_NAME = "Ldead_removal_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DeadRemovalStrategyTest.class.getSimpleName());

    @Test
    public void doesNotDetectAssignmentReassignedInOnlyOneMultiverse() {
        String methodName = "reassignedInOnlyOneMultiverse(I)I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        Collections.sort(found);
        List<Integer> expected = new LinkedList<Integer>();
        Assert.assertEquals(expected, found);
    }

    @Test
    public void doesNotDetectInstanceInitializer() {
        VirtualMachine vm = VMTester.spawnVM(true);
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(DeadRemovalStrategyTest.CLASS_NAME);
        VMState initial = new VMState();
        initial.setRegisters(0, new org.cf.smalivm.type.UninitializedInstance(virtualClass), DeadRemovalStrategyTest.CLASS_NAME);
        String methodName = "<init>()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName, initial);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        strategy.perform();
        BuilderInstruction instruction = manipulator.getInstruction(0);
        Assert.assertEquals(INVOKE_DIRECT, instruction.getOpcode());
    }

    @Test
    public void doesNotDetectMoveOpWithOnlyOneRegisterReassigned() {
        String methodName = "moveP0IntoV0With30Locals(I)V";
        ExecutionGraphManipulator manipulator = DeadRemovalStrategyTest.getOptimizedGraph(methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        Assert.assertEquals(0, found.size());
    }

    @Test
    public void detectsDeadCodeWithStrongSideEffect() {
        String methodName = "deadCodeWithStrongSideEffect()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAddresses();
        Collections.sort(found);
        List<Integer> expected = Collections.singletonList(1);
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsDeadNopPadding() {
        String methodName = "hasNopPadding()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAddresses();
        List<Integer> expected = new LinkedList<Integer>();
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsDeadTryCatchBlock() {
        String methodName = "deadTryCatchBlock()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = Collections.singletonList(0);
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsUnusedResultOfMethodInvocationWithNoSideEffects() {
        String methodName = "unusedResultNoSideEffects()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadResultAddresses();
        List<Integer> expected = Collections.singletonList(1);
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsMoveResultWithUnusedAssignment() {
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, "moveResult()Ljava/lang/String;", ResultRegister, "some string", "Ljava/lang/String;");
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = Arrays.asList(3, 7);
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsSimpleDeadCode() {
        String methodName = "deadCode()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAddresses();
        Collections.sort(found);
        List<Integer> expected = Arrays.asList(2, 3, 4, 5);
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsUnusedAssignment() {
        String methodName = "unusedAssignment()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = Collections.singletonList(0);
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsUnusedResultOfMethodInvocationWithSideEffects() {
        String methodName = "unusedResultWithSideEffects()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getDeadAssignmentAddresses();
        List<Integer> expected = new LinkedList<Integer>();
        Assert.assertEquals(expected, found);
    }

    @Test
    public void detectsUselessGoto() {
        String methodName = "uselessGoto()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(DeadRemovalStrategyTest.CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        List<Integer> found = strategy.getUselessBranchAddresses();
        List<Integer> expected = Collections.singletonList(0);
        Assert.assertEquals(expected, found);
    }
}

