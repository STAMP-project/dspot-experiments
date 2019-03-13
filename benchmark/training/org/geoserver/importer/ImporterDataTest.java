/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer;


import H2DataStoreFactory.DBTYPE.key;
import ImportContext.State.PENDING;
import ImportTask.State.BAD_FORMAT;
import ImportTask.State.COMPLETE;
import ImportTask.State.NO_CRS;
import ImportTask.State.NO_FORMAT;
import ImportTask.State.READY;
import Importer.UPLOAD_ROOT_KEY;
import Query.ALL;
import SLDHandler.VERSION_11;
import UpdateMode.APPEND;
import UpdateMode.REPLACE;
import VectorFormat.EMPTY_BOUNDS;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.importer.transform.AbstractInlineVectorTransform;
import org.geoserver.importer.transform.AttributesToPointGeometryTransform;
import org.geoserver.importer.transform.PostScriptTransform;
import org.geoserver.importer.transform.TransformChain;
import org.geoserver.platform.resource.Resources;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.renderer.style.StyleAttributeExtractor;
import org.geotools.styling.Style;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;


public class ImporterDataTest extends ImporterTestSupport {
    private static final class DescriptionLimitingTransform extends AbstractInlineVectorTransform {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        @Override
        public SimpleFeature apply(ImportTask task, DataStore dataStore, SimpleFeature oldFeature, SimpleFeature feature) throws Exception {
            Object origDesc = feature.getAttribute("description");
            if (origDesc == null) {
                return feature;
            }
            String newDesc = StringUtils.abbreviate(origDesc.toString(), 255);
            feature.setAttribute("description", newDesc);
            return feature;
        }
    }

    @Test
    public void testUploadRootExternalProps() throws Exception {
        // On a brand new data folder, the directory may not exists until the Importer has been
        // invoked the first time
        File dirFromProperties = Resources.directory(Resources.fromPath("props_uploads"));
        // Read the upload root folder and creates it if does not exists
        importer.getUploadRoot();
        // Read the folder again...
        dirFromProperties = Resources.directory(Resources.fromPath("props_uploads"));
        // ... and ensure it is the same as defined on the .properties file
        Assert.assertEquals(dirFromProperties, importer.getUploadRoot());
        // Let's now override the external folder through the Environment variable. This takes
        // precedence on .properties
        System.setProperty(UPLOAD_ROOT_KEY, "env_uploads");
        // Let's check that the upload root is now different from the previous one...
        Assert.assertNotEquals(dirFromProperties, importer.getUploadRoot());
        // ... but it is equal to the one defined through the Environment variable instead
        // Read the folder again...
        dirFromProperties = Resources.directory(Resources.fromPath("env_uploads"));
        // ... and ensure it is the same as defined on the .properties file
        Assert.assertEquals(dirFromProperties, importer.getUploadRoot());
    }

    @Test
    public void testImportShapefile() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("archsites");
    }

    @Test
    public void testImportRemoteDataFromDirectory() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new RemoteData(dir.getCanonicalPath()));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("archsites");
    }

    @Test
    public void testImportRemoteDataFromZip() throws Exception {
        URL resource = ImporterTestSupport.class.getResource("test-data/shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new RemoteData(resource.toExternalForm()));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("archsites");
    }

    @Test
    public void testImportShapefileFromDataDir() throws Exception {
        File dataDir = getCatalog().getResourceLoader().getBaseDirectory();
        File dir = unpack("shape/archsites_epsg_prj.zip", dataDir);
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        Assert.assertEquals(COMPLETE, task.getState());
        Assert.assertEquals("file:archsites.shp", task.getLayer().getResource().getStore().getConnectionParameters().get("url"));
        runChecks("archsites");
    }

    @Test
    public void testImportShapefilesWithExtraFiles() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        // make some 'extra' files
        new File(dir, "archsites.sbn").createNewFile();
        new File(dir, "archsites.sbx").createNewFile();
        new File(dir, "archsites.shp.xml").createNewFile();
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
    }

    @Test
    public void testImportSameLayerNameDifferentWorkspace() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        // make some 'extra' files
        new File(dir, "archsites.sbn").createNewFile();
        new File(dir, "archsites.sbx").createNewFile();
        new File(dir, "archsites.shp.xml").createNewFile();
        Catalog cat = getCatalog();
        WorkspaceInfo ws1 = createWorkspace(cat, "ws1");
        ImportContext context = importer.createContext(new Directory(dir), ws1);
        importer.run(context);
        Assert.assertNotNull(cat.getLayerByName("ws1:archsites"));
        // same import, different workspace
        WorkspaceInfo ws2 = createWorkspace(cat, "ws2");
        context = importer.createContext(new Directory(dir), ws2);
        importer.run(context);
        Assert.assertNotNull(cat.getLayerByName("ws2:archsites"));
    }

    @Test
    public void testImportShapefiles() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(2, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
        task = context.getTasks().get(1);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("bugsites", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        Assert.assertNotNull(cat.getLayerByName("bugsites"));
        Assert.assertEquals(COMPLETE, context.getTasks().get(0).getState());
        Assert.assertEquals(COMPLETE, context.getTasks().get(1).getState());
        runChecks("archsites");
        runChecks("bugsites");
    }

    @Test
    public void testImportShapefilesWithError() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_no_crs.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(2, context.getTasks().size());
        ImportTask task1 = context.getTasks().get(0);
        Assert.assertEquals(NO_CRS, task1.getState());
        Assert.assertEquals("archsites", task1.getLayer().getResource().getName());
        ImportTask task2 = context.getTasks().get(1);
        Assert.assertEquals(READY, task2.getState());
        Assert.assertEquals("bugsites", task2.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getLayerByName("archsites"));
        Assert.assertNotNull(cat.getLayerByName("bugsites"));
        Assert.assertEquals(NO_CRS, task1.getState());
        Assert.assertEquals(COMPLETE, task2.getState());
        runChecks("bugsites");
    }

    @Test
    public void testImportNoCrsLatLonBoundingBox() throws Exception {
        File dir = unpack("shape/archsites_no_crs.zip");
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(NO_CRS, task.getState());
        task.getLayer().getResource().setSRS("EPSG:26713");
        importer.changed(task);
        Assert.assertEquals(READY, task.getState());
        ResourceInfo r = task.getLayer().getResource();
        Assert.assertNotNull(r.getLatLonBoundingBox());
        Assert.assertNotNull(r.boundingBox());
        Assert.assertNotNull(r.boundingBox().getCoordinateReferenceSystem());
        Assert.assertEquals("EPSG:26713", CRS.toSRS(r.boundingBox().getCoordinateReferenceSystem()));
        // Do the import, verify the changed CRS is preserved when the bounds are calculated
        importer.doDirectImport(task);
        Assert.assertEquals(COMPLETE, task.getState());
        r = task.getLayer().getResource();
        Assert.assertNotNull(r.getLatLonBoundingBox());
        Assert.assertNotEquals(EMPTY_BOUNDS, r.getLatLonBoundingBox());
        Assert.assertNotNull(r.boundingBox());
        Assert.assertNotEquals(EMPTY_BOUNDS, r.boundingBox());
        Assert.assertNotNull(r.boundingBox().getCoordinateReferenceSystem());
        Assert.assertEquals("EPSG:26713", CRS.toSRS(r.boundingBox().getCoordinateReferenceSystem()));
    }

    @Test
    public void testImportUnknownFile() throws Exception {
        File dir = new File("./src/test/resources/org/geoserver/importer/test-data/random");
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(NO_FORMAT, task.getState());
        Assert.assertNull(task.getData().getFormat());
    }

    @Test
    public void testImportUnknownFileIndirect() throws Exception {
        DataStoreInfo ds = createH2DataStore(null, "foo");
        File dir = new File("./src/test/resources/org/geoserver/importer/test-data/random");
        ImportContext context = importer.createContext(new Directory(dir), ds);
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(NO_FORMAT, task.getState());
        Assert.assertNull(task.getData().getFormat());
    }

    @Test
    public void testImportDatabase() throws Exception {
        File dir = unpack("h2/cookbook.zip");
        Map params = new HashMap();
        params.put(key, "h2");
        params.put(H2DataStoreFactory.DATABASE.key, new File(dir, "cookbook").getAbsolutePath());
        ImportContext context = importer.createContext(new Database(params));
        Assert.assertEquals(3, context.getTasks().size());
        Assert.assertEquals(READY, context.getTasks().get(0).getState());
        Assert.assertEquals(READY, context.getTasks().get(1).getState());
        Assert.assertEquals(READY, context.getTasks().get(2).getState());
        Catalog cat = getCatalog();
        Assert.assertNull(cat.getDataStoreByName(cat.getDefaultWorkspace(), "cookbook"));
        Assert.assertNull(cat.getLayerByName("point"));
        Assert.assertNull(cat.getLayerByName("line"));
        Assert.assertNull(cat.getLayerByName("polygon"));
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getTasks().get(0).getState());
        Assert.assertEquals(COMPLETE, context.getTasks().get(1).getState());
        Assert.assertEquals(COMPLETE, context.getTasks().get(2).getState());
        Assert.assertNotNull(cat.getDataStoreByName(cat.getDefaultWorkspace(), "cookbook"));
        DataStoreInfo ds = cat.getDataStoreByName(cat.getDefaultWorkspace(), "cookbook");
        Assert.assertNotNull(cat.getFeatureTypeByDataStore(ds, "point"));
        Assert.assertNotNull(cat.getFeatureTypeByDataStore(ds, "line"));
        Assert.assertNotNull(cat.getFeatureTypeByDataStore(ds, "polygon"));
        Assert.assertNotNull(cat.getLayerByName("point"));
        Assert.assertNotNull(cat.getLayerByName("line"));
        Assert.assertNotNull(cat.getLayerByName("polygon"));
        runChecks("point");
        runChecks("line");
        runChecks("polygon");
    }

    @Test
    public void testImportIntoDatabase() throws Exception {
        Catalog cat = getCatalog();
        DataStoreInfo ds = createH2DataStore(cat.getDefaultWorkspace().getName(), "spearfish");
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        ImportContext context = importer.createContext(new Directory(dir), ds);
        Assert.assertEquals(2, context.getTasks().size());
        ImportTask task1 = context.getTasks().get(0);
        ImportTask task2 = context.getTasks().get(1);
        Assert.assertEquals(READY, task1.getState());
        Assert.assertEquals(READY, task2.getState());
        // assertEquals(ImportTask.State.READY, context.getTasks().get(1).getState());
        // cannot ensure ordering of items
        HashSet resources = new HashSet();
        resources.add(task1.getLayer().getResource().getName());
        resources.add(task2.getLayer().getResource().getName());
        Assert.assertTrue(resources.contains("bugsites"));
        Assert.assertTrue(resources.contains("archsites"));
        importer.run(context);
        Assert.assertEquals(COMPLETE, task1.getState());
        Assert.assertEquals(COMPLETE, task2.getState());
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        Assert.assertNotNull(cat.getLayerByName("bugsites"));
        Assert.assertNotNull(cat.getFeatureTypeByDataStore(ds, "archsites"));
        Assert.assertNotNull(cat.getFeatureTypeByDataStore(ds, "bugsites"));
        runChecks("archsites");
        runChecks("bugsites");
    }

    @Test
    public void testImportIntoDatabaseWithEncoding() throws Exception {
        Catalog cat = getCatalog();
        DataStoreInfo ds = createH2DataStore(cat.getDefaultWorkspace().getName(), "ming");
        File dir = tmpDir();
        unpack("shape/ming_time.zip", dir);
        ImportContext context = importer.createContext(new Directory(dir), ds);
        Assert.assertEquals(1, context.getTasks().size());
        context.getTasks().get(0).getData().setCharsetEncoding("UTF-8");
        importer.run(context);
        FeatureTypeInfo info = ((FeatureTypeInfo) (context.getTasks().get(0).getLayer().getResource()));
        FeatureSource<? extends FeatureType, ? extends Feature> fs = info.getFeatureSource(null, null);
        FeatureCollection<? extends FeatureType, ? extends Feature> features = fs.getFeatures();
        FeatureIterator<? extends Feature> it = features.features();
        Assert.assertTrue(it.hasNext());
        SimpleFeature next = ((SimpleFeature) (it.next()));
        // let's test some attributes to see if they were digested properly
        String type_ch = ((String) (next.getAttribute("type_ch")));
        Assert.assertEquals("?", type_ch);
        String name_ch = ((String) (next.getAttribute("name_ch")));
        Assert.assertEquals("????", name_ch);
        it.close();
    }

    @Test
    public void testImportIntoDatabaseUpdateModes() throws Exception {
        testImportIntoDatabase();
        DataStoreInfo ds = getCatalog().getDataStoreByName("spearfish");
        Assert.assertNotNull(ds);
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        FeatureSource<? extends FeatureType, ? extends Feature> fs = getCatalog().getFeatureTypeByName("archsites").getFeatureSource(null, null);
        int archsitesCount = fs.getCount(ALL);
        fs = getCatalog().getFeatureTypeByName("bugsites").getFeatureSource(null, null);
        int bugsitesCount = fs.getCount(ALL);
        ImportContext context = importer.createContext(new Directory(dir), ds);
        context.getTasks().get(0).setUpdateMode(REPLACE);
        context.getTasks().get(1).setUpdateMode(APPEND);
        importer.run(context);
        fs = getCatalog().getFeatureTypeByName("archsites").getFeatureSource(null, null);
        int archsitesCount2 = fs.getCount(ALL);
        fs = getCatalog().getFeatureTypeByName("bugsites").getFeatureSource(null, null);
        int bugsitesCount2 = fs.getCount(ALL);
        // tasks might not be in same order
        if (context.getTasks().get(0).getLayer().getName().equals("archsites")) {
            Assert.assertEquals(archsitesCount, archsitesCount2);
            Assert.assertEquals((bugsitesCount * 2), bugsitesCount2);
        } else {
            Assert.assertEquals((archsitesCount * 2), archsitesCount2);
            Assert.assertEquals(bugsitesCount, bugsitesCount2);
        }
    }

    @Test
    public void testImportGeoTIFF() throws Exception {
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2");
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "EmissiveCampania.tif")));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("EmissiveCampania", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("EmissiveCampania"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("EmissiveCampania");
    }

    @Test
    public void testImportGeoTIFFFromDataDir() throws Exception {
        File dataDir = getCatalog().getResourceLoader().getBaseDirectory();
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2", dataDir);
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "EmissiveCampania.tif")));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("EmissiveCampania", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("EmissiveCampania"));
        Assert.assertEquals(COMPLETE, task.getState());
        Assert.assertEquals("file:EmissiveCampania.tif", getURL());
        runChecks("EmissiveCampania");
    }

    @Test
    public void testImportNameClash() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        runChecks("archsites");
        context = importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        importer.run(context);
        Assert.assertEquals("archsites0", context.getTasks().get(0).getLayer().getName());
        runChecks("archsites0");
    }

    @Test
    public void testArchiveOnIndirectImport() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        Assert.assertTrue(dir.exists());
        DataStoreInfo ds = createH2DataStore(null, "foo");
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "archsites.shp")), ds);
        context.setArchive(true);
        importer.run(context);
        // under windows the shp in the original folder remains locked, but we could
        // not figure out why (a test in ShapefileDataStoreTest shows we can read a shapefile
        // and then delete the shp file without issues)
        if (!(SystemUtils.IS_OS_WINDOWS)) {
            Assert.assertFalse(dir.exists());
        }
        dir = unpack("shape/bugsites_esri_prj.tar.gz");
        Assert.assertTrue(dir.exists());
        context = importer.createContext(new SpatialFile(new File(dir, "bugsites.shp")), ds);
        context.setArchive(false);
        importer.run(context);
        Assert.assertTrue(dir.exists());
    }

    @Test
    public void testImportDatabaseIntoDatabase() throws Exception {
        File dir = unpack("h2/cookbook.zip");
        DataStoreInfo ds = createH2DataStore("gs", "cookbook");
        Map params = new HashMap();
        params.put(key, "h2");
        params.put(H2DataStoreFactory.DATABASE.key, new File(dir, "cookbook").getAbsolutePath());
        ImportContext context = importer.createContext(new Database(params), ds);
        Assert.assertEquals(3, context.getTasks().size());
    }

    @Test
    public void testImportCSV() throws Exception {
        File dir = unpack("csv/locations.zip");
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "locations.csv")));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(NO_CRS, task.getState());
        LayerInfo layer = task.getLayer();
        ResourceInfo resource = layer.getResource();
        resource.setSRS("EPSG:4326");
        Assert.assertTrue("Item not ready", importer.prep(task));
        Assert.assertEquals(READY, task.getState());
        context.updated();
        Assert.assertEquals(PENDING, context.getState());
        importer.run(context);
        Assert.assertEquals(ImportContext.State.COMPLETE, context.getState());
        FeatureTypeInfo fti = ((FeatureTypeInfo) (resource));
        SimpleFeatureType featureType = ((SimpleFeatureType) (fti.getFeatureType()));
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        Assert.assertNull("Expecting no geometry", geometryDescriptor);
        Assert.assertEquals(4, featureType.getAttributeCount());
    }

    @Test
    public void testImportGML2Poi() throws Exception {
        File gmlFile = file("gml/poi.gml2.gml");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        DataStoreInfo h2DataStore = createH2DataStore(wsName, "gml2poi");
        checkGMLPoiImport(gmlFile, h2DataStore);
    }

    @Test
    public void testImportGML3Poi() throws Exception {
        File gmlFile = file("gml/poi.gml3.gml");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        DataStoreInfo h2DataStore = createH2DataStore(wsName, "gml3poi");
        checkGMLPoiImport(gmlFile, h2DataStore);
    }

    @Test
    public void testImportGML2WithSchema() throws Exception {
        // TODO: remove this manipulation once we get relative schema references to work
        File gmlSourceFile = new File("src/test/resources/org/geoserver/importer/test-data/gml/states.gml2.gml");
        File gmlFile = new File("./target/states.gml2.gml");
        File schemaSourceFile = new File("src/test/resources/org/geoserver/importer/test-data/gml/states.gml2.xsd");
        File schemaFile = new File("./target/states.gml2.xsd");
        FileUtils.copyFile(schemaSourceFile, schemaFile);
        String gml = FileUtils.readFileToString(gmlSourceFile);
        gml = gml.replace("${schemaLocation}", schemaFile.getCanonicalPath());
        FileUtils.writeStringToFile(gmlFile, gml);
        String wsName = getCatalog().getDefaultWorkspace().getName();
        DataStoreInfo h2DataStore = createH2DataStore(wsName, "gml2States");
        checkGMLStatesImport(gmlFile, h2DataStore);
    }

    @Test
    public void testImportGML3WithSchema() throws Exception {
        // TODO: remove this manipulation once we get relative schema references to work
        File gmlSourceFile = new File("src/test/resources/org/geoserver/importer/test-data/gml/states.gml3.gml");
        File gmlFile = new File("./target/states.gml3.gml");
        File schemaSourceFile = new File("src/test/resources/org/geoserver/importer/test-data/gml/states.gml3.xsd");
        File schemaFile = new File("./target/states.gml3.xsd");
        FileUtils.copyFile(schemaSourceFile, schemaFile);
        String gml = FileUtils.readFileToString(gmlSourceFile);
        gml = gml.replace("${schemaLocation}", schemaFile.getCanonicalPath());
        FileUtils.writeStringToFile(gmlFile, gml);
        String wsName = getCatalog().getDefaultWorkspace().getName();
        DataStoreInfo h2DataStore = createH2DataStore(wsName, "gml2States");
        checkGMLStatesImport(gmlFile, h2DataStore);
    }

    @Test
    public void testImportCSVIndirect() throws Exception {
        File dir = unpack("csv/locations.zip");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        DataStoreInfo h2DataStore = createH2DataStore(wsName, "csvindirecttest");
        SpatialFile importData = new SpatialFile(new File(dir, "locations.csv"));
        ImportContext context = importer.createContext(importData, h2DataStore);
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        TransformChain transformChain = task.getTransform();
        transformChain.add(new AttributesToPointGeometryTransform("LAT", "LON"));
        Assert.assertEquals(NO_CRS, task.getState());
        LayerInfo layer = task.getLayer();
        ResourceInfo resource = layer.getResource();
        resource.setSRS("EPSG:4326");
        Assert.assertTrue("Item not ready", importer.prep(task));
        Assert.assertEquals(READY, task.getState());
        context.updated();
        Assert.assertEquals(PENDING, context.getState());
        importer.run(context);
        Assert.assertEquals(ImportContext.State.COMPLETE, context.getState());
        FeatureTypeInfo fti = ((FeatureTypeInfo) (resource));
        SimpleFeatureType featureType = ((SimpleFeatureType) (fti.getFeatureType()));
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        Assert.assertNotNull("Expecting geometry", geometryDescriptor);
        Assert.assertEquals("Invalid geometry name", "location", geometryDescriptor.getLocalName());
        Assert.assertEquals(3, featureType.getAttributeCount());
        FeatureSource<? extends FeatureType, ? extends Feature> featureSource = fti.getFeatureSource(null, null);
        FeatureCollection<? extends FeatureType, ? extends Feature> features = featureSource.getFeatures();
        Assert.assertEquals(9, features.size());
        FeatureIterator<? extends Feature> featureIterator = features.features();
        Assert.assertTrue("Expected features", featureIterator.hasNext());
        SimpleFeature feature = ((SimpleFeature) (featureIterator.next()));
        Assert.assertNotNull(feature);
        Assert.assertEquals("Invalid city attribute", "Trento", feature.getAttribute("CITY"));
        Assert.assertEquals("Invalid number attribute", 140, feature.getAttribute("NUMBER"));
        Object geomAttribute = feature.getAttribute("location");
        Assert.assertNotNull("Expected geometry", geomAttribute);
        Point point = ((Point) (geomAttribute));
        Coordinate coordinate = point.getCoordinate();
        Assert.assertEquals("Invalid x coordinate", 11.12, coordinate.x, 0.1);
        Assert.assertEquals("Invalid y coordinate", 46.07, coordinate.y, 0.1);
        featureIterator.close();
    }

    @Test
    public void testImportKMLIndirect() throws Exception {
        File dir = unpack("kml/sample.zip");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        DataStoreInfo h2DataStore = createH2DataStore(wsName, "kmltest");
        SpatialFile importData = new SpatialFile(new File(dir, "sample.kml"));
        ImportContext context = importer.createContext(importData, h2DataStore);
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        LayerInfo layer = task.getLayer();
        ResourceInfo resource = layer.getResource();
        Assert.assertEquals("Invalid srs", "EPSG:4326", resource.getSRS());
        ReferencedEnvelope emptyBounds = new ReferencedEnvelope();
        emptyBounds.setToNull();
        Assert.assertTrue("Unexpected bounding box", emptyBounds.equals(resource.getNativeBoundingBox()));
        // transform chain to limit characters
        // otherwise we get a sql exception thrown
        TransformChain transformChain = task.getTransform();
        transformChain.add(new ImporterDataTest.DescriptionLimitingTransform());
        importer.run(context);
        Exception error = task.getError();
        if (error != null) {
            error.printStackTrace();
            Assert.fail(error.getMessage());
        }
        Assert.assertFalse("Bounding box not updated", emptyBounds.equals(resource.getNativeBoundingBox()));
        FeatureTypeInfo fti = ((FeatureTypeInfo) (resource));
        Assert.assertEquals("Invalid type name", "sample", fti.getName());
        FeatureSource<? extends FeatureType, ? extends Feature> featureSource = fti.getFeatureSource(null, null);
        Assert.assertEquals("Unexpected feature count", 20, featureSource.getCount(ALL));
    }

    @Test
    public void testImportDirectoryWithRasterIndirect() throws Exception {
        DataStoreInfo ds = createH2DataStore(getCatalog().getDefaultWorkspace().getName(), "shapes");
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        unpack("geotiff/EmissiveCampania.tif.bz2", dir);
        ImportContext context = importer.createContext(new Directory(dir), ds);
        Assert.assertEquals(3, context.getTasks().size());
        Assert.assertTrue(((context.getData()) instanceof Directory));
        ImportTask task = Iterables.find(context.getTasks(), new Predicate<ImportTask>() {
            @Override
            public boolean apply(ImportTask input) {
                return "archsites".equals(input.getLayer().getResource().getName());
            }
        });
        Assert.assertEquals(READY, task.getState());
        Assert.assertTrue(((task.getData()) instanceof SpatialFile));
        Assert.assertEquals("Shapefile", task.getData().getFormat().getName());
        task = Iterables.find(context.getTasks(), new Predicate<ImportTask>() {
            @Override
            public boolean apply(ImportTask input) {
                return "bugsites".equals(input.getLayer().getResource().getName());
            }
        });
        Assert.assertEquals(READY, task.getState());
        Assert.assertTrue(((task.getData()) instanceof SpatialFile));
        Assert.assertEquals("Shapefile", task.getData().getFormat().getName());
        task = Iterables.find(context.getTasks(), new Predicate<ImportTask>() {
            @Override
            public boolean apply(ImportTask input) {
                return "EmissiveCampania".equals(input.getLayer().getResource().getName());
            }
        });
        Assert.assertEquals(BAD_FORMAT, task.getState());
        Assert.assertTrue(((task.getData()) instanceof SpatialFile));
        Assert.assertEquals("GeoTIFF", task.getData().getFormat().getName());
    }

    @Test
    public void testImportDirectoryWithRasterDirect() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        unpack("geotiff/EmissiveCampania.tif.bz2", dir);
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(3, context.getTasks().size());
        Assert.assertTrue(((context.getData()) instanceof Directory));
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
        Assert.assertTrue(((task.getData()) instanceof SpatialFile));
        Assert.assertEquals("Shapefile", task.getData().getFormat().getName());
        task = context.getTasks().get(1);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("bugsites", task.getLayer().getResource().getName());
        Assert.assertTrue(((task.getData()) instanceof SpatialFile));
        Assert.assertEquals("Shapefile", task.getData().getFormat().getName());
        task = context.getTasks().get(2);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("EmissiveCampania", task.getLayer().getResource().getName());
        Assert.assertTrue(((task.getData()) instanceof SpatialFile));
        Assert.assertEquals("GeoTIFF", task.getData().getFormat().getName());
    }

    @Test
    public void testGeoJSONImport() throws Exception {
        DataStoreInfo h2 = createH2DataStore(getCatalog().getDefaultWorkspace().getName(), "jsontest");
        File dir = unpack("geojson/point.json.zip");
        ImportContext imp = importer.createContext(new SpatialFile(new File(dir, "point.json")), h2);
        Assert.assertEquals(1, imp.getTasks().size());
        Assert.assertEquals(READY, imp.task(0).getState());
        importer.run(imp);
        Assert.assertEquals(ImportContext.State.COMPLETE, imp.getState());
        checkNoErrors(imp);
        runChecks("point");
    }

    @Test
    public void testJaggedGeoJSON() throws Exception {
        File json = file("geojson/jagged.json");
        ImportContext ctx = importer.createContext(new SpatialFile(json));
        Assert.assertEquals(1, ctx.getTasks().size());
        SimpleFeatureType info = ((SimpleFeatureType) (ctx.getTasks().get(0).getMetadata().get(FeatureType.class)));
        Assert.assertEquals(4, info.getAttributeCount());
        int cnt = 0;
        for (int i = 0; i < (info.getAttributeCount()); i++) {
            if (info.getDescriptor(i).getLocalName().equals("geometry")) {
                cnt++;
            }
        }
        Assert.assertEquals(1, cnt);
    }

    @Test
    public void testGeoJSONImportDirectory() throws Exception {
        DataStoreInfo h2 = createH2DataStore(getCatalog().getDefaultWorkspace().getName(), "jsontest");
        File dir = unpack("geojson/point.json.zip");
        unpack("geojson/line.json.zip", dir);
        unpack("geojson/polygon.json.zip", dir);
        ImportContext imp = importer.createContext(new Directory(dir), h2);
        Assert.assertEquals(3, imp.getTasks().size());
        Assert.assertEquals(PENDING, imp.getState());
        Assert.assertEquals(READY, imp.task(0).getState());
        Assert.assertEquals(READY, imp.task(1).getState());
        Assert.assertEquals(READY, imp.task(2).getState());
        importer.run(imp);
        Assert.assertEquals(ImportContext.State.COMPLETE, imp.getState());
        runChecks("point");
        runChecks("line");
        runChecks("polygon");
    }

    @Test
    public void testIllegalNames() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        for (File f : dir.listFiles()) {
            String ext = FilenameUtils.getExtension(f.getName());
            String base = FilenameUtils.getBaseName(f.getName());
            f.renameTo(new File(dir, ("1-." + ext)));
        }
        ImportContext imp = importer.createContext(new Directory(dir));
        importer.run(imp);
        ImportTask task = imp.getTasks().get(0);
        Assert.assertEquals("a_1_", task.getLayer().getName());
        Assert.assertEquals("a_1_", task.getLayer().getResource().getName());
    }

    @Test
    public void testImportArchiveWithStyleFile() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        // add an sld file
        String sld = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((((((((((((((((((((((((((((("<StyledLayerDescriptor version=\"1.0.0\" " + " xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\" ") + " xmlns:ogc=\"http://www.opengis.net/ogc\" ") + " xmlns=\"http://www.opengis.net/sld\" ") + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">") + "  <!-- a Named Layer is the basic building block of an SLD document -->") + "  <NamedLayer>") + "    <UserStyle>") + "      <FeatureTypeStyle>") + "        <Rule>") + "            <PointSymbolizer>") + "              <Graphic>") + "                <Mark>") + "                  <WellKnownName>square</WellKnownName>") + "                  <Fill>") + "                    <CssParameter name=\"fill\">#FF0000</CssParameter>") + "                  </Fill>") + "                </Mark>") + "              <Size>6</Size>") + "            </Graphic>") + "          </PointSymbolizer>") + "          <TextSymbolizer>") + "            <Label>") + "             <ogc:PropertyName>CAT_ID</ogc:PropertyName>") + "           </Label>") + "          </TextSymbolizer>") + "        </Rule>") + "      </FeatureTypeStyle>") + "    </UserStyle>") + "  </NamedLayer>") + "</StyledLayerDescriptor>");
        StyleInfo info = writeStyleAndImport(sld, "archsites.sld", dir);
        Style style = info.getStyle();
        StyleAttributeExtractor atts = new StyleAttributeExtractor();
        style.accept(atts);
        Assert.assertTrue(atts.getAttributeNameSet().contains("CAT_ID"));
    }

    @Test
    public void testImportStyleWithCorrectVersion() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        // add an sld file
        String sld = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((((((((((((((((("<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd\" xmlns:se=\"http://www.opengis.net/se\">" + "  <NamedLayer>") + "    <UserStyle>") + "      <se:FeatureTypeStyle>") + "        <se:Rule>") + "          <se:PolygonSymbolizer>") + "            <se:Fill>") + "              <se:SvgParameter name=\"fill\">#d7191c</se:SvgParameter>") + "            </se:Fill>") + "            <se:Stroke>") + "              <se:SvgParameter name=\"stroke\">#000000</se:SvgParameter>") + "            </se:Stroke>") + "          </se:PolygonSymbolizer>") + "        </se:Rule>") + "      </se:FeatureTypeStyle>") + "    </UserStyle>") + "  </NamedLayer>") + "</StyledLayerDescriptor>");
        StyleInfo info = writeStyleAndImport(sld, "archsites.sld", dir);
        Assert.assertEquals(VERSION_11, info.getFormatVersion());
    }

    @Test
    public void testImportSpaceInNames() throws Exception {
        File dir = unpack("shape/spaceInNames.zip");
        DataStoreInfo ds = createH2DataStore(null, "spaceInNamesContainer");
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "spaceInNames.shp")), ds);
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(NO_CRS, task.getState());
        task.getLayer().getResource().setSRS("EPSG:26713");
        importer.changed(task);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("spaceInNames", task.getLayer().getResource().getName());
        importer.run(context);
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("spaceInNames"));
        Assert.assertEquals(COMPLETE, task.getState());
        SimpleFeatureSource fs = ((SimpleFeatureSource) (cat.getFeatureTypeByName("spaceInNames").getFeatureSource(null, null)));
        SimpleFeature sf = DataUtilities.first(fs.getFeatures());
        Assert.assertNotNull(sf.getAttribute("WIND_SPEED"));
        Assert.assertNotNull(sf.getAttribute("WIND_DIREC"));
    }

    @Test
    public void testRunPostScript() throws Exception {
        // check if bash is there
        Assume.assumeTrue("Could not find sh in path, skipping", ImporterDataTest.checkShellAvailable());
        // even with bash available, the test won't work on windows as it won't know
        // how to run the .sh out of the box
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        // write out a simple shell script in the data dir and make it executable
        File scripts = getDataDirectory().findOrCreateDir("importer", "scripts");
        File script = new File(scripts, "test.sh");
        FileUtils.writeStringToFile(script, "touch test.properties\n");
        script.setExecutable(true, true);
        // create a simple import and place the script in the transform chain
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        Assert.assertEquals("archsites", task.getLayer().getResource().getName());
        TransformChain transformChain = task.getTransform();
        transformChain.add(new PostScriptTransform("test.sh", Collections.emptyList()));
        importer.run(context);
        // check the import run normally
        Assert.assertEquals(ImportContext.State.COMPLETE, context.getState());
        Catalog cat = getCatalog();
        Assert.assertNotNull(cat.getLayerByName("archsites"));
        Assert.assertEquals(COMPLETE, task.getState());
        runChecks("archsites");
        // verify the script also run
        File testFile = new File(scripts, "test.properties");
        Assert.assertTrue(testFile.exists());
    }

    @Test
    public void testRunNonExistingScript() throws Exception {
        // prepare the scripts folder, but leave it empty
        File scripts = getDataDirectory().findOrCreateDir("importer", "scripts");
        FileUtils.deleteQuietly(scripts);
        Assert.assertTrue(scripts.mkdirs());
        try {
            new PostScriptTransform("i_am_not_there.sh", Collections.emptyList());
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("i_am_not_there.sh"));
        }
    }

    @Test
    public void testPostScriptDisableTraversal() throws Exception {
        // prepare the scripts folder, but leave it empty
        File scripts = getDataDirectory().findOrCreateDir("importer", "scripts");
        FileUtils.deleteQuietly(scripts);
        Assert.assertTrue(scripts.mkdirs());
        try {
            new PostScriptTransform("../wfs.xml", Collections.emptyList());
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("invalid .."));
        }
    }

    @Test
    public void testPostScriptDisableAbsolutePath() {
        // check if sh is there
        Assume.assumeTrue(new File("/bin/sh").exists());
        try {
            new PostScriptTransform("/bin/sh", Collections.emptyList());
            Assert.fail("Should have failed disallowing usage of absoute path");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("/bin/sh"), CoreMatchers.containsString(("not " + "found"))));
        }
    }
}

