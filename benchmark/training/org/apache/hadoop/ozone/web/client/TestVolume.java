/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.web.client;


import java.io.IOException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.protocol.ClientProtocol;
import org.apache.hadoop.ozone.client.rest.OzoneException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test Ozone Volumes Lifecycle.
 */
@RunWith(Parameterized.class)
public class TestVolume {
    private static MiniOzoneCluster cluster = null;

    private static ClientProtocol client = null;

    private static OzoneConfiguration conf;

    @SuppressWarnings("visibilitymodifier")
    @Parameterized.Parameter
    public Class clientProtocol;

    @Test
    public void testCreateVolume() throws Exception {
        TestVolume.runTestCreateVolume(TestVolume.client);
    }

    @Test
    public void testCreateDuplicateVolume() throws Exception {
        TestVolume.runTestCreateDuplicateVolume(TestVolume.client);
    }

    @Test
    public void testDeleteVolume() throws IOException, OzoneException {
        TestVolume.runTestDeleteVolume(TestVolume.client);
    }

    @Test
    public void testChangeOwnerOnVolume() throws Exception {
        TestVolume.runTestChangeOwnerOnVolume(TestVolume.client);
    }

    @Test
    public void testChangeQuotaOnVolume() throws Exception {
        TestVolume.runTestChangeQuotaOnVolume(TestVolume.client);
    }
}

