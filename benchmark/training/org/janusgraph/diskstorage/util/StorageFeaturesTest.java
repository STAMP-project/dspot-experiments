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
package org.janusgraph.diskstorage.util;


import org.janusgraph.diskstorage.keycolumnvalue.StandardStoreFeatures;
import org.janusgraph.diskstorage.keycolumnvalue.StoreFeatures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class StorageFeaturesTest {
    @Test
    public void testFeaturesImplementation() {
        StoreFeatures features;
        features = new StandardStoreFeatures.Builder().build();
        Assertions.assertFalse(features.hasMultiQuery());
        Assertions.assertFalse(features.hasLocking());
        Assertions.assertFalse(features.isDistributed());
        Assertions.assertFalse(features.hasScan());
        features = new StandardStoreFeatures.Builder().locking(true).build();
        Assertions.assertFalse(features.hasMultiQuery());
        Assertions.assertTrue(features.hasLocking());
        Assertions.assertFalse(features.isDistributed());
        features = new StandardStoreFeatures.Builder().multiQuery(true).unorderedScan(true).build();
        Assertions.assertTrue(features.hasMultiQuery());
        Assertions.assertTrue(features.hasUnorderedScan());
        Assertions.assertFalse(features.hasOrderedScan());
        Assertions.assertTrue(features.hasScan());
        Assertions.assertFalse(features.isDistributed());
        Assertions.assertFalse(features.hasLocking());
    }
}

