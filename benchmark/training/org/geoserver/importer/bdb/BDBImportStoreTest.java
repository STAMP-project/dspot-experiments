/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.bdb;


import ImportContext.State.COMPLETE;
import ImportContext.State.PENDING;
import java.io.File;
import java.util.Iterator;
import org.geoserver.importer.Directory;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImportStore.ImportVisitor;
import org.geoserver.importer.Importer;
import org.geoserver.importer.ImporterTestSupport;
import org.geoserver.importer.RemoteData;
import org.geoserver.importer.bdb.BDBImportStore.BindingType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class BDBImportStoreTest extends ImporterTestSupport {
    BDBImportStore store;

    File dbRoot;

    private BindingType bindingType;

    public BDBImportStoreTest(String name, BindingType bindingType) {
        this.bindingType = bindingType;
    }

    // in order to test this, run once, then change the serialVersionUID of ImportContext2
    @Test
    public void testSerialVersionUIDChange() throws Exception {
        Importer imp = new Importer(null) {
            @Override
            public File getImportRoot() {
                File root = new File("target");
                root.mkdirs();
                return root;
            }
        };
        ImportContext ctx = new BDBImportStoreTest.ImportContext2();
        ctx.setState(PENDING);
        ctx.setUser("fooboo");
        BDBImportStore store = new BDBImportStore(imp);
        try {
            store.init();
            store.add(ctx);
            Iterator<ImportContext> iterator = store.iterator();
            while (iterator.hasNext()) {
                ctx = iterator.next();
                Assert.assertEquals("fooboo", ctx.getUser());
            } 
            store.add(ctx);
        } finally {
            store.destroy();
        }
    }

    public static class ImportContext2 extends ImportContext {
        private static final long serialVersionUID = 12345;
    }

    @Test
    public void testAdd() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new Directory(dir));
        Assert.assertEquals(1, context.getTasks().size());
        for (int i = 0; i < (context.getTasks().size()); i++) {
            Assert.assertNotNull(context.getTasks().get(i).getStore());
            Assert.assertNotNull(context.getTasks().get(i).getStore().getCatalog());
        }
        // @todo commented these out as importer.createContext adds to the store
        // assertNull(context.getId());
        CountingVisitor cv = new CountingVisitor();
        // store.query(cv);
        // assertEquals(0, cv.getCount());
        store.add(context);
        Assert.assertNotNull(context.getId());
        Assert.assertNotNull(context.getTasks().get(0).getLayer());
        ImportContext context2 = store.get(context.getId());
        Assert.assertNotNull(context2);
        Assert.assertEquals(context.getId(), context2.getId());
        store.query(cv);
        Assert.assertEquals(1, cv.getCount());
        BDBImportStoreTest.SearchingVisitor sv = new BDBImportStoreTest.SearchingVisitor(context.getId());
        store.query(sv);
        Assert.assertTrue(sv.isFound());
        importer.reattach(context2);
        // ensure various transient bits are set correctly on deserialization
        Assert.assertEquals(1, context2.getTasks().size());
        for (int i = 0; i < (context2.getTasks().size()); i++) {
            Assert.assertNotNull(context2.getTasks().get(i).getStore());
            Assert.assertNotNull(context2.getTasks().get(i).getStore().getCatalog());
        }
        Assert.assertNotNull(context2.getTasks().get(0).getLayer());
    }

    @Test
    public void testSaveRemoteData() throws Exception {
        ImportContext context = importer.registerContext(null);
        RemoteData data = new RemoteData("ftp://geoserver.org");
        data.setUsername("geoserver");
        data.setPassword("gisIsCool");
        context.setData(data);
        store.add(context);
        Assert.assertNotNull(context.getId());
        ImportContext context2 = store.get(context.getId());
        Assert.assertEquals(data, context2.getData());
    }

    @Test
    public void testSave() throws Exception {
        testAdd();
        ImportContext context = store.get(0);
        Assert.assertNotNull(context);
        Assert.assertEquals(PENDING, context.getState());
        context.setState(COMPLETE);
        ImportContext context2 = store.get(0);
        Assert.assertNotNull(context2);
        Assert.assertEquals(PENDING, context2.getState());
        store.save(context);
        context2 = store.get(0);
        Assert.assertNotNull(context2);
        Assert.assertEquals(COMPLETE, context2.getState());
    }

    @Test
    public void testIDManagement() throws Exception {
        // verify base - first one is zero
        ImportContext zero = new ImportContext();
        store.add(zero);
        Assert.assertEquals(Long.valueOf(0), zero.getId());
        // try for zero again (less than current case - client out of sync)
        Long advanceId = store.advanceId(0L);
        Assert.assertEquals(Long.valueOf(1), advanceId);
        // and again for current (equals current case - normal mode)
        advanceId = store.advanceId(2L);
        Assert.assertEquals(Long.valueOf(2), advanceId);
        // now jump ahead (client advances case - server out of sync)
        advanceId = store.advanceId(666L);
        Assert.assertEquals(Long.valueOf(666), advanceId);
        // the next created import should be one higher
        ImportContext dumby = new ImportContext();
        store.add(dumby);
        Assert.assertEquals(Long.valueOf(667), dumby.getId());
    }

    class SearchingVisitor implements ImportVisitor {
        long id;

        boolean found = false;

        SearchingVisitor(long id) {
            this.id = id;
        }

        public void visit(ImportContext context) {
            if ((context.getId().longValue()) == (id)) {
                found = true;
            }
        }

        public boolean isFound() {
            return found;
        }
    }
}

