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
package org.apache.drill.exec.physical.rowSet.impl;


import ProjectionType.ARRAY;
import ProjectionType.TUPLE;
import ProjectionType.UNPROJECTED;
import ProjectionType.UNSPECIFIED;
import ProjectionType.WILDCARD;
import SchemaPath.DYNAMIC_STAR;
import java.util.ArrayList;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.physical.rowSet.project.ImpliedTupleRequest;
import org.apache.drill.exec.physical.rowSet.project.RequestedTuple;
import org.apache.drill.exec.physical.rowSet.project.RequestedTuple.RequestedColumn;
import org.apache.drill.exec.physical.rowSet.project.RequestedTupleImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(RowSetTests.class)
public class TestProjectedTuple {
    @Test
    public void testProjectionAll() {
        // Null map means everything is projected
        RequestedTuple projSet = RequestedTupleImpl.parse(null);
        Assert.assertTrue((projSet instanceof ImpliedTupleRequest));
        Assert.assertEquals(UNSPECIFIED, projSet.projectionType("foo"));
    }

    /**
     * Test an empty projection which occurs in a
     * SELECT COUNT(*) query.
     */
    @Test
    public void testProjectionNone() {
        // Empty list means nothing is projected
        RequestedTuple projSet = RequestedTupleImpl.parse(new ArrayList<SchemaPath>());
        Assert.assertTrue((projSet instanceof ImpliedTupleRequest));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(0, cols.size());
        Assert.assertEquals(UNPROJECTED, projSet.projectionType("foo"));
    }

    @Test
    public void testProjectionSimple() {
        // Simple non-map columns
        RequestedTuple projSet = RequestedTupleImpl.parse(RowSetTestUtils.projectList("a", "b", "c"));
        Assert.assertTrue((projSet instanceof RequestedTupleImpl));
        Assert.assertEquals(UNSPECIFIED, projSet.projectionType("a"));
        Assert.assertEquals(UNSPECIFIED, projSet.projectionType("b"));
        Assert.assertEquals(UNPROJECTED, projSet.projectionType("d"));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(3, cols.size());
        RequestedColumn a = cols.get(0);
        Assert.assertEquals("a", a.name());
        Assert.assertEquals(UNSPECIFIED, a.type());
        Assert.assertTrue(a.isSimple());
        Assert.assertFalse(a.isWildcard());
        Assert.assertNull(a.mapProjection());
        Assert.assertNull(a.indexes());
        Assert.assertEquals("b", cols.get(1).name());
        Assert.assertEquals(UNSPECIFIED, cols.get(1).type());
        Assert.assertTrue(cols.get(1).isSimple());
        Assert.assertEquals("c", cols.get(2).name());
        Assert.assertEquals(UNSPECIFIED, cols.get(2).type());
        Assert.assertTrue(cols.get(2).isSimple());
    }

    @Test
    public void testProjectionWholeMap() {
        // Whole-map projection (note, fully projected maps are
        // identical to projected simple columns at this level of
        // abstraction.)
        List<SchemaPath> projCols = new ArrayList<>();
        projCols.add(SchemaPath.getSimplePath("map"));
        RequestedTuple projSet = RequestedTupleImpl.parse(projCols);
        Assert.assertTrue((projSet instanceof RequestedTupleImpl));
        Assert.assertEquals(UNSPECIFIED, projSet.projectionType("map"));
        Assert.assertEquals(UNPROJECTED, projSet.projectionType("another"));
        RequestedTuple mapProj = projSet.mapProjection("map");
        Assert.assertNotNull(mapProj);
        Assert.assertTrue((mapProj instanceof ImpliedTupleRequest));
        Assert.assertEquals(UNSPECIFIED, mapProj.projectionType("foo"));
        Assert.assertNotNull(projSet.mapProjection("another"));
        Assert.assertEquals(UNPROJECTED, projSet.mapProjection("another").projectionType("anyCol"));
    }

    @Test
    public void testProjectionMapSubset() {
        // Selected map projection, multiple levels, full projection
        // at leaf level.
        List<SchemaPath> projCols = new ArrayList<>();
        projCols.add(SchemaPath.getCompoundPath("map", "a"));
        projCols.add(SchemaPath.getCompoundPath("map", "b"));
        projCols.add(SchemaPath.getCompoundPath("map", "map2", "x"));
        RequestedTuple projSet = RequestedTupleImpl.parse(projCols);
        Assert.assertTrue((projSet instanceof RequestedTupleImpl));
        Assert.assertEquals(TUPLE, projSet.projectionType("map"));
        // Map: an explicit map at top level
        RequestedTuple mapProj = projSet.mapProjection("map");
        Assert.assertTrue((mapProj instanceof RequestedTupleImpl));
        Assert.assertEquals(UNSPECIFIED, mapProj.projectionType("a"));
        Assert.assertEquals(UNSPECIFIED, mapProj.projectionType("b"));
        Assert.assertEquals(TUPLE, mapProj.projectionType("map2"));
        Assert.assertEquals(UNPROJECTED, mapProj.projectionType("bogus"));
        // Map b: an implied nested map
        RequestedTuple bMapProj = mapProj.mapProjection("b");
        Assert.assertNotNull(bMapProj);
        Assert.assertTrue((bMapProj instanceof ImpliedTupleRequest));
        Assert.assertEquals(UNSPECIFIED, bMapProj.projectionType("foo"));
        // Map2, an nested map, has an explicit projection
        RequestedTuple map2Proj = mapProj.mapProjection("map2");
        Assert.assertNotNull(map2Proj);
        Assert.assertTrue((map2Proj instanceof RequestedTupleImpl));
        Assert.assertEquals(UNSPECIFIED, map2Proj.projectionType("x"));
        Assert.assertEquals(UNPROJECTED, map2Proj.projectionType("bogus"));
    }

    @Test
    public void testProjectionMapFieldAndMap() {
        // Project both a map member and the entire map.
        {
            List<SchemaPath> projCols = new ArrayList<>();
            projCols.add(SchemaPath.getCompoundPath("map", "a"));
            projCols.add(SchemaPath.getCompoundPath("map"));
            RequestedTuple projSet = RequestedTupleImpl.parse(projCols);
            Assert.assertTrue((projSet instanceof RequestedTupleImpl));
            Assert.assertEquals(TUPLE, projSet.projectionType("map"));
            RequestedTuple mapProj = projSet.mapProjection("map");
            Assert.assertTrue((mapProj instanceof ImpliedTupleRequest));
            Assert.assertEquals(UNSPECIFIED, mapProj.projectionType("a"));
            // Didn't ask for b, but did ask for whole map.
            Assert.assertEquals(UNSPECIFIED, mapProj.projectionType("b"));
        }
        // Now the other way around.
        {
            List<SchemaPath> projCols = new ArrayList<>();
            projCols.add(SchemaPath.getCompoundPath("map"));
            projCols.add(SchemaPath.getCompoundPath("map", "a"));
            RequestedTuple projSet = RequestedTupleImpl.parse(projCols);
            Assert.assertTrue((projSet instanceof RequestedTupleImpl));
            Assert.assertEquals(TUPLE, projSet.projectionType("map"));
            RequestedTuple mapProj = projSet.mapProjection("map");
            Assert.assertTrue((mapProj instanceof ImpliedTupleRequest));
            Assert.assertEquals(UNSPECIFIED, mapProj.projectionType("a"));
            Assert.assertEquals(UNSPECIFIED, mapProj.projectionType("b"));
        }
    }

    @Test
    public void testMapDetails() {
        RequestedTuple projSet = RequestedTupleImpl.parse(RowSetTestUtils.projectList("a.b.c", "a.c", "d"));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(2, cols.size());
        RequestedColumn a = cols.get(0);
        Assert.assertEquals("a", a.name());
        Assert.assertFalse(a.isSimple());
        Assert.assertFalse(a.isArray());
        Assert.assertTrue(a.isTuple());
        {
            Assert.assertNotNull(a.mapProjection());
            List<RequestedColumn> aMembers = a.mapProjection().projections();
            Assert.assertEquals(2, aMembers.size());
            RequestedColumn a_b = aMembers.get(0);
            Assert.assertEquals("b", a_b.name());
            Assert.assertTrue(a_b.isTuple());
            {
                Assert.assertNotNull(a_b.mapProjection());
                List<RequestedColumn> a_bMembers = a_b.mapProjection().projections();
                Assert.assertEquals(1, a_bMembers.size());
                Assert.assertEquals("c", a_bMembers.get(0).name());
                Assert.assertTrue(a_bMembers.get(0).isSimple());
            }
            Assert.assertEquals("c", aMembers.get(1).name());
            Assert.assertTrue(aMembers.get(1).isSimple());
        }
        Assert.assertEquals("d", cols.get(1).name());
        Assert.assertTrue(cols.get(1).isSimple());
    }

    @Test
    public void testMapDups() {
        try {
            RequestedTupleImpl.parse(RowSetTestUtils.projectList("a.b", "a.c", "a.b"));
            Assert.fail();
        } catch (UserException e) {
            // Expected
        }
    }

    /**
     * When the project list includes references to both the
     * map as a whole, and members, then the parser is forgiving
     * of duplicate map members since all members are projected.
     */
    @Test
    public void testMapDupsIgnored() {
        RequestedTuple projSet = RequestedTupleImpl.parse(RowSetTestUtils.projectList("a", "a.b", "a.c", "a.b"));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(1, cols.size());
    }

    @Test
    public void testWildcard() {
        RequestedTuple projSet = RequestedTupleImpl.parse(RowSetTestUtils.projectList(DYNAMIC_STAR));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(1, cols.size());
        RequestedColumn wildcard = cols.get(0);
        Assert.assertEquals(WILDCARD, wildcard.type());
        Assert.assertEquals(DYNAMIC_STAR, wildcard.name());
        Assert.assertTrue((!(wildcard.isSimple())));
        Assert.assertTrue(wildcard.isWildcard());
        Assert.assertNull(wildcard.mapProjection());
        Assert.assertNull(wildcard.indexes());
    }

    @Test
    public void testSimpleDups() {
        try {
            RequestedTupleImpl.parse(RowSetTestUtils.projectList("a", "b", "a"));
            Assert.fail();
        } catch (UserException e) {
            // Expected
        }
    }

    @Test
    public void testArray() {
        RequestedTuple projSet = RequestedTupleImpl.parse(RowSetTestUtils.projectList("a[1]", "a[3]"));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(1, cols.size());
        Assert.assertEquals(ARRAY, projSet.projectionType("a"));
        RequestedColumn a = cols.get(0);
        Assert.assertEquals("a", a.name());
        Assert.assertTrue(a.isArray());
        Assert.assertFalse(a.isSimple());
        Assert.assertFalse(a.isTuple());
        boolean[] indexes = a.indexes();
        Assert.assertNotNull(indexes);
        Assert.assertEquals(4, indexes.length);
        Assert.assertFalse(indexes[0]);
        Assert.assertTrue(indexes[1]);
        Assert.assertFalse(indexes[2]);
        Assert.assertTrue(indexes[3]);
    }

    @Test
    public void testArrayDups() {
        try {
            RequestedTupleImpl.parse(RowSetTestUtils.projectList("a[1]", "a[3]", "a[1]"));
            Assert.fail();
        } catch (UserException e) {
            // Expected
        }
    }

    @Test
    public void testArrayAndSimple() {
        RequestedTuple projSet = RequestedTupleImpl.parse(RowSetTestUtils.projectList("a[1]", "a"));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(1, cols.size());
        RequestedColumn a = cols.get(0);
        Assert.assertEquals("a", a.name());
        Assert.assertTrue(a.isArray());
        Assert.assertNull(a.indexes());
    }

    @Test
    public void testSimpleAndArray() {
        RequestedTuple projSet = RequestedTupleImpl.parse(RowSetTestUtils.projectList("a", "a[1]"));
        List<RequestedColumn> cols = projSet.projections();
        Assert.assertEquals(1, cols.size());
        RequestedColumn a = cols.get(0);
        Assert.assertEquals("a", a.name());
        Assert.assertTrue(a.isArray());
        Assert.assertNull(a.indexes());
        Assert.assertEquals(ARRAY, projSet.projectionType("a"));
        Assert.assertEquals(UNPROJECTED, projSet.projectionType("foo"));
    }
}

