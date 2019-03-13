/**
 * -
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */
package org.nd4j.linalg.ops;


import DataBuffer.Type.DOUBLE;
import java.util.Arrays;
import java.util.Random;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.string.NDArrayStrings;


@RunWith(Parameterized.class)
public class DerivativeTests extends BaseNd4jTest {
    public static final double REL_ERROR_TOLERANCE = 0.001;

    public DerivativeTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testHardTanhDerivative() {
        // HardTanh:
        // f(x) = 1 if x > 1
        // f(x) = -1 if x < -1
        // f(x) = x otherwise
        // This is piecewise differentiable.
        // f'(x) = 0 if |x|>1
        // f'(x) = 1 otherwise
        // Note for x= +/- 1, HardTanh is not differentiable. Choose f'(+/- 1) = 1
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            expOut[i] = ((Math.abs(x)) <= 1.0) ? 1 : 0;
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new HardTanhDerivative(z));
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(expOut[i], zPrime.getDouble(i), 0.1);
        }
    }

    @Test
    public void testRectifiedLinearDerivative() {
        DataTypeUtil.setDTypeForContext(DOUBLE);
        // ReLU:
        // f(x) = max(0,x)
        // Piecewise differentiable; choose f'(0) = 0
        // f'(x) = 1 if x > 0
        // f'(x) = 0 if x <= 0
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            expOut[i] = (x > 0) ? 1 : 0;
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new Step(z));
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(((expOut[i]) == (zPrime.getDouble(i))));
        }
    }

    @Test
    public void testSigmoidDerivative() {
        // Derivative of sigmoid: ds(x)/dx = s(x)*(1-s(x))
        // s(x) = 1 / (exp(-x) + 1)
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            double sigmoid = 1.0 / ((FastMath.exp((-x))) + 1);
            expOut[i] = sigmoid * (1 - sigmoid);
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.SigmoidDerivative(z));
        for (int i = 0; i < 100; i++) {
            double relError = (Math.abs(((expOut[i]) - (zPrime.getDouble(i))))) / ((Math.abs(expOut[i])) + (Math.abs(zPrime.getDouble(i))));
            Assert.assertTrue((relError < (DerivativeTests.REL_ERROR_TOLERANCE)));
        }
    }

    @Test
    public void testHardSigmoidDerivative() {
        /* f(x) = min(1, max(0, 0.2*x + 0.5))
        or equivalently: clip 0.2*x+0.5 to range 0 to 1
        where clipping bounds are -2.5 and 2.5

        Hard sigmoid derivative:
        f'(x) =
        0 if x < -2.5 or x > 2.5
        0.2 otherwise
         */
        double[] expHSOut = new double[300];
        double[] expDerivOut = new double[300];
        INDArray xArr = Nd4j.linspace((-3), 3, 300);
        for (int i = 0; i < (xArr.length()); i++) {
            double x = xArr.getDouble(i);
            double hs = (0.2 * x) + 0.5;
            if (hs < 0)
                hs = 0;

            if (hs > 1)
                hs = 1;

            expHSOut[i] = hs;
            double hsDeriv;
            if ((x < (-2.5)) || (x > 2.5))
                hsDeriv = 0;
            else
                hsDeriv = 0.2;

            expDerivOut[i] = hsDeriv;
        }
        INDArray z = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("hardsigmoid", xArr.dup()));
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new HardSigmoidDerivative(xArr.dup()));
        System.out.println(xArr);
        System.out.println(z);
        System.out.println(zPrime);
        for (int i = 0; i < (expHSOut.length); i++) {
            double relErrorHS = (Math.abs(((expHSOut[i]) - (z.getDouble(i))))) / ((Math.abs(expHSOut[i])) + (Math.abs(z.getDouble(i))));
            if (!(((expHSOut[i]) == 0) && ((z.getDouble(i)) == 0))) {
                Assert.assertTrue((relErrorHS < (DerivativeTests.REL_ERROR_TOLERANCE)));
            }
            double relErrorDeriv = (Math.abs(((expDerivOut[i]) - (zPrime.getDouble(i))))) / ((Math.abs(expDerivOut[i])) + (Math.abs(zPrime.getDouble(i))));
            if (!(((expDerivOut[i]) == 0) && ((zPrime.getDouble(i)) == 0))) {
                Assert.assertTrue((relErrorDeriv < (DerivativeTests.REL_ERROR_TOLERANCE)));
            }
        }
    }

    @Test
    public void testSoftMaxDerivative() {
        Random r = new Random(12345L);
        int[] mb = new int[]{ 10, 2, 1 };
        for (int minibatch : mb) {
            System.out.println(("Minibatch size: " + minibatch));
            INDArray z = Nd4j.zeros(minibatch, 5);
            double[][] in = new double[minibatch][5];
            double[][] softmax = new double[minibatch][5];
            double[][] expOut = new double[minibatch][5];
            for (int i = 0; i < minibatch; i++) {
                double rowSumExp = 0.0;
                for (int j = 0; j < 5; j++) {
                    in[i][j] = 10 * (r.nextDouble());
                    z.putScalar(new int[]{ i, j }, in[i][j]);
                    rowSumExp += FastMath.exp(in[i][j]);
                }
                for (int j = 0; j < 5; j++) {
                    softmax[i][j] = (FastMath.exp(in[i][j])) / rowSumExp;
                    expOut[i][j] = (softmax[i][j]) * (1.0 - (softmax[i][j]));
                }
            }
            INDArray sm = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", z.dup()));
            INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.SoftMaxDerivative(z));
            System.out.println(Arrays.toString(sm.data().asDouble()));
            System.out.println(Arrays.toString(zPrime.data().asDouble()));
            Assert.assertNotEquals(sm, zPrime);
            for (int i = 0; i < minibatch; i++) {
                for (int j = 0; j < 5; j++) {
                    double relError = (Math.abs(((expOut[i][j]) - (zPrime.getDouble(i, j))))) / ((Math.abs(expOut[i][j])) + (Math.abs(zPrime.getDouble(i, j))));
                    // System.out.println("Error: " + relError);
                    Assert.assertTrue((relError < (DerivativeTests.REL_ERROR_TOLERANCE)));
                }
            }
        }
    }

    @Test
    public void testSoftPlusDerivative() {
        // s(x) = 1 / (exp(-x) + 1)
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            expOut[i] = 1.0 / (1.0 + (FastMath.exp((-x))));
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new Sigmoid(z));
        for (int i = 0; i < 100; i++) {
            double relError = (Math.abs(((expOut[i]) - (zPrime.getDouble(i))))) / ((Math.abs(expOut[i])) + (Math.abs(zPrime.getDouble(i))));
            Assert.assertTrue((relError < (DerivativeTests.REL_ERROR_TOLERANCE)));
        }
    }

    @Test
    public void testTanhDerivative() {
        // Derivative of sigmoid: ds(x)/dx = s(x)*(1-s(x))
        // s(x) = 1 / (exp(-x) + 1)
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            double tanh = FastMath.tanh(x);
            expOut[i] = 1.0 - (tanh * tanh);
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.TanhDerivative(z));
        for (int i = 0; i < 100; i++) {
            double relError = (Math.abs(((expOut[i]) - (zPrime.getDouble(i))))) / ((Math.abs(expOut[i])) + (Math.abs(zPrime.getDouble(i))));
            Assert.assertTrue((relError < (DerivativeTests.REL_ERROR_TOLERANCE)));
        }
    }

    @Test
    public void testCubeDerivative() {
        // Derivative of cube: 3*x^2
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            expOut[i] = (3 * x) * x;
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new CubeDerivative(z));
        for (int i = 0; i < 100; i++) {
            double d1 = expOut[i];
            double d2 = zPrime.getDouble(i);
            double relError = (Math.abs((d1 - d1))) / ((Math.abs(d1)) + (Math.abs(d2)));
            if ((d1 == 0.0) && (d2 == 0.0))
                relError = 0.0;

            String str = (((("exp=" + (expOut[i])) + ", act=") + (zPrime.getDouble(i))) + "; relError = ") + relError;
            Assert.assertTrue(str, (relError < (DerivativeTests.REL_ERROR_TOLERANCE)));
        }
    }

    @Test
    public void testLeakyReLUDerivative() {
        // Derivative: 0.01 if x<0, 1 otherwise
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            expOut[i] = (x >= 0) ? 1 : 0.25;
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new LeakyReLUDerivative(z, 0.25));
        for (int i = 0; i < 100; i++) {
            double relError = (Math.abs(((expOut[i]) - (zPrime.getDouble(i))))) / ((Math.abs(expOut[i])) + (Math.abs(zPrime.getDouble(i))));
            Assert.assertTrue((relError < (DerivativeTests.REL_ERROR_TOLERANCE)));
        }
    }

    @Test
    public void testSoftSignDerivative() {
        // Derivative: 1 / (1+abs(x))^2
        INDArray z = Nd4j.zeros(100);
        double[] expOut = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            double temp = 1 + (Math.abs(x));
            expOut[i] = 1.0 / (temp * temp);
        }
        INDArray zPrime = Nd4j.getExecutioner().execAndReturn(new SoftSignDerivative(z));
        for (int i = 0; i < 100; i++) {
            double relError = (Math.abs(((expOut[i]) - (zPrime.getDouble(i))))) / ((Math.abs(expOut[i])) + (Math.abs(zPrime.getDouble(i))));
            Assert.assertTrue((relError < (DerivativeTests.REL_ERROR_TOLERANCE)));
        }
    }

    @Test
    public void testELUDerivative() {
        // f(x) = x if x>=0
        // f(x) = 1.0*(exp(x)-1)
        INDArray z = Nd4j.zeros(100);
        double[] out = new double[100];
        double[] outDeriv = new double[100];
        for (int i = 0; i < 100; i++) {
            double x = 0.1 * (i - 50);
            z.putScalar(i, x);
            if (x >= 0) {
                out[i] = x;
                outDeriv[i] = 1.0;
            } else {
                out[i] = (FastMath.exp(x)) - 1.0;
                outDeriv[i] = FastMath.exp(x);
            }
        }
        INDArray act = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("elu", z.dup()));
        INDArray actDeriv = Nd4j.getExecutioner().execAndReturn(new ELUDerivative(z.dup()));
        System.out.println(act);
        for (int i = 0; i < 100; i++) {
            double relError1 = (Math.abs(((out[i]) - (act.getDouble(i))))) / ((Math.abs(out[i])) + (Math.abs(act.getDouble(i))));
            if (((out[i]) == 0.0) && ((act.getDouble(i)) == 0.0))
                relError1 = 0.0;

            double relError2 = (Math.abs(((outDeriv[i]) - (actDeriv.getDouble(i))))) / ((Math.abs(outDeriv[i])) + (Math.abs(actDeriv.getDouble(i))));
            if (((outDeriv[i]) == 0.0) && ((actDeriv.getDouble(i)) == 0.0))
                relError2 = 0.0;

            Assert.assertTrue((relError1 < (DerivativeTests.REL_ERROR_TOLERANCE)));
            Assert.assertTrue((relError2 < (DerivativeTests.REL_ERROR_TOLERANCE)));
        }
    }

    @Test
    public void softmaxsimpleLossTest() {
        /* Softmax derivative is correct if it is standalone
        But when we are applying it in the chain rule the current derivative function is incomplete.
        For this test, I am assuming that the function off interest is just MSE
        What the fix is:
        We need the derivative of a softmax needs to return a rank 2 matrix.
        Right now we get only the diagonal elements of this matrix
        http://stats.stackexchange.com/questions/79454/softmax-layer-in-a-neural-network
         */
        // random array represeting preout
        INDArray X = Nd4j.rand(1, 2);
        // preout transformed to y_hat with softmax
        INDArray YHat = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", X.dup()));
        // hard coding something to construct a function with, using MSE
        INDArray Y = Nd4j.create(new double[][]{ new double[]{ 0.123, 1 - 0.123 } });
        // This is the MSE now
        double lossHere = Transforms.pow(Y.sub(YHat), 2).sumNumber().doubleValue();
        INDArray softmaxDer = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.SoftMaxDerivative(X.dup()));
        // the way we apply the chain rule now is 2*(y-yhat)*softmaxder
        INDArray dLdY = Y.sub(YHat).mul((-2));
        INDArray currentGradient = dLdY.mul(softmaxDer);
        // what I think we should be doing
        // we have x0, x1 -> y0,y1
        // we need the derivatives of the output of the softmax wrt every input (x0,x1)
        // we only have dy0/dx0 and dy1/dx1
        // we also need dy0/dx1 and dy1/dx0
        // the below is the chain rule in calc applied when L is a function of y0,y1; y0 and y1 are in turn functions of BOTH (x0 and x1)
        // dL/dx0 = (dl/dy0) * (dy0/dx0) + (dL/dy1) * (dy1/dx0)
        // dL/dx1 = (dl/dy0) * (dy0/dx1) + (dL/dy1) * (dy1/dx1)
        // worked it out on paper and googled it (should have googled first, gave formula from link above)
        // dy0/dx0 = y0*(1-y0) = y0*y1
        // dy1/dx0 = -y1*(1-y1) = -y0*y1
        // dy0/dx1 = -y0*(1-y0) = -y0*y1
        // dy1/dx1 = y1*(1-y1) = y0*y1
        // [ dL/dy0 dL/dy1] [[dy0/dx0 dy1/dx0] [dy0/dx1 dy1/dx1]]
        double y0y1 = softmaxDer.getDouble(0, 0);
        // hack but this is what we need to implement, straightforward here but complicated for >2
        // INDArray mysoftmaxDer = Nd4j.create(new double[][] {{y0y1,y0y1*-1},{-1*y0y1,y0y1}});
        INDArray mysoftmaxDer = DerivativeTests.correctSoftmax(X);
        INDArray myGradient = mysoftmaxDer.mulRowVector(dLdY).sum(1);
        double epsilon = 1.0E-4;
        INDArray Xiplus;
        INDArray Ximinus;
        INDArray YHatplus;
        INDArray YHatminus;
        double lossplus;
        double lossminus;
        INDArray numGradient = Nd4j.zeros(1, 2);
        for (int i = 0; i < 2; i++) {
            /* change X one value one at a time */
            // +epsilon
            double x = X.getDouble(0, i);
            Xiplus = X.dup();
            Xiplus.put(0, i, (x + epsilon));
            YHatplus = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", Xiplus.dup()));
            lossplus = Transforms.pow(Y.sub(YHatplus), 2).sumNumber().doubleValue();
            // -epsilon
            Ximinus = X.dup();
            Ximinus.put(0, i, (x - epsilon));
            YHatminus = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", Ximinus.dup()));
            lossminus = Transforms.pow(Y.sub(YHatminus), 2).sumNumber().doubleValue();
            double gradienti = (lossplus - lossminus) / (2 * epsilon);
            numGradient.put(0, i, gradienti);
        }
        System.out.println("=========================");
        System.out.println("NUMERICAL:");
        System.out.println(numGradient);
        System.out.println("\nCURRENTLY:");
        System.out.println(currentGradient);
        System.out.println("\nMY GRADIENT:");
        System.out.println((myGradient + "\n"));
        System.out.println("Because of the nature of the derivative of the softmax for length = 2, our current method will make it off by a factor of 2");
        System.out.println("=========================");
    }

    @Test
    public void softmaxsimplelongerlengthLossTest() {
        /* Read comments in earlier test for length = 2 */
        // random array represeting preout
        int someLength = 7;
        INDArray X = Nd4j.rand(1, someLength);
        // preout transformed to y_hat with softmax
        INDArray YHat = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", X.dup()));
        // hard coding something to construct a function with, using MSE
        INDArray temp = Nd4j.rand(1, someLength);
        INDArray Y = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", temp));
        // This is the MSE now
        double lossHere = Transforms.pow(Y.sub(YHat), 2).sumNumber().doubleValue();
        INDArray softmaxDer = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.transforms.SoftMaxDerivative(X.dup()));
        // the way we apply the chain rule now is 2*(y-yhat)*softmaxder
        INDArray dLdY = Y.sub(YHat).mul((-2));
        INDArray currentGradient = dLdY.mul(softmaxDer);
        INDArray mysoftmaxDer = DerivativeTests.correctSoftmax(X);
        INDArray myGradient = mysoftmaxDer.mulRowVector(dLdY).sum(1);
        double epsilon = 1.0E-4;
        INDArray Xiplus;
        INDArray Ximinus;
        INDArray YHatplus;
        INDArray YHatminus;
        double lossplus;
        double lossminus;
        INDArray numGradient = Nd4j.zeros(1, someLength);
        for (int i = 0; i < someLength; i++) {
            /* change X one value one at a time */
            // +epsilon
            double x = X.getDouble(0, i);
            Xiplus = X.dup();
            Xiplus.put(0, i, (x + epsilon));
            YHatplus = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", Xiplus.dup()));
            lossplus = Transforms.pow(Y.sub(YHatplus), 2).sumNumber().doubleValue();
            // -epsilon
            Ximinus = X.dup();
            Ximinus.put(0, i, (x - epsilon));
            YHatminus = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", Ximinus.dup()));
            lossminus = Transforms.pow(Y.sub(YHatminus), 2).sumNumber().doubleValue();
            double gradienti = (lossplus - lossminus) / (2 * epsilon);
            numGradient.put(0, i, gradienti);
        }
        System.out.println("=========================");
        System.out.println("NUMERICAL GRADIENT:");
        System.out.println(new NDArrayStrings(6).format(numGradient).toString());
        System.out.println("\nANALYTIC USING EXISTING SOFTMAX DER:");
        System.out.println(new NDArrayStrings(6).format(currentGradient).toString());
        System.out.println("\nGRADIENT USING MY VERSION OF SOFTMAX DER:");
        System.out.println(new NDArrayStrings(6).format(myGradient).toString());
        System.out.println("=========================");
    }
}

