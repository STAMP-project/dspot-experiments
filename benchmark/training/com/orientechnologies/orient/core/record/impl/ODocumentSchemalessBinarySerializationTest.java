package com.orientechnologies.orient.core.record.impl;


import ORecordSerializerBinary.INSTANCE;
import OType.CUSTOM;
import OType.DATE;
import OType.EMBEDDED;
import OType.EMBEDDEDLIST;
import OType.EMBEDDEDMAP;
import OType.EMBEDDEDSET;
import OType.LINKLIST;
import OType.LINKMAP;
import OType.LINKSET;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.serialization.ODocumentSerializable;
import com.orientechnologies.orient.core.serialization.OSerializableStream;
import com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.ORecordSerializerBinary;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.ORecordSerializerNetwork;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.ORecordSerializerNetworkV37;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ODocumentSchemalessBinarySerializationTest {
    protected ORecordSerializer serializer;

    private final byte serializerVersion;

    // first to test for all registreted serializers , then for network serializers
    public ODocumentSchemalessBinarySerializationTest(byte serializerVersion) {
        int numOfRegistretedSerializers = INSTANCE.getNumberOfSupportedVersions();
        if (serializerVersion < numOfRegistretedSerializers) {
            serializer = new ORecordSerializerBinary(serializerVersion);
        } else
            if (serializerVersion == numOfRegistretedSerializers) {
                serializer = new ORecordSerializerNetwork();
            } else
                if (serializerVersion == (numOfRegistretedSerializers + 1)) {
                    serializer = new ORecordSerializerNetworkV37();
                }


        this.serializerVersion = serializerVersion;
    }

    @Test
    public void testSimpleSerialization() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        document.field("name", "name");
        document.field("age", 20);
        document.field("youngAge", ((short) (20)));
        document.field("oldAge", ((long) (20)));
        document.field("heigth", 12.5F);
        document.field("bitHeigth", 12.5);
        document.field("class", ((byte) ('C')));
        document.field("nullField", ((Object) (null)));
        document.field("character", 'C');
        document.field("alive", true);
        document.field("dateTime", new Date());
        document.field("bigNumber", new BigDecimal("43989872423376487952454365232141525434.32146432321442534"));
        ORidBag bag = new ORidBag();
        bag.add(new ORecordId(1, 1));
        bag.add(new ORecordId(2, 2));
        // document.field("ridBag", bag);
        Calendar c = Calendar.getInstance();
        document.field("date", c.getTime(), DATE);
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.MILLISECOND, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        document.field("date1", c1.getTime(), DATE);
        byte[] byteValue = new byte[10];
        Arrays.fill(byteValue, ((byte) (10)));
        document.field("bytes", byteValue);
        document.field("utf8String", new String(("A" + ((("\u00ea" + "\u00f1") + "\u00fc") + "C"))));
        document.field("recordId", new ORecordId(10, 10));
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("name"), document.field("name"));
        Assert.assertEquals(extr.<Object>field("age"), document.field("age"));
        Assert.assertEquals(extr.<Object>field("youngAge"), document.field("youngAge"));
        Assert.assertEquals(extr.<Object>field("oldAge"), document.field("oldAge"));
        Assert.assertEquals(extr.<Object>field("heigth"), document.field("heigth"));
        Assert.assertEquals(extr.<Object>field("bitHeigth"), document.field("bitHeigth"));
        Assert.assertEquals(extr.<Object>field("class"), document.field("class"));
        // TODO fix char management issue:#2427
        // assertEquals(document.field("character"), extr.field("character"));
        Assert.assertEquals(extr.<Object>field("alive"), document.field("alive"));
        Assert.assertEquals(extr.<Object>field("dateTime"), document.field("dateTime"));
        Assert.assertEquals(extr.field("date"), c.getTime());
        Assert.assertEquals(extr.field("date1"), c1.getTime());
        // assertEquals(extr.<String>field("bytes"), document.field("bytes"));
        Assertions.assertThat(extr.<Object>field("bytes")).isEqualTo(document.field("bytes"));
        Assert.assertEquals(extr.<String>field("utf8String"), document.field("utf8String"));
        Assert.assertEquals(extr.<Object>field("recordId"), document.field("recordId"));
        Assert.assertEquals(extr.<Object>field("bigNumber"), document.field("bigNumber"));
        Assert.assertNull(extr.field("nullField"));
        // assertEquals(extr.field("ridBag"), document.field("ridBag"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSimpleLiteralArray() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        String[] strings = new String[3];
        strings[0] = "a";
        strings[1] = "b";
        strings[2] = "c";
        document.field("listStrings", strings);
        Short[] shorts = new Short[3];
        shorts[0] = ((short) (1));
        shorts[1] = ((short) (2));
        shorts[2] = ((short) (3));
        document.field("shorts", shorts);
        Long[] longs = new Long[3];
        longs[0] = ((long) (1));
        longs[1] = ((long) (2));
        longs[2] = ((long) (3));
        document.field("longs", longs);
        Integer[] ints = new Integer[3];
        ints[0] = 1;
        ints[1] = 2;
        ints[2] = 3;
        document.field("integers", ints);
        Float[] floats = new Float[3];
        floats[0] = 1.1F;
        floats[1] = 2.2F;
        floats[2] = 3.3F;
        document.field("floats", floats);
        Double[] doubles = new Double[3];
        doubles[0] = 1.1;
        doubles[1] = 2.2;
        doubles[2] = 3.3;
        document.field("doubles", doubles);
        Date[] dates = new Date[3];
        dates[0] = new Date();
        dates[1] = new Date();
        dates[2] = new Date();
        document.field("dates", dates);
        Byte[] bytes = new Byte[3];
        bytes[0] = ((byte) (0));
        bytes[1] = ((byte) (1));
        bytes[2] = ((byte) (3));
        document.field("bytes", bytes);
        // TODO: char not currently supported in orient.
        Character[] chars = new Character[3];
        chars[0] = 'A';
        chars[1] = 'B';
        chars[2] = 'C';
        // document.field("chars", chars);
        Boolean[] booleans = new Boolean[3];
        booleans[0] = true;
        booleans[1] = false;
        booleans[2] = false;
        document.field("booleans", booleans);
        Object[] arrayNulls = new Object[3];
        // document.field("arrayNulls", arrayNulls);
        // Object[] listMixed = new ArrayList[9];
        // listMixed[0] = new Boolean(true);
        // listMixed[1] = 1;
        // listMixed[2] = (long) 5;
        // listMixed[3] = (short) 2;
        // listMixed[4] = 4.0f;
        // listMixed[5] = 7.0D;
        // listMixed[6] = "hello";
        // listMixed[7] = new Date();
        // listMixed[8] = (byte) 10;
        // document.field("listMixed", listMixed);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(((List) (extr.field("listStrings"))).toArray(), document.field("listStrings"));
        Assert.assertEquals(((List) (extr.field("integers"))).toArray(), document.field("integers"));
        Assert.assertEquals(((List) (extr.field("doubles"))).toArray(), document.field("doubles"));
        Assert.assertEquals(((List) (extr.field("dates"))).toArray(), document.field("dates"));
        Assert.assertEquals(((List) (extr.field("bytes"))).toArray(), document.field("bytes"));
        Assert.assertEquals(((List) (extr.field("booleans"))).toArray(), document.field("booleans"));
        // assertEquals(((List) extr.field("arrayNulls")).toArray(), document.field("arrayNulls"));
        // assertEquals(extr.field("listMixed"), document.field("listMixed"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSimpleLiteralList() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        document.field("listStrings", strings);
        List<Short> shorts = new ArrayList<Short>();
        shorts.add(((short) (1)));
        shorts.add(((short) (2)));
        shorts.add(((short) (3)));
        document.field("shorts", shorts);
        List<Long> longs = new ArrayList<Long>();
        longs.add(((long) (1)));
        longs.add(((long) (2)));
        longs.add(((long) (3)));
        document.field("longs", longs);
        List<Integer> ints = new ArrayList<Integer>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        document.field("integers", ints);
        List<Float> floats = new ArrayList<Float>();
        floats.add(1.1F);
        floats.add(2.2F);
        floats.add(3.3F);
        document.field("floats", floats);
        List<Double> doubles = new ArrayList<Double>();
        doubles.add(1.1);
        doubles.add(2.2);
        doubles.add(3.3);
        document.field("doubles", doubles);
        List<Date> dates = new ArrayList<Date>();
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        document.field("dates", dates);
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.add(((byte) (0)));
        bytes.add(((byte) (1)));
        bytes.add(((byte) (3)));
        document.field("bytes", bytes);
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
        document.field("booleans", booleans);
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
        document.field("listMixed", listMixed);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("listStrings"), document.field("listStrings"));
        Assert.assertEquals(extr.<Object>field("integers"), document.field("integers"));
        Assert.assertEquals(extr.<Object>field("doubles"), document.field("doubles"));
        Assert.assertEquals(extr.<Object>field("dates"), document.field("dates"));
        Assert.assertEquals(extr.<Object>field("bytes"), document.field("bytes"));
        Assert.assertEquals(extr.<Object>field("booleans"), document.field("booleans"));
        Assert.assertEquals(extr.<Object>field("listMixed"), document.field("listMixed"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSimpleLiteralSet() throws InterruptedException {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        Set<String> strings = new HashSet<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        document.field("listStrings", strings);
        Set<Short> shorts = new HashSet<Short>();
        shorts.add(((short) (1)));
        shorts.add(((short) (2)));
        shorts.add(((short) (3)));
        document.field("shorts", shorts);
        Set<Long> longs = new HashSet<Long>();
        longs.add(((long) (1)));
        longs.add(((long) (2)));
        longs.add(((long) (3)));
        document.field("longs", longs);
        Set<Integer> ints = new HashSet<Integer>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        document.field("integers", ints);
        Set<Float> floats = new HashSet<Float>();
        floats.add(1.1F);
        floats.add(2.2F);
        floats.add(3.3F);
        document.field("floats", floats);
        Set<Double> doubles = new HashSet<Double>();
        doubles.add(1.1);
        doubles.add(2.2);
        doubles.add(3.3);
        document.field("doubles", doubles);
        Set<Date> dates = new HashSet<Date>();
        dates.add(new Date());
        Thread.sleep(1);
        dates.add(new Date());
        Thread.sleep(1);
        dates.add(new Date());
        document.field("dates", dates);
        Set<Byte> bytes = new HashSet<Byte>();
        bytes.add(((byte) (0)));
        bytes.add(((byte) (1)));
        bytes.add(((byte) (3)));
        document.field("bytes", bytes);
        // TODO: char not currently supported in orient.
        Set<Character> chars = new HashSet<Character>();
        chars.add('A');
        chars.add('B');
        chars.add('C');
        // document.field("chars", chars);
        Set<Boolean> booleans = new HashSet<Boolean>();
        booleans.add(true);
        booleans.add(false);
        booleans.add(false);
        document.field("booleans", booleans);
        Set listMixed = new HashSet();
        listMixed.add(true);
        listMixed.add(1);
        listMixed.add(((long) (5)));
        listMixed.add(((short) (2)));
        listMixed.add(4.0F);
        listMixed.add(7.0);
        listMixed.add("hello");
        listMixed.add(new Date());
        listMixed.add(((byte) (10)));
        listMixed.add(new ORecordId(10, 20));
        document.field("listMixed", listMixed);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("listStrings"), document.field("listStrings"));
        Assert.assertEquals(extr.<Object>field("integers"), document.field("integers"));
        Assert.assertEquals(extr.<Object>field("doubles"), document.field("doubles"));
        Assert.assertEquals(extr.<Object>field("dates"), document.field("dates"));
        Assert.assertEquals(extr.<Object>field("bytes"), document.field("bytes"));
        Assert.assertEquals(extr.<Object>field("booleans"), document.field("booleans"));
        Assert.assertEquals(extr.<Object>field("listMixed"), document.field("listMixed"));
    }

    @Test
    public void testLinkCollections() {
        ODatabaseDocument db = new ODatabaseDocumentTx("memory:ODocumentSchemalessBinarySerializationTest").create();
        try {
            ODocument document = new ODocument();
            Set<ORecordId> linkSet = new HashSet<ORecordId>();
            linkSet.add(new ORecordId(10, 20));
            linkSet.add(new ORecordId(10, 21));
            linkSet.add(new ORecordId(10, 22));
            linkSet.add(new ORecordId(11, 22));
            document.field("linkSet", linkSet, LINKSET);
            List<ORecordId> linkList = new ArrayList<ORecordId>();
            linkList.add(new ORecordId(10, 20));
            linkList.add(new ORecordId(10, 21));
            linkList.add(new ORecordId(10, 22));
            linkList.add(new ORecordId(11, 22));
            document.field("linkList", linkList, LINKLIST);
            byte[] res = serializer.toStream(document, false);
            ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
            Assert.assertEquals(extr.fields(), document.fields());
            Assert.assertEquals(((Set<?>) (extr.field("linkSet"))).size(), ((Set<?>) (document.field("linkSet"))).size());
            Assert.assertTrue(((Set<?>) (extr.field("linkSet"))).containsAll(((Set<?>) (document.field("linkSet")))));
            Assert.assertEquals(extr.<Object>field("linkList"), document.field("linkList"));
        } finally {
            db.drop();
        }
    }

    @Test
    public void testSimpleEmbeddedDoc() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        ODocument embedded = new ODocument();
        embedded.field("name", "test");
        embedded.field("surname", "something");
        document.field("embed", embedded, EMBEDDED);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(document.fields(), extr.fields());
        ODocument emb = extr.field("embed");
        Assert.assertNotNull(emb);
        Assert.assertEquals(emb.<Object>field("name"), embedded.field("name"));
        Assert.assertEquals(emb.<Object>field("surname"), embedded.field("surname"));
    }

    @Test
    public void testSimpleMapStringLiteral() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        Map<String, String> mapString = new HashMap<String, String>();
        mapString.put("key", "value");
        mapString.put("key1", "value1");
        document.field("mapString", mapString);
        Map<String, Integer> mapInt = new HashMap<String, Integer>();
        mapInt.put("key", 2);
        mapInt.put("key1", 3);
        document.field("mapInt", mapInt);
        Map<String, Long> mapLong = new HashMap<String, Long>();
        mapLong.put("key", 2L);
        mapLong.put("key1", 3L);
        document.field("mapLong", mapLong);
        Map<String, Short> shortMap = new HashMap<String, Short>();
        shortMap.put("key", ((short) (2)));
        shortMap.put("key1", ((short) (3)));
        document.field("shortMap", shortMap);
        Map<String, Date> dateMap = new HashMap<String, Date>();
        dateMap.put("key", new Date());
        dateMap.put("key1", new Date());
        document.field("dateMap", dateMap);
        Map<String, Float> floatMap = new HashMap<String, Float>();
        floatMap.put("key", 10.0F);
        floatMap.put("key1", 11.0F);
        document.field("floatMap", floatMap);
        Map<String, Double> doubleMap = new HashMap<String, Double>();
        doubleMap.put("key", 10.0);
        doubleMap.put("key1", 11.0);
        document.field("doubleMap", doubleMap);
        Map<String, Byte> bytesMap = new HashMap<String, Byte>();
        bytesMap.put("key", ((byte) (10)));
        bytesMap.put("key1", ((byte) (11)));
        document.field("bytesMap", bytesMap);
        Map<String, String> mapWithNulls = new HashMap<String, String>();
        mapWithNulls.put("key", "dddd");
        mapWithNulls.put("key1", null);
        document.field("bytesMap", mapWithNulls);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("mapString"), document.field("mapString"));
        Assert.assertEquals(extr.<Object>field("mapLong"), document.field("mapLong"));
        Assert.assertEquals(extr.<Object>field("shortMap"), document.field("shortMap"));
        Assert.assertEquals(extr.<Object>field("dateMap"), document.field("dateMap"));
        Assert.assertEquals(extr.<Object>field("doubleMap"), document.field("doubleMap"));
        Assert.assertEquals(extr.<Object>field("bytesMap"), document.field("bytesMap"));
    }

    @Test
    public void testlistOfList() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> ls = new ArrayList<String>();
        ls.add("test1");
        ls.add("test2");
        list.add(ls);
        document.field("complexList", list);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("complexList"), document.field("complexList"));
    }

    @Test
    public void testArrayOfArray() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        String[][] array = new String[1][];
        String[] ls = new String[2];
        ls[0] = "test1";
        ls[1] = "test2";
        array[0] = ls;
        document.field("complexArray", array);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        List<List<String>> savedValue = extr.field("complexArray");
        Assert.assertEquals(savedValue.size(), array.length);
        Assert.assertEquals(savedValue.get(0).size(), array[0].length);
        Assert.assertEquals(savedValue.get(0).get(0), array[0][0]);
        Assert.assertEquals(savedValue.get(0).get(1), array[0][1]);
    }

    @Test
    public void testEmbeddedListOfEmbeddedMap() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        List<Map<String, String>> coll = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("first", "something");
        map.put("second", "somethingElse");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("first", "something");
        map2.put("second", "somethingElse");
        coll.add(map);
        coll.add(map2);
        document.field("list", coll);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("list"), document.field("list"));
    }

    @Test
    public void testMapOfEmbeddedDocument() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        ODocument embeddedInMap = new ODocument();
        embeddedInMap.field("name", "test");
        embeddedInMap.field("surname", "something");
        Map<String, ODocument> map = new HashMap<String, ODocument>();
        map.put("embedded", embeddedInMap);
        document.field("map", map, EMBEDDEDMAP);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Map<String, ODocument> mapS = extr.field("map");
        Assert.assertEquals(1, mapS.size());
        ODocument emb = mapS.get("embedded");
        Assert.assertNotNull(emb);
        Assert.assertEquals(emb.<Object>field("name"), embeddedInMap.field("name"));
        Assert.assertEquals(emb.<Object>field("surname"), embeddedInMap.field("surname"));
    }

    @Test
    public void testMapOfLink() {
        // needs a database because of the lazy loading
        ODatabaseDocument db = new ODatabaseDocumentTx("memory:ODocumentSchemalessBinarySerializationTest").create();
        try {
            ODocument document = new ODocument();
            Map<String, OIdentifiable> map = new HashMap<String, OIdentifiable>();
            map.put("link", new ORecordId(0, 0));
            document.field("map", map, LINKMAP);
            byte[] res = serializer.toStream(document, false);
            ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
            Assert.assertEquals(extr.fields(), document.fields());
            Assert.assertEquals(extr.<Object>field("map"), document.field("map"));
        } finally {
            db.drop();
        }
    }

    @Test
    public void testDocumentSimple() {
        ODatabaseDocument db = new ODatabaseDocumentTx("memory:ODocumentSchemalessBinarySerializationTest").create();
        try {
            ODocument document = new ODocument("TestClass");
            document.field("test", "test");
            byte[] res = serializer.toStream(document, false);
            ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
            // assertEquals(extr.getClassName(), document.getClassName());
            Assert.assertEquals(extr.fields(), document.fields());
            Assert.assertEquals(extr.<Object>field("test"), document.field("test"));
        } finally {
            db.drop();
        }
    }

    @Test
    public void testDocumentWithCostum() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        document.field("test", "test");
        document.field("custom", new ODocumentSchemalessBinarySerializationTest.Custom());
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.getClassName(), document.getClassName());
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("test"), document.field("test"));
        Assert.assertEquals(extr.<Object>field("custom"), document.field("custom"));
    }

    @Test
    public void testDocumentWithCostumDocument() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        document.field("test", "test");
        document.field("custom", new ODocumentSchemalessBinarySerializationTest.CustomDocument());
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.getClassName(), document.getClassName());
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("test"), document.field("test"));
        Assert.assertEquals(extr.<Object>field("custom"), document.field("custom"));
    }

    @Test(expected = OSerializationException.class)
    public void testSetOfWrongData() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        Set<Object> embeddedSet = new HashSet<Object>();
        embeddedSet.add(new ODocumentSchemalessBinarySerializationTest.WrongData());
        document.field("embeddedSet", embeddedSet, EMBEDDEDSET);
        serializer.toStream(document, false);
    }

    @Test(expected = OSerializationException.class)
    public void testListOfWrongData() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        List<Object> embeddedList = new ArrayList<Object>();
        embeddedList.add(new ODocumentSchemalessBinarySerializationTest.WrongData());
        document.field("embeddedList", embeddedList, EMBEDDEDLIST);
        serializer.toStream(document, false);
    }

    @Test(expected = OSerializationException.class)
    public void testMapOfWrongData() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        Map<String, Object> embeddedMap = new HashMap<String, Object>();
        embeddedMap.put("name", new ODocumentSchemalessBinarySerializationTest.WrongData());
        document.field("embeddedMap", embeddedMap, EMBEDDEDMAP);
        serializer.toStream(document, false);
    }

    @Test(expected = ClassCastException.class)
    public void testLinkSetOfWrongData() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        Set<Object> linkSet = new HashSet<Object>();
        linkSet.add(new ODocumentSchemalessBinarySerializationTest.WrongData());
        document.field("linkSet", linkSet, LINKSET);
        serializer.toStream(document, false);
    }

    @Test(expected = ClassCastException.class)
    public void testLinkListOfWrongData() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        List<Object> linkList = new ArrayList<Object>();
        linkList.add(new ODocumentSchemalessBinarySerializationTest.WrongData());
        document.field("linkList", linkList, LINKLIST);
        serializer.toStream(document, false);
    }

    @Test(expected = ClassCastException.class)
    public void testLinkMapOfWrongData() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        Map<String, Object> linkMap = new HashMap<String, Object>();
        linkMap.put("name", new ODocumentSchemalessBinarySerializationTest.WrongData());
        document.field("linkMap", linkMap, LINKMAP);
        serializer.toStream(document, false);
    }

    @Test(expected = OSerializationException.class)
    public void testFieldWrongData() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        document.field("wrongData", new ODocumentSchemalessBinarySerializationTest.WrongData());
        serializer.toStream(document, false);
    }

    @Test
    public void testCollectionOfEmbeddedDocument() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        ODocument embeddedInList = new ODocument();
        embeddedInList.field("name", "test");
        embeddedInList.field("surname", "something");
        ODocument embeddedInList2 = new ODocument();
        embeddedInList2.field("name", "test1");
        embeddedInList2.field("surname", "something2");
        List<ODocument> embeddedList = new ArrayList<ODocument>();
        embeddedList.add(embeddedInList);
        embeddedList.add(embeddedInList2);
        embeddedList.add(null);
        embeddedList.add(new ODocument());
        document.field("embeddedList", embeddedList, EMBEDDEDLIST);
        ODocument embeddedInSet = new ODocument();
        embeddedInSet.field("name", "test2");
        embeddedInSet.field("surname", "something3");
        ODocument embeddedInSet2 = new ODocument();
        embeddedInSet2.field("name", "test5");
        embeddedInSet2.field("surname", "something6");
        Set<ODocument> embeddedSet = new HashSet<ODocument>();
        embeddedSet.add(embeddedInSet);
        embeddedSet.add(embeddedInSet2);
        embeddedSet.add(new ODocument());
        document.field("embeddedSet", embeddedSet, EMBEDDEDSET);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        List<ODocument> ser = extr.field("embeddedList");
        Assert.assertEquals(ser.size(), 4);
        Assert.assertNotNull(ser.get(0));
        Assert.assertNotNull(ser.get(1));
        Assert.assertNull(ser.get(2));
        Assert.assertNotNull(ser.get(3));
        ODocument inList = ser.get(0);
        Assert.assertNotNull(inList);
        Assert.assertEquals(inList.<Object>field("name"), embeddedInList.field("name"));
        Assert.assertEquals(inList.<Object>field("surname"), embeddedInList.field("surname"));
        Set<ODocument> setEmb = extr.field("embeddedSet");
        Assert.assertEquals(setEmb.size(), 3);
        boolean ok = false;
        for (ODocument inSet : setEmb) {
            Assert.assertNotNull(inSet);
            if ((embeddedInSet.field("name").equals(inSet.field("name"))) && (embeddedInSet.field("surname").equals(inSet.field("surname"))))
                ok = true;

        }
        Assert.assertTrue("not found record in the set after serilize", ok);
    }

    @Test
    public void testSerializableValue() {
        ODocument document = new ODocument();
        SimpleSerializableClass ser = new SimpleSerializableClass();
        ser.name = "testName";
        document.field("seri", ser);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertNotNull(extr.field("seri"));
        Assert.assertEquals(extr.fieldType("seri"), CUSTOM);
        SimpleSerializableClass newser = extr.field("seri");
        Assert.assertEquals(newser.name, ser.name);
    }

    @Test
    public void testFieldNames() {
        ODocument document = new ODocument();
        document.fields("a", 1, "b", 2, "c", 3);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        final String[] fields = extr.fieldNames();
        Assert.assertNotNull(fields);
        Assert.assertEquals(fields.length, 3);
        Assert.assertEquals(fields[0], "a");
        Assert.assertEquals(fields[1], "b");
        Assert.assertEquals(fields[2], "c");
    }

    @Test
    public void testFieldNamesRaw() {
        ODocument document = new ODocument();
        document.fields("a", 1, "b", 2, "c", 3);
        byte[] res = serializer.toStream(document, false);
        final String[] fields = serializer.getFieldNamesRoot(document, res);
        Assert.assertNotNull(fields);
        Assert.assertEquals(fields.length, 3);
        Assert.assertEquals(fields[0], "a");
        Assert.assertEquals(fields[1], "b");
        Assert.assertEquals(fields[2], "c");
    }

    @Test
    public void testPartial() {
        ODocument document = new ODocument();
        document.field("name", "name");
        document.field("age", 20);
        document.field("youngAge", ((short) (20)));
        document.field("oldAge", ((long) (20)));
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{ "name", "age" })));
        Assert.assertEquals(document.field("name"), extr.<Object>field("name"));
        Assert.assertEquals(document.<Object>field("age"), extr.field("age"));
        Assert.assertNull(extr.field("youngAge"));
        Assert.assertNull(extr.field("oldAge"));
    }

    @Test
    public void testWithRemove() {
        ODocument document = new ODocument();
        document.field("name", "name");
        document.field("age", 20);
        document.field("youngAge", ((short) (20)));
        document.field("oldAge", ((long) (20)));
        document.removeField("oldAge");
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(document.field("name"), extr.<Object>field("name"));
        Assert.assertEquals(document.<Object>field("age"), extr.field("age"));
        Assert.assertEquals(document.<Object>field("youngAge"), extr.field("youngAge"));
        Assert.assertNull(extr.field("oldAge"));
    }

    @Test
    public void testPartialCustom() {
        ODocument document = new ODocument();
        document.field("name", "name");
        document.field("age", 20);
        document.field("youngAge", ((short) (20)));
        document.field("oldAge", ((long) (20)));
        byte[] res = serializer.toStream(document, false);
        ODocument extr = new ODocument(res);
        ORecordInternal.setRecordSerializer(extr, serializer);
        Assert.assertEquals(document.field("name"), extr.<Object>field("name"));
        Assert.assertEquals(document.<Object>field("age"), extr.field("age"));
        Assert.assertEquals(document.<Object>field("youngAge"), extr.field("youngAge"));
        Assert.assertEquals(document.<Object>field("oldAge"), extr.field("oldAge"));
        Assert.assertEquals(document.fieldNames().length, extr.fieldNames().length);
    }

    @Test
    public void testPartialNotFound() {
        // this test want to do only for ORecordSerializerNetworkV37
        if ((serializer) instanceof ORecordSerializerNetworkV37) {
            System.out.println("ISIDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            ODocument document = new ODocument();
            document.field("name", "name");
            document.field("age", 20);
            document.field("youngAge", ((short) (20)));
            document.field("oldAge", ((long) (20)));
            byte[] res = serializer.toStream(document, false);
            ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{ "foo" })));
            Assert.assertEquals(document.field("name"), extr.<Object>field("name"));
            Assert.assertEquals(document.<Object>field("age"), extr.field("age"));
            Assert.assertEquals(document.<Object>field("youngAge"), extr.field("youngAge"));
            Assert.assertEquals(document.<Object>field("oldAge"), extr.field("oldAge"));
        }
    }

    @Test
    public void testListOfMapsWithNull() {
        ODatabaseRecordThreadLocal.instance().remove();
        ODocument document = new ODocument();
        List lista = new ArrayList<>();
        Map mappa = new LinkedHashMap<>();
        mappa.put("prop1", "val1");
        mappa.put("prop2", null);
        lista.add(mappa);
        mappa = new HashMap();
        mappa.put("prop", "val");
        lista.add(mappa);
        document.setProperty("list", lista);
        byte[] res = serializer.toStream(document, false);
        ODocument extr = ((ODocument) (serializer.fromStream(res, new ODocument(), new String[]{  })));
        Assert.assertEquals(extr.fields(), document.fields());
        Assert.assertEquals(extr.<Object>field("list"), document.field("list"));
    }

    public static class Custom implements OSerializableStream {
        byte[] bytes = new byte[10];

        @Override
        public OSerializableStream fromStream(byte[] iStream) throws OSerializationException {
            bytes = iStream;
            return this;
        }

        @Override
        public byte[] toStream() throws OSerializationException {
            for (int i = 0; i < (bytes.length); i++) {
                bytes[i] = ((byte) (i));
            }
            return bytes;
        }

        @Override
        public boolean equals(Object obj) {
            return ((obj != null) && (obj instanceof ODocumentSchemalessBinarySerializationTest.Custom)) && (Arrays.equals(bytes, ((ODocumentSchemalessBinarySerializationTest.Custom) (obj)).bytes));
        }
    }

    public static class CustomDocument implements ODocumentSerializable {
        private ODocument document;

        @Override
        public void fromDocument(ODocument document) {
            this.document = document;
        }

        @Override
        public ODocument toDocument() {
            document = new ODocument();
            document.field("test", "some strange content");
            return document;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj != null) && (document.field("test").equals(((ODocumentSchemalessBinarySerializationTest.CustomDocument) (obj)).document.field("test")));
        }
    }

    private class WrongData {}
}

