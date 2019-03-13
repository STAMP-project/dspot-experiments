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
package org.deeplearning4j.nn.misc;


import CacheMode.NONE;
import DataType.FLOAT;
import MemoryType.ACTIVATIONS;
import MemoryType.ACTIVATION_GRADIENTS;
import MemoryType.WORKING_MEMORY_VARIABLE;
import MemoryUseMode.INFERENCE;
import MemoryUseMode.TRAINING;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.memory.MemoryReport;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by Alex on 14/07/2017.
 */
public class TestMemoryReports extends BaseDL4JTest {
    @Test
    public void testMemoryReportSimple() {
        List<Pair<? extends Layer, InputType>> l = TestMemoryReports.getTestLayers();
        for (Pair<? extends Layer, InputType> p : l) {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, p.getFirst().clone()).layer(1, p.getFirst().clone()).validateOutputLayerConfig(false).build();
            MemoryReport mr = conf.getMemoryReport(p.getSecond());
            // System.out.println(mr.toString());
            // System.out.println("\n\n");
            // Test to/from JSON + YAML
            String json = mr.toJson();
            String yaml = mr.toYaml();
            MemoryReport fromJson = MemoryReport.fromJson(json);
            MemoryReport fromYaml = MemoryReport.fromYaml(yaml);
            Assert.assertEquals(mr, fromJson);
            Assert.assertEquals(mr, fromYaml);
        }
    }

    @Test
    public void testMemoryReportSimpleCG() {
        List<Pair<? extends Layer, InputType>> l = TestMemoryReports.getTestLayers();
        for (Pair<? extends Layer, InputType> p : l) {
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", p.getFirst().clone(), "in").addLayer("1", p.getFirst().clone(), "0").setOutputs("1").validateOutputLayerConfig(false).build();
            MemoryReport mr = conf.getMemoryReport(p.getSecond());
            // System.out.println(mr.toString());
            // System.out.println("\n\n");
            // Test to/from JSON + YAML
            String json = mr.toJson();
            String yaml = mr.toYaml();
            MemoryReport fromJson = MemoryReport.fromJson(json);
            MemoryReport fromYaml = MemoryReport.fromYaml(yaml);
            Assert.assertEquals(mr, fromJson);
            Assert.assertEquals(mr, fromYaml);
        }
    }

    @Test
    public void testMemoryReportsVerticesCG() {
        List<Pair<? extends GraphVertex, InputType[]>> l = TestMemoryReports.getTestVertices();
        for (Pair<? extends GraphVertex, InputType[]> p : l) {
            List<String> inputs = new ArrayList<>();
            for (int i = 0; i < (p.getSecond().length); i++) {
                inputs.add(String.valueOf(i));
            }
            String[] layerInputs = inputs.toArray(new String[inputs.size()]);
            if ((p.getFirst()) instanceof DuplicateToTimeSeriesVertex) {
                layerInputs = new String[]{ "1" };
            }
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs(inputs).allowDisconnected(true).addVertex("gv", p.getFirst(), layerInputs).setOutputs("gv").build();
            MemoryReport mr = conf.getMemoryReport(p.getSecond());
            // System.out.println(mr.toString());
            // System.out.println("\n\n");
            // Test to/from JSON + YAML
            String json = mr.toJson();
            String yaml = mr.toYaml();
            MemoryReport fromJson = MemoryReport.fromJson(json);
            MemoryReport fromYaml = MemoryReport.fromYaml(yaml);
            Assert.assertEquals(mr, fromJson);
            Assert.assertEquals(mr, fromYaml);
        }
    }

    @Test
    public void testInferInputType() {
        List<Pair<INDArray[], InputType[]>> l = new ArrayList<>();
        l.add(new Pair(new INDArray[]{ Nd4j.create(10, 8) }, new InputType[]{ InputType.feedForward(8) }));
        l.add(new Pair(new INDArray[]{ Nd4j.create(10, 8), Nd4j.create(10, 20) }, new InputType[]{ InputType.feedForward(8), InputType.feedForward(20) }));
        l.add(new Pair(new INDArray[]{ Nd4j.create(10, 8, 7) }, new InputType[]{ InputType.recurrent(8, 7) }));
        l.add(new Pair(new INDArray[]{ Nd4j.create(10, 8, 7), Nd4j.create(10, 20, 6) }, new InputType[]{ InputType.recurrent(8, 7), InputType.recurrent(20, 6) }));
        // Activations order: [m,d,h,w]
        l.add(new Pair(new INDArray[]{ Nd4j.create(10, 8, 7, 6) }, new InputType[]{ InputType.convolutional(7, 6, 8) }));
        l.add(new Pair(new INDArray[]{ Nd4j.create(10, 8, 7, 6), Nd4j.create(10, 4, 3, 2) }, new InputType[]{ InputType.convolutional(7, 6, 8), InputType.convolutional(3, 2, 4) }));
        for (Pair<INDArray[], InputType[]> p : l) {
            InputType[] act = InputType.inferInputTypes(p.getFirst());
            Assert.assertArrayEquals(p.getSecond(), act);
        }
    }

    @Test
    public void validateSimple() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new DenseLayer.Builder().nIn(10).nOut(20).build()).layer(1, new DenseLayer.Builder().nIn(20).nOut(27).build()).build();
        MemoryReport mr = conf.getMemoryReport(InputType.feedForward(10));
        int numParams = ((10 * 20) + 20) + ((20 * 27) + 27);// 787 -> 3148 bytes

        int actSize = 20 + 27;// 47 -> 188 bytes

        int total15Minibatch = numParams + (15 * actSize);
        // Fixed: should be just params
        long fixedBytes = mr.getTotalMemoryBytes(0, INFERENCE, NONE, FLOAT);
        long varBytes = (mr.getTotalMemoryBytes(1, INFERENCE, NONE, FLOAT)) - fixedBytes;
        Assert.assertEquals((numParams * 4), fixedBytes);
        Assert.assertEquals((actSize * 4), varBytes);
        long minibatch15 = mr.getTotalMemoryBytes(15, INFERENCE, NONE, FLOAT);
        Assert.assertEquals((total15Minibatch * 4), minibatch15);
        // System.out.println(fixedBytes + "\t" + varBytes);
        // System.out.println(mr.toString());
        Assert.assertEquals((actSize * 4), mr.getMemoryBytes(ACTIVATIONS, 1, TRAINING, NONE, FLOAT));
        Assert.assertEquals((actSize * 4), mr.getMemoryBytes(ACTIVATIONS, 1, INFERENCE, NONE, FLOAT));
        int inputActSize = 10 + 20;
        Assert.assertEquals((inputActSize * 4), mr.getMemoryBytes(ACTIVATION_GRADIENTS, 1, TRAINING, NONE, FLOAT));
        Assert.assertEquals(0, mr.getMemoryBytes(ACTIVATION_GRADIENTS, 1, INFERENCE, NONE, FLOAT));
        // Variable working memory - due to preout during backprop. But not it's the MAX value, as it can be GC'd or workspaced
        int workingMemVariable = 27;
        Assert.assertEquals((workingMemVariable * 4), mr.getMemoryBytes(WORKING_MEMORY_VARIABLE, 1, TRAINING, NONE, FLOAT));
        Assert.assertEquals(0, mr.getMemoryBytes(WORKING_MEMORY_VARIABLE, 1, INFERENCE, NONE, FLOAT));
    }

    @Test
    public void testPreprocessors() throws Exception {
        // https://github.com/deeplearning4j/deeplearning4j/issues/4223
        File f = new ClassPathResource("4223/CompGraphConfig.json").getTempFileFromArchive();
        String s = FileUtils.readFileToString(f, Charset.defaultCharset());
        ComputationGraphConfiguration conf = ComputationGraphConfiguration.fromJson(s);
        conf.getMemoryReport(InputType.convolutional(17, 19, 19));
    }
}

