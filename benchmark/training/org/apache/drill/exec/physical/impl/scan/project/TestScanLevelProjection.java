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


import ProjectionType.ARRAY;
import ProjectionType.TUPLE;
import ProjectionType.UNPROJECTED;
import ProjectionType.UNSPECIFIED;
import SchemaPath.DYNAMIC_STAR;
import UnresolvedColumn.UNRESOLVED;
import UnresolvedColumn.WILDCARD;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.physical.impl.scan.ScanTestUtils;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.physical.rowSet.project.ImpliedTupleRequest;
import org.apache.drill.exec.physical.rowSet.project.RequestedTuple;
import org.apache.drill.exec.physical.rowSet.project.RequestedTuple.RequestedColumn;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the level of projection done at the level of the scan as a whole;
 * before knowledge of table "implicit" columns or the specific table schema.
 */
@Category(RowSetTests.class)
public class TestScanLevelProjection extends SubOperatorTest {
    /**
     * Basic test: select a set of columns (a, b, c) when the
     * data source has an early schema of (a, c, d). (a, c) are
     * projected, (d) is null.
     */
    @Test
    public void testBasics() {
        // Simulate SELECT a, b, c ...
        // Build the projection plan and verify
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a", "b", "c"), ScanTestUtils.parsers());
        Assert.assertFalse(scanProj.projectAll());
        Assert.assertFalse(scanProj.projectNone());
        Assert.assertEquals(3, scanProj.requestedCols().size());
        Assert.assertEquals("a", scanProj.requestedCols().get(0).rootName());
        Assert.assertEquals("b", scanProj.requestedCols().get(1).rootName());
        Assert.assertEquals("c", scanProj.requestedCols().get(2).rootName());
        Assert.assertEquals(3, scanProj.columns().size());
        Assert.assertEquals("a", scanProj.columns().get(0).name());
        Assert.assertEquals("b", scanProj.columns().get(1).name());
        Assert.assertEquals("c", scanProj.columns().get(2).name());
        // Verify column type
        Assert.assertEquals(UNRESOLVED, scanProj.columns().get(0).nodeType());
        // Verify tuple projection
        RequestedTuple outputProj = scanProj.rootProjection();
        Assert.assertEquals(3, outputProj.projections().size());
        Assert.assertNotNull(outputProj.get("a"));
        Assert.assertTrue(outputProj.get("a").isSimple());
        RequestedTuple readerProj = scanProj.readerProjection();
        Assert.assertEquals(3, readerProj.projections().size());
        Assert.assertNotNull(readerProj.get("a"));
        Assert.assertEquals(UNSPECIFIED, readerProj.projectionType("a"));
        Assert.assertEquals(UNPROJECTED, readerProj.projectionType("d"));
    }

    /**
     * Map projection occurs when a query contains project-list items with
     * a dot, such as "a.b". We may not know the type of "b", but have
     * just learned that "a" must be a map.
     */
    @Test
    public void testMap() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a.x", "b.x", "a.y", "b.y", "c"), ScanTestUtils.parsers());
        Assert.assertFalse(scanProj.projectAll());
        Assert.assertFalse(scanProj.projectNone());
        Assert.assertEquals(3, scanProj.columns().size());
        Assert.assertEquals("a", scanProj.columns().get(0).name());
        Assert.assertEquals("b", scanProj.columns().get(1).name());
        Assert.assertEquals("c", scanProj.columns().get(2).name());
        // Verify column type
        Assert.assertEquals(UNRESOLVED, scanProj.columns().get(0).nodeType());
        // Map structure
        final RequestedColumn a = element();
        Assert.assertTrue(a.isTuple());
        Assert.assertEquals(UNSPECIFIED, a.mapProjection().projectionType("x"));
        Assert.assertEquals(UNSPECIFIED, a.mapProjection().projectionType("y"));
        Assert.assertEquals(UNPROJECTED, a.mapProjection().projectionType("z"));
        final RequestedColumn c = element();
        Assert.assertTrue(c.isSimple());
        // Verify tuple projection
        RequestedTuple outputProj = scanProj.rootProjection();
        Assert.assertEquals(3, outputProj.projections().size());
        Assert.assertNotNull(outputProj.get("a"));
        Assert.assertTrue(outputProj.get("a").isTuple());
        RequestedTuple readerProj = scanProj.readerProjection();
        Assert.assertEquals(3, readerProj.projections().size());
        Assert.assertNotNull(readerProj.get("a"));
        Assert.assertEquals(TUPLE, readerProj.projectionType("a"));
        Assert.assertEquals(UNSPECIFIED, readerProj.projectionType("c"));
        Assert.assertEquals(UNPROJECTED, readerProj.projectionType("d"));
    }

    /**
     * Similar to maps, if the project list contains "a[1]" then we've learned that
     * a is an array, but we don't know what type.
     */
    @Test
    public void testArray() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a[1]", "a[3]"), ScanTestUtils.parsers());
        Assert.assertFalse(scanProj.projectAll());
        Assert.assertFalse(scanProj.projectNone());
        Assert.assertEquals(1, scanProj.columns().size());
        Assert.assertEquals("a", scanProj.columns().get(0).name());
        // Verify column type
        Assert.assertEquals(UNRESOLVED, scanProj.columns().get(0).nodeType());
        // Map structure
        final RequestedColumn a = element();
        Assert.assertTrue(a.isArray());
        Assert.assertFalse(a.hasIndex(0));
        Assert.assertTrue(a.hasIndex(1));
        Assert.assertFalse(a.hasIndex(2));
        Assert.assertTrue(a.hasIndex(3));
        // Verify tuple projection
        RequestedTuple outputProj = scanProj.rootProjection();
        Assert.assertEquals(1, outputProj.projections().size());
        Assert.assertNotNull(outputProj.get("a"));
        Assert.assertTrue(outputProj.get("a").isArray());
        RequestedTuple readerProj = scanProj.readerProjection();
        Assert.assertEquals(1, readerProj.projections().size());
        Assert.assertNotNull(readerProj.get("a"));
        Assert.assertEquals(ARRAY, readerProj.projectionType("a"));
        Assert.assertEquals(UNPROJECTED, readerProj.projectionType("c"));
    }

    /**
     * Simulate a SELECT * query by passing "**" (Drill's internal representation
     * of the wildcard) as a column name.
     */
    @Test
    public void testWildcard() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        Assert.assertTrue(scanProj.projectAll());
        Assert.assertFalse(scanProj.projectNone());
        Assert.assertEquals(1, scanProj.requestedCols().size());
        Assert.assertTrue(scanProj.requestedCols().get(0).isDynamicStar());
        Assert.assertEquals(1, scanProj.columns().size());
        Assert.assertEquals(DYNAMIC_STAR, scanProj.columns().get(0).name());
        // Verify bindings
        Assert.assertEquals(scanProj.columns().get(0).name(), scanProj.requestedCols().get(0).rootName());
        // Verify column type
        Assert.assertEquals(WILDCARD, scanProj.columns().get(0).nodeType());
        // Verify tuple projection
        RequestedTuple outputProj = scanProj.rootProjection();
        Assert.assertEquals(1, outputProj.projections().size());
        Assert.assertNotNull(outputProj.get("**"));
        Assert.assertTrue(outputProj.get("**").isWildcard());
        RequestedTuple readerProj = scanProj.readerProjection();
        Assert.assertTrue((readerProj instanceof ImpliedTupleRequest));
        Assert.assertEquals(UNSPECIFIED, readerProj.projectionType("a"));
    }

    /**
     * Test an empty projection which occurs in a
     * SELECT COUNT(*) query.
     */
    @Test
    public void testEmptyProjection() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList(), ScanTestUtils.parsers());
        Assert.assertFalse(scanProj.projectAll());
        Assert.assertTrue(scanProj.projectNone());
        Assert.assertEquals(0, scanProj.requestedCols().size());
        // Verify tuple projection
        RequestedTuple outputProj = scanProj.rootProjection();
        Assert.assertEquals(0, outputProj.projections().size());
        RequestedTuple readerProj = scanProj.readerProjection();
        Assert.assertTrue((readerProj instanceof ImpliedTupleRequest));
        Assert.assertEquals(UNPROJECTED, readerProj.projectionType("a"));
    }

    /**
     * Can include both a wildcard and a column name. The Project
     * operator will fill in the column, the scan framework just ignores
     * the extra column.
     */
    @Test
    public void testWildcardAndColumns() {
        ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList(DYNAMIC_STAR, "a"), ScanTestUtils.parsers());
        Assert.assertTrue(scanProj.projectAll());
        Assert.assertFalse(scanProj.projectNone());
        Assert.assertEquals(2, scanProj.requestedCols().size());
        Assert.assertEquals(1, scanProj.columns().size());
        // Verify tuple projection
        RequestedTuple outputProj = scanProj.rootProjection();
        Assert.assertEquals(2, outputProj.projections().size());
        Assert.assertNotNull(outputProj.get("**"));
        Assert.assertTrue(outputProj.get("**").isWildcard());
        Assert.assertNotNull(outputProj.get("a"));
        RequestedTuple readerProj = scanProj.readerProjection();
        Assert.assertTrue((readerProj instanceof ImpliedTupleRequest));
        Assert.assertEquals(UNSPECIFIED, readerProj.projectionType("a"));
        Assert.assertEquals(UNSPECIFIED, readerProj.projectionType("c"));
    }

    /**
     * Test a column name and a wildcard.
     */
    @Test
    public void testColumnAndWildcard() {
        ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("a", DYNAMIC_STAR), ScanTestUtils.parsers());
        Assert.assertTrue(scanProj.projectAll());
        Assert.assertFalse(scanProj.projectNone());
        Assert.assertEquals(2, scanProj.requestedCols().size());
        Assert.assertEquals(1, scanProj.columns().size());
    }

    /**
     * Can't include a wildcard twice.
     * <p>
     * Note: Drill actually allows this, but the work should be done
     * in the project operator; scan should see at most one wildcard.
     */
    @Test
    public void testErrorTwoWildcards() {
        try {
            new ScanLevelProjection(RowSetTestUtils.projectList(DYNAMIC_STAR, DYNAMIC_STAR), ScanTestUtils.parsers());
            Assert.fail();
        } catch (final UserException e) {
            // Expected
        }
    }
}

