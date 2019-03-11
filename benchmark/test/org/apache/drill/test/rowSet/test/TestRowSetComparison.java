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
package org.apache.drill.test.rowSet.test;


import TypeProtos.DataMode.REQUIRED;
import TypeProtos.MinorType.FLOAT4;
import TypeProtos.MinorType.FLOAT8;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(RowSetTests.class)
public class TestRowSetComparison {
    private BufferAllocator allocator;

    @Test
    public void simpleUnorderedComparisonMatchTest() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT, REQUIRED).add("b", INT, REQUIRED).buildSchema();
        final RowSet expected = new RowSetBuilder(allocator, schema).addRow(1, 1).addRow(1, 1).addRow(1, 2).addRow(2, 1).build();
        final RowSet actual = new RowSetBuilder(allocator, schema).addRow(1, 1).addRow(1, 2).addRow(2, 1).addRow(1, 1).build();
        try {
            new RowSetComparison(expected).unorderedVerify(actual);
        } finally {
            expected.clear();
            actual.clear();
        }
    }

    @Test
    public void simpleDoubleUnorderedComparisonMatchTest() {
        final TupleMetadata schema = new SchemaBuilder().add("a", FLOAT4, REQUIRED).add("b", FLOAT8, REQUIRED).buildSchema();
        final RowSet expected = new RowSetBuilder(allocator, schema).addRow(1.0F, 1.0).addRow(1.0F, 1.0).addRow(1.0F, 1.01).addRow(1.01F, 1.0).build();
        final RowSet actual = new RowSetBuilder(allocator, schema).addRow(1.004F, 0.9996).addRow(1.0F, 1.008).addRow(1.008F, 1.0).addRow(0.9996F, 1.004).build();
        try {
            new RowSetComparison(expected).unorderedVerify(actual);
        } finally {
            expected.clear();
            actual.clear();
        }
    }

    @Test
    public void simpleVarcharUnorderedComparisonMatchTest() {
        final TupleMetadata schema = new SchemaBuilder().add("a", VARCHAR, REQUIRED).buildSchema();
        final RowSet expected = new RowSetBuilder(allocator, schema).addRow("aaa").addRow("bbb").addRow("ccc").addRow("bbb").build();
        final RowSet actual = new RowSetBuilder(allocator, schema).addRow("ccc").addRow("aaa").addRow("bbb").addRow("bbb").build();
        try {
            new RowSetComparison(expected).unorderedVerify(actual);
        } finally {
            expected.clear();
            actual.clear();
        }
    }

    @Test(expected = AssertionError.class)
    public void simpleUnorderedComparisonNoMatchTest() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT, REQUIRED).add("b", INT, REQUIRED).buildSchema();
        final RowSet expected = new RowSetBuilder(allocator, schema).addRow(1, 1).addRow(3, 2).addRow(2, 4).build();
        final RowSet actual = new RowSetBuilder(allocator, schema).addRow(1, 1).addRow(2, 1).addRow(1, 1).build();
        try {
            new RowSetComparison(expected).unorderedVerify(actual);
        } finally {
            expected.clear();
            actual.clear();
        }
    }

    @Test(expected = AssertionError.class)
    public void simpleDoubleUnorderedComparisonNoMatchTest() {
        final TupleMetadata schema = new SchemaBuilder().add("a", FLOAT4, REQUIRED).add("b", FLOAT8, REQUIRED).buildSchema();
        final RowSet expected = new RowSetBuilder(allocator, schema).addRow(1.0F, 1.0).addRow(1.0F, 1.0).addRow(1.0F, 1.01).addRow(1.01F, 1.0).build();
        final RowSet actual = new RowSetBuilder(allocator, schema).addRow(1.009F, 0.9996).addRow(1.0F, 1.004).addRow(1.008F, 1.0).addRow(0.9994F, 1.004).build();
        try {
            new RowSetComparison(expected).unorderedVerify(actual);
        } finally {
            expected.clear();
            actual.clear();
        }
    }

    @Test(expected = AssertionError.class)
    public void simpleVarcharUnorderedComparisonNoMatchTest() {
        final TupleMetadata schema = new SchemaBuilder().add("a", VARCHAR, REQUIRED).buildSchema();
        final RowSet expected = new RowSetBuilder(allocator, schema).addRow("red").addRow("bbb").addRow("ccc").addRow("bbb").build();
        final RowSet actual = new RowSetBuilder(allocator, schema).addRow("ccc").addRow("aaa").addRow("blue").addRow("bbb").build();
        try {
            new RowSetComparison(expected).unorderedVerify(actual);
        } finally {
            expected.clear();
            actual.clear();
        }
    }
}

