package org.nd4j.linalg.api.blas;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.BaseSparseNDArray;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author Audrey Loeffel
 */
// temporary ignored
@Ignore
@RunWith(Parameterized.class)
public class SparseCSRLevel1Test extends BaseNd4jTest {
    private double[] data = new double[]{ 1, 2, 4 };

    private int[] col = new int[]{ 0, 1, 3 };

    private int[] pointerB = new int[]{ 0 };

    private int[] pointerE = new int[]{ 4 };

    private int[] shape = new int[]{ 1, 4 };

    public SparseCSRLevel1Test(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void shouldComputeDot() {
        INDArray sparseVec = Nd4j.createSparseCSR(data, col, pointerB, pointerE, shape);
        // INDArray vec = Nd4j.create( new double[] {1 ,2, 3, 4});
        INDArray matrix = Nd4j.linspace(1, 4, 4).reshape(1, 4);
        INDArray vec = matrix.getRow(0);
        Assert.assertEquals(21, Nd4j.getBlasWrapper().dot(sparseVec, vec), 0.1);
    }

    @Test
    public void shouldComputeNrm2() {
        INDArray sparseVec = Nd4j.createSparseCSR(data, col, pointerB, pointerE, shape);
        Assert.assertEquals(Math.sqrt(21), Nd4j.getBlasWrapper().nrm2(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeAsum() {
        INDArray sparseVec = Nd4j.createSparseCSR(data, col, pointerB, pointerE, shape);
        Assert.assertEquals(7, Nd4j.getBlasWrapper().asum(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeIamax() {
        INDArray sparseVec = Nd4j.createSparseCSR(data, col, pointerB, pointerE, shape);
        Assert.assertEquals(2, Nd4j.getBlasWrapper().iamax(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeIamin() {
        INDArray sparseVec = Nd4j.createSparseCSR(data, col, pointerB, pointerE, shape);
        Assert.assertEquals(0, Nd4j.getBlasWrapper().level1().iamin(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeAxpy() {
        INDArray sparseVec = Nd4j.createSparseCSR(data, col, pointerB, pointerE, shape);
        INDArray vec = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        INDArray expected = Nd4j.create(new double[]{ 2, 4, 3, 8 });
        Nd4j.getBlasWrapper().level1().axpy(vec.length(), 1, sparseVec, vec);
        Assert.assertEquals(getFailureMessage(), expected, vec);
    }

    @Test
    public void shouldComputeRot() {
        // try with dense vectors to get the expected result
        INDArray temp1 = Nd4j.create(new double[]{ 1, 2, 0, 4 });
        INDArray temp2 = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        System.out.println(((("before: " + (temp1.data())) + " ") + (temp2.data())));
        Nd4j.getBlasWrapper().level1().rot(temp1.length(), temp1, temp2, 1, 2);
        System.out.println(((("after: " + (temp1.data())) + " ") + (temp2.data())));
        // before: [1.0,2.0,0.0,4.0]  [1.0,2.0,3.0,4.0]
        // after: [3.0,6.0,6.0,12.0] [-1.0,-2.0,3.0,-4.0]
        INDArray sparseVec = Nd4j.createSparseCSR(data, col, pointerB, pointerE, shape);
        INDArray vec = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        Nd4j.getBlasWrapper().level1().rot(vec.length(), sparseVec, vec, 1, 2);
        System.out.println((((sparseVec.data()) + " ") + (vec.data())));
        // System.out.println("indexes: " + ((BaseSparseNDArray) sparseVec).getVectorCoordinates().toString());
        INDArray expectedSparseVec = Nd4j.createSparseCSR(new double[]{ 3, 6, 6, 12 }, new int[]{ 0, 1, 2, 3 }, new int[]{ 0 }, new int[]{ 4 }, new int[]{ 1, 4 });
        INDArray expectedVec = Nd4j.create(new double[]{ -1, -2, 3, -4 });
        Assert.assertEquals(getFailureMessage(), expectedSparseVec.data(), sparseVec.data());
        Assert.assertEquals(getFailureMessage(), expectedVec, vec);
        // TODO fix it
    }

    @Test
    public void shouldComputeRotWithFullVector() {
        // try with dense vectors to get the expected result
        /* INDArray temp1 = Nd4j.create( new double[] {1 ,2, 3, 4});
        INDArray temp2 = Nd4j.create( new double[] {1 ,2, 3, 4});
        System.out.println("before: " + temp1.data() + " " + temp2.data());
        Nd4j.getBlasWrapper().level1().rot(temp1.length(), temp1, temp2, 1, 2);
        System.out.println("after: " + temp1.data() + " " + temp2.data());
         */
        // before: [1.0,2.0,3.0,4.0]  [1.0,2.0,3.0,4.0]
        // after: [3.0,6.0,0.0,12.0] [-1.0,-2.0,-3.0,-4.0]
        int[] cols = new int[]{ 0, 1, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4 };
        INDArray sparseVec = Nd4j.createSparseCSR(values, cols, pointerB, pointerE, shape);
        INDArray vec = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        Nd4j.getBlasWrapper().level1().rot(vec.length(), sparseVec, vec, 1, 2);
        INDArray expectedSparseVec = Nd4j.createSparseCSR(new double[]{ 3, 6, 9, 12 }, new int[]{ 0, 1, 2, 3 }, new int[]{ 0 }, new int[]{ 4 }, new int[]{ 1, 4 });
        INDArray expectedVec = Nd4j.create(new double[]{ -1, -2, -3, -4 });
        Assert.assertEquals(getFailureMessage(), expectedSparseVec.data(), sparseVec.data());
        Assert.assertEquals(getFailureMessage(), expectedVec, vec);
        if ((expectedSparseVec.isSparse()) && (sparseVec.isSparse())) {
            BaseSparseNDArray vec2 = ((BaseSparseNDArray) (expectedSparseVec));
            BaseSparseNDArray vecSparse2 = ((BaseSparseNDArray) (sparseVec));
            Assert.assertEquals(getFailureMessage(), vec2.getVectorCoordinates(), vecSparse2);
        }
    }
}

