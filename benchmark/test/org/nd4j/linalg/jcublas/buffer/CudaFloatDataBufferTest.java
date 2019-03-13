package org.nd4j.linalg.jcublas.buffer;


import AllocationStatus.CONSTANT;
import AllocationStatus.DEVICE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.jita.allocator.impl.AllocationPoint;
import org.nd4j.jita.allocator.impl.AtomicAllocator;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.jcublas.context.CudaContext;
import org.nd4j.linalg.util.ArrayUtil;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class CudaFloatDataBufferTest {
    @Test
    public void testDoubleDimJava1() throws Exception {
        INDArray sliceZero = Nd4j.create(new double[][]{ new double[]{ 1, 7 }, new double[]{ 4, 10 } });
        System.out.println(("Slice: " + sliceZero));
        Assert.assertEquals(1.0F, sliceZero.getFloat(0), 0.01F);
        Assert.assertEquals(7.0F, sliceZero.getFloat(1), 0.01F);
    }

    @Test
    public void getDouble() throws Exception {
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F });
        Assert.assertEquals("CudaFloatDataBuffer", buffer.getClass().getSimpleName());
        System.out.println("Starting check...");
        Assert.assertEquals(1.0F, buffer.getFloat(0), 0.001F);
        Assert.assertEquals(2.0F, buffer.getFloat(1), 0.001F);
        Assert.assertEquals(3.0F, buffer.getFloat(2), 0.001F);
        Assert.assertEquals(4.0F, buffer.getFloat(3), 0.001F);
        System.out.println(("Data: " + buffer));
    }

    @Test
    public void testPut() throws Exception {
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F });
        buffer.put(2, 16.0F);
        Assert.assertEquals(16.0F, buffer.getFloat(2), 0.001F);
        System.out.println(("Data: " + buffer));
    }

    @Test
    public void testNdArrayView1() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F });
        Assert.assertEquals(1.0F, array.getFloat(0), 0.001F);
        Assert.assertEquals(2.0F, array.getFloat(1), 0.001F);
        Assert.assertEquals(3.0F, array.getFloat(2), 0.001F);
        Assert.assertEquals(4.0F, array.getFloat(3), 0.001F);
    }

    @Test
    public void testNdArrayView2() throws Exception {
        INDArray array = Nd4j.create(10, 10);
        System.out.println("X0 --------------------------------");
        long tp1 = array.data().getTrackingPoint();
        array.putScalar(0, 10.0F);
        Assert.assertEquals(10.0F, array.getFloat(0), 0.01F);
        System.out.println("X1 --------------------------------");
        INDArray array2 = array.slice(1);
        long tp2 = array2.data().getTrackingPoint();
        Assert.assertEquals(tp1, tp2);
        array2.putScalar(0, 10);
        System.out.println("X2 --------------------------------");
        Assert.assertEquals(10.0F, array2.getFloat(0), 0.01F);
        tp2 = array2.data().getTrackingPoint();
        tp1 = array.data().getTrackingPoint();
        Assert.assertEquals(tp1, tp2);
    }

    @Test
    public void testINDArrayOffsets2() throws Exception {
        INDArray array = Nd4j.linspace(0, 24, 25).reshape(5, 5);
        Assert.assertEquals(6.0F, array.getFloat(6), 0.01F);
        INDArray slice0 = array.slice(0);
        Assert.assertEquals(0.0F, slice0.getFloat(0), 0.01F);
        INDArray slice1 = array.slice(1);
        Assert.assertEquals(5.0F, slice1.getFloat(0), 0.01F);
        INDArray slice2 = array.slice(2);
        Assert.assertEquals(10.0F, slice2.getFloat(0), 0.01F);
        INDArray slice3 = array.slice(3);
        Assert.assertEquals(15.0F, slice3.getFloat(0), 0.01F);
        Assert.assertEquals(array.data().getTrackingPoint(), slice0.data().getTrackingPoint());
        Assert.assertEquals(array.data().getTrackingPoint(), slice1.data().getTrackingPoint());
        Assert.assertEquals(array.data().getTrackingPoint(), slice2.data().getTrackingPoint());
        Assert.assertEquals(array.data().getTrackingPoint(), slice3.data().getTrackingPoint());
    }

    @Test
    public void testINDArrayOffsets3() throws Exception {
        INDArray array = Nd4j.linspace(0, 24, 25).reshape(5, 5);
        INDArray array2 = Nd4j.create(5, 5);
        Assert.assertEquals(6.0F, array.getFloat(6), 0.01F);
        INDArray slice0 = array.slice(0);
        Assert.assertEquals(0.0F, slice0.getFloat(0), 0.01F);
        array2.putRow(0, slice0);
        Assert.assertEquals(slice0, array2.slice(0));
        INDArray slice1 = array.slice(1);
        Assert.assertEquals(5.0F, slice1.getFloat(0), 0.01F);
        System.out.println("---------------------------------------------------------------------");
        array2.putRow(1, slice1);
        // assertFalse(true);
        Assert.assertEquals(slice1, array2.slice(1));
        System.out.println("---------------------------------------------------------------------");
        INDArray slice2 = array.slice(2);
        Assert.assertEquals(10.0F, slice2.getFloat(0), 0.01F);
        INDArray slice3 = array.slice(3);
        Assert.assertEquals(15.0F, slice3.getFloat(0), 0.01F);
        Assert.assertEquals(array.data().getTrackingPoint(), slice0.data().getTrackingPoint());
        Assert.assertEquals(array.data().getTrackingPoint(), slice1.data().getTrackingPoint());
        Assert.assertEquals(array.data().getTrackingPoint(), slice2.data().getTrackingPoint());
        Assert.assertEquals(array.data().getTrackingPoint(), slice3.data().getTrackingPoint());
    }

    @Test
    public void testDup1() throws Exception {
        INDArray array0 = Nd4j.create(10);
        INDArray array7 = Nd4j.create(10);
        CudaContext context = ((CudaContext) (AtomicAllocator.getInstance().getDeviceContext().getContext()));
        context.syncOldStream();
        long time1 = System.nanoTime();
        INDArray array1 = Nd4j.linspace(0, 9, 1000);
        long time2 = System.nanoTime();
        // context.syncOldStream();
        long time3 = System.nanoTime();
        INDArray array2 = array1.dup();
        long time4 = System.nanoTime();
        // context.syncOldStream();
        System.out.println(("Linspace time: " + (time2 - time1)));
        System.out.println(("Dup time: " + (time4 - time3)));
        System.out.println(("Total time: " + ((time2 - time1) + (time4 - time3))));
        Assert.assertEquals(array1, array2);
        Assert.assertNotEquals(array1.data().getTrackingPoint(), array2.data().getTrackingPoint());
    }

    @Test
    public void testDup2() throws Exception {
        INDArray array = Nd4j.linspace(0, 99, 100).reshape(10, 10);
        INDArray slice1 = array.slice(2);
        Assert.assertEquals(10, slice1.length());
        INDArray duplicate = slice1.dup();
        Assert.assertEquals(10, duplicate.length());
        Assert.assertEquals(10, duplicate.data().length());
        Assert.assertEquals(10, duplicate.data().asDouble().length);
        Assert.assertEquals(20.0F, duplicate.getFloat(0), 1.0E-4F);
        Assert.assertEquals(21.0F, duplicate.getFloat(1), 1.0E-4F);
        Assert.assertEquals(22.0F, duplicate.getFloat(2), 1.0E-4F);
        Assert.assertEquals(23.0F, duplicate.getFloat(3), 1.0E-4F);
    }

    @Test
    public void testDup3() throws Exception {
        INDArray array = Nd4j.linspace(0, 99, 100).reshape(10, 10);
        INDArray slice1 = array.slice(2);
        DataBuffer duplicate = slice1.data().dup();
        Assert.assertEquals(10, duplicate.length());
        Assert.assertEquals(20.0F, duplicate.getFloat(0), 1.0E-4F);
        Assert.assertEquals(21.0F, duplicate.getFloat(1), 1.0E-4F);
        Assert.assertEquals(22.0F, duplicate.getFloat(2), 1.0E-4F);
        Assert.assertEquals(23.0F, duplicate.getFloat(3), 1.0E-4F);
        Assert.assertEquals(24.0F, duplicate.getFloat(4), 1.0E-4F);
        Assert.assertEquals(25.0F, duplicate.getFloat(5), 1.0E-4F);
        Assert.assertEquals(26.0F, duplicate.getFloat(6), 1.0E-4F);
        Assert.assertEquals(27.0F, duplicate.getFloat(7), 1.0E-4F);
        Assert.assertEquals(28.0F, duplicate.getFloat(8), 1.0E-4F);
        Assert.assertEquals(29.0F, duplicate.getFloat(9), 1.0E-4F);
    }

    @Test
    public void testShapeInfo1() throws Exception {
        INDArray array1 = Nd4j.ones(1, 10);
        System.out.println("X 0: -----------------------------");
        System.out.println(array1.shapeInfoDataBuffer());
        System.out.println(array1);
        System.out.println("X 1: -----------------------------");
        Assert.assertEquals(1.0, array1.getFloat(0), 1.0E-4);
        System.out.println("X 2: -----------------------------");
        Assert.assertEquals(1.0, array1.getFloat(1), 1.0E-4);
        Assert.assertEquals(1.0, array1.getFloat(2), 1.0E-4);
        System.out.println("X 3: -----------------------------");
        float sum = array1.sumNumber().floatValue();
        System.out.println("X 4: -----------------------------");
        System.out.println(("Sum: " + sum));
    }

    @Test
    public void testIndexer1() throws Exception {
        INDArray array1 = Nd4j.zeros(15, 15);
        System.out.println("-------------------------------------");
        Assert.assertEquals(0.0, array1.getFloat(0), 1.0E-4);
        // System.out.println(array1);
    }

    @Test
    public void testIndexer2() throws Exception {
        INDArray array1 = Nd4j.create(15);
        System.out.println("-------------------------------------");
        // assertEquals(0.0, array1.getFloat(0), 0.0001);
        System.out.println(array1);
    }

    @Test
    public void testSum2() {
        INDArray n = Nd4j.create(Nd4j.linspace(1, 8, 8).data(), new int[]{ 2, 2, 2 });
        System.out.println("X 0: -------------------------------------");
        // System.out.println("N result: " + n);
        INDArray test = Nd4j.create(new float[]{ 3, 7, 11, 15 }, new int[]{ 2, 2 });
        System.out.println("X 1: -------------------------------------");
        // System.out.println("Test result: " + test);
        INDArray sum = n.sum((-1));
        System.out.println("X 2: -------------------------------------");
        // System.out.println("Sum result: " + sum);
        Assert.assertEquals(test, sum);
    }

    @Test
    public void testOffsets() throws Exception {
        DataBuffer create = Nd4j.createBuffer(new double[]{ 1, 2, 3, 4 }, 2);
        Assert.assertEquals(2, create.length());
        Assert.assertEquals(4, create.underlyingLength());
        Assert.assertEquals(2, create.offset());
        Assert.assertEquals(3, create.getDouble(0), 0.1);
        Assert.assertEquals(4, create.getDouble(1), 0.1);
    }

    @Test
    public void testArraySimple1() throws Exception {
        // INDArray array2 = Nd4j.linspace(1, 100000, 100000);
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        System.out.println("------------------------");
        System.out.println(Shape.isRowVectorShape(array.shapeInfoDataBuffer()));
        System.out.println("------------------------");
        System.out.println(array.shapeInfoDataBuffer());
    }

    @Test
    public void testArraySimple2() throws Exception {
        // INDArray array2 = Nd4j.linspace(1, 100000, 100000);
        INDArray array = Nd4j.zeros(100, 100);
        System.out.println("X0: ------------------------");
        System.out.println(Shape.isRowVectorShape(array.shapeInfoDataBuffer()));
        System.out.println("X1: ------------------------");
        System.out.println(array.shapeInfoDataBuffer());
        System.out.println("X2: ------------------------");
        INDArray slice = array.getRow(12);
        System.out.println("X3: ------------------------");
        // AtomicAllocator.getInstance().getPointer(slice.shapeInfoDataBuffer());
        System.out.println("X4: ------------------------");
        System.out.println(Shape.isRowVectorShape(slice.shapeInfoDataBuffer()));
        System.out.println("X5: ------------------------");
        System.out.println(slice.shapeInfoDataBuffer());
    }

    @Test
    public void testSerialization() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        INDArray arr = Nd4j.rand(1, 20);
        String temp = System.getProperty("java.io.tmpdir");
        String outPath = FilenameUtils.concat(temp, "dl4jtestserialization.bin");
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(outPath)))) {
            Nd4j.write(arr, dos);
        }
        INDArray in;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(outPath))) {
            in = Nd4j.read(dis);
        }
        INDArray inDup = in.dup();
        System.out.println(in);
        System.out.println(inDup);
        Assert.assertEquals(arr, in);// Passes:   Original array "in" is OK, but array "inDup" is not!?

        Assert.assertEquals(in, inDup);// Fails

    }

    @Test
    public void testReadWrite() throws Exception {
        INDArray write = Nd4j.linspace(1, 4, 4);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        Nd4j.write(write, dos);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        DataInputStream dis = new DataInputStream(bis);
        INDArray read = Nd4j.read(dis);
        Assert.assertEquals(write, read);
    }

    @Test
    public void testFlattened1() throws Exception {
        List<INDArray> test = new ArrayList<>();
        for (int x = 0; x < 100; x++) {
            INDArray array = Nd4j.linspace(0, 99, 100);
            test.add(array);
        }
        INDArray ret = Nd4j.toFlattened(test);
        Assert.assertEquals(10000, ret.length());
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                Assert.assertEquals((((("X: [" + x) + "], Y: [") + y) + "] failed: "), y, ret.getFloat(((x * 100) + y)), 0.01F);
            }
        }
    }

    @Test
    public void testToFlattenedOrder() throws Exception {
        INDArray concatC = Nd4j.linspace(1, 4, 4).reshape('c', 2, 2);
        INDArray concatF = Nd4j.create(new int[]{ 2, 2 }, 'f');
        concatF.assign(concatC);
        INDArray assertionC = Nd4j.create(new double[]{ 1, 2, 3, 4, 1, 2, 3, 4 });
        // INDArray testC = Nd4j.toFlattened('c',concatC,concatF);
        // assertEquals(assertionC,testC);
        System.out.println("P0: --------------------------------------------------------");
        INDArray test = Nd4j.toFlattened('f', concatC, concatF);
        System.out.println("P1: --------------------------------------------------------");
        INDArray assertion = Nd4j.create(new double[]{ 1, 3, 2, 4, 1, 3, 2, 4 });
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testToFlattenedWithOrder() {
        int[] firstShape = new int[]{ 10, 3 };
        int firstLen = ArrayUtil.prod(firstShape);
        int[] secondShape = new int[]{ 2, 7 };
        int secondLen = ArrayUtil.prod(secondShape);
        int[] thirdShape = new int[]{ 3, 3 };
        int thirdLen = ArrayUtil.prod(thirdShape);
        INDArray firstC = Nd4j.linspace(1, firstLen, firstLen).reshape('c', firstShape);
        INDArray firstF = Nd4j.create(firstShape, 'f').assign(firstC);
        INDArray secondC = Nd4j.linspace(1, secondLen, secondLen).reshape('c', secondShape);
        INDArray secondF = Nd4j.create(secondShape, 'f').assign(secondC);
        INDArray thirdC = Nd4j.linspace(1, thirdLen, thirdLen).reshape('c', thirdShape);
        INDArray thirdF = Nd4j.create(thirdShape, 'f').assign(thirdC);
        Assert.assertEquals(firstC, firstF);
        Assert.assertEquals(secondC, secondF);
        Assert.assertEquals(thirdC, thirdF);
        INDArray cc = Nd4j.toFlattened('c', firstC, secondC, thirdC);
        INDArray cf = Nd4j.toFlattened('c', firstF, secondF, thirdF);
        Assert.assertEquals(cc, cf);
        INDArray cmixed = Nd4j.toFlattened('c', firstC, secondF, thirdF);
        Assert.assertEquals(cc, cmixed);
        INDArray fc = Nd4j.toFlattened('f', firstC, secondC, thirdC);
        Assert.assertNotEquals(cc, fc);
        INDArray ff = Nd4j.toFlattened('f', firstF, secondF, thirdF);
        Assert.assertEquals(fc, ff);
        INDArray fmixed = Nd4j.toFlattened('f', firstC, secondF, thirdF);
        Assert.assertEquals(fc, fmixed);
    }

    @Test
    public void testDataCreation1() throws Exception {
        BaseCudaDataBuffer buffer = ((BaseCudaDataBuffer) (Nd4j.createBuffer(10)));
        AllocationPoint point = buffer.getAllocationPoint();
        CudaContext context = AtomicAllocator.getInstance().getContextPool().acquireContextForDevice(0);
        Assert.assertEquals(true, point.isActualOnHostSide());
        buffer.put(0, 10.0F);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        buffer.put(1, 10.0F);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        AtomicAllocator.getInstance().getPointer(buffer, context);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        System.out.println("AM ------------------------------------");
        AtomicAllocator.getInstance().getHostPointer(buffer);
        System.out.println("AN ------------------------------------");
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(true, point.isActualOnDeviceSide());
    }

    @Test
    public void testDataCreation2() throws Exception {
        BaseCudaDataBuffer buffer = ((BaseCudaDataBuffer) (Nd4j.createBuffer(new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 })));
        AllocationPoint point = buffer.getAllocationPoint();
        CudaContext context = AtomicAllocator.getInstance().getContextPool().acquireContextForDevice(0);
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        Assert.assertEquals(false, point.isActualOnHostSide());
        System.out.println("AX --------------------------");
        buffer.put(0, 10.0F);
        System.out.println("AZ --------------------------");
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        buffer.put(1, 10.0F);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        AtomicAllocator.getInstance().getPointer(buffer, context);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        System.out.println("AM ------------------------------------");
        AtomicAllocator.getInstance().getHostPointer(buffer);
        System.out.println("AN ------------------------------------");
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        Assert.assertEquals(DEVICE, point.getAllocationStatus());
    }

    @Test
    public void testDataCreation3() throws Exception {
        BaseCudaDataBuffer buffer = ((BaseCudaDataBuffer) (Nd4j.createBuffer(new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 })));
        AllocationPoint point = buffer.getAllocationPoint();
        CudaContext context = AtomicAllocator.getInstance().getContextPool().acquireContextForDevice(0);
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        Assert.assertEquals(false, point.isActualOnHostSide());
        System.out.println("AX --------------------------");
        buffer.put(0, 10.0F);
        System.out.println("AZ --------------------------");
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        buffer.put(1, 10.0F);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(false, point.isActualOnDeviceSide());
        AtomicAllocator.getInstance().getPointer(buffer, context);
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        System.out.println("AM ------------------------------------");
        AtomicAllocator.getInstance().getHostPointer(buffer);
        System.out.println("AN ------------------------------------");
        Assert.assertEquals(true, point.isActualOnHostSide());
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        Assert.assertEquals(DEVICE, point.getAllocationStatus());
    }

    @Test
    public void testDataCreation4() throws Exception {
        BaseCudaDataBuffer buffer = ((BaseCudaDataBuffer) (Nd4j.createBuffer(new int[8])));
        AllocationPoint point = buffer.getAllocationPoint();
        Assert.assertEquals(true, point.isActualOnDeviceSide());
        Assert.assertEquals(false, point.isActualOnHostSide());
        System.out.println("AX --------------------------");
        buffer.put(0, 10.0F);
        System.out.println("AZ --------------------------");
        Assert.assertEquals(DEVICE, point.getAllocationStatus());
    }

    @Test
    public void testDataCreation5() throws Exception {
        INDArray array = Nd4j.create(new double[][]{ new double[]{ 0, 2 }, new double[]{ 2, 1 } });
        AllocationPoint pointMain = getAllocationPoint();
        AllocationPoint pointShape = getAllocationPoint();
        Assert.assertEquals(true, pointShape.isActualOnDeviceSide());
        Assert.assertEquals(true, pointShape.isActualOnHostSide());
        Assert.assertEquals(true, pointMain.isActualOnDeviceSide());
        Assert.assertEquals(false, pointMain.isActualOnHostSide());
        Assert.assertEquals(DEVICE, pointMain.getAllocationStatus());
        Assert.assertEquals(CONSTANT, pointShape.getAllocationStatus());
    }

    @Test
    public void testDataCreation6() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 0, 1, 2, 3 });
        AllocationPoint pointMain = getAllocationPoint();
        AllocationPoint pointShape = getAllocationPoint();
        Assert.assertEquals(true, pointShape.isActualOnDeviceSide());
        Assert.assertEquals(true, pointShape.isActualOnHostSide());
        Assert.assertEquals(true, pointMain.isActualOnDeviceSide());
        Assert.assertEquals(false, pointMain.isActualOnHostSide());
        Assert.assertEquals(DEVICE, pointMain.getAllocationStatus());
        Assert.assertEquals(CONSTANT, pointShape.getAllocationStatus());
    }

    @Test
    public void testDataCreation7() throws Exception {
        INDArray array = Nd4j.zeros(1500, 150);
        AllocationPoint pointMain = getAllocationPoint();
        AllocationPoint pointShape = getAllocationPoint();
        Assert.assertEquals(true, pointMain.isActualOnDeviceSide());
        Assert.assertEquals(true, pointMain.isActualOnHostSide());
        Assert.assertEquals(true, pointShape.isActualOnDeviceSide());
        Assert.assertEquals(true, pointShape.isActualOnHostSide());
    }

    @Test
    public void testDataCreation8() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1, 2, 3, 4, 5 });
        AllocationPoint pointMain = getAllocationPoint();
        AllocationPoint pointShape = getAllocationPoint();
        Assert.assertFalse(pointMain.isConstant());
        Assert.assertTrue(pointShape.isConstant());
    }

    @Test
    public void testDataCreation9() throws Exception {
        INDArray array = Nd4j.create(20);
        AllocationPoint pointMain = getAllocationPoint();
        AllocationPoint pointShape = getAllocationPoint();
        Assert.assertFalse(pointMain.isConstant());
        Assert.assertTrue(pointShape.isConstant());
        Assert.assertEquals(true, pointMain.isActualOnDeviceSide());
        Assert.assertEquals(true, pointMain.isActualOnHostSide());
    }

    @Test
    public void testReshape1() throws Exception {
        INDArray arrayC = Nd4j.zeros(1000);
        INDArray arrayF = arrayC.reshape('f', 10, 100);
        Assert.assertEquals(102, arrayF.shapeInfoDataBuffer().getInt(7));
        Assert.assertEquals(1, arrayF.shapeInfoDataBuffer().getInt(6));
        System.out.println(arrayC.shapeInfoDataBuffer());
        System.out.println(arrayF.shapeInfoDataBuffer());
        System.out.println(("Stride: " + (arrayF.elementWiseStride())));
        System.out.println(arrayF.shapeInfoDataBuffer());
        Assert.assertEquals('f', Shape.getOrder(arrayF));
        Assert.assertEquals(102, arrayF.shapeInfoDataBuffer().getInt(7));
        Assert.assertEquals(1, arrayF.shapeInfoDataBuffer().getInt(6));
        INDArray arrayZ = Nd4j.create(10, 100, 'f');
    }

    @Test
    public void testReshapeDup1() throws Exception {
        INDArray arrayC = Nd4j.create(10, 100);
        INDArray arrayF = arrayC.dup('f');
        System.out.println(arrayC.shapeInfoDataBuffer());
        System.out.println(arrayF.shapeInfoDataBuffer());
        Assert.assertEquals(102, arrayF.shapeInfoDataBuffer().getInt(7));
        Assert.assertEquals(1, arrayF.shapeInfoDataBuffer().getInt(6));
    }

    @Test
    public void testReshapeDup2() throws Exception {
        INDArray arrayC = Nd4j.create(5, 10, 100);
        INDArray arrayF = arrayC.dup('f');
        System.out.println(arrayC.shapeInfoDataBuffer());
        System.out.println(arrayF.shapeInfoDataBuffer());
        Assert.assertEquals(102, arrayF.shapeInfoDataBuffer().getInt(9));
        Assert.assertEquals(1, arrayF.shapeInfoDataBuffer().getInt(8));
    }

    @Test
    public void testPermuteAssingn() {
        INDArray arr = Nd4j.linspace(1, 60, 60).reshape('c', 3, 4, 5);
        INDArray arr2 = arr.permute(1, 0, 2);
        INDArray arr3 = arr2.dup('c');
        Assert.assertEquals(arr2, arr3);
    }
}

