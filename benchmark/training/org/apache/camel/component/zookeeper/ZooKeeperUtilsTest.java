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
package org.apache.camel.component.zookeeper;


import CreateMode.EPHEMERAL;
import CreateMode.EPHEMERAL_SEQUENTIAL;
import CreateMode.PERSISTENT;
import CreateMode.PERSISTENT_SEQUENTIAL;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


public class ZooKeeperUtilsTest {
    private CamelContext camelContext = new DefaultCamelContext();

    @Test
    public void testCreateModeExtraction() {
        Assert.assertEquals(EPHEMERAL, ZooKeeperUtils.getCreateModeFromString("EPHEMERAL", EPHEMERAL));
        Assert.assertEquals(EPHEMERAL_SEQUENTIAL, ZooKeeperUtils.getCreateModeFromString("EPHEMERAL_SEQUENTIAL", EPHEMERAL));
        Assert.assertEquals(PERSISTENT, ZooKeeperUtils.getCreateModeFromString("PERSISTENT", EPHEMERAL));
        Assert.assertEquals(PERSISTENT_SEQUENTIAL, ZooKeeperUtils.getCreateModeFromString("PERSISTENT_SEQUENTIAL", EPHEMERAL));
        Assert.assertEquals(EPHEMERAL, ZooKeeperUtils.getCreateModeFromString("DOESNOTEXIST", EPHEMERAL));
    }

    @Test
    public void testCreateModeExtractionFromMessageHeader() {
        Assert.assertEquals(EPHEMERAL, testModeInMessage("EPHEMERAL", EPHEMERAL));
        Assert.assertEquals(EPHEMERAL_SEQUENTIAL, testModeInMessage("EPHEMERAL_SEQUENTIAL", EPHEMERAL));
        Assert.assertEquals(PERSISTENT, testModeInMessage("PERSISTENT", EPHEMERAL));
        Assert.assertEquals(PERSISTENT_SEQUENTIAL, testModeInMessage("PERSISTENT_SEQUENTIAL", EPHEMERAL));
        Assert.assertEquals(EPHEMERAL, testModeInMessage("DOESNOTEXIST", EPHEMERAL));
    }
}

