package org.nd4j.linalg.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.base.Preconditions;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;


public class PreconditionsTest extends BaseNd4jTest {
    public PreconditionsTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void test() {
        INDArray arr = Nd4j.linspace(1, 60, 60).reshape('c', 3, 4, 5);
        try {
            Preconditions.checkArgument(false, "Shape is: %ndShape with rank %ndRank", arr, arr);
            Assert.fail("Expected exception");
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.assertEquals("Shape is: [3, 4, 5] with rank 3", t.getMessage());
        }
        try {
            Preconditions.checkArgument(false, "Stride is: %ndStride with shape info %ndSInfo", arr, arr);
            Assert.fail("Expected exception");
        } catch (Throwable t) {
            String si = arr.shapeInfoToString().replaceAll("\n", "");
            Assert.assertEquals(((("Stride is: " + (Arrays.toString(arr.stride()))) + " with shape info ") + si), t.getMessage());
        }
        INDArray asVector = arr.reshape(arr.length());
        try {
            Preconditions.checkArgument(false, "First 10: %nd10", arr);
            Assert.fail("Expected exception");
        } catch (Throwable t) {
            INDArray get10 = asVector.get(NDArrayIndex.interval(0, 10));
            Assert.assertEquals(("First 10: " + get10), t.getMessage());
        }
    }
}

