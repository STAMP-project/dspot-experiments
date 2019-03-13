/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mllp;


import MllpComponent.DEFAULT_LOG_PHI;
import MllpComponent.DEFAULT_LOG_PHI_MAX_BYTES;
import MllpComponent.logPhi;
import MllpComponent.logPhiMaxBytes;
import org.junit.Assert;
import org.junit.Test;

import static MllpComponent.logPhi;
import static MllpComponent.logPhiMaxBytes;


/**
 * Tests for the  class.
 */
public class MllpComponentTest {
    Boolean initialLogPhiValue;

    Integer initialLogPhiMaxBytesValue;

    MllpComponent instance;

    @Test
    public void testHasLogPhi() throws Exception {
        logPhi = null;
        Assert.assertFalse(MllpComponent.hasLogPhi());
        logPhi = false;
        Assert.assertTrue(MllpComponent.hasLogPhi());
        logPhi = true;
        Assert.assertTrue(MllpComponent.hasLogPhi());
    }

    @Test
    public void testIsLogPhi() throws Exception {
        logPhi = null;
        Assert.assertEquals(DEFAULT_LOG_PHI, MllpComponent.isLogPhi());
        logPhi = false;
        Assert.assertFalse(MllpComponent.isLogPhi());
        logPhi = true;
        Assert.assertTrue(MllpComponent.isLogPhi());
    }

    @Test
    public void testSetLogPhi() throws Exception {
        MllpComponent.setLogPhi(null);
        Assert.assertNull(logPhi);
        MllpComponent.setLogPhi(true);
        Assert.assertEquals(Boolean.TRUE, logPhi);
        MllpComponent.setLogPhi(false);
        Assert.assertEquals(Boolean.FALSE, logPhi);
    }

    @Test
    public void testHasLogPhiMaxBytes() throws Exception {
        logPhiMaxBytes = null;
        Assert.assertFalse(MllpComponent.hasLogPhiMaxBytes());
        logPhiMaxBytes = -1;
        Assert.assertTrue(MllpComponent.hasLogPhiMaxBytes());
        logPhiMaxBytes = 1024;
        Assert.assertTrue(MllpComponent.hasLogPhiMaxBytes());
    }

    @Test
    public void testGetLogPhiMaxBytes() throws Exception {
        logPhiMaxBytes = null;
        Assert.assertEquals(DEFAULT_LOG_PHI_MAX_BYTES, MllpComponent.getLogPhiMaxBytes());
        int expected = -1;
        logPhiMaxBytes = expected;
        Assert.assertEquals(expected, MllpComponent.getLogPhiMaxBytes());
        expected = 1024;
        logPhiMaxBytes = expected;
        Assert.assertEquals(expected, MllpComponent.getLogPhiMaxBytes());
    }

    @Test
    public void testSetLogPhiMaxBytes() throws Exception {
        Integer expected = null;
        MllpComponent.setLogPhiMaxBytes(expected);
        Assert.assertEquals(expected, logPhiMaxBytes);
        expected = -1;
        MllpComponent.setLogPhiMaxBytes(expected);
        Assert.assertEquals(expected, logPhiMaxBytes);
        expected = 1024;
        MllpComponent.setLogPhiMaxBytes(expected);
        Assert.assertEquals(expected, logPhiMaxBytes);
    }
}

