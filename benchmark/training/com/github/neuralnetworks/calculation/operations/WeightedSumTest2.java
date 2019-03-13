package com.github.neuralnetworks.calculation.operations;


import CalculationProvider.OPENCL;
import EXECUTION_MODE.GPU;
import EXECUTION_MODE.SEQ;
import com.github.neuralnetworks.architecture.Connections;
import com.github.neuralnetworks.architecture.FullyConnected;
import com.github.neuralnetworks.calculation.ConnectionCalculator;
import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.tensor.Tensor.TensorIterator;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.tensor.ValuesProvider;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test class for Conv2DFF operations
 */
@RunWith(Parameterized.class)
public class WeightedSumTest2 extends AbstractTest {
    // ////////////////////////////
    // Configuration starts here //
    // ////////////////////////////
    /**
     * set to > 0 to use as constant seed
     */
    private static long seed = -1;

    /**
     * size of the minibatch. Values [1, 256]
     */
    private int minibatchSize = 128;

    private WeightedSumTest2.KernelConfiguration kernelConfiguration;

    // //////////////////////////
    // Configuration ends here //
    // //////////////////////////
    private FullyConnected connection;// this is set automatically


    public WeightedSumTest2(WeightedSumTest2.KernelConfiguration conf) {
        this.connection = conf.connection;
        this.kernelConfiguration = conf;
    }

    @Test
    public void test() {
        // initialize connection weights and input
        Random r = new Random();
        if ((WeightedSumTest2.seed) > 0) {
            r.setSeed(WeightedSumTest2.seed);
        }
        new RandomInitializerImpl(r, (-1.0F), 1.0F).initialize(connection.getWeights());
        ValuesProvider vp = TensorFactory.tensorProvider(connection, minibatchSize, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
        Tensor input = vp.get(connection.getInputLayer());
        input.forEach(( i) -> input.getElements()[i] = r.nextFloat());
        // setup
        List<Connections> connections = new ArrayList<>();
        connections.add(connection);
        // OpenCL
        Tensor oclOutput = null;
        if (kernelConfiguration.testOpenCL) {
            RuntimeConfiguration oclConf = new RuntimeConfiguration();
            oclConf.setCalculationProvider(OPENCL);
            oclConf.setUseDataSharedMemory(false);
            oclConf.setUseWeightsSharedMemory(false);
            oclConf.getOpenCLConfiguration().setAggregateOperations(false);
            oclConf.getOpenCLConfiguration().setSynchronizeAfterOpertation(true);
            oclConf.getAparapiConfiguration().setExecutionMode(SEQ);
            Environment.getInstance().setRuntimeConfiguration(oclConf);
            ConnectionCalculator oclWeightedSum = OperationsFactory.weightedSum();
            oclWeightedSum.calculate(connections, vp, connection.getOutputLayer());
            oclOutput = TensorFactory.tensor(vp.get(connection.getOutputLayer()).getDimensions());
            TensorFactory.copy(vp.get(connection.getOutputLayer()), oclOutput);
            oclConf.getOpenCLConfiguration().setSynchronizeAfterOpertation(false);
            // perform "cycles" with the opencl calculator
            for (int i = 0; i < (kernelConfiguration.kernelRuns); i++) {
                oclWeightedSum.calculate(connections, vp, connection.getOutputLayer());
            }
        }
        // CPU
        Tensor cpuOutput = null;
        if (kernelConfiguration.testCpu) {
            RuntimeConfiguration cpuConf = new RuntimeConfiguration();
            cpuConf.getAparapiConfiguration().setExecutionMode(SEQ);
            cpuConf.setUseDataSharedMemory(false);
            cpuConf.setUseWeightsSharedMemory(false);
            Environment.getInstance().setRuntimeConfiguration(cpuConf);
            ConnectionCalculator cpuWeightedSum = OperationsFactory.weightedSum();
            ValuesProvider cpuVP = TensorFactory.tensorProvider(connection, minibatchSize, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
            TensorFactory.copy(input, cpuVP.get(connection.getInputLayer()));
            // prepare the kernel
            cpuWeightedSum.calculate(connections, cpuVP, connection.getOutputLayer());
            cpuOutput = TensorFactory.tensor(cpuVP.get(connection.getOutputLayer()).getDimensions());
            TensorFactory.copy(cpuVP.get(connection.getOutputLayer()), cpuOutput);
            // measure time
            for (int i = 0; i < (kernelConfiguration.kernelRuns); i++) {
                cpuWeightedSum.calculate(connections, cpuVP, connection.getOutputLayer());
            }
        }
        // Aparapi
        Tensor aparapiOutput = null;
        if (kernelConfiguration.testAparapi) {
            RuntimeConfiguration aparapiConf = new RuntimeConfiguration();
            aparapiConf.getAparapiConfiguration().setExecutionMode(GPU);
            aparapiConf.setUseDataSharedMemory(false);
            aparapiConf.setUseWeightsSharedMemory(false);
            Environment.getInstance().setRuntimeConfiguration(aparapiConf);
            ConnectionCalculator aparapiWeightedSum = OperationsFactory.weightedSum();
            ValuesProvider aparapiVP = TensorFactory.tensorProvider(connection, minibatchSize, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
            TensorFactory.copy(input, aparapiVP.get(connection.getInputLayer()));
            // prepare the kernel
            aparapiWeightedSum.calculate(connections, aparapiVP, connection.getOutputLayer());
            aparapiOutput = TensorFactory.tensor(aparapiVP.get(connection.getOutputLayer()).getDimensions());
            TensorFactory.copy(aparapiVP.get(connection.getOutputLayer()), aparapiOutput);
            for (int i = 0; i < (kernelConfiguration.kernelRuns); i++) {
                aparapiWeightedSum.calculate(connections, aparapiVP, connection.getOutputLayer());
            }
        }
        if ((oclOutput != null) && (cpuOutput != null)) {
            TensorIterator oclIt = oclOutput.iterator();
            TensorIterator cpuIt = cpuOutput.iterator();
            while ((oclIt.hasNext()) && (cpuIt.hasNext())) {
                Assert.assertEquals(oclOutput.getElements()[oclIt.next()], cpuOutput.getElements()[cpuIt.next()], 1.0E-4F);
            } 
        }
    }

    private static class KernelConfiguration {
        private FullyConnected connection;

        /**
         * how many times to execute each opencl kernel. Note that the output array is not erased after each cycle. This means that, while the input is always the same, consecutive executions of the same kernels will
         * produce different results
         */
        private int kernelRuns;

        /**
         * set to true to test using OpenCL
         */
        private boolean testOpenCL = true;

        /**
         * set to true to compare the results between OpenCL and Aparapi
         */
        private boolean testAparapi = true;

        /**
         * set to true to inlcude CPU testing for performance comparison
         */
        private boolean testCpu = true;
    }
}

