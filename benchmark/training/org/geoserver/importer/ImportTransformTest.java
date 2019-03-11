/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer;


import ImportContext.State.COMPLETE;
import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.importer.transform.DateFormatTransform;
import org.geoserver.importer.transform.IntegerFieldToDateTransform;
import org.geoserver.importer.transform.NumberFormatTransform;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


public class ImportTransformTest extends ImporterTestSupport {
    DataStoreInfo store;

    @Test
    public void testNumberFormatTransform() throws Exception {
        Catalog cat = getCatalog();
        File dir = unpack("shape/restricted.zip");
        SpatialFile file = new SpatialFile(new File(dir, "restricted.shp"));
        file.prepare();
        ImportContext context = importer.createContext(file, store);
        Assert.assertEquals(1, context.getTasks().size());
        context.setTargetStore(store);
        ImportTask task = context.getTasks().get(0);
        task.getTransform().add(new NumberFormatTransform("cat", Integer.class));
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        FeatureTypeInfo ft = cat.getFeatureTypeByDataStore(store, "restricted");
        Assert.assertNotNull(ft);
        SimpleFeatureType schema = ((SimpleFeatureType) (ft.getFeatureType()));
        Assert.assertEquals(Integer.class, schema.getDescriptor("cat").getType().getBinding());
        FeatureIterator it = ft.getFeatureSource(null, null).getFeatures().features();
        try {
            Assert.assertTrue(it.hasNext());
            while (it.hasNext()) {
                SimpleFeature f = ((SimpleFeature) (it.next()));
                Assert.assertTrue(((f.getAttribute("cat")) instanceof Integer));
            } 
        } finally {
            it.close();
        }
    }

    @Test
    public void testIntegerToDateTransform() throws Exception {
        Catalog cat = getCatalog();
        File dir = unpack("shape/archsites_epsg_prj.zip");
        SpatialFile file = new SpatialFile(new File(dir, "archsites.shp"));
        file.prepare();
        ImportContext context = importer.createContext(file, store);
        Assert.assertEquals(1, context.getTasks().size());
        context.setTargetStore(store);
        ImportTask task = context.getTasks().get(0);
        // this is a silly test - CAT_ID ranges from 1-25 and is not supposed to be a date
        // java date handling doesn't like dates in year 1
        task.getTransform().add(new IntegerFieldToDateTransform("CAT_ID"));
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        FeatureTypeInfo ft = cat.getFeatureTypeByDataStore(store, "archsites");
        Assert.assertNotNull(ft);
        SimpleFeatureType schema = ((SimpleFeatureType) (ft.getFeatureType()));
        Assert.assertEquals(Timestamp.class, schema.getDescriptor("CAT_ID").getType().getBinding());
        FeatureIterator it = ft.getFeatureSource(null, null).getFeatures().features();
        int year = 2;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            // make sure we have something
            Assert.assertTrue(it.hasNext());
            // the first date will be bogus due to java date limitation
            it.next();
            while (it.hasNext()) {
                SimpleFeature f = ((SimpleFeature) (it.next()));
                // class will be timestamp
                cal.setTime(((Date) (f.getAttribute("CAT_ID"))));
                Assert.assertEquals((year++), cal.get(Calendar.YEAR));
            } 
        } finally {
            it.close();
        }
    }

    @Test
    public void testDateFormatTransform() throws Exception {
        Catalog cat = getCatalog();
        File dir = unpack("shape/ivan.zip");
        SpatialFile file = new SpatialFile(new File(dir, "ivan.shp"));
        file.prepare();
        ImportContext context = importer.createContext(file, store);
        Assert.assertEquals(1, context.getTasks().size());
        context.setTargetStore(store);
        ImportTask task = context.getTasks().get(0);
        task.getTransform().add(new DateFormatTransform("timestamp", "yyyy-MM-dd HH:mm:ss.S"));
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        FeatureTypeInfo ft = cat.getFeatureTypeByDataStore(store, "ivan");
        Assert.assertNotNull(ft);
        SimpleFeatureType schema = ((SimpleFeatureType) (ft.getFeatureType()));
        Assert.assertTrue(Date.class.isAssignableFrom(schema.getDescriptor("timestamp").getType().getBinding()));
        FeatureIterator it = ft.getFeatureSource(null, null).getFeatures().features();
        try {
            Assert.assertTrue(it.hasNext());
            while (it.hasNext()) {
                SimpleFeature f = ((SimpleFeature) (it.next()));
                Assert.assertTrue(((f.getAttribute("timestamp")) instanceof Date));
            } 
        } finally {
            it.close();
        }
    }

    @Test
    public void testReprojectTransform() throws Exception {
        Catalog cat = getCatalog();
        File dir = unpack("shape/archsites_epsg_prj.zip");
        SpatialFile file = new SpatialFile(new File(dir, "archsites.shp"));
        file.prepare();
        ImportContext context = importer.createContext(file, store);
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        LayerInfo l1 = context.getTasks().get(0).getLayer();
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:26713"), l1.getResource().getNativeCRS()));
        Assert.assertEquals("EPSG:26713", l1.getResource().getSRS());
        dir = unpack("shape/archsites_epsg_prj.zip");
        file = new SpatialFile(new File(dir, "archsites.shp"));
        file.prepare();
        context = importer.createContext(file, store);
        ImportTask item = context.getTasks().get(0);
        item.getTransform().add(new org.geoserver.importer.transform.ReprojectTransform(CRS.decode("EPSG:4326")));
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        LayerInfo l2 = context.getTasks().get(0).getLayer();
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), l2.getResource().getNativeCRS()));
        Assert.assertEquals("EPSG:4326", l2.getResource().getSRS());
        Assert.assertFalse(l1.getResource().getNativeBoundingBox().equals(l2.getResource().getNativeBoundingBox()));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(l2.getResource().getNativeCRS(), l2.getResource().getNativeBoundingBox().getCoordinateReferenceSystem()));
        LayerInfo l = cat.getLayer(l2.getId());
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), l2.getResource().getNativeCRS()));
        Assert.assertEquals("EPSG:4326", l2.getResource().getSRS());
    }
}

