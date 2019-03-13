package com.orientechnologies.orient.core.metadata.schema;


import OType.ANY;
import OType.DECIMAL;
import OType.DOUBLE;
import OType.EMBEDDEDLIST;
import OType.EMBEDDEDMAP;
import OType.EMBEDDEDSET;
import OType.FLOAT;
import OType.INTEGER;
import OType.LINKLIST;
import OType.LINKMAP;
import OType.LINKSET;
import OType.LONG;
import OType.SHORT;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


public class OClassImplTest {
    private ODatabaseDocumentTx db;

    /**
     * If class was not abstract and we call {@code setAbstract(false)} clusters should not be changed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSetAbstractClusterNotChanged() throws Exception {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test1");
        final int oldClusterId = oClass.getDefaultClusterId();
        oClass.setAbstract(false);
        Assert.assertEquals(oClass.getDefaultClusterId(), oldClusterId);
    }

    /**
     * If class was abstract and we call {@code setAbstract(false)} a new non default cluster should be created.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSetAbstractShouldCreateNewClusters() throws Exception {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createAbstractClass("Test2");
        oClass.setAbstract(false);
        Assert.assertFalse(((oClass.getDefaultClusterId()) == (-1)));
        Assert.assertFalse(((oClass.getDefaultClusterId()) == (db.getDefaultClusterId())));
    }

    @Test
    public void testCreateNoLinkedClass() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test21");
        oClass.createProperty("some", LINKLIST, ((OClass) (null)));
        oClass.createProperty("some2", LINKLIST, ((OClass) (null)), true);
        Assert.assertNotNull(oClass.getProperty("some"));
        Assert.assertNotNull(oClass.getProperty("some2"));
    }

    @Test(expected = OSchemaException.class)
    public void testCreatePropertyFailOnExistingData() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test3");
        ODocument document = new ODocument("Test3");
        document.field("some", "String");
        db.save(document);
        db.commit();
        oClass.createProperty("some", INTEGER);
    }

    @Test(expected = OSchemaException.class)
    public void testCreatePropertyFailOnExistingDataLinkList() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test4");
        ODocument document = new ODocument("Test4");
        ArrayList<ODocument> list = new ArrayList<ODocument>();
        list.add(new ODocument("Test4"));
        document.field("some", list);
        db.save(document);
        db.commit();
        oClass.createProperty("some", EMBEDDEDLIST);
    }

    @Test(expected = OSchemaException.class)
    public void testCreatePropertyFailOnExistingDataLinkSet() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test5");
        ODocument document = new ODocument("Test5");
        Set<ODocument> list = new HashSet<ODocument>();
        list.add(new ODocument("Test5"));
        document.field("somelinkset", list);
        db.save(document);
        db.commit();
        oClass.createProperty("somelinkset", EMBEDDEDSET);
    }

    @Test(expected = OSchemaException.class)
    public void testCreatePropertyFailOnExistingDataEmbeddetSet() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test6");
        ODocument document = new ODocument("Test6");
        Set<ODocument> list = new HashSet<ODocument>();
        list.add(new ODocument("Test6"));
        document.field("someembededset", list, EMBEDDEDSET);
        db.save(document);
        db.commit();
        oClass.createProperty("someembededset", LINKSET);
    }

    @Test(expected = OSchemaException.class)
    public void testCreatePropertyFailOnExistingDataEmbeddedList() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test7");
        ODocument document = new ODocument("Test7");
        List<ODocument> list = new ArrayList<ODocument>();
        list.add(new ODocument("Test7"));
        document.field("someembeddedlist", list, EMBEDDEDLIST);
        db.save(document);
        db.commit();
        oClass.createProperty("someembeddedlist", LINKLIST);
    }

    @Test(expected = OSchemaException.class)
    public void testCreatePropertyFailOnExistingDataEmbeddedMap() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test8");
        ODocument document = new ODocument("Test8");
        Map<String, ODocument> map = new HashMap<String, ODocument>();
        map.put("test", new ODocument("Test8"));
        document.field("someembededmap", map, EMBEDDEDMAP);
        db.save(document);
        db.commit();
        oClass.createProperty("someembededmap", LINKMAP);
    }

    @Test(expected = OSchemaException.class)
    public void testCreatePropertyFailOnExistingDataLinkMap() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test9");
        ODocument document = new ODocument("Test9");
        Map<String, ODocument> map = new HashMap<String, ODocument>();
        map.put("test", new ODocument("Test8"));
        document.field("somelinkmap", map, LINKMAP);
        db.save(document);
        db.commit();
        oClass.createProperty("somelinkmap", EMBEDDEDMAP);
    }

    @Test
    public void testCreatePropertyCastable() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test10");
        ODocument document = new ODocument("Test10");
        // TODO add boolan and byte
        document.field("test1", ((short) (1)));
        document.field("test2", 1);
        document.field("test3", 4L);
        document.field("test4", 3.0F);
        document.field("test5", 3.0);
        document.field("test6", 4);
        db.save(document);
        db.commit();
        oClass.createProperty("test1", INTEGER);
        oClass.createProperty("test2", LONG);
        oClass.createProperty("test3", DOUBLE);
        oClass.createProperty("test4", DOUBLE);
        oClass.createProperty("test5", DECIMAL);
        oClass.createProperty("test6", FLOAT);
        ODocument doc1 = db.load(document.getIdentity());
        Assert.assertEquals(doc1.fieldType("test1"), INTEGER);
        Assert.assertTrue(((doc1.field("test1")) instanceof Integer));
        Assert.assertEquals(doc1.fieldType("test2"), LONG);
        Assert.assertTrue(((doc1.field("test2")) instanceof Long));
        Assert.assertEquals(doc1.fieldType("test3"), DOUBLE);
        Assert.assertTrue(((doc1.field("test3")) instanceof Double));
        Assert.assertEquals(doc1.fieldType("test4"), DOUBLE);
        Assert.assertTrue(((doc1.field("test4")) instanceof Double));
        Assert.assertEquals(doc1.fieldType("test5"), DECIMAL);
        Assert.assertTrue(((doc1.field("test5")) instanceof BigDecimal));
        Assert.assertEquals(doc1.fieldType("test6"), FLOAT);
        Assert.assertTrue(((doc1.field("test6")) instanceof Float));
    }

    @Test
    public void testCreatePropertyCastableColection() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test11");
        ODocument document = new ODocument("Test11");
        document.field("test1", new ArrayList<ODocument>(), EMBEDDEDLIST);
        document.field("test2", new ArrayList<ODocument>(), LINKLIST);
        document.field("test3", new HashSet<ODocument>(), EMBEDDEDSET);
        document.field("test4", new HashSet<ODocument>(), LINKSET);
        document.field("test5", new HashMap<String, ODocument>(), EMBEDDEDMAP);
        document.field("test6", new HashMap<String, ODocument>(), LINKMAP);
        db.save(document);
        db.commit();
        oClass.createProperty("test1", LINKLIST);
        oClass.createProperty("test2", EMBEDDEDLIST);
        oClass.createProperty("test3", LINKSET);
        oClass.createProperty("test4", EMBEDDEDSET);
        oClass.createProperty("test5", LINKMAP);
        oClass.createProperty("test6", EMBEDDEDMAP);
        ODocument doc1 = db.load(document.getIdentity());
        Assert.assertEquals(doc1.fieldType("test1"), LINKLIST);
        Assert.assertEquals(doc1.fieldType("test2"), EMBEDDEDLIST);
        Assert.assertEquals(doc1.fieldType("test3"), LINKSET);
        Assert.assertEquals(doc1.fieldType("test4"), EMBEDDEDSET);
        Assert.assertEquals(doc1.fieldType("test5"), LINKMAP);
        Assert.assertEquals(doc1.fieldType("test6"), EMBEDDEDMAP);
    }

    @Test
    public void testCreatePropertyIdKeep() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test12");
        OProperty prop = oClass.createProperty("test2", STRING);
        Integer id = prop.getId();
        oClass.dropProperty("test2");
        prop = oClass.createProperty("test2", STRING);
        Assert.assertEquals(id, prop.getId());
    }

    @Test
    public void testRenameProperty() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test13");
        OProperty prop = oClass.createProperty("test1", STRING);
        Integer id = prop.getId();
        prop.setName("test2");
        Assert.assertNotEquals(id, prop.getId());
    }

    @Test
    public void testChangeTypeProperty() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test14");
        OProperty prop = oClass.createProperty("test1", SHORT);
        Integer id = prop.getId();
        prop.setType(INTEGER);
        Assert.assertNotEquals(id, prop.getId());
    }

    @Test
    public void testRenameBackProperty() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test15");
        OProperty prop = oClass.createProperty("test1", STRING);
        Integer id = prop.getId();
        prop.setName("test2");
        Assert.assertNotEquals(id, prop.getId());
        prop.setName("test1");
        Assert.assertEquals(id, prop.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUncastableType() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test16");
        OProperty prop = oClass.createProperty("test1", STRING);
        prop.setType(INTEGER);
    }

    @Test
    public void testFindById() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test17");
        OProperty prop = oClass.createProperty("testaaa", STRING);
        OGlobalProperty global = oSchema.getGlobalPropertyById(prop.getId());
        Assert.assertEquals(prop.getId(), global.getId());
        Assert.assertEquals(prop.getName(), global.getName());
        Assert.assertEquals(prop.getType(), global.getType());
    }

    @Test
    public void testFindByIdDrop() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test18");
        OProperty prop = oClass.createProperty("testaaa", STRING);
        Integer id = prop.getId();
        oClass.dropProperty("testaaa");
        OGlobalProperty global = oSchema.getGlobalPropertyById(id);
        Assert.assertEquals(id, global.getId());
        Assert.assertEquals("testaaa", global.getName());
        Assert.assertEquals(STRING, global.getType());
    }

    @Test
    public void testChangePropertyTypeCastable() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test19");
        oClass.createProperty("test1", SHORT);
        oClass.createProperty("test2", INTEGER);
        oClass.createProperty("test3", LONG);
        oClass.createProperty("test4", FLOAT);
        oClass.createProperty("test5", DOUBLE);
        oClass.createProperty("test6", INTEGER);
        ODocument document = new ODocument("Test19");
        // TODO add boolean and byte
        document.field("test1", ((short) (1)));
        document.field("test2", 1);
        document.field("test3", 4L);
        document.field("test4", 3.0F);
        document.field("test5", 3.0);
        document.field("test6", 4);
        db.save(document);
        db.commit();
        oClass.getProperty("test1").setType(INTEGER);
        oClass.getProperty("test2").setType(LONG);
        oClass.getProperty("test3").setType(DOUBLE);
        oClass.getProperty("test4").setType(DOUBLE);
        oClass.getProperty("test5").setType(DECIMAL);
        oClass.getProperty("test6").setType(FLOAT);
        ODocument doc1 = db.load(document.getIdentity());
        Assert.assertEquals(doc1.fieldType("test1"), INTEGER);
        Assert.assertTrue(((doc1.field("test1")) instanceof Integer));
        Assert.assertEquals(doc1.fieldType("test2"), LONG);
        Assert.assertTrue(((doc1.field("test2")) instanceof Long));
        Assert.assertEquals(doc1.fieldType("test3"), DOUBLE);
        Assert.assertTrue(((doc1.field("test3")) instanceof Double));
        Assert.assertEquals(doc1.fieldType("test4"), DOUBLE);
        Assert.assertTrue(((doc1.field("test4")) instanceof Double));
        Assert.assertEquals(doc1.fieldType("test5"), DECIMAL);
        Assert.assertTrue(((doc1.field("test5")) instanceof BigDecimal));
        Assert.assertEquals(doc1.fieldType("test6"), FLOAT);
        Assert.assertTrue(((doc1.field("test6")) instanceof Float));
    }

    @Test
    public void testChangePropertyName() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test20");
        oClass.createProperty("test1", SHORT);
        oClass.createProperty("test2", INTEGER);
        oClass.createProperty("test3", LONG);
        oClass.createProperty("test4", FLOAT);
        oClass.createProperty("test5", DOUBLE);
        oClass.createProperty("test6", INTEGER);
        ODocument document = new ODocument("Test20");
        // TODO add boolan and byte
        document.field("test1", ((short) (1)));
        document.field("test2", 1);
        document.field("test3", 4L);
        document.field("test4", 3.0F);
        document.field("test5", 3.0);
        document.field("test6", 4);
        db.save(document);
        db.commit();
        oClass.getProperty("test1").setName("test1a");
        oClass.getProperty("test2").setName("test2a");
        oClass.getProperty("test3").setName("test3a");
        oClass.getProperty("test4").setName("test4a");
        oClass.getProperty("test5").setName("test5a");
        oClass.getProperty("test6").setName("test6a");
        ODocument doc1 = db.load(document.getIdentity());
        Assert.assertEquals(doc1.fieldType("test1a"), SHORT);
        Assert.assertTrue(((doc1.field("test1a")) instanceof Short));
        Assert.assertEquals(doc1.fieldType("test2a"), INTEGER);
        Assert.assertTrue(((doc1.field("test2a")) instanceof Integer));
        Assert.assertEquals(doc1.fieldType("test3a"), LONG);
        Assert.assertTrue(((doc1.field("test3a")) instanceof Long));
        Assert.assertEquals(doc1.fieldType("test4a"), FLOAT);
        Assert.assertTrue(((doc1.field("test4a")) instanceof Float));
        Assert.assertEquals(doc1.fieldType("test5a"), DOUBLE);
        Assert.assertTrue(((doc1.field("test5")) instanceof Double));
        Assert.assertEquals(doc1.fieldType("test6a"), INTEGER);
        Assert.assertTrue(((doc1.field("test6a")) instanceof Integer));
    }

    @Test
    public void testCreatePropertyCastableColectionNoCache() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("Test11bis");
        final ODocument document = new ODocument("Test11bis");
        document.field("test1", new ArrayList<ODocument>(), EMBEDDEDLIST);
        document.field("test2", new ArrayList<ODocument>(), LINKLIST);
        document.field("test3", new HashSet<ODocument>(), EMBEDDEDSET);
        document.field("test4", new HashSet<ODocument>(), LINKSET);
        document.field("test5", new HashMap<String, ODocument>(), EMBEDDEDMAP);
        document.field("test6", new HashMap<String, ODocument>(), LINKMAP);
        db.save(document);
        db.commit();
        oClass.createProperty("test1", LINKLIST);
        oClass.createProperty("test2", EMBEDDEDLIST);
        oClass.createProperty("test3", LINKSET);
        oClass.createProperty("test4", EMBEDDEDSET);
        oClass.createProperty("test5", LINKMAP);
        oClass.createProperty("test6", EMBEDDEDMAP);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ODocument> future = executor.submit(new Callable<ODocument>() {
            @Override
            public ODocument call() throws Exception {
                ODocument doc1 = db.copy().load(document.getIdentity());
                Assert.assertEquals(doc1.fieldType("test1"), LINKLIST);
                Assert.assertEquals(doc1.fieldType("test2"), EMBEDDEDLIST);
                Assert.assertEquals(doc1.fieldType("test3"), LINKSET);
                Assert.assertEquals(doc1.fieldType("test4"), EMBEDDEDSET);
                Assert.assertEquals(doc1.fieldType("test5"), LINKMAP);
                Assert.assertEquals(doc1.fieldType("test6"), EMBEDDEDMAP);
                return doc1;
            }
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            if ((e.getCause()) instanceof AssertionError) {
                throw ((AssertionError) (e.getCause()));
            }
        }
        executor.shutdown();
    }

    @Test
    public void testClassNameSyntax() {
        final OSchema oSchema = db.getMetadata().getSchema();
        Assert.assertNotNull(oSchema.createClass("OClassImplTesttestClassNameSyntax"));
        Assert.assertNotNull(oSchema.createClass("_OClassImplTesttestClassNameSyntax"));
        Assert.assertNotNull(oSchema.createClass("_OClassImplTesttestClassNameSyntax_"));
        Assert.assertNotNull(oSchema.createClass("_OClassImplTestte_stClassNameSyntax_"));
        Assert.assertNotNull(oSchema.createClass("_OClassImplTesttestClassNameSyntax_1"));
        Assert.assertNotNull(oSchema.createClass("_OClassImplTesttestClassNameSyntax_12"));
        Assert.assertNotNull(oSchema.createClass("_OClassImplTesttestCla23ssNameSyntax_12"));
        Assert.assertNotNull(oSchema.createClass("$OClassImplTesttestCla23ssNameSyntax_12"));
        Assert.assertNotNull(oSchema.createClass("OClassImplTesttestC$la23ssNameSyntax_12"));
        Assert.assertNotNull(oSchema.createClass("oOClassImplTesttestC$la23ssNameSyntax_12"));
        String[] validClassNamesSince30 = new String[]{ "foo bar", "12", "#12", "12AAA", ",asdfasdf", "adsf,asdf", "asdf.sadf", ".asdf", "asdfaf.", "asdf:asdf" };
        for (String s : validClassNamesSince30) {
            Assert.assertNotNull(oSchema.createClass(s));
        }
    }

    @Test
    public void testTypeAny() {
        String className = "testTypeAny";
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass(className);
        ODocument record = db.newInstance(className);
        record.field("name", "foo");
        record.save();
        oClass.createProperty("name", ANY);
        List<?> result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>((("select from " + className) + " where name = 'foo'")));
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    public void testAlterCustomAttributeInClass() {
        OSchema schema = db.getMetadata().getSchema();
        OClass oClass = schema.createClass("TestCreateCustomAttributeClass");
        oClass.setCustom("customAttribute", "value1");
        Assert.assertEquals("value1", oClass.getCustom("customAttribute"));
        oClass.setCustom("custom.attribute", "value2");
        Assert.assertEquals("value2", oClass.getCustom("custom.attribute"));
    }
}

