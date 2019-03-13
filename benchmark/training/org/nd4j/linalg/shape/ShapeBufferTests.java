package org.nd4j.linalg.shape;


import java.nio.IntBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.util.ArrayUtil;


/**
 * Created by agibsoncccc on 1/30/16.
 */
@RunWith(Parameterized.class)
public class ShapeBufferTests extends BaseNd4jTest {
    public ShapeBufferTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testRank() {
        int[] shape = new int[]{ 2, 4 };
        int[] stride = new int[]{ 1, 2 };
        IntBuffer buff = Shape.createShapeInformation(shape, stride, 0, 1, 'c').asNioInt();
        int rank = 2;
        Assert.assertEquals(rank, Shape.rank(buff));
    }

    @Test
    public void testArrCreationShape() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        for (int i = 0; i < 2; i++)
            Assert.assertEquals(2, arr.size(i));

        int[] stride = ArrayUtil.calcStrides(new int[]{ 2, 2 });
        for (int i = 0; i < (stride.length); i++) {
            Assert.assertEquals(stride[i], arr.stride(i));
        }
    }

    @Test
    public void testShape() {
        int[] shape = new int[]{ 2, 4 };
        int[] stride = new int[]{ 1, 2 };
        IntBuffer buff = Shape.createShapeInformation(shape, stride, 0, 1, 'c').asNioInt();
        IntBuffer shapeView = Shape.shapeOf(buff);
        Assert.assertTrue(Shape.contentEquals(shape, shapeView));
        IntBuffer strideView = Shape.stride(buff);
        Assert.assertTrue(Shape.contentEquals(stride, strideView));
        Assert.assertEquals('c', Shape.order(buff));
        Assert.assertEquals(1, Shape.elementWiseStride(buff));
        Assert.assertFalse(Shape.isVector(buff));
        Assert.assertTrue(Shape.contentEquals(shape, Shape.shapeOf(buff)));
        Assert.assertTrue(Shape.contentEquals(stride, Shape.stride(buff)));
    }

    @Test
    public void testBuff() {
        int[] shape = new int[]{ 1, 2 };
        int[] stride = new int[]{ 1, 2 };
        IntBuffer buff = Shape.createShapeInformation(shape, stride, 0, 1, 'c').asNioInt();
        Assert.assertTrue(Shape.isVector(buff));
    }
}

