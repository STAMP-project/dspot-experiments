/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer;


import ImportContext.State.COMPLETE;
import ImportContext.State.PENDING;
import ImportTask.State.READY;
import java.io.File;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.junit.Assert;
import org.junit.Test;


public abstract class ImporterDbTestBase extends ImporterDbTestSupport {
    @Test
    public void testDirectImport() throws Exception {
        Database db = new Database(getConnectionParams());
        ImportContext context = importer.createContext(db);
        Assert.assertEquals(PENDING, context.getState());
        Assert.assertEquals(1, context.getTasks().size());
        importer.run(context);
        runChecks(("gs:" + (tableName("widgets"))));
    }

    @Test
    public void testIndirectToShapefile() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        ImportContext context = importer.createContext(new Directory(dir));
        importer.run(context);
        runChecks("gs:archsites");
        runChecks("gs:bugsites");
        DataStoreInfo store = ((DataStoreInfo) (context.getTasks().get(0).getStore()));
        Assert.assertNotNull(store);
        Assert.assertEquals(2, getCatalog().getFeatureTypesByDataStore(store).size());
        context = importer.createContext(new Database(getConnectionParams()), store);
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertEquals(READY, task.getState());
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        Assert.assertEquals(3, getCatalog().getFeatureTypesByDataStore(store).size());
        runChecks(("gs:" + (tableName("widgets"))));
    }

    @Test
    public void testIndirectToDb() throws Exception {
        Catalog cat = getCatalog();
        DataStoreInfo ds = cat.getFactory().createDataStore();
        ds.setName("oracle");
        ds.setWorkspace(cat.getDefaultWorkspace());
        ds.setEnabled(true);
        ds.getConnectionParameters().putAll(getConnectionParams());
        cat.add(ds);
        Assert.assertEquals(0, cat.getFeatureTypesByDataStore(ds).size());
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        ImportContext context = importer.createContext(new Directory(dir), ds);
        Assert.assertEquals(2, context.getTasks().size());
        Assert.assertEquals(READY, context.getTasks().get(0).getState());
        Assert.assertEquals(READY, context.getTasks().get(1).getState());
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        Assert.assertEquals(2, cat.getFeatureTypesByDataStore(ds).size());
        runChecks(("gs:" + (tableName("archsites"))));
        runChecks(("gs:" + (tableName("bugsites"))));
    }
}

