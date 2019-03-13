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
package org.deeplearning4j.datasets;


import org.deeplearning4j.datasets.fetchers.Cifar10Fetcher;
import org.deeplearning4j.datasets.fetchers.TinyImageNetFetcher;
import org.junit.Test;


public class TestDataSets {
    @Test
    public void testTinyImageNetExists() throws Exception {
        // Simple sanity check on extracting
        TinyImageNetFetcher f = new TinyImageNetFetcher();
        f.downloadAndExtract();
        f.downloadAndExtract();
    }

    @Test
    public void testCifar10Exists() throws Exception {
        // Simple sanity check on extracting
        Cifar10Fetcher f = new Cifar10Fetcher();
        f.downloadAndExtract();
        f.downloadAndExtract();
    }
}

