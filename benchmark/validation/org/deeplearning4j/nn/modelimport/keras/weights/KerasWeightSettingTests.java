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
package org.deeplearning4j.nn.modelimport.keras.weights;


import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


@Slf4j
public class KerasWeightSettingTests {
    @Rule
    public final TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testSimpleLayersWithWeights() throws Exception {
        int[] kerasVersions = new int[]{ 1, 2 };
        String[] backends = new String[]{ "tensorflow", "theano" };
        for (int version : kerasVersions) {
            for (String backend : backends) {
                String densePath = ((("modelimport/keras/weights/dense_" + backend) + "_") + version) + ".h5";
                importDense(densePath);
                String conv2dPath = ((("modelimport/keras/weights/conv2d_" + backend) + "_") + version) + ".h5";
                importConv2D(conv2dPath);
                if ((version == 2) && (backend.equals("tensorflow"))) {
                    // TODO should work for theano
                    String conv2dReshapePath = ((("modelimport/keras/weights/conv2d_reshape_" + backend) + "_") + version) + ".h5";
                    System.out.println(((backend + "_") + version));
                    importConv2DReshape(conv2dReshapePath);
                }
                if (version == 2) {
                    String conv1dFlattenPath = ((("modelimport/keras/weights/embedding_conv1d_flatten_" + backend) + "_") + version) + ".h5";
                    importConv1DFlatten(conv1dFlattenPath);
                }
                String lstmPath = ((("modelimport/keras/weights/lstm_" + backend) + "_") + version) + ".h5";
                importLstm(lstmPath);
                String embeddingLstmPath = ((("modelimport/keras/weights/embedding_lstm_" + backend) + "_") + version) + ".h5";
                importEmbeddingLstm(embeddingLstmPath);
                if (version == 2) {
                    String embeddingConv1dExtendedPath = ((("modelimport/keras/weights/embedding_conv1d_extended_" + backend) + "_") + version) + ".h5";
                    importEmbeddingConv1DExtended(embeddingConv1dExtendedPath);
                }
                if (version == 2) {
                    String embeddingConv1dPath = ((("modelimport/keras/weights/embedding_conv1d_" + backend) + "_") + version) + ".h5";
                    importEmbeddingConv1D(embeddingConv1dPath);
                }
                String simpleRnnPath = ((("modelimport/keras/weights/simple_rnn_" + backend) + "_") + version) + ".h5";
                importSimpleRnn(simpleRnnPath);
                String bidirectionalLstmPath = ((("modelimport/keras/weights/bidirectional_lstm_" + backend) + "_") + version) + ".h5";
                importBidirectionalLstm(bidirectionalLstmPath);
                String bidirectionalLstmNoSequencesPath = ((("modelimport/keras/weights/bidirectional_lstm_no_return_sequences_" + backend) + "_") + version) + ".h5";
                importBidirectionalLstm(bidirectionalLstmNoSequencesPath);
                if ((version == 2) && (backend.equals("tensorflow"))) {
                    String batchToConv2dPath = ((("modelimport/keras/weights/batch_to_conv2d_" + backend) + "_") + version) + ".h5";
                    importBatchNormToConv2D(batchToConv2dPath);
                }
                if ((backend.equals("tensorflow")) && (version == 2)) {
                    // TODO should work for theano
                    String simpleSpaceToBatchPath = ((("modelimport/keras/weights/space_to_depth_simple_" + backend) + "_") + version) + ".h5";
                    importSimpleSpaceToDepth(simpleSpaceToBatchPath);
                }
                if ((backend.equals("tensorflow")) && (version == 2)) {
                    String graphSpaceToBatchPath = ((("modelimport/keras/weights/space_to_depth_graph_" + backend) + "_") + version) + ".h5";
                    importGraphSpaceToDepth(graphSpaceToBatchPath);
                }
                if ((backend.equals("tensorflow")) && (version == 2)) {
                    String sepConvPath = ((("modelimport/keras/weights/sepconv2d_" + backend) + "_") + version) + ".h5";
                    importSepConv2D(sepConvPath);
                }
            }
        }
    }
}

