/**
 * Copyright 2018 Google LLC
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
package com.google.cloud.bigquery;


import LegacySQLTypeName.INTEGER;
import com.google.api.gax.paging.Page;
import com.google.cloud.PageImpl;
import com.google.common.collect.ImmutableList;
import org.junit.Test;


public class TableResultTest {
    private static final Page<FieldValueList> INNER_PAGE_0 = new PageImpl(new PageImpl.NextPageFetcher<FieldValueList>() {
        @Override
        public Page<FieldValueList> getNextPage() {
            return com.google.cloud.bigquery.INNER_PAGE_1;
        }
    }, "abc", ImmutableList.of(TableResultTest.newFieldValueList("0"), TableResultTest.newFieldValueList("1")));

    private static final Page<FieldValueList> INNER_PAGE_1 = new PageImpl(new PageImpl.NextPageFetcher<FieldValueList>() {
        @Override
        public Page<FieldValueList> getNextPage() {
            return null;
        }
    }, null, ImmutableList.of(TableResultTest.newFieldValueList("2")));

    private static final Schema SCHEMA = Schema.of(Field.of("field", INTEGER));

    @Test
    public void testNullSchema() {
        TableResult result = new TableResult(null, 3, TableResultTest.INNER_PAGE_0);
        assertThat(result.getSchema()).isNull();
        assertThat(result.hasNextPage()).isTrue();
        assertThat(result.getNextPageToken()).isNotNull();
        assertThat(result.getValues()).containsExactly(TableResultTest.newFieldValueList("0"), TableResultTest.newFieldValueList("1")).inOrder();
        TableResult next = result.getNextPage();
        assertThat(next.getSchema()).isNull();
        assertThat(next.hasNextPage()).isFalse();
        assertThat(next.getNextPageToken()).isNull();
        assertThat(next.getValues()).containsExactly(TableResultTest.newFieldValueList("2"));
        assertThat(next.getNextPage()).isNull();
        assertThat(result.iterateAll()).containsExactly(TableResultTest.newFieldValueList("0"), TableResultTest.newFieldValueList("1"), TableResultTest.newFieldValueList("2")).inOrder();
    }

    @Test
    public void testSchema() {
        TableResult result = new TableResult(TableResultTest.SCHEMA, 3, TableResultTest.INNER_PAGE_0);
        assertThat(result.getSchema()).isEqualTo(TableResultTest.SCHEMA);
        assertThat(result.hasNextPage()).isTrue();
        assertThat(result.getNextPageToken()).isNotNull();
        assertThat(result.getValues()).containsExactly(TableResultTest.newFieldValueList("0").withSchema(TableResultTest.SCHEMA.getFields()), TableResultTest.newFieldValueList("1").withSchema(TableResultTest.SCHEMA.getFields())).inOrder();
        TableResult next = result.getNextPage();
        assertThat(next.getSchema()).isEqualTo(TableResultTest.SCHEMA);
        assertThat(next.hasNextPage()).isFalse();
        assertThat(next.getNextPageToken()).isNull();
        assertThat(next.getValues()).containsExactly(TableResultTest.newFieldValueList("2").withSchema(TableResultTest.SCHEMA.getFields()));
        assertThat(next.getNextPage()).isNull();
        assertThat(result.iterateAll()).containsExactly(TableResultTest.newFieldValueList("0").withSchema(TableResultTest.SCHEMA.getFields()), TableResultTest.newFieldValueList("1").withSchema(TableResultTest.SCHEMA.getFields()), TableResultTest.newFieldValueList("2").withSchema(TableResultTest.SCHEMA.getFields())).inOrder();
    }
}

