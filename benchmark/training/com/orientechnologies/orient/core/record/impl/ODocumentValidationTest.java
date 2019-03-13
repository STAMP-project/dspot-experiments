package com.orientechnologies.orient.core.record.impl;


import OType.BINARY;
import OType.BOOLEAN;
import OType.BYTE;
import OType.DATE;
import OType.DATETIME;
import OType.DECIMAL;
import OType.DOUBLE;
import OType.EMBEDDED;
import OType.EMBEDDEDLIST;
import OType.EMBEDDEDMAP;
import OType.EMBEDDEDSET;
import OType.FLOAT;
import OType.INTEGER;
import OType.LINK;
import OType.LINKBAG;
import OType.LINKLIST;
import OType.LINKMAP;
import OType.LINKSET;
import OType.LONG;
import OType.SHORT;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.exception.OValidationException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ODocumentValidationTest {
    @Test
    public void testRequiredValidation() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            ODocument doc = new ODocument();
            OIdentifiable id = db.save(doc, db.getClusterNameById(db.getDefaultClusterId())).getIdentity();
            OClass embeddedClazz = db.getMetadata().getSchema().createClass("EmbeddedValidation");
            embeddedClazz.createProperty("int", INTEGER).setMandatory(true);
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setMandatory(true);
            clazz.createProperty("long", LONG).setMandatory(true);
            clazz.createProperty("float", FLOAT).setMandatory(true);
            clazz.createProperty("boolean", BOOLEAN).setMandatory(true);
            clazz.createProperty("binary", BINARY).setMandatory(true);
            clazz.createProperty("byte", BYTE).setMandatory(true);
            clazz.createProperty("date", DATE).setMandatory(true);
            clazz.createProperty("datetime", DATETIME).setMandatory(true);
            clazz.createProperty("decimal", DECIMAL).setMandatory(true);
            clazz.createProperty("double", DOUBLE).setMandatory(true);
            clazz.createProperty("short", SHORT).setMandatory(true);
            clazz.createProperty("string", STRING).setMandatory(true);
            clazz.createProperty("link", LINK).setMandatory(true);
            clazz.createProperty("embedded", EMBEDDED, embeddedClazz).setMandatory(true);
            clazz.createProperty("embeddedListNoClass", EMBEDDEDLIST).setMandatory(true);
            clazz.createProperty("embeddedSetNoClass", EMBEDDEDSET).setMandatory(true);
            clazz.createProperty("embeddedMapNoClass", EMBEDDEDMAP).setMandatory(true);
            clazz.createProperty("embeddedList", EMBEDDEDLIST, embeddedClazz).setMandatory(true);
            clazz.createProperty("embeddedSet", EMBEDDEDSET, embeddedClazz).setMandatory(true);
            clazz.createProperty("embeddedMap", EMBEDDEDMAP, embeddedClazz).setMandatory(true);
            clazz.createProperty("linkList", LINKLIST).setMandatory(true);
            clazz.createProperty("linkSet", LINKSET).setMandatory(true);
            clazz.createProperty("linkMap", LINKMAP).setMandatory(true);
            ODocument d = new ODocument(clazz);
            d.field("int", 10);
            d.field("long", 10);
            d.field("float", 10);
            d.field("boolean", 10);
            d.field("binary", new byte[]{  });
            d.field("byte", 10);
            d.field("date", new Date());
            d.field("datetime", new Date());
            d.field("decimal", 10);
            d.field("double", 10);
            d.field("short", 10);
            d.field("string", "yeah");
            d.field("link", id);
            d.field("linkList", new ArrayList<ORecordId>());
            d.field("linkSet", new HashSet<ORecordId>());
            d.field("linkMap", new HashMap<String, ORecordId>());
            d.field("embeddedListNoClass", new ArrayList<ORecordId>());
            d.field("embeddedSetNoClass", new HashSet<ORecordId>());
            d.field("embeddedMapNoClass", new HashMap<String, ORecordId>());
            ODocument embedded = new ODocument("EmbeddedValidation");
            embedded.field("int", 20);
            embedded.field("long", 20);
            d.field("embedded", embedded);
            ODocument embeddedInList = new ODocument("EmbeddedValidation");
            embeddedInList.field("int", 30);
            embeddedInList.field("long", 30);
            final ArrayList<ODocument> embeddedList = new ArrayList<ODocument>();
            embeddedList.add(embeddedInList);
            d.field("embeddedList", embeddedList);
            ODocument embeddedInSet = new ODocument("EmbeddedValidation");
            embeddedInSet.field("int", 30);
            embeddedInSet.field("long", 30);
            final Set<ODocument> embeddedSet = new HashSet<ODocument>();
            embeddedSet.add(embeddedInSet);
            d.field("embeddedSet", embeddedSet);
            ODocument embeddedInMap = new ODocument("EmbeddedValidation");
            embeddedInMap.field("int", 30);
            embeddedInMap.field("long", 30);
            final Map<String, ODocument> embeddedMap = new HashMap<String, ODocument>();
            embeddedMap.put("testEmbedded", embeddedInMap);
            d.field("embeddedMap", embeddedMap);
            d.validate();
            checkRequireField(d, "int");
            checkRequireField(d, "long");
            checkRequireField(d, "float");
            checkRequireField(d, "boolean");
            checkRequireField(d, "binary");
            checkRequireField(d, "byte");
            checkRequireField(d, "date");
            checkRequireField(d, "datetime");
            checkRequireField(d, "decimal");
            checkRequireField(d, "double");
            checkRequireField(d, "short");
            checkRequireField(d, "string");
            checkRequireField(d, "link");
            checkRequireField(d, "embedded");
            checkRequireField(d, "embeddedList");
            checkRequireField(d, "embeddedSet");
            checkRequireField(d, "embeddedMap");
            checkRequireField(d, "linkList");
            checkRequireField(d, "linkSet");
            checkRequireField(d, "linkMap");
        } finally {
            db.drop();
        }
    }

    @Test
    public void testValidationNotValidEmbedded() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass embeddedClazz = db.getMetadata().getSchema().createClass("EmbeddedValidation");
            embeddedClazz.createProperty("int", INTEGER).setMandatory(true);
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setMandatory(true);
            clazz.createProperty("long", LONG).setMandatory(true);
            clazz.createProperty("embedded", EMBEDDED, embeddedClazz).setMandatory(true);
            ODocument d = new ODocument(clazz);
            d.field("int", 30);
            d.field("long", 30);
            d.field("embedded", new ODocument("EmbeddedValidation").field("test", "test"));
            try {
                d.validate();
                Assert.fail("Validation doesn't throw exception");
            } catch (OValidationException e) {
                Assert.assertTrue(e.toString().contains("EmbeddedValidation.int"));
            }
        } finally {
            db.drop();
        }
    }

    @Test
    public void testValidationNotValidEmbeddedSet() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass embeddedClazz = db.getMetadata().getSchema().createClass("EmbeddedValidation");
            embeddedClazz.createProperty("int", INTEGER).setMandatory(true);
            embeddedClazz.createProperty("long", LONG).setMandatory(true);
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setMandatory(true);
            clazz.createProperty("long", LONG).setMandatory(true);
            clazz.createProperty("embeddedSet", EMBEDDEDSET, embeddedClazz).setMandatory(true);
            ODocument d = new ODocument(clazz);
            d.field("int", 30);
            d.field("long", 30);
            final Set<ODocument> embeddedSet = new HashSet<ODocument>();
            d.field("embeddedSet", embeddedSet);
            ODocument embeddedInSet = new ODocument("EmbeddedValidation");
            embeddedInSet.field("int", 30);
            embeddedInSet.field("long", 30);
            embeddedSet.add(embeddedInSet);
            ODocument embeddedInSet2 = new ODocument("EmbeddedValidation");
            embeddedInSet2.field("int", 30);
            embeddedSet.add(embeddedInSet2);
            try {
                d.validate();
                Assert.fail("Validation doesn't throw exception");
            } catch (OValidationException e) {
                Assert.assertTrue(e.toString().contains("EmbeddedValidation.long"));
            }
        } finally {
            db.drop();
        }
    }

    @Test
    public void testValidationNotValidEmbeddedList() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass embeddedClazz = db.getMetadata().getSchema().createClass("EmbeddedValidation");
            embeddedClazz.createProperty("int", INTEGER).setMandatory(true);
            embeddedClazz.createProperty("long", LONG).setMandatory(true);
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setMandatory(true);
            clazz.createProperty("long", LONG).setMandatory(true);
            clazz.createProperty("embeddedList", EMBEDDEDLIST, embeddedClazz).setMandatory(true);
            ODocument d = new ODocument(clazz);
            d.field("int", 30);
            d.field("long", 30);
            final ArrayList<ODocument> embeddedList = new ArrayList<ODocument>();
            d.field("embeddedList", embeddedList);
            ODocument embeddedInList = new ODocument("EmbeddedValidation");
            embeddedInList.field("int", 30);
            embeddedInList.field("long", 30);
            embeddedList.add(embeddedInList);
            ODocument embeddedInList2 = new ODocument("EmbeddedValidation");
            embeddedInList2.field("int", 30);
            embeddedList.add(embeddedInList2);
            try {
                d.validate();
                Assert.fail("Validation doesn't throw exception");
            } catch (OValidationException e) {
                Assert.assertTrue(e.toString().contains("EmbeddedValidation.long"));
            }
        } finally {
            db.drop();
        }
    }

    @Test
    public void testValidationNotValidEmbeddedMap() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass embeddedClazz = db.getMetadata().getSchema().createClass("EmbeddedValidation");
            embeddedClazz.createProperty("int", INTEGER).setMandatory(true);
            embeddedClazz.createProperty("long", LONG).setMandatory(true);
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setMandatory(true);
            clazz.createProperty("long", LONG).setMandatory(true);
            clazz.createProperty("embeddedMap", EMBEDDEDMAP, embeddedClazz).setMandatory(true);
            ODocument d = new ODocument(clazz);
            d.field("int", 30);
            d.field("long", 30);
            final Map<String, ODocument> embeddedMap = new HashMap<String, ODocument>();
            d.field("embeddedMap", embeddedMap);
            ODocument embeddedInMap = new ODocument("EmbeddedValidation");
            embeddedInMap.field("int", 30);
            embeddedInMap.field("long", 30);
            embeddedMap.put("1", embeddedInMap);
            ODocument embeddedInMap2 = new ODocument("EmbeddedValidation");
            embeddedInMap2.field("int", 30);
            embeddedMap.put("2", embeddedInMap2);
            try {
                d.validate();
                Assert.fail("Validation doesn't throw exception");
            } catch (OValidationException e) {
                Assert.assertTrue(e.toString().contains("EmbeddedValidation.long"));
            }
        } finally {
            db.drop();
        }
    }

    @Test
    public void testMaxValidation() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setMax("11");
            clazz.createProperty("long", LONG).setMax("11");
            clazz.createProperty("float", FLOAT).setMax("11");
            // clazz.createProperty("boolean", OType.BOOLEAN) no meaning
            clazz.createProperty("binary", BINARY).setMax("11");
            clazz.createProperty("byte", BYTE).setMax("11");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, ((cal.get(Calendar.HOUR)) == 11 ? 0 : 1));
            SimpleDateFormat format = getStorage().getConfiguration().getDateFormatInstance();
            clazz.createProperty("date", DATE).setMax(format.format(cal.getTime()));
            cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            format = getStorage().getConfiguration().getDateTimeFormatInstance();
            clazz.createProperty("datetime", DATETIME).setMax(format.format(cal.getTime()));
            clazz.createProperty("decimal", DECIMAL).setMax("11");
            clazz.createProperty("double", DOUBLE).setMax("11");
            clazz.createProperty("short", SHORT).setMax("11");
            clazz.createProperty("string", STRING).setMax("11");
            // clazz.createProperty("link", OType.LINK) no meaning
            // clazz.createProperty("embedded", OType.EMBEDDED) no meaning
            clazz.createProperty("embeddedList", EMBEDDEDLIST).setMax("2");
            clazz.createProperty("embeddedSet", EMBEDDEDSET).setMax("2");
            clazz.createProperty("embeddedMap", EMBEDDEDMAP).setMax("2");
            clazz.createProperty("linkList", LINKLIST).setMax("2");
            clazz.createProperty("linkSet", LINKSET).setMax("2");
            clazz.createProperty("linkMap", LINKMAP).setMax("2");
            clazz.createProperty("linkBag", LINKBAG).setMax("2");
            ODocument d = new ODocument(clazz);
            d.field("int", 11);
            d.field("long", 11);
            d.field("float", 11);
            d.field("binary", new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
            d.field("byte", 11);
            d.field("date", new Date());
            d.field("datetime", new Date());
            d.field("decimal", 10);
            d.field("double", 10);
            d.field("short", 10);
            d.field("string", "yeah");
            d.field("embeddedList", Arrays.asList("a", "b"));
            d.field("embeddedSet", new HashSet<String>(Arrays.asList("a", "b")));
            HashMap<String, String> cont = new HashMap<String, String>();
            cont.put("one", "one");
            cont.put("two", "one");
            d.field("embeddedMap", cont);
            d.field("linkList", Arrays.asList(new ORecordId(40, 30), new ORecordId(40, 34)));
            d.field("linkSet", new HashSet<ORecordId>(Arrays.asList(new ORecordId(40, 30), new ORecordId(40, 31))));
            HashMap<String, ORecordId> cont1 = new HashMap<String, ORecordId>();
            cont1.put("one", new ORecordId(30, 30));
            cont1.put("two", new ORecordId(30, 30));
            d.field("linkMap", cont1);
            ORidBag bag1 = new ORidBag();
            bag1.add(new ORecordId(40, 30));
            bag1.add(new ORecordId(40, 33));
            d.field("linkBag", bag1);
            d.validate();
            checkField(d, "int", 12);
            checkField(d, "long", 12);
            checkField(d, "float", 20);
            checkField(d, "binary", new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 });
            checkField(d, "byte", 20);
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            checkField(d, "date", cal.getTime());
            checkField(d, "datetime", cal.getTime());
            checkField(d, "decimal", 20);
            checkField(d, "double", 20);
            checkField(d, "short", 20);
            checkField(d, "string", "0123456789101112");
            checkField(d, "embeddedList", Arrays.asList("a", "b", "d"));
            checkField(d, "embeddedSet", new HashSet<String>(Arrays.asList("a", "b", "d")));
            HashMap<String, String> con1 = new HashMap<String, String>();
            con1.put("one", "one");
            con1.put("two", "one");
            con1.put("three", "one");
            checkField(d, "embeddedMap", con1);
            checkField(d, "linkList", Arrays.asList(new ORecordId(40, 30), new ORecordId(40, 33), new ORecordId(40, 31)));
            checkField(d, "linkSet", new HashSet<ORecordId>(Arrays.asList(new ORecordId(40, 30), new ORecordId(40, 33), new ORecordId(40, 31))));
            HashMap<String, ORecordId> cont3 = new HashMap<String, ORecordId>();
            cont3.put("one", new ORecordId(30, 30));
            cont3.put("two", new ORecordId(30, 30));
            cont3.put("three", new ORecordId(30, 30));
            checkField(d, "linkMap", cont3);
            ORidBag bag2 = new ORidBag();
            bag2.add(new ORecordId(40, 30));
            bag2.add(new ORecordId(40, 33));
            bag2.add(new ORecordId(40, 31));
            checkField(d, "linkBag", bag2);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testMinValidation() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            ODocument doc = new ODocument();
            OIdentifiable id = db.save(doc, db.getClusterNameById(db.getDefaultClusterId())).getIdentity();
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setMin("11");
            clazz.createProperty("long", LONG).setMin("11");
            clazz.createProperty("float", FLOAT).setMin("11");
            // clazz.createProperty("boolean", OType.BOOLEAN) //no meaning
            clazz.createProperty("binary", BINARY).setMin("11");
            clazz.createProperty("byte", BYTE).setMin("11");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, ((cal.get(Calendar.HOUR)) == 11 ? 0 : 1));
            SimpleDateFormat format = getStorage().getConfiguration().getDateFormatInstance();
            clazz.createProperty("date", DATE).setMin(format.format(cal.getTime()));
            cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            format = getStorage().getConfiguration().getDateTimeFormatInstance();
            clazz.createProperty("datetime", DATETIME).setMin(format.format(cal.getTime()));
            clazz.createProperty("decimal", DECIMAL).setMin("11");
            clazz.createProperty("double", DOUBLE).setMin("11");
            clazz.createProperty("short", SHORT).setMin("11");
            clazz.createProperty("string", STRING).setMin("11");
            // clazz.createProperty("link", OType.LINK) no meaning
            // clazz.createProperty("embedded", OType.EMBEDDED) no meaning
            clazz.createProperty("embeddedList", EMBEDDEDLIST).setMin("1");
            clazz.createProperty("embeddedSet", EMBEDDEDSET).setMin("1");
            clazz.createProperty("embeddedMap", EMBEDDEDMAP).setMin("1");
            clazz.createProperty("linkList", LINKLIST).setMin("1");
            clazz.createProperty("linkSet", LINKSET).setMin("1");
            clazz.createProperty("linkMap", LINKMAP).setMin("1");
            clazz.createProperty("linkBag", LINKBAG).setMin("1");
            ODocument d = new ODocument(clazz);
            d.field("int", 11);
            d.field("long", 11);
            d.field("float", 11);
            d.field("binary", new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
            d.field("byte", 11);
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            d.field("date", new Date());
            d.field("datetime", cal.getTime());
            d.field("decimal", 12);
            d.field("double", 12);
            d.field("short", 12);
            d.field("string", "yeahyeahyeah");
            d.field("link", id);
            // d.field("embedded", new ODocument().field("test", "test"));
            d.field("embeddedList", Arrays.asList("a"));
            d.field("embeddedSet", new HashSet<String>(Arrays.asList("a")));
            Map<String, String> map = new HashMap<String, String>();
            map.put("some", "value");
            d.field("embeddedMap", map);
            d.field("linkList", Arrays.asList(new ORecordId(40, 50)));
            d.field("linkSet", new HashSet<ORecordId>(Arrays.asList(new ORecordId(40, 50))));
            HashMap<String, ORecordId> map1 = new HashMap<String, ORecordId>();
            map1.put("some", new ORecordId(40, 50));
            d.field("linkMap", map1);
            ORidBag bag1 = new ORidBag();
            bag1.add(new ORecordId(40, 50));
            d.field("linkBag", bag1);
            d.validate();
            checkField(d, "int", 10);
            checkField(d, "long", 10);
            checkField(d, "float", 10);
            checkField(d, "binary", new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
            checkField(d, "byte", 10);
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, (-1));
            checkField(d, "date", cal.getTime());
            checkField(d, "datetime", new Date());
            checkField(d, "decimal", 10);
            checkField(d, "double", 10);
            checkField(d, "short", 10);
            checkField(d, "string", "01234");
            checkField(d, "embeddedList", new ArrayList<String>());
            checkField(d, "embeddedSet", new HashSet<String>());
            checkField(d, "embeddedMap", new HashMap<String, String>());
            checkField(d, "linkList", new ArrayList<ORecordId>());
            checkField(d, "linkSet", new HashSet<ORecordId>());
            checkField(d, "linkMap", new HashMap<String, ORecordId>());
            checkField(d, "linkBag", new ORidBag());
        } finally {
            db.drop();
        }
    }

    @Test
    public void testNotNullValidation() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            ODocument doc = new ODocument();
            OIdentifiable id = db.save(doc, db.getClusterNameById(db.getDefaultClusterId())).getIdentity();
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("int", INTEGER).setNotNull(true);
            clazz.createProperty("long", LONG).setNotNull(true);
            clazz.createProperty("float", FLOAT).setNotNull(true);
            clazz.createProperty("boolean", BOOLEAN).setNotNull(true);
            clazz.createProperty("binary", BINARY).setNotNull(true);
            clazz.createProperty("byte", BYTE).setNotNull(true);
            clazz.createProperty("date", DATE).setNotNull(true);
            clazz.createProperty("datetime", DATETIME).setNotNull(true);
            clazz.createProperty("decimal", DECIMAL).setNotNull(true);
            clazz.createProperty("double", DOUBLE).setNotNull(true);
            clazz.createProperty("short", SHORT).setNotNull(true);
            clazz.createProperty("string", STRING).setNotNull(true);
            clazz.createProperty("link", LINK).setNotNull(true);
            clazz.createProperty("embedded", EMBEDDED).setNotNull(true);
            clazz.createProperty("embeddedList", EMBEDDEDLIST).setNotNull(true);
            clazz.createProperty("embeddedSet", EMBEDDEDSET).setNotNull(true);
            clazz.createProperty("embeddedMap", EMBEDDEDMAP).setNotNull(true);
            clazz.createProperty("linkList", LINKLIST).setNotNull(true);
            clazz.createProperty("linkSet", LINKSET).setNotNull(true);
            clazz.createProperty("linkMap", LINKMAP).setNotNull(true);
            ODocument d = new ODocument(clazz);
            d.field("int", 12);
            d.field("long", 12);
            d.field("float", 12);
            d.field("boolean", true);
            d.field("binary", new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
            d.field("byte", 12);
            d.field("date", new Date());
            d.field("datetime", new Date());
            d.field("decimal", 12);
            d.field("double", 12);
            d.field("short", 12);
            d.field("string", "yeah");
            d.field("link", id);
            d.field("embedded", new ODocument().field("test", "test"));
            d.field("embeddedList", new ArrayList<String>());
            d.field("embeddedSet", new HashSet<String>());
            d.field("embeddedMap", new HashMap<String, String>());
            d.field("linkList", new ArrayList<ORecordId>());
            d.field("linkSet", new HashSet<ORecordId>());
            d.field("linkMap", new HashMap<String, ORecordId>());
            d.validate();
            checkField(d, "int", null);
            checkField(d, "long", null);
            checkField(d, "float", null);
            checkField(d, "boolean", null);
            checkField(d, "binary", null);
            checkField(d, "byte", null);
            checkField(d, "date", null);
            checkField(d, "datetime", null);
            checkField(d, "decimal", null);
            checkField(d, "double", null);
            checkField(d, "short", null);
            checkField(d, "string", null);
            checkField(d, "link", null);
            checkField(d, "embedded", null);
            checkField(d, "embeddedList", null);
            checkField(d, "embeddedSet", null);
            checkField(d, "embeddedMap", null);
            checkField(d, "linkList", null);
            checkField(d, "linkSet", null);
            checkField(d, "linkMap", null);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testRegExpValidation() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("string", STRING).setRegexp("[^Z]*");
            ODocument d = new ODocument(clazz);
            d.field("string", "yeah");
            d.validate();
            checkField(d, "string", "yaZah");
        } finally {
            db.drop();
        }
    }

    @Test
    public void testLinkedTypeValidation() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            clazz.createProperty("embeddedList", EMBEDDEDLIST).setLinkedType(INTEGER);
            clazz.createProperty("embeddedSet", EMBEDDEDSET).setLinkedType(INTEGER);
            clazz.createProperty("embeddedMap", EMBEDDEDMAP).setLinkedType(INTEGER);
            ODocument d = new ODocument(clazz);
            List<Integer> list = Arrays.asList(1, 2);
            d.field("embeddedList", list);
            Set<Integer> set = new HashSet<Integer>(list);
            d.field("embeddedSet", set);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("a", 1);
            map.put("b", 2);
            d.field("embeddedMap", map);
            d.validate();
            checkField(d, "embeddedList", Arrays.asList("a", "b"));
            checkField(d, "embeddedSet", new HashSet<String>(Arrays.asList("a", "b")));
            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("a", "a1");
            map1.put("b", "a2");
            checkField(d, "embeddedMap", map1);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testLinkedClassValidation() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            OClass clazz1 = db.getMetadata().getSchema().createClass("Validation1");
            clazz.createProperty("link", LINK).setLinkedClass(clazz1);
            clazz.createProperty("embedded", EMBEDDED).setLinkedClass(clazz1);
            clazz.createProperty("linkList", LINKLIST).setLinkedClass(clazz1);
            clazz.createProperty("embeddedList", EMBEDDEDLIST).setLinkedClass(clazz1);
            clazz.createProperty("embeddedSet", EMBEDDEDSET).setLinkedClass(clazz1);
            clazz.createProperty("linkSet", LINKSET).setLinkedClass(clazz1);
            clazz.createProperty("linkMap", LINKMAP).setLinkedClass(clazz1);
            clazz.createProperty("linkBag", LINKBAG).setLinkedClass(clazz1);
            ODocument d = new ODocument(clazz);
            d.field("link", new ODocument(clazz1));
            d.field("embedded", new ODocument(clazz1));
            List<ODocument> list = Arrays.asList(new ODocument(clazz1));
            d.field("linkList", list);
            Set<ODocument> set = new HashSet<ODocument>(list);
            d.field("linkSet", set);
            List<ODocument> embeddedList = Arrays.asList(new ODocument(clazz1), null);
            d.field("embeddedList", embeddedList);
            Set<ODocument> embeddedSet = new HashSet<ODocument>(embeddedList);
            d.field("embeddedSet", embeddedSet);
            Map<String, ODocument> map = new HashMap<String, ODocument>();
            map.put("a", new ODocument(clazz1));
            d.field("linkMap", map);
            d.validate();
            checkField(d, "link", new ODocument(clazz));
            checkField(d, "embedded", new ODocument(clazz));
            checkField(d, "linkList", Arrays.asList("a", "b"));
            checkField(d, "linkSet", new HashSet<String>(Arrays.asList("a", "b")));
            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("a", "a1");
            map1.put("b", "a2");
            checkField(d, "linkMap", map1);
            checkField(d, "linkList", Arrays.asList(new ODocument(clazz)));
            checkField(d, "linkSet", new HashSet<ODocument>(Arrays.asList(new ODocument(clazz))));
            checkField(d, "embeddedList", Arrays.asList(new ODocument(clazz)));
            checkField(d, "embeddedSet", Arrays.asList(new ODocument(clazz)));
            ORidBag bag = new ORidBag();
            bag.add(new ODocument(clazz));
            checkField(d, "linkBag", bag);
            Map<String, ODocument> map2 = new HashMap<String, ODocument>();
            map2.put("a", new ODocument(clazz));
            checkField(d, "linkMap", map2);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testValidLinkCollectionsUpdate() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODocumentValidationTest.class.getSimpleName())));
        db.create();
        try {
            OClass clazz = db.getMetadata().getSchema().createClass("Validation");
            OClass clazz1 = db.getMetadata().getSchema().createClass("Validation1");
            clazz.createProperty("linkList", LINKLIST).setLinkedClass(clazz1);
            clazz.createProperty("linkSet", LINKSET).setLinkedClass(clazz1);
            clazz.createProperty("linkMap", LINKMAP).setLinkedClass(clazz1);
            clazz.createProperty("linkBag", LINKBAG).setLinkedClass(clazz1);
            ODocument d = new ODocument(clazz);
            d.field("link", new ODocument(clazz1));
            d.field("embedded", new ODocument(clazz1));
            List<ODocument> list = Arrays.asList(new ODocument(clazz1));
            d.field("linkList", list);
            Set<ODocument> set = new HashSet<ODocument>(list);
            d.field("linkSet", set);
            d.field("linkBag", new ORidBag());
            Map<String, ODocument> map = new HashMap<String, ODocument>();
            map.put("a", new ODocument(clazz1));
            d.field("linkMap", map);
            db.save(d);
            try {
                ODocument newD = d.copy();
                ((Collection) (newD.field("linkList"))).add(new ODocument(clazz));
                newD.validate();
                Assert.fail();
            } catch (OValidationException v) {
            }
            try {
                ODocument newD = d.copy();
                ((Collection) (newD.field("linkSet"))).add(new ODocument(clazz));
                newD.validate();
                Assert.fail();
            } catch (OValidationException v) {
            }
            try {
                ODocument newD = d.copy();
                add(new ODocument(clazz));
                newD.validate();
                Assert.fail();
            } catch (OValidationException v) {
            }
            try {
                ODocument newD = d.copy();
                ((Map<String, ODocument>) (newD.field("linkMap"))).put("a", new ODocument(clazz));
                newD.validate();
                Assert.fail();
            } catch (OValidationException v) {
            }
        } finally {
            db.drop();
        }
    }
}

