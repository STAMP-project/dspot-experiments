/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import WMS.ADVANCED_PROJECTION_DENSIFICATION_KEY;
import WMS.DATELINE_WRAPPING_HEURISTIC_KEY;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Date;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geotools.renderer.style.DynamicSymbolFactoryFinder;
import org.geotools.renderer.style.ExternalGraphicFactory;
import org.geotools.renderer.style.ImageGraphicFactory;
import org.geotools.util.DateRange;
import org.geotools.util.NumberRange;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ian Schneider <ischneider@opengeo.org>
 */
public class WMSTest extends WMSTestSupport {
    static final QName TIME_WITH_START_END = new QName(MockData.SF_URI, "TimeWithStartEnd", MockData.SF_PREFIX);

    WMS wms;

    @Test
    public void testGetTimeElevationToFilterStartEndDate() throws Exception {
        setupStartEndTimeDimension(WMSTest.TIME_WITH_START_END.getLocalPart(), "time", "startTime", "endTime");
        setupStartEndTimeDimension(WMSTest.TIME_WITH_START_END.getLocalPart(), "elevation", "startElevation", "endElevation");
        /* Reference for test assertions
        TimeElevation.0=0|2012-02-11|2012-02-12|1|2
        TimeElevation.1=1|2012-02-12|2012-02-13|2|3
        TimeElevation.2=2|2012-02-11|2012-02-14|1|3
         */
        doTimeElevationFilter(Date.valueOf("2012-02-10"), null);
        doTimeElevationFilter(Date.valueOf("2012-02-11"), null, 0, 2);
        doTimeElevationFilter(Date.valueOf("2012-02-12"), null, 0, 1, 2);
        doTimeElevationFilter(Date.valueOf("2012-02-13"), null, 1, 2);
        doTimeElevationFilter(Date.valueOf("2012-02-15"), null);
        // Test start and end before all ranges.
        doTimeElevationFilter(new DateRange(Date.valueOf("2012-02-09"), Date.valueOf("2012-02-10")), null);
        // Test start before and end during a range.
        doTimeElevationFilter(new DateRange(Date.valueOf("2012-02-09"), Date.valueOf("2012-02-11")), null, 0, 2);
        // Test start on and end after or during a range.
        doTimeElevationFilter(new DateRange(Date.valueOf("2012-02-11"), Date.valueOf("2012-02-13")), null, 0, 1, 2);
        // Test start before and end after all ranges.
        doTimeElevationFilter(new DateRange(Date.valueOf("2012-02-09"), Date.valueOf("2012-02-14")), null, 0, 1, 2);
        // Test start during and end after a range.
        doTimeElevationFilter(new DateRange(Date.valueOf("2012-02-13"), Date.valueOf("2012-02-14")), null, 1, 2);
        // Test start during and end during a range.
        doTimeElevationFilter(new DateRange(Date.valueOf("2012-02-12"), Date.valueOf("2012-02-13")), null, 0, 1, 2);
        // Test start and end after all ranges.
        doTimeElevationFilter(new DateRange(Date.valueOf("2012-02-15"), Date.valueOf("2012-02-16")), null);
        doTimeElevationFilter(null, 0);
        doTimeElevationFilter(null, 1, 0, 2);
        doTimeElevationFilter(null, 2, 0, 1, 2);
        doTimeElevationFilter(null, 3, 1, 2);
        doTimeElevationFilter(null, 4);
        doTimeElevationFilter(null, new NumberRange(Integer.class, (-1), 0));
        doTimeElevationFilter(null, new NumberRange(Integer.class, (-1), 1), 0, 2);
        doTimeElevationFilter(null, new NumberRange(Integer.class, 1, 3), 0, 1, 2);
        doTimeElevationFilter(null, new NumberRange(Integer.class, (-1), 4), 0, 1, 2);
        doTimeElevationFilter(null, new NumberRange(Integer.class, 3, 4), 1, 2);
        doTimeElevationFilter(null, new NumberRange(Integer.class, 4, 5));
        // combined date/elevation - this should be an 'and' filter
        doTimeElevationFilter(Date.valueOf("2012-02-12"), 2, 0, 1, 2);
        // disjunct verification
        doTimeElevationFilter(Date.valueOf("2012-02-11"), 3, 2);
    }

    @Test
    public void testWMSLifecycleHandlerGraphicCacheReset() throws Exception {
        Iterator<ExternalGraphicFactory> it = DynamicSymbolFactoryFinder.getExternalGraphicFactories();
        Map<URL, BufferedImage> imageCache = null;
        while (it.hasNext()) {
            ExternalGraphicFactory egf = it.next();
            if (egf instanceof ImageGraphicFactory) {
                Field cache = egf.getClass().getDeclaredField("imageCache");
                cache.setAccessible(true);
                imageCache = ((Map) (cache.get(egf)));
                URL u = new URL("http://boundless.org");
                BufferedImage b = new BufferedImage(6, 6, 8);
                imageCache.put(u, b);
            }
        } 
        Assert.assertNotEquals(0, imageCache.size());
        getGeoServer().reload();
        Assert.assertEquals(0, imageCache.size());
    }

    @Test
    public void testCacheConfiguration() {
        Assert.assertFalse(wms.isRemoteStylesCacheEnabled());
        WMSInfo info = wms.getServiceInfo();
        info.setCacheConfiguration(new CacheConfiguration(true));
        getGeoServer().save(info);
        Assert.assertTrue(wms.isRemoteStylesCacheEnabled());
    }

    @Test
    public void testProjectionDensification() {
        Assert.assertFalse(wms.isAdvancedProjectionDensificationEnabled());
        WMSInfo info = wms.getServiceInfo();
        info.getMetadata().put(ADVANCED_PROJECTION_DENSIFICATION_KEY, true);
        getGeoServer().save(info);
        Assert.assertTrue(wms.isAdvancedProjectionDensificationEnabled());
    }

    @Test
    public void testWrappingHeuristic() {
        Assert.assertFalse(wms.isDateLineWrappingHeuristicDisabled());
        WMSInfo info = wms.getServiceInfo();
        info.getMetadata().put(DATELINE_WRAPPING_HEURISTIC_KEY, true);
        getGeoServer().save(info);
        Assert.assertTrue(wms.isDateLineWrappingHeuristicDisabled());
    }
}

