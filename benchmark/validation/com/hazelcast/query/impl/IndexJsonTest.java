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
package com.hazelcast.query.impl;


import Index.OperationSource.USER;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.json.HazelcastJson;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
public class IndexJsonTest {
    @Parameterized.Parameter(0)
    public IndexCopyBehavior copyBehavior;

    final InternalSerializationService ss = new DefaultSerializationServiceBuilder().build();

    @Test
    public void testJsonIndex() {
        InternalSerializationService ss = new DefaultSerializationServiceBuilder().build();
        Indexes is = Indexes.newBuilder(ss, copyBehavior).extractors(Extractors.newBuilder(ss).build()).indexProvider(new DefaultIndexProvider()).usesCachedQueryableEntries(true).statsEnabled(true).global(true).build();
        Index numberIndex = is.addOrGetIndex("age", false);
        Index boolIndex = is.addOrGetIndex("active", false);
        Index stringIndex = is.addOrGetIndex("name", false);
        for (int i = 0; i < 1001; i++) {
            Data key = ss.toData(i);
            String jsonString = ((("{\"age\" : " + i) + "  , \"name\" : \"sancar\" , \"active\" :  ") + ((i % 2) == 0)) + " } ";
            is.putEntry(new QueryEntry(ss, key, HazelcastJson.fromString(jsonString), Extractors.newBuilder(ss).build()), null, USER);
        }
        Assert.assertEquals(1, numberIndex.getRecords(10).size());
        Assert.assertEquals(0, numberIndex.getRecords((-1)).size());
        Assert.assertEquals(1001, stringIndex.getRecords("sancar").size());
        Assert.assertEquals(501, boolIndex.getRecords(true).size());
        Assert.assertEquals(501, is.query(new com.hazelcast.query.impl.predicates.AndPredicate(new EqualPredicate("name", "sancar"), new EqualPredicate("active", "true"))).size());
        Assert.assertEquals(300, is.query(Predicates.and(Predicates.greaterThan("age", 400), Predicates.equal("active", true))).size());
        Assert.assertEquals(1001, is.query(new SqlPredicate("name == sancar")).size());
    }
}

