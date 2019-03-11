/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer;


import java.io.File;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Assert;
import org.junit.Test;


public class ImporterTest extends ImporterTestSupport {
    @Test
    public void testCreateContextSingleFile() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        SpatialFile file = new SpatialFile(new File(dir, "archsites.shp"));
        file.prepare();
        ImportContext context = importer.createContext(file);
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(file, task.getData());
    }

    @Test
    public void testCreateContextDirectoryHomo() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        Directory d = new Directory(dir);
        ImportContext context = importer.createContext(d);
        Assert.assertEquals(2, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(d.part("archsites"), task.getData());
        task = context.getTasks().get(1);
        Assert.assertEquals(d.part("bugsites"), task.getData());
    }

    @Test
    public void testCreateContextDirectoryHetero() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        unpack("geotiff/EmissiveCampania.tif.bz2", dir);
        Directory d = new Directory(dir);
        ImportContext context = importer.createContext(d);
        Assert.assertEquals(2, context.getTasks().size());
        // cannot ensure order of tasks due to hashing
        HashSet files = new HashSet();
        files.add(context.getTasks().get(0).getData());
        files.add(context.getTasks().get(1).getData());
        Assert.assertTrue(files.containsAll(d.getFiles()));
    }

    @Test
    public void testCreateContextFromArchive() throws Exception {
        File file = file("shape/archsites_epsg_prj.zip");
        Archive arch = new Archive(file);
        ImportContext context = importer.createContext(arch);
        Assert.assertEquals(1, context.getTasks().size());
    }

    @Test
    public void testCreateContextIgnoreHidden() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        FileUtils.touch(new File(dir, ".DS_Store"));
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(1, context.getTasks().size());
    }

    @Test
    public void testCalculateBounds() throws Exception {
        FeatureTypeInfo resource = getCatalog().getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        CatalogBuilder cb = new CatalogBuilder(getCatalog());
        ReferencedEnvelope nativeBounds = cb.getNativeBounds(resource);
        resource.setNativeBoundingBox(nativeBounds);
        resource.setLatLonBoundingBox(cb.getLatLonBounds(nativeBounds, resource.getCRS()));
        getCatalog().save(resource);
        Assert.assertNotNull(resource.getNativeBoundingBox());
        Assert.assertFalse(resource.getNativeBoundingBox().isEmpty());
        ReferencedEnvelope bbox = resource.getNativeBoundingBox();
        // Test null bbox
        resource.setNativeBoundingBox(null);
        importer.calculateBounds(resource);
        Assert.assertFalse(resource.getNativeBoundingBox().isEmpty());
        Assert.assertEquals(bbox, resource.getNativeBoundingBox());
        // Test empty bbox
        resource.setNativeBoundingBox(new ReferencedEnvelope());
        Assert.assertTrue(resource.getNativeBoundingBox().isEmpty());
        importer.calculateBounds(resource);
        Assert.assertFalse(resource.getNativeBoundingBox().isEmpty());
        Assert.assertEquals(bbox, resource.getNativeBoundingBox());
        // Test nonempty bbox - should not be changed
        ReferencedEnvelope customBbox = new ReferencedEnvelope(30, 60, (-10), 30, bbox.getCoordinateReferenceSystem());
        resource.setNativeBoundingBox(customBbox);
        Assert.assertFalse(bbox.equals(resource.getNativeBoundingBox()));
        importer.calculateBounds(resource);
        Assert.assertFalse(resource.getNativeBoundingBox().isEmpty());
        Assert.assertFalse(bbox.equals(resource.getNativeBoundingBox()));
        // Test with "recalculate-bounds"=false
        resource.setNativeBoundingBox(customBbox);
        resource.getMetadata().put("recalculate-bounds", false);
        Assert.assertFalse(bbox.equals(resource.getNativeBoundingBox()));
        importer.calculateBounds(resource);
        Assert.assertFalse(resource.getNativeBoundingBox().isEmpty());
        Assert.assertFalse(bbox.equals(resource.getNativeBoundingBox()));
        // Test with "recalculate-bounds"=true
        resource.setNativeBoundingBox(customBbox);
        resource.getMetadata().put("recalculate-bounds", true);
        Assert.assertFalse(bbox.equals(resource.getNativeBoundingBox()));
        importer.calculateBounds(resource);
        Assert.assertFalse(resource.getNativeBoundingBox().isEmpty());
        Assert.assertTrue(bbox.equals(resource.getNativeBoundingBox()));
        // Test with "recalculate-bounds"="true"
        resource.setNativeBoundingBox(customBbox);
        resource.getMetadata().put("recalculate-bounds", "true");
        Assert.assertFalse(bbox.equals(resource.getNativeBoundingBox()));
        importer.calculateBounds(resource);
        Assert.assertFalse(resource.getNativeBoundingBox().isEmpty());
        Assert.assertTrue(bbox.equals(resource.getNativeBoundingBox()));
    }
}

