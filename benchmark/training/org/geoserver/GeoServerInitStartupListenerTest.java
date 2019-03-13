/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver;


import Hints.COMPARISON_TOLERANCE;
import Hints.EXECUTOR_SERVICE;
import Hints.FEATURE_FACTORY;
import Hints.FILTER_FACTORY;
import Hints.GRID_COVERAGE_FACTORY;
import Hints.LENIENT_DATUM_SHIFT;
import Hints.STYLE_FACTORY;
import java.util.concurrent.ExecutorService;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.util.factory.GeoTools;
import org.geotools.util.factory.Hints;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.FeatureFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.style.StyleFactory;


public class GeoServerInitStartupListenerTest {
    private static final double CUSTOM_TOLERANCE = 1.0E-7;

    private GeoserverInitStartupListener listener;

    @Test
    public void testStartupListener() {
        Hints hints = GeoTools.getDefaultHints();
        final Object factory = hints.get(GRID_COVERAGE_FACTORY);
        Assert.assertNotNull(factory);
        Assert.assertTrue((factory instanceof GridCoverageFactory));
        final Object datumShift = hints.get(LENIENT_DATUM_SHIFT);
        Assert.assertNotNull(datumShift);
        Assert.assertTrue(((Boolean) (datumShift)));
        final Object tolerance = hints.get(COMPARISON_TOLERANCE);
        Assert.assertNotNull(tolerance);
        Assert.assertEquals(GeoServerInitStartupListenerTest.CUSTOM_TOLERANCE, ((Double) (tolerance)), 1.0E-12);
        final Object filterFactory = hints.get(FILTER_FACTORY);
        Assert.assertNotNull(filterFactory);
        Assert.assertTrue((filterFactory instanceof FilterFactory));
        final Object styleFactory = hints.get(STYLE_FACTORY);
        Assert.assertNotNull(styleFactory);
        Assert.assertTrue((styleFactory instanceof StyleFactory));
        final Object featureFactory = hints.get(FEATURE_FACTORY);
        Assert.assertNotNull(featureFactory);
        Assert.assertTrue((featureFactory instanceof FeatureFactory));
        final Object executorService = hints.get(EXECUTOR_SERVICE);
        Assert.assertNotNull(executorService);
        Assert.assertTrue((executorService instanceof ExecutorService));
    }

    @Test
    public void testJPEG2000Registration() {
        IIORegistry registry = IIORegistry.getDefaultInstance();
        assertNoSunJPEG2000(registry, ImageReaderSpi.class);
        assertNoSunJPEG2000(registry, ImageWriterSpi.class);
    }
}

