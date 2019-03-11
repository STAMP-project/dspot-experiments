/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.rest;


import org.elasticsearch.hadoop.rest.stats.Stats;
import org.elasticsearch.hadoop.serialization.JsonUtils;
import org.elasticsearch.hadoop.serialization.ScrollReader;
import org.elasticsearch.hadoop.util.BytesArray;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ScrollQueryTest {
    @Test
    public void test() throws Exception {
        RestRepository repository = mockRepository();
        ScrollReader scrollReader = Mockito.mock(ScrollReader.class);
        String query = "/index/type/_search?scroll=10m&etc=etc";
        BytesArray body = new BytesArray("{}");
        long size = 100;
        ScrollQuery scrollQuery = new ScrollQuery(repository, query, body, size, scrollReader);
        Assert.assertTrue(scrollQuery.hasNext());
        Assert.assertEquals("value", JsonUtils.query("field").apply(scrollQuery.next()[1]));
        Assert.assertFalse(scrollQuery.hasNext());
        scrollQuery.close();
        Stats stats = scrollQuery.stats();
        Assert.assertEquals(1, stats.docsReceived);
    }
}

