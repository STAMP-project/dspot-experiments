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
package org.deeplearning4j.nn.transferlearning;


import Activation.ELU;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.learning.config.AdaGrad;


/**
 * Created by Alex on 27/03/2017.
 */
public class TestTransferLearningJson extends BaseDL4JTest {
    @Test
    public void testJsonYaml() {
        FineTuneConfiguration c = new FineTuneConfiguration.Builder().activation(ELU).updater(new AdaGrad(1.0)).biasUpdater(new AdaGrad(10.0)).build();
        String asJson = c.toJson();
        String asYaml = c.toYaml();
        FineTuneConfiguration fromJson = FineTuneConfiguration.fromJson(asJson);
        FineTuneConfiguration fromYaml = FineTuneConfiguration.fromYaml(asYaml);
        // System.out.println(asJson);
        Assert.assertEquals(c, fromJson);
        Assert.assertEquals(c, fromYaml);
        Assert.assertEquals(asJson, fromJson.toJson());
        Assert.assertEquals(asYaml, fromYaml.toYaml());
    }
}

