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
package org.deeplearning4j.nn.modelimport.keras.e2e;


import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Unit tests for end-to-end Keras model import.
 *
 * @author dave@skymind.io, Max Pumperla
 */
@Slf4j
public class KerasModelEndToEndTest {
    private static final String GROUP_ATTR_INPUTS = "inputs";

    private static final String GROUP_ATTR_OUTPUTS = "outputs";

    private static final String GROUP_PREDICTIONS = "predictions";

    private static final String GROUP_ACTIVATIONS = "activations";

    private static final String TEMP_OUTPUTS_FILENAME = "tempOutputs";

    private static final String TEMP_MODEL_FILENAME = "tempModel";

    private static final String H5_EXTENSION = ".h5";

    private static final double EPS = 1.0E-5;

    private static final boolean SKIP_GRAD_CHECKS = true;

    @Rule
    public final TemporaryFolder testDir = new TemporaryFolder();

    @Test(expected = FileNotFoundException.class)
    public void fileNotFoundEndToEnd() throws Exception {
        String modelPath = "modelimport/keras/examples/foo/bar.h5";
        importEndModelTest(modelPath, null, true, true, false);
    }

    /**
     * MNIST MLP tests
     */
    @Test
    public void importMnistMlpTfKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/mnist_mlp/mnist_mlp_tf_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/mnist_mlp/mnist_mlp_tf_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    @Test
    public void importMnistMlpThKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/mnist_mlp/mnist_mlp_th_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/mnist_mlp/mnist_mlp_th_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, false, true, false);
    }

    @Test
    public void importMnistMlpTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/mnist_mlp/mnist_mlp_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/mnist_mlp/mnist_mlp_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    @Test
    public void importMnistMlpReshapeTfKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/mnist_mlp_reshape/mnist_mlp_reshape_tf_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/mnist_mlp_reshape/mnist_mlp_reshape_tf_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, true);
    }

    /**
     * MNIST CNN tests
     */
    @Test
    public void importMnistCnnTfKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/mnist_cnn/mnist_cnn_tf_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/mnist_cnn/mnist_cnn_tf_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, false, false);
    }

    @Test
    public void importMnistCnnThKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/mnist_cnn/mnist_cnn_th_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/mnist_cnn/mnist_cnn_th_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, false, true, true);
    }

    @Test
    public void importMnistCnnTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/mnist_cnn/mnist_cnn_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/mnist_cnn/mnist_cnn_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, true);
    }

    /**
     * IMDB Embedding and LSTM test
     */
    @Test
    public void importImdbLstmTfKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_tf_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_tf_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    @Test
    public void importImdbLstmThKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_th_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_th_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    @Test
    public void importImdbLstmTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    @Test
    public void importImdbLstmThKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_th_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/imdb_lstm/imdb_lstm_th_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, false, true, false);
    }

    /**
     * IMDB LSTM fasttext
     */
    // TODO: prediction checks fail due to globalpooling for fasttext, very few grads fail as well
    @Test
    public void importImdbFasttextTfKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/imdb_fasttext/imdb_fasttext_tf_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/imdb_fasttext/imdb_fasttext_tf_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, false, false, false);
    }

    @Test
    public void importImdbFasttextThKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/imdb_fasttext/imdb_fasttext_th_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/imdb_fasttext/imdb_fasttext_th_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, false, false, false);
    }

    @Test
    public void importImdbFasttextTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/imdb_fasttext/imdb_fasttext_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/imdb_fasttext/imdb_fasttext_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, false, false);
    }

    /**
     * Simple LSTM (return sequences = false) into Dense layer test
     */
    @Test
    public void importSimpleLstmTfKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/simple_lstm/simple_lstm_tf_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/simple_lstm/simple_lstm_tf_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    @Test
    public void importSimpleLstmThKeras1() throws Exception {
        String modelPath = "modelimport/keras/examples/simple_lstm/simple_lstm_th_keras_1_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/simple_lstm/simple_lstm_th_keras_1_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    @Test
    public void importSimpleLstmTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/simple_lstm/simple_lstm_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/simple_lstm/simple_lstm_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, false, false);
    }

    /**
     * Simple LSTM (return sequences = true) into flatten into Dense layer test
     */
    @Test
    public void importSimpleFlattenLstmTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/simple_flatten_lstm/simple_flatten_lstm_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/simple_flatten_lstm/" + "simple_flatten_lstm_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    /**
     * Simple RNN (return sequences = true) into flatten into Dense layer test
     */
    @Test
    public void importSimpleFlattenRnnTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/simple_flatten_rnn/simple_flatten_rnn_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/simple_flatten_rnn/" + "simple_flatten_rnn_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    /**
     * Simple RNN (return sequences = false) into Dense layer test
     */
    @Test
    public void importSimpleRnnTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/simple_rnn/simple_rnn_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/simple_rnn/" + "simple_rnn_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, true, false);
    }

    /**
     * CNN without bias test
     */
    @Test
    public void importCnnNoBiasTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/cnn_no_bias/mnist_cnn_no_bias_tf_keras_2_model.h5";
        String inputsOutputPath = "modelimport/keras/examples/cnn_no_bias/mnist_cnn_no_bias_tf_keras_2_inputs_and_outputs.h5";
        importEndModelTest(modelPath, inputsOutputPath, true, false, true);
    }

    /**
     * GAN import tests
     */
    @Test
    public void importDcganMnistDiscriminator() throws Exception {
        importSequentialModelH5Test("modelimport/keras/examples/mnist_dcgan/dcgan_discriminator_epoch_50.h5");
    }

    @Test
    public void importDcganMnistGenerator() throws Exception {
        importSequentialModelH5Test("modelimport/keras/examples/mnist_dcgan/dcgan_generator_epoch_50.h5");
    }

    /**
     * Auxillary classifier GAN import test
     */
    @Test
    public void importAcganDiscriminator() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/acgan/acgan_discriminator_1_epochs.h5");
        INDArray input = Nd4j.create(10, 1, 28, 28);
        INDArray[] output = model.output(input);
    }

    @Test
    public void importAcganGenerator() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/acgan/acgan_generator_1_epochs.h5");
        // System.out.println(model.summary()) ;
        INDArray latent = Nd4j.create(10, 100);
        INDArray label = Nd4j.create(10, 1);
        INDArray[] output = model.output(latent, label);
    }

    @Test
    public void importAcganCombined() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/acgan/acgan_combined_1_epochs.h5");
        // TODO: imports, but incorrectly. Has only one input, should have two.
    }

    /**
     * Deep convolutional GAN import test
     */
    @Test
    public void importDcganDiscriminator() throws Exception {
        importSequentialModelH5Test("modelimport/keras/examples/gans/dcgan_discriminator.h5");
    }

    @Test
    public void importDcganGenerator() throws Exception {
        importSequentialModelH5Test("modelimport/keras/examples/gans/dcgan_generator.h5");
    }

    /**
     * Wasserstein GAN import test
     */
    @Test
    public void importWganDiscriminator() throws Exception {
        for (int i = 0; i < 100; i++) {
            // run a few times to make sure HDF5 doesn't crash
            importSequentialModelH5Test("modelimport/keras/examples/gans/wgan_discriminator.h5");
        }
    }

    @Test
    public void importWganGenerator() throws Exception {
        importSequentialModelH5Test("modelimport/keras/examples/gans/wgan_generator.h5");
    }

    @Test
    public void importCnn1d() throws Exception {
        importSequentialModelH5Test("modelimport/keras/examples/cnn1d/cnn1d_flatten_tf_keras2.h5");
    }

    /**
     * DGA classifier test
     */
    @Test
    public void importDgaClassifier() throws Exception {
        importSequentialModelH5Test("modelimport/keras/examples/dga_classifier/keras2_dga_classifier_tf_model.h5");
    }

    /**
     * Reshape flat input into 3D to fit into an LSTM model
     */
    @Test
    public void importFlatIntoLSTM() throws Exception {
        importFunctionalModelH5Test("modelimport/keras/examples/reshape_to_rnn/reshape_model.h5");
    }

    /**
     * Functional LSTM test
     */
    @Test
    public void importFunctionalLstmTfKeras2() throws Exception {
        String modelPath = "modelimport/keras/examples/functional_lstm/lstm_functional_tf_keras_2.h5";
        // No training enabled
        ComputationGraph graphNoTrain = importFunctionalModelH5Test(modelPath, null, false);
        System.out.println(graphNoTrain.summary());
        // Training enabled
        ComputationGraph graph = importFunctionalModelH5Test(modelPath, null, true);
        System.out.println(graph.summary());
        // Make predictions
        int miniBatch = 32;
        INDArray input = Nd4j.ones(miniBatch, 4, 10);
        INDArray[] out = graph.output(input);
        // Fit model
        graph.fit(new INDArray[]{ input }, out);
    }

    /**
     * U-Net
     */
    @Test
    public void importUnetTfKeras2() throws Exception {
        importFunctionalModelH5Test("modelimport/keras/examples/unet/unet_keras_2_tf.h5", null, true);
    }

    /**
     * ResNet50
     */
    @Test
    public void importResnet50() throws Exception {
        importFunctionalModelH5Test("modelimport/keras/examples/resnet/resnet50_weights_tf_dim_ordering_tf_kernels.h5");
    }

    /**
     * DenseNet
     */
    @Test
    public void importDenseNet() throws Exception {
        importFunctionalModelH5Test("modelimport/keras/examples/densenet/densenet121_tf_keras_2.h5");
    }

    /**
     * SqueezeNet
     */
    @Test
    public void importSqueezeNet() throws Exception {
        importFunctionalModelH5Test("modelimport/keras/examples/squeezenet/squeezenet.h5");
    }

    /**
     * MobileNet
     */
    @Test
    public void importMobileNet() throws Exception {
        ComputationGraph graph = importFunctionalModelH5Test("modelimport/keras/examples/mobilenet/alternative.hdf5");
        INDArray input = Nd4j.ones(10, 3, 299, 299);
        graph.output(input);
    }

    /**
     * InceptionV3 Keras 2 no top
     */
    @Test
    public void importInceptionKeras2() throws Exception {
        int[] inputShape = new int[]{ 299, 299, 3 };
        ComputationGraph graph = importFunctionalModelH5Test("modelimport/keras/examples/inception/inception_tf_keras_2.h5", inputShape, false);
        INDArray input = Nd4j.ones(10, 3, 299, 299);
        graph.output(input);
        System.out.println(graph.summary());
    }

    /**
     * Xception
     */
    @Test
    public void importXception() throws Exception {
        int[] inputShape = new int[]{ 299, 299, 3 };
        ComputationGraph graph = importFunctionalModelH5Test("modelimport/keras/examples/xception/xception_tf_keras_2.h5", inputShape, false);
    }

    /**
     * Import all AlphaGo Zero model variants, i.e.
     * - Dual residual architecture
     * - Dual convolutional architecture
     * - Separate (policy and value) residual architecture
     * - Separate (policy and value) convolutional architecture
     */
    @Test
    public void importSepConvPolicy() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/agz/sep_conv_policy.h5");
        INDArray input = Nd4j.create(32, 19, 19, 10);
        model.output(input);
    }

    @Test
    public void importSepResPolicy() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/agz/sep_res_policy.h5");
        INDArray input = Nd4j.create(32, 19, 19, 10);
        model.output(input);
    }

    @Test
    public void importSepConvValue() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/agz/sep_conv_value.h5");
        INDArray input = Nd4j.create(32, 19, 19, 10);
        model.output(input);
    }

    @Test
    public void importSepResValue() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/agz/sep_res_value.h5");
        INDArray input = Nd4j.create(32, 19, 19, 10);
        model.output(input);
    }

    @Test
    public void importDualRes() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/agz/dual_res.h5");
        INDArray input = Nd4j.create(32, 19, 19, 10);
        model.output(input);
    }

    @Test
    public void importDualConv() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/agz/dual_conv.h5");
        INDArray input = Nd4j.create(32, 19, 19, 10);
        model.output(input);
    }

    /**
     * MTCNN
     */
    @Test
    public void importMTCNN() throws Exception {
        ComputationGraph model = importFunctionalModelH5Test("modelimport/keras/examples/48net_complete.h5");
    }

    /**
     * Masking layers (simple Masking into LSTM)
     */
    @Test
    public void testMaskingZeroValue() throws Exception {
        MultiLayerNetwork model = importSequentialModelH5Test("modelimport/keras/examples/masking/masking_zero_lstm.h5");
        model.summary();
    }

    @Test
    public void testMaskingTwoValue() throws Exception {
        MultiLayerNetwork model = importSequentialModelH5Test("modelimport/keras/examples/masking/masking_two_lstm.h5");
        model.summary();
    }
}

