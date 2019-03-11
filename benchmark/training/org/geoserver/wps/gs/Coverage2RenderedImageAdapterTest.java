/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs;


import java.awt.geom.Point2D;
import junit.framework.Assert;
import org.geoserver.wps.WPSTestSupport;
import org.geotools.coverage.grid.GridCoverage2D;
import org.junit.Test;


/**
 *
 *
 * @author ETj <etj at geo-solutions.it>
 */
public class Coverage2RenderedImageAdapterTest extends WPSTestSupport {
    protected static final double NODATA = 3.0;

    public Coverage2RenderedImageAdapterTest() {
    }

    @Test
    public void testSame() throws InterruptedException {
        GridCoverage2D src = Coverage2RenderedImageAdapterTest.createTestCoverage(500, 500, 0, 0, 10, 10);
        GridCoverage2D dst = Coverage2RenderedImageAdapterTest.createTestCoverage(500, 500, 0, 0, 10, 10);
        GridCoverage2DRIA cria = GridCoverage2DRIA.create(src, dst, Coverage2RenderedImageAdapterTest.NODATA);
        // --- internal points should stay the same
        Point2D psrc = new Point2D.Double(2.0, 3.0);// this is on dst gc

        Point2D pdst = cria.mapSourcePoint(psrc, 0);
        Assert.assertEquals(2.0, pdst.getX());
        Assert.assertEquals(3.0, pdst.getY());
        // --- external points should not be remapped
        psrc = new Point2D.Double(600.0, 600.0);// this is on dst gc

        pdst = cria.mapSourcePoint(psrc, 0);
        Assert.assertNull(pdst);
        // view(cria, dst.getGridGeometry(), src.getSampleDimensions());
        // Viewer.show(src);
        // Thread.sleep(15000);
    }

    @Test
    public void testSameWorldSmallerDstRaster() throws InterruptedException {
        GridCoverage2D src = Coverage2RenderedImageAdapterTest.createTestCoverage(500, 500, 0, 0, 10, 10);
        GridCoverage2D dst = Coverage2RenderedImageAdapterTest.createTestCoverage(250, 250, 0, 0, 10, 10);
        GridCoverage2DRIA cria = GridCoverage2DRIA.create(dst, src, Coverage2RenderedImageAdapterTest.NODATA);
        // --- internal points should double coords (no interp on coords)
        Point2D psrc = new Point2D.Double(13.0, 16.0);// this is on dst gc

        Point2D pdst = cria.mapSourcePoint(psrc, 0);
        Assert.assertNotNull(("Can't convert " + psrc), pdst);
        Assert.assertEquals(26.0, pdst.getX());
        Assert.assertEquals(32.0, pdst.getY());
        // --- external points should not be remapped
        psrc = new Point2D.Double(600.0, 600.0);// this is on dst gc

        pdst = cria.mapSourcePoint(psrc, 0);
        Assert.assertNull(pdst);
        // new Viewer(getName(), cria);
        // Thread.sleep(15000);
    }

    /**
     * Same raster dimension, subset word area
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testSameRasterSmallerWorld() throws InterruptedException {
        GridCoverage2D src = Coverage2RenderedImageAdapterTest.createTestCoverage(500, 500, 0, 0, 10, 10);
        GridCoverage2D dst = Coverage2RenderedImageAdapterTest.createTestCoverage(500, 500, 0, 0, 5, 5);
        // double nodata[] = src.getSampleDimension(0).getNoDataValues();
        GridCoverage2DRIA cria = GridCoverage2DRIA.create(dst, src, Coverage2RenderedImageAdapterTest.NODATA);
        // --- internal points should halves coords (no interp on coords)
        Point2D psrc = new Point2D.Double(0.0, 0.0);
        Point2D pdst = cria.mapSourcePoint(psrc, 0);
        // System.out.println(pdst);
        Assert.assertEquals(0.0, pdst.getX());
        Assert.assertEquals(250.0, pdst.getY());
        psrc = new Point2D.Double(20.0, 30.0);// this is on dst gc

        pdst = cria.mapSourcePoint(psrc, 0);
        Assert.assertEquals(10.0, pdst.getX());
        Assert.assertEquals((250.0 + 15.0), pdst.getY());
        // System.out.println(pdst);
        // new Viewer(getName(), cria);
        // Thread.sleep(15000);
    }

    /**
     * Same raster dimension, subset word area
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testSameRasterTranslatedWorld0() throws InterruptedException {
        GridCoverage2D src = Coverage2RenderedImageAdapterTest.createTestCoverage(500, 500, 0, 0, 5, 5);
        GridCoverage2D dst = Coverage2RenderedImageAdapterTest.createTestCoverage(500, 500, 2, 2, 5, 5);
        GridCoverage2DRIA cria = GridCoverage2DRIA.create(dst, src, Coverage2RenderedImageAdapterTest.NODATA);
        // --- internal points should halves coords (no interp on coords)
        Point2D psrc = new Point2D.Double(0.0, 499.0);// this is on dst gc

        Point2D pdst = cria.mapSourcePoint(psrc, 0);
        Assert.assertNotNull(pdst);
        Assert.assertEquals(200.0, pdst.getX());
        Assert.assertEquals(299.0, pdst.getY());
        // --- points not inside dest but inside src shoud be remapped on a novalue cell
        psrc = new Point2D.Double(0.0, 0.0);// this is on dst gc

        pdst = cria.mapSourcePoint(psrc, 0);
        Assert.assertNull(pdst);// should not map on src raster

        double val = cria.getData().getSampleFloat(0, 0, 0);
        Assert.assertEquals("Value should be noData", Coverage2RenderedImageAdapterTest.NODATA, val);
        // new Viewer(getName(), cria);
        // Thread.sleep(20000);
    }
}

