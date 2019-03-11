/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.metadata;


import Schemas.DOC_SCHEMA_NAME;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class IndexPartsTest {
    @Test
    public void testParsing() {
        String table = "table";
        String schemaTable = "schema.table";
        String ident = "ident";
        String partitionedTable = ".partitioned.table." + ident;
        String schemaPartitionedTable = "schema..partitioned.table." + ident;
        MatcherAssert.assertThat(new IndexParts(table).getSchema(), Is.is(DOC_SCHEMA_NAME));
        MatcherAssert.assertThat(new IndexParts(schemaTable).getSchema(), Is.is("schema"));
        MatcherAssert.assertThat(new IndexParts(partitionedTable).getSchema(), Is.is(DOC_SCHEMA_NAME));
        MatcherAssert.assertThat(new IndexParts(schemaPartitionedTable).getSchema(), Is.is("schema"));
        MatcherAssert.assertThat(new IndexParts(table).getTable(), Is.is(table));
        MatcherAssert.assertThat(new IndexParts(schemaTable).getTable(), Is.is(table));
        MatcherAssert.assertThat(new IndexParts(partitionedTable).getTable(), Is.is(table));
        MatcherAssert.assertThat(new IndexParts(schemaPartitionedTable).getTable(), Is.is(table));
        MatcherAssert.assertThat(new IndexParts(table).isPartitioned(), Is.is(false));
        MatcherAssert.assertThat(new IndexParts(schemaTable).isPartitioned(), Is.is(false));
        MatcherAssert.assertThat(new IndexParts(partitionedTable).isPartitioned(), Is.is(true));
        MatcherAssert.assertThat(new IndexParts(schemaPartitionedTable).isPartitioned(), Is.is(true));
        try {
            new IndexParts("schema..partitioned.");
            Assert.fail("Should have failed due to invalid index name");
        } catch (IllegalArgumentException ignored) {
        }
        MatcherAssert.assertThat(IndexParts.isPartitioned(table), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isPartitioned(schemaTable), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isPartitioned(partitionedTable), Is.is(true));
        MatcherAssert.assertThat(IndexParts.isPartitioned(schemaPartitionedTable), Is.is(true));
        MatcherAssert.assertThat(IndexParts.isPartitioned("schema..partitioned."), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isPartitioned("schema.partitioned."), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isPartitioned("schema..partitioned.t"), Is.is(true));
        MatcherAssert.assertThat(new IndexParts(table).getPartitionIdent(), Is.is(""));
        MatcherAssert.assertThat(new IndexParts(schemaTable).getPartitionIdent(), Is.is(""));
        MatcherAssert.assertThat(new IndexParts(partitionedTable).getPartitionIdent(), Is.is(ident));
        MatcherAssert.assertThat(new IndexParts(schemaPartitionedTable).getPartitionIdent(), Is.is(ident));
        MatcherAssert.assertThat(IndexParts.isDangling(table), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isDangling(schemaTable), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isDangling(partitionedTable), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isDangling(schemaPartitionedTable), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isDangling("schema..partitioned."), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isDangling("schema.partitioned."), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isDangling("schema..partitioned.t"), Is.is(false));
        MatcherAssert.assertThat(IndexParts.isDangling(".shrinked.t"), Is.is(true));
        MatcherAssert.assertThat(IndexParts.isDangling(".shrinked.schema.t"), Is.is(true));
        MatcherAssert.assertThat(IndexParts.isDangling(".shrinked.partitioned.t.ident"), Is.is(true));
        MatcherAssert.assertThat(IndexParts.isDangling(".shrinked.schema..partitioned.t.ident"), Is.is(true));
    }
}

