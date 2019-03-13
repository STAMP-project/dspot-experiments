package org.nd4j.linalg;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by agibsonccc on 4/1/16.
 */
@Slf4j
@RunWith(Parameterized.class)
public class LoneTest extends BaseNd4jTest {
    public LoneTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testSoftmaxStability() {
        INDArray input = Nd4j.create(new double[]{ -0.75, 0.58, 0.42, 1.03, -0.61, 0.19, -0.37, -0.4, -1.42, -0.04 }).transpose();
        System.out.println(("Input transpose " + (Shape.shapeToString(input.shapeInfo()))));
        INDArray output = Nd4j.create(10, 1);
        System.out.println(("Element wise stride of output " + (output.elementWiseStride())));
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.OldSoftMax(input, output));
    }

    @Test
    public void testFlattenedView() {
        int rows = 8;
        int cols = 8;
        int dim2 = 4;
        int length = rows * cols;
        int length3d = (rows * cols) * dim2;
        INDArray first = Nd4j.linspace(1, length, length).reshape('c', rows, cols);
        INDArray second = Nd4j.create(new int[]{ rows, cols }, 'f').assign(first);
        INDArray third = Nd4j.linspace(1, length3d, length3d).reshape('c', rows, cols, dim2);
        first.addi(0.1);
        second.addi(0.2);
        third.addi(0.3);
        first = first.get(NDArrayIndex.interval(4, 8), NDArrayIndex.interval(0, 2, 8));
        for (int i = 0; i < (first.tensorssAlongDimension(0)); i++) {
            System.out.println(first.tensorAlongDimension(i, 0));
        }
        for (int i = 0; i < (first.tensorssAlongDimension(1)); i++) {
            System.out.println(first.tensorAlongDimension(i, 1));
        }
        second = second.get(NDArrayIndex.interval(3, 7), NDArrayIndex.all());
        third = third.permute(0, 2, 1);
        INDArray cAssertion = Nd4j.create(new double[]{ 33.1, 35.1, 37.1, 39.1, 41.1, 43.1, 45.1, 47.1, 49.1, 51.1, 53.1, 55.1, 57.1, 59.1, 61.1, 63.1 });
        INDArray fAssertion = Nd4j.create(new double[]{ 33.1, 41.1, 49.1, 57.1, 35.1, 43.1, 51.1, 59.1, 37.1, 45.1, 53.1, 61.1, 39.1, 47.1, 55.1, 63.1 });
        Assert.assertEquals(cAssertion, Nd4j.toFlattened('c', first));
        Assert.assertEquals(fAssertion, Nd4j.toFlattened('f', first));
    }

    @Test
    public void testIndexingColVec() {
        int elements = 5;
        INDArray rowVector = Nd4j.linspace(1, elements, elements).reshape(1, elements);
        INDArray colVector = rowVector.transpose();
        int j;
        INDArray jj;
        for (int i = 0; i < elements; i++) {
            j = i + 1;
            Assert.assertEquals((i + 1), colVector.getRow(i).getInt(0));
            Assert.assertEquals((i + 1), rowVector.getColumn(i).getInt(0));
            Assert.assertEquals((i + 1), rowVector.get(NDArrayIndex.interval(i, j)).getInt(0));
            Assert.assertEquals((i + 1), colVector.get(NDArrayIndex.interval(i, j)).getInt(0));
            System.out.println("Making sure index interval will not crash with begin/end vals...");
            jj = colVector.get(NDArrayIndex.interval(i, (i + 10)));
            jj = colVector.get(NDArrayIndex.interval(i, (i + 10)));
        }
    }

    @Test
    public void concatScalarVectorIssue() {
        // A bug was found when the first array that concat sees is a scalar and the rest vectors + scalars
        INDArray arr1 = Nd4j.create(1, 1);
        INDArray arr2 = Nd4j.create(1, 8);
        INDArray arr3 = Nd4j.create(1, 1);
        INDArray arr4 = Nd4j.concat(1, arr1, arr2, arr3);
        Assert.assertTrue(((arr4.sumNumber().floatValue()) <= (Nd4j.EPS_THRESHOLD)));
    }

    @Test
    public void reshapeTensorMmul() {
        INDArray a = Nd4j.linspace(1, 2, 12).reshape(2, 3, 2);
        INDArray b = Nd4j.linspace(3, 4, 4).reshape(2, 2);
        int[][] axes = new int[2][];
        axes[0] = new int[]{ 0, 1 };
        axes[1] = new int[]{ 0, 2 };
        // this was throwing an exception
        INDArray c = Nd4j.tensorMmul(b, a, axes);
    }

    @Test
    public void maskWhenMerge() {
        DataSet dsA = new DataSet(Nd4j.linspace(1, 15, 15).reshape(1, 3, 5), Nd4j.zeros(1, 3, 5));
        DataSet dsB = new DataSet(Nd4j.linspace(1, 9, 9).reshape(1, 3, 3), Nd4j.zeros(1, 3, 3));
        List<DataSet> dataSetList = new ArrayList<DataSet>();
        dataSetList.add(dsA);
        dataSetList.add(dsB);
        DataSet fullDataSet = DataSet.merge(dataSetList);
        Assert.assertTrue(((fullDataSet.getFeaturesMaskArray()) != null));
        DataSet fullDataSetCopy = fullDataSet.copy();
        Assert.assertTrue(((fullDataSetCopy.getFeaturesMaskArray()) != null));
    }

    @Test
    public void testRelu() {
        INDArray aA = Nd4j.linspace((-3), 4, 8).reshape(2, 4);
        INDArray aD = Nd4j.linspace((-3), 4, 8).reshape(2, 4);
        INDArray b = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.Tanh(aA));
        // Nd4j.getExecutioner().execAndReturn(new TanhDerivative(aD));
        System.out.println(aA);
        System.out.println(aD);
        System.out.println(b);
    }

    @Test
    public void testTad() {
        int[] someShape = new int[]{ 2, 1, 3, 3 };
        INDArray a = Nd4j.linspace(1, 18, 18).reshape(someShape);
        INDArray java = a.javaTensorAlongDimension(0, 2, 3);
        INDArray tad = a.tensorAlongDimension(0, 2, 3);
        // assertTrue(a.tensorAlongDimension(0,2,3).rank() == 2); //is rank 3 with an extra 1
        Assert.assertEquals(java, tad);
    }

    // broken at a threshold
    @Test
    public void testArgMax() {
        int max = 63;
        INDArray A = Nd4j.linspace(1, max, max).reshape(1, max);
        int currentArgMax = Nd4j.argMax(A).getInt(0, 0);
        Assert.assertEquals((max - 1), currentArgMax);
        max = 64;
        A = Nd4j.linspace(1, max, max).reshape(1, max);
        currentArgMax = Nd4j.argMax(A).getInt(0, 0);
        System.out.println(("Returned argMax is " + currentArgMax));
        Assert.assertEquals((max - 1), currentArgMax);
    }

    @Test
    public void testConcat3D_Vstack_C() throws Exception {
        int[] shape = new int[]{ 1, 1000, 150 };
        // INDArray cOrder =  Nd4j.rand(shape,123);
        List<INDArray> cArrays = new ArrayList<>();
        List<INDArray> fArrays = new ArrayList<>();
        for (int e = 0; e < 32; e++) {
            cArrays.add(Nd4j.create(shape, 'c').assign(e));
            // fArrays.add(cOrder.dup('f'));
        }
        Nd4j.getExecutioner().commit();
        long time1 = System.currentTimeMillis();
        INDArray res = Nd4j.vstack(cArrays);
        long time2 = System.currentTimeMillis();
        log.info("Time spent: {} ms", (time2 - time1));
        for (int e = 0; e < 32; e++) {
            INDArray tad = res.tensorAlongDimension(e, 1, 2);
            Assert.assertEquals(((double) (e)), tad.meanNumber().doubleValue(), 1.0E-5);
        }
    }

    @Test
    public void testGetRow1() throws Exception {
        INDArray array = Nd4j.create(10000, 10000);
        // Thread.sleep(10000);
        int numTries = 1000;
        List<Long> times = new ArrayList<>();
        long time = 0;
        for (int i = 0; i < numTries; i++) {
            int idx = RandomUtils.nextInt(0, 10000);
            long time1 = System.nanoTime();
            array.getRow(idx);
            long time2 = (System.nanoTime()) - time1;
            times.add(time2);
            time += time2;
        }
        time /= numTries;
        Collections.sort(times);
        log.info("p50: {}; avg: {};", times.get(((times.size()) / 2)), time);
    }

    @Test(expected = Exception.class)
    public void checkIllegalElementOps() {
        INDArray A = Nd4j.linspace(1, 20, 20).reshape(4, 5);
        INDArray B = A.dup().reshape(2, 2, 5);
        // multiplication of arrays of different rank should throw exception
        INDArray C = A.mul(B);
    }

    @Test
    public void checkSliceofSlice() {
        /* Issue 1: Slice of slice with c order and f order views are not equal

        Comment out assert and run then -> Issue 2: Index out of bound exception with certain shapes when accessing elements with getDouble() in f order
        (looks like problem is when rank-1==1) eg. 1,2,1 and 2,2,1
         */
        int[] ranksToCheck = new int[]{ 2, 3, 4, 5 };
        for (int rank = 0; rank < (ranksToCheck.length); rank++) {
            log.info(("\nRunning through rank " + (ranksToCheck[rank])));
            List<Pair<INDArray, String>> allF = NDArrayCreationUtil.getTestMatricesWithVaryingShapes(ranksToCheck[rank], 'f');
            Iterator<Pair<INDArray, String>> iter = allF.iterator();
            while (iter.hasNext()) {
                Pair<INDArray, String> currentPair = iter.next();
                INDArray origArrayF = currentPair.getFirst();
                INDArray sameArrayC = origArrayF.dup('c');
                log.info(("\nLooping through slices for shape " + (currentPair.getSecond())));
                log.info(("\nOriginal array:\n" + origArrayF));
                INDArray viewF = origArrayF.slice(0);
                INDArray viewC = sameArrayC.slice(0);
                log.info(("\nSlice 0, C order:\n" + (viewC.toString())));
                log.info(("\nSlice 0, F order:\n" + (viewF.toString())));
                for (int i = 0; i < (viewF.slices()); i++) {
                    // assertEquals(viewF.slice(i),viewC.slice(i));
                    for (int j = 0; j < (viewF.slice(i).length()); j++) {
                        // if (j>0) break;
                        log.info(((("\nC order slice " + i) + ", element 0 :") + (viewC.slice(i).getDouble(j))));// C order is fine

                        log.info(((("\nF order slice " + i) + ", element 0 :") + (viewF.slice(i).getDouble(j))));// throws index out of bound err on F order

                    }
                }
            } 
        }
    }

    @Test
    public void checkWithReshape() {
        INDArray arr = Nd4j.create(1, 3);
        INDArray reshaped = arr.reshape('f', 3, 1);
        for (int i = 0; i < (reshaped.length()); i++) {
            log.info((("C order element " + i) + (arr.getDouble(i))));
            log.info((("F order element " + i) + (reshaped.getDouble(i))));
        }
        for (int j = 0; j < (arr.slices()); j++) {
            for (int k = 0; k < (arr.slice(j).length()); k++) {
                log.info(((((("\nArr: slice " + j) + " element ") + k) + " ") + (arr.slice(j).getDouble(k))));
            }
        }
        for (int j = 0; j < (reshaped.slices()); j++) {
            for (int k = 0; k < (reshaped.slice(j).length()); k++) {
                log.info(((((("\nReshaped: slice " + j) + " element ") + k) + " ") + (reshaped.slice(j).getDouble(k))));
            }
        }
    }
}

