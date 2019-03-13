/**
 * Copyright 2017-present Open Networking Foundation
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.protocols.raft.session;


import io.atomix.primitive.PrimitiveId;
import io.atomix.primitive.session.SessionId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Raft session manager test.
 */
public class RaftSessionRegistryTest {
    @Test
    public void testAddRemoveSession() throws Exception {
        RaftSessionRegistry sessionManager = new RaftSessionRegistry();
        RaftSession session = createSession(1);
        sessionManager.addSession(session);
        Assert.assertNotNull(sessionManager.getSession(1));
        Assert.assertNotNull(sessionManager.getSession(session.sessionId()));
        Assert.assertEquals(0, sessionManager.getSessions(PrimitiveId.from(1)).size());
        session.open();
        Assert.assertEquals(1, sessionManager.getSessions(PrimitiveId.from(1)).size());
        sessionManager.removeSession(SessionId.from(1));
        Assert.assertNull(sessionManager.getSession(1));
    }
}

