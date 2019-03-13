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
package org.deeplearning4j.perf.listener;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import oshi.json.SystemInfo;


public class TestHardWareMetric {
    @Test
    public void testHardwareMetric() {
        HardwareMetric hardwareMetric = HardwareMetric.fromSystem(new SystemInfo());
        TestCase.assertNotNull(hardwareMetric);
        System.out.println(hardwareMetric);
        String yaml = hardwareMetric.toYaml();
        HardwareMetric fromYaml = HardwareMetric.fromYaml(yaml);
        Assert.assertEquals(hardwareMetric, fromYaml);
    }
}

