package org.nd4j.linalg.api.indexing.resolve;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.PointIndex;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class NDArrayIndexResolveTests extends BaseNd4jTest {
    public NDArrayIndexResolveTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testResolvePoint() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArrayIndex[] test = NDArrayIndex.resolve(arr.shape(), NDArrayIndex.point(1));
        INDArrayIndex[] assertion = new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.all() };
        BaseNd4jTest.assertArrayEquals(assertion, test);
        INDArrayIndex[] allAssertion = new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all() };
        BaseNd4jTest.assertArrayEquals(allAssertion, NDArrayIndex.resolve(arr.shape(), NDArrayIndex.all()));
        INDArrayIndex[] allAndOne = new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.point(1) };
        BaseNd4jTest.assertArrayEquals(allAndOne, NDArrayIndex.resolve(arr.shape(), allAndOne));
    }

    @Test
    public void testResolvePointVector() {
        INDArray arr = Nd4j.linspace(1, 4, 4);
        INDArrayIndex[] getPoint = new INDArrayIndex[]{ NDArrayIndex.point(1) };
        INDArrayIndex[] resolved = NDArrayIndex.resolve(arr.shape(), getPoint);
        if ((getPoint.length) == (resolved.length))
            BaseNd4jTest.assertArrayEquals(getPoint, resolved);
        else {
            Assert.assertEquals(2, resolved.length);
            Assert.assertTrue(((resolved[0]) instanceof PointIndex));
            Assert.assertEquals(0, resolved[0].current());
            Assert.assertTrue(((resolved[1]) instanceof PointIndex));
            Assert.assertEquals(1, resolved[1].current());
        }
    }
}

