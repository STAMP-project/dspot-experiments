package org.nd4j.linalg.workspace;


import AllocationPolicy.OVERALLOCATE;
import AllocationPolicy.STRICT;
import DataBuffer.Type;
import LearningPolicy.FIRST_LOOP;
import LearningPolicy.NONE;
import LearningPolicy.OVER_TIME;
import LocationPolicy.MMAP;
import MirroringPolicy.FULL;
import SpillPolicy.EXTERNAL;
import SpillPolicy.FAIL;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
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
public class BasicWorkspaceTests extends BaseNd4jTest {
    Type initialType;

    private static final WorkspaceConfiguration basicConfig = WorkspaceConfiguration.builder().initialSize(((10 * 1024) * 1024)).maxSize(((10 * 1024) * 1024)).overallocationLimit(0.1).policyAllocation(STRICT).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();

    private static final WorkspaceConfiguration loopOverTimeConfig = WorkspaceConfiguration.builder().initialSize(0).maxSize(((10 * 1024) * 1024)).overallocationLimit(0.1).policyAllocation(STRICT).policyLearning(OVER_TIME).policyMirroring(FULL).policySpill(EXTERNAL).build();

    private static final WorkspaceConfiguration loopFirstConfig = WorkspaceConfiguration.builder().initialSize(0).maxSize(((10 * 1024) * 1024)).overallocationLimit(0.1).policyAllocation(STRICT).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();

    public BasicWorkspaceTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testCold() throws Exception {
        INDArray array = Nd4j.create(10);
        array.addi(1.0);
        Assert.assertEquals(10.0F, array.sumNumber().floatValue(), 0.01F);
    }

    @Test
    public void testMinSize1() throws Exception {
        WorkspaceConfiguration conf = WorkspaceConfiguration.builder().minSize(((10 * 1024) * 1024)).overallocationLimit(1.0).policyAllocation(OVERALLOCATE).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();
        try (Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(conf, "WT")))) {
            INDArray array = Nd4j.create(100);
            Assert.assertEquals(0, workspace.getCurrentSize());
        }
        try (Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(conf, "WT")))) {
            INDArray array = Nd4j.create(100);
            Assert.assertEquals(((10 * 1024) * 1024), workspace.getCurrentSize());
        }
    }

    @Test
    public void testBreakout2() throws Exception {
        Assert.assertEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        INDArray scoped = outScope2();
        Assert.assertEquals(null, scoped);
        Assert.assertEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
    }

    @Test
    public void testBreakout1() throws Exception {
        Assert.assertEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        INDArray scoped = outScope1();
        Assert.assertEquals(true, scoped.isAttached());
        Assert.assertEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
    }

    @Test
    public void testLeverage3() throws Exception {
        try (Nd4jWorkspace wsOne = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "EXT")))) {
            INDArray array = null;
            try (Nd4jWorkspace wsTwo = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "INT")))) {
                INDArray matrix = Nd4j.create(32, 1, 40);
                INDArray view = matrix.tensorAlongDimension(0, 1, 2);
                view.assign(1.0F);
                Assert.assertEquals(40.0F, matrix.sumNumber().floatValue(), 0.01F);
                Assert.assertEquals(40.0F, view.sumNumber().floatValue(), 0.01F);
                array = view.leverageTo("EXT");
            }
            Assert.assertEquals(40.0F, array.sumNumber().floatValue(), 0.01F);
        }
    }

    @Test
    public void testLeverageTo2() throws Exception {
        try (Nd4jWorkspace wsOne = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.loopOverTimeConfig, "EXT")))) {
            INDArray array1 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
            INDArray array3 = null;
            try (Nd4jWorkspace wsTwo = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "INT")))) {
                INDArray array2 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
                long reqMemory = 5 * (Nd4j.sizeOfDataType());
                array3 = array2.leverageTo("EXT");
                Assert.assertEquals(0, wsOne.getCurrentSize());
                Assert.assertEquals(15.0F, array3.sumNumber().floatValue(), 0.01F);
            }
            try (Nd4jWorkspace wsTwo = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "INT")))) {
                INDArray array2 = Nd4j.create(100);
            }
            Assert.assertEquals(15.0F, array3.sumNumber().floatValue(), 0.01F);
        }
    }

    @Test
    public void testLeverageTo1() throws Exception {
        try (Nd4jWorkspace wsOne = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "EXT")))) {
            INDArray array1 = Nd4j.create(5);
            try (Nd4jWorkspace wsTwo = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "INT")))) {
                INDArray array2 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
                long reqMemory = 5 * (Nd4j.sizeOfDataType());
                Assert.assertEquals((reqMemory + (reqMemory % 8)), wsOne.getHostOffset());
                array2.leverageTo("EXT");
                Assert.assertEquals(((reqMemory + (reqMemory % 8)) * 2), wsOne.getHostOffset());
            }
        }
    }

    @Test
    public void testOutOfScope1() throws Exception {
        try (Nd4jWorkspace wsOne = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "EXT")))) {
            INDArray array1 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
            long reqMemory = 5 * (Nd4j.sizeOfDataType());
            Assert.assertEquals((reqMemory + (reqMemory % 8)), wsOne.getHostOffset());
            INDArray array2;
            try (MemoryWorkspace workspace = Nd4j.getMemoryManager().scopeOutOfWorkspaces()) {
                array2 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
            }
            Assert.assertFalse(array2.isAttached());
            log.info("Current workspace: {}", Nd4j.getMemoryManager().getCurrentWorkspace());
            Assert.assertTrue((wsOne == (Nd4j.getMemoryManager().getCurrentWorkspace())));
            INDArray array3 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
            reqMemory = 5 * (Nd4j.sizeOfDataType());
            Assert.assertEquals(((reqMemory + (reqMemory % 8)) * 2), wsOne.getHostOffset());
            array1.addi(array2);
            Assert.assertEquals(30.0F, array1.sumNumber().floatValue(), 0.01F);
        }
    }

    @Test
    public void testLeverage1() throws Exception {
        try (Nd4jWorkspace wsOne = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "EXT")))) {
            Assert.assertEquals(0, wsOne.getHostOffset());
            try (Nd4jWorkspace wsTwo = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "INT")))) {
                INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
                Assert.assertEquals(0, wsOne.getHostOffset());
                long reqMemory = 5 * (Nd4j.sizeOfDataType());
                Assert.assertEquals((reqMemory + (reqMemory % 8)), wsTwo.getHostOffset());
                INDArray copy = array.leverage();
                Assert.assertEquals((reqMemory + (reqMemory % 8)), wsTwo.getHostOffset());
                Assert.assertEquals((reqMemory + (reqMemory % 8)), wsOne.getHostOffset());
                Assert.assertNotEquals(null, copy);
                Assert.assertTrue(copy.isAttached());
                Assert.assertEquals(15.0F, copy.sumNumber().floatValue(), 0.01F);
            }
        }
    }

    @Test
    public void testNoShape1() {
        int outDepth = 50;
        int miniBatch = 64;
        int outH = 8;
        int outW = 8;
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "ITER")))) {
            INDArray delta = Nd4j.create(new int[]{ 50, 64, 8, 8 }, new int[]{ 64, 3200, 8, 1 }, 'c');
            delta = delta.permute(1, 0, 2, 3);
            BaseNd4jTest.assertArrayEquals(new int[]{ 64, 50, 8, 8 }, delta.shape());
            BaseNd4jTest.assertArrayEquals(new int[]{ 3200, 64, 8, 1 }, delta.stride());
            INDArray delta2d = Shape.newShapeNoCopy(delta, new int[]{ outDepth, (miniBatch * outH) * outW }, false);
            Assert.assertNotNull(delta2d);
        }
    }

    @Test
    public void testCreateDetached1() throws Exception {
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "ITER")))) {
            INDArray array1 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
            INDArray array2 = Nd4j.createUninitializedDetached(5);
            array2.assign(array1);
            long reqMemory = 5 * (Nd4j.sizeOfDataType());
            Assert.assertEquals((reqMemory + (reqMemory % 8)), wsI.getHostOffset());
            Assert.assertEquals(array1, array2);
        }
    }

    @Test
    public void testDetach1() throws Exception {
        INDArray array = null;
        INDArray copy = null;
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "ITER")))) {
            array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
            // despite we're allocating this array in workspace, it's empty yet, so it's external allocation
            Assert.assertTrue(array.isInScope());
            Assert.assertTrue(array.isAttached());
            long reqMemory = 5 * (Nd4j.sizeOfDataType());
            Assert.assertEquals((reqMemory + (reqMemory % 8)), wsI.getHostOffset());
            copy = array.detach();
            Assert.assertTrue(array.isInScope());
            Assert.assertTrue(array.isAttached());
            Assert.assertEquals((reqMemory + (reqMemory % 8)), wsI.getHostOffset());
            Assert.assertFalse(copy.isAttached());
            Assert.assertTrue(copy.isInScope());
            Assert.assertEquals((reqMemory + (reqMemory % 8)), wsI.getHostOffset());
        }
        Assert.assertEquals(15.0F, copy.sumNumber().floatValue(), 0.01F);
        Assert.assertFalse((array == copy));
    }

    @Test
    public void testScope2() throws Exception {
        INDArray array = null;
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.loopFirstConfig, "ITER")))) {
            array = Nd4j.create(100);
            // despite we're allocating this array in workspace, it's empty yet, so it's external allocation
            Assert.assertTrue(array.isInScope());
            Assert.assertEquals(0, wsI.getCurrentSize());
        }
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.loopFirstConfig, "ITER")))) {
            array = Nd4j.create(100);
            Assert.assertTrue(array.isInScope());
            Assert.assertEquals((100 * (Nd4j.sizeOfDataType())), wsI.getHostOffset());
        }
        Assert.assertFalse(array.isInScope());
    }

    @Test
    public void testScope1() throws Exception {
        INDArray array = null;
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "ITER")))) {
            array = Nd4j.create(100);
            Assert.assertTrue(array.isInScope());
        }
        Assert.assertFalse(array.isInScope());
    }

    @Test
    public void testIsAttached3() {
        INDArray array = Nd4j.create(100);
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "ITER")))) {
            INDArray arrayL = array.leverageTo("ITER");
            Assert.assertFalse(array.isAttached());
            Assert.assertFalse(arrayL.isAttached());
        }
        INDArray array2 = Nd4j.create(100);
        Assert.assertFalse(array.isAttached());
        Assert.assertFalse(array2.isAttached());
    }

    @Test
    public void testIsAttached2() {
        INDArray array = Nd4j.create(100);
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.loopFirstConfig, "ITER")))) {
            INDArray arrayL = array.leverageTo("ITER");
            Assert.assertFalse(array.isAttached());
            Assert.assertFalse(arrayL.isAttached());
        }
        INDArray array2 = Nd4j.create(100);
        Assert.assertFalse(array.isAttached());
        Assert.assertFalse(array2.isAttached());
    }

    @Test
    public void testIsAttached1() {
        try (Nd4jWorkspace wsI = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.loopFirstConfig, "ITER")))) {
            INDArray array = Nd4j.create(100);
            Assert.assertTrue(array.isAttached());
        }
        INDArray array = Nd4j.create(100);
        Assert.assertFalse(array.isAttached());
    }

    @Test
    public void testOverallocation3() throws Exception {
        WorkspaceConfiguration overallocationConfig = WorkspaceConfiguration.builder().initialSize(0).maxSize(((10 * 1024) * 1024)).overallocationLimit(1.0).policyAllocation(OVERALLOCATE).policyLearning(OVER_TIME).policyMirroring(FULL).policySpill(EXTERNAL).build();
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(overallocationConfig)));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertEquals(0, workspace.getCurrentSize());
        for (int x = 10; x <= 100; x += 10) {
            try (MemoryWorkspace cW = workspace.notifyScopeEntered()) {
                INDArray array = Nd4j.create(x);
            }
        }
        Assert.assertEquals(0, workspace.getCurrentSize());
        workspace.initializeWorkspace();
        // should be 800 = 100 elements * 4 bytes per element * 2 as overallocation coefficient
        Assert.assertEquals((200 * (Nd4j.sizeOfDataType())), workspace.getCurrentSize());
    }

    @Test
    public void testOverallocation2() throws Exception {
        WorkspaceConfiguration overallocationConfig = WorkspaceConfiguration.builder().initialSize(0).maxSize(((10 * 1024) * 1024)).overallocationLimit(1.0).policyAllocation(OVERALLOCATE).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(overallocationConfig)));
        // Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertEquals(0, workspace.getCurrentSize());
        try (MemoryWorkspace cW = workspace.notifyScopeEntered()) {
            INDArray array = Nd4j.create(100);
        }
        // should be 800 = 100 elements * 4 bytes per element * 2 as overallocation coefficient
        Assert.assertEquals((200 * (Nd4j.sizeOfDataType())), workspace.getCurrentSize());
    }

    @Test
    public void testOverallocation1() throws Exception {
        WorkspaceConfiguration overallocationConfig = WorkspaceConfiguration.builder().initialSize(1024).maxSize(((10 * 1024) * 1024)).overallocationLimit(1.0).policyAllocation(OVERALLOCATE).policyLearning(NONE).policyMirroring(FULL).policySpill(EXTERNAL).build();
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(overallocationConfig)));
        Assert.assertEquals(2048, workspace.getCurrentSize());
    }

    @Test
    public void testToggle1() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(BasicWorkspaceTests.loopFirstConfig)));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        try (MemoryWorkspace cW = workspace.notifyScopeEntered()) {
            INDArray array1 = Nd4j.create(100);
            cW.toggleWorkspaceUse(false);
            INDArray arrayDetached = Nd4j.create(100);
            arrayDetached.assign(1.0F);
            double sum = arrayDetached.sumNumber().doubleValue();
            Assert.assertEquals(100.0F, sum, 0.01);
            cW.toggleWorkspaceUse(true);
            INDArray array2 = Nd4j.create(100);
        }
        Assert.assertEquals(0, workspace.getHostOffset());
        Assert.assertEquals((200 * (Nd4j.sizeOfDataType())), workspace.getCurrentSize());
        log.info("--------------------------");
        try (MemoryWorkspace cW = workspace.notifyScopeEntered()) {
            INDArray array1 = Nd4j.create(100);
            cW.toggleWorkspaceUse(false);
            INDArray arrayDetached = Nd4j.create(100);
            arrayDetached.assign(1.0F);
            double sum = arrayDetached.sumNumber().doubleValue();
            Assert.assertEquals(100.0F, sum, 0.01);
            cW.toggleWorkspaceUse(true);
            Assert.assertEquals((100 * (Nd4j.sizeOfDataType())), workspace.getHostOffset());
            INDArray array2 = Nd4j.create(100);
            Assert.assertEquals((200 * (Nd4j.sizeOfDataType())), workspace.getHostOffset());
        }
    }

    @Test
    public void testLoop4() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(BasicWorkspaceTests.loopFirstConfig)));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        try (MemoryWorkspace cW = workspace.notifyScopeEntered()) {
            INDArray array1 = Nd4j.create(100);
            INDArray array2 = Nd4j.create(100);
        }
        Assert.assertEquals(0, workspace.getHostOffset());
        Assert.assertEquals((200 * (Nd4j.sizeOfDataType())), workspace.getCurrentSize());
        try (MemoryWorkspace cW = workspace.notifyScopeEntered()) {
            INDArray array1 = Nd4j.create(100);
            Assert.assertEquals((100 * (Nd4j.sizeOfDataType())), workspace.getHostOffset());
        }
        Assert.assertEquals(0, workspace.getHostOffset());
    }

    @Test
    public void testLoops3() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(BasicWorkspaceTests.loopFirstConfig)));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        workspace.notifyScopeEntered();
        INDArray arrayCold1 = Nd4j.create(100);
        INDArray arrayCold2 = Nd4j.create(10);
        Assert.assertEquals(0, workspace.getHostOffset());
        Assert.assertEquals(0, workspace.getCurrentSize());
        workspace.notifyScopeLeft();
        Assert.assertEquals(0, workspace.getHostOffset());
        long reqMem = 110 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getCurrentSize());
    }

    @Test
    public void testLoops2() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(BasicWorkspaceTests.loopOverTimeConfig)));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        for (int x = 1; x <= 100; x++) {
            workspace.notifyScopeEntered();
            INDArray arrayCold = Nd4j.create(x);
            Assert.assertEquals(0, workspace.getHostOffset());
            Assert.assertEquals(0, workspace.getCurrentSize());
            workspace.notifyScopeLeft();
        }
        workspace.initializeWorkspace();
        long reqMem = 100 * (Nd4j.sizeOfDataType());
        // assertEquals(reqMem + reqMem % 8, workspace.getCurrentSize());
        Assert.assertEquals(0, workspace.getHostOffset());
        workspace.notifyScopeEntered();
        INDArray arrayHot = Nd4j.create(10);
        reqMem = 10 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
        workspace.notifyScopeLeft();
    }

    @Test
    public void testLoops1() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(BasicWorkspaceTests.loopOverTimeConfig)));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        workspace.notifyScopeEntered();
        INDArray arrayCold = Nd4j.create(10);
        Assert.assertEquals(0, workspace.getHostOffset());
        Assert.assertEquals(0, workspace.getCurrentSize());
        arrayCold.assign(1.0F);
        Assert.assertEquals(10.0F, arrayCold.sumNumber().floatValue(), 0.01F);
        workspace.notifyScopeLeft();
        workspace.initializeWorkspace();
        long reqMemory = 11 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMemory + (reqMemory % 8)), workspace.getCurrentSize());
        log.info("-----------------------");
        for (int x = 0; x < 10; x++) {
            Assert.assertEquals(0, workspace.getHostOffset());
            workspace.notifyScopeEntered();
            INDArray array = Nd4j.create(10);
            long reqMem = 10 * (Nd4j.sizeOfDataType());
            Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
            array.addi(1.0F);
            Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
            Assert.assertEquals(("Failed on iteration " + x), 10, array.sumNumber().doubleValue(), 0.01);
            workspace.notifyScopeLeft();
            Assert.assertEquals(0, workspace.getHostOffset());
        }
    }

    @Test
    public void testAllocation6() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "testAllocation6")));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        INDArray array = Nd4j.rand(100, 10, 10);
        // checking if allocation actually happened
        Assert.assertEquals((1000 * (Nd4j.sizeOfDataType())), workspace.getHostOffset());
        INDArray dup = array.dup();
        Assert.assertEquals((2000 * (Nd4j.sizeOfDataType())), workspace.getHostOffset());
        // assertEquals(5, dup.sumNumber().doubleValue(), 0.01);
        workspace.close();
    }

    @Test
    public void testAllocation5() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "testAllocation5")));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        INDArray array = Nd4j.create(new int[]{ 1, 5 }, 'c');
        // checking if allocation actually happened
        long reqMemory = 5 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMemory + (reqMemory % 8)), workspace.getHostOffset());
        array.assign(1.0F);
        INDArray dup = array.dup();
        Assert.assertEquals(((reqMemory + (reqMemory % 8)) * 2), workspace.getHostOffset());
        Assert.assertEquals(5, dup.sumNumber().doubleValue(), 0.01);
        workspace.close();
    }

    @Test
    public void testAllocation4() throws Exception {
        WorkspaceConfiguration failConfig = WorkspaceConfiguration.builder().initialSize((1024 * 1024)).maxSize((1024 * 1024)).overallocationLimit(0.1).policyAllocation(STRICT).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(FAIL).build();
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().createNewWorkspace(failConfig)));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        INDArray array = Nd4j.create(new int[]{ 1, 5 }, 'c');
        // checking if allocation actually happened
        long reqMem = 5 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
        try {
            INDArray array2 = Nd4j.create(10000000);
            Assert.assertTrue(false);
        } catch (ND4JIllegalStateException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
        INDArray array2 = Nd4j.create(new int[]{ 1, 5 }, 'c');
        Assert.assertEquals(((reqMem + (reqMem % 8)) * 2), workspace.getHostOffset());
    }

    @Test
    public void testAllocation3() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "testAllocation2")));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        INDArray array = Nd4j.create(new int[]{ 1, 5 }, 'c');
        // checking if allocation actually happened
        long reqMem = 5 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
        array.assign(1.0F);
        Assert.assertEquals(5, array.sumNumber().doubleValue(), 0.01);
        workspace.close();
    }

    @Test
    public void testAllocation2() throws Exception {
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "testAllocation2")));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        INDArray array = Nd4j.create(5);
        // checking if allocation actually happened
        long reqMem = 5 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
        array.assign(1.0F);
        Assert.assertEquals(5, array.sumNumber().doubleValue(), 0.01);
        workspace.close();
    }

    @Test
    public void testAllocation1() throws Exception {
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        Nd4jWorkspace workspace = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(BasicWorkspaceTests.basicConfig, "TestAllocation1")));
        Nd4j.getMemoryManager().setCurrentWorkspace(workspace);
        Assert.assertNotEquals(null, Nd4j.getMemoryManager().getCurrentWorkspace());
        Assert.assertEquals(0, workspace.getHostOffset());
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        // checking if allocation actually happened
        long reqMem = 5 * (Nd4j.sizeOfDataType());
        Assert.assertEquals((reqMem + (reqMem % 8)), workspace.getHostOffset());
        Assert.assertEquals(exp, array);
        // checking stuff at native side
        double sum = array.sumNumber().doubleValue();
        Assert.assertEquals(15.0, sum, 0.01);
        // checking INDArray validity
        Assert.assertEquals(1.0, array.getFloat(0), 0.01);
        Assert.assertEquals(2.0, array.getFloat(1), 0.01);
        Assert.assertEquals(3.0, array.getFloat(2), 0.01);
        Assert.assertEquals(4.0, array.getFloat(3), 0.01);
        Assert.assertEquals(5.0, array.getFloat(4), 0.01);
        // checking INDArray validity
        Assert.assertEquals(1.0, array.getDouble(0), 0.01);
        Assert.assertEquals(2.0, array.getDouble(1), 0.01);
        Assert.assertEquals(3.0, array.getDouble(2), 0.01);
        Assert.assertEquals(4.0, array.getDouble(3), 0.01);
        Assert.assertEquals(5.0, array.getDouble(4), 0.01);
        // checking workspace memory space
        INDArray array2 = Nd4j.create(new float[]{ 5.0F, 4.0F, 3.0F, 2.0F, 1.0F });
        sum = array2.sumNumber().doubleValue();
        Assert.assertEquals(15.0, sum, 0.01);
        // 44 = 20 + 4 + 20, 4 was allocated as Op.extraArgs for sum
        // assertEquals(44, workspace.getHostOffset());
        array.addi(array2);
        sum = array.sumNumber().doubleValue();
        Assert.assertEquals(30.0, sum, 0.01);
        // checking INDArray validity
        Assert.assertEquals(6.0, array.getFloat(0), 0.01);
        Assert.assertEquals(6.0, array.getFloat(1), 0.01);
        Assert.assertEquals(6.0, array.getFloat(2), 0.01);
        Assert.assertEquals(6.0, array.getFloat(3), 0.01);
        Assert.assertEquals(6.0, array.getFloat(4), 0.01);
        workspace.close();
    }

    @Test
    public void testMmap1() throws Exception {
        // we don't support MMAP on cuda yet
        if (Nd4j.getExecutioner().getClass().getName().toLowerCase().contains("cuda"))
            return;

        WorkspaceConfiguration mmap = WorkspaceConfiguration.builder().initialSize(1000000).policyLocation(MMAP).build();
        MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(mmap, "M2");
        INDArray mArray = Nd4j.create(100);
        mArray.assign(10.0F);
        Assert.assertEquals(1000.0F, mArray.sumNumber().floatValue(), 1.0E-5);
        ws.close();
        ws.notifyScopeEntered();
        INDArray mArrayR = Nd4j.createUninitialized(100);
        Assert.assertEquals(1000.0F, mArrayR.sumNumber().floatValue(), 1.0E-5);
        ws.close();
    }

    @Test
    public void testMmap2() throws Exception {
        // we don't support MMAP on cuda yet
        if (Nd4j.getExecutioner().getClass().getName().toLowerCase().contains("cuda"))
            return;

        File tmp = File.createTempFile("tmp", "fdsfdf");
        tmp.deleteOnExit();
        Nd4jWorkspace.fillFile(tmp, 100000);
        WorkspaceConfiguration mmap = WorkspaceConfiguration.builder().policyLocation(MMAP).tempFilePath(tmp.getAbsolutePath()).build();
        MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(mmap, "M3");
        INDArray mArray = Nd4j.create(100);
        mArray.assign(10.0F);
        Assert.assertEquals(1000.0F, mArray.sumNumber().floatValue(), 1.0E-5);
        ws.notifyScopeLeft();
    }

    @Test
    public void testInvalidLeverageMigrateDetach() {
        try {
            MemoryWorkspace ws = Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(BasicWorkspaceTests.basicConfig, "testInvalidLeverage");
            INDArray invalidArray = null;
            for (int i = 0; i < 10; i++) {
                try (MemoryWorkspace ws2 = ws.notifyScopeEntered()) {
                    invalidArray = Nd4j.linspace(1, 10, 10);
                }
            }
            Assert.assertTrue(invalidArray.isAttached());
            MemoryWorkspace ws2 = Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(BasicWorkspaceTests.basicConfig, "testInvalidLeverage2");
            // Leverage
            try (MemoryWorkspace ws3 = ws2.notifyScopeEntered()) {
                invalidArray.leverage();
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                // Expected exception
                e.printStackTrace();
            }
            try (MemoryWorkspace ws3 = ws2.notifyScopeEntered()) {
                invalidArray.leverageTo("testInvalidLeverage2");
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                // Expected exception
                e.printStackTrace();
            }
            try (MemoryWorkspace ws3 = ws2.notifyScopeEntered()) {
                invalidArray.leverageOrDetach("testInvalidLeverage2");
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                // Expected exception
                e.printStackTrace();
            }
            try {
                invalidArray.leverageTo("testInvalidLeverage2");
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                // Expected exception
                e.printStackTrace();
            }
            // Detach
            try {
                invalidArray.detach();
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                e.printStackTrace();
            }
            // Migrate
            try (MemoryWorkspace ws3 = ws2.notifyScopeEntered()) {
                invalidArray.migrate();
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                // Expected exception
                e.printStackTrace();
            }
            try {
                invalidArray.migrate(true);
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                // Expected exception
                e.printStackTrace();
            }
            // Dup
            try {
                invalidArray.dup();
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                e.printStackTrace();
            }
            // Unsafe dup:
            try {
                invalidArray.unsafeDuplication();
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                e.printStackTrace();
            }
            try {
                invalidArray.unsafeDuplication(true);
                Assert.fail("Exception should be thrown");
            } catch (ND4JWorkspaceException e) {
                e.printStackTrace();
            }
        } finally {
            Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        }
    }

    @Test
    public void testBadGenerationLeverageMigrateDetach() {
        INDArray gen2 = null;
        for (int i = 0; i < 4; i++) {
            MemoryWorkspace wsOuter = Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(BasicWorkspaceTests.basicConfig, "testBadGeneration");
            try (MemoryWorkspace wsOuter2 = wsOuter.notifyScopeEntered()) {
                INDArray arr = Nd4j.linspace(1, 10, 10);
                if (i == 2) {
                    gen2 = arr;
                }
                if (i == 3) {
                    MemoryWorkspace wsInner = Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(BasicWorkspaceTests.basicConfig, "testBadGeneration2");
                    try (MemoryWorkspace wsInner2 = wsInner.notifyScopeEntered()) {
                        // Leverage
                        try {
                            gen2.leverage();
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            // Expected exception
                            e.printStackTrace();
                        }
                        try {
                            gen2.leverageTo("testBadGeneration2");
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            // Expected exception
                            e.printStackTrace();
                        }
                        try {
                            gen2.leverageOrDetach("testBadGeneration2");
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            // Expected exception
                            e.printStackTrace();
                        }
                        try {
                            gen2.leverageTo("testBadGeneration2");
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            // Expected exception
                            e.printStackTrace();
                        }
                        // Detach
                        try {
                            gen2.detach();
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            e.printStackTrace();
                        }
                        // Migrate
                        try {
                            gen2.migrate();
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            // Expected exception
                            e.printStackTrace();
                        }
                        try {
                            gen2.migrate(true);
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            // Expected exception
                            e.printStackTrace();
                        }
                        // Dup
                        try {
                            gen2.dup();
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            e.printStackTrace();
                        }
                        // Unsafe dup:
                        try {
                            gen2.unsafeDuplication();
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            e.printStackTrace();
                        }
                        try {
                            gen2.unsafeDuplication(true);
                            Assert.fail("Exception should be thrown");
                        } catch (ND4JWorkspaceException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

