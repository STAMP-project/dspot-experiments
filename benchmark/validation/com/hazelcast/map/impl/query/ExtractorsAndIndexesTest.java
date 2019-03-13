/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.map.impl.query;


import GroupProperty.PARTITION_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExtractorsAndIndexesTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void testExtractorsAreRespectedByEntriesReturnedFromIndexes() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = new Config();
        config.getMapConfig(mapName).setInMemoryFormat(inMemoryFormat).addMapIndexConfig(new MapIndexConfig("last", true)).addMapAttributeConfig(new MapAttributeConfig("generated", ExtractorsAndIndexesTest.Extractor.class.getName()));
        config.getNativeMemoryConfig().setEnabled(true);
        config.setProperty(PARTITION_COUNT.getName(), "1");
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, ExtractorsAndIndexesTest.Person> map = instance.getMap(mapName);
        populateMap(map);
        // this predicate queries the index
        Predicate lastPredicate = Predicates.equal("last", "last");
        // this predicate is not indexed and acts on the entries returned from
        // the index which must support extractors otherwise this test will fail
        Predicate alwaysFirst = Predicates.equal("generated", "first");
        Predicate composed = Predicates.and(lastPredicate, alwaysFirst);
        Collection<ExtractorsAndIndexesTest.Person> values = map.values(composed);
        Assert.assertEquals(100, values.size());
    }

    public static class Person implements Serializable {
        public String first;

        public String last;
    }

    public static class Extractor extends ValueExtractor<ExtractorsAndIndexesTest.Person, Void> {
        @SuppressWarnings("unchecked")
        @Override
        public void extract(ExtractorsAndIndexesTest.Person person, Void aVoid, ValueCollector valueCollector) {
            valueCollector.addObject("first");
        }
    }
}

