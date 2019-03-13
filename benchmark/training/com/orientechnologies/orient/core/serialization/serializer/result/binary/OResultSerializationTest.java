package com.orientechnologies.orient.core.serialization.serializer.result.binary;


import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by luigidellaquila on 09/12/16.
 */
public class OResultSerializationTest {
    protected OResultSerializerNetwork serializer;

    @Test
    public void testSimpleSerialization() {
        OResultInternal document = new OResultInternal();
        document.setProperty("name", "name");
        document.setProperty("age", 20);
        document.setProperty("youngAge", ((short) (20)));
        document.setProperty("oldAge", ((long) (20)));
        document.setProperty("heigth", 12.5F);
        document.setProperty("bitHeigth", 12.5);
        document.setProperty("class", ((byte) ('C')));
        document.setProperty("character", 'C');
        document.setProperty("alive", true);
        document.setProperty("date", new Date());
        document.setProperty("recordId", new ORecordId(10, 10));
        OResultInternal extr = serializeDeserialize(document);
        Assert.assertEquals(extr.getPropertyNames(), document.getPropertyNames());
        Assert.assertEquals(extr.<String>getProperty("name"), document.getProperty("name"));
        Assert.assertEquals(extr.<String>getProperty("age"), document.getProperty("age"));
        Assert.assertEquals(extr.<String>getProperty("youngAge"), document.getProperty("youngAge"));
        Assert.assertEquals(extr.<String>getProperty("oldAge"), document.getProperty("oldAge"));
        Assert.assertEquals(extr.<String>getProperty("heigth"), document.getProperty("heigth"));
        Assert.assertEquals(extr.<String>getProperty("bitHeigth"), document.getProperty("bitHeigth"));
        Assert.assertEquals(extr.<String>getProperty("class"), document.getProperty("class"));
        // TODO fix char management issue:#2427
        // assertEquals(document.getProperty("character"), extr.getProperty("character"));
        Assert.assertEquals(extr.<String>getProperty("alive"), document.getProperty("alive"));
        Assert.assertEquals(extr.<String>getProperty("date"), document.getProperty("date"));
        // assertEquals(extr.field("recordId"), document.field("recordId"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSimpleLiteralList() {
        OResultInternal document = new OResultInternal();
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        document.setProperty("listStrings", strings);
        List<Short> shorts = new ArrayList<Short>();
        shorts.add(((short) (1)));
        shorts.add(((short) (2)));
        shorts.add(((short) (3)));
        document.setProperty("shorts", shorts);
        List<Long> longs = new ArrayList<Long>();
        longs.add(((long) (1)));
        longs.add(((long) (2)));
        longs.add(((long) (3)));
        document.setProperty("longs", longs);
        List<Integer> ints = new ArrayList<Integer>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        document.setProperty("integers", ints);
        List<Float> floats = new ArrayList<Float>();
        floats.add(1.1F);
        floats.add(2.2F);
        floats.add(3.3F);
        document.setProperty("floats", floats);
        List<Double> doubles = new ArrayList<Double>();
        doubles.add(1.1);
        doubles.add(2.2);
        doubles.add(3.3);
        document.setProperty("doubles", doubles);
        List<Date> dates = new ArrayList<Date>();
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        document.setProperty("dates", dates);
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.add(((byte) (0)));
        bytes.add(((byte) (1)));
        bytes.add(((byte) (3)));
        document.setProperty("bytes", bytes);
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
        document.setProperty("booleans", booleans);
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
        listMixed.add(null);
        document.setProperty("listMixed", listMixed);
        OResult extr = serializeDeserialize(document);
        Assert.assertEquals(extr.getPropertyNames(), document.getPropertyNames());
        Assert.assertEquals(extr.<String>getProperty("listStrings"), document.getProperty("listStrings"));
        Assert.assertEquals(extr.<String>getProperty("integers"), document.getProperty("integers"));
        Assert.assertEquals(extr.<String>getProperty("doubles"), document.getProperty("doubles"));
        Assert.assertEquals(extr.<String>getProperty("dates"), document.getProperty("dates"));
        Assert.assertEquals(extr.<String>getProperty("bytes"), document.getProperty("bytes"));
        Assert.assertEquals(extr.<String>getProperty("booleans"), document.getProperty("booleans"));
        Assert.assertEquals(extr.<String>getProperty("listMixed"), document.getProperty("listMixed"));
    }

    @Test
    public void testSimpleMapStringLiteral() {
        OResultInternal document = new OResultInternal();
        Map<String, String> mapString = new HashMap<String, String>();
        mapString.put("key", "value");
        mapString.put("key1", "value1");
        document.setProperty("mapString", mapString);
        Map<String, Integer> mapInt = new HashMap<String, Integer>();
        mapInt.put("key", 2);
        mapInt.put("key1", 3);
        document.setProperty("mapInt", mapInt);
        Map<String, Long> mapLong = new HashMap<String, Long>();
        mapLong.put("key", 2L);
        mapLong.put("key1", 3L);
        document.setProperty("mapLong", mapLong);
        Map<String, Short> shortMap = new HashMap<String, Short>();
        shortMap.put("key", ((short) (2)));
        shortMap.put("key1", ((short) (3)));
        document.setProperty("shortMap", shortMap);
        Map<String, Date> dateMap = new HashMap<String, Date>();
        dateMap.put("key", new Date());
        dateMap.put("key1", new Date());
        document.setProperty("dateMap", dateMap);
        Map<String, Float> floatMap = new HashMap<String, Float>();
        floatMap.put("key", 10.0F);
        floatMap.put("key1", 11.0F);
        document.setProperty("floatMap", floatMap);
        Map<String, Double> doubleMap = new HashMap<String, Double>();
        doubleMap.put("key", 10.0);
        doubleMap.put("key1", 11.0);
        document.setProperty("doubleMap", doubleMap);
        Map<String, Byte> bytesMap = new HashMap<String, Byte>();
        bytesMap.put("key", ((byte) (10)));
        bytesMap.put("key1", ((byte) (11)));
        document.setProperty("bytesMap", bytesMap);
        OResult extr = serializeDeserialize(document);
        Assert.assertEquals(extr.getPropertyNames(), document.getPropertyNames());
        Assert.assertEquals(extr.<String>getProperty("mapString"), document.getProperty("mapString"));
        Assert.assertEquals(extr.<String>getProperty("mapLong"), document.getProperty("mapLong"));
        Assert.assertEquals(extr.<String>getProperty("shortMap"), document.getProperty("shortMap"));
        Assert.assertEquals(extr.<String>getProperty("dateMap"), document.getProperty("dateMap"));
        Assert.assertEquals(extr.<String>getProperty("doubleMap"), document.getProperty("doubleMap"));
        Assert.assertEquals(extr.<String>getProperty("bytesMap"), document.getProperty("bytesMap"));
    }

    @Test
    public void testSimpleEmbeddedDoc() {
        OResultInternal document = new OResultInternal();
        OResultInternal embedded = new OResultInternal();
        embedded.setProperty("name", "test");
        embedded.setProperty("surname", "something");
        document.setProperty("embed", embedded);
        OResult extr = serializeDeserialize(document);
        Assert.assertEquals(document.getPropertyNames(), extr.getPropertyNames());
        OResult emb = extr.getProperty("embed");
        Assert.assertNotNull(emb);
        Assert.assertEquals(emb.<String>getProperty("name"), embedded.getProperty("name"));
        Assert.assertEquals(emb.<String>getProperty("surname"), embedded.getProperty("surname"));
    }

    @Test
    public void testMapOfEmbeddedDocument() {
        OResultInternal document = new OResultInternal();
        OResultInternal embeddedInMap = new OResultInternal();
        embeddedInMap.setProperty("name", "test");
        embeddedInMap.setProperty("surname", "something");
        Map<String, OResult> map = new HashMap<String, OResult>();
        map.put("embedded", embeddedInMap);
        document.setProperty("map", map);
        OResult extr = serializeDeserialize(document);
        Map<String, OResult> mapS = extr.getProperty("map");
        Assert.assertEquals(1, mapS.size());
        OResult emb = mapS.get("embedded");
        Assert.assertNotNull(emb);
        Assert.assertEquals(emb.<String>getProperty("name"), embeddedInMap.getProperty("name"));
        Assert.assertEquals(emb.<String>getProperty("surname"), embeddedInMap.getProperty("surname"));
    }

    @Test
    public void testCollectionOfEmbeddedDocument() {
        OResultInternal document = new OResultInternal();
        OResultInternal embeddedInList = new OResultInternal();
        embeddedInList.setProperty("name", "test");
        embeddedInList.setProperty("surname", "something");
        List<OResult> embeddedList = new ArrayList<OResult>();
        embeddedList.add(embeddedInList);
        document.setProperty("embeddedList", embeddedList);
        OResultInternal embeddedInSet = new OResultInternal();
        embeddedInSet.setProperty("name", "test1");
        embeddedInSet.setProperty("surname", "something2");
        Set<OResult> embeddedSet = new HashSet<>();
        embeddedSet.add(embeddedInSet);
        document.setProperty("embeddedSet", embeddedSet);
        OResult extr = serializeDeserialize(document);
        List<OResult> ser = extr.getProperty("embeddedList");
        Assert.assertEquals(1, ser.size());
        OResult inList = ser.get(0);
        Assert.assertNotNull(inList);
        Assert.assertEquals(inList.<String>getProperty("name"), embeddedInList.getProperty("name"));
        Assert.assertEquals(inList.<String>getProperty("surname"), embeddedInList.getProperty("surname"));
        Set<OResult> setEmb = extr.getProperty("embeddedSet");
        Assert.assertEquals(1, setEmb.size());
        OResult inSet = setEmb.iterator().next();
        Assert.assertNotNull(inSet);
        Assert.assertEquals(inSet.<String>getProperty("name"), embeddedInSet.getProperty("name"));
        Assert.assertEquals(inSet.<String>getProperty("surname"), embeddedInSet.getProperty("surname"));
    }

    @Test
    public void testMetadataSerialization() {
        OResultInternal document = new OResultInternal();
        document.setProperty("name", "foo");
        document.setMetadata("name", "bar");
        document.setMetadata("age", 20);
        document.setMetadata("youngAge", ((short) (20)));
        document.setMetadata("oldAge", ((long) (20)));
        document.setMetadata("heigth", 12.5F);
        document.setMetadata("bitHeigth", 12.5);
        document.setMetadata("class", ((byte) ('C')));
        document.setMetadata("alive", true);
        document.setMetadata("date", new Date());
        OResultInternal extr = serializeDeserialize(document);
        Assert.assertEquals(extr.getPropertyNames(), document.getPropertyNames());
        Assert.assertEquals(extr.<String>getProperty("foo"), document.getProperty("foo"));
        Assert.assertEquals("foo", extr.<String>getProperty("name"));
        Assert.assertEquals(extr.getMetadataKeys(), document.getMetadataKeys());
        Assert.assertEquals("bar", extr.<String>getMetadata("name"));
        Assert.assertEquals(extr.<String>getMetadata("name"), document.getMetadata("name"));
        Assert.assertEquals(extr.<String>getMetadata("age"), document.getMetadata("age"));
        Assert.assertEquals(extr.<String>getMetadata("youngAge"), document.getMetadata("youngAge"));
        Assert.assertEquals(extr.<String>getMetadata("oldAge"), document.getMetadata("oldAge"));
        Assert.assertEquals(extr.<String>getMetadata("heigth"), document.getMetadata("heigth"));
        Assert.assertEquals(extr.<String>getMetadata("bitHeigth"), document.getMetadata("bitHeigth"));
        Assert.assertEquals(extr.<String>getMetadata("class"), document.getMetadata("class"));
        Assert.assertEquals(extr.<String>getMetadata("alive"), document.getMetadata("alive"));
        Assert.assertEquals(extr.<String>getMetadata("date"), document.getMetadata("date"));
    }
}

