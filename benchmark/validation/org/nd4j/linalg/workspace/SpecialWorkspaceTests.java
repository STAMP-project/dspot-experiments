package org.nd4j.linalg.workspace;


import AllocationPolicy.OVERALLOCATE;
import DataBuffer.Type;
import LearningPolicy.FIRST_LOOP;
import ResetPolicy.BLOCK_LEFT;
import ResetPolicy.ENDOFBUFFER_REACHED;
import SpillPolicy.EXTERNAL;
import SpillPolicy.REALLOCATE;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.memory.abstracts.Nd4jWorkspace;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class SpecialWorkspaceTests extends BaseNd4jTest {
    private Type initialType;

    public SpecialWorkspaceTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testVariableTimeSeries1() throws Exception {
        WorkspaceConfiguration configuration = WorkspaceConfiguration.builder().initialSize(0).overallocationLimit(3.0).policyAllocation(OVERALLOCATE).policySpill(EXTERNAL).policyLearning(FIRST_LOOP).policyReset(ENDOFBUFFER_REACHED).build();
        try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, "WS1")) {
            Nd4j.create(500);
            Nd4j.create(500);
        }
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread("WS1")));
        workspace.enableDebug(true);
        Assert.assertEquals(0, workspace.getStepNumber());
        long requiredMemory = 1000 * (Nd4j.sizeOfDataType());
        long shiftedSize = ((long) (requiredMemory * 1.3)) + (8 - (((long) (requiredMemory * 1.3)) % 8));
        Assert.assertEquals(requiredMemory, workspace.getSpilledSize());
        Assert.assertEquals(shiftedSize, workspace.getInitialBlockSize());
        Assert.assertEquals(((workspace.getInitialBlockSize()) * 4), workspace.getCurrentSize());
        try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace("WS1")) {
            Nd4j.create(2000);
        }
        Assert.assertEquals(0, workspace.getStepNumber());
        Assert.assertEquals((1000 * (Nd4j.sizeOfDataType())), workspace.getSpilledSize());
        Assert.assertEquals((2000 * (Nd4j.sizeOfDataType())), workspace.getPinnedSize());
        Assert.assertEquals(0, workspace.getDeviceOffset());
        // FIXME: fix this!
        // assertEquals(0, workspace.getHostOffset());
        Assert.assertEquals(0, workspace.getThisCycleAllocations());
        log.info("------------------");
        Assert.assertEquals(1, workspace.getNumberOfPinnedAllocations());
        for (int e = 0; e < 4; e++) {
            for (int i = 0; i < 4; i++) {
                try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, "WS1")) {
                    Nd4j.create(500);
                    Nd4j.create(500);
                }
                Assert.assertEquals(("Failed on iteration " + i), ((i + 1) * (workspace.getInitialBlockSize())), workspace.getDeviceOffset());
            }
            if (e >= 2) {
                Assert.assertEquals(("Failed on iteration " + e), 0, workspace.getNumberOfPinnedAllocations());
            } else {
                Assert.assertEquals(("Failed on iteration " + e), 1, workspace.getNumberOfPinnedAllocations());
            }
        }
        Assert.assertEquals(0, workspace.getSpilledSize());
        Assert.assertEquals(0, workspace.getPinnedSize());
        Assert.assertEquals(0, workspace.getNumberOfPinnedAllocations());
        Assert.assertEquals(0, workspace.getNumberOfExternalAllocations());
        log.info("Workspace state after first block: ---------------------------------------------------------");
        Nd4j.getWorkspaceManager().printAllocationStatisticsForCurrentThread();
        log.info("--------------------------------------------------------------------------------------------");
        // we just do huge loop now, with pinned stuff in it
        for (int i = 0; i < 100; i++) {
            try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, "WS1")) {
                Nd4j.create(500);
                Nd4j.create(500);
                Nd4j.create(500);
                Assert.assertEquals((1500 * (Nd4j.sizeOfDataType())), workspace.getThisCycleAllocations());
            }
        }
        Assert.assertEquals(0, workspace.getSpilledSize());
        Assert.assertNotEquals(0, workspace.getPinnedSize());
        Assert.assertNotEquals(0, workspace.getNumberOfPinnedAllocations());
        Assert.assertEquals(0, workspace.getNumberOfExternalAllocations());
        // and we do another clean loo, without pinned stuff in it, to ensure all pinned allocates are gone
        for (int i = 0; i < 100; i++) {
            try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, "WS1")) {
                Nd4j.create(500);
                Nd4j.create(500);
            }
        }
        Assert.assertEquals(0, workspace.getSpilledSize());
        Assert.assertEquals(0, workspace.getPinnedSize());
        Assert.assertEquals(0, workspace.getNumberOfPinnedAllocations());
        Assert.assertEquals(0, workspace.getNumberOfExternalAllocations());
        log.info("Workspace state after second block: ---------------------------------------------------------");
        Nd4j.getWorkspaceManager().printAllocationStatisticsForCurrentThread();
    }

    @Test
    public void testVariableTimeSeries2() throws Exception {
        WorkspaceConfiguration configuration = WorkspaceConfiguration.builder().initialSize(0).overallocationLimit(3.0).policyAllocation(OVERALLOCATE).policySpill(REALLOCATE).policyLearning(FIRST_LOOP).policyReset(ENDOFBUFFER_REACHED).build();
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(configuration, "WS1")));
        workspace.enableDebug(true);
        try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, "WS1")) {
            Nd4j.create(500);
            Nd4j.create(500);
        }
        Assert.assertEquals(0, workspace.getStepNumber());
        long requiredMemory = 1000 * (Nd4j.sizeOfDataType());
        long shiftedSize = ((long) (requiredMemory * 1.3)) + (8 - (((long) (requiredMemory * 1.3)) % 8));
        Assert.assertEquals(requiredMemory, workspace.getSpilledSize());
        Assert.assertEquals(shiftedSize, workspace.getInitialBlockSize());
        Assert.assertEquals(((workspace.getInitialBlockSize()) * 4), workspace.getCurrentSize());
        for (int i = 0; i < 100; i++) {
            try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, "WS1")) {
                Nd4j.create(500);
                Nd4j.create(500);
                Nd4j.create(500);
            }
        }
        Assert.assertEquals(((workspace.getInitialBlockSize()) * 4), workspace.getCurrentSize());
        Assert.assertEquals(0, workspace.getNumberOfPinnedAllocations());
        Assert.assertEquals(0, workspace.getNumberOfExternalAllocations());
        Assert.assertEquals(0, workspace.getSpilledSize());
        Assert.assertEquals(0, workspace.getPinnedSize());
    }

    @Test
    public void testViewDetach_1() throws Exception {
        WorkspaceConfiguration configuration = WorkspaceConfiguration.builder().initialSize(10000000).overallocationLimit(3.0).policyAllocation(OVERALLOCATE).policySpill(REALLOCATE).policyLearning(FIRST_LOOP).policyReset(BLOCK_LEFT).build();
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(configuration, "WS109")));
        INDArray row = Nd4j.linspace(1, 10, 10);
        INDArray exp = Nd4j.create(1, 10).assign(2.0);
        INDArray result = null;
        try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, "WS109")) {
            INDArray matrix = Nd4j.create(10, 10);
            for (int e = 0; e < (matrix.rows()); e++)
                matrix.getRow(e).assign(row);

            INDArray column = matrix.getColumn(1);
            Assert.assertTrue(column.isView());
            Assert.assertTrue(column.isAttached());
            result = column.detach();
        }
        Assert.assertFalse(result.isView());
        Assert.assertFalse(result.isAttached());
        Assert.assertEquals(exp, result);
    }
}

