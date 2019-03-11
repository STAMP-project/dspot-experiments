/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import ResultType.ENTITY;
import ResultType.KEY;
import ResultType.PROJECTION_ENTITY;
import StructuredQuery.KEY_PROPERTY_NAME;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StructuredQueryTest {
    private static final String NAMESPACE = "ns";

    private static final String KIND = "k";

    private static final Cursor START_CURSOR = Cursor.copyFrom(new byte[]{ 1, 2 });

    private static final Cursor END_CURSOR = Cursor.copyFrom(new byte[]{ 10 });

    private static final int OFFSET = 42;

    private static final Integer LIMIT = 43;

    private static final Filter FILTER = CompositeFilter.and(PropertyFilter.gt("p1", 10), PropertyFilter.eq("a", "v"));

    private static final OrderBy ORDER_BY_1 = OrderBy.asc("p2");

    private static final OrderBy ORDER_BY_2 = OrderBy.desc("p3");

    private static final List<OrderBy> ORDER_BY = ImmutableList.of(StructuredQueryTest.ORDER_BY_1, StructuredQueryTest.ORDER_BY_2);

    private static final String PROJECTION1 = "p4";

    private static final String PROJECTION2 = "p5";

    private static final List<String> PROJECTION = ImmutableList.of(StructuredQueryTest.PROJECTION1, StructuredQueryTest.PROJECTION2);

    private static final String DISTINCT_ON1 = "p6";

    private static final String DISTINCT_ON2 = "p7";

    private static final List<String> DISTINCT_ON = ImmutableList.of(StructuredQueryTest.DISTINCT_ON1, StructuredQueryTest.DISTINCT_ON2);

    private static final EntityQuery ENTITY_QUERY = Query.newEntityQueryBuilder().setNamespace(StructuredQueryTest.NAMESPACE).setKind(StructuredQueryTest.KIND).setStartCursor(StructuredQueryTest.START_CURSOR).setEndCursor(StructuredQueryTest.END_CURSOR).setOffset(StructuredQueryTest.OFFSET).setLimit(StructuredQueryTest.LIMIT).setFilter(StructuredQueryTest.FILTER).setOrderBy(StructuredQueryTest.ORDER_BY_1, StructuredQueryTest.ORDER_BY_2).build();

    private static final KeyQuery KEY_QUERY = Query.newKeyQueryBuilder().setNamespace(StructuredQueryTest.NAMESPACE).setKind(StructuredQueryTest.KIND).setStartCursor(StructuredQueryTest.START_CURSOR).setEndCursor(StructuredQueryTest.END_CURSOR).setOffset(StructuredQueryTest.OFFSET).setLimit(StructuredQueryTest.LIMIT).setFilter(StructuredQueryTest.FILTER).setOrderBy(StructuredQueryTest.ORDER_BY_1, StructuredQueryTest.ORDER_BY_2).build();

    private static final ProjectionEntityQuery PROJECTION_QUERY = Query.newProjectionEntityQueryBuilder().setNamespace(StructuredQueryTest.NAMESPACE).setKind(StructuredQueryTest.KIND).setStartCursor(StructuredQueryTest.START_CURSOR).setEndCursor(StructuredQueryTest.END_CURSOR).setOffset(StructuredQueryTest.OFFSET).setLimit(StructuredQueryTest.LIMIT).setFilter(StructuredQueryTest.FILTER).setOrderBy(StructuredQueryTest.ORDER_BY_1, StructuredQueryTest.ORDER_BY_2).setProjection(StructuredQueryTest.PROJECTION1, StructuredQueryTest.PROJECTION2).setDistinctOn(StructuredQueryTest.DISTINCT_ON1, StructuredQueryTest.DISTINCT_ON2).build();

    @Test
    public void testEntityQueryBuilder() {
        compareBaseBuilderFields(StructuredQueryTest.ENTITY_QUERY);
        Assert.assertTrue(StructuredQueryTest.ENTITY_QUERY.getProjection().isEmpty());
        Assert.assertTrue(StructuredQueryTest.ENTITY_QUERY.getDistinctOn().isEmpty());
    }

    @Test
    public void testKeyQueryBuilder() {
        compareBaseBuilderFields(StructuredQueryTest.KEY_QUERY);
        Assert.assertEquals(ImmutableList.of(KEY_PROPERTY_NAME), StructuredQueryTest.KEY_QUERY.getProjection());
        Assert.assertTrue(StructuredQueryTest.KEY_QUERY.getDistinctOn().isEmpty());
    }

    @Test
    public void testProjectionEntityQueryBuilder() {
        compareBaseBuilderFields(StructuredQueryTest.PROJECTION_QUERY);
        Assert.assertEquals(StructuredQueryTest.PROJECTION, StructuredQueryTest.PROJECTION_QUERY.getProjection());
        Assert.assertEquals(StructuredQueryTest.DISTINCT_ON, StructuredQueryTest.PROJECTION_QUERY.getDistinctOn());
    }

    @Test
    public void mergeFrom() {
        compareMergedQuery(StructuredQueryTest.ENTITY_QUERY, new EntityQuery.Builder().mergeFrom(StructuredQueryTest.ENTITY_QUERY.toPb()).build());
        compareMergedQuery(StructuredQueryTest.KEY_QUERY, new KeyQuery.Builder().mergeFrom(StructuredQueryTest.KEY_QUERY.toPb()).build());
        compareMergedQuery(StructuredQueryTest.PROJECTION_QUERY, new ProjectionEntityQuery.Builder().mergeFrom(StructuredQueryTest.PROJECTION_QUERY.toPb()).build());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(StructuredQueryTest.ENTITY_QUERY, StructuredQuery.fromPb(ENTITY, StructuredQueryTest.ENTITY_QUERY.getNamespace(), StructuredQueryTest.ENTITY_QUERY.toPb()));
        Assert.assertEquals(StructuredQueryTest.KEY_QUERY, StructuredQuery.fromPb(KEY, StructuredQueryTest.KEY_QUERY.getNamespace(), StructuredQueryTest.KEY_QUERY.toPb()));
        Assert.assertEquals(StructuredQueryTest.PROJECTION_QUERY, StructuredQuery.fromPb(PROJECTION_ENTITY, StructuredQueryTest.PROJECTION_QUERY.getNamespace(), StructuredQueryTest.PROJECTION_QUERY.toPb()));
    }

    @Test
    public void testToBuilder() {
        List<StructuredQuery<?>> queries = ImmutableList.<StructuredQuery<?>>of(StructuredQueryTest.ENTITY_QUERY, StructuredQueryTest.KEY_QUERY, StructuredQueryTest.PROJECTION_QUERY);
        for (StructuredQuery<?> query : queries) {
            Assert.assertEquals(query, query.toBuilder().build());
        }
    }

    @Test
    public void testKeyOnly() {
        Assert.assertTrue(StructuredQueryTest.KEY_QUERY.isKeyOnly());
        Assert.assertFalse(StructuredQueryTest.ENTITY_QUERY.isKeyOnly());
        Assert.assertFalse(StructuredQueryTest.PROJECTION_QUERY.isKeyOnly());
    }
}

