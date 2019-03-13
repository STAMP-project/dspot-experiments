package jcuda.jcublas.ops;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class CudaReduce3Tests {
    /**
     * Norm2 + cuBlas dot call
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPinnedCosineSim() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.create(new float[]{ 2.01F, 2.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F });
        INDArray array2 = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        double similarity = Transforms.cosineSim(array1, array2);
        System.out.println(("Cosine similarity: " + similarity));
        Assert.assertEquals(0.95F, similarity, 0.01F);
    }

    @Test
    public void testPinnedManhattanDistance() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.create(new float[]{ 0.0F, 1.0F, 2.0F, 3.0F, 4.0F });
        INDArray array2 = Nd4j.create(new float[]{ 0.5F, 1.5F, 2.5F, 3.5F, 4.5F });
        double result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.accum.distances.ManhattanDistance(array1, array2)).getFinalResult().doubleValue();
        System.out.println(("Distance: " + result));
        Assert.assertEquals(2.5, result, 0.01);
        System.out.println(("Array1: " + array1));
        System.out.println(("Array2: " + array2));
    }

    @Test
    public void testPinnedEuclideanDistance() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.create(new float[]{ 0.0F, 1.0F, 2.0F, 3.0F, 4.0F });
        INDArray array2 = Nd4j.create(new float[]{ 0.5F, 1.5F, 2.5F, 3.5F, 4.5F });
        double result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.accum.distances.EuclideanDistance(array1, array2)).getFinalResult().doubleValue();
        System.out.println(("Distance: " + result));
        Assert.assertEquals(1.118033988749895, result, 0.01);
    }

    @Test
    public void testPinnedEuclideanDistance2() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 10000, 10000);
        INDArray array2 = Nd4j.linspace(1, 9000, 10000);
        float result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.accum.distances.EuclideanDistance(array1, array2)).getFinalResult().floatValue();
        System.out.println(("Distance: " + result));
        Assert.assertEquals(57736.473F, result, 0.01F);
    }

    @Test
    public void testPinnedManhattanDistance2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 1000, 1000);
        INDArray array2 = Nd4j.linspace(1, 900, 1000);
        double result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.accum.distances.ManhattanDistance(array1, array2)).getFinalResult().doubleValue();
        Assert.assertEquals(50000.0, result, 0.001F);
    }

    @Test
    public void testPinnedCosineSimilarity2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 1000, 1000);
        INDArray array2 = Nd4j.linspace(100, 200, 1000);
        double result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.accum.distances.CosineSimilarity(array1, array2)).getFinalResult().doubleValue();
        Assert.assertEquals(0.945F, result, 0.001F);
    }

    @Test
    public void testPinnedEuclideanDistance3() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 10000, 130);
        INDArray array2 = Nd4j.linspace(1, 9000, 130);
        float result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.accum.distances.EuclideanDistance(array1, array2)).getFinalResult().floatValue();
        System.out.println(("Distance: " + result));
        Assert.assertEquals(6595.551F, result, 0.01F);
    }

    @Test
    public void testPinnedManhattanDistance3() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 10000, 98);
        INDArray array2 = Nd4j.linspace(1, 9000, 98);
        float result = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.accum.distances.ManhattanDistance(array1, array2)).getFinalResult().floatValue();
        Assert.assertEquals(49000.004F, result, 0.01F);
    }

    @Test
    public void testEqualityOp1() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 10000, 2048);
        INDArray array2 = Nd4j.linspace(1, 9000, 2048);
        Assert.assertNotEquals(array1, array2);
    }

    @Test
    public void testEqualityOp2() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 10000, 2048);
        INDArray array2 = Nd4j.linspace(1, 10000, 2048);
        Assert.assertEquals(array1, array2);
    }
}

