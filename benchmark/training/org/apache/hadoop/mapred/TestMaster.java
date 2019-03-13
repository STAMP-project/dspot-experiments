/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred;


import MRConfig.CLASSIC_FRAMEWORK_NAME;
import MRConfig.FRAMEWORK_NAME;
import MRConfig.MASTER_ADDRESS;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class TestMaster {
    @Test
    public void testGetMasterAddress() {
        YarnConfiguration conf = new YarnConfiguration();
        // Trying invalid master address for classic
        conf.set(FRAMEWORK_NAME, CLASSIC_FRAMEWORK_NAME);
        conf.set(MASTER_ADDRESS, "local:invalid");
        // should throw an exception for invalid value
        try {
            Master.getMasterAddress(conf);
            Assert.fail("Should not reach here as there is a bad master address");
        } catch (Exception e) {
            // Expected
        }
        // Change master address to a valid value
        conf.set(MASTER_ADDRESS, "bar.com:8042");
        String masterHostname = Master.getMasterAddress(conf);
        Assert.assertEquals(masterHostname, "bar.com");
    }
}

