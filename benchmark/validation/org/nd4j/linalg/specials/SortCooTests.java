package org.nd4j.linalg.specials;


import DataBuffer.Type;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.LongPointer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.nativeblas.NativeOpsHolder;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class SortCooTests extends BaseNd4jTest {
    Type initialType;

    public SortCooTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void sortSparseCooIndicesSort1() throws Exception {
        // FIXME: we don't want this test running on cuda for now
        if (Nd4j.getExecutioner().getClass().getCanonicalName().toLowerCase().contains("cuda"))
            return;

        val indices = new long[]{ 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1 };
        // we don't care about
        double[] values = new double[]{ 2, 1, 0, 3 };
        val expIndices = new long[]{ 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1 };
        double[] expValues = new double[]{ 0, 1, 2, 3 };
        DataBuffer idx = Nd4j.getDataBufferFactory().createLong(indices);
        DataBuffer val = Nd4j.createBuffer(values);
        log.info("Old indices: {}", Arrays.toString(idx.asInt()));
        NativeOpsHolder.getInstance().getDeviceNativeOps().sortCooIndicesFloat(null, ((LongPointer) (idx.addressPointer())), ((FloatPointer) (val.addressPointer())), 4, 3);
        log.info("New indices: {}", Arrays.toString(idx.asInt()));
        BaseNd4jTest.assertArrayEquals(expIndices, idx.asInt());
        BaseNd4jTest.assertArrayEquals(expValues, val.asDouble(), 1.0E-5);
    }

    @Test
    public void sortSparseCooIndicesSort2() throws Exception {
        // FIXME: we don't want this test running on cuda for now
        if (Nd4j.getExecutioner().getClass().getCanonicalName().toLowerCase().contains("cuda"))
            return;

        val indices = new long[]{ 0, 0, 0, 2, 2, 2, 1, 1, 1 };
        // we don't care about
        double[] values = new double[]{ 2, 1, 3 };
        val expIndices = new long[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 };
        double[] expValues = new double[]{ 2, 3, 1 };
        DataBuffer idx = Nd4j.getDataBufferFactory().createLong(indices);
        DataBuffer val = Nd4j.createBuffer(values);
        NativeOpsHolder.getInstance().getDeviceNativeOps().sortCooIndicesFloat(null, ((LongPointer) (idx.addressPointer())), ((FloatPointer) (val.addressPointer())), 3, 3);
        BaseNd4jTest.assertArrayEquals(expIndices, idx.asInt());
        BaseNd4jTest.assertArrayEquals(expValues, val.asDouble(), 1.0E-5);
    }
}

