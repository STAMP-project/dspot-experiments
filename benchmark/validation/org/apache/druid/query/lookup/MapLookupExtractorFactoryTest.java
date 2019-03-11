/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.lookup;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class MapLookupExtractorFactoryTest {
    private static final String KEY = "foo";

    private static final String VALUE = "bar";

    private static final MapLookupExtractorFactory factory = new MapLookupExtractorFactory(ImmutableMap.of(MapLookupExtractorFactoryTest.KEY, MapLookupExtractorFactoryTest.VALUE), true);

    @Test
    public void testSimpleExtraction() {
        Assert.assertEquals(MapLookupExtractorFactoryTest.factory.get().apply(MapLookupExtractorFactoryTest.KEY), MapLookupExtractorFactoryTest.VALUE);
        Assert.assertTrue(MapLookupExtractorFactoryTest.factory.get().isOneToOne());
    }

    @Test
    public void testReplaces() {
        Assert.assertFalse(MapLookupExtractorFactoryTest.factory.replaces(MapLookupExtractorFactoryTest.factory));
        Assert.assertFalse(MapLookupExtractorFactoryTest.factory.replaces(new MapLookupExtractorFactory(ImmutableMap.of(MapLookupExtractorFactoryTest.KEY, MapLookupExtractorFactoryTest.VALUE), true)));
        Assert.assertTrue(MapLookupExtractorFactoryTest.factory.replaces(new MapLookupExtractorFactory(ImmutableMap.of(MapLookupExtractorFactoryTest.KEY, MapLookupExtractorFactoryTest.VALUE), false)));
        Assert.assertTrue(MapLookupExtractorFactoryTest.factory.replaces(new MapLookupExtractorFactory(ImmutableMap.of(((MapLookupExtractorFactoryTest.KEY) + "1"), MapLookupExtractorFactoryTest.VALUE), true)));
        Assert.assertTrue(MapLookupExtractorFactoryTest.factory.replaces(new MapLookupExtractorFactory(ImmutableMap.of(MapLookupExtractorFactoryTest.KEY, ((MapLookupExtractorFactoryTest.VALUE) + "1")), true)));
        Assert.assertTrue(MapLookupExtractorFactoryTest.factory.replaces(null));
    }

    @Test
    public void testSerDeserMapLookupExtractorFactory() throws IOException {
        ObjectMapper mapper = new DefaultObjectMapper();
        mapper.registerSubtypes(MapLookupExtractorFactory.class);
        LookupExtractorFactory lookupExtractorFactory = new MapLookupExtractorFactory(ImmutableMap.of("key", "value"), true);
        Assert.assertEquals(lookupExtractorFactory, mapper.readerFor(LookupExtractorFactory.class).readValue(mapper.writeValueAsString(lookupExtractorFactory)));
    }
}

