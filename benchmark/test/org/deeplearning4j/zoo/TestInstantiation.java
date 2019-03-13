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
package org.deeplearning4j.zoo;


import Activation.IDENTITY;
import Activation.SOFTMAX;
import LossFunctions.LossFunction.MCXENT;
import PretrainedType.IMAGENET;
import java.io.IOException;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.datasets.iterator.impl.BenchmarkDataSetIterator;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.LossLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.transferlearning.TransferLearningHelper;
import org.deeplearning4j.zoo.model.helper.DarknetHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Tests workflow for zoo model instantiation.
 *
 * @author Justin Long (crockpotveggies)
 */
@Slf4j
public class TestInstantiation extends BaseDL4JTest {
    @Test
    public void testMultipleCnnTraining() throws Exception {
        ZooModel[] models = new ZooModel[]{ Darknet19.builder().numClasses(10).build(), TinyYOLO.builder().numClasses(10).build(), YOLO2.builder().numClasses(10).build() };
        for (int i = 0; i < (models.length); i++) {
            int numClasses = 10;
            ZooModel model = models[i];
            String modelName = model.getClass().getSimpleName();
            log.info(("Testing training on zoo model " + modelName));
            int gridWidth = -1;
            int gridHeight = -1;
            if ((modelName.equals("TinyYOLO")) || (modelName.equals("YOLO2"))) {
                int[] inputShapes = model.metaData().getInputShape()[0];
                gridWidth = DarknetHelper.getGridWidth(inputShapes);
                gridHeight = DarknetHelper.getGridHeight(inputShapes);
                numClasses += 4;
            }
            // set up data iterator
            int[] inputShape = model.metaData().getInputShape()[0];
            DataSetIterator iter = new BenchmarkDataSetIterator(new int[]{ 8, inputShape[0], inputShape[1], inputShape[2] }, numClasses, 1, gridWidth, gridHeight);
            Model initializedModel = model.init();
            while (iter.hasNext()) {
                DataSet ds = iter.next();
                if (initializedModel instanceof ComputationGraph)
                    ((ComputationGraph) (initializedModel)).fit(ds);
                else
                    if (initializedModel instanceof MultiLayerNetwork)
                        ((MultiLayerNetwork) (initializedModel)).fit(ds);
                    else
                        throw new IllegalStateException("Zoo models are only MultiLayerNetwork or ComputationGraph.");


            } 
            // clean up for current model
            Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
            System.gc();
            Thread.sleep(1000);
        }
    }

    @Test
    public void testInitPretrained() throws IOException {
        ZooModel model = ResNet50.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        ComputationGraph initializedModel = ((ComputationGraph) (model.initPretrained()));
        INDArray f = Nd4j.rand(new int[]{ 1, 3, 224, 224 });
        INDArray[] result = initializedModel.output(f);
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // Test fitting. Not ewe need to use transfer learning, as ResNet50 has a dense layer, not an OutputLayer
        initializedModel = removeVertexAndConnections("fc1000").addLayer("fc1000", new OutputLayer.Builder().lossFunction(MCXENT).nIn(2048).nOut(1000).activation(SOFTMAX).build(), "flatten_1").setOutputs("fc1000").build();
        initializedModel.fit(new org.nd4j.linalg.dataset.DataSet(f, TestUtils.randomOneHot(1, 1000, 12345)));
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = VGG16.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        result = initializedModel.output(Nd4j.rand(new int[]{ 1, 3, 224, 224 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = VGG19.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        result = initializedModel.output(Nd4j.rand(new int[]{ 1, 3, 224, 224 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = Darknet19.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        result = initializedModel.output(Nd4j.rand(new long[]{ 1, 3, 224, 224 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = Darknet19.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        model.setInputShape(new int[][]{ new int[]{ 3, 448, 448 } });
        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        result = initializedModel.output(Nd4j.rand(new long[]{ 1, 3, 448, 448 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = TinyYOLO.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        result = initializedModel.output(Nd4j.rand(new long[]{ 1, 3, 416, 416 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 125, 13, 13 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = YOLO2.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        result = initializedModel.output(Nd4j.rand(new int[]{ 1, 3, 608, 608 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 425, 19, 19 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = Xception.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        result = initializedModel.output(Nd4j.rand(new int[]{ 1, 3, 299, 299 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = SqueezeNet.builder().numClasses(0).build();// num labels doesn't matter since we're getting pretrained imagenet

        TestCase.assertTrue(model.pretrainedAvailable(IMAGENET));
        initializedModel = ((ComputationGraph) (model.initPretrained()));
        log.info(initializedModel.summary());
        result = initializedModel.output(Nd4j.rand(new long[]{ 1, 3, 227, 227 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
    }

    @Test
    public void testInitRandomModel() throws IOException {
        // Test initialization of NON-PRETRAINED models
        ZooModel model = ResNet50.builder().numClasses(1000).build();// num labels doesn't matter since we're getting pretrained imagenet

        log.info("Testing {}", model.getClass().getSimpleName());
        ComputationGraph initializedModel = model.init();
        INDArray f = Nd4j.rand(new int[]{ 1, 3, 224, 224 });
        INDArray[] result = initializedModel.output(f);
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        initializedModel.fit(new org.nd4j.linalg.dataset.DataSet(f, TestUtils.randomOneHot(1, 1000, 12345)));
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        // model = VGG16.builder().numClasses(1000).build();
        // initializedModel = model.init();
        // result = initializedModel.output(Nd4j.rand(new int[] {1, 3, 224, 224}));
        // assertArrayEquals(result[0].shape(), new long[] {1, 1000});
        // 
        // // clean up for current model
        // Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        // System.gc();
        model = VGG19.builder().numClasses(1000).build();
        log.info("Testing {}", model.getClass().getSimpleName());
        initializedModel = model.init();
        result = initializedModel.output(Nd4j.rand(new int[]{ 1, 3, 224, 224 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = Darknet19.builder().numClasses(1000).build();// num labels doesn't matter since we're getting pretrained imagenet

        log.info("Testing {}", model.getClass().getSimpleName());
        initializedModel = model.init();
        result = initializedModel.output(Nd4j.rand(new long[]{ 1, 3, 224, 224 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        log.info("Testing {}", model.getClass().getSimpleName());
        model = Darknet19.builder().numClasses(1000).build();
        model.setInputShape(new int[][]{ new int[]{ 3, 448, 448 } });
        initializedModel = model.init();
        result = initializedModel.output(Nd4j.rand(new long[]{ 1, 3, 448, 448 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        // model = TinyYOLO.builder().numClasses(1000).build();
        // initializedModel = model.init();
        // result = initializedModel.output(Nd4j.rand(new long[] {1, 3, 416, 416}));
        // assertArrayEquals(result[0].shape(), new long[] {1, 1000});
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        // model = YOLO2.builder().numClasses(1000).build();
        // initializedModel = model.init();
        // result = initializedModel.output(Nd4j.rand(new int[] {1, 3, 608, 608}));
        // assertArrayEquals(result[0].shape(), new long[] {1, 1000});
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = Xception.builder().numClasses(1000).build();
        log.info("Testing {}", model.getClass().getSimpleName());
        initializedModel = model.init();
        result = initializedModel.output(Nd4j.rand(new int[]{ 1, 3, 299, 299 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
        // clean up for current model
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        System.gc();
        model = SqueezeNet.builder().numClasses(1000).build();
        log.info("Testing {}", model.getClass().getSimpleName());
        initializedModel = model.init();
        log.info(initializedModel.summary());
        result = initializedModel.output(Nd4j.rand(new long[]{ 1, 3, 227, 227 }));
        Assert.assertArrayEquals(result[0].shape(), new long[]{ 1, 1000 });
    }

    @Test
    public void testYolo4635() throws Exception {
        // https://github.com/deeplearning4j/deeplearning4j/issues/4635
        int nClasses = 10;
        TinyYOLO model = TinyYOLO.builder().numClasses(nClasses).build();
        ComputationGraph computationGraph = ((ComputationGraph) (model.initPretrained()));
        TransferLearningHelper transferLearningHelper = new TransferLearningHelper(computationGraph, "conv2d_9");
    }

    @Test
    public void testInitNotPretrained() throws Exception {
        // Sanity check on the non-pretrained versions:
        ZooModel[] models = new ZooModel[]{ VGG16.builder().numClasses(10).build(), VGG19.builder().numClasses(10).build(), FaceNetNN4Small2.builder().embeddingSize(100).numClasses(10).build(), UNet.builder().build() };
        int[][] inputSizes = new int[][]{ new int[]{ 1, 3, 224, 224 }, new int[]{ 1, 3, 224, 224 }, new int[]{ 1, 3, 64, 64 }, new int[]{ 1, 3, 512, 512 } };
        for (int i = 0; i < (models.length); i++) {
            ZooModel zm = models[i];
            INDArray in = Nd4j.create(inputSizes[i]);
            Model m = zm.init();
            if (m instanceof MultiLayerNetwork) {
                MultiLayerNetwork mln = ((MultiLayerNetwork) (m));
                mln.output(in);
            } else {
                ComputationGraph cg = ((ComputationGraph) (m));
                cg.output(in);
            }
            System.gc();
        }
    }

    @Test
    public void testTransferLearning() throws Exception {
        // https://github.com/deeplearning4j/deeplearning4j/issues/7193
        ComputationGraph cg = ((ComputationGraph) (ResNet50.builder().build().initPretrained()));
        cg = new org.deeplearning4j.nn.transferlearning.TransferLearning.GraphBuilder(cg).addLayer("out", new LossLayer.Builder().lossFunction(MCXENT).activation(IDENTITY).build(), "fc1000").setInputTypes(InputType.convolutional(224, 224, 3)).setOutputs("out").build();
    }
}

