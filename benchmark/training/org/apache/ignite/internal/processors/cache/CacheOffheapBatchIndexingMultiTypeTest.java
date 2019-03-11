/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache;


import org.junit.Test;


/**
 * Tests various cache operations with indexing enabled.
 * Cache contain multiple types.
 */
public class CacheOffheapBatchIndexingMultiTypeTest extends CacheOffheapBatchIndexingBaseTest {
    /**
     * Tests putAll with multiple indexed entities and streamer pre-loading with low off-heap cache size.
     */
    @Test
    public void testPutAllMultupleEntitiesAndStreamer() {
        doStreamerBatchTest(50, 1000, new Class<?>[]{ Integer.class, CacheOffheapBatchIndexingBaseTest.Person.class, Integer.class, CacheOffheapBatchIndexingBaseTest.Organization.class }, true);
    }

    /**
     * Tests putAll after with streamer batch load with one entity.
     */
    @Test
    public void testPuAllSingleEntity() {
        doStreamerBatchTest(50, 1000, new Class<?>[]{ Integer.class, CacheOffheapBatchIndexingBaseTest.Organization.class }, false);
    }
}

