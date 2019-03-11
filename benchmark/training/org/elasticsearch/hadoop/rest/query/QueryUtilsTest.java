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
package org.elasticsearch.hadoop.rest.query;


import org.elasticsearch.hadoop.cfg.Settings;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class QueryUtilsTest {
    private Settings cfg;

    @Test
    public void testTranslateSimpleUriQueryWithoutArgs() throws Exception {
        cfg.setQuery("?q=m*");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testTranslateSimpleUriQuery() throws Exception {
        cfg.setQuery("?q=foo");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testTranslateSimpleFieldUriQuery() throws Exception {
        cfg.setQuery("?q=foo:bar");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testTranslateUriQueryWithAnalyzer() throws Exception {
        cfg.setQuery("?q=foo:bar&analyzer=default");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testTranslateUriQueryWithDefaultField() throws Exception {
        cfg.setQuery("?q=foo:bar&df=name");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testTranslateSimpleUriQueryWithNoFilter() throws Exception {
        cfg.setQuery("?q=foo:bar");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testTranslateUriQueryWithBasicFilter() throws Exception {
        cfg.setQuery("?q=foo:bar");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testTranslateUriQueryWithDefaultFieldAndFilters() throws Exception {
        cfg.setQuery("?q=foo:bar&df=name");
        QueryBuilder query = QueryUtils.parseQuery(cfg);
        System.out.println(query);
    }

    @Test
    public void testIsExplicitlyRequested() throws Exception {
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test"), Matchers.is(false));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "test"), Matchers.is(true));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "nope"), Matchers.is(false));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "nope", "test"), Matchers.is(true));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "+test"), Matchers.is(true));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "-test"), Matchers.is(false));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "+te*"), Matchers.is(true));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "-te*"), Matchers.is(false));
        // If we're looking for all indices, it's an implicit requesting, not explicit
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "+*"), Matchers.is(false));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "-*"), Matchers.is(false));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "+_all"), Matchers.is(false));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "-_all"), Matchers.is(false));
        Assert.assertThat(QueryUtils.isExplicitlyRequested("test", "test"), Matchers.is(true));
    }
}

