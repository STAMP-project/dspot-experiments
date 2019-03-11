/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.om;


import java.io.IOException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.web.handlers.UserArgs;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test Ozone Manager Init.
 */
public class TestOmInit {
    private static MiniOzoneCluster cluster = null;

    private static StorageHandler storageHandler;

    private static UserArgs userArgs;

    private static OMMetrics omMetrics;

    private static OzoneConfiguration conf;

    private static String clusterId;

    private static String scmId;

    private static String omId;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Tests the OM Initialization.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testOmInitAgain() throws IOException {
        // Stop the Ozone Manager
        TestOmInit.cluster.getOzoneManager().stop();
        // Now try to init the OM again. It should succeed
        Assert.assertTrue(OzoneManager.omInit(TestOmInit.conf));
    }
}

