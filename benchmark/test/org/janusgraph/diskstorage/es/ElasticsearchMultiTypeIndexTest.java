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
package org.janusgraph.diskstorage.es;


import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.time.Instant;
import java.util.Arrays;
import org.janusgraph.core.attribute.Geoshape;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;


/**
 *
 *
 * @author David Clement (david.clement90@laposte.net)
 */
@EnabledIf("org.janusgraph.diskstorage.es.JanusGraphElasticsearchContainer.getEsMajorVersion().value < 6")
public class ElasticsearchMultiTypeIndexTest extends ElasticsearchIndexTest {
    @Test
    @Override
    public void clearStorageTest() throws Exception {
        final String store = "vertex";
        initialize(store);
        final Multimap<String, Object> doc1 = getDocument("Hello world", 1001, 5.2, Geoshape.point(48.0, 0.0), Geoshape.polygon(Arrays.asList(new double[][]{ new double[]{ -0.1, 47.9 }, new double[]{ 0.1, 47.9 }, new double[]{ 0.1, 48.1 }, new double[]{ -0.1, 48.1 }, new double[]{ -0.1, 47.9 } })), Arrays.asList("1", "2", "3"), Sets.newHashSet("1", "2"), Instant.ofEpochSecond(1));
        add(store, "doc1", doc1, true);
        clopen();
        Assertions.assertTrue(index.exists());
        tearDown();
        setUp();
        Assertions.assertFalse(index.exists());
    }
}

