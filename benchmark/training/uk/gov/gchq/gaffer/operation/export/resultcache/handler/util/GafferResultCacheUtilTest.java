/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.operation.export.resultcache.handler.util;


import GafferResultCacheUtil.DEFAULT_TIME_TO_LIVE;
import StreamUtil.STORE_PROPERTIES;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.CollectionUtil;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.store.schema.Schema;

import static GafferResultCacheUtil.DEFAULT_TIME_TO_LIVE;


public class GafferResultCacheUtilTest {
    private final Edge validEdge = new Edge.Builder().group("result").source("jobId").dest("exportId").directed(true).property("opAuths", CollectionUtil.treeSet("user01")).property("timestamp", System.currentTimeMillis()).property("visibility", "private").property("resultClass", String.class.getName()).property("result", "test".getBytes()).build();

    private final Edge oldEdge = new Edge.Builder().group("result").source("jobId").dest("exportId").directed(true).property("opAuths", CollectionUtil.treeSet("user01")).property("timestamp", (((System.currentTimeMillis()) - (DEFAULT_TIME_TO_LIVE)) - 1)).property("visibility", "private").property("resultClass", String.class.getName()).property("result", "test".getBytes()).build();

    @Test
    public void shouldThrowExceptionIfStorePropertiesAreNull() {
        // When / Then
        try {
            GafferResultCacheUtil.createGraph("graphId", null, DEFAULT_TIME_TO_LIVE);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldCreateGraphWithValidSchema() {
        // Given
        final Graph graph = GafferResultCacheUtil.createGraph("graphId", STORE_PROPERTIES, DEFAULT_TIME_TO_LIVE);
        final Schema schema = graph.getSchema();
        // When
        final boolean isValid = schema.validate().isValid();
        // Then
        Assert.assertTrue(isValid);
        Assert.assertEquals(DEFAULT_TIME_TO_LIVE, getAgeOffTime());
        Assert.assertTrue(new uk.gov.gchq.gaffer.store.ElementValidator(schema).validate(validEdge));
        Assert.assertFalse(new uk.gov.gchq.gaffer.store.ElementValidator(schema).validate(oldEdge));
    }

    @Test
    public void shouldCreateGraphWithValidSchemaWithoutAgeOff() {
        // Given
        final Graph graph = GafferResultCacheUtil.createGraph("graphId", STORE_PROPERTIES, null);
        final Schema schema = graph.getSchema();
        // When
        final boolean isValid = schema.validate().isValid();
        // Then
        Assert.assertTrue(isValid);
        Assert.assertNull(schema.getType("timestamp").getValidateFunctions());
        Assert.assertTrue(new uk.gov.gchq.gaffer.store.ElementValidator(schema).validate(validEdge));
        Assert.assertTrue(new uk.gov.gchq.gaffer.store.ElementValidator(schema).validate(oldEdge));
    }
}

