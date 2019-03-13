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
package org.deeplearning4j.nn.modelimport.keras.optimizers;


import org.junit.Test;


public class OptimizerImport {
    @Test
    public void importAdam() throws Exception {
        importSequential("modelimport/keras/optimizers/adam.h5");
    }

    @Test
    public void importAdaMax() throws Exception {
        importSequential("modelimport/keras/optimizers/adamax.h5");
    }

    @Test
    public void importAdaGrad() throws Exception {
        importSequential("modelimport/keras/optimizers/adagrad.h5");
    }

    @Test
    public void importAdaDelta() throws Exception {
        importSequential("modelimport/keras/optimizers/adadelta.h5");
    }

    @Test
    public void importSGD() throws Exception {
        importSequential("modelimport/keras/optimizers/sgd.h5");
    }

    @Test
    public void importRmsProp() throws Exception {
        importSequential("modelimport/keras/optimizers/rmsprop.h5");
    }

    @Test
    public void importNadam() throws Exception {
        importSequential("modelimport/keras/optimizers/nadam.h5");
    }
}

