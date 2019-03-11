/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb.tinkerpop.gremlin.server.auth;


import java.net.InetAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class SaslAndHMACAuthenticatorTest extends JanusGraphAbstractAuthenticatorTest {
    @Test
    public void testNewSaslNegotiator() {
        Assertions.assertThrows(RuntimeException.class, () -> new SaslAndHMACAuthenticator().newSaslNegotiator());
    }

    @Test
    public void testNewSaslNegotiatorInet() {
        final InetAddress inet = createMock(InetAddress.class);
        Assertions.assertThrows(RuntimeException.class, () -> new SaslAndHMACAuthenticator().newSaslNegotiator(inet));
    }

    @Test
    public void testAuthenticate() {
        Assertions.assertThrows(IllegalStateException.class, () -> new SaslAndHMACAuthenticator().authenticate(null));
    }
}

