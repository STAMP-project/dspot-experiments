package org.nd4j.linalg.shape;


import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * This class contains tests for new Long shapes
 *
 * @author raver119@gmail.com
 */
@RunWith(Parameterized.class)
public class LongShapeTests extends BaseNd4jTest {
    public LongShapeTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testLongBuffer_1() {
        val exp = new long[]{ 2, 5, 3, 3, 1, 0, 1, 99 };
        val buffer = Nd4j.getDataBufferFactory().createLong(exp);
        val java = buffer.asLong();
        BaseNd4jTest.assertArrayEquals(exp, java);
    }

    @Test
    public void testLongShape_1() {
        val exp = new long[]{ 2, 5, 3, 3, 1, 0, 1, 99 };
        val array = Nd4j.createUninitialized(5, 3);
        val buffer = array.shapeInfoDataBuffer();
        val java = buffer.asLong();
        BaseNd4jTest.assertArrayEquals(exp, java);
        Assert.assertEquals(8, buffer.getElementSize());
    }
}

