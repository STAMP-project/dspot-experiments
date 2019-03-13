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


import Activation.SOFTMAX;
import PretrainedType.IMAGENET;
import WeightInit.XAVIER;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.model.VGG16;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;


public class MiscTests extends BaseDL4JTest {
    @Test
    public void testTransferVGG() throws Exception {
        // https://github.com/deeplearning4j/deeplearning4j/issues/5167
        DataSet ds = new DataSet();
        ds.setFeatures(Nd4j.create(1, 3, 224, 224));
        ds.setLabels(Nd4j.create(1, 2));
        ComputationGraph model = ((ComputationGraph) (VGG16.builder().build().initPretrained(IMAGENET)));
        // System.out.println(model.summary());
        ComputationGraph transferModel = setFeatureExtractor("fc2").removeVertexKeepConnections("predictions").addLayer("predictions", nIn(4096).nOut(2).weightInit(XAVIER).activation(SOFTMAX).build(), "fc2").build();
        // System.out.println(transferModel.summary());
        // System.out.println("Fitting");
        transferModel.fit(ds);
        ComputationGraph g2 = TestUtils.testModelSerialization(transferModel);
        g2.fit(ds);
    }
}

