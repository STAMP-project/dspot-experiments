/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 - 2016 Boundless Spatial Inc.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ysld;


import org.geotools.ysld.TestUtils;
import org.geotools.ysld.parse.ScaleRange;
import org.geotools.ysld.parse.ZoomContext;
import org.geotools.ysld.parse.ZoomContextFinder;
import org.geowebcache.grid.Grid;
import org.geowebcache.grid.GridSet;
import org.geowebcache.grid.GridSetBroker;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GWCZoomContextFinderTest {
    private static final double EPSILON = 1.0E-9;

    @Test
    public void testGetContext() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andReturn(set);
        Grid grid1 = mockGrid(1, 5.0E8, set);
        replay(broker, set, grid1);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        Assert.assertThat(zContext, Matchers.notNullValue());
        verify(broker, set, grid1);
    }

    @Test
    public void testCouldntFind() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        expect(broker.get("doesntexist")).andStubReturn(null);
        Grid grid1 = mockGrid(1, 5.0E8, set);
        replay(broker, set, grid1);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("doesntexist");
        Assert.assertThat(zContext, Matchers.nullValue());
        verify(broker, set, grid1);
    }

    @Test
    public void testCorrectScale() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        Grid grid1 = mockGrid(1, 5.0E8, set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set, grid1);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        double denom = zContext.getScaleDenominator(1);
        Assert.assertThat(denom, Matchers.closeTo(5.0E8, GWCZoomContextFinderTest.EPSILON));
        verify(broker, set, grid1);
    }

    @Test
    public void testScaleNegativeLevel() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        double denom = zContext.getScaleDenominator((-1));
        Assert.assertThat(denom, Matchers.is(Double.POSITIVE_INFINITY));
        verify(broker, set);
    }

    @Test
    public void testScalePastEnd() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        double denom = zContext.getScaleDenominator(5);
        Assert.assertThat(denom, Matchers.is(0.0));
        verify(broker, set);
    }

    @Test
    public void testRange() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        Grid grid1 = mockGrid(1, 5.0E8, set);
        Grid grid2 = mockGrid(2, 2.0E8, set);
        Grid grid3 = mockGrid(3, 1.0E8, set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set, grid1, grid2, grid3);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        ScaleRange range = zContext.getRange(2, 2);
        Assert.assertThat(range, TestUtils.rangeContains(2.0E8));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(5.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(1.0E8)));
        verify(broker, set, grid1, grid2, grid3);
    }

    @Test
    public void testRangeStart() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        Grid grid0 = mockGrid(0, 5.0E8, set);
        Grid grid1 = mockGrid(1, 2.0E8, set);
        Grid grid2 = mockGrid(2, 1.0E8, set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set, grid0, grid1, grid2);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        ScaleRange range = zContext.getRange(0, 1);
        Assert.assertThat(range, TestUtils.rangeContains((1 / (GWCZoomContextFinderTest.EPSILON))));
        Assert.assertThat(range, TestUtils.rangeContains(5.0E8));
        Assert.assertThat(range, TestUtils.rangeContains(2.0E8));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(1.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(GWCZoomContextFinderTest.EPSILON)));
        verify(broker, set, grid0, grid1, grid2);
    }

    @Test
    public void testRangeEnd() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        Grid grid2 = mockGrid(2, 5.0E8, set);
        Grid grid3 = mockGrid(3, 2.0E8, set);
        Grid grid4 = mockGrid(4, 1.0E8, set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set, grid2, grid3, grid4);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        ScaleRange range = zContext.getRange(3, 4);
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains((1 / (GWCZoomContextFinderTest.EPSILON)))));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(5.0E8)));
        Assert.assertThat(range, TestUtils.rangeContains(2.0E8));
        Assert.assertThat(range, TestUtils.rangeContains(1.0E8));
        Assert.assertThat(range, TestUtils.rangeContains(GWCZoomContextFinderTest.EPSILON));
        verify(broker, set, grid2, grid3, grid4);
    }

    @Test
    public void testRangePastEnd() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        Grid grid2 = mockGrid(2, 5.0E8, set);
        Grid grid3 = mockGrid(3, 2.0E8, set);
        Grid grid4 = mockGrid(4, 1.0E8, set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set, grid2, grid3, grid4);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        ScaleRange range = zContext.getRange(6, 7);
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains((1 / (GWCZoomContextFinderTest.EPSILON)))));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(5.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(2.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(1.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(GWCZoomContextFinderTest.EPSILON)));
        verify(broker, set, grid2, grid3, grid4);
    }

    @Test
    public void testRangePastStart() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        Grid grid2 = mockGrid(2, 5.0E8, set);
        Grid grid3 = mockGrid(3, 2.0E8, set);
        Grid grid4 = mockGrid(4, 1.0E8, set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set, grid2, grid3, grid4);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        ScaleRange range = zContext.getRange((-2), (-1));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains((1 / (GWCZoomContextFinderTest.EPSILON)))));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(5.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(2.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(1.0E8)));
        Assert.assertThat(range, Matchers.not(TestUtils.rangeContains(GWCZoomContextFinderTest.EPSILON)));
        verify(broker, set, grid2, grid3, grid4);
    }

    @Test
    public void testRangeBoundaryLikeTileFuser() throws Exception {
        GridSetBroker broker = createMock(GridSetBroker.class);
        GridSet set = createMock(GridSet.class);
        expect(broker.get("test")).andStubReturn(set);
        Grid grid2 = mockGrid(2, 5.0E8, set);
        Grid grid3 = mockGrid(3, 2.0E8, set);
        Grid grid4 = mockGrid(4, 1.0E8, set);
        expect(set.getNumLevels()).andStubReturn(5);
        replay(broker, set, grid2, grid3, grid4);
        ZoomContextFinder finder = new GWCZoomContextFinder(broker);
        ZoomContext zContext = finder.get("test");
        ScaleRange range = zContext.getRange(3, 3);
        Assert.assertThat(range.getMaxDenom(), Matchers.closeTo((5.0E8 / 1.005), GWCZoomContextFinderTest.EPSILON));
        Assert.assertThat(range.getMinDenom(), Matchers.closeTo((2.0E8 / 1.005), GWCZoomContextFinderTest.EPSILON));
        verify(broker, set, grid2, grid3, grid4);
    }
}

