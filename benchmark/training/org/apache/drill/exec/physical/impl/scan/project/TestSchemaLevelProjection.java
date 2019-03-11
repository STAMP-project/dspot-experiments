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


import MinorType.BIGINT;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.VARCHAR;
import ResolvedTableColumn.ID;
import UnresolvedColumn.UNRESOLVED;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.physical.impl.scan.ScanTestUtils;
import org.apache.drill.exec.physical.impl.scan.project.ResolvedTuple.ResolvedRow;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * "Schema level projection" describes one side of the projection
 * mechanism. When we project, we have the set of column the user
 * wants "the schema level" and the set of columns on offer from
 * the data source "the scan level." The projection mechanism
 * combines these to map out the actual projection.
 */
@Category(RowSetTests.class)
public class TestSchemaLevelProjection extends SubOperatorTest {
    /**
     * Test wildcard projection: take all columns on offer from
     * the data source, in the order that the data source specifies.
     */
    @Test
    public void testWildcard() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        Assert.assertEquals(1, scanProj.columns().size());
        final TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).addNullable("c", INT).addArray("d", FLOAT8).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new WildcardSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(3, columns.size());
        Assert.assertEquals("a", columns.get(0).name());
        Assert.assertEquals(0, columns.get(0).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(0).source());
        Assert.assertEquals("c", columns.get(1).name());
        Assert.assertEquals(1, columns.get(1).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(1).source());
        Assert.assertEquals("d", columns.get(2).name());
        Assert.assertEquals(2, columns.get(2).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(2).source());
    }

    /**
     * Test SELECT list with columns defined in a order and with
     * name case different than the early-schema table.
     */
    @Test
    public void testFullList() {
        // Simulate SELECT c, b, a ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("c", "b", "a"), ScanTestUtils.parsers());
        Assert.assertEquals(3, scanProj.columns().size());
        // Simulate a data source, with early schema, of (a, b, c)
        final TupleMetadata tableSchema = new SchemaBuilder().add("A", VARCHAR).add("B", VARCHAR).add("C", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(3, columns.size());
        Assert.assertEquals("c", columns.get(0).name());
        Assert.assertEquals(2, columns.get(0).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(0).source());
        Assert.assertEquals("b", columns.get(1).name());
        Assert.assertEquals(1, columns.get(1).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(1).source());
        Assert.assertEquals("a", columns.get(2).name());
        Assert.assertEquals(0, columns.get(2).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(2).source());
    }

    /**
     * Test SELECT list with columns missing from the table schema.
     */
    @Test
    public void testMissing() {
        // Simulate SELECT c, v, b, w ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("c", "v", "b", "w"), ScanTestUtils.parsers());
        Assert.assertEquals(4, scanProj.columns().size());
        // Simulate a data source, with early schema, of (a, b, c)
        final TupleMetadata tableSchema = new SchemaBuilder().add("A", VARCHAR).add("B", VARCHAR).add("C", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(4, columns.size());
        final VectorSource nullBuilder = rootTuple.nullBuilder();
        Assert.assertEquals("c", columns.get(0).name());
        Assert.assertEquals(2, columns.get(0).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(0).source());
        Assert.assertEquals("v", columns.get(1).name());
        Assert.assertEquals(0, columns.get(1).sourceIndex());
        Assert.assertSame(nullBuilder, columns.get(1).source());
        Assert.assertEquals("b", columns.get(2).name());
        Assert.assertEquals(1, columns.get(2).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(2).source());
        Assert.assertEquals("w", columns.get(3).name());
        Assert.assertEquals(1, columns.get(3).sourceIndex());
        Assert.assertSame(nullBuilder, columns.get(3).source());
    }

    /**
     * Test an explicit projection (providing columns) in which the
     * names in the project lists are a different case than the data
     * source, the order of columns differs, and we ask for a
     * subset of data source columns.
     */
    @Test
    public void testSubset() {
        // Simulate SELECT c, a ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("c", "a"), ScanTestUtils.parsers());
        Assert.assertEquals(2, scanProj.columns().size());
        // Simulate a data source, with early schema, of (a, b, c)
        final TupleMetadata tableSchema = new SchemaBuilder().add("A", VARCHAR).add("B", VARCHAR).add("C", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(2, columns.size());
        Assert.assertEquals("c", columns.get(0).name());
        Assert.assertEquals(2, columns.get(0).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(0).source());
        Assert.assertEquals("a", columns.get(1).name());
        Assert.assertEquals(0, columns.get(1).sourceIndex());
        Assert.assertSame(rootTuple, columns.get(1).source());
    }

    /**
     * Drill is unique in that we can select (a, b) from a data source
     * that only offers (c, d). We get null columns as a result.
     */
    @Test
    public void testDisjoint() {
        // Simulate SELECT c, a ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("b"), ScanTestUtils.parsers());
        Assert.assertEquals(1, scanProj.columns().size());
        // Simulate a data source, with early schema, of (a)
        final TupleMetadata tableSchema = new SchemaBuilder().add("A", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(1, columns.size());
        final VectorSource nullBuilder = rootTuple.nullBuilder();
        Assert.assertEquals("b", columns.get(0).name());
        Assert.assertEquals(0, columns.get(0).sourceIndex());
        Assert.assertSame(nullBuilder, columns.get(0).source());
    }

    /**
     * Test the obscure case that the data source contains a map, but we
     * project only one of the members of the map. The output should be a
     * map that contains only the members we request.
     */
    @Test
    public void testOmittedMap() {
        // Simulate SELECT a, b.c.d ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a", "b.c.d"), ScanTestUtils.parsers());
        Assert.assertEquals(2, scanProj.columns().size());
        {
            Assert.assertEquals(UNRESOLVED, scanProj.columns().get(1).nodeType());
            final UnresolvedColumn bCol = ((UnresolvedColumn) (scanProj.columns().get(1)));
            Assert.assertTrue(bCol.element().isTuple());
        }
        // Simulate a data source, with early schema, of (a)
        final TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(2, columns.size());
        // Should have resolved a to a table column, b to a missing map.
        // A is projected
        final ResolvedColumn aCol = columns.get(0);
        Assert.assertEquals("a", aCol.name());
        Assert.assertEquals(ID, aCol.nodeType());
        // B is not projected, is implicitly a map
        final ResolvedColumn bCol = columns.get(1);
        Assert.assertEquals("b", bCol.name());
        Assert.assertEquals(ResolvedMapColumn.ID, bCol.nodeType());
        final ResolvedMapColumn bMap = ((ResolvedMapColumn) (bCol));
        final ResolvedTuple bMembers = bMap.members();
        Assert.assertNotNull(bMembers);
        Assert.assertEquals(1, bMembers.columns().size());
        // C is a map within b
        final ResolvedColumn cCol = bMembers.columns().get(0);
        Assert.assertEquals(ResolvedMapColumn.ID, cCol.nodeType());
        final ResolvedMapColumn cMap = ((ResolvedMapColumn) (cCol));
        final ResolvedTuple cMembers = cMap.members();
        Assert.assertNotNull(cMembers);
        Assert.assertEquals(1, cMembers.columns().size());
        // D is an unknown column type (not a map)
        final ResolvedColumn dCol = cMembers.columns().get(0);
        Assert.assertEquals(ResolvedNullColumn.ID, dCol.nodeType());
    }

    /**
     * Test of a map with missing columns.
     * table of (a{b, c}), project a.c, a.d, a.e.f
     */
    @Test
    public void testOmittedMapMembers() {
        // Simulate SELECT a.c, a.d, a.e.f ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("x", "a.c", "a.d", "a.e.f", "y"), ScanTestUtils.parsers());
        Assert.assertEquals(3, scanProj.columns().size());
        // Simulate a data source, with early schema, of (x, y, a{b, c})
        final TupleMetadata tableSchema = new SchemaBuilder().add("x", VARCHAR).add("y", INT).addMap("a").add("b", BIGINT).add("c", FLOAT8).resumeSchema().buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(3, columns.size());
        // Should have resolved a.b to a map column,
        // a.d to a missing nested map, and a.e.f to a missing
        // nested map member
        // X is projected
        final ResolvedColumn xCol = columns.get(0);
        Assert.assertEquals("x", xCol.name());
        Assert.assertEquals(ID, xCol.nodeType());
        Assert.assertSame(rootTuple, xCol.source());
        Assert.assertEquals(0, xCol.sourceIndex());
        // Y is projected
        final ResolvedColumn yCol = columns.get(2);
        Assert.assertEquals("y", yCol.name());
        Assert.assertEquals(ID, yCol.nodeType());
        Assert.assertSame(rootTuple, yCol.source());
        Assert.assertEquals(1, yCol.sourceIndex());
        // A is projected
        final ResolvedColumn aCol = columns.get(1);
        Assert.assertEquals("a", aCol.name());
        Assert.assertEquals(ResolvedMapColumn.ID, aCol.nodeType());
        final ResolvedMapColumn aMap = ((ResolvedMapColumn) (aCol));
        final ResolvedTuple aMembers = aMap.members();
        Assert.assertFalse(aMembers.isSimpleProjection());
        Assert.assertNotNull(aMembers);
        Assert.assertEquals(3, aMembers.columns().size());
        // a.c is projected
        final ResolvedColumn acCol = aMembers.columns().get(0);
        Assert.assertEquals("c", acCol.name());
        Assert.assertEquals(ID, acCol.nodeType());
        Assert.assertEquals(1, acCol.sourceIndex());
        // a.d is not in the table, is null
        final ResolvedColumn adCol = aMembers.columns().get(1);
        Assert.assertEquals("d", adCol.name());
        Assert.assertEquals(ResolvedNullColumn.ID, adCol.nodeType());
        // a.e is not in the table, is implicitly a map
        final ResolvedColumn aeCol = aMembers.columns().get(2);
        Assert.assertEquals("e", aeCol.name());
        Assert.assertEquals(ResolvedMapColumn.ID, aeCol.nodeType());
        final ResolvedMapColumn aeMap = ((ResolvedMapColumn) (aeCol));
        final ResolvedTuple aeMembers = aeMap.members();
        Assert.assertFalse(aeMembers.isSimpleProjection());
        Assert.assertNotNull(aeMembers);
        Assert.assertEquals(1, aeMembers.columns().size());
        // a.d.f is a null column
        final ResolvedColumn aefCol = aeMembers.columns().get(0);
        Assert.assertEquals("f", aefCol.name());
        Assert.assertEquals(ResolvedNullColumn.ID, aefCol.nodeType());
    }

    /**
     * Simple map project. This is an internal case in which the
     * query asks for a set of columns inside a map, and the table
     * loader produces exactly that set. No special projection is
     * needed, the map is projected as a whole.
     */
    @Test
    public void testSimpleMapProject() {
        // Simulate SELECT a.b, a.c ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a.b", "a.c"), ScanTestUtils.parsers());
        Assert.assertEquals(1, scanProj.columns().size());
        // Simulate a data source, with early schema, of (a{b, c})
        final TupleMetadata tableSchema = new SchemaBuilder().addMap("a").add("b", BIGINT).add("c", FLOAT8).resumeSchema().buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(1, columns.size());
        // Should have resolved a.b to a map column,
        // a.d to a missing nested map, and a.e.f to a missing
        // nested map member
        // a is projected as a vector, not as a structured map
        final ResolvedColumn aCol = columns.get(0);
        Assert.assertEquals("a", aCol.name());
        Assert.assertEquals(ID, aCol.nodeType());
        Assert.assertSame(rootTuple, aCol.source());
        Assert.assertEquals(0, aCol.sourceIndex());
    }

    /**
     * Project of a non-map as a map
     */
    @Test
    public void testMapMismatch() {
        // Simulate SELECT a.b ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a.b"), ScanTestUtils.parsers());
        // Simulate a data source, with early schema, of (a)
        // where a is not a map.
        final TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        try {
            new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
            Assert.fail();
        } catch (final UserException e) {
            // Expected
        }
    }

    /**
     * Test project of an array. At the scan level, we just verify
     * that the requested column is, indeed, an array.
     */
    @Test
    public void testArrayProject() {
        // Simulate SELECT a[0] ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a[0]"), ScanTestUtils.parsers());
        // Simulate a data source, with early schema, of (a)
        // where a is not an array.
        final TupleMetadata tableSchema = new SchemaBuilder().addArray("a", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
        final List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(1, columns.size());
        final ResolvedColumn aCol = columns.get(0);
        Assert.assertEquals("a", aCol.name());
        Assert.assertEquals(ID, aCol.nodeType());
        Assert.assertSame(rootTuple, aCol.source());
        Assert.assertEquals(0, aCol.sourceIndex());
    }

    /**
     * Project of a non-array as an array
     */
    @Test
    public void testArrayMismatch() {
        // Simulate SELECT a[0] ...
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a[0]"), ScanTestUtils.parsers());
        // Simulate a data source, with early schema, of (a)
        // where a is not an array.
        final TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        final NullColumnBuilder builder = new NullColumnBuilder(null, false);
        final ResolvedRow rootTuple = new ResolvedRow(builder);
        try {
            new ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers());
            Assert.fail();
        } catch (final UserException e) {
            // Expected
        }
    }
}

