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
package org.deeplearning4j.parallelism;


import Activation.SOFTMAX;
import Activation.TANH;
import ConvolutionMode.Same;
import InferenceMode.BATCHED;
import InferenceMode.SEQUENTIAL;
import ParallelInference.ObservablesProvider;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.exception.DL4JInvalidInputException;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.parallelism.inference.InferenceMode;
import org.deeplearning4j.parallelism.inference.InferenceObservable;
import org.deeplearning4j.parallelism.inference.observers.BasicInferenceObserver;
import org.deeplearning4j.parallelism.inference.observers.BatchedInferenceObservable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

import static PoolingType.AVG;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class ParallelInferenceTest {
    private static MultiLayerNetwork model;

    private static DataSetIterator iterator;

    @Test(timeout = 30000L)
    public void testInferenceSequential1() throws Exception {
        long count0 = 0;
        long count1 = 0;
        // We can't guarantee that on any particular run each thread will get data - it might randomly be assigned to
        // only one. Consequently: we'll run the test multiple times and ensure that in at least *some* of the test
        // runs both workers get some data.
        for (int i = 0; (i < 20) && ((count0 == 0) || (count1 == 0)); i++) {
            ParallelInferenceTest.iterator = new MnistDataSetIterator(1, false, 12345);
            ParallelInference inf = new ParallelInference.Builder(ParallelInferenceTest.model).inferenceMode(SEQUENTIAL).workers(2).build();
            log.info("Features shape: {}", Arrays.toString(ParallelInferenceTest.iterator.next().getFeatures().shapeInfoDataBuffer().asInt()));
            INDArray array1 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            INDArray array2 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            Assert.assertFalse(array1.isAttached());
            Assert.assertFalse(array2.isAttached());
            INDArray array3 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            Assert.assertFalse(array3.isAttached());
            ParallelInferenceTest.iterator.reset();
            evalClassifcationSingleThread(inf, ParallelInferenceTest.iterator);
            count0 = inf.getWorkerCounter(0);
            count1 = inf.getWorkerCounter(1);
            // System.out.println("Counts: " + count0 + ", " + count1);
        }
        // both workers threads should have non-zero
        Assert.assertTrue((count0 > 0L));
        Assert.assertTrue((count1 > 0L));
    }

    @Test(timeout = 30000L)
    public void testInferenceSequential2() throws Exception {
        long count0 = 0;
        long count1 = 0;
        // We can't guarantee that on any particular run each thread will get data - it might randomly be assigned to
        // only one. Consequently: we'll run the test multiple times and ensure that in at least *some* of the test
        // runs both workers get some data.
        for (int i = 0; (i < 20) && ((count0 == 0) || (count1 == 0)); i++) {
            ParallelInferenceTest.iterator = new MnistDataSetIterator(1, false, 12345);
            ParallelInference inf = new ParallelInference.Builder(ParallelInferenceTest.model).inferenceMode(SEQUENTIAL).workers(2).build();
            log.info("Features shape: {}", Arrays.toString(ParallelInferenceTest.iterator.next().getFeatures().shapeInfoDataBuffer().asInt()));
            INDArray array1 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            INDArray array2 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            Assert.assertFalse(array1.isAttached());
            Assert.assertFalse(array2.isAttached());
            INDArray array3 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            Assert.assertFalse(array3.isAttached());
            ParallelInferenceTest.iterator.reset();
            evalClassifcationMultipleThreads(inf, ParallelInferenceTest.iterator, 10);
            // both workers threads should have non-zero
            count0 = inf.getWorkerCounter(0);
            count1 = inf.getWorkerCounter(1);
            // System.out.println("Counts: " + count0 + ", " + count1);
        }
        Assert.assertTrue((count0 > 0L));
        Assert.assertTrue((count1 > 0L));
    }

    @Test(timeout = 30000L)
    public void testInferenceBatched1() throws Exception {
        long count0 = 0;
        long count1 = 0;
        // We can't guarantee that on any particular run each thread will get data - it might randomly be assigned to
        // only one. Consequently: we'll run the test multiple times and ensure that in at least *some* of the test
        // runs both workers get some data.
        for (int i = 0; (i < 20) && ((count0 == 0) || (count1 == 0)); i++) {
            ParallelInference inf = new ParallelInference.Builder(ParallelInferenceTest.model).inferenceMode(BATCHED).batchLimit(8).workers(2).build();
            ParallelInferenceTest.iterator = new MnistDataSetIterator(1, false, 12345);
            log.info("Features shape: {}", Arrays.toString(ParallelInferenceTest.iterator.next().getFeatures().shapeInfoDataBuffer().asInt()));
            INDArray array1 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            INDArray array2 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            Assert.assertFalse(array1.isAttached());
            Assert.assertFalse(array2.isAttached());
            INDArray array3 = inf.output(ParallelInferenceTest.iterator.next().getFeatures());
            Assert.assertFalse(array3.isAttached());
            ParallelInferenceTest.iterator.reset();
            evalClassifcationMultipleThreads(inf, ParallelInferenceTest.iterator, 20);
            // both workers threads should have non-zero
            count0 = inf.getWorkerCounter(0);
            count1 = inf.getWorkerCounter(1);
            // System.out.println("Counts: " + count0 + ", " + count1);
        }
        Assert.assertTrue((count0 > 0L));
        Assert.assertTrue((count1 > 0L));
    }

    @Test
    public void testProvider1() throws Exception {
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        BasicInferenceObserver observer = new BasicInferenceObserver();
        ParallelInference.ObservablesProvider provider = new ParallelInference.ObservablesProvider(10000000L, 100, queue);
        InferenceObservable observable1 = provider.setInput(observer, Nd4j.create(1, 100));
        InferenceObservable observable2 = provider.setInput(observer, Nd4j.create(1, 100));
        Assert.assertNotEquals(null, observable1);
        Assert.assertTrue((observable1 == observable2));
    }

    @Test
    public void testProvider2() throws Exception {
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        BasicInferenceObserver observer = new BasicInferenceObserver();
        ParallelInference.ObservablesProvider provider = new ParallelInference.ObservablesProvider(10000000L, 100, queue);
        InferenceObservable observable1 = provider.setInput(observer, Nd4j.create(1, 100).assign(1.0));
        InferenceObservable observable2 = provider.setInput(observer, Nd4j.create(1, 100).assign(2.0));
        Assert.assertNotEquals(null, observable1);
        Assert.assertTrue((observable1 == observable2));
        List<Pair<INDArray[], INDArray[]>> l = observable1.getInputBatches();
        Assert.assertEquals(1, l.size());
        INDArray[] input = l.get(0).getFirst();
        Assert.assertNull(l.get(0).getSecond());
        Assert.assertEquals(1, input.length);
        Assert.assertArrayEquals(new long[]{ 2, 100 }, input[0].shape());
        Assert.assertEquals(1.0F, input[0].tensorAlongDimension(0, 1).meanNumber().floatValue(), 0.001);
        Assert.assertEquals(2.0F, input[0].tensorAlongDimension(1, 1).meanNumber().floatValue(), 0.001);
    }

    @Test
    public void testProvider3() throws Exception {
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        BasicInferenceObserver observer = new BasicInferenceObserver();
        ParallelInference.ObservablesProvider provider = new ParallelInference.ObservablesProvider(10000000L, 2, queue);
        InferenceObservable observable1 = provider.setInput(observer, Nd4j.create(1, 100).assign(1.0));
        InferenceObservable observable2 = provider.setInput(observer, Nd4j.create(1, 100).assign(2.0));
        InferenceObservable observable3 = provider.setInput(observer, Nd4j.create(1, 100).assign(3.0));
        Assert.assertNotEquals(null, observable1);
        Assert.assertNotEquals(null, observable3);
        Assert.assertTrue((observable1 == observable2));
        Assert.assertTrue((observable1 != observable3));
        List<Pair<INDArray[], INDArray[]>> l = observable1.getInputBatches();
        Assert.assertEquals(1, l.size());
        INDArray[] input = l.get(0).getFirst();
        Assert.assertNull(l.get(0).getSecond());
        Assert.assertEquals(1.0F, input[0].tensorAlongDimension(0, 1).meanNumber().floatValue(), 0.001);
        Assert.assertEquals(2.0F, input[0].tensorAlongDimension(1, 1).meanNumber().floatValue(), 0.001);
        l = observable3.getInputBatches();
        Assert.assertEquals(1, l.size());
        input = l.get(0).getFirst();
        Assert.assertNull(l.get(0).getSecond());
        Assert.assertEquals(3.0F, input[0].tensorAlongDimension(0, 1).meanNumber().floatValue(), 0.001);
    }

    @Test
    public void testProvider4() throws Exception {
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        BasicInferenceObserver observer = new BasicInferenceObserver();
        ParallelInference.ObservablesProvider provider = new ParallelInference.ObservablesProvider(10000000L, 4, queue);
        BatchedInferenceObservable observable1 = ((BatchedInferenceObservable) (provider.setInput(observer, Nd4j.create(1, 100).assign(1.0))));
        BatchedInferenceObservable observable2 = ((BatchedInferenceObservable) (provider.setInput(observer, Nd4j.create(1, 100).assign(2.0))));
        BatchedInferenceObservable observable3 = ((BatchedInferenceObservable) (provider.setInput(observer, Nd4j.create(1, 100).assign(3.0))));
        INDArray bigOutput = Nd4j.create(3, 10);
        for (int i = 0; i < (bigOutput.rows()); i++)
            bigOutput.getRow(i).assign(((float) (i)));

        Field f = BatchedInferenceObservable.class.getDeclaredField("outputBatchInputArrays");
        f.setAccessible(true);
        List<int[]> l = new ArrayList<>();
        l.add(new int[]{ 0, 2 });
        f.set(observable3, l);
        f = BatchedInferenceObservable.class.getDeclaredField("inputs");
        f.setAccessible(true);
        f.set(observable3, Arrays.asList(new INDArray[]{ bigOutput.getRow(0) }, new INDArray[]{ bigOutput.getRow(1) }, new INDArray[]{ bigOutput.getRow(2) }));
        observable3.setOutputBatches(Collections.singletonList(new INDArray[]{ bigOutput }));
        INDArray out = null;
        observable3.setPosition(0);
        out = observable3.getOutput()[0];
        Assert.assertArrayEquals(new long[]{ 1, 10 }, out.shape());
        Assert.assertEquals(0.0F, out.meanNumber().floatValue(), 0.01F);
        observable3.setPosition(1);
        out = observable3.getOutput()[0];
        Assert.assertArrayEquals(new long[]{ 1, 10 }, out.shape());
        Assert.assertEquals(1.0F, out.meanNumber().floatValue(), 0.01F);
        observable3.setPosition(2);
        out = observable3.getOutput()[0];
        Assert.assertArrayEquals(new long[]{ 1, 10 }, out.shape());
        Assert.assertEquals(2.0F, out.meanNumber().floatValue(), 0.01F);
    }

    @Test(timeout = 30000L)
    public void testParallelInferenceVariableLengthTS() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        int nIn = 10;
        int[] tsLengths = new int[]{ 3, 5, 7, 10, 50, 100 };
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().activation(TANH).seed(12345).list().layer(new LSTM.Builder().nIn(nIn).nOut(5).build()).layer(new RnnOutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        for (InferenceMode m : InferenceMode.values()) {
            for (int w : new int[]{ 1, 2 }) {
                final ParallelInference inf = new ParallelInference.Builder(net).inferenceMode(m).batchLimit(20).queueLimit(64).workers(w).build();
                List<INDArray> arrs = new ArrayList<>();
                List<INDArray> exp = new ArrayList<>();
                for (int l : tsLengths) {
                    INDArray in = Nd4j.rand(new int[]{ 1, nIn, l });
                    arrs.add(in);
                    INDArray out = net.output(in);
                    exp.add(out);
                }
                ParallelInferenceTest.testParallelInference(inf, arrs, exp);
                inf.shutdown();
            }
        }
    }

    @Test(timeout = 60000L)
    public void testParallelInferenceVariableLengthTS2() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        int nIn = 10;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().activation(TANH).seed(12345).list().layer(new LSTM.Builder().nIn(nIn).nOut(5).build()).layer(new RnnOutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        int[] defaultSize = new int[]{ 1, 10, 5 };
        for (InferenceMode m : InferenceMode.values()) {
            for (int w : new int[]{ 2, 3 }) {
                final ParallelInference inf = new ParallelInference.Builder(net).inferenceMode(m).batchLimit(20).queueLimit(64).workers(w).build();
                List<INDArray> arrs = new ArrayList<>();
                List<INDArray> exp = new ArrayList<>();
                Random r = new Random();
                for (int i = 0; i < 500; i++) {
                    int[] shape = defaultSize;
                    if ((r.nextDouble()) < 0.4) {
                        shape = new int[]{ (r.nextInt(5)) + 1, 10, (r.nextInt(10)) + 1 };
                    }
                    INDArray in = Nd4j.rand(shape);
                    arrs.add(in);
                    INDArray out = net.output(in);
                    exp.add(out);
                }
                ParallelInferenceTest.testParallelInference(inf, arrs, exp);
                inf.shutdown();
            }
        }
    }

    @Test(timeout = 30000L)
    public void testParallelInferenceVariableSizeCNN() throws Exception {
        // Variable size input for CNN model - for example, YOLO models
        // In these cases, we can't batch and have to execute the different size inputs separately
        Nd4j.getRandom().setSeed(12345);
        int nIn = 3;
        int[][] shapes = new int[][]{ new int[]{ 1, nIn, 10, 10 }, new int[]{ 1, nIn, 10, 15 }, new int[]{ 1, nIn, 20, 15 }, new int[]{ 1, nIn, 20, 20 }, new int[]{ 1, nIn, 30, 30 }, new int[]{ 1, nIn, 40, 40 }, new int[]{ 1, nIn, 40, 45 } };
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().activation(TANH).seed(12345).list().layer(new ConvolutionLayer.Builder().nIn(nIn).nOut(5).build()).layer(new CnnLossLayer.Builder().activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        for (InferenceMode m : InferenceMode.values()) {
            for (int w : new int[]{ 1, 2 }) {
                final ParallelInference inf = new ParallelInference.Builder(net).inferenceMode(m).batchLimit(20).queueLimit(64).workers(w).build();
                List<INDArray> arrs = new ArrayList<>();
                List<INDArray> exp = new ArrayList<>();
                for (int[] shape : shapes) {
                    INDArray in = Nd4j.rand(shape);
                    arrs.add(in);
                    INDArray out = net.output(in);
                    exp.add(out);
                }
                ParallelInferenceTest.testParallelInference(inf, arrs, exp);
                inf.shutdown();
            }
        }
    }

    @Test(timeout = 30000L)
    public void testParallelInferenceVariableSizeCNN2() throws Exception {
        // Variable size input for CNN model - for example, YOLO models
        // In these cases, we can't batch and have to execute the different size inputs separately
        Nd4j.getRandom().setSeed(12345);
        int nIn = 3;
        int[] defaultShape = new int[]{ 1, nIn, 16, 16 };
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().activation(TANH).seed(12345).convolutionMode(Same).list().layer(new ConvolutionLayer.Builder().nIn(nIn).nOut(5).build()).layer(new CnnLossLayer.Builder().activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        for (InferenceMode m : InferenceMode.values()) {
            for (int w : new int[]{ 1, 2 }) {
                final ParallelInference inf = new ParallelInference.Builder(net).inferenceMode(m).batchLimit(20).queueLimit(64).workers(w).build();
                List<INDArray> arrs = new ArrayList<>();
                List<INDArray> exp = new ArrayList<>();
                Random r = new Random();
                for (int i = 0; i < 500; i++) {
                    int[] shape = defaultShape;
                    if ((r.nextDouble()) < 0.4) {
                        shape = new int[]{ (r.nextInt(5)) + 1, nIn, 10, (r.nextInt(10)) + 1 };
                    }
                    INDArray in = Nd4j.rand(shape);
                    arrs.add(in);
                    INDArray out = net.output(in);
                    exp.add(out);
                }
                ParallelInferenceTest.testParallelInference(inf, arrs, exp);
                inf.shutdown();
            }
        }
    }

    @Test(timeout = 20000L)
    public void testParallelInferenceErrorPropagation() {
        int nIn = 10;
        int wrongNIn = 5;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().activation(TANH).seed(12345).list().layer(new DenseLayer.Builder().nIn(nIn).nOut(5).build()).layer(new OutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray inOk = Nd4j.ones(1, nIn);
        INDArray inWrong = Nd4j.ones(1, wrongNIn);
        INDArray expOk = net.output(inOk);
        for (InferenceMode m : InferenceMode.values()) {
            for (int w : new int[]{ 1, 2 }) {
                log.info("Starting: m={}, w={}", m, w);
                final ParallelInference inf = new ParallelInference.Builder(net).inferenceMode(m).batchLimit(20).queueLimit(64).workers(w).build();
                INDArray actOk = inf.output(inOk);
                Assert.assertEquals(expOk, actOk);
                try {
                    inf.output(inWrong);
                    Assert.fail("Expected exception");
                } catch (DL4JInvalidInputException e) {
                    // OK
                    System.out.println(("Expected exception: " + (e.getMessage())));
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail("Expected other exception type");
                }
                actOk = inf.output(inOk);
                Assert.assertEquals(expOk, actOk);
                inf.shutdown();
            }
        }
    }

    @Test
    public void testInputMaskingCyclic() throws Exception {
        for (int e = 0; e < 3; e++) {
            testInputMasking();
            log.info("Iteration: {} finished", e);
            System.gc();
        }
    }

    @Test(timeout = 60000)
    public void testInputMasking() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        int nIn = 10;
        int tsLength = 16;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().activation(TANH).seed(12345).list().layer(new LSTM.Builder().nIn(nIn).nOut(5).build()).layer(new GlobalPoolingLayer(AVG)).layer(new OutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        Random r = new Random();
        for (InferenceMode m : InferenceMode.values()) {
            log.info("Testing inference mode: [{}]", m);
            for (int w : new int[]{ 1, 2 }) {
                for (boolean randomTSLength : new boolean[]{ false, true }) {
                    final ParallelInference inf = new ParallelInference.Builder(net).inferenceMode(m).batchLimit(5).queueLimit(64).workers(w).build();
                    List<INDArray> in = new ArrayList<>();
                    List<INDArray> inMasks = new ArrayList<>();
                    List<INDArray> exp = new ArrayList<>();
                    for (int i = 0; i < 100; i++) {
                        int currTSLength = (randomTSLength) ? 1 + (r.nextInt(tsLength)) : tsLength;
                        int currNumEx = 1 + (r.nextInt(3));
                        INDArray inArr = Nd4j.rand(new int[]{ currNumEx, nIn, currTSLength });
                        in.add(inArr);
                        INDArray inMask = null;
                        if ((r.nextDouble()) < 0.5) {
                            inMask = Nd4j.ones(currNumEx, currTSLength);
                            for (int mb = 0; mb < currNumEx; mb++) {
                                if (currTSLength > 1) {
                                    int firstMaskedStep = 1 + (r.nextInt(currTSLength));
                                    for (int j = firstMaskedStep; j < currTSLength; j++) {
                                        inMask.putScalar(mb, j, 0.0);
                                    }
                                }
                            }
                        }
                        inMasks.add(inMask);
                        INDArray out = net.output(inArr, false, inMask, null);
                        exp.add(out);
                    }
                    ParallelInferenceTest.testParallelInference(inf, in, inMasks, exp);
                    inf.shutdown();
                }
            }
        }
    }

    @Test(timeout = 20000L)
    public void testModelUpdate_1() throws Exception {
        int nIn = 5;
        val conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("out0", new OutputLayer.Builder().nIn(nIn).nOut(4).activation(SOFTMAX).build(), "in").layer("out1", new OutputLayer.Builder().nIn(nIn).nOut(6).activation(SOFTMAX).build(), "in").setOutputs("out0", "out1").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        val inf = new ParallelInference.Builder(net).inferenceMode(SEQUENTIAL).batchLimit(5).queueLimit(64).workers(4).build();
        // imitating use of the original model
        for (int e = 0; e < 10; e++) {
            val output = inf.output(new INDArray[]{ Nd4j.createUninitialized(1, 5) });
            Assert.assertNotNull(output);
            Assert.assertNotEquals(0, output.length);
        }
        Model[] modelsBefore = inf.getCurrentModelsFromWorkers();
        Assert.assertEquals(4, modelsBefore.length);
        boolean passed = false;
        int cnt0 = 0;
        for (Model m : modelsBefore) {
            // model can be null for some of the workers yet, due to race condition
            if (m != null) {
                Thread.sleep(500);
                Assert.assertEquals((("Failed at model [" + cnt0) + "]"), net.params(), m.params());
                passed = true;
            }
            cnt0++;
        }
        Assert.assertTrue(passed);
        val conf2 = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("out0", new OutputLayer.Builder().nIn(nIn).nOut(4).build(), "in").layer("out1", new OutputLayer.Builder().nIn(nIn).nOut(6).build(), "in").layer("out2", new OutputLayer.Builder().nIn(nIn).nOut(8).build(), "in").setOutputs("out0", "out1", "out2").build();
        val net2 = new ComputationGraph(conf2);
        net2.init();
        inf.updateModel(net2);
        val modelsAfter = inf.getCurrentModelsFromWorkers();
        Assert.assertEquals(4, modelsAfter.length);
        cnt0 = 0;
        for (val m : modelsAfter) {
            Assert.assertNotNull((("Failed at model [" + cnt0) + "]"), m);
            Assert.assertEquals((("Failed at model [" + (cnt0++)) + "]"), net2.params(), m.params());
        }
        inf.shutdown();
    }

    @Test(timeout = 60000L)
    public void testMultiOutputNet() throws Exception {
        int nIn = 5;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("out0", new OutputLayer.Builder().nIn(nIn).nOut(4).activation(SOFTMAX).build(), "in").layer("out1", new OutputLayer.Builder().nIn(nIn).nOut(6).activation(SOFTMAX).build(), "in").setOutputs("out0", "out1").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        Random r = new Random();
        for (InferenceMode m : InferenceMode.values()) {
            for (int w : new int[]{ 1, 2 }) {
                final ParallelInference inf = new ParallelInference.Builder(net).inferenceMode(m).batchLimit(5).queueLimit(64).workers(w).build();
                List<INDArray[]> in = new ArrayList<>();
                List<INDArray[]> exp = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    int currNumEx = 1 + (r.nextInt(3));
                    INDArray inArr = Nd4j.rand(new int[]{ currNumEx, nIn });
                    in.add(new INDArray[]{ inArr });
                    INDArray[] out = net.output(inArr);
                    exp.add(out);
                }
                ParallelInferenceTest.testParallelInferenceMulti(inf, in, null, exp);
                inf.shutdown();
            }
        }
    }
}

