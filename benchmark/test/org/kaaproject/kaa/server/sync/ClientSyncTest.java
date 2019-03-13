/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.sync;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ClientSyncTest {
    @Test
    public void isValidProfileHashExistsTest() {
        ClientSyncMetaData clientSyncMetaData = Mockito.mock(ClientSyncMetaData.class);
        Mockito.when(clientSyncMetaData.getProfileHash()).thenReturn(ByteBuffer.allocate(0));
        ClientSync clientSync = new ClientSync(0, clientSyncMetaData, null, null, null, null, null, null);
        Assert.assertEquals(clientSync.isValid(), true);
    }

    @Test
    public void isValidProfileHashNullRequestNullTest() {
        ClientSyncMetaData clientSyncMetaData = Mockito.mock(ClientSyncMetaData.class);
        Mockito.when(clientSyncMetaData.getProfileHash()).thenReturn(null);
        ClientSync clientSync = new ClientSync(0, clientSyncMetaData, null, null, null, null, null, null);
        Assert.assertEquals(clientSync.isValid(), false);
    }

    @Test
    public void isValidProfileHashNullEndpointKeyNullTest() {
        ClientSyncMetaData clientSyncMetaData = Mockito.mock(ClientSyncMetaData.class);
        ProfileClientSync profileClientSync = Mockito.mock(ProfileClientSync.class);
        Mockito.when(clientSyncMetaData.getProfileHash()).thenReturn(null);
        ClientSync clientSync = new ClientSync(0, clientSyncMetaData, profileClientSync, null, null, null, null, null);
        Assert.assertEquals(clientSync.isValid(), false);
        Assert.assertNotNull(new ClientSyncMetaData().toString());
    }
}

