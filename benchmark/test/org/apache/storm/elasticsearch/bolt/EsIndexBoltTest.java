/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.elasticsearch.bolt;


import org.apache.storm.tuple.Tuple;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class EsIndexBoltTest extends AbstractEsBoltIntegrationTest<EsIndexBolt> {
    @Test
    public void testEsIndexBolt() throws Exception {
        Tuple tuple = createTestTuple(AbstractEsBoltTest.index, AbstractEsBoltTest.type);
        bolt.execute(tuple);
        Mockito.verify(outputCollector).ack(tuple);
        AbstractEsBoltIntegrationTest.node.client().admin().indices().prepareRefresh(AbstractEsBoltTest.index).execute().actionGet();
        SearchResponse resp = AbstractEsBoltIntegrationTest.node.client().prepareSearch(AbstractEsBoltTest.index).setQuery(new TermQueryBuilder("_type", AbstractEsBoltTest.type)).setSize(0).execute().actionGet();
        Assert.assertEquals(1, resp.getHits().getTotalHits());
    }

    @Test
    public void indexMissing() throws Exception {
        String index = "missing";
        Tuple tuple = createTestTuple(index, AbstractEsBoltTest.type);
        bolt.execute(tuple);
        Mockito.verify(outputCollector).ack(tuple);
        AbstractEsBoltIntegrationTest.node.client().admin().indices().prepareRefresh(index).execute().actionGet();
        SearchResponse resp = AbstractEsBoltIntegrationTest.node.client().prepareSearch(index).setQuery(new TermQueryBuilder("_type", AbstractEsBoltTest.type)).setSize(0).execute().actionGet();
        Assert.assertEquals(1, resp.getHits().getTotalHits());
    }
}

