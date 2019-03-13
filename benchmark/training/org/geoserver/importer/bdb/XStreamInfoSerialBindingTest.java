/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.bdb;


import com.sleepycat.je.DatabaseEntry;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.importer.Directory;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImportTask;
import org.geoserver.importer.ImporterTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class XStreamInfoSerialBindingTest extends ImporterTestSupport {
    @Test
    public void testSerializeWithNewStore() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new Directory(dir));
        XStreamPersister xp = importer.createXStreamPersisterXML();
        XStreamInfoSerialBinding<ImportContext> binding = new XStreamInfoSerialBinding<ImportContext>(xp, ImportContext.class);
        binding.setCompress(false);
        DatabaseEntry e = new DatabaseEntry();
        binding.objectToEntry(context, e);
        Document dom = dom(new ByteArrayInputStream(e.getData(), 0, e.getSize()));
        print(dom);
        XMLAssert.assertXpathExists("/import", dom);
        print(dom);
        // workspace referenced by id
        XMLAssert.assertXpathExists("/import/targetWorkspace/id", dom);
        // store inline
        XMLAssert.assertXpathExists("/import/tasks/task[position()=1]/store/name", dom);
        XMLAssert.assertXpathNotExists("/import/tasks/task[position()=1]/store/id", dom);
        ImportContext context2 = binding.entryToObject(e);
        Assert.assertNotNull(context2.getTargetWorkspace());
        Assert.assertNotNull(context2.getTargetWorkspace().getId());
        Assert.assertNotNull(context2.getTargetWorkspace().getName());
        ImportTask task = context2.getTasks().get(0);
        Assert.assertNotNull(task.getStore());
        Assert.assertNull(task.getStore().getId());
        Assert.assertNotNull(task.getStore().getName());
        Assert.assertNotNull(task.getLayer());
        // assertNotNull(item.getLayer().getResource());
    }

    @Test
    public void testSerialize2() throws Exception {
        Catalog cat = getCatalog();
        DataStoreInfo ds = cat.getFactory().createDataStore();
        ds.setWorkspace(cat.getDefaultWorkspace());
        ds.setName("spearfish");
        ds.setType("H2");
        Map params = new HashMap();
        params.put("database", ((getTestData().getDataDirectoryRoot().getPath()) + "/spearfish"));
        params.put("dbtype", "h2");
        ds.getConnectionParameters().putAll(params);
        ds.setEnabled(true);
        cat.add(ds);
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        ds = cat.getDataStore(ds.getId());
        ImportContext context = importer.createContext(new Directory(dir), ds);
        Assert.assertEquals(2, context.getTasks().size());
        XStreamPersister xp = new XStreamPersisterFactory().createXMLPersister();
        xp.getXStream().omitField(ImportTask.class, "context");
        XStreamInfoSerialBinding<ImportContext> binding = new XStreamInfoSerialBinding<ImportContext>(xp, ImportContext.class);
        binding.setCompress(false);
        DatabaseEntry e = new DatabaseEntry();
        binding.objectToEntry(context, e);
    }
}

