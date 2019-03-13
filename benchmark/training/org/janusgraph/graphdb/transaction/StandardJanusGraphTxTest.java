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
package org.janusgraph.graphdb.transaction;


import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StandardJanusGraphTxTest extends EasyMockSupport {
    @Test
    public void testGetOrCreatePropertyKey() {
        StandardJanusGraphTx tx = createTxWithMockedInternals();
        tx.getOrCreatePropertyKey("Foo", "Bar");
        Exception e = null;
        try {
            tx.getOrCreatePropertyKey("Baz", "Quuz");
        } catch (IllegalArgumentException ex) {
            e = ex;
        }
        tx.getOrCreatePropertyKey("Qux", "Quux");
        Assertions.assertNotNull(e, "getOrCreatePropertyKey should throw an Exception when the relationType is not a PropertyKey");
        verifyAll();
    }
}

