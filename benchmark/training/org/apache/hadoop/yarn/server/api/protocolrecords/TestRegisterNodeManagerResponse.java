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
package org.apache.hadoop.yarn.server.api.protocolrecords;


import NodeAction.NORMAL;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.junit.Assert;
import org.junit.Test;


public class TestRegisterNodeManagerResponse {
    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    @Test
    public void testRoundTrip() throws Exception {
        RegisterNodeManagerResponse resp = TestRegisterNodeManagerResponse.recordFactory.newRecordInstance(RegisterNodeManagerResponse.class);
        byte[] b = new byte[]{ 0, 1, 2, 3, 4, 5 };
        MasterKey containerTokenMK = TestRegisterNodeManagerResponse.recordFactory.newRecordInstance(MasterKey.class);
        containerTokenMK.setKeyId(54321);
        containerTokenMK.setBytes(ByteBuffer.wrap(b));
        resp.setContainerTokenMasterKey(containerTokenMK);
        MasterKey nmTokenMK = TestRegisterNodeManagerResponse.recordFactory.newRecordInstance(MasterKey.class);
        nmTokenMK.setKeyId(12345);
        nmTokenMK.setBytes(ByteBuffer.wrap(b));
        resp.setNMTokenMasterKey(nmTokenMK);
        resp.setNodeAction(NORMAL);
        Assert.assertEquals(NORMAL, resp.getNodeAction());
        // Verifying containerTokenMasterKey
        Assert.assertNotNull(resp.getContainerTokenMasterKey());
        Assert.assertEquals(54321, resp.getContainerTokenMasterKey().getKeyId());
        Assert.assertArrayEquals(b, resp.getContainerTokenMasterKey().getBytes().array());
        RegisterNodeManagerResponse respCopy = TestRegisterNodeManagerResponse.serDe(resp);
        Assert.assertEquals(NORMAL, respCopy.getNodeAction());
        Assert.assertNotNull(respCopy.getContainerTokenMasterKey());
        Assert.assertEquals(54321, respCopy.getContainerTokenMasterKey().getKeyId());
        Assert.assertArrayEquals(b, respCopy.getContainerTokenMasterKey().getBytes().array());
        // Verifying nmTokenMasterKey
        Assert.assertNotNull(resp.getNMTokenMasterKey());
        Assert.assertEquals(12345, resp.getNMTokenMasterKey().getKeyId());
        Assert.assertArrayEquals(b, resp.getNMTokenMasterKey().getBytes().array());
        respCopy = TestRegisterNodeManagerResponse.serDe(resp);
        Assert.assertEquals(NORMAL, respCopy.getNodeAction());
        Assert.assertNotNull(respCopy.getNMTokenMasterKey());
        Assert.assertEquals(12345, respCopy.getNMTokenMasterKey().getKeyId());
        Assert.assertArrayEquals(b, respCopy.getNMTokenMasterKey().getBytes().array());
    }
}

