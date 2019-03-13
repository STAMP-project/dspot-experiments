package org.nd4j.linalg.lossfunctions;


import DataBuffer.Type;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.iter.NdIndexIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Created by Alex on 08/08/2016.
 */
@Slf4j
public class LossFunctionGradientChecks extends BaseNd4jTest {
    public static final double epsilon = 1.0E-6;

    private static final double maxRelError = 5.0;// 5% relative error


    Type initialType;

    public LossFunctionGradientChecks(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testLossFunctionGradients() {
        INDArray[] labels = // Nd4j.create(new double[][] {{10,1,3},{1,10,1},{1,2,5}}),
        // Nd4j.create(new double[][] {{10,-1,3},{1,10,1},{1,2,-5}}),
        new INDArray[]{ Nd4j.create(new double[]{ 0, 1, 0 }), Nd4j.create(new double[]{ 0, 1, 1 }), /* Nd4j.create(new double[][]{{1,0,0},{0,1,0},{0,0,1}}),
        Nd4j.create(new double[]{1,2,1}),
        Nd4j.create(new double[][]{{1,2,1},{0.1,1,0.5},{20,3,1}}),
        Nd4j.create(new double[]{1,0,0}),
        Nd4j.create(new double[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}}),
        Nd4j.create(new double[]{1,2,1}),
        Nd4j.create(new double[][]{{101,21,110},{10.1,1,0.5},{200,30,0.001}}),
         */
        Nd4j.create(new double[]{ 1, 2, 1 }), Nd4j.create(new double[][]{ new double[]{ 101, 21, 110 }, new double[]{ 10.1, 1, 0.5 }, new double[]{ 200, 30, 0.001 } }), Nd4j.create(new double[]{ 1, 2, 1 }), Nd4j.create(new double[][]{ new double[]{ 101, 21, 110 }, new double[]{ 10.1, 1, 0.5 }, new double[]{ 200, 30, 0.001 } }), Nd4j.create(new double[]{ 1, 2, 1 }), Nd4j.create(new double[][]{ new double[]{ 101, 21, 110 }, new double[]{ 10.1, 1, 0.5 }, new double[]{ 200, 30, 0.001 } }), Nd4j.create(new double[]{ 1, 2, 1 }), Nd4j.create(new double[][]{ new double[]{ 101, 21, 110 }, new double[]{ 10.1, 1, 0.5 }, new double[]{ 200, 30, 0.001 } }), // Nd4j.create(new double[][] {{-1,-1,1},{-1,1,1},{-1,1,1}}),
        Nd4j.create(new double[][]{ new double[]{ -1, 1, -1 }, new double[]{ 1, 1, -1 }, new double[]{ -1, 1, 1 } }), Nd4j.create(new double[][]{ new double[]{ -1, 1, -1 }, new double[]{ 1, 1, -1 }, new double[]{ -1, 1, 1 } }), Nd4j.create(new double[][]{ new double[]{ 1, 0, 0 }, new double[]{ 0, 1, 0 }, new double[]{ 1, 0, 1 } }) }// Nd4j.create(new double[][] {{10,1,3},{1,10,1},{1,2,5}}),
        // Nd4j.create(new double[][] {{10,-1,3},{1,10,1},{1,2,-5}}),
        ;
        INDArray[] preOut = // Nd4j.rand(3,3),
        // Nd4j.randn(3,3)
        new INDArray[]{ Nd4j.rand(1, 3), Nd4j.rand(1, 3), /* Nd4j.rand(3,3),
        Nd4j.rand(1,3).add(5),
        Nd4j.rand(3,3),
        Nd4j.rand(1,3).add(5),
        Nd4j.rand(4,4),
         */
        Nd4j.randn(1, 3), Nd4j.randn(3, 3).add(10), Nd4j.rand(1, 3), Nd4j.randn(3, 3).add(10), Nd4j.randn(1, 3), Nd4j.randn(3, 3).add(10), Nd4j.rand(1, 3), Nd4j.randn(3, 3).add(10), /* Nd4j.rand(1,3),
        Nd4j.randn(3,3).add(10),
         */
        Nd4j.rand(3, 3).addi((-0.5))// adding a neg num makes some +ve/ some -ve
        , Nd4j.rand(3, 3).addi((-0.5))// adding a neg num makes some +ve/ some -ve
        , Nd4j.rand(3, 3).addi((-0.5)) }// Nd4j.rand(3,3),
        // Nd4j.randn(3,3)
        ;
        ILossFunction[] lossFn = // new LossPoisson(),
        // new LossCosineProximity()
        new ILossFunction[]{ new LossBinaryXENT(), new LossBinaryXENT(), /* new LossMCXENT(), new LossMCXENT(),
        new LossMCXENT(),new LossMSE(), new LossMSE(), new LossKLD(), new LossKLD(), new LossMAE(), new LossMAE(),
         */
        new LossMAE(), new LossMAE(), new LossMSE(), new LossMSE(), new LossL1(), new LossL1(), new LossL2(), new LossL2(), new LossSquaredHinge(), new LossHinge(), new LossMultiLabel() }// new LossPoisson(),
        // new LossCosineProximity()
        ;
        String[] activationFns = // "relu",
        // "identity"
        new String[]{ "identity", "tanh", /* "softmax","tanh","identity","tanh",
        "tanh","identity","identity","identity","identity",
         */
        "identity", "identity", "identity", "identity", "sigmoid", "relu", "sigmoid", "relu", "identity", "identity", "identity" }// "relu",
        // "identity"
        ;
        for (int i = 0; i < (labels.length); i++) {
            // if (i != labels.length-2)  continue;
            int totalNFailures = 0;
            ILossFunction lf = lossFn[i];
            INDArray l = labels[i];
            INDArray p = preOut[i];
            String afn = activationFns[i];
            System.out.printf("Starting test: %s, %s, input shape = %s\n", lf, afn, Arrays.toString(p.shape()));
            INDArray grad = lf.computeGradient(l, p, LossFunctionGradientChecks.activationInstance(afn), null);
            NdIndexIterator iter = new NdIndexIterator(l.shape());
            while (iter.hasNext()) {
                val next = iter.next();
                double before = p.getDouble(next);
                p.putScalar(next, (before + (LossFunctionGradientChecks.epsilon)));
                double scorePlus = lf.computeScore(l, p, LossFunctionGradientChecks.activationInstance(afn), null, true);
                p.putScalar(next, (before - (LossFunctionGradientChecks.epsilon)));
                double scoreMinus = lf.computeScore(l, p, LossFunctionGradientChecks.activationInstance(afn), null, true);
                p.putScalar(next, before);
                double scoreDelta = scorePlus - scoreMinus;
                double numericalGradient = scoreDelta / (2 * (LossFunctionGradientChecks.epsilon));
                double analyticGradient = (grad.getDouble(next)) / (l.size(0));// Analytic gradient method is before dividing by minibatch

                double relError = ((Math.abs((analyticGradient - numericalGradient))) * 100) / (Math.abs(numericalGradient));
                if ((analyticGradient == 0.0) && (numericalGradient == 0.0))
                    relError = 0.0;
                // Edge case: i.e., RNNs with time series length of 1.0

                if ((relError > (LossFunctionGradientChecks.maxRelError)) || (Double.isNaN(relError))) {
                    System.out.println(((((((((((("Param " + i) + " FAILED: grad= ") + analyticGradient) + ", numericalGrad= ") + numericalGradient) + ", relErrorPerc= ") + relError) + ", scorePlus=") + scorePlus) + ", scoreMinus= ") + scoreMinus));
                    totalNFailures++;
                } else {
                    System.out.println(((((((((((("Param " + i) + " passed: grad= ") + analyticGradient) + ", numericalGrad= ") + numericalGradient) + ", relError= ") + relError) + ", scorePlus=") + scorePlus) + ", scoreMinus= ") + scoreMinus));
                }
                // System.out.println("Param " + i + " passed: grad= " + analyticGradient + ", numericalGrad= " + numericalGradient
                // + ", relError= " + relError + ", scorePlus="+scorePlus+", scoreMinus= " + scoreMinus );
            } 
            if (totalNFailures > 0)
                System.out.println(((("Gradient check failed for loss function " + lf) + "; total num failures = ") + totalNFailures));

            System.out.println("DONE");
        }
    }
}

