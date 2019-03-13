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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * The same as {@link TestVolume} except that this test is Ratis enabled.
 */
@Ignore("Disabling Ratis tests for pipeline work.")
@RunWith(Parameterized.class)
public class TestVolumeRatis {
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static ClientProtocol client;

    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    @Parameterized.Parameter
    @SuppressWarnings("visibilitymodifier")
    public Class clientProtocol;

    @Test
    public void testCreateVolume() throws Exception {
        TestVolume.runTestCreateVolume(TestVolumeRatis.client);
    }

    @Test
    public void testCreateDuplicateVolume() throws Exception {
        TestVolume.runTestCreateDuplicateVolume(TestVolumeRatis.client);
    }

    @Test
    public void testDeleteVolume() throws IOException, OzoneException {
        TestVolume.runTestDeleteVolume(TestVolumeRatis.client);
    }

    @Test
    public void testChangeOwnerOnVolume() throws Exception {
        TestVolume.runTestChangeOwnerOnVolume(TestVolumeRatis.client);
    }

    @Test
    public void testChangeQuotaOnVolume() throws Exception {
        TestVolume.runTestChangeQuotaOnVolume(TestVolumeRatis.client);
    }
}

