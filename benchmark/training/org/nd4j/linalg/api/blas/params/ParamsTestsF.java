package org.nd4j.linalg.api.blas.params;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class ParamsTestsF extends BaseNd4jTest {
    public ParamsTestsF(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testGemm() {
        INDArray a = Nd4j.create(2, 2);
        INDArray b = Nd4j.create(2, 3);
        INDArray c = Nd4j.create(2, 3);
        GemmParams params = new GemmParams(a, b, c);
        Assert.assertEquals(a.rows(), params.getM());
        Assert.assertEquals(b.columns(), params.getN());
        Assert.assertEquals(a.columns(), params.getK());
        Assert.assertEquals(a.rows(), params.getLda());
        Assert.assertEquals(b.rows(), params.getLdb());
        Assert.assertEquals(c.rows(), params.getLdc());
    }
}

