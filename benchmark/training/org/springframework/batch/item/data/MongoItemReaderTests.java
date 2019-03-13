/**
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.data;


import Sort.Direction;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;


public class MongoItemReaderTests {
    private MongoItemReader<String> reader;

    @Mock
    private MongoOperations template;

    private Map<String, Sort.Direction> sortOptions;

    @Test
    public void testAfterPropertiesSetForQueryString() throws Exception {
        reader = new MongoItemReader();
        try {
            reader.afterPropertiesSet();
            Assert.fail("Template was not set but exception was not thrown.");
        } catch (IllegalStateException iae) {
            Assert.assertEquals("An implementation of MongoOperations is required.", iae.getMessage());
        } catch (Throwable t) {
            Assert.fail("Wrong exception was thrown.");
        }
        reader.setTemplate(template);
        try {
            reader.afterPropertiesSet();
            Assert.fail("type was not set but exception was not thrown.");
        } catch (IllegalStateException iae) {
            Assert.assertEquals("A type to convert the input into is required.", iae.getMessage());
        } catch (Throwable t) {
            Assert.fail("Wrong exception was thrown.");
        }
        reader.setTargetType(String.class);
        try {
            reader.afterPropertiesSet();
            Assert.fail("Query was not set but exception was not thrown.");
        } catch (IllegalStateException iae) {
            Assert.assertEquals("A query is required.", iae.getMessage());
        } catch (Throwable t) {
            Assert.fail("Wrong exception was thrown.");
        }
        reader.setQuery("");
        try {
            reader.afterPropertiesSet();
            Assert.fail("Sort was not set but exception was not thrown.");
        } catch (IllegalStateException iae) {
            Assert.assertEquals("A sort is required.", iae.getMessage());
        } catch (Throwable t) {
            Assert.fail("Wrong exception was thrown.");
        }
        reader.setSort(sortOptions);
        reader.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSetForQueryObject() throws Exception {
        reader = new MongoItemReader();
        reader.setTemplate(template);
        reader.setTargetType(String.class);
        Query query1 = new Query().with(Sort.by(new org.springframework.data.domain.Sort.Order(Direction.ASC, "_id")));
        reader.setQuery(query1);
        reader.afterPropertiesSet();
    }

    @Test
    public void testBasicQueryFirstPage() {
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query query = queryContainer.getValue();
        Assert.assertEquals(50, query.getLimit());
        Assert.assertEquals(0, query.getSkip());
        Assert.assertEquals("{}", query.getQueryObject().toJson());
        Assert.assertEquals("{\"name\": -1}", query.getSortObject().toJson());
    }

    @Test
    public void testBasicQuerySecondPage() {
        reader.page = 2;
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query query = queryContainer.getValue();
        Assert.assertEquals(50, query.getLimit());
        Assert.assertEquals(100, query.getSkip());
        Assert.assertEquals("{}", query.getQueryObject().toJson());
        Assert.assertEquals("{\"name\": -1}", query.getSortObject().toJson());
        Assert.assertTrue(query.getFieldsObject().isEmpty());
    }

    @Test
    public void testQueryWithFields() {
        reader.setFields("{name : 1, age : 1, _id: 0}");
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query query = queryContainer.getValue();
        Assert.assertEquals(50, query.getLimit());
        Assert.assertEquals(0, query.getSkip());
        Assert.assertEquals("{}", query.getQueryObject().toJson());
        Assert.assertEquals("{\"name\": -1}", query.getSortObject().toJson());
        Assert.assertEquals(1, query.getFieldsObject().get("name"));
        Assert.assertEquals(1, query.getFieldsObject().get("age"));
        Assert.assertEquals(0, query.getFieldsObject().get("_id"));
    }

    @Test
    public void testQueryWithHint() {
        reader.setHint("{ $natural : 1}");
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query query = queryContainer.getValue();
        Assert.assertEquals(50, query.getLimit());
        Assert.assertEquals(0, query.getSkip());
        Assert.assertEquals("{}", query.getQueryObject().toJson());
        Assert.assertEquals("{\"name\": -1}", query.getSortObject().toJson());
        Assert.assertEquals("{ $natural : 1}", query.getHint());
    }

    @SuppressWarnings("serial")
    @Test
    public void testQueryWithParameters() {
        reader.setParameterValues(new ArrayList<Object>() {
            {
                add("foo");
            }
        });
        reader.setQuery("{ name : ?0 }");
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query query = queryContainer.getValue();
        Assert.assertEquals(50, query.getLimit());
        Assert.assertEquals(0, query.getSkip());
        Assert.assertEquals("{\"name\": \"foo\"}", query.getQueryObject().toJson());
        Assert.assertEquals("{\"name\": -1}", query.getSortObject().toJson());
    }

    @SuppressWarnings("serial")
    @Test
    public void testQueryWithCollection() {
        reader.setParameterValues(new ArrayList<Object>() {
            {
                add("foo");
            }
        });
        reader.setQuery("{ name : ?0 }");
        reader.setCollection("collection");
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<String> collectionContainer = ArgumentCaptor.forClass(String.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class), collectionContainer.capture())).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query query = queryContainer.getValue();
        Assert.assertEquals(50, query.getLimit());
        Assert.assertEquals(0, query.getSkip());
        Assert.assertEquals("{\"name\": \"foo\"}", query.getQueryObject().toJson());
        Assert.assertEquals("{\"name\": -1}", query.getSortObject().toJson());
        Assert.assertEquals("collection", collectionContainer.getValue());
    }

    @Test
    public void testQueryObject() throws Exception {
        reader = new MongoItemReader();
        reader.setTemplate(template);
        Query query = new Query().with(Sort.by(new org.springframework.data.domain.Sort.Order(Direction.ASC, "_id")));
        reader.setQuery(query);
        reader.setTargetType(String.class);
        reader.afterPropertiesSet();
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query actualQuery = queryContainer.getValue();
        Assert.assertFalse(reader.doPageRead().hasNext());
        Assert.assertEquals(10, actualQuery.getLimit());
        Assert.assertEquals(0, actualQuery.getSkip());
    }

    @Test
    public void testQueryObjectWithIgnoredPageSize() throws Exception {
        reader = new MongoItemReader();
        reader.setTemplate(template);
        Query query = new Query().with(Sort.by(new org.springframework.data.domain.Sort.Order(Direction.ASC, "_id"))).with(PageRequest.of(0, 50));
        reader.setQuery(query);
        reader.setTargetType(String.class);
        reader.afterPropertiesSet();
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query actualQuery = queryContainer.getValue();
        Assert.assertFalse(reader.doPageRead().hasNext());
        Assert.assertEquals(10, actualQuery.getLimit());
        Assert.assertEquals(0, actualQuery.getSkip());
    }

    @Test
    public void testQueryObjectWithPageSize() throws Exception {
        reader = new MongoItemReader();
        reader.setTemplate(template);
        Query query = new Query().with(Sort.by(new org.springframework.data.domain.Sort.Order(Direction.ASC, "_id"))).with(PageRequest.of(30, 50));
        reader.setQuery(query);
        reader.setTargetType(String.class);
        reader.setPageSize(100);
        reader.afterPropertiesSet();
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class))).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query actualQuery = queryContainer.getValue();
        Assert.assertFalse(reader.doPageRead().hasNext());
        Assert.assertEquals(100, actualQuery.getLimit());
        Assert.assertEquals(0, actualQuery.getSkip());
    }

    @Test
    public void testQueryObjectWithCollection() throws Exception {
        reader = new MongoItemReader();
        reader.setTemplate(template);
        Query query = new Query().with(Sort.by(new org.springframework.data.domain.Sort.Order(Direction.ASC, "_id")));
        reader.setQuery(query);
        reader.setTargetType(String.class);
        reader.setCollection("collection");
        reader.afterPropertiesSet();
        ArgumentCaptor<Query> queryContainer = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<String> stringContainer = ArgumentCaptor.forClass(String.class);
        Mockito.when(template.find(queryContainer.capture(), ArgumentMatchers.eq(String.class), stringContainer.capture())).thenReturn(new ArrayList());
        Assert.assertFalse(reader.doPageRead().hasNext());
        Query actualQuery = queryContainer.getValue();
        Assert.assertFalse(reader.doPageRead().hasNext());
        Assert.assertEquals(10, actualQuery.getLimit());
        Assert.assertEquals(0, actualQuery.getSkip());
        Assert.assertEquals("collection", stringContainer.getValue());
    }
}

