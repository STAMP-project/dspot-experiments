/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql.meta.provider.kafka;


import org.apache.beam.sdk.extensions.sql.BeamSqlTable;
import org.apache.beam.sdk.extensions.sql.meta.Table;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;


/**
 * UnitTest for {@link KafkaTableProvider}.
 */
public class KafkaTableProviderTest {
    private KafkaTableProvider provider = new KafkaTableProvider();

    @Test
    public void testBuildBeamSqlTable() throws Exception {
        Table table = KafkaTableProviderTest.mockTable("hello");
        BeamSqlTable sqlTable = provider.buildBeamSqlTable(table);
        Assert.assertNotNull(sqlTable);
        Assert.assertTrue((sqlTable instanceof BeamKafkaCSVTable));
        BeamKafkaCSVTable csvTable = ((BeamKafkaCSVTable) (sqlTable));
        Assert.assertEquals("localhost:9092", csvTable.getBootstrapServers());
        Assert.assertEquals(ImmutableList.of("topic1", "topic2"), csvTable.getTopics());
    }

    @Test
    public void testGetTableType() throws Exception {
        Assert.assertEquals("kafka", provider.getTableType());
    }
}

