package org.nd4j.linalg.profiling;


import OpExecutioner.ProfilingMode.ALL;
import OpExecutioner.ProfilingMode.ANY_PANIC;
import OpExecutioner.ProfilingMode.DISABLED;
import OpExecutioner.ProfilingMode.INF_PANIC;
import OpExecutioner.ProfilingMode.NAN_PANIC;
import OpExecutioner.ProfilingMode.SCOPE_PANIC;
import OpProfiler.PenaltyCause;
import OpProfiler.PenaltyCause.MIXED_ORDER;
import OpProfiler.PenaltyCause.NONE;
import OpProfiler.PenaltyCause.NON_EWS_ACCESS;
import OpProfiler.PenaltyCause.TAD_NON_EWS_ACCESS;
import OpProfiler.PenaltyCause.TAD_STRIDED_ACCESS;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;
import org.nd4j.linalg.profiler.OpProfiler;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class OperationProfilerTests {
    @Test
    public void testCounter1() {
        INDArray array = Nd4j.createUninitialized(100);
        array.assign(10.0F);
        array.divi(2.0F);
        Assert.assertEquals(2, OpProfiler.getInstance().getInvocationsCount());
    }

    @Test
    public void testStack1() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(ALL);
        INDArray array = Nd4j.createUninitialized(100);
        array.assign(10.0F);
        array.assign(20.0F);
        array.assign(30.0F);
        Assert.assertEquals(3, OpProfiler.getInstance().getInvocationsCount());
        OpProfiler.getInstance().printOutDashboard();
    }

    @Test
    public void testBadCombos1() throws Exception {
        INDArray x = Nd4j.create(100);
        INDArray y = Nd4j.create(100);
        OpProfiler[] causes = OpProfiler.getInstance().processOperands(x, y);
        Assert.assertEquals(1, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, NONE));
    }

    @Test
    public void testBadCombos2() throws Exception {
        INDArray x = Nd4j.create(100).reshape('f', 10, 10);
        INDArray y = Nd4j.create(100).reshape('c', 10, 10);
        OpProfiler[] causes = OpProfiler.getInstance().processOperands(x, y);
        Assert.assertEquals(1, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, MIXED_ORDER));
    }

    @Test
    public void testBadCombos3() throws Exception {
        INDArray x = Nd4j.create(27).reshape('c', 3, 3, 3).tensorAlongDimension(0, 1, 2);
        INDArray y = Nd4j.create(100).reshape('f', 10, 10);
        OpProfiler[] causes = OpProfiler.getInstance().processOperands(x, y);
        log.info("Causes: {}", Arrays.toString(causes));
        Assert.assertEquals(2, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, MIXED_ORDER));
        Assert.assertTrue(ArrayUtils.contains(causes, NON_EWS_ACCESS));
    }

    @Test
    public void testBadCombos4() throws Exception {
        INDArray x = Nd4j.create(27).reshape('c', 3, 3, 3).tensorAlongDimension(0, 1, 2);
        INDArray y = Nd4j.create(100).reshape('f', 10, 10);
        INDArray z = Nd4j.create(100).reshape('f', 10, 10);
        OpProfiler[] causes = OpProfiler.getInstance().processOperands(x, y, z);
        log.info("Causes: {}", Arrays.toString(causes));
        Assert.assertEquals(2, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, MIXED_ORDER));
        Assert.assertTrue(ArrayUtils.contains(causes, NON_EWS_ACCESS));
    }

    @Test
    public void testBadCombos5() throws Exception {
        INDArray w = Nd4j.create(100).reshape('c', 10, 10);
        INDArray x = Nd4j.create(100).reshape('c', 10, 10);
        INDArray y = Nd4j.create(100).reshape('f', 10, 10);
        INDArray z = Nd4j.create(100).reshape('c', 10, 10);
        OpProfiler[] causes = OpProfiler.getInstance().processOperands(w, x, y, z);
        log.info("Causes: {}", Arrays.toString(causes));
        Assert.assertEquals(1, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, MIXED_ORDER));
    }

    @Test
    public void testBadTad1() throws Exception {
        INDArray x = Nd4j.create(2, 4, 5, 6);
        Pair<DataBuffer, DataBuffer> pair = Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(x, new int[]{ 0, 2 });
        OpProfiler[] causes = OpProfiler.getInstance().processTADOperands(pair.getFirst());
        log.info("Causes: {}", Arrays.toString(causes));
        Assert.assertEquals(1, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, TAD_NON_EWS_ACCESS));
    }

    @Test
    public void testBadTad2() throws Exception {
        INDArray x = Nd4j.create(2, 4, 5, 6);
        Pair<DataBuffer, DataBuffer> pair = Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(x, new int[]{ 2, 3 });
        OpProfiler[] causes = OpProfiler.getInstance().processTADOperands(pair.getFirst());
        log.info("Causes: {}", Arrays.toString(causes));
        Assert.assertEquals(1, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, TAD_NON_EWS_ACCESS));
    }

    @Test
    public void testBadTad3() throws Exception {
        INDArray x = Nd4j.create(new int[]{ 2, 4, 5, 6, 7 }, 'f');
        Pair<DataBuffer, DataBuffer> pair = Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(x, new int[]{ 0, 2, 4 });
        OpProfiler[] causes = OpProfiler.getInstance().processTADOperands(pair.getFirst());
        log.info("Causes: {}", Arrays.toString(causes));
        Assert.assertEquals(1, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, TAD_NON_EWS_ACCESS));
    }

    @Test
    public void testBadTad5() throws Exception {
        INDArray x = Nd4j.create(new int[]{ 2, 4, 5, 6, 7 }, 'f');
        Pair<DataBuffer, DataBuffer> pair = Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(x, new int[]{ 4 });
        OpProfiler[] causes = OpProfiler.getInstance().processTADOperands(pair.getFirst());
        log.info("TAD: {}", Arrays.toString(pair.getFirst().asInt()));
        log.info("Causes: {}", Arrays.toString(causes));
        Assert.assertEquals(1, causes.length);
        Assert.assertTrue(ArrayUtils.contains(causes, TAD_STRIDED_ACCESS));
    }

    @Test
    public void testCxFxF1() throws Exception {
        INDArray a = Nd4j.create(10, 10).reshape('f', 10, 10);
        INDArray b = Nd4j.create(10, 10).reshape('c', 10, 10);
        INDArray c = Nd4j.create(10, 10).reshape('f', 10, 10);
        String ret = OpProfiler.getInstance().processOrders(a, b, c);
        Assert.assertEquals("F x C x F", ret);
    }

    @Test
    public void testCxFxF2() throws Exception {
        INDArray a = Nd4j.create(10, 10).reshape('c', 10, 10);
        INDArray b = Nd4j.create(10, 10).reshape('c', 10, 10);
        INDArray c = Nd4j.create(10, 10).reshape('f', 10, 10);
        String ret = OpProfiler.getInstance().processOrders(a, b, c);
        Assert.assertEquals("C x C x F", ret);
    }

    @Test
    public void testCxFxF3() throws Exception {
        INDArray a = Nd4j.create(10, 10).reshape('c', 10, 10);
        INDArray b = Nd4j.create(10, 10).reshape('c', 10, 10);
        INDArray c = Nd4j.create(10, 10).reshape('c', 10, 10);
        String ret = OpProfiler.getInstance().processOrders(a, b, c);
        Assert.assertEquals("C x C x C", ret);
    }

    @Test
    public void testBlasFF() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(ALL);
        INDArray a = Nd4j.create(10, 10).reshape('f', 10, 10);
        INDArray b = Nd4j.create(10, 10).reshape('f', 10, 10);
        a.mmul(b);
        OpProfiler.getInstance().printOutDashboard();
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNaNPanic1() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(NAN_PANIC);
        INDArray a = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, Float.NaN });
        a.muli(3.0F);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNaNPanic2() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(INF_PANIC);
        INDArray a = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, Float.POSITIVE_INFINITY });
        a.muli(3.0F);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNaNPanic3() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(ANY_PANIC);
        INDArray a = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, Float.NEGATIVE_INFINITY });
        a.muli(3.0F);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testScopePanic1() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(SCOPE_PANIC);
        INDArray array;
        try (MemoryWorkspace workspace = Nd4j.getWorkspaceManager().getAndActivateWorkspace("WS119")) {
            array = Nd4j.create(10);
            Assert.assertTrue(array.isAttached());
        }
        array.add(1.0);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testScopePanic2() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(SCOPE_PANIC);
        INDArray array;
        try (MemoryWorkspace workspace = Nd4j.getWorkspaceManager().getAndActivateWorkspace("WS120")) {
            array = Nd4j.create(10);
            Assert.assertTrue(array.isAttached());
            Assert.assertEquals(1, workspace.getGenerationId());
        }
        try (MemoryWorkspace workspace = Nd4j.getWorkspaceManager().getAndActivateWorkspace("WS120")) {
            Assert.assertEquals(2, workspace.getGenerationId());
            array.add(1.0);
            Assert.assertTrue(array.isAttached());
        }
    }

    @Test
    public void testScopePanic3() throws Exception {
        Nd4j.getExecutioner().setProfilingMode(SCOPE_PANIC);
        INDArray array;
        try (MemoryWorkspace workspace = Nd4j.getWorkspaceManager().getAndActivateWorkspace("WS121")) {
            array = Nd4j.create(10);
            Assert.assertTrue(array.isAttached());
            Assert.assertEquals(1, workspace.getGenerationId());
            try (MemoryWorkspace workspaceInner = Nd4j.getWorkspaceManager().getAndActivateWorkspace("WS122")) {
                array.add(1.0);
            }
        }
    }

    @Test
    public void testScopePanicPerf() {
        try (MemoryWorkspace workspace = Nd4j.getWorkspaceManager().getAndActivateWorkspace("WS121")) {
            INDArray x = Nd4j.create(1000, 1000).assign(1.0);
            INDArray y = Nd4j.create(1000, 1000).assign(1.0);
            for (int e = 0; e < 10000; e++) {
                x.addi(y);
            }
            Nd4j.getExecutioner().setProfilingMode(SCOPE_PANIC);
            val nanosC = System.nanoTime();
            for (int e = 0; e < 10000; e++) {
                x.addi(y);
            }
            val nanosD = System.nanoTime();
            val avgB = (nanosD - nanosC) / 10000;
            Nd4j.getExecutioner().setProfilingMode(DISABLED);
            val nanosA = System.nanoTime();
            for (int e = 0; e < 10000; e++) {
                x.addi(y);
            }
            val nanosB = System.nanoTime();
            val avgA = (nanosB - nanosA) / 10000;
            log.info("A: {}; B: {}", avgA, avgB);
        }
    }
}

