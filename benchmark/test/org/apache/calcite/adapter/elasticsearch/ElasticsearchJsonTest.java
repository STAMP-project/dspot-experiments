/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.elasticsearch;


import ElasticsearchJson.Aggregations;
import ElasticsearchJson.MultiBucketsAggregation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing correct parsing of JSON (elasticsearch) response.
 */
public class ElasticsearchJsonTest {
    private ObjectMapper mapper;

    @Test
    public void aggEmpty() throws Exception {
        String json = "{}";
        ElasticsearchJson.Aggregations a = mapper.readValue(json, Aggregations.class);
        Assert.assertNotNull(a);
        Assert.assertThat(a.asList().size(), CoreMatchers.is(0));
        Assert.assertThat(a.asMap().size(), CoreMatchers.is(0));
    }

    @Test
    public void aggSingle1() throws Exception {
        String json = "{agg1: {value: '111'}}";
        ElasticsearchJson.Aggregations a = mapper.readValue(json, Aggregations.class);
        Assert.assertNotNull(a);
        Assert.assertEquals(1, a.asList().size());
        Assert.assertEquals(1, a.asMap().size());
        Assert.assertEquals("agg1", a.asList().get(0).getName());
        Assert.assertEquals("agg1", a.asMap().keySet().iterator().next());
        Assert.assertEquals("111", value());
        List<Map<String, Object>> rows = new ArrayList<>();
        ElasticsearchJson.visitValueNodes(a, rows::add);
        Assert.assertThat(rows.size(), CoreMatchers.is(1));
        Assert.assertThat(rows.get(0).get("agg1"), CoreMatchers.is("111"));
    }

    @Test
    public void aggMultiValues() throws Exception {
        String json = "{ agg1: {min: 0, max: 2, avg: 2.33}}";
        ElasticsearchJson.Aggregations a = mapper.readValue(json, Aggregations.class);
        Assert.assertNotNull(a);
        Assert.assertEquals(1, a.asList().size());
        Assert.assertEquals(1, a.asMap().size());
        Assert.assertEquals("agg1", a.asList().get(0).getName());
        Map<String, Object> values = values();
        Assert.assertThat(values.keySet(), CoreMatchers.hasItems("min", "max", "avg"));
    }

    @Test
    public void aggSingle2() throws Exception {
        String json = "{ agg1: {value: 'foo'}, agg2: {value: 42}}";
        ElasticsearchJson.Aggregations a = mapper.readValue(json, Aggregations.class);
        Assert.assertNotNull(a);
        Assert.assertEquals(2, a.asList().size());
        Assert.assertEquals(2, a.asMap().size());
        Assert.assertThat(a.asMap().keySet(), CoreMatchers.hasItems("agg1", "agg2"));
    }

    @Test
    public void aggBuckets1() throws Exception {
        String json = "{ groupby: {buckets: [{key:'k1', doc_count:0, myagg:{value: 1.1}}," + " {key:'k2', myagg:{value: 2.2}}] }}";
        ElasticsearchJson.Aggregations a = mapper.readValue(json, Aggregations.class);
        Assert.assertThat(a.asMap().keySet(), IsCollectionContaining.hasItem("groupby"));
        Assert.assertThat(a.get("groupby"), CoreMatchers.instanceOf(MultiBucketsAggregation.class));
        ElasticsearchJson.MultiBucketsAggregation multi = a.get("groupby");
        Assert.assertThat(multi.buckets().size(), CoreMatchers.is(2));
        Assert.assertThat(multi.getName(), CoreMatchers.is("groupby"));
        Assert.assertThat(multi.buckets().get(0).key(), CoreMatchers.is("k1"));
        Assert.assertThat(multi.buckets().get(0).keyAsString(), CoreMatchers.is("k1"));
        Assert.assertThat(multi.buckets().get(1).key(), CoreMatchers.is("k2"));
        Assert.assertThat(multi.buckets().get(1).keyAsString(), CoreMatchers.is("k2"));
    }

    @Test
    public void aggManyAggregations() throws Exception {
        String json = "{groupby:{buckets:[" + (("{key:'k1', a1:{value:1}, a2:{value:2}}," + "{key:'k2', a1:{value:3}, a2:{value:4}}") + "]}}");
        ElasticsearchJson.Aggregations a = mapper.readValue(json, Aggregations.class);
        ElasticsearchJson.MultiBucketsAggregation multi = a.get("groupby");
        Assert.assertThat(multi.buckets().get(0).getAggregations().asMap().size(), CoreMatchers.is(2));
        Assert.assertThat(multi.buckets().get(0).getName(), CoreMatchers.is("groupby"));
        Assert.assertThat(multi.buckets().get(0).key(), CoreMatchers.is("k1"));
        Assert.assertThat(multi.buckets().get(0).getAggregations().asMap().keySet(), CoreMatchers.hasItems("a1", "a2"));
        Assert.assertThat(multi.buckets().get(1).getAggregations().asMap().size(), CoreMatchers.is(2));
        Assert.assertThat(multi.buckets().get(1).getName(), CoreMatchers.is("groupby"));
        Assert.assertThat(multi.buckets().get(1).key(), CoreMatchers.is("k2"));
        Assert.assertThat(multi.buckets().get(1).getAggregations().asMap().keySet(), CoreMatchers.hasItems("a1", "a2"));
        List<Map<String, Object>> rows = new ArrayList<>();
        ElasticsearchJson.visitValueNodes(a, rows::add);
        Assert.assertThat(rows.size(), CoreMatchers.is(2));
        Assert.assertThat(rows.get(0).get("groupby"), CoreMatchers.is("k1"));
        Assert.assertThat(rows.get(0).get("a1"), CoreMatchers.is(1));
        Assert.assertThat(rows.get(0).get("a2"), CoreMatchers.is(2));
    }

    @Test
    public void aggMultiBuckets() throws Exception {
        String json = "{col1: {buckets: [" + (("{col2: {doc_count:1, buckets:[{key:'k3', max:{value:41}}]}, key:'k1'}," + "{col2: {buckets:[{key:'k4', max:{value:42}}], doc_count:1}, key:'k2'}") + "]}}");
        ElasticsearchJson.Aggregations a = mapper.readValue(json, Aggregations.class);
        Assert.assertNotNull(a);
        Assert.assertThat(a.asMap().keySet(), IsCollectionContaining.hasItem("col1"));
        Assert.assertThat(a.get("col1"), CoreMatchers.instanceOf(MultiBucketsAggregation.class));
        ElasticsearchJson.MultiBucketsAggregation m = a.get("col1");
        Assert.assertThat(m.getName(), CoreMatchers.is("col1"));
        Assert.assertThat(m.buckets().size(), CoreMatchers.is(2));
        Assert.assertThat(m.buckets().get(0).key(), CoreMatchers.is("k1"));
        Assert.assertThat(m.buckets().get(0).getName(), CoreMatchers.is("col1"));
        Assert.assertThat(m.buckets().get(0).getAggregations().asMap().keySet(), IsCollectionContaining.hasItem("col2"));
        Assert.assertThat(m.buckets().get(1).key(), CoreMatchers.is("k2"));
        List<Map<String, Object>> rows = new ArrayList<>();
        ElasticsearchJson.visitValueNodes(a, rows::add);
        Assert.assertThat(rows.size(), CoreMatchers.is(2));
        Assert.assertThat(rows.get(0).keySet(), CoreMatchers.hasItems("col1", "col2", "max"));
        Assert.assertThat(rows.get(0).get("col1"), CoreMatchers.is("k1"));
        Assert.assertThat(rows.get(0).get("col2"), CoreMatchers.is("k3"));
        Assert.assertThat(rows.get(0).get("max"), CoreMatchers.is(41));
        Assert.assertThat(rows.get(1).keySet(), CoreMatchers.hasItems("col1", "col2", "max"));
        Assert.assertThat(rows.get(1).get("col1"), CoreMatchers.is("k2"));
        Assert.assertThat(rows.get(1).get("col2"), CoreMatchers.is("k4"));
        Assert.assertThat(rows.get(1).get("max"), CoreMatchers.is(42));
    }
}

/**
 * End ElasticsearchJsonTest.java
 */
