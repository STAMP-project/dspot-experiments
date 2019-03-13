package org.nd4j.linalg.shape;


import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.primitives.Triple;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class ShapeTests extends BaseNd4jTest {
    public ShapeTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testRowColVectorVsScalar() {
        INDArray arr = Nd4j.create(2);
        Assert.assertTrue(arr.isRowVector());
        INDArray colVector = arr.reshape(2, 1);
        Assert.assertTrue(colVector.isColumnVector());
        Assert.assertFalse(arr.isScalar());
        Assert.assertFalse(colVector.isScalar());
        INDArray arr3 = Nd4j.scalar(1.0);
        Assert.assertFalse(arr3.isColumnVector());
        Assert.assertFalse(arr3.isRowVector());
    }

    @Test
    public void testSixteenZeroOne() {
        INDArray baseArr = Nd4j.linspace(1, 16, 16).reshape(2, 2, 2, 2);
        Assert.assertEquals(4, baseArr.tensorssAlongDimension(0, 1));
        INDArray columnVectorFirst = Nd4j.create(new double[][]{ new double[]{ 1, 3 }, new double[]{ 2, 4 } });
        INDArray columnVectorSecond = Nd4j.create(new double[][]{ new double[]{ 9, 11 }, new double[]{ 10, 12 } });
        INDArray columnVectorThird = Nd4j.create(new double[][]{ new double[]{ 5, 7 }, new double[]{ 6, 8 } });
        INDArray columnVectorFourth = Nd4j.create(new double[][]{ new double[]{ 13, 15 }, new double[]{ 14, 16 } });
        INDArray[] assertions = new INDArray[]{ columnVectorFirst, columnVectorSecond, columnVectorThird, columnVectorFourth };
        for (int i = 0; i < (baseArr.tensorssAlongDimension(0, 1)); i++) {
            INDArray test = baseArr.tensorAlongDimension(i, 0, 1);
            Assert.assertEquals(("Wrong at index " + i), assertions[i], test);
        }
    }

    @Test
    public void testVectorAlongDimension1() {
        INDArray arr = Nd4j.create(1, 5, 5);
        Assert.assertEquals(arr.vectorsAlongDimension(0), 5);
        Assert.assertEquals(arr.vectorsAlongDimension(1), 5);
        for (int i = 0; i < (arr.vectorsAlongDimension(0)); i++) {
            if ((i < ((arr.vectorsAlongDimension(0)) - 1)) && (i > 0))
                Assert.assertEquals(25, arr.vectorAlongDimension(i, 0).length());

        }
    }

    @Test
    public void testSixteenSecondDim() {
        INDArray baseArr = Nd4j.linspace(1, 16, 16).reshape(2, 2, 2, 2);
        INDArray[] assertions = new INDArray[]{ Nd4j.create(new double[]{ 1, 5 }), Nd4j.create(new double[]{ 9, 13 }), Nd4j.create(new double[]{ 3, 7 }), Nd4j.create(new double[]{ 11, 15 }), Nd4j.create(new double[]{ 2, 6 }), Nd4j.create(new double[]{ 10, 14 }), Nd4j.create(new double[]{ 4, 8 }), Nd4j.create(new double[]{ 12, 16 }) };
        for (int i = 0; i < (baseArr.tensorssAlongDimension(2)); i++) {
            INDArray arr = baseArr.tensorAlongDimension(i, 2);
            Assert.assertEquals(("Failed at index " + i), assertions[i], arr);
        }
    }

    @Test
    public void testVectorAlongDimension() {
        INDArray arr = Nd4j.linspace(1, 24, 24).reshape(4, 3, 2);
        INDArray assertion = Nd4j.create(new float[]{ 5, 17 }, new long[]{ 1, 2 });
        INDArray vectorDimensionTest = arr.vectorAlongDimension(1, 2);
        Assert.assertEquals(assertion, vectorDimensionTest);
        INDArray zeroOne = arr.vectorAlongDimension(0, 1);
        Assert.assertEquals(zeroOne, Nd4j.create(new float[]{ 1, 5, 9 }));
        INDArray testColumn2Assertion = Nd4j.create(new float[]{ 13, 17, 21 });
        INDArray testColumn2 = arr.vectorAlongDimension(1, 1);
        Assert.assertEquals(testColumn2Assertion, testColumn2);
        INDArray testColumn3Assertion = Nd4j.create(new float[]{ 2, 6, 10 });
        INDArray testColumn3 = arr.vectorAlongDimension(2, 1);
        Assert.assertEquals(testColumn3Assertion, testColumn3);
        INDArray v1 = Nd4j.linspace(1, 4, 4).reshape(new long[]{ 2, 2 });
        INDArray testColumnV1 = v1.vectorAlongDimension(0, 0);
        INDArray testColumnV1Assertion = Nd4j.create(new float[]{ 1, 2 });
        Assert.assertEquals(testColumnV1Assertion, testColumnV1);
        INDArray testRowV1 = v1.vectorAlongDimension(1, 0);
        INDArray testRowV1Assertion = Nd4j.create(new float[]{ 3, 4 });
        Assert.assertEquals(testRowV1Assertion, testRowV1);
    }

    @Test
    public void testThreeTwoTwo() {
        INDArray threeTwoTwo = Nd4j.linspace(1, 12, 12).reshape(3, 2, 2);
        INDArray[] assertions = new INDArray[]{ Nd4j.create(new double[]{ 1, 4 }), Nd4j.create(new double[]{ 7, 10 }), Nd4j.create(new double[]{ 2, 5 }), Nd4j.create(new double[]{ 8, 11 }), Nd4j.create(new double[]{ 3, 6 }), Nd4j.create(new double[]{ 9, 12 }) };
        Assert.assertEquals(assertions.length, threeTwoTwo.tensorssAlongDimension(1));
        for (int i = 0; i < (assertions.length); i++) {
            INDArray test = threeTwoTwo.tensorAlongDimension(i, 1);
            Assert.assertEquals(assertions[i], test);
        }
    }

    @Test
    public void testNoCopy() {
        INDArray threeTwoTwo = Nd4j.linspace(1, 12, 12);
        INDArray arr = Shape.newShapeNoCopy(threeTwoTwo, new long[]{ 3, 2, 2 }, true);
        BaseNd4jTest.assertArrayEquals(arr.shape(), new long[]{ 3, 2, 2 });
    }

    @Test
    public void testThreeTwoTwoTwo() {
        INDArray threeTwoTwo = Nd4j.linspace(1, 12, 12).reshape(3, 2, 2);
        INDArray[] assertions = new INDArray[]{ Nd4j.create(new double[]{ 1, 7 }), Nd4j.create(new double[]{ 4, 10 }), Nd4j.create(new double[]{ 2, 8 }), Nd4j.create(new double[]{ 5, 11 }), Nd4j.create(new double[]{ 3, 9 }), Nd4j.create(new double[]{ 6, 12 }) };
        Assert.assertEquals(assertions.length, threeTwoTwo.tensorssAlongDimension(2));
        for (int i = 0; i < (assertions.length); i++) {
            INDArray test = threeTwoTwo.tensorAlongDimension(i, 2);
            Assert.assertEquals(assertions[i], test);
        }
    }

    @Test
    public void testNewAxis() {
        INDArray tensor = Nd4j.linspace(1, 12, 12).reshape(3, 2, 2);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 1, 7 }, new double[]{ 4, 10 } }).reshape(1, 2, 2);
        INDArray tensorGet = tensor.get(NDArrayIndex.point(0), NDArrayIndex.newAxis());
        Assert.assertEquals(assertion, tensorGet);
    }

    @Test
    public void testSixteenFirstDim() {
        INDArray baseArr = Nd4j.linspace(1, 16, 16).reshape(2, 2, 2, 2);
        INDArray[] assertions = new INDArray[]{ Nd4j.create(new double[]{ 1, 3 }), Nd4j.create(new double[]{ 9, 11 }), Nd4j.create(new double[]{ 5, 7 }), Nd4j.create(new double[]{ 13, 15 }), Nd4j.create(new double[]{ 2, 4 }), Nd4j.create(new double[]{ 10, 12 }), Nd4j.create(new double[]{ 6, 8 }), Nd4j.create(new double[]{ 14, 16 }) };
        for (int i = 0; i < (baseArr.tensorssAlongDimension(1)); i++) {
            INDArray arr = baseArr.tensorAlongDimension(i, 1);
            Assert.assertEquals(("Failed at index " + i), assertions[i], arr);
        }
    }

    @Test
    public void testDimShuffle() {
        INDArray scalarTest = Nd4j.scalar(0.0);
        INDArray broadcast = scalarTest.dimShuffle(new Object[]{ 'x' }, new long[]{ 0, 1 }, new boolean[]{ true, true });
        Assert.assertTrue(((broadcast.rank()) == 3));
        INDArray rowVector = Nd4j.linspace(1, 4, 4);
        Assert.assertEquals(rowVector, rowVector.dimShuffle(new Object[]{ 0, 1 }, new int[]{ 0, 1 }, new boolean[]{ false, false }));
        // add extra dimension to row vector in middle
        INDArray rearrangedRowVector = rowVector.dimShuffle(new Object[]{ 0, 'x', 1 }, new int[]{ 0, 1 }, new boolean[]{ true, true });
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1, 4 }, rearrangedRowVector.shape());
        INDArray dimshuffed = rowVector.dimShuffle(new Object[]{ 'x', 0, 'x', 'x' }, new long[]{ 0, 1 }, new boolean[]{ true, true });
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1, 1, 1, 4 }, dimshuffed.shape());
    }

    @Test
    public void testEight() {
        INDArray baseArr = Nd4j.linspace(1, 8, 8).reshape(2, 2, 2);
        Assert.assertEquals(2, baseArr.tensorssAlongDimension(0, 1));
        INDArray columnVectorFirst = Nd4j.create(new double[][]{ new double[]{ 1, 3 }, new double[]{ 2, 4 } });
        INDArray columnVectorSecond = Nd4j.create(new double[][]{ new double[]{ 5, 7 }, new double[]{ 6, 8 } });
        Assert.assertEquals(columnVectorFirst, baseArr.tensorAlongDimension(0, 0, 1));
        Assert.assertEquals(columnVectorSecond, baseArr.tensorAlongDimension(1, 0, 1));
    }

    @Test
    public void testBroadcastShapes() {
        // Test cases: in1Shape, in2Shape, shapeOf(op(in1,in2))
        List<Triple<long[], long[], long[]>> testCases = new ArrayList<>();
        testCases.add(new Triple(new long[]{ 3, 1 }, new long[]{ 1, 4 }, new long[]{ 3, 4 }));
        testCases.add(new Triple(new long[]{ 3, 1 }, new long[]{ 3, 4 }, new long[]{ 3, 4 }));
        testCases.add(new Triple(new long[]{ 3, 4 }, new long[]{ 1, 4 }, new long[]{ 3, 4 }));
        testCases.add(new Triple(new long[]{ 3, 4, 1 }, new long[]{ 1, 1, 5 }, new long[]{ 3, 4, 5 }));
        testCases.add(new Triple(new long[]{ 3, 4, 1 }, new long[]{ 3, 1, 5 }, new long[]{ 3, 4, 5 }));
        testCases.add(new Triple(new long[]{ 3, 1, 5 }, new long[]{ 1, 4, 1 }, new long[]{ 3, 4, 5 }));
        testCases.add(new Triple(new long[]{ 3, 1, 5 }, new long[]{ 1, 4, 5 }, new long[]{ 3, 4, 5 }));
        testCases.add(new Triple(new long[]{ 3, 1, 5 }, new long[]{ 3, 4, 5 }, new long[]{ 3, 4, 5 }));
        testCases.add(new Triple(new long[]{ 3, 1, 1, 1 }, new long[]{ 1, 4, 5, 6 }, new long[]{ 3, 4, 5, 6 }));
        testCases.add(new Triple(new long[]{ 1, 1, 1, 6 }, new long[]{ 3, 4, 5, 6 }, new long[]{ 3, 4, 5, 6 }));
        testCases.add(new Triple(new long[]{ 1, 4, 5, 1 }, new long[]{ 3, 1, 1, 6 }, new long[]{ 3, 4, 5, 6 }));
        testCases.add(new Triple(new long[]{ 1, 6 }, new long[]{ 3, 4, 5, 1 }, new long[]{ 3, 4, 5, 6 }));
        for (Triple<long[], long[], long[]> t : testCases) {
            val x = t.getFirst();
            val y = t.getSecond();
            val exp = t.getThird();
            val act = Shape.broadcastOutputShape(x, y);
            BaseNd4jTest.assertArrayEquals(exp, act);
        }
    }
}

