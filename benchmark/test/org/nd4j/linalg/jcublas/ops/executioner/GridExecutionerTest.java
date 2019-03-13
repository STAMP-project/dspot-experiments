package org.nd4j.linalg.jcublas.ops.executioner;


import CudaGridExecutioner.MetaType.INVERTED_PREDICATE;
import CudaGridExecutioner.MetaType.NOT_APPLICABLE;
import Op.Type.REDUCE;
import Op.Type.SCALAR;
import org.bytedeco.javacpp.Pointer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.jita.allocator.impl.AllocationPoint;
import org.nd4j.jita.allocator.impl.AtomicAllocator;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.grid.GridPointers;
import org.nd4j.linalg.api.ops.impl.accum.EqualsWithEps;
import org.nd4j.linalg.api.ops.impl.accum.Max;
import org.nd4j.linalg.api.ops.impl.accum.Sum;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarAdd;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarMultiplication;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarSet;
import org.nd4j.linalg.api.ops.impl.transforms.Abs;
import org.nd4j.linalg.api.ops.impl.transforms.Set;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.jcublas.context.CudaContext;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class GridExecutionerTest {
    // /////////////////////////////////////////////////////////////////////////
    /* /////////////////////////////////////////////////////////////////////////

    MatchMeta tests are checking, how ops are matching for MetaOp requirements
     */
    // ///////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////
    @Test
    public void isMatchingMetaOp1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray array = Nd4j.create(10);
        ScalarAdd opA = new ScalarAdd(array, 10.0F);
        ScalarAdd opB = new ScalarAdd(array, 10.0F);
        executioner.exec(opA);
        Assert.assertEquals(NOT_APPLICABLE, executioner.getMetaOpType(opB));
    }

    @Test
    public void isMatchingMetaOp2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray array = Nd4j.create(10);
        INDArray array2 = Nd4j.create(10);
        ScalarAdd opA = new ScalarAdd(array, 10.0F);
        ScalarAdd opB = new ScalarAdd(array2, 10.0F);
        executioner.exec(opA);
        Assert.assertEquals(executioner.getMetaOpType(opB), NOT_APPLICABLE);
    }

    @Test
    public void isMatchingMetaOp3() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray array = Nd4j.create(10);
        ScalarAdd opA = new ScalarAdd(array, 10.0F);
        Max opB = new Max(array);
        executioner.exec(opA);
        Assert.assertEquals(NOT_APPLICABLE, executioner.getMetaOpType(opB));
    }

    @Test
    public void isMatchingMetaOp4() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(10);
        INDArray arrayY = Nd4j.create(10);
        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());
        ScalarAdd opB = new ScalarAdd(arrayX, 10.0F);
        executioner.exec(opA);
        Assert.assertEquals(INVERTED_PREDICATE, executioner.getMetaOpType(opB));
    }

    // /////////////////////////////////////////////////////////////////////////
    /* /////////////////////////////////////////////////////////////////////////

    GridFlow tests are checking how ops are getting queued upon exec() calls
     */
    // ///////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////
    @Test
    public void testGridFlow1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        Assert.assertEquals(0, executioner.getQueueLength());
        INDArray array = Nd4j.create(10);
        ScalarAdd opA = new ScalarAdd(array, 10.0F);
        executioner.exec(opA);
        long time1 = System.nanoTime();
        Max opB = new Max(array);
        executioner.exec(opB);
        Assert.assertEquals(0, executioner.getQueueLength());
        long time2 = System.nanoTime();
        opB = new Max(array);
        executioner.exec(opB);
        long time3 = System.nanoTime();
        Assert.assertEquals(0, executioner.getQueueLength());
        long firstExec = time2 - time1;
        long secondExec = time3 - time2;
        System.out.println(("First exec time: " + firstExec));
        System.out.println(("Second exec time: " + secondExec));
        System.out.println(("Array: " + array));
    }

    @Test
    public void testGridFlow2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(10);
        INDArray arrayY = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        INDArray exp = Nd4j.create(new float[]{ 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F });
        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());
        executioner.exec(opA);
        Assert.assertEquals(1, executioner.getQueueLength());
        long time1 = System.nanoTime();
        ScalarAdd opB = new ScalarAdd(arrayX, 2.0F);
        executioner.exec(opB);
        Assert.assertEquals(0, executioner.getQueueLength());
        long time2 = System.nanoTime();
        long time3 = System.nanoTime();
        Assert.assertEquals(0, executioner.getQueueLength());
        long firstExec = time2 - time1;
        long secondExec = time3 - time2;
        System.out.println(("First exec time: " + firstExec));
        System.out.println(("Second exec time: " + secondExec));
        Assert.assertEquals(exp, arrayX);
        System.out.println(("ArrayX: " + arrayX));
        System.out.println(("ArrayExp: " + exp));
    }

    @Test
    public void testGridFlow3() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(10);
        INDArray arrayY = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        EqualsWithEps op = new EqualsWithEps(arrayX, arrayY, Nd4j.EPS_THRESHOLD);
        executioner.exec(op);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertNotEquals(null, op.getFinalResult());
        Assert.assertEquals(10, op.getFinalResult().intValue());
    }

    @Test
    public void testGridFlow4() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        Sum op = new Sum(arrayX);
        executioner.exec(op);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertNotEquals(null, op.getFinalResult());
        Assert.assertEquals(5, op.getFinalResult().intValue());
    }

    @Test
    public void testGridFlow5() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(5, 5);
        Sum op = new Sum(arrayX);
        executioner.exec(op, 1);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(0, op.z().getFloat(0), 0.1F);
    }

    @Test
    public void testGridFlow6() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(new float[]{ -1.0F, -1.0F, 1.0F });
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F });
        Abs op = new Abs(arrayX);
        executioner.exec(op);
        op = new Abs(arrayX);
        executioner.exec(op);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(exp, arrayX);
    }

    @Test
    public void testGridFlow7() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(new float[]{ 0.0F, 0.0F, 0.0F });
        INDArray arrayY1 = Nd4j.create(new float[]{ -1.0F, -1.0F, 1.0F });
        INDArray arrayY2 = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F });
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F });
        Set opA = new Set(arrayX, arrayY1, arrayX, arrayY1.length());
        executioner.exec(opA);
        Assert.assertEquals(1, executioner.getQueueLength());
        Set opB = new Set(arrayX, arrayY2, arrayX, arrayY1.length());
        executioner.exec(opB);
        Assert.assertEquals(1, executioner.getQueueLength());
        Assert.assertEquals(1, executioner.getExecutionCounter());
        // System.out.println("---------------------------");
        executioner.flushQueueBlocking();
        arrayX.getFloat(0);
        // it should be 0, because getFloat() should trigger flushQueue
        Assert.assertEquals(2, executioner.getExecutionCounter());
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(1.0F, arrayX.getFloat(0), 0.1F);
        // assertEquals(exp, arrayX);
    }

    @Test
    public void testGridFlow8() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(new float[]{ 0.0F, 0.0F, 0.0F });
        INDArray arrayY1 = Nd4j.create(new float[]{ -1.0F, -1.0F, 1.0F });
        INDArray arrayY2 = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F });
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F });
        Set opA = new Set(arrayX, arrayY1, arrayX, arrayY1.length());
        executioner.exec(opA);
        Assert.assertEquals(1, executioner.getQueueLength());
        ScalarSet opB = new ScalarSet(arrayX, 1.0F);
        executioner.exec(opB);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(1.0F, arrayX.getFloat(0), 0.1F);
        Assert.assertEquals(1.0F, arrayX.getFloat(1), 0.1F);
        // assertEquals(exp, arrayX);
    }

    /* @Test
    public void testGridFlow9() throws Exception {
    CudaGridExecutioner executioner = new CudaGridExecutioner();

    INDArray arrayX = Nd4j.create(new float[] {0f, 0f, 0f});
    INDArray arrayY1 = Nd4j.create(new float[] {-1f, -1f, 1f});
    INDArray arrayY2 = Nd4j.create(new float[] {1f, 1f, 1f});
    INDArray exp = Nd4j.create(new float[] {1f, 1f, 1f});

    Set opA = new Set(arrayX, arrayY1, arrayX, arrayY1.length());

    executioner.exec(opA);

    assertEquals(1, executioner.getQueueLength());

    ScalarSet opB = new ScalarSet(arrayX, 1f);
    executioner.exec(opB);

    assertEquals(0, executioner.getQueueLength());
    assertEquals(1f, arrayX.getFloat(0), 0.1f);
    assertEquals(1f, arrayX.getFloat(1), 0.1f);
    //assertEquals(exp, arrayX);
    }
     */
    @Test
    public void testGridFlowFlush1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(10);
        INDArray arrayY = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        INDArray exp = Nd4j.create(new float[]{ 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F });
        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());
        executioner.exec(opA);
        executioner.flushQueue();
        Assert.assertEquals(arrayY, arrayX);
    }

    @Test
    public void testGridFlowFlush2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(10);
        INDArray arrayX2 = Nd4j.create(10);
        INDArray arrayY = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        INDArray exp = Nd4j.create(new float[]{ 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F });
        INDArray exp2 = Nd4j.create(new float[]{ 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F });
        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());
        executioner.exec(opA);
        Assert.assertEquals(1, executioner.getQueueLength());
        ScalarAdd opB = new ScalarAdd(arrayX2, 10.0F);
        executioner.exec(opB);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(arrayY, arrayX);
        Assert.assertEquals(exp2, arrayX2);
    }

    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////
    /* Performance test for combined op */
    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////
    @Test
    public void testGridPerformance1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray arrayX = Nd4j.create(1024);
        INDArray arrayY = Nd4j.create(1024);
        Set opA = new Set(arrayX, arrayY, arrayX, arrayX.length());
        ScalarAdd opB = new ScalarAdd(arrayX, 2.0F);
        long time1 = System.nanoTime();
        for (int x = 0; x < 1000000; x++) {
            executioner.exec(opA);
            executioner.exec(opB);
        }
        long time2 = System.nanoTime();
        System.out.println(("Execution time Meta: " + ((time2 - time1) / 1000000)));
    }

    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////
    /* Pointerize tests are checking how Ops are converted into GridPointers */
    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////
    @Test
    public void testOpPointerizeScalar1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray array = Nd4j.create(10);
        ScalarMultiplication opA = new ScalarMultiplication(array, 10.0F);
        GridPointers pointers = executioner.pointerizeOp(opA, null);
        Assert.assertEquals(opA.opNum(), pointers.getOpNum());
        Assert.assertEquals(SCALAR, pointers.getType());
        CudaContext context = ((CudaContext) (AtomicAllocator.getInstance().getDeviceContext().getContext()));
        Pointer x = AtomicAllocator.getInstance().getPointer(array, context);
        Pointer xShapeInfo = AtomicAllocator.getInstance().getPointer(array.shapeInfoDataBuffer(), context);
        Assert.assertEquals(x, pointers.getX());
        Assert.assertEquals(null, pointers.getY());
        Assert.assertEquals(x, pointers.getZ());
        Assert.assertEquals(1, pointers.getXStride());
        Assert.assertEquals((-1), pointers.getYStride());
        Assert.assertEquals(1, pointers.getZStride());
        Assert.assertEquals(xShapeInfo, pointers.getXShapeInfo());
        Assert.assertEquals(null, pointers.getYShapeInfo());
        Assert.assertEquals(xShapeInfo, pointers.getZShapeInfo());
        Assert.assertEquals(null, pointers.getDimensions());
        Assert.assertEquals(0, pointers.getDimensionsLength());
        Assert.assertEquals(null, pointers.getTadShape());
        Assert.assertEquals(null, pointers.getTadOffsets());
        Assert.assertEquals(null, pointers.getExtraArgs());
    }

    /**
     * Reduce along dimensions
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOpPointerizeReduce1() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray array = Nd4j.create(10, 10);
        Sum opA = new Sum(array);
        // we need exec here, to init Op.Z for specific dimension
        executioner.exec(opA, 1);
        GridPointers pointers = executioner.pointerizeOp(opA, 1);
        Assert.assertEquals(opA.opNum(), pointers.getOpNum());
        Assert.assertEquals(REDUCE, pointers.getType());
        CudaContext context = ((CudaContext) (AtomicAllocator.getInstance().getDeviceContext().getContext()));
        Pointer x = AtomicAllocator.getInstance().getPointer(array, context);
        Pointer xShapeInfo = AtomicAllocator.getInstance().getPointer(array.shapeInfoDataBuffer(), context);
        Pointer z = AtomicAllocator.getInstance().getPointer(opA.z(), context);
        Pointer zShapeInfo = AtomicAllocator.getInstance().getPointer(opA.z().shapeInfoDataBuffer(), context);
        DataBuffer dimBuff = Nd4j.getConstantHandler().getConstantBuffer(new int[]{ 1 });
        Pointer ptrBuff = AtomicAllocator.getInstance().getPointer(dimBuff, context);
        Assert.assertEquals(x, pointers.getX());
        Assert.assertEquals(null, pointers.getY());
        Assert.assertNotEquals(null, pointers.getZ());
        Assert.assertEquals(z, pointers.getZ());
        Assert.assertEquals(10, opA.z().length());
        Assert.assertEquals(10, pointers.getZLength());
        /* // We dont really care about EWS here, since we're testing TAD-based operation

        assertEquals(1, pointers.getXStride());
        assertEquals(-1, pointers.getYStride());
        assertEquals(1, pointers.getZStride());
         */
        Assert.assertEquals(xShapeInfo, pointers.getXShapeInfo());
        Assert.assertEquals(null, pointers.getYShapeInfo());
        Assert.assertEquals(zShapeInfo, pointers.getZShapeInfo());
        Assert.assertEquals(ptrBuff, pointers.getDimensions());
        Assert.assertEquals(1, pointers.getDimensionsLength());
        Assert.assertNotEquals(null, pointers.getTadShape());
        Assert.assertNotEquals(null, pointers.getTadOffsets());
        Assert.assertEquals(null, pointers.getExtraArgs());
    }

    /**
     * Reduce along all dimensions
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOpPointerizeReduce2() throws Exception {
        CudaGridExecutioner executioner = new CudaGridExecutioner();
        INDArray array = Nd4j.create(10, 10);
        Sum opA = new Sum(array);
        // we need exec here, to init Op.Z for specific dimension
        executioner.exec(opA);
        GridPointers pointers = executioner.pointerizeOp(opA, null);
        Assert.assertEquals(opA.opNum(), pointers.getOpNum());
        Assert.assertEquals(REDUCE, pointers.getType());
        CudaContext context = ((CudaContext) (AtomicAllocator.getInstance().getDeviceContext().getContext()));
        Pointer x = AtomicAllocator.getInstance().getPointer(array, context);
        Pointer xShapeInfo = AtomicAllocator.getInstance().getPointer(array.shapeInfoDataBuffer(), context);
        Pointer z = AtomicAllocator.getInstance().getPointer(opA.z(), context);
        Pointer zShapeInfo = AtomicAllocator.getInstance().getPointer(opA.z().shapeInfoDataBuffer(), context);
        DataBuffer dimBuff = Nd4j.getConstantHandler().getConstantBuffer(new int[]{ 1 });
        Pointer ptrBuff = AtomicAllocator.getInstance().getPointer(dimBuff, context);
        Assert.assertEquals(x, pointers.getX());
        Assert.assertEquals(null, pointers.getY());
        Assert.assertNotEquals(null, pointers.getZ());
        Assert.assertEquals(z, pointers.getZ());
        Assert.assertEquals(1, opA.z().length());
        Assert.assertEquals(1, pointers.getZLength());
        /* // We dont really care about EWS here, since we're testing TAD-based operation

        assertEquals(1, pointers.getXStride());
        assertEquals(-1, pointers.getYStride());
        assertEquals(1, pointers.getZStride());
         */
        Assert.assertEquals(xShapeInfo, pointers.getXShapeInfo());
        Assert.assertEquals(null, pointers.getYShapeInfo());
        Assert.assertEquals(zShapeInfo, pointers.getZShapeInfo());
        Assert.assertEquals(null, pointers.getDimensions());
        Assert.assertEquals(0, pointers.getDimensionsLength());
        Assert.assertEquals(null, pointers.getTadShape());
        Assert.assertEquals(null, pointers.getTadOffsets());
        Assert.assertEquals(null, pointers.getExtraArgs());
    }

    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////
    /* Reverse flow tests */
    // ///////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////
    @Test
    public void testReverseFlow1() throws Exception {
        CudaGridExecutioner executioner = ((CudaGridExecutioner) (Nd4j.getExecutioner()));
        INDArray put = Nd4j.create(new double[]{ 5, 6 });
        INDArray row1 = Nd4j.linspace(1, 4, 4);
        AllocationPoint point = AtomicAllocator.getInstance().getAllocationPoint(row1);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        System.out.println("A: --------------------------");
        row1 = row1.reshape(2, 2);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        System.out.println("B: --------------------------");
        // ((CudaGridExecutioner) Nd4j.getExecutioner()).flushQueueBlocking();
        row1.putRow(1, put);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        System.out.println("C: --------------------------");
        Assert.assertEquals(1, executioner.getQueueLength());
        executioner.flushQueueBlocking();
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(false, point.isActualOnHostSide());
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        System.out.println("D: --------------------------");
        // ((CudaGridExecutioner) Nd4j.getExecutioner()).flushQueueBlocking();
        // System.out.println(row1);
        Assert.assertArrayEquals(new float[]{ 1, 2, 5, 6 }, row1.data().asFloat(), 0.1F);
    }

    @Test
    public void testReverseFlow2() {
        CudaGridExecutioner executioner = ((CudaGridExecutioner) (Nd4j.getExecutioner()));
        INDArray n1 = Nd4j.scalar(1);
        INDArray n2 = Nd4j.scalar(2);
        INDArray n3 = Nd4j.scalar(3);
        INDArray n4 = Nd4j.scalar(4);
        System.out.println("0: ------------------------");
        INDArray nClone = n1.add(n2);
        Assert.assertEquals(Nd4j.scalar(3), nClone);
        INDArray n1PlusN2 = n1.add(n2);
        Assert.assertFalse(n1PlusN2.equals(n1));
        System.out.println("2: ------------------------");
        System.out.println(n4);
        INDArray subbed = n4.sub(n3);
        INDArray mulled = n4.mul(n3);
        INDArray div = n4.div(n3);
        System.out.println(("Subbed: " + subbed));
        System.out.println(("Mulled: " + mulled));
        System.out.println(("Div: " + div));
        System.out.println("4: ------------------------");
        Assert.assertFalse(subbed.equals(n4));
        Assert.assertFalse(mulled.equals(n4));
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertEquals(Nd4j.scalar(1), subbed);
        Assert.assertEquals(Nd4j.scalar(12), mulled);
        Assert.assertEquals(Nd4j.scalar(1.3333333333333333), div);
    }

    @Test
    public void testReverseFlow3() throws Exception {
        INDArray toSort = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray ascending = Nd4j.sort(toSort.dup(), 1, true);
        Assert.assertEquals(toSort, ascending);
        INDArray columnSorted = Nd4j.create(new float[]{ 2, 1, 4, 3 }, new int[]{ 2, 2 });
        // in this particular code point, toSort.dup() isn't executed, but queued
        INDArray dupd = toSort.dup();
        // if we execute muli - dup() op will be flushed as metaOp
        // dupd.muli(1.0);
        INDArray sorted = Nd4j.sort(dupd, 1, false);
        Assert.assertEquals(columnSorted, sorted);
    }

    @Test
    public void testDupLocality1() throws Exception {
        INDArray array1 = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 });
        AllocationPoint point1 = AtomicAllocator.getInstance().getAllocationPoint(array1);
        Assert.assertEquals(true, point1.isActualOnDeviceSide());
        Assert.assertEquals(false, point1.isActualOnHostSide());
        INDArray array2 = array1.dup();
        // ((GridExecutioner) Nd4j.getExecutioner()).flushQueueBlocking();
        AllocationPoint point2 = AtomicAllocator.getInstance().getAllocationPoint(array2);
        Assert.assertEquals(true, point2.isActualOnDeviceSide());
        Assert.assertEquals(true, point2.isActualOnHostSide());
    }

    @Test
    public void testDupLocality2() throws Exception {
        INDArray array2 = Nd4j.createUninitialized(new int[]{ 10, 10 }, 'c');
        // ((GridExecutioner) Nd4j.getExecutioner()).flushQueueBlocking();
        AllocationPoint point2 = AtomicAllocator.getInstance().getAllocationPoint(array2);
        Assert.assertEquals(true, point2.isActualOnDeviceSide());
        Assert.assertEquals(true, point2.isActualOnHostSide());
    }

    @Test
    public void testDupLocality3() throws Exception {
        INDArray array1 = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        INDArray exp1 = Nd4j.create(new float[]{ 0.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        INDArray exp2 = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        INDArray array2 = array1.dup();
        AllocationPoint point1 = AtomicAllocator.getInstance().getAllocationPoint(array1);
        AllocationPoint point2 = AtomicAllocator.getInstance().getAllocationPoint(array2);
        Assert.assertTrue(point1.isActualOnDeviceSide());
        Assert.assertTrue(point2.isActualOnDeviceSide());
        Assert.assertTrue(point1.isEnqueued());
        Assert.assertTrue(point2.isEnqueued());
        array1.putScalar(0, 0.0F);
        Assert.assertEquals(0, getQueueLength());
        Assert.assertFalse(point1.isActualOnDeviceSide());
        Assert.assertEquals(exp1, array1);
        Assert.assertEquals(exp2, array2);
    }

    @Test
    public void testDupLocality4() throws Exception {
        int nIn = 8;
        int layerSize = 10;
        int nOut = 4;
        INDArray in = Nd4j.ones(1, 10).dup('c');
        AllocationPoint point1 = AtomicAllocator.getInstance().getAllocationPoint(in);
        Assert.assertEquals(true, point1.isEnqueued());
        // assertEquals(1, ((GridExecutioner) Nd4j.getExecutioner()).getQueueLength());
        INDArray out = Nd4j.zeros(1, 10).dup('c');
        AllocationPoint point1A = AtomicAllocator.getInstance().getAllocationPoint(in);
        AllocationPoint point2 = AtomicAllocator.getInstance().getAllocationPoint(out);
        Assert.assertEquals(1, getQueueLength());
        Assert.assertTrue((point1 == point1A));
        Assert.assertEquals(true, point2.isEnqueued());
        Assert.assertEquals(false, point1.isEnqueued());
        Assert.assertEquals(Nd4j.ones(1, 10), in);
        Assert.assertEquals(Nd4j.zeros(1, 10), out);
        INDArray inCopy = in.dup('c');
        AllocationPoint point3 = AtomicAllocator.getInstance().getAllocationPoint(inCopy);
        Assert.assertEquals(false, point2.isEnqueued());
        Assert.assertEquals(true, point3.isEnqueued());
        Assert.assertEquals(true, point1.isEnqueued());
    }
}

