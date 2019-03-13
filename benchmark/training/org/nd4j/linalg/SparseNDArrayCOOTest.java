package org.nd4j.linalg;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;


/**
 *
 *
 * @author Audrey Loeffel
 */
// temporary ignored
@Slf4j
@Ignore
public class SparseNDArrayCOOTest {
    double[] data = new double[]{ 10, 1, 2, 3, 4, 5 };

    long[] shape = new long[]{ 2, 2, 2 };

    int[][] indices = new int[][]{ new int[]{ 0, 0, 0, 1, 2, 2 }, new int[]{ 0, 0, 1, 1, 1, 2 }, new int[]{ 1, 2, 2, 1, 0, 1 } };

    @Test
    public void shouldCreateSparseMatrix() {
        INDArray sparse = Nd4j.createSparseCOO(data, indices, shape);
        Assert.assertArrayEquals(shape, sparse.shape());
        Assert.assertEquals(data.length, sparse.nnz());
    }

    @Test
    public void shouldPutScalar() {
        INDArray sparse = Nd4j.createSparseCOO(new double[]{ 1, 2 }, new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 2 } }, new int[]{ 1, 3 });
        sparse.putScalar(1, 3);
    }

    @Test
    public void shouldntPutZero() {
        INDArray sparse = Nd4j.createSparseCOO(new double[]{ 1, 2 }, new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 2 } }, new int[]{ 1, 3 });
        int oldNNZ = sparse.nnz();
        sparse.putScalar(1, 0);
        Assert.assertArrayEquals(new int[]{ 0, 2 }, sparse.getVectorCoordinates().asInt());
        Assert.assertTrue(sparse.isRowVector());
        Assert.assertEquals(oldNNZ, sparse.nnz());
    }

    @Test
    public void shouldRemoveZero() {
        INDArray sparse = Nd4j.createSparseCOO(new double[]{ 1, 2 }, new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 2 } }, new int[]{ 1, 3 });
        sparse.putScalar(0, 0);
        Assert.assertArrayEquals(new int[]{ 2 }, sparse.getVectorCoordinates().asInt());
    }

    @Test
    public void shouldTakeViewInLeftTopCorner() {
        // Test with dense ndarray
        double[] data = new double[]{ 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0 };
        INDArray array = Nd4j.create(data, new int[]{ 5, 5 }, 0, 'c');
        INDArray denseView = array.get(NDArrayIndex.interval(0, 2), NDArrayIndex.interval(0, 2));
        // test with sparse :
        double[] values = new double[]{ 1, 2, 3, 4 };
        int[][] indices = new int[][]{ new int[]{ 0, 3 }, new int[]{ 1, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 4 } };
        INDArray sparseNDArray = Nd4j.createSparseCOO(values, indices, new int[]{ 5, 5 });
        // subarray in the top right corner
        BaseSparseNDArrayCOO sparseView = ((BaseSparseNDArrayCOO) (sparseNDArray.get(NDArrayIndex.interval(0, 2), NDArrayIndex.interval(0, 2))));
        Assert.assertArrayEquals(denseView.shape(), sparseView.shape());
        double[] currentValues = sparseView.data().asDouble();
        Assert.assertArrayEquals(values, currentValues, 1.0E-5);
        Assert.assertArrayEquals(ArrayUtil.flatten(indices), sparseView.getUnderlyingIndices().asInt());
        Assert.assertEquals(0, sparseView.nnz());
        System.out.println(sparseView.sparseInfoDataBuffer());
    }

    @Test
    public void shouldTakeViewInLeftBottomCorner() {
        double[] values = new double[]{ 1, 2, 3, 4 };
        int[][] indices = new int[][]{ new int[]{ 0, 3 }, new int[]{ 1, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 4 } };
        INDArray sparseNDArray = Nd4j.createSparseCOO(values, indices, new int[]{ 5, 5 });
        BaseSparseNDArrayCOO sparseView = ((BaseSparseNDArrayCOO) (sparseNDArray.get(NDArrayIndex.interval(2, 5), NDArrayIndex.interval(0, 2))));
        Assert.assertEquals(1, sparseView.nnz());
        Assert.assertArrayEquals(new double[]{ 3 }, sparseView.getIncludedValues().asDouble(), 0.1);
        Assert.assertArrayEquals(new int[]{ 0, 1 }, sparseView.getIncludedIndices().asInt());
        System.out.println(sparseView.sparseInfoDataBuffer());
    }

    @Test
    public void shouldTakeViewInRightTopCorner() {
        double[] values = new double[]{ 1, 2, 3, 4 };
        int[][] indices = new int[][]{ new int[]{ 0, 3 }, new int[]{ 1, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 4 } };
        INDArray sparseNDArray = Nd4j.createSparseCOO(values, indices, new int[]{ 5, 5 });
        BaseSparseNDArrayCOO sparseView = ((BaseSparseNDArrayCOO) (sparseNDArray.get(NDArrayIndex.interval(0, 2), NDArrayIndex.interval(2, 5))));
        Assert.assertEquals(2, sparseView.nnz());
        Assert.assertArrayEquals(new double[]{ 1, 2 }, sparseView.getIncludedValues().asDouble(), 0.1);
        Assert.assertArrayEquals(new int[]{ 0, 1, 1, 0 }, sparseView.getIncludedIndices().asInt());
        System.out.println(sparseView.sparseInfoDataBuffer());
    }

    @Test
    public void shouldTakeViewInTheMiddle() {
        double[] values = new double[]{ 1, 2, 3, 4 };
        int[][] indices = new int[][]{ new int[]{ 0, 3 }, new int[]{ 1, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 4 } };
        INDArray sparseNDArray = Nd4j.createSparseCOO(values, indices, new int[]{ 5, 5 });
        BaseSparseNDArrayCOO sparseView = ((BaseSparseNDArrayCOO) (sparseNDArray.get(NDArrayIndex.interval(1, 3), NDArrayIndex.interval(1, 3))));
        Assert.assertEquals(2, sparseView.nnz());
        Assert.assertArrayEquals(new double[]{ 2, 3 }, sparseView.getIncludedValues().asDouble(), 0.1);
        Assert.assertArrayEquals(new int[]{ 0, 1, 1, 0 }, sparseView.getIncludedIndices().asInt());
        System.out.println(sparseView.sparseInfoDataBuffer());
    }

    @Test
    public void shouldGetFirstColumn() {
        double[] values = new double[]{ 1, 2, 3, 4 };
        int[][] indices = new int[][]{ new int[]{ 0, 3 }, new int[]{ 1, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 4 } };
        INDArray sparseNDArray = Nd4j.createSparseCOO(values, indices, new int[]{ 5, 5 });
        BaseSparseNDArrayCOO sparseView = ((BaseSparseNDArrayCOO) (sparseNDArray.get(NDArrayIndex.all(), NDArrayIndex.point(0))));
        Assert.assertEquals(0, sparseView.nnz());
        System.out.println(sparseView.sparseInfoDataBuffer());
    }

    @Test
    public void shouldGetRowInTheMiddle() {
        double[] values = new double[]{ 1, 2, 3, 4 };
        int[][] indices = new int[][]{ new int[]{ 0, 3 }, new int[]{ 1, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 4 } };
        INDArray sparseNDArray = Nd4j.createSparseCOO(values, indices, new int[]{ 5, 5 });
        BaseSparseNDArrayCOO sparseView = ((BaseSparseNDArrayCOO) (sparseNDArray.get(NDArrayIndex.point(2), NDArrayIndex.all())));
        Assert.assertEquals(1, sparseView.nnz());
        Assert.assertArrayEquals(new int[]{ 0, 1 }, sparseView.getIncludedIndices().asInt());
        Assert.assertArrayEquals(new double[]{ 3 }, sparseView.getIncludedValues().asDouble(), 0.1);
        System.out.println(sparseView.sparseInfoDataBuffer());
    }

    @Test
    public void shouldGetScalar() {
        double[] values = new double[]{ 1, 2, 3, 4 };
        int[][] indices = new int[][]{ new int[]{ 0, 3 }, new int[]{ 1, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 4 } };
        INDArray sparseNDArray = Nd4j.createSparseCOO(values, indices, new int[]{ 5, 5 });
        BaseSparseNDArrayCOO sparseView = ((BaseSparseNDArrayCOO) (sparseNDArray.get(NDArrayIndex.point(2), NDArrayIndex.point(1))));
        Assert.assertEquals(1, sparseView.nnz());
        Assert.assertArrayEquals(new int[]{ 0, 0 }, sparseView.getIncludedIndices().asInt());
        Assert.assertArrayEquals(new double[]{ 3 }, sparseView.getIncludedValues().asDouble(), 0.1);
        Assert.assertTrue(sparseView.isScalar());
    }

    @Test
    public void shouldTakeView3dimensionArray() {
        int[] shape = new int[]{ 2, 2, 2 };
        double[] values = new double[]{ 2, 1, 4, 3 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 0 }, new int[]{ 1, 1, 1 } };
        INDArray array = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (array.get(NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.all())));
        Assert.assertEquals(2, view.nnz());
        Assert.assertArrayEquals(new long[]{ 2, 2 }, view.shape());
        Assert.assertArrayEquals(new int[]{ 0, 0, 1, 1 }, view.getIncludedIndices().asInt());
        Assert.assertArrayEquals(new double[]{ 2, 1 }, view.getIncludedValues().asDouble(), 0.1);
        System.out.println(view.sparseInfoDataBuffer());
    }

    @Test
    public void shouldTakeViewOfView() {
        int[] shape = new int[]{ 2, 2, 2 };
        double[] values = new double[]{ 2, 1, 4, 3 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 0 }, new int[]{ 1, 1, 1 } };
        INDArray array = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO baseView = ((BaseSparseNDArrayCOO) (array.get(NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.all())));
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (baseView.get(NDArrayIndex.point(1), NDArrayIndex.all())));
        Assert.assertEquals(1, view.nnz());
        Assert.assertArrayEquals(new long[]{ 1, 2 }, view.shape());
        Assert.assertArrayEquals(new int[]{ 0, 1 }, view.getIncludedIndices().asInt());
        Assert.assertArrayEquals(new double[]{ 1 }, view.getIncludedValues().asDouble(), 0.1);
    }

    @Test
    public void shouldTakeViewOfView2() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 1 }, new int[]{ 3, 1, 0 } };
        INDArray array = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO baseView = ((BaseSparseNDArrayCOO) (array.get(NDArrayIndex.interval(1, 4), NDArrayIndex.point(1), NDArrayIndex.all())));
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (baseView.get(NDArrayIndex.all(), NDArrayIndex.point(2))));
        Assert.assertEquals(2, view.nnz());
        Assert.assertArrayEquals(new long[]{ 3, 1 }, view.shape());
        Assert.assertArrayEquals(new int[]{ 0, 0, 1, 0 }, view.getIncludedIndices().asInt());
        Assert.assertArrayEquals(new double[]{ 5, 7 }, view.getIncludedValues().asDouble(), 0.1);
        Assert.assertTrue(view.isColumnVector());
    }

    @Test
    public void shouldGetWithSpecifiedIndexes() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 1 }, new int[]{ 3, 1, 0 } };
        INDArray array = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO newArray = ((BaseSparseNDArrayCOO) (array.get(new SpecifiedIndex(new int[]{ 0, 3 }), NDArrayIndex.all(), NDArrayIndex.all())));
        Assert.assertEquals(4, newArray.nnz());
        Assert.assertArrayEquals(new double[]{ 1, 2, 8, 9 }, newArray.getIncludedValues().asDouble(), 0.1);
        Assert.assertArrayEquals(new int[]{ 0, 0, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0 }, newArray.getIncludedIndices().asInt());
    }

    @Test
    public void shouldGetWithSpecifiedIndexes2() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 1, 0 } };
        INDArray array = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO newArray = ((BaseSparseNDArrayCOO) (array.get(NDArrayIndex.interval(1, 4), new SpecifiedIndex(new int[]{ 0 }), new SpecifiedIndex(new int[]{ 0, 2 }))));
        Assert.assertEquals(2, newArray.nnz());
        Assert.assertArrayEquals(new double[]{ 3, 8 }, newArray.getIncludedValues().asDouble(), 0.1);
        Assert.assertArrayEquals(new int[]{ 0, 0, 2, 1 }, newArray.getIncludedIndices().asInt());
    }

    @Test
    public void specifiedIndexWithDenseArray() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        System.out.println(arr.toString());
        INDArray v = arr.get(NDArrayIndex.interval(1, 3), new SpecifiedIndex(new int[]{ 0 }), new SpecifiedIndex(new int[]{ 0, 2 }));
        System.out.println("v ");
        System.out.println(v.toString());
    }

    @Test
    public void newAxisWithSparseArray() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 1, 0 } };
        INDArray array = Nd4j.createSparseCOO(values, indices, shape);
        INDArray v = array.get(NDArrayIndex.point(0), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
    }

    @Test
    public void nestedSparseViewWithNewAxis() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 1, 0 } };
        INDArray array = Nd4j.createSparseCOO(values, indices, shape);
        System.out.println("\nTaking view (all, point(1), all");
        INDArray v = array.get(NDArrayIndex.all(), NDArrayIndex.point(1));
        System.out.println(v.toString());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(("Fixed dimension " + (v.flags())));
        System.out.println(("sparse offsets " + (v.sparseOffsets())));
        System.out.println(("hidden dimensions " + (v.hiddenDimensions())));
        System.out.println(("number of hidden dimensions " + (getNumHiddenDimension())));
        // shape 4 x 3
        System.out.println("\nTaking view (all new axis");
        INDArray v1 = v.get(NDArrayIndex.all(), NDArrayIndex.newAxis());
        System.out.println(v1.toString());
        System.out.println(v1.shapeInfoDataBuffer());
        System.out.println(("Fixed dimension " + (v1.flags())));
        System.out.println(("sparse offsets " + (v1.sparseOffsets())));
        System.out.println(("hidden dimensions " + (v1.hiddenDimensions())));
        System.out.println(("number of hidden dimensions " + (getNumHiddenDimension())));
        // shape 4 x 1 x 3
        System.out.println("\nTaking view (all new axis");
        v1 = v.get(NDArrayIndex.newAxis(), NDArrayIndex.all(), NDArrayIndex.newAxis());
        System.out.println(v1.toString());
        System.out.println(v1.shapeInfoDataBuffer());
        System.out.println(("Fixed dimension " + (v1.flags())));
        System.out.println(("sparse offsets " + (v1.sparseOffsets())));
        System.out.println(("hidden dimensions " + (v1.hiddenDimensions())));
        System.out.println(("number of hidden dimensions " + (getNumHiddenDimension())));
    }

    @Test
    public void nestedViewWithNewAxis() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        System.out.println(arr.toString());
        System.out.println(arr.shapeInfoDataBuffer());
        System.out.println("\nTaking view (all, point(1), all");
        INDArray v = arr.get(NDArrayIndex.all(), NDArrayIndex.point(1));
        System.out.println(v.toString());
        System.out.println(v.shapeInfoDataBuffer());
        // shape 4 x 3
        System.out.println("\nTaking view (all new axis");
        INDArray v1 = v.get(NDArrayIndex.all(), NDArrayIndex.newAxis());
        System.out.println(v1.toString());
        System.out.println(v1.shapeInfoDataBuffer());
        // shape 4 x 1 x 3
        System.out.println("\nTaking view (all new axis");
        v1 = v1.get(NDArrayIndex.newAxis());
        System.out.println(v1.toString());
        System.out.println(v1.shapeInfoDataBuffer());
        // shape 4 x 3
    }

    @Test
    public void shouldTranslateViewIndexesToOriginal() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 1, 0 } };
        INDArray original = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (original.get(NDArrayIndex.all(), NDArrayIndex.point(1))));
        int[] originalIdx = view.translateToPhysical(new int[]{ 0, 0 });
        int[] exceptedIdx = new int[]{ 0, 1, 0 };
        Assert.assertArrayEquals(exceptedIdx, originalIdx);
    }

    @Test
    public void shouldTranslateViewIndexesToOriginal2() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 1, 0 } };
        INDArray original = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (original.get(NDArrayIndex.all(), NDArrayIndex.newAxis(), NDArrayIndex.point(1))));
        Assert.assertArrayEquals(new int[]{ 0, 1, 0 }, view.translateToPhysical(new int[]{ 0, 0, 0 }));
        Assert.assertArrayEquals(new int[]{ 1, 1, 1 }, view.translateToPhysical(new int[]{ 1, 0, 1 }));
    }

    @Test
    public void shouldTranslateViewIndexesToOriginal3() {
        int[] shape = new int[]{ 4, 2, 3, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2, 0 }, new int[]{ 0, 1, 1, 1 }, new int[]{ 1, 0, 0, 0 }, new int[]{ 1, 0, 1, 0 }, new int[]{ 1, 1, 2, 1 }, new int[]{ 2, 0, 1, 0 }, new int[]{ 2, 1, 2, 0 }, new int[]{ 3, 0, 2, 1 }, new int[]{ 3, 1, 0, 1 } };
        INDArray original = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (original.get(NDArrayIndex.all(), NDArrayIndex.newAxis(), NDArrayIndex.point(1), NDArrayIndex.point(2))));
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 0 }, view.translateToPhysical(new int[]{ 0, 0, 0 }));
        Assert.assertArrayEquals(new int[]{ 1, 1, 2, 1 }, view.translateToPhysical(new int[]{ 1, 0, 1 }));
    }

    @Test
    public void shouldTranslateViewWithPrependNewAxis() {
        // TODO FIX get view with a new prepend axis
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 1, 0 } };
        INDArray original = Nd4j.createSparseCOO(values, indices, shape);
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (original.get(NDArrayIndex.newAxis(), NDArrayIndex.all(), NDArrayIndex.point(1))));
        System.out.println(view.getIncludedIndices());
        System.out.println(view.getIncludedValues());
        Assert.assertArrayEquals(new int[]{ 0, 1, 0 }, view.translateToPhysical(new int[]{ 0, 0, 0 }));
        Assert.assertArrayEquals(new int[]{ 1, 1, 1 }, view.translateToPhysical(new int[]{ 0, 1, 1 }));
        int[] originalIdx = view.translateToPhysical(new int[]{ 0, 1, 2 });
        int[] exceptedIdx = new int[]{ 1, 0, 2 };
        Assert.assertArrayEquals(exceptedIdx, originalIdx);
    }

    @Test
    public void shouldSortCOOIndices() {
        int[] shape = new int[]{ 4, 3, 3 };
        double[] values = new double[]{ 1 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 0 } };
        INDArray original = Nd4j.createSparseCOO(values, indices, shape);
        original.putScalar(2, 2, 2, 3);
        original.putScalar(1, 1, 1, 2);
        BaseSparseNDArrayCOO view = ((BaseSparseNDArrayCOO) (original.get(NDArrayIndex.all())));
        int[] expectedIdx = new int[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 };
        double[] expectedValues = new double[]{ 1, 2, 3 };
        Assert.assertArrayEquals(expectedIdx, view.getIncludedIndices().asInt());
        Assert.assertArrayEquals(expectedValues, view.getIncludedValues().asDouble(), 1.0E-5);
        Assert.assertTrue((view == original));
    }

    @Test
    public void testWithDense() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        System.out.println(arr);
        INDArray view = arr.get(NDArrayIndex.all(), NDArrayIndex.point(1));
        // System.out.println(view.shapeInfoDataBuffer());
        view = arr.get(NDArrayIndex.newAxis(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.all());
        System.out.println("view");
        System.out.println(view);
        System.out.println(view.shapeInfoDataBuffer());
    }

    @Test
    public void newAxisWithDenseArray() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        System.out.println(arr.toString());
        System.out.println(arr.shapeInfoDataBuffer());
        System.out.println("\npoint 0");
        INDArray v = arr.get(NDArrayIndex.point(0));
        System.out.println(v.shapeInfoDataBuffer());
        // => shape 2 x 3
        System.out.println("new axis, all, point 1");
        v = arr.get(NDArrayIndex.newAxis(), NDArrayIndex.all(), NDArrayIndex.point(1));
        // System.out.println(v.toString());
        v = arr.get(NDArrayIndex.interval(1, 4), NDArrayIndex.point(0), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(v.isView());
        // => shape 1 x 2 x 3
        System.out.println("\npoint 0, newaxis");
        v = arr.get(NDArrayIndex.point(0), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(v.isView());
        // => shape 1 x 2 x 3
        System.out.println("\n point 0, newaxis, newaxis");
        v = arr.get(NDArrayIndex.point(0), NDArrayIndex.newAxis(), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        // => shape 1 x 1 x 2 x 3
        System.out.println("\n new axis, point 0, newaxis");
        v = arr.get(NDArrayIndex.newAxis(), NDArrayIndex.point(0), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        // => shape 1 x 1 x 2 x 3
        System.out.println("\nget( new axis, point(0), point(0), new axis)");
        v = arr.get(NDArrayIndex.newAxis(), NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(v.toString());
        // => shape 1 x 1 x 3 x 1
        System.out.println("\nget( specified(1), specified(0), new axis)");
        v = arr.get(new SpecifiedIndex(0), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(v.toString());
        // => crash
        // System.out.println("\nget( new axis, point(0), new axis, point(0))");
        // v = arr.get( NDArrayIndex.newAxis(), NDArrayIndex.point(0), NDArrayIndex.newAxis(),  NDArrayIndex.point(0));
        // System.out.println(v.shapeInfoDataBuffer());
        // System.out.println(v.toString());
        // => crash
        System.out.println("\n interval(0, 2), newaxis");
        v = arr.get(NDArrayIndex.interval(0, 2), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        // => shape 1 x 2 x 2 x 3 - new axis is added at the first position
        /* System.out.println("\n point 0 , all, new axis");
        v = arr.get(
        NDArrayIndex.point(0),
        NDArrayIndex.all(),
        NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
         */
        // => crash
    }

    @Test
    public void testDenseNewAxisWithSpecifiedIdx() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        INDArray v = arr.get(new SpecifiedIndex(0), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(v.toString());
        // null pointer exception in shapeoffsetresolution.exec
    }

    @Test
    public void testDenseNewAxisWithSpecifiedIdx2() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        INDArray v = arr.get(NDArrayIndex.newAxis(), new SpecifiedIndex(0, 1), NDArrayIndex.all());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(v.toString());
        // null pointer exception in shapeoffsetresolution.exec
    }

    @Test
    public void testDenseNewAxisWithSpecifiedIdx3() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        INDArray v = arr.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.newAxis());
        System.out.println(v.shapeInfoDataBuffer());
        System.out.println(v.toString());
        // IndexOutOfBoundsException: Index: 2, Size: 1
        // in shapeoffsetresolution.exec
    }

    @Test
    public void testDenseWithNewAxis() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        INDArray view = arr.get(NDArrayIndex.newAxis(), NDArrayIndex.all(), NDArrayIndex.point(1));
        System.out.println(view);
    }

    @Test
    public void testWithPrependNewAxis() {
        INDArray arr = Nd4j.rand(new int[]{ 4, 2, 3 });
        System.out.println(arr.toString());
        System.out.println(arr.shapeInfoDataBuffer());
        System.out.println("new axis, all, point 1");
        INDArray v = arr.get(NDArrayIndex.newAxis(), NDArrayIndex.all(), NDArrayIndex.point(1));
        System.out.println(v.toString());
        System.out.println(v.shapeInfoDataBuffer());
    }

    @Test
    public void binarySearch() {
        int[] shape = new int[]{ 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 2 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 1, 0 } };
        BaseSparseNDArrayCOO array = ((BaseSparseNDArrayCOO) (Nd4j.createSparseCOO(values, indices, shape)));
        Assert.assertEquals(0, array.reverseIndexes(new int[]{ 0, 0, 2 }));
        Assert.assertEquals(7, array.reverseIndexes(new int[]{ 3, 0, 2 }));
        Assert.assertEquals(8, array.reverseIndexes(new int[]{ 3, 1, 0 }));
    }

    @Test
    public void rdmTest() {
        INDArray i = Nd4j.rand(new int[]{ 3, 3, 3 });
        INDArray ii = i.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all());
        System.out.println(ii);
        System.out.println(ii.shapeInfoDataBuffer());
    }

    @Test
    public void tryToFindABugWithHiddenDim() {
        int[] shape = new int[]{ 1, 4, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[][] indices = new int[][]{ new int[]{ 0, 0, 0, 2 }, new int[]{ 0, 0, 1, 1 }, new int[]{ 0, 1, 0, 0 }, new int[]{ 0, 1, 0, 1 }, new int[]{ 0, 1, 1, 2 }, new int[]{ 0, 2, 0, 1 }, new int[]{ 0, 2, 1, 2 }, new int[]{ 0, 3, 0, 2 }, new int[]{ 0, 3, 1, 0 } };
        BaseSparseNDArrayCOO array = ((BaseSparseNDArrayCOO) (Nd4j.createSparseCOO(values, indices, shape)));
        BaseSparseNDArrayCOO view1 = ((BaseSparseNDArrayCOO) (array.get(NDArrayIndex.point(0), NDArrayIndex.newAxis(), NDArrayIndex.newAxis(), NDArrayIndex.point(0))));
        System.out.println(view1.shapeInfoDataBuffer());
        System.out.println(view1.sparseInfoDataBuffer());
        BaseSparseNDArrayCOO view2 = ((BaseSparseNDArrayCOO) (view1.get(NDArrayIndex.point(0), NDArrayIndex.newAxis(), NDArrayIndex.newAxis(), NDArrayIndex.point(0))));
        System.out.println(view2.shapeInfoDataBuffer());
        System.out.println(view2.sparseInfoDataBuffer());
    }
}

