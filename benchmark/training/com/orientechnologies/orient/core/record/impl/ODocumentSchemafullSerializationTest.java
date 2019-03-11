package com.orientechnologies.orient.core.record.impl;


import OType.STRING;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public abstract class ODocumentSchemafullSerializationTest {
    private static final String CITY = "city";

    private static final String NUMBER = "number";

    private static final String INT_FIELD = ODocumentSchemafullSerializationTest.NUMBER;

    private static final String NAME = "name";

    private static final String MAP_BYTES = "bytesMap";

    private static final String MAP_DOUBLE = "doubleMap";

    private static final String MAP_FLOAT = "floatMap";

    private static final String MAP_DATE = "dateMap";

    private static final String MAP_SHORT = "shortMap";

    private static final String MAP_LONG = "mapLong";

    private static final String MAP_INT = "mapInt";

    private static final String MAP_STRING = "mapString";

    private static final String LIST_MIXED = "listMixed";

    private static final String LIST_BOOLEANS = "booleans";

    private static final String LIST_BYTES = "bytes";

    private static final String LIST_DATES = "dates";

    private static final String LIST_DOUBLES = "doubles";

    private static final String LIST_FLOATS = "floats";

    private static final String LIST_INTEGERS = "integers";

    private static final String LIST_LONGS = "longs";

    private static final String LIST_SHORTS = "shorts";

    private static final String LIST_STRINGS = "listStrings";

    private static final String SHORT_FIELD = "shortNumber";

    private static final String LONG_FIELD = "longNumber";

    private static final String STRING_FIELD = "stringField";

    private static final String FLOAT_NUMBER = "floatNumber";

    private static final String DOUBLE_NUMBER = "doubleNumber";

    private static final String BYTE_FIELD = "byteField";

    private static final String BOOLEAN_FIELD = "booleanField";

    private static final String DATE_FIELD = "dateField";

    private static final String RECORDID_FIELD = "recordField";

    private static final String EMBEDDED_FIELD = "embeddedField";

    private static final String ANY_FIELD = "anyField";

    @Rule
    public TestName name = new TestName();

    private ODatabaseDocumentInternal databaseDocument;

    private OClass simple;

    private ORecordSerializer serializer;

    private OClass embSimp;

    private OClass address;

    private OClass embMapSimple;

    public ODocumentSchemafullSerializationTest(ORecordSerializer serializer) {
        this.serializer = serializer;
    }

    @Test
    public void testSimpleSerialization() {
        ODatabaseRecordThreadLocal.instance().set(databaseDocument);
        ODocument document = new ODocument(simple);
        document.field(ODocumentSchemafullSerializationTest.STRING_FIELD, ODocumentSchemafullSerializationTest.NAME);
        document.field(ODocumentSchemafullSerializationTest.INT_FIELD, 20);
        document.field(ODocumentSchemafullSerializationTest.SHORT_FIELD, ((short) (20)));
        document.field(ODocumentSchemafullSerializationTest.LONG_FIELD, ((long) (20)));
        document.field(ODocumentSchemafullSerializationTest.FLOAT_NUMBER, 12.5F);
        document.field(ODocumentSchemafullSerializationTest.DOUBLE_NUMBER, 12.5);
        document.field(ODocumentSchemafullSerializationTest.BYTE_FIELD, ((byte) ('C')));
        document.field(ODocumentSchemafullSerializationTest.BOOLEAN_FIELD, true);
        document.field(ODocumentSchemafullSerializationTest.DATE_FIELD, new Date());
        document.field(ODocumentSchemafullSerializationTest.RECORDID_FIELD, new ORecordId(10, 0));
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.STRING_FIELD), document.field(ODocumentSchemafullSerializationTest.STRING_FIELD));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.INT_FIELD), document.field(ODocumentSchemafullSerializationTest.INT_FIELD));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.SHORT_FIELD), document.field(ODocumentSchemafullSerializationTest.SHORT_FIELD));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LONG_FIELD), document.field(ODocumentSchemafullSerializationTest.LONG_FIELD));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.FLOAT_NUMBER), document.field(ODocumentSchemafullSerializationTest.FLOAT_NUMBER));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.DOUBLE_NUMBER), document.field(ODocumentSchemafullSerializationTest.DOUBLE_NUMBER));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.BYTE_FIELD), document.field(ODocumentSchemafullSerializationTest.BYTE_FIELD));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.BOOLEAN_FIELD), document.field(ODocumentSchemafullSerializationTest.BOOLEAN_FIELD));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.DATE_FIELD), document.field(ODocumentSchemafullSerializationTest.DATE_FIELD));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.RECORDID_FIELD), document.field(ODocumentSchemafullSerializationTest.RECORDID_FIELD));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSimpleLiteralList() {
        ODatabaseRecordThreadLocal.instance().set(databaseDocument);
        ODocument document = new ODocument(embSimp);
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        document.field(ODocumentSchemafullSerializationTest.LIST_STRINGS, strings);
        List<Short> shorts = new ArrayList<Short>();
        shorts.add(((short) (1)));
        shorts.add(((short) (2)));
        shorts.add(((short) (3)));
        document.field(ODocumentSchemafullSerializationTest.LIST_SHORTS, shorts);
        List<Long> longs = new ArrayList<Long>();
        longs.add(((long) (1)));
        longs.add(((long) (2)));
        longs.add(((long) (3)));
        document.field(ODocumentSchemafullSerializationTest.LIST_LONGS, longs);
        List<Integer> ints = new ArrayList<Integer>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        document.field(ODocumentSchemafullSerializationTest.LIST_INTEGERS, ints);
        List<Float> floats = new ArrayList<Float>();
        floats.add(1.1F);
        floats.add(2.2F);
        floats.add(3.3F);
        document.field(ODocumentSchemafullSerializationTest.LIST_FLOATS, floats);
        List<Double> doubles = new ArrayList<Double>();
        doubles.add(1.1);
        doubles.add(2.2);
        doubles.add(3.3);
        document.field(ODocumentSchemafullSerializationTest.LIST_DOUBLES, doubles);
        List<Date> dates = new ArrayList<Date>();
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        document.field(ODocumentSchemafullSerializationTest.LIST_DATES, dates);
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.add(((byte) (0)));
        bytes.add(((byte) (1)));
        bytes.add(((byte) (3)));
        document.field(ODocumentSchemafullSerializationTest.LIST_BYTES, bytes);
        // TODO: char not currently supported in orient.
        List<Character> chars = new ArrayList<Character>();
        chars.add('A');
        chars.add('B');
        chars.add('C');
        // document.field("chars", chars);
        List<Boolean> booleans = new ArrayList<Boolean>();
        booleans.add(true);
        booleans.add(false);
        booleans.add(false);
        document.field(ODocumentSchemafullSerializationTest.LIST_BOOLEANS, booleans);
        List listMixed = new ArrayList();
        listMixed.add(true);
        listMixed.add(1);
        listMixed.add(((long) (5)));
        listMixed.add(((short) (2)));
        listMixed.add(4.0F);
        listMixed.add(7.0);
        listMixed.add("hello");
        listMixed.add(new Date());
        listMixed.add(((byte) (10)));
        document.field(ODocumentSchemafullSerializationTest.LIST_MIXED, listMixed);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LIST_STRINGS), document.field(ODocumentSchemafullSerializationTest.LIST_STRINGS));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LIST_INTEGERS), document.field(ODocumentSchemafullSerializationTest.LIST_INTEGERS));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LIST_DOUBLES), document.field(ODocumentSchemafullSerializationTest.LIST_DOUBLES));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LIST_DATES), document.field(ODocumentSchemafullSerializationTest.LIST_DATES));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LIST_BYTES), document.field(ODocumentSchemafullSerializationTest.LIST_BYTES));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LIST_BOOLEANS), document.field(ODocumentSchemafullSerializationTest.LIST_BOOLEANS));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.LIST_MIXED), document.field(ODocumentSchemafullSerializationTest.LIST_MIXED));
    }

    @Test
    public void testSimpleMapStringLiteral() {
        ODatabaseRecordThreadLocal.instance().set(databaseDocument);
        ODocument document = new ODocument(embMapSimple);
        Map<String, String> mapString = new HashMap<String, String>();
        mapString.put("key", "value");
        mapString.put("key1", "value1");
        document.field(ODocumentSchemafullSerializationTest.MAP_STRING, mapString);
        Map<String, Integer> mapInt = new HashMap<String, Integer>();
        mapInt.put("key", 2);
        mapInt.put("key1", 3);
        document.field(ODocumentSchemafullSerializationTest.MAP_INT, mapInt);
        Map<String, Long> mapLong = new HashMap<String, Long>();
        mapLong.put("key", 2L);
        mapLong.put("key1", 3L);
        document.field(ODocumentSchemafullSerializationTest.MAP_LONG, mapLong);
        Map<String, Short> shortMap = new HashMap<String, Short>();
        shortMap.put("key", ((short) (2)));
        shortMap.put("key1", ((short) (3)));
        document.field(ODocumentSchemafullSerializationTest.MAP_SHORT, shortMap);
        Map<String, Date> dateMap = new HashMap<String, Date>();
        dateMap.put("key", new Date());
        dateMap.put("key1", new Date());
        document.field(ODocumentSchemafullSerializationTest.MAP_DATE, dateMap);
        Map<String, Float> floatMap = new HashMap<String, Float>();
        floatMap.put("key", 10.0F);
        floatMap.put("key1", 11.0F);
        document.field(ODocumentSchemafullSerializationTest.MAP_FLOAT, floatMap);
        Map<String, Double> doubleMap = new HashMap<String, Double>();
        doubleMap.put("key", 10.0);
        doubleMap.put("key1", 11.0);
        document.field(ODocumentSchemafullSerializationTest.MAP_DOUBLE, doubleMap);
        Map<String, Byte> bytesMap = new HashMap<String, Byte>();
        bytesMap.put("key", ((byte) (10)));
        bytesMap.put("key1", ((byte) (11)));
        document.field(ODocumentSchemafullSerializationTest.MAP_BYTES, bytesMap);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.MAP_STRING), document.field(ODocumentSchemafullSerializationTest.MAP_STRING));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.MAP_LONG), document.field(ODocumentSchemafullSerializationTest.MAP_LONG));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.MAP_SHORT), document.field(ODocumentSchemafullSerializationTest.MAP_SHORT));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.MAP_DATE), document.field(ODocumentSchemafullSerializationTest.MAP_DATE));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.MAP_DOUBLE), document.field(ODocumentSchemafullSerializationTest.MAP_DOUBLE));
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.MAP_BYTES), document.field(ODocumentSchemafullSerializationTest.MAP_BYTES));
    }

    @Test
    public void testSimpleEmbeddedDoc() {
        ODatabaseRecordThreadLocal.instance().set(databaseDocument);
        ODocument document = new ODocument(simple);
        ODocument embedded = new ODocument(address);
        embedded.field(ODocumentSchemafullSerializationTest.NAME, "test");
        embedded.field(ODocumentSchemafullSerializationTest.NUMBER, 1);
        embedded.field(ODocumentSchemafullSerializationTest.CITY, "aaa");
        document.field(ODocumentSchemafullSerializationTest.EMBEDDED_FIELD, embedded);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(document.fields(), extr.fields());
        ODocument emb = extr.field(ODocumentSchemafullSerializationTest.EMBEDDED_FIELD);
        Assert.assertNotNull(emb);
        Assert.assertEquals(emb.<Object>field(ODocumentSchemafullSerializationTest.NAME), embedded.field(ODocumentSchemafullSerializationTest.NAME));
        Assert.assertEquals(emb.<Object>field(ODocumentSchemafullSerializationTest.NUMBER), embedded.field(ODocumentSchemafullSerializationTest.NUMBER));
        Assert.assertEquals(emb.<Object>field(ODocumentSchemafullSerializationTest.CITY), embedded.field(ODocumentSchemafullSerializationTest.CITY));
    }

    @Test
    public void testUpdateBooleanWithPropertyTypeAny() {
        ODatabaseRecordThreadLocal.instance().set(databaseDocument);
        ODocument document = new ODocument(simple);
        document.field(ODocumentSchemafullSerializationTest.ANY_FIELD, false);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(document.fields(), extr.fields());
        Assert.assertEquals(extr.<Object>field(ODocumentSchemafullSerializationTest.ANY_FIELD), false);
        extr.field(ODocumentSchemafullSerializationTest.ANY_FIELD, false);
        res = serializer.toStream(extr, false);
        ODocument extr2 = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), extr2.fields());
        Assert.assertEquals(extr2.<Object>field(ODocumentSchemafullSerializationTest.ANY_FIELD), false);
    }

    @Test
    public void simpleTypeKeepingTest() {
        ODatabaseRecordThreadLocal.instance().set(databaseDocument);
        ODocument document = new ODocument();
        document.field("name", "test");
        byte[] res = serializer.toStream(document, false);
        ODocument extr = new ODocument().fromStream(res);
        Assert.assertEquals(STRING, extr.fieldType("name"));
    }
}

