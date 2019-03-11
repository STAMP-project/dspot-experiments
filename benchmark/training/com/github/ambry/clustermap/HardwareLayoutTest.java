/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.clustermap;


import HardwareState.AVAILABLE;
import HardwareState.UNAVAILABLE;
import com.github.ambry.config.VerifiableProperties;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link HardwareLayout} class.
 */
public class HardwareLayoutTest {
    private static final int diskCount = 10;

    private static final long diskCapacityInBytes = ((1000 * 1024) * 1024) * 1024L;

    private static final int dataNodeCount = 6;

    private static final int datacenterCount = 3;

    private static final int basePort = 6666;

    private static final int baseSslPort = 7666;

    private Properties props;

    public HardwareLayoutTest() {
        props = new Properties();
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
    }

    @Test
    public void basics() throws JSONException {
        JSONObject jsonObject = TestUtils.getJsonHardwareLayout("Alpha", getDatacenters());
        HardwareLayout hardwareLayout = new HardwareLayout(jsonObject, new com.github.ambry.config.ClusterMapConfig(new VerifiableProperties(props)));
        Assert.assertEquals(hardwareLayout.getVersion(), TestUtils.defaultHardwareLayoutVersion);
        Assert.assertEquals(hardwareLayout.getClusterName(), "Alpha");
        Assert.assertEquals(hardwareLayout.getDatacenters().size(), HardwareLayoutTest.datacenterCount);
        Assert.assertEquals(hardwareLayout.getRawCapacityInBytes(), ((((HardwareLayoutTest.datacenterCount) * (HardwareLayoutTest.dataNodeCount)) * (HardwareLayoutTest.diskCount)) * (HardwareLayoutTest.diskCapacityInBytes)));
        Assert.assertEquals(hardwareLayout.toJSONObject().toString(), jsonObject.toString());
        Assert.assertEquals(hardwareLayout.getDataNodeInHardStateCount(AVAILABLE), ((HardwareLayoutTest.datacenterCount) * (HardwareLayoutTest.dataNodeCount)));
        Assert.assertEquals(hardwareLayout.getDataNodeInHardStateCount(UNAVAILABLE), 0);
        Assert.assertEquals(hardwareLayout.calculateUnavailableDataNodeCount(), 0);
        Assert.assertEquals(hardwareLayout.getDiskInHardStateCount(AVAILABLE), (((HardwareLayoutTest.datacenterCount) * (HardwareLayoutTest.dataNodeCount)) * (HardwareLayoutTest.diskCount)));
        Assert.assertEquals(hardwareLayout.getDiskInHardStateCount(UNAVAILABLE), 0);
        Assert.assertEquals(hardwareLayout.calculateUnavailableDiskCount(), 0);
    }

    @Test
    public void validation() throws JSONException {
        JSONObject jsonObject;
        // Bad cluster name
        jsonObject = TestUtils.getJsonHardwareLayout("", getDatacenters());
        failValidation(jsonObject);
        // Duplicate disks
        jsonObject = TestUtils.getJsonHardwareLayout("Beta", getDatacentersWithDuplicateDisks());
        failValidation(jsonObject);
        // Duplicate data nodes
        jsonObject = TestUtils.getJsonHardwareLayout("Beta", getDatacentersWithDuplicateDataNodes());
        failValidation(jsonObject);
        // Duplicate datacenters
        jsonObject = TestUtils.getJsonHardwareLayout("Beta", getDuplicateDatacenters());
        failValidation(jsonObject);
    }
}

