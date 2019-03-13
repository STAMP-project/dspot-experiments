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
package org.deeplearning4j.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.dataset.api.preprocessor.Normalizer;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by agibsonccc on 12/29/16.
 */
public class ModelGuesserTest extends BaseDL4JTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testModelGuessFile() throws Exception {
        ClassPathResource sequenceResource = new ClassPathResource("modelimport/keras/examples/mnist_mlp/mnist_mlp_tf_keras_1_model.h5");
        Assert.assertTrue(sequenceResource.exists());
        File f = getTempFile(sequenceResource);
        Model guess1 = ModelGuesser.loadModelGuess(f.getAbsolutePath());
        Assume.assumeNotNull(guess1);
        ClassPathResource sequenceResource2 = new ClassPathResource("modelimport/keras/examples/mnist_cnn/mnist_cnn_tf_keras_1_model.h5");
        Assert.assertTrue(sequenceResource2.exists());
        File f2 = getTempFile(sequenceResource);
        Model guess2 = ModelGuesser.loadModelGuess(f2.getAbsolutePath());
        Assume.assumeNotNull(guess2);
    }

    @Test
    public void testModelGuessInputStream() throws Exception {
        ClassPathResource sequenceResource = new ClassPathResource("modelimport/keras/examples/mnist_mlp/mnist_mlp_tf_keras_1_model.h5");
        Assert.assertTrue(sequenceResource.exists());
        File f = getTempFile(sequenceResource);
        try (InputStream inputStream = new FileInputStream(f)) {
            Model guess1 = ModelGuesser.loadModelGuess(inputStream);
            Assume.assumeNotNull(guess1);
        }
        ClassPathResource sequenceResource2 = new ClassPathResource("modelimport/keras/examples/mnist_cnn/mnist_cnn_tf_keras_1_model.h5");
        Assert.assertTrue(sequenceResource2.exists());
        File f2 = getTempFile(sequenceResource);
        try (InputStream inputStream = new FileInputStream(f2)) {
            Model guess1 = ModelGuesser.loadModelGuess(inputStream);
            Assume.assumeNotNull(guess1);
        }
    }

    @Test
    public void testLoadNormalizersFile() throws Exception {
        MultiLayerNetwork net = getNetwork();
        File tempFile = testDir.newFile("testLoadNormalizersFile.bin");
        ModelSerializer.writeModel(net, tempFile, true);
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fit(new org.nd4j.linalg.dataset.DataSet(Nd4j.rand(new int[]{ 2, 2 }), Nd4j.rand(new int[]{ 2, 2 })));
        ModelSerializer.addNormalizerToModel(tempFile, normalizer);
        Model model = ModelGuesser.loadModelGuess(tempFile.getAbsolutePath());
        Normalizer<?> normalizer1 = ModelGuesser.loadNormalizer(tempFile.getAbsolutePath());
        Assert.assertEquals(model, net);
        Assert.assertEquals(normalizer, normalizer1);
    }

    @Test
    public void testNormalizerInPlace() throws Exception {
        MultiLayerNetwork net = getNetwork();
        File tempFile = testDir.newFile("testNormalizerInPlace.bin");
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fit(new org.nd4j.linalg.dataset.DataSet(Nd4j.rand(new int[]{ 2, 2 }), Nd4j.rand(new int[]{ 2, 2 })));
        ModelSerializer.writeModel(net, tempFile, true, normalizer);
        Model model = ModelGuesser.loadModelGuess(tempFile.getAbsolutePath());
        Normalizer<?> normalizer1 = ModelGuesser.loadNormalizer(tempFile.getAbsolutePath());
        Assert.assertEquals(model, net);
        Assert.assertEquals(normalizer, normalizer1);
    }

    @Test
    public void testLoadNormalizersInputStream() throws Exception {
        MultiLayerNetwork net = getNetwork();
        File tempFile = testDir.newFile("testLoadNormalizersInputStream.bin");
        ModelSerializer.writeModel(net, tempFile, true);
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fit(new org.nd4j.linalg.dataset.DataSet(Nd4j.rand(new int[]{ 2, 2 }), Nd4j.rand(new int[]{ 2, 2 })));
        ModelSerializer.addNormalizerToModel(tempFile, normalizer);
        Model model = ModelGuesser.loadModelGuess(tempFile.getAbsolutePath());
        try (InputStream inputStream = new FileInputStream(tempFile)) {
            Normalizer<?> normalizer1 = ModelGuesser.loadNormalizer(inputStream);
            Assert.assertEquals(model, net);
            Assert.assertEquals(normalizer, normalizer1);
        }
    }

    @Test
    public void testModelGuesserDl4jModelFile() throws Exception {
        MultiLayerNetwork net = getNetwork();
        File tempFile = testDir.newFile("testModelGuesserDl4jModelFile.bin");
        ModelSerializer.writeModel(net, tempFile, true);
        MultiLayerNetwork network = ((MultiLayerNetwork) (ModelGuesser.loadModelGuess(tempFile.getAbsolutePath())));
        Assert.assertEquals(network.getLayerWiseConfigurations().toJson(), net.getLayerWiseConfigurations().toJson());
        Assert.assertEquals(net.params(), network.params());
        Assert.assertEquals(net.getUpdater().getStateViewArray(), network.getUpdater().getStateViewArray());
    }

    @Test
    public void testModelGuesserDl4jModelInputStream() throws Exception {
        MultiLayerNetwork net = getNetwork();
        File tempFile = testDir.newFile("testModelGuesserDl4jModelInputStream.bin");
        ModelSerializer.writeModel(net, tempFile, true);
        try (InputStream inputStream = new FileInputStream(tempFile)) {
            MultiLayerNetwork network = ((MultiLayerNetwork) (ModelGuesser.loadModelGuess(inputStream)));
            Assume.assumeNotNull(network);
            Assert.assertEquals(network.getLayerWiseConfigurations().toJson(), net.getLayerWiseConfigurations().toJson());
            Assert.assertEquals(net.params(), network.params());
            Assert.assertEquals(net.getUpdater().getStateViewArray(), network.getUpdater().getStateViewArray());
        }
    }

    @Test
    public void testModelGuessConfigFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("modelimport/keras/configs/cnn_tf_config.json", ModelGuesserTest.class.getClassLoader());
        File f = getTempFile(resource);
        String configFilename = f.getAbsolutePath();
        Object conf = ModelGuesser.loadConfigGuess(configFilename);
        Assert.assertTrue((conf instanceof MultiLayerConfiguration));
        ClassPathResource sequenceResource = new ClassPathResource("/keras/simple/mlp_fapi_multiloss_config.json");
        File f2 = getTempFile(sequenceResource);
        Object sequenceConf = ModelGuesser.loadConfigGuess(f2.getAbsolutePath());
        Assert.assertTrue((sequenceConf instanceof ComputationGraphConfiguration));
        ClassPathResource resourceDl4j = new ClassPathResource("model.json");
        File fDl4j = getTempFile(resourceDl4j);
        String configFilenameDl4j = fDl4j.getAbsolutePath();
        Object confDl4j = ModelGuesser.loadConfigGuess(configFilenameDl4j);
        Assert.assertTrue((confDl4j instanceof ComputationGraphConfiguration));
    }

    @Test
    public void testModelGuessConfigInputStream() throws Exception {
        ClassPathResource resource = new ClassPathResource("modelimport/keras/configs/cnn_tf_config.json", ModelGuesserTest.class.getClassLoader());
        File f = getTempFile(resource);
        try (InputStream inputStream = new FileInputStream(f)) {
            Object conf = ModelGuesser.loadConfigGuess(inputStream);
            Assert.assertTrue((conf instanceof MultiLayerConfiguration));
        }
        ClassPathResource sequenceResource = new ClassPathResource("/keras/simple/mlp_fapi_multiloss_config.json");
        File f2 = getTempFile(sequenceResource);
        try (InputStream inputStream = new FileInputStream(f2)) {
            Object sequenceConf = ModelGuesser.loadConfigGuess(inputStream);
            Assert.assertTrue((sequenceConf instanceof ComputationGraphConfiguration));
        }
        ClassPathResource resourceDl4j = new ClassPathResource("model.json");
        File fDl4j = getTempFile(resourceDl4j);
        try (InputStream inputStream = new FileInputStream(fDl4j)) {
            Object confDl4j = ModelGuesser.loadConfigGuess(inputStream);
            Assert.assertTrue((confDl4j instanceof ComputationGraphConfiguration));
        }
    }
}

