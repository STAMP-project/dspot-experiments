package org.nd4j.linalg.ops;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Rational tanh approximation from
 * https://arxiv.org/pdf/1508.01292v3
 * https://github.com/deeplearning4j/libnd4j/issues/351
 */
@RunWith(Parameterized.class)
public class RationalTanhTest extends BaseNd4jTest {
    public RationalTanhTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void gradientCheck() {
        double eps = 1.0E-6;
        INDArray A = Nd4j.linspace((-3), 3, 10).reshape(2, 5);
        INDArray ADer = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.gradient.RationalTanhDerivative(A.dup()));
        double[] a = A.data().asDouble();
        double[] aDer = ADer.data().asDouble();
        for (int i = 0; i < 10; i++) {
            double empirical = ((RationalTanhTest.f(((a[i]) + eps))) - (RationalTanhTest.f(((a[i]) - eps)))) / (2 * eps);
            double analytic = aDer[i];
            Assert.assertTrue((((Math.abs((empirical - analytic))) / ((Math.abs(empirical)) + (Math.abs(analytic)))) < 0.001));
        }
    }
}

