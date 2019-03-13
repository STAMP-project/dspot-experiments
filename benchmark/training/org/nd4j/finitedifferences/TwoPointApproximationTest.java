package org.nd4j.finitedifferences;


import DataBuffer.Type;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.function.Function;
import org.nd4j.linalg.io.ClassPathResource;


public class TwoPointApproximationTest {
    private Type dtype;

    @Test
    public void testLinspaceDerivative() throws Exception {
        String basePath = "/two_points_approx_deriv_numpy/";
        INDArray linspace = Nd4j.createNpyFromInputStream(new ClassPathResource((basePath + "x.npy")).getInputStream());
        INDArray yLinspace = Nd4j.createNpyFromInputStream(new ClassPathResource((basePath + "y.npy")).getInputStream());
        Function<INDArray, INDArray> f = new Function<INDArray, INDArray>() {
            @Override
            public INDArray apply(INDArray indArray) {
                return indArray.add(1);
            }
        };
        INDArray test = TwoPointApproximation.approximateDerivative(f, linspace, null, yLinspace, Nd4j.create(new double[]{ Float.MIN_VALUE, Float.MAX_VALUE }));
        INDArray npLoad = Nd4j.createNpyFromInputStream(new ClassPathResource((basePath + "approx_deriv_small.npy")).getInputStream());
        Assert.assertEquals(npLoad, test);
        System.out.println(test);
    }
}

