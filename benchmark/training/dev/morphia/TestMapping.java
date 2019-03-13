/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package dev.morphia;


import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import dev.morphia.annotations.AlsoLoad;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Serialized;
import dev.morphia.mapping.DefaultCreator;
import dev.morphia.mapping.MappingException;
import dev.morphia.mapping.cache.DefaultEntityCache;
import dev.morphia.query.FindOptions;
import dev.morphia.testmodel.Article;
import dev.morphia.testmodel.Circle;
import dev.morphia.testmodel.Hotel;
import dev.morphia.testmodel.Rectangle;
import dev.morphia.testmodel.RecursiveChild;
import dev.morphia.testmodel.RecursiveParent;
import dev.morphia.testmodel.Translation;
import dev.morphia.testmodel.TravelAgency;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 */
public class TestMapping extends TestBase {
    @Test
    public void testAlsoLoad() {
        final TestMapping.ContainsIntegerList cil = new TestMapping.ContainsIntegerList();
        cil.intList.add(1);
        getDs().save(cil);
        final TestMapping.ContainsIntegerList cilLoaded = getDs().get(cil);
        Assert.assertNotNull(cilLoaded);
        Assert.assertNotNull(cilLoaded.intList);
        Assert.assertEquals(cilLoaded.intList.size(), cil.intList.size());
        Assert.assertEquals(cilLoaded.intList.get(0), cil.intList.get(0));
        final TestMapping.ContainsIntegerListNew cilNew = getDs().get(TestMapping.ContainsIntegerListNew.class, cil.id);
        Assert.assertNotNull(cilNew);
        Assert.assertNotNull(cilNew.integers);
        Assert.assertEquals(1, cilNew.integers.size());
        Assert.assertEquals(1, ((int) (cil.intList.get(0))));
    }

    @Test
    public void testBadMappings() {
        try {
            getMorphia().map(TestMapping.MissingId.class);
            Assert.fail("Validation: Missing @Id field not caught");
        } catch (MappingException e) {
            // good
        }
        try {
            getMorphia().map(TestMapping.IdOnEmbedded.class);
            Assert.fail("Validation: @Id field on @Embedded not caught");
        } catch (MappingException e) {
            // good
        }
        try {
            getMorphia().map(TestMapping.RenamedEmbedded.class);
            Assert.fail("Validation: @Embedded(\"name\") not caught on Class");
        } catch (MappingException e) {
            // good
        }
        try {
            getMorphia().map(TestMapping.MissingIdStill.class);
            Assert.fail("Validation: Missing @Id field not not caught");
        } catch (MappingException e) {
            // good
        }
        try {
            getMorphia().map(TestMapping.MissingIdRenamed.class);
            Assert.fail("Validation: Missing @Id field not not caught");
        } catch (MappingException e) {
            // good
        }
        try {
            getMorphia().map(TestMapping.NonStaticInnerClass.class);
            Assert.fail("Validation: Non-static inner class allowed");
        } catch (MappingException e) {
            // good
        }
    }

    @Test
    public void testBaseEntityValidity() {
        getMorphia().map(TestMapping.UsesBaseEntity.class);
    }

    @Test
    public void testBasicMapping() {
        performBasicMappingTest();
        final DefaultCreator objectFactory = ((DefaultCreator) (getMorphia().getMapper().getOptions().getObjectFactory()));
        Assert.assertTrue(objectFactory.getClassNameCache().isEmpty());
    }

    @Test
    public void testBasicMappingWithCachedClasses() {
        getMorphia().getMapper().getOptions().setCacheClassLookups(true);
        try {
            performBasicMappingTest();
            final DefaultCreator objectFactory = ((DefaultCreator) (getMorphia().getMapper().getOptions().getObjectFactory()));
            Assert.assertTrue(objectFactory.getClassNameCache().containsKey(Hotel.class.getName()));
            Assert.assertTrue(objectFactory.getClassNameCache().containsKey(TravelAgency.class.getName()));
        } finally {
            getMorphia().getMapper().getOptions().setCacheClassLookups(false);
        }
    }

    @Test
    public void testByteArrayMapping() {
        getMorphia().map(TestMapping.ContainsByteArray.class);
        final Key<TestMapping.ContainsByteArray> savedKey = getDs().save(new TestMapping.ContainsByteArray());
        final TestMapping.ContainsByteArray loaded = getDs().get(TestMapping.ContainsByteArray.class, savedKey.getId());
        Assert.assertEquals(new String(new TestMapping.ContainsByteArray().bytes), new String(loaded.bytes));
        Assert.assertNotNull(loaded.id);
    }

    @Test
    public void testCollectionMapping() {
        getMorphia().map(TestMapping.ContainsCollection.class);
        final Key<TestMapping.ContainsCollection> savedKey = getDs().save(new TestMapping.ContainsCollection());
        final TestMapping.ContainsCollection loaded = getDs().get(TestMapping.ContainsCollection.class, savedKey.getId());
        Assert.assertEquals(loaded.coll, new TestMapping.ContainsCollection().coll);
        Assert.assertNotNull(loaded.id);
    }

    @Test
    public void testDbRefMapping() {
        getMorphia().map(TestMapping.ContainsRef.class).map(Rectangle.class);
        final DBCollection stuff = getDb().getCollection("stuff");
        final DBCollection rectangles = getDb().getCollection("rectangles");
        Assert.assertTrue("'ne' field should not be persisted!", (!(getMorphia().getMapper().getMCMap().get(TestMapping.ContainsRef.class.getName()).containsJavaFieldName("ne"))));
        final Rectangle r = new Rectangle(1, 1);
        final DBObject rDbObject = getMorphia().toDBObject(r);
        rDbObject.put("_ns", rectangles.getName());
        rectangles.save(rDbObject);
        final TestMapping.ContainsRef cRef = new TestMapping.ContainsRef();
        cRef.rect = new DBRef(((String) (rDbObject.get("_ns"))), rDbObject.get("_id"));
        final DBObject cRefDbObject = getMorphia().toDBObject(cRef);
        stuff.save(cRefDbObject);
        final BasicDBObject cRefDbObjectLoaded = ((BasicDBObject) (stuff.findOne(BasicDBObjectBuilder.start("_id", cRefDbObject.get("_id")).get())));
        final TestMapping.ContainsRef cRefLoaded = getMorphia().fromDBObject(getDs(), TestMapping.ContainsRef.class, cRefDbObjectLoaded, new DefaultEntityCache());
        Assert.assertNotNull(cRefLoaded);
        Assert.assertNotNull(cRefLoaded.rect);
        Assert.assertNotNull(cRefLoaded.rect.getId());
        Assert.assertNotNull(cRefLoaded.rect.getCollectionName());
        Assert.assertEquals(cRefLoaded.rect.getId(), cRef.rect.getId());
        Assert.assertEquals(cRefLoaded.rect.getCollectionName(), cRef.rect.getCollectionName());
    }

    @Test
    public void testEmbeddedArrayElementHasNoClassname() {
        getMorphia().map(TestMapping.ContainsEmbeddedArray.class);
        final TestMapping.ContainsEmbeddedArray cea = new TestMapping.ContainsEmbeddedArray();
        cea.res = new TestMapping.RenamedEmbedded[]{ new TestMapping.RenamedEmbedded() };
        final DBObject dbObj = getMorphia().toDBObject(cea);
        Assert.assertTrue((!(((DBObject) (((List) (dbObj.get("res"))).get(0))).containsField(getMorphia().getMapper().getOptions().getDiscriminatorField()))));
    }

    @Test
    public void testEmbeddedDBObject() {
        getMorphia().map(TestMapping.ContainsDBObject.class);
        getDs().save(new TestMapping.ContainsDBObject());
        Assert.assertNotNull(getDs().find(TestMapping.ContainsDBObject.class).find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testEmbeddedEntity() {
        getMorphia().map(TestMapping.ContainsEmbeddedEntity.class);
        getDs().save(new TestMapping.ContainsEmbeddedEntity());
        final TestMapping.ContainsEmbeddedEntity ceeLoaded = getDs().find(TestMapping.ContainsEmbeddedEntity.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(ceeLoaded);
        Assert.assertNotNull(ceeLoaded.id);
        Assert.assertNotNull(ceeLoaded.cil);
        Assert.assertNull(ceeLoaded.cil.id);
    }

    @Test
    public void testEmbeddedEntityDBObjectHasNoClassname() {
        getMorphia().map(TestMapping.ContainsEmbeddedEntity.class);
        final TestMapping.ContainsEmbeddedEntity cee = new TestMapping.ContainsEmbeddedEntity();
        cee.cil = new TestMapping.ContainsIntegerList();
        cee.cil.intList = Collections.singletonList(1);
        final DBObject dbObj = getMorphia().toDBObject(cee);
        Assert.assertTrue((!(((DBObject) (dbObj.get("cil"))).containsField(getMorphia().getMapper().getOptions().getDiscriminatorField()))));
    }

    @Test
    public void testEnumKeyedMap() {
        final TestMapping.ContainsEnum1KeyMap map = new TestMapping.ContainsEnum1KeyMap();
        map.values.put(TestMapping.Enum1.A, "I'm a");
        map.values.put(TestMapping.Enum1.B, "I'm b");
        map.embeddedValues.put(TestMapping.Enum1.A, "I'm a");
        map.embeddedValues.put(TestMapping.Enum1.B, "I'm b");
        final Key<?> mapKey = getDs().save(map);
        final TestMapping.ContainsEnum1KeyMap mapLoaded = getDs().get(TestMapping.ContainsEnum1KeyMap.class, mapKey.getId());
        Assert.assertNotNull(mapLoaded);
        Assert.assertEquals(2, mapLoaded.values.size());
        Assert.assertNotNull(mapLoaded.values.get(TestMapping.Enum1.A));
        Assert.assertNotNull(mapLoaded.values.get(TestMapping.Enum1.B));
        Assert.assertEquals(2, mapLoaded.embeddedValues.size());
        Assert.assertNotNull(mapLoaded.embeddedValues.get(TestMapping.Enum1.A));
        Assert.assertNotNull(mapLoaded.embeddedValues.get(TestMapping.Enum1.B));
    }

    @Test
    public void testFinalField() {
        getMorphia().map(TestMapping.ContainsFinalField.class);
        final Key<TestMapping.ContainsFinalField> savedKey = getDs().save(new TestMapping.ContainsFinalField("blah"));
        final TestMapping.ContainsFinalField loaded = getDs().get(TestMapping.ContainsFinalField.class, savedKey.getId());
        Assert.assertNotNull(loaded);
        Assert.assertNotNull(loaded.name);
        Assert.assertEquals("blah", loaded.name);
    }

    @Test
    public void testFinalFieldNotPersisted() {
        getMorphia().getMapper().getOptions().setIgnoreFinals(true);
        getMorphia().map(TestMapping.ContainsFinalField.class);
        final Key<TestMapping.ContainsFinalField> savedKey = getDs().save(new TestMapping.ContainsFinalField("blah"));
        final TestMapping.ContainsFinalField loaded = getDs().get(TestMapping.ContainsFinalField.class, savedKey.getId());
        Assert.assertNotNull(loaded);
        Assert.assertNotNull(loaded.name);
        Assert.assertEquals("foo", loaded.name);
    }

    @Test
    public void testFinalIdField() {
        getMorphia().map(TestMapping.HasFinalFieldId.class);
        final Key<TestMapping.HasFinalFieldId> savedKey = getDs().save(new TestMapping.HasFinalFieldId(12));
        final TestMapping.HasFinalFieldId loaded = getDs().get(TestMapping.HasFinalFieldId.class, savedKey.getId());
        Assert.assertNotNull(loaded);
        Assert.assertNotNull(loaded.id);
        Assert.assertEquals(12, loaded.id);
    }

    @Test
    public void testIdFieldWithUnderscore() {
        getMorphia().map(TestMapping.StrangelyNamedIdField.class);
    }

    @Test
    public void testIntKeySetStringMap() {
        final TestMapping.ContainsIntKeySetStringMap map = new TestMapping.ContainsIntKeySetStringMap();
        map.values.put(1, Collections.singleton("I'm 1"));
        map.values.put(2, Collections.singleton("I'm 2"));
        final Key<?> mapKey = getDs().save(map);
        final TestMapping.ContainsIntKeySetStringMap mapLoaded = getDs().get(TestMapping.ContainsIntKeySetStringMap.class, mapKey.getId());
        Assert.assertNotNull(mapLoaded);
        Assert.assertEquals(2, mapLoaded.values.size());
        Assert.assertNotNull(mapLoaded.values.get(1));
        Assert.assertNotNull(mapLoaded.values.get(2));
        Assert.assertEquals(1, mapLoaded.values.get(1).size());
        Assert.assertNotNull(getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.2").exists());
        Assert.assertEquals(0, getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.2").doesNotExist().count());
        Assert.assertNotNull(getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.4").doesNotExist());
        Assert.assertEquals(0, getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.4").exists().count());
    }

    @Test
    public void testIntKeyedMap() {
        final TestMapping.ContainsIntKeyMap map = new TestMapping.ContainsIntKeyMap();
        map.values.put(1, "I'm 1");
        map.values.put(2, "I'm 2");
        final Key<?> mapKey = getDs().save(map);
        final TestMapping.ContainsIntKeyMap mapLoaded = getDs().get(TestMapping.ContainsIntKeyMap.class, mapKey.getId());
        Assert.assertNotNull(mapLoaded);
        Assert.assertEquals(2, mapLoaded.values.size());
        Assert.assertNotNull(mapLoaded.values.get(1));
        Assert.assertNotNull(mapLoaded.values.get(2));
        Assert.assertNotNull(getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.2").exists());
        Assert.assertEquals(0, getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.2").doesNotExist().count());
        Assert.assertNotNull(getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.4").doesNotExist());
        Assert.assertEquals(0, getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.4").exists().count());
    }

    @Test
    public void testIntLists() {
        TestMapping.ContainsIntegerList cil = new TestMapping.ContainsIntegerList();
        getDs().save(cil);
        TestMapping.ContainsIntegerList cilLoaded = getDs().get(cil);
        Assert.assertNotNull(cilLoaded);
        Assert.assertNotNull(cilLoaded.intList);
        Assert.assertEquals(cilLoaded.intList.size(), cil.intList.size());
        cil = new TestMapping.ContainsIntegerList();
        cil.intList = null;
        getDs().save(cil);
        cilLoaded = getDs().get(cil);
        Assert.assertNotNull(cilLoaded);
        Assert.assertNotNull(cilLoaded.intList);
        Assert.assertEquals(0, cilLoaded.intList.size());
        cil = new TestMapping.ContainsIntegerList();
        cil.intList.add(1);
        getDs().save(cil);
        cilLoaded = getDs().get(cil);
        Assert.assertNotNull(cilLoaded);
        Assert.assertNotNull(cilLoaded.intList);
        Assert.assertEquals(1, cilLoaded.intList.size());
        Assert.assertEquals(1, ((int) (cilLoaded.intList.get(0))));
    }

    @Test
    public void testLongArrayMapping() {
        getMorphia().map(TestMapping.ContainsLongAndStringArray.class);
        getDs().save(new TestMapping.ContainsLongAndStringArray());
        TestMapping.ContainsLongAndStringArray loaded = getDs().find(TestMapping.ContainsLongAndStringArray.class).find(new FindOptions().limit(1)).next();
        Assert.assertArrayEquals(loaded.longs, new TestMapping.ContainsLongAndStringArray().longs);
        Assert.assertArrayEquals(loaded.strings, new TestMapping.ContainsLongAndStringArray().strings);
        final TestMapping.ContainsLongAndStringArray array = new TestMapping.ContainsLongAndStringArray();
        array.strings = new String[]{ "a", "B", "c" };
        array.longs = new Long[]{ 4L, 5L, 4L };
        final Key<TestMapping.ContainsLongAndStringArray> k1 = getDs().save(array);
        loaded = getDs().getByKey(TestMapping.ContainsLongAndStringArray.class, k1);
        Assert.assertArrayEquals(loaded.longs, array.longs);
        Assert.assertArrayEquals(loaded.strings, array.strings);
        Assert.assertNotNull(loaded.id);
    }

    @Test
    public void testMapLike() {
        final TestMapping.ContainsMapLike ml = new TestMapping.ContainsMapLike();
        ml.m.put("first", "test");
        getDs().save(ml);
        final TestMapping.ContainsMapLike mlLoaded = getDs().find(TestMapping.ContainsMapLike.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(mlLoaded);
        Assert.assertNotNull(mlLoaded.m);
        Assert.assertNotNull(mlLoaded.m.containsKey("first"));
    }

    @Test
    public void testMapWithEmbeddedInterface() {
        final TestMapping.ContainsMapWithEmbeddedInterface aMap = new TestMapping.ContainsMapWithEmbeddedInterface();
        final TestMapping.Foo f1 = new TestMapping.Foo1();
        final TestMapping.Foo f2 = new TestMapping.Foo2();
        aMap.embeddedValues.put("first", f1);
        aMap.embeddedValues.put("second", f2);
        getDs().save(aMap);
        final TestMapping.ContainsMapWithEmbeddedInterface mapLoaded = getDs().find(TestMapping.ContainsMapWithEmbeddedInterface.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(mapLoaded);
        Assert.assertEquals(2, mapLoaded.embeddedValues.size());
        Assert.assertTrue(((mapLoaded.embeddedValues.get("first")) instanceof TestMapping.Foo1));
        Assert.assertTrue(((mapLoaded.embeddedValues.get("second")) instanceof TestMapping.Foo2));
    }

    @Test
    public void testMaps() {
        final DBCollection articles = getDb().getCollection("articles");
        getMorphia().map(Article.class).map(Translation.class).map(Circle.class);
        final Article related = new Article();
        final BasicDBObject relatedDbObj = ((BasicDBObject) (getMorphia().toDBObject(related)));
        articles.save(relatedDbObj);
        final Article relatedLoaded = getMorphia().fromDBObject(getDs(), Article.class, articles.findOne(new BasicDBObject("_id", relatedDbObj.get("_id"))), new DefaultEntityCache());
        final Article article = new Article();
        article.setTranslation("en", new Translation("Hello World", "Just a test"));
        article.setTranslation("is", new Translation("Hall? heimur", "Bara a? pr?fa"));
        article.setAttribute("myDate", new Date());
        article.setAttribute("myString", "Test");
        article.setAttribute("myInt", 123);
        article.putRelated("test", relatedLoaded);
        final BasicDBObject articleDbObj = ((BasicDBObject) (getMorphia().toDBObject(article)));
        articles.save(articleDbObj);
        final Article articleLoaded = getMorphia().fromDBObject(getDs(), Article.class, articles.findOne(new BasicDBObject("_id", articleDbObj.get("_id"))), new DefaultEntityCache());
        Assert.assertEquals(article.getTranslations().size(), articleLoaded.getTranslations().size());
        Assert.assertEquals(article.getTranslation("en").getTitle(), articleLoaded.getTranslation("en").getTitle());
        Assert.assertEquals(article.getTranslation("is").getBody(), articleLoaded.getTranslation("is").getBody());
        Assert.assertEquals(article.getAttributes().size(), articleLoaded.getAttributes().size());
        Assert.assertEquals(article.getAttribute("myDate"), articleLoaded.getAttribute("myDate"));
        Assert.assertEquals(article.getAttribute("myString"), articleLoaded.getAttribute("myString"));
        Assert.assertEquals(article.getAttribute("myInt"), articleLoaded.getAttribute("myInt"));
        Assert.assertEquals(article.getRelated().size(), articleLoaded.getRelated().size());
        Assert.assertEquals(article.getRelated("test").getId(), articleLoaded.getRelated("test").getId());
    }

    @Test
    public void testObjectIdKeyedMap() {
        getMorphia().map(TestMapping.ContainsObjectIdKeyMap.class);
        final TestMapping.ContainsObjectIdKeyMap map = new TestMapping.ContainsObjectIdKeyMap();
        final ObjectId o1 = new ObjectId("111111111111111111111111");
        final ObjectId o2 = new ObjectId("222222222222222222222222");
        map.values.put(o1, "I'm 1s");
        map.values.put(o2, "I'm 2s");
        final Key<?> mapKey = getDs().save(map);
        final TestMapping.ContainsObjectIdKeyMap mapLoaded = getDs().get(TestMapping.ContainsObjectIdKeyMap.class, mapKey.getId());
        Assert.assertNotNull(mapLoaded);
        Assert.assertEquals(2, mapLoaded.values.size());
        Assert.assertNotNull(mapLoaded.values.get(o1));
        Assert.assertNotNull(mapLoaded.values.get(o2));
        Assert.assertNotNull(getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.111111111111111111111111").exists());
        Assert.assertEquals(0, getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.111111111111111111111111").doesNotExist().count());
        Assert.assertNotNull(getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.4").doesNotExist());
        Assert.assertEquals(0, getDs().find(TestMapping.ContainsIntKeyMap.class).field("values.4").exists().count());
    }

    @Test
    public void testPrimMap() {
        final TestMapping.ContainsPrimitiveMap primMap = new TestMapping.ContainsPrimitiveMap();
        primMap.embeddedValues.put("first", 1L);
        primMap.embeddedValues.put("second", 2L);
        primMap.values.put("first", 1L);
        primMap.values.put("second", 2L);
        final Key<TestMapping.ContainsPrimitiveMap> primMapKey = getDs().save(primMap);
        final TestMapping.ContainsPrimitiveMap primMapLoaded = getDs().get(TestMapping.ContainsPrimitiveMap.class, primMapKey.getId());
        Assert.assertNotNull(primMapLoaded);
        Assert.assertEquals(2, primMapLoaded.embeddedValues.size());
        Assert.assertEquals(2, primMapLoaded.values.size());
    }

    @Test
    public void testPrimMapWithNullValue() {
        final TestMapping.ContainsPrimitiveMap primMap = new TestMapping.ContainsPrimitiveMap();
        primMap.embeddedValues.put("first", null);
        primMap.embeddedValues.put("second", 2L);
        primMap.values.put("first", null);
        primMap.values.put("second", 2L);
        final Key<TestMapping.ContainsPrimitiveMap> primMapKey = getDs().save(primMap);
        final TestMapping.ContainsPrimitiveMap primMapLoaded = getDs().get(TestMapping.ContainsPrimitiveMap.class, primMapKey.getId());
        Assert.assertNotNull(primMapLoaded);
        Assert.assertEquals(2, primMapLoaded.embeddedValues.size());
        Assert.assertEquals(2, primMapLoaded.values.size());
    }

    @Test
    public void testRecursiveReference() {
        final DBCollection stuff = getDb().getCollection("stuff");
        getMorphia().map(RecursiveParent.class).map(RecursiveChild.class);
        final RecursiveParent parent = new RecursiveParent();
        final DBObject parentDbObj = getMorphia().toDBObject(parent);
        stuff.save(parentDbObj);
        final RecursiveChild child = new RecursiveChild();
        final DBObject childDbObj = getMorphia().toDBObject(child);
        stuff.save(childDbObj);
        final RecursiveParent parentLoaded = getMorphia().fromDBObject(getDs(), RecursiveParent.class, stuff.findOne(new BasicDBObject("_id", parentDbObj.get("_id"))), new DefaultEntityCache());
        final RecursiveChild childLoaded = getMorphia().fromDBObject(getDs(), RecursiveChild.class, stuff.findOne(new BasicDBObject("_id", childDbObj.get("_id"))), new DefaultEntityCache());
        parentLoaded.setChild(childLoaded);
        childLoaded.setParent(parentLoaded);
        stuff.save(getMorphia().toDBObject(parentLoaded));
        stuff.save(getMorphia().toDBObject(childLoaded));
        final RecursiveParent finalParentLoaded = getMorphia().fromDBObject(getDs(), RecursiveParent.class, stuff.findOne(new BasicDBObject("_id", parentDbObj.get("_id"))), new DefaultEntityCache());
        final RecursiveChild finalChildLoaded = getMorphia().fromDBObject(getDs(), RecursiveChild.class, stuff.findOne(new BasicDBObject("_id", childDbObj.get("_id"))), new DefaultEntityCache());
        Assert.assertNotNull(finalParentLoaded.getChild());
        Assert.assertNotNull(finalChildLoaded.getParent());
    }

    @Test(expected = MappingException.class)
    public void testReferenceWithoutIdValue() {
        final RecursiveParent parent = new RecursiveParent();
        final RecursiveChild child = new RecursiveChild();
        child.setId(null);
        parent.setChild(child);
        getDs().save(parent);
    }

    @Test
    public void testSerializedMapping() {
        getMorphia().map(TestMapping.ContainsSerializedData.class);
        final Key<TestMapping.ContainsSerializedData> savedKey = getDs().save(new TestMapping.ContainsSerializedData());
        final TestMapping.ContainsSerializedData loaded = getDs().get(TestMapping.ContainsSerializedData.class, savedKey.getId());
        Assert.assertNotNull(loaded.data);
        Assert.assertEquals(loaded.data.someString, new TestMapping.ContainsSerializedData().data.someString);
        Assert.assertNotNull(loaded.id);
    }

    @Test
    public void testTimestampMapping() {
        getMorphia().map(TestMapping.ContainsTimestamp.class);
        final TestMapping.ContainsTimestamp cts = new TestMapping.ContainsTimestamp();
        final Key<TestMapping.ContainsTimestamp> savedKey = getDs().save(cts);
        final TestMapping.ContainsTimestamp loaded = getDs().get(TestMapping.ContainsTimestamp.class, savedKey.getId());
        Assert.assertNotNull(loaded.ts);
        Assert.assertEquals(loaded.ts.getTime(), cts.ts.getTime());
    }

    @Test
    public void testUUID() {
        // getMorphia().map(ContainsUUID.class);
        final TestMapping.ContainsUUID uuid = new TestMapping.ContainsUUID();
        final UUID before = uuid.uuid;
        getDs().save(uuid);
        final TestMapping.ContainsUUID loaded = getDs().find(TestMapping.ContainsUUID.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(loaded);
        Assert.assertNotNull(loaded.id);
        Assert.assertNotNull(loaded.uuid);
        Assert.assertEquals(before, loaded.uuid);
    }

    @Test
    public void testUuidId() {
        getMorphia().map(TestMapping.ContainsUuidId.class);
        final TestMapping.ContainsUuidId uuidId = new TestMapping.ContainsUuidId();
        final UUID before = uuidId.id;
        getDs().save(uuidId);
        final TestMapping.ContainsUuidId loaded = getDs().get(TestMapping.ContainsUuidId.class, before);
        Assert.assertNotNull(loaded);
        Assert.assertNotNull(loaded.id);
        Assert.assertEquals(before, loaded.id);
    }

    public enum Enum1 {

        A,
        B;}

    private interface Foo {}

    public abstract static class BaseEntity implements Serializable {
        @Id
        private ObjectId id;

        public String getId() {
            return id.toString();
        }

        public void setId(final String id) {
            this.id = new ObjectId(id);
        }
    }

    @Entity
    public static class MissingId {
        private String id;
    }

    private static class MissingIdStill {
        private String id;
    }

    @Entity("no-id")
    private static class MissingIdRenamed {
        private String id;
    }

    @Embedded
    private static class IdOnEmbedded {
        @Id
        private ObjectId id;
    }

    @Embedded("no-id")
    private static class RenamedEmbedded {
        private String name;
    }

    // CHECKSTYLE:ON
    private static class StrangelyNamedIdField {
        // CHECKSTYLE:OFF
        @Id
        private ObjectId id_ = new ObjectId();
    }

    private static class ContainsEmbeddedArray {
        @Id
        private ObjectId id = new ObjectId();

        private TestMapping.RenamedEmbedded[] res;
    }

    private static class NotEmbeddable {
        private String noImNot = "no, I'm not";
    }

    private static class SerializableClass implements Serializable {
        private final String someString = "hi, from the ether.";
    }

    private static class ContainsRef {
        @Id
        private ObjectId id;

        private DBRef rect;
    }

    private static class HasFinalFieldId {
        @Id
        private final long id;

        private String name = "some string";

        // only called when loaded by the persistence framework.
        protected HasFinalFieldId() {
            id = -1;
        }

        HasFinalFieldId(final long id) {
            this.id = id;
        }
    }

    private static class ContainsFinalField {
        private final String name;

        @Id
        private ObjectId id;

        protected ContainsFinalField() {
            name = "foo";
        }

        ContainsFinalField(final String name) {
            this.name = name;
        }
    }

    private static class ContainsTimestamp {
        private final Timestamp ts = new Timestamp(System.currentTimeMillis());

        @Id
        private ObjectId id;
    }

    private static class ContainsDBObject {
        @Id
        private ObjectId id;

        private DBObject dbObj = BasicDBObjectBuilder.start("field", "val").get();
    }

    private static class ContainsByteArray {
        private final byte[] bytes = "Scott".getBytes();

        @Id
        private ObjectId id;
    }

    private static class ContainsSerializedData {
        @Serialized
        private final TestMapping.SerializableClass data = new TestMapping.SerializableClass();

        @Id
        private ObjectId id;
    }

    private static class ContainsLongAndStringArray {
        @Id
        private ObjectId id;

        private Long[] longs = new Long[]{ 0L, 1L, 2L };

        private String[] strings = new String[]{ "Scott", "Rocks" };
    }

    private static final class ContainsCollection {
        private final Collection<String> coll = new ArrayList<String>();

        @Id
        private ObjectId id;

        private ContainsCollection() {
            coll.add("hi");
            coll.add("Scott");
        }
    }

    private static class ContainsPrimitiveMap {
        @Embedded
        private final Map<String, Long> embeddedValues = new HashMap<String, Long>();

        private final Map<String, Long> values = new HashMap<String, Long>();

        @Id
        private ObjectId id;
    }

    private static class Foo1 implements TestMapping.Foo {
        private String s;
    }

    private static class Foo2 implements TestMapping.Foo {
        private int i;
    }

    private static class ContainsMapWithEmbeddedInterface {
        @Embedded
        private final Map<String, TestMapping.Foo> embeddedValues = new HashMap<String, TestMapping.Foo>();

        @Id
        private ObjectId id;
    }

    private static class ContainsEmbeddedEntity {
        @Id
        private final ObjectId id = new ObjectId();

        @Embedded
        private TestMapping.ContainsIntegerList cil = new TestMapping.ContainsIntegerList();
    }

    @Entity(value = "cil", noClassnameStored = true)
    private static class ContainsIntegerList {
        @Id
        private ObjectId id;

        private List<Integer> intList = new ArrayList<Integer>();
    }

    private static class ContainsIntegerListNewAndOld {
        @Id
        private ObjectId id;

        private List<Integer> intList = new ArrayList<Integer>();

        private List<Integer> integers = new ArrayList<Integer>();
    }

    @Entity(value = "cil", noClassnameStored = true)
    private static class ContainsIntegerListNew {
        @AlsoLoad("intList")
        private final List<Integer> integers = new ArrayList<Integer>();

        @Id
        private ObjectId id;
    }

    @Entity(noClassnameStored = true)
    private static class ContainsUUID {
        private final UUID uuid = UUID.randomUUID();

        @Id
        private ObjectId id;
    }

    @Entity(noClassnameStored = true)
    private static class ContainsUuidId {
        @Id
        private final UUID id = UUID.randomUUID();
    }

    private static class ContainsEnum1KeyMap {
        private final Map<TestMapping.Enum1, String> values = new HashMap<TestMapping.Enum1, String>();

        @Embedded
        private final Map<TestMapping.Enum1, String> embeddedValues = new HashMap<TestMapping.Enum1, String>();

        @Id
        private ObjectId id;
    }

    private static class ContainsIntKeyMap {
        private final Map<Integer, String> values = new HashMap<Integer, String>();

        @Id
        private ObjectId id;
    }

    private static class ContainsIntKeySetStringMap {
        @Embedded
        private final Map<Integer, Set<String>> values = new HashMap<Integer, Set<String>>();

        @Id
        private ObjectId id;
    }

    private static class ContainsObjectIdKeyMap {
        private final Map<ObjectId, String> values = new HashMap<ObjectId, String>();

        @Id
        private ObjectId id;
    }

    private static class ContainsXKeyMap<T> {
        private final Map<T, String> values = new HashMap<T, String>();

        @Id
        private ObjectId id;
    }

    private static class ContainsMapLike {
        private final TestInheritanceMappings.MapLike m = new TestInheritanceMappings.MapLike();

        @Id
        private ObjectId id;
    }

    @Entity
    private static class UsesBaseEntity extends TestMapping.BaseEntity {}

    private static class MapSubclass extends LinkedHashMap<String, Object> {
        @Id
        private ObjectId id;
    }

    private class NonStaticInnerClass {
        @Id
        private long id = 1;
    }
}

