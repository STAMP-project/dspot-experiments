package com.orientechnologies.orient.core.metadata;


import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OImmutableSchema;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.storage.cache.OWriteCache;
import com.orientechnologies.orient.core.storage.cluster.OPaginatedCluster;
import com.orientechnologies.orient.core.storage.impl.local.OAbstractPaginatedStorage;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class ClassTest {
    public static final String SHORTNAME_CLASS_NAME = "TestShortName";

    private static ODatabaseDocumentTx db = null;

    @Test
    public void testShortName() {
        OSchema schema = ClassTest.db.getMetadata().getSchema();
        OClass oClass = schema.createClass(ClassTest.SHORTNAME_CLASS_NAME);
        Assert.assertNull(oClass.getShortName());
        Assert.assertNull(queryShortName());
        final OStorage storage = ClassTest.db.getStorage();
        if (storage instanceof OAbstractPaginatedStorage) {
            final OAbstractPaginatedStorage paginatedStorage = ((OAbstractPaginatedStorage) (storage));
            final OWriteCache writeCache = paginatedStorage.getWriteCache();
            Assert.assertTrue(writeCache.exists(((ClassTest.SHORTNAME_CLASS_NAME.toLowerCase(Locale.ENGLISH)) + (OPaginatedCluster.DEF_EXTENSION))));
        }
        String shortName = "shortname";
        oClass.setShortName(shortName);
        Assert.assertEquals(shortName, oClass.getShortName());
        Assert.assertEquals(shortName, queryShortName());
        // FAILS, saves null value and stores "null" string (not null value) internally
        shortName = "null";
        oClass.setShortName(shortName);
        Assert.assertEquals(shortName, oClass.getShortName());
        Assert.assertEquals(shortName, queryShortName());
        oClass.setShortName(null);
        Assert.assertNull(oClass.getShortName());
        Assert.assertNull(queryShortName());
        oClass.setShortName("");
        Assert.assertNull(oClass.getShortName());
        Assert.assertNull(queryShortName());
    }

    @Test
    public void testShortNameSnapshot() {
        OSchema schema = ClassTest.db.getMetadata().getSchema();
        OClass oClass = schema.createClass(ClassTest.SHORTNAME_CLASS_NAME);
        Assert.assertNull(oClass.getShortName());
        String shortName = "shortName";
        oClass.setShortName(shortName);
        Assert.assertEquals(shortName, oClass.getShortName());
        OClass shorted = schema.getClass(shortName);
        Assert.assertNotNull(shorted);
        Assert.assertEquals(shortName, shorted.getShortName());
        OMetadataInternal intern = ClassTest.db.getMetadata();
        OImmutableSchema immSchema = intern.getImmutableSchemaSnapshot();
        shorted = immSchema.getClass(shortName);
        Assert.assertNotNull(shorted);
        Assert.assertEquals(shortName, shorted.getShortName());
    }

    @Test
    public void testRename() {
        OSchema schema = ClassTest.db.getMetadata().getSchema();
        OClass oClass = schema.createClass("ClassName");
        final OStorage storage = ClassTest.db.getStorage();
        final OAbstractPaginatedStorage paginatedStorage = ((OAbstractPaginatedStorage) (storage));
        final OWriteCache writeCache = paginatedStorage.getWriteCache();
        Assert.assertTrue(writeCache.exists(("classname" + (OPaginatedCluster.DEF_EXTENSION))));
        oClass.setName("ClassNameNew");
        Assert.assertTrue((!(writeCache.exists(("classname" + (OPaginatedCluster.DEF_EXTENSION))))));
        Assert.assertTrue(writeCache.exists(("classnamenew" + (OPaginatedCluster.DEF_EXTENSION))));
        oClass.setName("ClassName");
        Assert.assertTrue((!(writeCache.exists(("classnamenew" + (OPaginatedCluster.DEF_EXTENSION))))));
        Assert.assertTrue(writeCache.exists(("classname" + (OPaginatedCluster.DEF_EXTENSION))));
    }

    @Test
    public void testRenameClusterAlreadyExists() {
        OSchema schema = ClassTest.db.getMetadata().getSchema();
        OClass classOne = schema.createClass("ClassOne");
        OClass classTwo = schema.createClass("ClassTwo");
        final int clusterId = ClassTest.db.addCluster("classthree");
        classTwo.addClusterId(clusterId);
        ODocument document = new ODocument("ClassTwo");
        document.save("classthree");
        document = new ODocument("ClassTwo");
        document.save();
        document = new ODocument("ClassOne");
        document.save();
        Assert.assertEquals(ClassTest.db.countClass("ClassTwo"), 2);
        Assert.assertEquals(ClassTest.db.countClass("ClassOne"), 1);
        classOne.setName("ClassThree");
        final OStorage storage = ClassTest.db.getStorage();
        final OAbstractPaginatedStorage paginatedStorage = ((OAbstractPaginatedStorage) (storage));
        final OWriteCache writeCache = paginatedStorage.getWriteCache();
        Assert.assertTrue(writeCache.exists(("classone" + (OPaginatedCluster.DEF_EXTENSION))));
        Assert.assertEquals(ClassTest.db.countClass("ClassTwo"), 2);
        Assert.assertEquals(ClassTest.db.countClass("ClassThree"), 1);
        classOne.setName("ClassOne");
        Assert.assertTrue(writeCache.exists(("classone" + (OPaginatedCluster.DEF_EXTENSION))));
        Assert.assertEquals(ClassTest.db.countClass("ClassTwo"), 2);
        Assert.assertEquals(ClassTest.db.countClass("ClassOne"), 1);
    }

    @Test
    public void testOClassAndOPropertyDescription() {
        final OSchema oSchema = ClassTest.db.getMetadata().getSchema();
        OClass oClass = oSchema.createClass("DescriptionTest");
        OProperty oProperty = oClass.createProperty("property", STRING);
        oClass.setDescription("DescriptionTest-class-description");
        oProperty.setDescription("DescriptionTest-property-description");
        Assert.assertEquals(oClass.getDescription(), "DescriptionTest-class-description");
        Assert.assertEquals(oProperty.getDescription(), "DescriptionTest-property-description");
        oSchema.reload();
        oClass = oSchema.getClass("DescriptionTest");
        oProperty = oClass.getProperty("property");
        Assert.assertEquals(oClass.getDescription(), "DescriptionTest-class-description");
        Assert.assertEquals(oProperty.getDescription(), "DescriptionTest-property-description");
        oClass = ClassTest.db.getMetadata().getImmutableSchemaSnapshot().getClass("DescriptionTest");
        oProperty = oClass.getProperty("property");
        Assert.assertEquals(oClass.getDescription(), "DescriptionTest-class-description");
        Assert.assertEquals(oProperty.getDescription(), "DescriptionTest-property-description");
    }
}

