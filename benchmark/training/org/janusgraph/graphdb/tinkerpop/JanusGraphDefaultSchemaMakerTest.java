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
package org.janusgraph.graphdb.tinkerpop;


import java.util.Date;
import java.util.UUID;
import org.easymock.EasyMockSupport;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.DefaultSchemaMaker;
import org.janusgraph.core.schema.PropertyKeyMaker;
import org.junit.jupiter.api.Test;

import static JanusGraphDefaultSchemaMaker.INSTANCE;


public class JanusGraphDefaultSchemaMakerTest extends EasyMockSupport {
    @Test
    public void testMakePropertyKey() {
        PropertyKeyMaker pkm = mockPropertyKeyMaker();
        DefaultSchemaMaker schemaMaker = INSTANCE;
        byte b = 100;
        short s = 10000;
        schemaMaker.makePropertyKey(pkm, "Foo");
        schemaMaker.makePropertyKey(pkm, 'f');
        schemaMaker.makePropertyKey(pkm, true);
        schemaMaker.makePropertyKey(pkm, b);
        schemaMaker.makePropertyKey(pkm, s);
        schemaMaker.makePropertyKey(pkm, 100);
        schemaMaker.makePropertyKey(pkm, 100L);
        schemaMaker.makePropertyKey(pkm, 100.0F);
        schemaMaker.makePropertyKey(pkm, 123.0);
        schemaMaker.makePropertyKey(pkm, new Date());
        schemaMaker.makePropertyKey(pkm, Geoshape.point(42.3601F, 71.0589F));
        schemaMaker.makePropertyKey(pkm, UUID.randomUUID());
        schemaMaker.makePropertyKey(pkm, new Object());
        verifyAll();
    }
}

