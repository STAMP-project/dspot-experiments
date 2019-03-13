package org.nd4j.linalg.dimensionalityreduction;


import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 * Created by huitseeker on 7/28/17.
 */
@RunWith(Parameterized.class)
public class TestRandomProjection extends BaseNd4jTest {
    INDArray z1 = Nd4j.createUninitialized(new int[]{ ((int) (1000000.0)), 1000 });

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    public TestRandomProjection(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testJohnsonLindenStraussDim() {
        Assert.assertEquals(663, ((int) (RandomProjection.johnsonLindenStraussMinDim(((int) (1000000.0)), 0.5).get(0))));
        Assert.assertTrue(RandomProjection.johnsonLindenStraussMinDim(((int) (1000000.0)), 0.5).equals(new ArrayList<Integer>(Arrays.asList(663))));
        ArrayList<Integer> res1 = new ArrayList<Integer>(Arrays.asList(663, 11841, 1112658));
        Assert.assertEquals(RandomProjection.johnsonLindenStraussMinDim(((int) (1000000.0)), 0.5, 0.1, 0.01), res1);
        ArrayList<Integer> res2 = new ArrayList<>(Arrays.asList(7894, 9868, 11841));
        Assert.assertEquals(RandomProjection.johnsonLindenstraussMinDim(new int[]{ ((int) (10000.0)), ((int) (100000.0)), ((int) (1000000.0)) }, 0.1), res2);
    }

    @Test
    public void testTargetShape() {
        BaseNd4jTest.assertArrayEquals(RandomProjection.targetShape(z1, 0.5), new long[]{ 1000, 663 });
        BaseNd4jTest.assertArrayEquals(RandomProjection.targetShape(Nd4j.createUninitialized(new int[]{ ((int) (100.0)), 225 }), 0.5), new long[]{ 225, 221 });
        // non-changing estimate
        BaseNd4jTest.assertArrayEquals(RandomProjection.targetShape(z1, 700), new long[]{ 1000, 700 });
    }

    @Test
    public void testTargetEpsilonChecks() {
        exception.expect(IllegalArgumentException.class);
        // wrong rel. error
        RandomProjection.targetShape(z1, 0.0);
    }

    @Test
    public void testTargetShapeTooHigh() {
        exception.expect(ND4JIllegalStateException.class);
        // original dimension too small
        RandomProjection.targetShape(Nd4j.createUninitialized(new int[]{ ((int) (100.0)), 1 }), 0.5);
        // target dimension too high
        RandomProjection.targetShape(z1, 1001);
        // suggested dimension too high
        RandomProjection.targetShape(z1, 0.1);
        // original samples too small
        RandomProjection.targetShape(Nd4j.createUninitialized(new int[]{ 1, 1000 }), 0.5);
    }

    @Test
    public void testBasicEmbedding() {
        INDArray z1 = Nd4j.randn(10000, 500);
        RandomProjection rp = new RandomProjection(0.5);
        INDArray res = Nd4j.zeros(10000, 442);
        INDArray z2 = rp.projecti(z1, res);
        BaseNd4jTest.assertArrayEquals(new long[]{ 10000, 442 }, z2.shape());
    }

    @Test
    public void testEmbedding() {
        INDArray z1 = Nd4j.randn(2000, 400);
        INDArray z2 = z1.dup();
        INDArray result = Transforms.allEuclideanDistances(z1, z2, 1);
        RandomProjection rp = new RandomProjection(0.5);
        INDArray zp = rp.project(z1);
        INDArray zp2 = zp.dup();
        INDArray projRes = Transforms.allEuclideanDistances(zp, zp2, 1);
        // check that the automatically tuned values for the density respect the
        // contract for eps: pairwise distances are preserved according to the
        // Johnson-Lindenstrauss lemma
        INDArray ratios = projRes.div(result);
        for (int i = 0; i < (ratios.length()); i++) {
            double val = ratios.getDouble(i);
            // this avoids the NaNs we get along the diagonal
            if (val == val) {
                Assert.assertTrue(((ratios.getDouble(i)) < 1.5));
            }
        }
    }
}

