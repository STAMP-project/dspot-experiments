package org.nd4j.jita.memory.impl;


import AllocationStatus.CONSTANT;
import AllocationStatus.DEVICE;
import AllocationStatus.HOST;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import junit.framework.TestCase;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.jita.allocator.impl.AllocationPoint;
import org.nd4j.jita.allocator.impl.AtomicAllocator;
import org.nd4j.jita.allocator.pointers.PointersPair;
import org.nd4j.jita.allocator.tad.DeviceTADManager;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cache.TADManager;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class DelayedMemoryTest extends TestCase {
    /**
     * This test should be run manually
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDelayedAllocation2() throws Exception {
        AtomicAllocator allocator = AtomicAllocator.getInstance();
        INDArray array = Nd4j.create(10, 10);
        AllocationPoint pointer = allocator.getAllocationPoint(array);
        PointersPair pair = pointer.getPointers();
        // pointers should be equal, device memory wasn't allocated yet
        TestCase.assertEquals(pair.getDevicePointer(), pair.getHostPointer());
        // ////////////
        AllocationPoint shapePointer = allocator.getAllocationPoint(array.shapeInfoDataBuffer());
        // pointers should be equal, device memory wasn't allocated yet
        TestCase.assertEquals(shapePointer.getPointers().getDevicePointer(), shapePointer.getPointers().getHostPointer());
        TestCase.assertEquals(pointer.getAllocationStatus(), HOST);
        TestCase.assertEquals(shapePointer.getAllocationStatus(), HOST);
        float sum = array.sumNumber().floatValue();
        TestCase.assertEquals(0.0F, sum, 1.0E-4F);
        shapePointer = allocator.getAllocationPoint(array.shapeInfoDataBuffer());
        pointer = allocator.getAllocationPoint(array);
        TestCase.assertEquals(CONSTANT, shapePointer.getAllocationStatus());
        TestCase.assertEquals(DEVICE, pointer.getAllocationStatus());
        // at this point all pointers show be different, since we've used OP (sumNumber)
        Assert.assertNotEquals(shapePointer.getPointers().getDevicePointer(), shapePointer.getPointers().getHostPointer());
    }

    @Test
    public void testDelayedAllocation1() throws Exception {
        final AtomicAllocator allocator = AtomicAllocator.getInstance();
        final int limit = 6;
        final INDArray[] arrays = new INDArray[limit];
        final Thread[] threads = new Thread[limit];
        final int[] cards = new int[limit];
        for (int c = 0; c < (arrays.length); c++) {
            arrays[c] = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
            // we ensure, that both buffers are located in host memory now
            TestCase.assertEquals(HOST, allocator.getAllocationPoint(arrays[c]).getAllocationStatus());
            TestCase.assertEquals(HOST, allocator.getAllocationPoint(arrays[c].shapeInfoDataBuffer()).getAllocationStatus());
        }
        /* for (int c = 0; c < arrays.length; c++) {
        System.out.println(arrays[c]);

        assertEquals(AllocationStatus.DEVICE, allocator.getAllocationPoint(arrays[c]).getAllocationStatus());
        assertEquals(AllocationStatus.CONSTANT, allocator.getAllocationPoint(arrays[c].shapeInfoDataBuffer()).getAllocationStatus());
        }
         */
        for (int c = 0; c < (arrays.length); c++) {
            final int cnt = c;
            threads[cnt] = new Thread(new Runnable() {
                @Override
                public void run() {
                    float sum = arrays[cnt].sumNumber().floatValue();
                    cards[cnt] = allocator.getDeviceId();
                    TestCase.assertEquals(("Failed on C: " + cnt), 15.0F, sum, 0.001F);
                }
            });
            threads[cnt].start();
        }
        for (int c = 0; c < (arrays.length); c++) {
            threads[c].join();
        }
        // check if all devices present in system were used
        for (int c = 0; c < (arrays.length); c++) {
            Assert.assertNotEquals(allocator.getAllocationPoint(arrays[c]).getPointers().getDevicePointer(), allocator.getAllocationPoint(arrays[c]).getPointers().getHostPointer());
            Assert.assertNotEquals(allocator.getAllocationPoint(arrays[c].shapeInfoDataBuffer()).getPointers().getDevicePointer(), allocator.getAllocationPoint(arrays[c].shapeInfoDataBuffer()).getPointers().getHostPointer());
        }
        int numDevices = Nd4j.getAffinityManager().getNumberOfDevices();
        for (int c = 0; c < numDevices; c++) {
            TestCase.assertTrue((("Failed to find device [" + c) + "] in used devices"), ArrayUtils.contains(cards, c));
        }
    }

    @Test
    public void testDelayedAllocation3() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        AllocationPoint pointer = AtomicAllocator.getInstance().getAllocationPoint(array);
        PointersPair pair = pointer.getPointers();
        // pointers should be equal, device memory wasn't allocated yet
        TestCase.assertEquals(pair.getDevicePointer(), pair.getHostPointer());
        TestCase.assertEquals(2.0F, array.getFloat(1), 0.001F);
        TestCase.assertEquals(pair.getDevicePointer(), pair.getHostPointer());
    }

    @Test
    public void testDelayedAllocation4() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        AllocationPoint pointer = AtomicAllocator.getInstance().getAllocationPoint(array);
        PointersPair pair = pointer.getPointers();
        // pointers should be equal, device memory wasn't allocated yet
        TestCase.assertEquals(pair.getDevicePointer(), pair.getHostPointer());
        TestCase.assertEquals(2.0F, array.getFloat(1), 0.001F);
        TestCase.assertEquals(pair.getDevicePointer(), pair.getHostPointer());
        String temp = System.getProperty("java.io.tmpdir");
        String outPath = FilenameUtils.concat(temp, "dl4jtestserialization.bin");
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(outPath)))) {
            Nd4j.write(array, dos);
        }
        INDArray in;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(outPath))) {
            in = Nd4j.read(dis);
        }
        TestCase.assertEquals(AtomicAllocator.getInstance().getAllocationPoint(in).getPointers().getDevicePointer(), AtomicAllocator.getInstance().getAllocationPoint(in).getPointers().getHostPointer());
        TestCase.assertEquals(array, in);
    }

    @Test
    public void testDelayedTAD1() throws Exception {
        TADManager tadManager = new DeviceTADManager();
        INDArray array = Nd4j.create(128, 256);
        Pair<DataBuffer, DataBuffer> tadBuffers = tadManager.getTADOnlyShapeInfo(array, new int[]{ 0 });
        DataBuffer tadBuffer = tadBuffers.getFirst();
        DataBuffer offBuffer = tadBuffers.getSecond();
        AllocationPoint pointTad = AtomicAllocator.getInstance().getAllocationPoint(tadBuffer);
        AllocationPoint pointOff = AtomicAllocator.getInstance().getAllocationPoint(offBuffer);
        TestCase.assertEquals(CONSTANT, pointTad.getAllocationStatus());
        TestCase.assertEquals(DEVICE, pointOff.getAllocationStatus());
    }

    @Test
    public void testDelayedDup1() throws Exception {
        INDArray array = Nd4j.linspace(1, 1000, 1000).reshape(10, 10, 10);
        AllocationPoint pointShape = AtomicAllocator.getInstance().getAllocationPoint(array.shapeInfoDataBuffer());
        AllocationPoint pointArray = AtomicAllocator.getInstance().getAllocationPoint(array);
        TestCase.assertEquals(HOST, pointArray.getAllocationStatus());
        TestCase.assertEquals(HOST, pointShape.getAllocationStatus());
        float sum = array.sumNumber().floatValue();
        pointShape = AtomicAllocator.getInstance().getAllocationPoint(array.shapeInfoDataBuffer());
        pointArray = AtomicAllocator.getInstance().getAllocationPoint(array);
        TestCase.assertEquals(DEVICE, pointArray.getAllocationStatus());
        TestCase.assertEquals(CONSTANT, pointShape.getAllocationStatus());
        INDArray dup = array.dup();
        AllocationPoint dupShape = AtomicAllocator.getInstance().getAllocationPoint(dup.shapeInfoDataBuffer());
        AllocationPoint dupArray = AtomicAllocator.getInstance().getAllocationPoint(dup);
        TestCase.assertEquals(DEVICE, dupArray.getAllocationStatus());
        TestCase.assertEquals(CONSTANT, dupShape.getAllocationStatus());
    }

    @Test
    public void testDelayedZeroes1() throws Exception {
        INDArray zeroes = Nd4j.zeros(10);
        zeroes.putScalar(1, 1.0F);
        zeroes.putScalar(2, 1.0F);
        float sum = zeroes.sumNumber().floatValue();
        TestCase.assertEquals(2.0F, sum, 1.0E-4F);
    }
}

