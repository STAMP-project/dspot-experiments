/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import ImageMosaicFormat.FILTER;
import ImageMosaicFormat.MAX_ALLOWED_TILES;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class CatalogBuilderIntTest extends GeoServerSystemTestSupport {
    @Test
    public void testLargeNDMosaic() throws Exception {
        // build a mosaic with 1025 files (the standard ulimit is 1024)
        File mosaic = new File("./target/largeMosaic");
        try {
            createTimeMosaic(mosaic, 1025);
            // now configure a new store based on it
            Catalog cat = getCatalog();
            CatalogBuilder cb = new CatalogBuilder(cat);
            CoverageStoreInfo store = cb.buildCoverageStore("largeMosaic");
            store.setURL(mosaic.getAbsolutePath());
            store.setType("ImageMosaic");
            cat.add(store);
            // and configure also the coverage
            cb.setStore(store);
            CoverageInfo ci = cb.buildCoverage();
            cat.add(ci);
            cat.getResourcePool().dispose();
        } finally {
            if ((mosaic.exists()) && (mosaic.isDirectory())) {
                FileUtils.deleteDirectory(mosaic);
            }
        }
    }

    @Test
    public void testMosaicParameters() throws Exception {
        // build a mosaic with 1025 files (the standard ulimit is 1024)
        File mosaic = new File("./target/smallMosaic");
        try {
            createTimeMosaic(mosaic, 4);
            // now configure a new store based on it
            Catalog cat = getCatalog();
            CatalogBuilder cb = new CatalogBuilder(cat);
            CoverageStoreInfo store = cb.buildCoverageStore("smallMosaic");
            store.setURL(mosaic.getAbsolutePath());
            store.setType("ImageMosaic");
            cat.add(store);
            // and configure also the coverage
            cb.setStore(store);
            CoverageInfo ci = cb.buildCoverage();
            cat.add(ci);
            // check the parameters have the default values
            Assert.assertEquals(String.valueOf((-1)), ci.getParameters().get(MAX_ALLOWED_TILES.getName().toString()));
            Assert.assertEquals("", ci.getParameters().get(FILTER.getName().toString()));
            cat.getResourcePool().dispose();
        } finally {
            if ((mosaic.exists()) && (mosaic.isDirectory())) {
                FileUtils.deleteDirectory(mosaic);
            }
        }
    }
}

