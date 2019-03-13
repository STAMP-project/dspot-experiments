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


import junit.framework.TestCase;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 26/06/2016.
 */
public class TestUIDProvider extends BaseDL4JTest {
    @Test
    public void testUIDProvider() {
        String jvmUID = UIDProvider.getJVMUID();
        String hardwareUID = UIDProvider.getHardwareUID();
        Assert.assertNotNull(jvmUID);
        Assert.assertNotNull(hardwareUID);
        TestCase.assertTrue((!(jvmUID.isEmpty())));
        TestCase.assertTrue((!(hardwareUID.isEmpty())));
        Assert.assertEquals(jvmUID, UIDProvider.getJVMUID());
        Assert.assertEquals(hardwareUID, UIDProvider.getHardwareUID());
        System.out.println(("JVM uid:      " + jvmUID));
        System.out.println(("Hardware uid: " + hardwareUID));
    }
}

