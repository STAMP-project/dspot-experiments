/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import java.util.Arrays;
import java.util.List;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.data.Join;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;


public class JoinExtractingVisitorTest {
    private FeatureTypeInfo lakes;

    private FeatureTypeInfo forests;

    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    private FeatureTypeInfo buildings;

    @Test
    public void testTwoWayJoin() {
        JoinExtractingVisitor visitor = new JoinExtractingVisitor(Arrays.asList(lakes, forests), Arrays.asList("a", "b"));
        Filter f = ff.equals(ff.property("a/FID"), ff.property("b/FID"));
        f.accept(visitor, null);
        Assert.assertEquals("a", visitor.getPrimaryAlias());
        Filter primary = visitor.getPrimaryFilter();
        Assert.assertNull(primary);
        List<Join> joins = visitor.getJoins();
        Assert.assertEquals(1, joins.size());
        Join join = joins.get(0);
        Assert.assertEquals("Forests", join.getTypeName());
        Assert.assertEquals("b", join.getAlias());
        Assert.assertEquals(ff.equals(ff.property("a.FID"), ff.property("b.FID")), join.getJoinFilter());
    }

    @Test
    public void testThreeWayJoinWithAliases() {
        JoinExtractingVisitor visitor = new JoinExtractingVisitor(Arrays.asList(lakes, forests, buildings), Arrays.asList("a", "b", "c"));
        Filter f1 = ff.equals(ff.property("a/FID"), ff.property("b/FID"));
        Filter f2 = ff.equals(ff.property("b/FID"), ff.property("c/FID"));
        Filter f = ff.and(Arrays.asList(f1, f2));
        testThreeWayJoin(visitor, f);
    }

    @Test
    public void testThreeWayJoinNoAliasesUnqualified() {
        JoinExtractingVisitor visitor = new JoinExtractingVisitor(Arrays.asList(lakes, forests, buildings), null);
        Filter f1 = ff.equals(ff.property("Lakes/FID"), ff.property("Forests/FID"));
        Filter f2 = ff.equals(ff.property("Forests/FID"), ff.property("Buildings/FID"));
        Filter f = ff.and(Arrays.asList(f1, f2));
        testThreeWayJoin(visitor, f);
    }

    @Test
    public void testThreeWayJoinNoAliasesQualified() {
        JoinExtractingVisitor visitor = new JoinExtractingVisitor(Arrays.asList(lakes, forests, buildings), null);
        Filter f1 = ff.equals(ff.property("gs:Lakes/FID"), ff.property("gs:Forests/FID"));
        Filter f2 = ff.equals(ff.property("gs:Forests/FID"), ff.property("gs:Buildings/FID"));
        Filter f = ff.and(Arrays.asList(f1, f2));
        testThreeWayJoin(visitor, f);
    }

    @Test
    public void testThreeWayJoinPrimaryFilters() {
        JoinExtractingVisitor visitor = new JoinExtractingVisitor(Arrays.asList(lakes, forests, buildings), Arrays.asList("a", "b", "c"));
        Filter fj1 = ff.equals(ff.property("a/FID"), ff.property("b/FID"));
        Filter fj2 = ff.equals(ff.property("b/FID"), ff.property("c/FID"));
        Filter f1 = ff.equals(ff.property("a/FID"), ff.literal("Lakes.10"));
        Filter f2 = ff.equals(ff.property("b/FID"), ff.literal("Forests.10"));
        Filter f3 = ff.equals(ff.property("c/FID"), ff.literal("Buildings.10"));
        Filter f = ff.and(Arrays.asList(f1, f2, f3, fj1, fj2));
        f.accept(visitor, null);
        Assert.assertEquals("b", visitor.getPrimaryAlias());
        Filter primary = visitor.getPrimaryFilter();
        Assert.assertEquals(ff.equals(ff.property("FID"), ff.literal("Forests.10")), primary);
        List<Join> joins = visitor.getJoins();
        Assert.assertEquals(2, joins.size());
        Join j1 = joins.get(0);
        Assert.assertEquals("Lakes", j1.getTypeName());
        Assert.assertEquals("a", j1.getAlias());
        Assert.assertEquals(ff.equals(ff.property("a.FID"), ff.property("b.FID")), j1.getJoinFilter());
        Assert.assertEquals(ff.equals(ff.property("FID"), ff.literal("Lakes.10")), j1.getFilter());
        Join j2 = joins.get(1);
        Assert.assertEquals("Buildings", j2.getTypeName());
        Assert.assertEquals("c", j2.getAlias());
        Assert.assertEquals(ff.equals(ff.property("b.FID"), ff.property("c.FID")), j2.getJoinFilter());
        Assert.assertEquals(ff.equals(ff.property("FID"), ff.literal("Buildings.10")), j2.getFilter());
    }

    @Test
    public void testThreeWayJoinWithSelf() {
        JoinExtractingVisitor visitor = new JoinExtractingVisitor(Arrays.asList(forests, lakes, lakes), Arrays.asList("a", "b", "c"));
        Filter f1 = ff.equals(ff.property("a/FID"), ff.property("b/FID"));
        Filter f2 = ff.equals(ff.property("b/FID"), ff.property("c/FID"));
        Filter f = ff.and(Arrays.asList(f1, f2));
        f.accept(visitor, null);
        Assert.assertEquals("b", visitor.getPrimaryAlias());
        Filter primary = visitor.getPrimaryFilter();
        Assert.assertNull(primary);
        List<Join> joins = visitor.getJoins();
        Assert.assertEquals(2, joins.size());
        Join j1 = joins.get(0);
        Assert.assertEquals("Forests", j1.getTypeName());
        Assert.assertEquals("a", j1.getAlias());
        Assert.assertEquals(ff.equals(ff.property("a.FID"), ff.property("b.FID")), j1.getJoinFilter());
        Join j2 = joins.get(1);
        Assert.assertEquals("Lakes", j2.getTypeName());
        Assert.assertEquals("c", j2.getAlias());
        Assert.assertEquals(ff.equals(ff.property("b.FID"), ff.property("c.FID")), j2.getJoinFilter());
    }
}

