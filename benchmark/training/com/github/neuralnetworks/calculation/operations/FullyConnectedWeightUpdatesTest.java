package com.github.neuralnetworks.calculation.operations;


import Kernel.EXECUTION_MODE.SEQ;
import RuntimeConfiguration.CalculationProvider.OPENCL;
import Tensor.TensorIterator;
import com.github.neuralnetworks.architecture.ConnectionFactory;
import com.github.neuralnetworks.architecture.Connections;
import com.github.neuralnetworks.architecture.FullyConnected;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.tensor.ValuesProvider;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.backpropagation.WeightUpdates;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 25.11.14.
 */
public class FullyConnectedWeightUpdatesTest extends AbstractTest {
    @Test
    public void randomTest01() {
        long seed = System.currentTimeMillis();
        Matrix openClResult = randomTest(AbstractTest.Runtime.OPENCL, seed, 2, 1, 10, 10);
        Matrix seqResult = randomTest(AbstractTest.Runtime.CPU_SEQ, seed, 2, 1, 10, 10);
        System.out.println(("Use seed: " + seed));
        Assert.assertTrue(isEqual(openClResult, seqResult));
    }

    @Test
    public void randomTest02() {
        long seed = System.currentTimeMillis();
        Matrix openClResult = randomTest(AbstractTest.Runtime.OPENCL, seed, 100000, 1, 10, 10);
        Matrix seqResult = randomTest(AbstractTest.Runtime.CPU_SEQ, seed, 100000, 1, 10, 10);
        System.out.println(("Use seed: " + seed));
        Assert.assertTrue(isEqual(openClResult, seqResult));
    }

    @Test
    public void randomTest03() {
        long seed = System.currentTimeMillis();
        Matrix openClResult = randomTest(AbstractTest.Runtime.OPENCL, seed, 2, 128, 10, 10);
        Matrix seqResult = randomTest(AbstractTest.Runtime.CPU_SEQ, seed, 2, 128, 10, 10);
        System.out.println(("Use seed: " + seed));
        Assert.assertTrue(isEqual(openClResult, seqResult));
    }

    @Test
    public void randomTest04() {
        long seed = System.currentTimeMillis();
        Matrix openClResult = randomTest(AbstractTest.Runtime.OPENCL, seed, 2, 32, 10000, 1000);
        Matrix seqResult = randomTest(AbstractTest.Runtime.CPU_SEQ, seed, 2, 32, 10000, 1000);
        System.out.println(("Use seed: " + seed));
        Assert.assertTrue(isEqual(openClResult, seqResult));
    }

    // taken from de.exb.neuralnetworks.samples.test.TestFullyConnectedWeightUpdates
    // but without paramaters
    @Test
    public void testOld() {
        // initialize connection weights and input
        Random r = new Random();
        r.setSeed(1);
        int minibatchSize = 32;
        FullyConnected connection = new ConnectionFactory().fullyConnected(new Layer(), new Layer(), 784, 10);
        new RandomInitializerImpl(r, (-1.0F), 1.0F).initialize(connection.getWeights());
        Matrix weights = connection.getWeights();
        ValuesProvider vp = TensorFactory.tensorProvider(connection, minibatchSize, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
        Matrix input = vp.get(connection.getOutputLayer());
        input.forEach(( i) -> input.getElements()[i] = r.nextFloat());
        ValuesProvider activations = TensorFactory.tensorProvider(connection, minibatchSize, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
        Matrix inputActivations = activations.get(connection.getInputLayer());
        inputActivations.forEach(( i) -> inputActivations.getElements()[i] = r.nextFloat());
        Matrix outputActivations = activations.get(connection.getOutputLayer());
        outputActivations.forEach(( i) -> outputActivations.getElements()[i] = r.nextFloat());
        // setup
        List<Connections> connections = new ArrayList<>();
        connections.add(connection);
        System.out.println("START KERNEL CONFIGURATION");
        // OpenCL
        Matrix oclOutput = null;
        {
            try {
                RuntimeConfiguration oclConf = new RuntimeConfiguration();
                oclConf.setCalculationProvider(OPENCL);
                oclConf.setUseDataSharedMemory(false);
                oclConf.setUseWeightsSharedMemory(false);
                oclConf.getOpenCLConfiguration().setAggregateOperations(false);
                oclConf.getOpenCLConfiguration().setSynchronizeAfterOpertation(true);
                oclConf.getAparapiConfiguration().setExecutionMode(SEQ);
                Environment.getInstance().setRuntimeConfiguration(oclConf);
                Matrix weightUpdates = TensorFactory.tensor(connection.getWeights().getDimensions());
                Matrix weightsCopy = TensorFactory.tensor(connection.getWeights().getDimensions());
                TensorFactory.copy(weights, weightsCopy);
                connection.setWeights(weightsCopy);
                WeightUpdates wu = OperationsFactory.weightUpdates(connection, vp, activations, weightUpdates);
                wu.updateWeights(0.01F, 0.1F, 1.0E-4F, 1.0E-4F);
                oclOutput = TensorFactory.tensor(connection.getWeights().getDimensions());
                TensorFactory.copy(weightsCopy, oclOutput);
                oclConf.getOpenCLConfiguration().setSynchronizeAfterOpertation(false);
                // perform "cycles" with the opencl calculator
                long start = System.currentTimeMillis();
                for (int i = 0; i < 1; i++) {
                    wu.updateWeights(0.01F, 0.1F, 1.0E-4F, 1.0E-4F);
                }
                long time = (System.currentTimeMillis()) - start;
                System.out.println((((((((("OpenCL : " + time) + " ms (") + (time / 1000)) + " s) for ") + 1) + " kernel runs, ") + ((time * 1000) / 1)) + " micro seconds per kernel run"));
            } finally {
                connection.setWeights(weights);
            }
        }
        // CPU
        Matrix cpuOutput = null;
        {
            RuntimeConfiguration cpuConf = new RuntimeConfiguration();
            cpuConf.getAparapiConfiguration().setExecutionMode(SEQ);
            cpuConf.setUseDataSharedMemory(false);
            cpuConf.setUseWeightsSharedMemory(false);
            Environment.getInstance().setRuntimeConfiguration(cpuConf);
            Matrix weightUpdates = TensorFactory.tensor(connection.getWeights().getDimensions());
            Matrix weightsCopy = TensorFactory.tensor(connection.getWeights().getDimensions());
            TensorFactory.copy(weights, weightsCopy);
            connection.setWeights(weightsCopy);
            WeightUpdates wu = OperationsFactory.weightUpdates(connection, vp, activations, weightUpdates);
            wu.updateWeights(0.01F, 0.1F, 1.0E-4F, 1.0E-4F);
            cpuOutput = TensorFactory.tensor(connection.getWeights().getDimensions());
            TensorFactory.copy(connection.getWeights(), cpuOutput);
            // to file - sometimes is needed
            // try
            // {
            // PrintWriter activationsPrinter = new PrintWriter("E:\\activations.txt");
            // activationsPrinter.print(inputActivations.getElements()[0]);
            // for (int i = 1; i < inputActivations.getElements().length; i++) {
            // activationsPrinter.print(",");
            // activationsPrinter.print(inputActivations.getElements()[i]);
            // }
            // activationsPrinter.close();
            // 
            // PrintWriter weightsStartPrinter = new PrintWriter("E:\\weights.txt");
            // weightsStartPrinter.print(weights.getElements()[0]);
            // for (int i = 1; i < weights.getElements().length; i++) {
            // weightsStartPrinter.print(",");
            // weightsStartPrinter.print(weights.getElements()[i]);
            // }
            // weightsStartPrinter.close();
            // 
            // PrintWriter weightsUpdatePrinter = new PrintWriter("E:\\weightsAfterUpdate.txt");
            // weightsUpdatePrinter.print(cpuOutput.getElements()[0]);
            // for (int i = 1; i < cpuOutput.getElements().length; i++) {
            // weightsUpdatePrinter.print(",");
            // weightsUpdatePrinter.print(cpuOutput.getElements()[i]);
            // }
            // weightsUpdatePrinter.close();
            // 
            // Matrix gradient = vp.get(connection.getOutputLayer());
            // PrintWriter output = new PrintWriter("E:\\output.txt");
            // output.print(gradient.getElements()[0]);
            // for (int i = 1; i < gradient.getElements().length; i++) {
            // output.print(",");
            // output.print(gradient.getElements()[i]);
            // }
            // output.close();
            // 
            // PrintWriter parameters = new PrintWriter("E:\\parameters.txt");
            // parameters.println(OpenCLCore.getKernelOptionsString((Kernel) wu));
            // parameters.close();
            // } catch (FileNotFoundException e)
            // {
            // e.printStackTrace();
            // }
            // measure time
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                wu.updateWeights(0.01F, 0.1F, 1.0E-4F, 1.0E-4F);
            }
            long time = (System.currentTimeMillis()) - start;
            System.out.println((((((((("CPU    : " + time) + " ms (") + (time / 1000)) + " s) for ") + 1) + " kernel runs, ") + ((time * 1000) / 1)) + " micro seconds per kernel run"));
            connection.setWeights(weights);
        }
        if ((oclOutput != null) && (cpuOutput != null)) {
            Tensor.TensorIterator oclIt = oclOutput.iterator();
            Tensor.TensorIterator cpuIt = cpuOutput.iterator();
            Tensor.TensorIterator weightsIt = weights.iterator();
            while ((oclIt.hasNext()) && (cpuIt.hasNext())) {
                int cpuId = cpuIt.next();
                Assert.assertFalse(((cpuOutput.getElements()[cpuId]) == (weights.getElements()[weightsIt.next()])));
                Assert.assertEquals(oclOutput.getElements()[oclIt.next()], cpuOutput.getElements()[cpuId], 1.0E-4F);
            } 
        }
        System.out.println("END KERNEL CONFIGURATION");
        System.out.println();
    }

    @SuppressWarnings("unused")
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

