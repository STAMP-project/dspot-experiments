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
package org.kaaproject.kaa.server.operations.service.akka.messages.core.endpoint;


import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;


public class EndpointAwareMessageTest {
    @Test
    public void endpointAwareMessageTest() {
        EndpointAwareMessage message1 = new EndpointAwareMessage("appToken1", EndpointObjectHash.fromSha1("1"), null);
        EndpointAwareMessage message2 = new EndpointAwareMessage("appToken1", EndpointObjectHash.fromSha1("1"), null);
        Assert.assertTrue(message1.equals(message1));
        Assert.assertEquals(message1.hashCode(), message1.hashCode());
        Assert.assertFalse(message1.equals(message2));
        Assert.assertNotEquals(message1.hashCode(), message2.hashCode());
        Assert.assertNotEquals(message1.getUuid(), message2.getUuid());
        UUID uuid = UUID.randomUUID();
        EndpointAwareMessage message3 = new EndpointAwareMessage(uuid, "appToken1", EndpointObjectHash.fromSha1("1"), null);
        EndpointAwareMessage message4 = new EndpointAwareMessage(uuid, "appToken1", EndpointObjectHash.fromSha1("1"), null);
        Assert.assertTrue(message3.equals(message4));
        Assert.assertEquals(message3.hashCode(), message4.hashCode());
        Assert.assertNotEquals(message1, new String(""));
        Assert.assertNotEquals(message1, new EndpointAwareMessage(null, "appToken1", EndpointObjectHash.fromSha1("1"), null));
    }
}

