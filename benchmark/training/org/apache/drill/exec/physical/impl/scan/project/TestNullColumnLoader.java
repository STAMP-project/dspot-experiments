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
package org.apache.drill.exec.physical.impl.scan.project;


import DataMode.OPTIONAL;
import DataMode.REPEATED;
import DataMode.REQUIRED;
import MinorType.FLOAT8;
import MinorType.NULL;
import MinorType.VARCHAR;
import NullColumnLoader.DEFAULT_NULL_TYPE;
import java.util.ArrayList;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.physical.rowSet.ResultVectorCache;
import org.apache.drill.exec.physical.rowSet.impl.ResultVectorCacheImpl;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the mechanism that handles all-null columns during projection.
 * An all-null column is one projected in the query, but which does
 * not actually exist in the underlying data source (or input
 * operator.)
 * <p>
 * In anticipation of having type information, this mechanism
 * can create the classic nullable Int null column, or one of
 * any other type and mode.
 */
@Category(RowSetTests.class)
public class TestNullColumnLoader extends SubOperatorTest {
    /**
     * Test the simplest case: default null type, nothing in the vector
     * cache. Specify no column type, the special NULL type, or a
     * predefined type. Output types should be set accordingly.
     */
    @Test
    public void testBasics() {
        final List<ResolvedNullColumn> defns = new ArrayList<>();
        defns.add(makeNullCol("unspecified", null));
        defns.add(makeNullCol("nullType", Types.optional(NULL)));
        defns.add(makeNullCol("specifiedOpt", Types.optional(VARCHAR)));
        defns.add(makeNullCol("specifiedReq", Types.required(VARCHAR)));
        defns.add(makeNullCol("specifiedArray", Types.repeated(VARCHAR)));
        final ResultVectorCache cache = new org.apache.drill.exec.physical.rowSet.impl.NullResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        final NullColumnLoader staticLoader = new NullColumnLoader(cache, defns, null, false);
        // Create a batch
        final VectorContainer output = staticLoader.load(2);
        // Verify values and types
        final BatchSchema expectedSchema = new SchemaBuilder().add("unspecified", DEFAULT_NULL_TYPE).add("nullType", DEFAULT_NULL_TYPE).addNullable("specifiedOpt", VARCHAR).addNullable("specifiedReq", VARCHAR).addArray("specifiedArray", VARCHAR).build();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(null, null, null, null, new String[]{  }).addRow(null, null, null, null, new String[]{  }).build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(output));
        staticLoader.close();
    }

    /**
     * Test the ability to use a type other than nullable INT for null
     * columns. This occurs, for example, in the CSV reader where no
     * column is ever INT (nullable or otherwise) and we want our null
     * columns to be (non-nullable) VARCHAR.
     */
    @Test
    public void testCustomNullType() {
        final List<ResolvedNullColumn> defns = new ArrayList<>();
        defns.add(makeNullCol("unspecified", null));
        defns.add(makeNullCol("nullType", MajorType.newBuilder().setMinorType(NULL).setMode(OPTIONAL).build()));
        // Null required is an oxymoron, so is not tested.
        // Null type array does not make sense, so is not tested.
        final ResultVectorCache cache = new org.apache.drill.exec.physical.rowSet.impl.NullResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        final MajorType nullType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).build();
        final NullColumnLoader staticLoader = new NullColumnLoader(cache, defns, nullType, false);
        // Create a batch
        final VectorContainer output = staticLoader.load(2);
        // Verify values and types
        final BatchSchema expectedSchema = new SchemaBuilder().add("unspecified", nullType).add("nullType", nullType).build();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(null, null).addRow(null, null).build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(output));
        staticLoader.close();
    }

    /**
     * Drill requires "schema persistence": if a scan operator
     * reads two files, F1 and F2, then the scan operator must
     * provide the same vectors from both readers. Not just the
     * same types, the same value vector instances (but, of course,
     * populated with different data.)
     * <p>
     * Test the case in which the reader for F1 found columns
     * (a, b, c) but, F2 found only (a, b), requiring that we
     * fill in column c, filled with nulls, but of the same type that it
     * was in file F1. We use a vector cache to pull off this trick.
     * This test ensures that the null column mechanism looks in that
     * vector cache when asked to create a nullable column.
     */
    @Test
    public void testCachedTypesMapToNullable() {
        final List<ResolvedNullColumn> defns = new ArrayList<>();
        defns.add(makeNullCol("req"));
        defns.add(makeNullCol("opt"));
        defns.add(makeNullCol("rep"));
        defns.add(makeNullCol("unk"));
        // Populate the cache with a column of each mode.
        final ResultVectorCacheImpl cache = new ResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        cache.addOrGet(SchemaBuilder.columnSchema("req", FLOAT8, REQUIRED));
        final ValueVector opt = cache.addOrGet(SchemaBuilder.columnSchema("opt", FLOAT8, OPTIONAL));
        final ValueVector rep = cache.addOrGet(SchemaBuilder.columnSchema("rep", FLOAT8, REPEATED));
        // Use nullable Varchar for unknown null columns.
        final MajorType nullType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).build();
        final NullColumnLoader staticLoader = new NullColumnLoader(cache, defns, nullType, false);
        // Create a batch
        final VectorContainer output = staticLoader.load(2);
        // Verify vectors are reused
        Assert.assertSame(opt, output.getValueVector(1).getValueVector());
        Assert.assertSame(rep, output.getValueVector(2).getValueVector());
        // Verify values and types
        final BatchSchema expectedSchema = new SchemaBuilder().addNullable("req", FLOAT8).addNullable("opt", FLOAT8).addArray("rep", FLOAT8).addNullable("unk", VARCHAR).build();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(null, null, new int[]{  }, null).addRow(null, null, new int[]{  }, null).build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(output));
        staticLoader.close();
    }

    /**
     * Suppose, in the previous test, that one of the columns that
     * goes missing is a required column. The null-column mechanism can
     * create the "null" column as a required column, then fill it with
     * empty values (zero or "") -- if the scan operator feels doing so would
     * be helpful.
     */
    @Test
    public void testCachedTypesAllowRequired() {
        final List<ResolvedNullColumn> defns = new ArrayList<>();
        defns.add(makeNullCol("req"));
        defns.add(makeNullCol("opt"));
        defns.add(makeNullCol("rep"));
        defns.add(makeNullCol("unk"));
        // Populate the cache with a column of each mode.
        final ResultVectorCacheImpl cache = new ResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        cache.addOrGet(SchemaBuilder.columnSchema("req", FLOAT8, REQUIRED));
        final ValueVector opt = cache.addOrGet(SchemaBuilder.columnSchema("opt", FLOAT8, OPTIONAL));
        final ValueVector rep = cache.addOrGet(SchemaBuilder.columnSchema("rep", FLOAT8, REPEATED));
        // Use nullable Varchar for unknown null columns.
        final MajorType nullType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).build();
        final NullColumnLoader staticLoader = new NullColumnLoader(cache, defns, nullType, true);
        // Create a batch
        final VectorContainer output = staticLoader.load(2);
        // Verify vectors are reused
        Assert.assertSame(opt, output.getValueVector(1).getValueVector());
        Assert.assertSame(rep, output.getValueVector(2).getValueVector());
        // Verify values and types
        final BatchSchema expectedSchema = new SchemaBuilder().add("req", FLOAT8).addNullable("opt", FLOAT8).addArray("rep", FLOAT8).addNullable("unk", VARCHAR).build();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(0.0, null, new int[]{  }, null).addRow(0.0, null, new int[]{  }, null).build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(output));
        staticLoader.close();
    }

    /**
     * Test the shim class that adapts between the null column loader
     * and the projection mechanism. The projection mechanism uses this
     * to pull in the null columns which the null column loader has
     * created.
     */
    @Test
    public void testNullColumnBuilder() {
        final ResultVectorCache cache = new org.apache.drill.exec.physical.rowSet.impl.NullResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        builder.add("unspecified");
        builder.add("nullType", Types.optional(NULL));
        builder.add("specifiedOpt", Types.optional(VARCHAR));
        builder.add("specifiedReq", Types.required(VARCHAR));
        builder.add("specifiedArray", Types.repeated(VARCHAR));
        builder.build(cache);
        // Create a batch
        builder.load(2);
        // Verify values and types
        final BatchSchema expectedSchema = new SchemaBuilder().add("unspecified", DEFAULT_NULL_TYPE).add("nullType", DEFAULT_NULL_TYPE).addNullable("specifiedOpt", VARCHAR).addNullable("specifiedReq", VARCHAR).addArray("specifiedArray", VARCHAR).build();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(null, null, null, null, new String[]{  }).addRow(null, null, null, null, new String[]{  }).build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(builder.output()));
        builder.close();
    }
}

