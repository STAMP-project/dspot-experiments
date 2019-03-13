package org.nd4j.list;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.list.matrix.MatrixNDArrayList;


public class NDArrayListTest {
    @Test
    public void testList() {
        NDArrayList ndArrayList = new NDArrayList();
        List<Double> arrayAssertion = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            ndArrayList.add(((double) (i)));
            arrayAssertion.add(((double) (i)));
        }
        Assert.assertEquals(arrayAssertion.size(), arrayAssertion.size());
        Assert.assertEquals(arrayAssertion, ndArrayList);
        arrayAssertion.remove(1);
        ndArrayList.remove(1);
        Assert.assertEquals(ndArrayList, arrayAssertion);
        arrayAssertion.remove(2);
        ndArrayList.remove(2);
        Assert.assertEquals(ndArrayList, arrayAssertion);
        arrayAssertion.add(5, 8.0);
        ndArrayList.add(5, 8.0);
        Assert.assertEquals(arrayAssertion, ndArrayList);
        Assert.assertEquals(arrayAssertion.contains(8.0), ndArrayList.contains(8.0));
        Assert.assertEquals(arrayAssertion.indexOf(8.0), ndArrayList.indexOf(8.0));
        Assert.assertEquals(arrayAssertion.lastIndexOf(8.0), ndArrayList.lastIndexOf(8.0));
        Assert.assertEquals(ndArrayList.size(), ndArrayList.array().length());
    }

    @Test
    public void testMatrixList() {
        MatrixNDArrayList matrixNDArrayList = new MatrixNDArrayList();
        for (int i = 0; i < 5; i++) {
            NDArrayList ndArrayList = new NDArrayList();
            for (int j = 0; j < 4; j++) {
                ndArrayList.add(((double) (j)));
            }
            matrixNDArrayList.add(ndArrayList);
        }
        Assert.assertEquals(5, matrixNDArrayList.array().rows());
        Assert.assertFalse(matrixNDArrayList.isEmpty());
        Assert.assertEquals(0.0, matrixNDArrayList.getEntry(0, 0));
    }
}

