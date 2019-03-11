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


import com.google.common.collect.ImmutableList;
import org.junit.Test;


public class BigtableOptionsTest {
    private static final BigtableColumn COL1 = BigtableColumn.newBuilder().setQualifierEncoded("aaa").setFieldName("field1").setOnlyReadLatest(true).setEncoding("BINARY").setType("BYTES").build();

    private static final BigtableColumn COL2 = BigtableColumn.newBuilder().setQualifierEncoded("bbb").setFieldName("field2").setOnlyReadLatest(true).setEncoding("TEXT").setType("STRING").build();

    private static final BigtableColumnFamily TESTFAMILY = BigtableColumnFamily.newBuilder().setFamilyID("fooFamily").setEncoding("TEXT").setOnlyReadLatest(true).setType("INTEGER").setColumns(ImmutableList.of(BigtableOptionsTest.COL1, BigtableOptionsTest.COL2)).build();

    private static final BigtableOptions OPTIONS = BigtableOptions.newBuilder().setIgnoreUnspecifiedColumnFamilies(true).setReadRowkeyAsString(true).setColumnFamilies(ImmutableList.of(BigtableOptionsTest.TESTFAMILY)).build();

    @Test
    public void testConstructors() {
        // column
        assertThat(BigtableOptionsTest.COL1.getQualifierEncoded()).isEqualTo("aaa");
        assertThat(BigtableOptionsTest.COL1.getFieldName()).isEqualTo("field1");
        assertThat(BigtableOptionsTest.COL1.getOnlyReadLatest()).isEqualTo(true);
        assertThat(BigtableOptionsTest.COL1.getEncoding()).isEqualTo("BINARY");
        assertThat(BigtableOptionsTest.COL1.getType()).isEqualTo("BYTES");
        // family
        assertThat(BigtableOptionsTest.TESTFAMILY.getFamilyID()).isEqualTo("fooFamily");
        assertThat(BigtableOptionsTest.TESTFAMILY.getEncoding()).isEqualTo("TEXT");
        assertThat(BigtableOptionsTest.TESTFAMILY.getOnlyReadLatest()).isEqualTo(true);
        assertThat(BigtableOptionsTest.TESTFAMILY.getType()).isEqualTo("INTEGER");
        assertThat(BigtableOptionsTest.TESTFAMILY.getColumns()).isEqualTo(ImmutableList.of(BigtableOptionsTest.COL1, BigtableOptionsTest.COL2));
        // options
        assertThat(BigtableOptionsTest.OPTIONS.getIgnoreUnspecifiedColumnFamilies()).isEqualTo(true);
        assertThat(BigtableOptionsTest.OPTIONS.getReadRowkeyAsString()).isEqualTo(true);
        assertThat(BigtableOptionsTest.OPTIONS.getColumnFamilies()).isEqualTo(ImmutableList.of(BigtableOptionsTest.TESTFAMILY));
    }

    @Test
    public void testToAndFromPb() {
        compareBigtableColumn(BigtableOptionsTest.COL1, BigtableColumn.fromPb(BigtableOptionsTest.COL1.toPb()));
        compareBigtableColumnFamily(BigtableOptionsTest.TESTFAMILY, BigtableColumnFamily.fromPb(BigtableOptionsTest.TESTFAMILY.toPb()));
        compareBigtableOptions(BigtableOptionsTest.OPTIONS, BigtableOptions.fromPb(BigtableOptionsTest.OPTIONS.toPb()));
    }

    @Test
    public void testEquals() {
        compareBigtableColumn(BigtableOptionsTest.COL1, BigtableOptionsTest.COL1);
        compareBigtableColumnFamily(BigtableOptionsTest.TESTFAMILY, BigtableOptionsTest.TESTFAMILY);
        compareBigtableOptions(BigtableOptionsTest.OPTIONS, BigtableOptionsTest.OPTIONS);
    }
}

