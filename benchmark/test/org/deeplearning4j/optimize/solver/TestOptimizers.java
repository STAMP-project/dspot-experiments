/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.optimize.solver;


import DataType.DOUBLE;
import OptimizationAlgorithm.CONJUGATE_GRADIENT;
import OptimizationAlgorithm.LBFGS;
import OptimizationAlgorithm.LINE_GRADIENT_DESCENT;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.val;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.layers.LayerHelper;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.optimize.api.ConvexOptimizer;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.DefaultRandom;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.api.rng.distribution.Distribution;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.conditions.Condition;
import org.nd4j.linalg.primitives.Pair;

import static OptimizationAlgorithm.CONJUGATE_GRADIENT;
import static OptimizationAlgorithm.LBFGS;
import static OptimizationAlgorithm.LINE_GRADIENT_DESCENT;
import static OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;


public class TestOptimizers extends BaseDL4JTest {
    // For debugging.
    private static final boolean PRINT_OPT_RESULTS = true;

    @Test
    public void testOptimizersBasicMLPBackprop() {
        // Basic tests of the 'does it throw an exception' variety.
        DataSetIterator iter = new IrisDataSetIterator(5, 50);
        OptimizationAlgorithm[] toTest = new OptimizationAlgorithm[]{ STOCHASTIC_GRADIENT_DESCENT, LINE_GRADIENT_DESCENT, CONJUGATE_GRADIENT, LBFGS };
        for (OptimizationAlgorithm oa : toTest) {
            MultiLayerNetwork network = new MultiLayerNetwork(TestOptimizers.getMLPConfigIris(oa));
            network.init();
            iter.reset();
            network.fit(iter);
        }
    }

    @Test
    public void testOptimizersMLP() {
        // Check that the score actually decreases over time
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        OptimizationAlgorithm[] toTest = new OptimizationAlgorithm[]{ STOCHASTIC_GRADIENT_DESCENT, LINE_GRADIENT_DESCENT, CONJUGATE_GRADIENT, LBFGS };
        DataSet ds = iter.next();
        ds.normalizeZeroMeanZeroUnitVariance();
        for (OptimizationAlgorithm oa : toTest) {
            int nIter = 10;
            MultiLayerNetwork network = new MultiLayerNetwork(TestOptimizers.getMLPConfigIris(oa));
            network.init();
            double score = network.score(ds);
            Assert.assertTrue(((score != 0.0) && (!(Double.isNaN(score)))));
            if (TestOptimizers.PRINT_OPT_RESULTS)
                System.out.println(("testOptimizersMLP() - " + oa));

            int nCallsToOptimizer = 30;
            double[] scores = new double[nCallsToOptimizer + 1];
            scores[0] = score;
            for (int i = 0; i < nCallsToOptimizer; i++) {
                for (int j = 0; j < nIter; j++) {
                    network.fit(ds);
                }
                double scoreAfter = network.score(ds);
                scores[(i + 1)] = scoreAfter;
                Assert.assertTrue("Score is NaN after optimization", (!(Double.isNaN(scoreAfter))));
                Assert.assertTrue(((((("OA= " + oa) + ", before= ") + score) + ", after= ") + scoreAfter), (scoreAfter <= score));
                score = scoreAfter;
            }
            if (TestOptimizers.PRINT_OPT_RESULTS)
                System.out.println(((oa + " - ") + (Arrays.toString(scores))));

        }
    }

    // ==================================================
    // Sphere Function Optimizer Tests
    @Test
    public void testSphereFnOptStochGradDescent() {
        testSphereFnOptHelper(STOCHASTIC_GRADIENT_DESCENT, 5, 2);
        testSphereFnOptHelper(STOCHASTIC_GRADIENT_DESCENT, 5, 10);
        testSphereFnOptHelper(STOCHASTIC_GRADIENT_DESCENT, 5, 100);
    }

    @Test
    public void testSphereFnOptLineGradDescent() {
        // Test a single line search with calculated search direction (with multiple line search iterations)
        int[] numLineSearchIter = new int[]{ 5, 10 };
        for (int n : numLineSearchIter)
            testSphereFnOptHelper(LINE_GRADIENT_DESCENT, n, 2);

        for (int n : numLineSearchIter)
            testSphereFnOptHelper(LINE_GRADIENT_DESCENT, n, 10);

        for (int n : numLineSearchIter)
            testSphereFnOptHelper(LINE_GRADIENT_DESCENT, n, 100);

    }

    @Test
    public void testSphereFnOptCG() {
        // Test a single line search with calculated search direction (with multiple line search iterations)
        int[] numLineSearchIter = new int[]{ 5, 10 };
        for (int n : numLineSearchIter)
            testSphereFnOptHelper(CONJUGATE_GRADIENT, n, 2);

        for (int n : numLineSearchIter)
            testSphereFnOptHelper(CONJUGATE_GRADIENT, n, 10);

        for (int n : numLineSearchIter)
            testSphereFnOptHelper(CONJUGATE_GRADIENT, n, 100);

    }

    @Test
    public void testSphereFnOptLBFGS() {
        // Test a single line search with calculated search direction (with multiple line search iterations)
        int[] numLineSearchIter = new int[]{ 5, 10 };
        for (int n : numLineSearchIter)
            testSphereFnOptHelper(LBFGS, n, 2);

        for (int n : numLineSearchIter)
            testSphereFnOptHelper(LBFGS, n, 10);

        for (int n : numLineSearchIter)
            testSphereFnOptHelper(LBFGS, n, 100);

    }

    @Test
    public void testSphereFnOptStochGradDescentMultipleSteps() {
        // Earlier tests: only do a single line search, though each line search will do multiple iterations
        // of line search algorithm.
        // Here, do multiple optimization runs + multiple line search iterations within each run
        // i.e., gradient is re-calculated at each step/run
        // Single step tests earlier won't test storing of state between iterations
        TestOptimizers.testSphereFnMultipleStepsHelper(STOCHASTIC_GRADIENT_DESCENT, 100, 5);
    }

    @Test
    public void testSphereFnOptLineGradDescentMultipleSteps() {
        TestOptimizers.testSphereFnMultipleStepsHelper(LINE_GRADIENT_DESCENT, 100, 5);
    }

    @Test
    public void testSphereFnOptCGMultipleSteps() {
        TestOptimizers.testSphereFnMultipleStepsHelper(CONJUGATE_GRADIENT, 100, 5);
    }

    @Test
    public void testSphereFnOptLBFGSMultipleSteps() {
        TestOptimizers.testSphereFnMultipleStepsHelper(LBFGS, 100, 5);
    }

    /**
     * A non-NN optimization problem. Optimization function (cost function) is
     * \sum_i x_i^2. Has minimum of 0.0 at x_i=0 for all x_i
     * See: https://en.wikipedia.org/wiki/Test_functions_for_optimization
     */
    private static class SphereFunctionModel extends TestOptimizers.SimpleOptimizableModel {
        private static final long serialVersionUID = -6963606137417355405L;

        private SphereFunctionModel(int nParams, Distribution distribution, NeuralNetConfiguration conf) {
            super(distribution.sample(new int[]{ 1, nParams }), conf);
        }

        @Override
        public void computeGradientAndScore(LayerWorkspaceMgr workspaceMgr) {
            // Gradients: d(x^2)/dx = 2x
            INDArray gradient = parameters.mul(2);
            Gradient g = new DefaultGradient();
            g.gradientForVariable().put("W", this.gradientView);
            this.gradient = g;
            this.score = Nd4j.getBlasWrapper().dot(parameters, parameters);// sum_i x_i^2

            this.gradientView.assign(gradient);
        }

        @Override
        public long numParams(boolean backwards) {
            return 0;
        }

        @Override
        public void setParamsViewArray(INDArray params) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public void setBackpropGradientsViewArray(INDArray gradients) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCacheMode(CacheMode mode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setListeners(TrainingListener... listeners) {
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public void setInput(INDArray input, LayerWorkspaceMgr workspaceMgr) {
        }

        @Override
        public boolean isPretrainLayer() {
            return false;
        }

        @Override
        public void clearNoiseWeightParams() {
        }
    }

    // ==================================================
    // Rastrigin Function Optimizer Tests
    @Test
    public void testRastriginFnOptStochGradDescentMultipleSteps() {
        TestOptimizers.testRastriginFnMultipleStepsHelper(STOCHASTIC_GRADIENT_DESCENT, 5, 20);
    }

    @Test
    public void testRastriginFnOptLineGradDescentMultipleSteps() {
        TestOptimizers.testRastriginFnMultipleStepsHelper(LINE_GRADIENT_DESCENT, 10, 20);
    }

    @Test
    public void testRastriginFnOptCGMultipleSteps() {
        TestOptimizers.testRastriginFnMultipleStepsHelper(CONJUGATE_GRADIENT, 10, 20);
    }

    @Test
    public void testRastriginFnOptLBFGSMultipleSteps() {
        TestOptimizers.testRastriginFnMultipleStepsHelper(LBFGS, 10, 20);
    }

    /**
     * Rastrigin function: A much more complex non-NN multi-dimensional optimization problem.
     * Global minimum of 0 at x_i = 0 for all x_i.
     * Very large number of local minima. Can't expect to achieve global minimum with gradient-based (line search)
     * optimizers, but can expect significant improvement in score/cost relative to initial parameters.
     * This implementation has cost function = infinity if any parameters x_i are
     * outside of range [-5.12,5.12]
     * https://en.wikipedia.org/wiki/Rastrigin_function
     */
    private static class RastriginFunctionModel extends TestOptimizers.SimpleOptimizableModel {
        private static final long serialVersionUID = -1772954508787487941L;

        private RastriginFunctionModel(int nDimensions, NeuralNetConfiguration conf) {
            super(TestOptimizers.RastriginFunctionModel.initParams(nDimensions), conf);
        }

        private static INDArray initParams(int nDimensions) {
            Random rng = new DefaultRandom(12345L);
            Distribution dist = new org.nd4j.linalg.api.rng.distribution.impl.UniformDistribution(rng, (-5.12), 5.12);
            return dist.sample(new int[]{ 1, nDimensions });
        }

        @Override
        public void computeGradientAndScore(LayerWorkspaceMgr workspaceMgr) {
            // Gradient decomposes due to sum, so:
            // d(x^2 - 10*cos(2*Pi*x))/dx
            // = 2x + 20*pi*sin(2*Pi*x)
            INDArray gradient = parameters.mul((2 * (Math.PI)));
            Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.strict.Sin(gradient));
            gradient.muli((20 * (Math.PI)));
            gradient.addi(parameters.mul(2));
            Gradient g = new DefaultGradient(this.gradientView);
            g.gradientForVariable().put("W", this.gradientView);
            this.gradient = g;
            // If any parameters are outside range [-5.12,5.12]: score = infinity
            INDArray paramExceeds512 = parameters.cond(new Condition() {
                @Override
                public int condtionNum() {
                    return 0;
                }

                @Override
                public double getValue() {
                    return 0;
                }

                @Override
                public double epsThreshold() {
                    return 0;
                }

                @Override
                public Boolean apply(Number input) {
                    return (Math.abs(input.doubleValue())) > 5.12;
                }
            });
            int nExceeds512 = paramExceeds512.castTo(DOUBLE).sum(Integer.MAX_VALUE).getInt(0);
            if (nExceeds512 > 0)
                this.score = Double.POSITIVE_INFINITY;

            // Otherwise:
            double costFn = 10 * (parameters.length());
            costFn += Nd4j.getBlasWrapper().dot(parameters, parameters);// xi*xi

            INDArray temp = parameters.mul((2.0 * (Math.PI)));
            Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.strict.Cos(temp));
            temp.muli((-10.0));// After this: each element is -10*cos(2*Pi*xi)

            costFn += temp.sum(Integer.MAX_VALUE).getDouble(0);
            this.score = costFn;
            this.gradientView.assign(gradient);
        }

        @Override
        public long numParams(boolean backwards) {
            return 0;
        }

        @Override
        public void setParamsViewArray(INDArray params) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public void setBackpropGradientsViewArray(INDArray gradients) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCacheMode(CacheMode mode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setListeners(TrainingListener... listeners) {
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public void setInput(INDArray input, LayerWorkspaceMgr workspaceMgr) {
        }

        @Override
        public boolean isPretrainLayer() {
            return false;
        }

        @Override
        public void clearNoiseWeightParams() {
        }
    }

    // ==================================================
    // Rosenbrock Function Optimizer Tests
    @Test
    public void testRosenbrockFnOptLineGradDescentMultipleSteps() {
        TestOptimizers.testRosenbrockFnMultipleStepsHelper(LINE_GRADIENT_DESCENT, 20, 20);
    }

    @Test
    public void testRosenbrockFnOptCGMultipleSteps() {
        TestOptimizers.testRosenbrockFnMultipleStepsHelper(CONJUGATE_GRADIENT, 20, 20);
    }

    @Test
    public void testRosenbrockFnOptLBFGSMultipleSteps() {
        TestOptimizers.testRosenbrockFnMultipleStepsHelper(LBFGS, 20, 20);
    }

    /**
     * Rosenbrock function: a multi-dimensional 'valley' type function.
     * Has a single local/global minimum of f(x)=0 at x_i=1 for all x_i.
     * Expect gradient-based optimization functions to find global minimum eventually,
     * but optimization may be slow due to nearly flat gradient along valley.
     * Restricted here to the range [-5,5]. This implementation gives infinite cost/score
     * if any parameter is outside of this range.
     * Parameters initialized in range [-4,4]
     * See: http://www.sfu.ca/~ssurjano/rosen.html
     */
    private static class RosenbrockFunctionModel extends TestOptimizers.SimpleOptimizableModel {
        private static final long serialVersionUID = -5129494342531033706L;

        private RosenbrockFunctionModel(int nDimensions, NeuralNetConfiguration conf) {
            super(TestOptimizers.RosenbrockFunctionModel.initParams(nDimensions), conf);
        }

        private static INDArray initParams(int nDimensions) {
            Random rng = new DefaultRandom(12345L);
            Distribution dist = new org.nd4j.linalg.api.rng.distribution.impl.UniformDistribution(rng, (-4.0), 4.0);
            return dist.sample(new int[]{ 1, nDimensions });
        }

        @Override
        public void computeGradientAndScore(LayerWorkspaceMgr workspaceMgr) {
            val nDims = parameters.length();
            INDArray gradient = Nd4j.zeros(nDims);
            double x0 = parameters.getDouble(0);
            double x1 = parameters.getDouble(1);
            double g0 = (((-400) * x0) * (x1 - (x0 * x0))) + (2 * (x0 - 1));
            gradient.put(0, 0, g0);
            for (int i = 1; i < (nDims - 1); i++) {
                double xim1 = parameters.getDouble((i - 1));
                double xi = parameters.getDouble(i);
                double xip1 = parameters.getDouble((i + 1));
                double g = ((200 * (xi - (xim1 * xim1))) - ((400 * xi) * (xip1 - (xi * xi)))) + (2 * (xi - 1));
                gradient.put(0, i, g);
            }
            double xl = parameters.getDouble((nDims - 1));
            double xlm1 = parameters.getDouble((nDims - 2));
            double gl = 200 * (xl - (xlm1 * xlm1));
            // FIXME: int cast
            gradient.put(0, (((int) (nDims)) - 1), gl);
            Gradient g = new DefaultGradient();
            g.gradientForVariable().put("W", gradient);
            this.gradient = g;
            INDArray paramExceeds5 = parameters.cond(new Condition() {
                @Override
                public int condtionNum() {
                    return 0;
                }

                @Override
                public double getValue() {
                    return 0;
                }

                @Override
                public double epsThreshold() {
                    return 0;
                }

                @Override
                public Boolean apply(Number input) {
                    return (Math.abs(input.doubleValue())) > 5.0;
                }
            });
            int nExceeds5 = paramExceeds5.castTo(DOUBLE).sum(Integer.MAX_VALUE).getInt(0);
            if (nExceeds5 > 0)
                this.score = Double.POSITIVE_INFINITY;
            else {
                double score = 0.0;
                for (int i = 0; i < (nDims - 1); i++) {
                    double xi = parameters.getDouble(i);
                    double xi1 = parameters.getDouble((i + 1));
                    score += (100.0 * (Math.pow((xi1 - (xi * xi)), 2.0))) + ((xi - 1) * (xi - 1));
                }
                this.score = score;
            }
        }

        @Override
        public long numParams(boolean backwards) {
            return 0;
        }

        @Override
        public void setParamsViewArray(INDArray params) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public void setBackpropGradientsViewArray(INDArray gradients) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCacheMode(CacheMode mode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setListeners(TrainingListener... listeners) {
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public void setInput(INDArray input, LayerWorkspaceMgr workspaceMgr) {
        }

        @Override
        public boolean isPretrainLayer() {
            return false;
        }

        @Override
        public void clearNoiseWeightParams() {
        }
    }

    /**
     * Simple abstract class to deal with the fact that we don't care about the majority of the Model/Layer
     * methods here. Classes extending this model for optimizer tests need only implement the score() and
     * gradient() methods.
     */
    private abstract static class SimpleOptimizableModel implements Layer , Model {
        private static final long serialVersionUID = 4409380971404019303L;

        protected INDArray parameters;

        protected INDArray gradientView;

        protected final NeuralNetConfiguration conf;

        protected Gradient gradient;

        protected double score;

        /**
         *
         *
         * @param parameterInit
         * 		Initial parameters. Also determines dimensionality of problem. Should be row vector.
         */
        private SimpleOptimizableModel(INDArray parameterInit, NeuralNetConfiguration conf) {
            this.parameters = parameterInit.dup();
            this.gradientView = Nd4j.create(parameterInit.shape());
            this.conf = conf;
        }

        @Override
        public void addListeners(TrainingListener... listener) {
            // no-op
        }

        @Override
        public TrainingConfig getConfig() {
            return conf.getLayer();
        }

        /**
         * Init the model
         */
        @Override
        public void init() {
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public void setInput(INDArray input, LayerWorkspaceMgr workspaceMgr) {
        }

        @Override
        public void fit() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void update(INDArray gradient, String paramType) {
            if (!("W".equals(paramType)))
                throw new UnsupportedOperationException();

            parameters.subi(gradient);
        }

        @Override
        public void setListeners(TrainingListener... listeners) {
        }

        @Override
        public void update(Gradient gradient) {
        }

        @Override
        public INDArray activate(boolean training, LayerWorkspaceMgr workspaceMgr) {
            return null;
        }

        @Override
        public INDArray activate(INDArray input, boolean training, LayerWorkspaceMgr workspaceMgr) {
            return null;
        }

        @Override
        public double score() {
            return score;
        }

        @Override
        public Gradient gradient() {
            return gradient;
        }

        @Override
        public double calcRegularizationScore(boolean backpropParamsOnly) {
            return 0;
        }

        @Override
        public void computeGradientAndScore(LayerWorkspaceMgr workspaceMgr) {
            throw new UnsupportedOperationException("Ensure you implement this function.");
        }

        @Override
        public INDArray params() {
            return parameters;
        }

        @Override
        public long numParams() {
            // FIXME: int cast
            return ((int) (parameters.length()));
        }

        @Override
        public void setParams(INDArray params) {
            this.parameters = params;
        }

        @Override
        public void fit(INDArray data, LayerWorkspaceMgr workspaceMgr) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pair<Gradient, Double> gradientAndScore() {
            computeGradientAndScore(LayerWorkspaceMgr.noWorkspaces());
            return new Pair(gradient(), score());
        }

        @Override
        public int batchSize() {
            return 1;
        }

        @Override
        public NeuralNetConfiguration conf() {
            return conf;
        }

        @Override
        public void setConf(NeuralNetConfiguration conf) {
            throw new UnsupportedOperationException();
        }

        @Override
        public INDArray input() {
            // Work-around for BaseUpdater.postApply(): Uses Layer.input().size(0)
            // in order to get mini-batch size. i.e., divide by 1 here.
            return Nd4j.zeros(1);
        }

        @Override
        public ConvexOptimizer getOptimizer() {
            throw new UnsupportedOperationException();
        }

        @Override
        public INDArray getParam(String param) {
            return parameters;
        }

        @Override
        public Map<String, INDArray> paramTable() {
            return Collections.singletonMap("W", getParam("W"));
        }

        @Override
        public Map<String, INDArray> paramTable(boolean backpropParamsOnly) {
            return paramTable();
        }

        @Override
        public void setParamTable(Map<String, INDArray> paramTable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setParam(String key, INDArray val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Type type() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pair<Gradient, INDArray> backpropGradient(INDArray epsilon, LayerWorkspaceMgr mgr) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<TrainingListener> getListeners() {
            return null;
        }

        @Override
        public void setListeners(Collection<TrainingListener> listeners) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setIndex(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setInputMiniBatchSize(int size) {
        }

        @Override
        public int getInputMiniBatchSize() {
            return 1;
        }

        @Override
        public void setMaskArray(INDArray maskArray) {
        }

        @Override
        public INDArray getMaskArray() {
            return null;
        }

        @Override
        public Pair<INDArray, MaskState> feedForwardMaskArray(INDArray maskArray, MaskState currentMaskState, int minibatchSize) {
            throw new UnsupportedOperationException();
        }

        @Override
        public INDArray getGradientsViewArray() {
            return gradientView;
        }

        @Override
        public void applyConstraints(int iteration, int epoch) {
        }

        @Override
        public int getIterationCount() {
            return 0;
        }

        @Override
        public int getEpochCount() {
            return 0;
        }

        @Override
        public void setIterationCount(int iterationCount) {
        }

        @Override
        public void setEpochCount(int epochCount) {
        }

        @Override
        public void allowInputModification(boolean allow) {
        }

        @Override
        public LayerHelper getHelper() {
            return null;
        }

        @Override
        public boolean updaterDivideByMinibatch(String paramName) {
            return true;
        }
    }
}

