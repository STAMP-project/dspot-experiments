package com.orientechnologies.orient.core.record.impl;


import OType.DOUBLE;
import OType.FLOAT;
import OType.INTEGER;
import OType.LONG;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ODocumentFieldConversionTest {
    private ODatabaseDocumentTx db;

    private OClass clazz;

    @Test
    public void testDateToSchemaConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Calendar calendare = Calendar.getInstance();
        calendare.set(Calendar.MILLISECOND, 0);
        Date date = calendare.getTime();
        String dateString = db.getStorage().getConfiguration().getDateTimeFormatInstance().format(date);
        ODocument doc = new ODocument(clazz);
        doc.field("date", dateString);
        Assert.assertTrue(((doc.field("date")) instanceof Date));
        Assert.assertEquals(date, doc.field("date"));
        doc.field("date", 20304);
        Assert.assertTrue(((doc.field("date")) instanceof Date));
        Assert.assertEquals(20304L, ((Date) (doc.field("date"))).getTime());
        doc.field("date", 4.343244E7F);
        Assert.assertTrue(((doc.field("date")) instanceof Date));
        Assert.assertEquals(43432440L, ((Date) (doc.field("date"))).getTime());
        doc.field("date", 4.3432444E7);
        Assert.assertTrue(((doc.field("date")) instanceof Date));
        Assert.assertEquals(43432444L, ((Date) (doc.field("date"))).getTime());
        doc.field("date", 20304L);
        Assert.assertTrue(((doc.field("date")) instanceof Date));
        Assert.assertEquals(20304L, ((Date) (doc.field("date"))).getTime());
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralToSchemaConversionInteger() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("integer", 2L);
        Assert.assertTrue(((doc.field("integer")) instanceof Integer));
        // assertEquals(2, doc.field("integer"));
        assertThat(doc.<Integer>field("integer")).isEqualTo(2);
        doc.field("integer", 3.0F);
        Assert.assertTrue(((doc.field("integer")) instanceof Integer));
        // assertEquals(3, doc.field("integer"));
        assertThat(doc.<Integer>field("integer")).isEqualTo(3);
        doc.field("integer", 4.0);
        Assert.assertTrue(((doc.field("integer")) instanceof Integer));
        // assertEquals(4, doc.field("integer"));
        assertThat(doc.<Integer>field("integer")).isEqualTo(4);
        doc.field("integer", "5");
        Assert.assertTrue(((doc.field("integer")) instanceof Integer));
        // assertEquals(5, doc.field("integer"));
        assertThat(doc.<Integer>field("integer")).isEqualTo(5);
        doc.field("integer", new BigDecimal("6"));
        Assert.assertTrue(((doc.field("integer")) instanceof Integer));
        // assertEquals(6, doc.field("integer"));
        assertThat(doc.<Integer>field("integer")).isEqualTo(6);
        // doc.field("integer", true);
        // assertTrue(doc.field("integer") instanceof Integer);
        // assertEquals(1, doc.field("integer"));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralToSchemaConversionString() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("string", 1);
        Assert.assertTrue(((doc.field("string")) instanceof String));
        Assert.assertEquals("1", doc.field("string"));
        doc.field("string", 2L);
        Assert.assertTrue(((doc.field("string")) instanceof String));
        Assert.assertEquals("2", doc.field("string"));
        doc.field("string", 3.0F);
        Assert.assertTrue(((doc.field("string")) instanceof String));
        Assert.assertEquals("3.0", doc.field("string"));
        doc.field("string", 4.0);
        Assert.assertTrue(((doc.field("string")) instanceof String));
        Assert.assertEquals("4.0", doc.field("string"));
        doc.field("string", new BigDecimal("6"));
        Assert.assertTrue(((doc.field("string")) instanceof String));
        Assert.assertEquals("6", doc.field("string"));
        doc.field("string", true);
        Assert.assertTrue(((doc.field("string")) instanceof String));
        Assert.assertEquals("true", doc.field("string"));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralToSchemaConversionFloat() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("float", 1);
        Assert.assertTrue(((doc.field("float")) instanceof Float));
        // assertEquals(1f, doc.field("float"));
        assertThat(doc.<Float>field("float")).isEqualTo(1.0F);
        doc.field("float", 2L);
        Assert.assertTrue(((doc.field("float")) instanceof Float));
        // assertEquals(2f, doc.field("float"));
        assertThat(doc.<Float>field("float")).isEqualTo(2.0F);
        doc.field("float", "3");
        Assert.assertTrue(((doc.field("float")) instanceof Float));
        assertThat(doc.<Float>field("float")).isEqualTo(3.0F);
        doc.field("float", 4.0);
        Assert.assertTrue(((doc.field("float")) instanceof Float));
        // assertEquals(4f, doc.field("float"));
        assertThat(doc.<Float>field("float")).isEqualTo(4.0F);
        doc.field("float", new BigDecimal("6"));
        Assert.assertTrue(((doc.field("float")) instanceof Float));
        // assertEquals(6f, doc.field("float"));
        assertThat(doc.<Float>field("float")).isEqualTo(6.0F);
        // doc.field("float", true);
        // assertTrue(doc.field("float") instanceof Float);
        // assertEquals(1f, doc.field("float"));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralToSchemaConversionDouble() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("double", 1);
        Assert.assertTrue(((doc.field("double")) instanceof Double));
        // assertEquals(1d, doc.field("double"));
        assertThat(doc.<Double>field("double")).isEqualTo(1.0);
        doc.field("double", 2L);
        Assert.assertTrue(((doc.field("double")) instanceof Double));
        // assertEquals(2d, doc.field("double"));
        assertThat(doc.<Double>field("double")).isEqualTo(2.0);
        doc.field("double", "3");
        Assert.assertTrue(((doc.field("double")) instanceof Double));
        // assertEquals(3d, doc.field("double"));
        assertThat(doc.<Double>field("double")).isEqualTo(3.0);
        doc.field("double", 4.0F);
        Assert.assertTrue(((doc.field("double")) instanceof Double));
        // assertEquals(4d, doc.field("double"));
        assertThat(doc.<Double>field("double")).isEqualTo(4.0);
        doc.field("double", new BigDecimal("6"));
        Assert.assertTrue(((doc.field("double")) instanceof Double));
        // assertEquals(6d, doc.field("double"));
        assertThat(doc.<Double>field("double")).isEqualTo(6.0);
        // doc.field("double", true);
        // assertTrue(doc.field("double") instanceof Double);
        // assertEquals(1d, doc.field("double"));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralToSchemaConversionLong() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("long", 1);
        Assert.assertTrue(((doc.field("long")) instanceof Long));
        // assertEquals(1L, doc.field("long"));
        assertThat(doc.<Long>field("long")).isEqualTo(1L);
        doc.field("long", 2.0F);
        Assert.assertTrue(((doc.field("long")) instanceof Long));
        // assertEquals(2L, doc.field("long"));
        assertThat(doc.<Long>field("long")).isEqualTo(2L);
        doc.field("long", "3");
        Assert.assertTrue(((doc.field("long")) instanceof Long));
        // assertEquals(3L, doc.field("long"));
        assertThat(doc.<Long>field("long")).isEqualTo(3L);
        doc.field("long", 4.0);
        Assert.assertTrue(((doc.field("long")) instanceof Long));
        // assertEquals(4L, doc.field("long"));
        assertThat(doc.<Long>field("long")).isEqualTo(4L);
        doc.field("long", new BigDecimal("6"));
        Assert.assertTrue(((doc.field("long")) instanceof Long));
        // assertEquals(6L, doc.field("long"));
        assertThat(doc.<Long>field("long")).isEqualTo(6L);
        // doc.field("long", true);
        // assertTrue(doc.field("long") instanceof Long);
        // assertEquals(1, doc.field("long"));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralToSchemaConversionBoolean() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("boolean", 0);
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(false, doc.field("boolean"));
        doc.field("boolean", 1L);
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(true, doc.field("boolean"));
        doc.field("boolean", 2.0F);
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(true, doc.field("boolean"));
        doc.field("boolean", "true");
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(true, doc.field("boolean"));
        doc.field("boolean", 4.0);
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(true, doc.field("boolean"));
        doc.field("boolean", new BigDecimal("6"));
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(true, doc.field("boolean"));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralToSchemaConversionDecimal() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("decimal", 0);
        Assert.assertTrue(((doc.field("decimal")) instanceof BigDecimal));
        Assert.assertEquals(BigDecimal.ZERO, doc.field("decimal"));
        doc.field("decimal", 1L);
        Assert.assertTrue(((doc.field("decimal")) instanceof BigDecimal));
        Assert.assertEquals(BigDecimal.ONE, doc.field("decimal"));
        doc.field("decimal", 2.0F);
        Assert.assertTrue(((doc.field("decimal")) instanceof BigDecimal));
        Assert.assertEquals(new BigDecimal("2.0"), doc.field("decimal"));
        doc.field("decimal", "3");
        Assert.assertTrue(((doc.field("decimal")) instanceof BigDecimal));
        Assert.assertEquals(new BigDecimal("3"), doc.field("decimal"));
        doc.field("decimal", 4.0);
        Assert.assertTrue(((doc.field("decimal")) instanceof BigDecimal));
        Assert.assertEquals(new BigDecimal("4.0"), doc.field("decimal"));
        doc.field("boolean", new BigDecimal("6"));
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(true, doc.field("boolean"));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testConversionAlsoWithWrongType() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument(clazz);
        doc.field("float", 2, INTEGER);
        Assert.assertTrue(((doc.field("float")) instanceof Float));
        // assertEquals(2f, doc.field("float"));
        assertThat(doc.<Float>field("float")).isEqualTo(2.0F);
        doc.field("integer", 3.0F, FLOAT);
        Assert.assertTrue(((doc.field("integer")) instanceof Integer));
        // assertEquals(3, doc.field("integer"));
        assertThat(doc.<Integer>field("integer")).isEqualTo(3);
        doc.field("double", 1L, LONG);
        Assert.assertTrue(((doc.field("double")) instanceof Double));
        // assertEquals(1d, doc.field("double"));
        assertThat(doc.<Double>field("double")).isEqualTo(1.0);
        doc.field("long", 1.0, DOUBLE);
        Assert.assertTrue(((doc.field("long")) instanceof Long));
        // assertEquals(1L, doc.field("long"));
        assertThat(doc.<Long>field("long")).isEqualTo(1L);
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testLiteralConversionAfterSchemaSet() {
        ODatabaseRecordThreadLocal.instance().set(db);
        ODocument doc = new ODocument();
        doc.field("float", 1);
        doc.field("integer", 3.0F);
        doc.field("double", 2L);
        doc.field("long", 2.0);
        doc.field("string", 25);
        doc.field("boolean", "true");
        doc.field("decimal", (-1));
        doc.field("date", 20304L);
        doc.setClass(clazz);
        Assert.assertTrue(((doc.field("float")) instanceof Float));
        // assertEquals(1f, doc.field("float"));
        assertThat(doc.<Float>field("float")).isEqualTo(1.0F);
        Assert.assertTrue(((doc.field("integer")) instanceof Integer));
        // assertEquals(3, doc.field("integer"));
        assertThat(doc.<Integer>field("integer")).isEqualTo(3);
        Assert.assertTrue(((doc.field("long")) instanceof Long));
        // assertEquals(2L, doc.field("long"));
        assertThat(doc.<Long>field("long")).isEqualTo(2L);
        Assert.assertTrue(((doc.field("string")) instanceof String));
        Assert.assertEquals("25", doc.field("string"));
        Assert.assertTrue(((doc.field("boolean")) instanceof Boolean));
        Assert.assertEquals(true, doc.field("boolean"));
        Assert.assertTrue(((doc.field("decimal")) instanceof BigDecimal));
        Assert.assertEquals(new BigDecimal((-1)), doc.field("decimal"));
        Assert.assertTrue(((doc.field("date")) instanceof Date));
        Assert.assertEquals(20304L, ((Date) (doc.field("date"))).getTime());
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testListByteCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("byteList", values);
        fillList(values);
        Set set = new HashSet();
        doc.field("byteSet", set);
        fillSet(set);
        doc.autoConvertValues();
        values = doc.field("byteList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof Byte));
            Assert.assertEquals(val, ((byte) (1)));
        }
        set = doc.field("byteSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof Byte));
        }
        for (int i = 1; i < 7; i++) {
            Assert.assertTrue(set.contains(((byte) (i))));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testCollectionIntegerCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("integerList", values);
        fillList(values);
        Set set = new HashSet();
        doc.field("integerSet", set);
        fillSet(set);
        doc.autoConvertValues();
        values = doc.field("integerList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof Integer));
            Assert.assertEquals(((Integer) (1)), val);
        }
        set = doc.field("integerSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof Integer));
        }
        for (int i = 1; i < 7; i++) {
            Assert.assertTrue(set.contains(i));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testCollectionLongCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("longList", values);
        fillList(values);
        Set set = new HashSet();
        doc.field("longSet", set);
        fillSet(set);
        doc.autoConvertValues();
        values = doc.field("longList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof Long));
            Assert.assertEquals(((Long) (1L)), val);
        }
        set = doc.field("longSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof Long));
        }
        for (int i = 1; i < 7; i++) {
            Assert.assertTrue(set.contains(((long) (i))));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testCollectionBooleanCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("booleanList", values);
        values.add(((byte) (1)));
        values.add("true");
        values.add(1L);
        values.add(1);
        values.add(((short) (1)));
        values.add(1.0F);
        values.add(1.0);
        values.add(BigDecimal.ONE);
        Set set = new HashSet();
        doc.field("booleanSet", set);
        set.add(((byte) (1)));
        set.add(2L);
        set.add(3);
        set.add(((short) (4)));
        set.add("true");
        set.add(6.0F);
        set.add(7.0);
        set.add(new BigDecimal(8));
        doc.autoConvertValues();
        values = doc.field("booleanList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof Boolean));
            Assert.assertEquals(((Boolean) (true)), val);
        }
        set = doc.field("booleanSet");
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(((set.iterator().next()) instanceof Boolean));
        Assert.assertTrue(((Boolean) (set.iterator().next())));
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testCollectionStringCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("stringList", values);
        values.add(((byte) (1)));
        values.add(1L);
        values.add(1);
        values.add(((short) (1)));
        values.add(1.0F);
        values.add(1.0);
        values.add(BigDecimal.ONE);
        Set set = new HashSet();
        doc.field("stringSet", set);
        fillSet(set);
        doc.autoConvertValues();
        values = doc.field("stringList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof String));
            Assert.assertTrue(((String) (val)).contains("1"));
        }
        set = doc.field("stringSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof String));
        }
        for (int i = 1; i < 7; i++) {
            boolean contain = false;
            for (Object object : set) {
                if (object.toString().contains(((Integer) (i)).toString()))
                    contain = true;

            }
            Assert.assertTrue(contain);
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testCollectionFloatCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("floatList", values);
        fillList(values);
        Set set = new HashSet();
        doc.field("floatSet", set);
        fillSet(set);
        doc.autoConvertValues();
        values = doc.field("floatList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof Float));
            Assert.assertEquals(((Float) (1.0F)), val);
        }
        set = doc.field("floatSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof Float));
        }
        for (int i = 1; i < 7; i++) {
            Assert.assertTrue(set.contains(new Float(i)));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testCollectionDoubleCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("doubleList", values);
        fillList(values);
        Set set = new HashSet();
        doc.field("doubleSet", set);
        fillSet(set);
        doc.autoConvertValues();
        values = doc.field("doubleList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof Double));
            Assert.assertEquals(((Double) (1.0)), val);
        }
        set = doc.field("doubleSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof Double));
        }
        for (int i = 1; i < 7; i++) {
            Assert.assertTrue(set.contains(new Double(i)));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testCollectionDecimalCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("decimalList", values);
        fillList(values);
        Set set = new HashSet();
        doc.field("decimalSet", set);
        fillSet(set);
        doc.autoConvertValues();
        values = doc.field("decimalList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof BigDecimal));
            Assert.assertTrue(val.toString().contains("1"));
        }
        set = doc.field("decimalSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof BigDecimal));
        }
        for (int i = 1; i < 7; i++) {
            boolean contain = false;
            for (Object object : set) {
                if (object.toString().contains(((Integer) (i)).toString()))
                    contain = true;

            }
            Assert.assertTrue(contain);
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testCollectionDateCoversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        List values = new ArrayList();
        ODocument doc = new ODocument(clazz);
        doc.field("dateList", values);
        values.add(1L);
        values.add(1);
        values.add(((short) (1)));
        values.add(1.0F);
        values.add(1.0);
        values.add(BigDecimal.ONE);
        Set set = new HashSet();
        doc.field("dateSet", set);
        set.add(1L);
        set.add(2);
        set.add(((short) (3)));
        set.add(4.0F);
        set.add(5.0);
        set.add(new BigDecimal(6));
        doc.autoConvertValues();
        values = doc.field("dateList");
        for (Object val : values) {
            Assert.assertTrue((val instanceof Date));
            Assert.assertEquals(1, ((Date) (val)).getTime());
        }
        set = doc.field("dateSet");
        for (Object val : set) {
            Assert.assertTrue((val instanceof Date));
        }
        for (int i = 1; i < 7; i++) {
            Assert.assertTrue(set.contains(new Date(i)));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapIntegerConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("integerMap", values);
        fillMap(values);
        doc.autoConvertValues();
        values = doc.field("integerMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof Integer));
            Assert.assertEquals(((Integer) (1)), val);
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapLongConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("longMap", values);
        fillMap(values);
        doc.autoConvertValues();
        values = doc.field("longMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof Long));
            Assert.assertEquals(((Long) (1L)), val);
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapByteConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("byteMap", values);
        fillMap(values);
        doc.autoConvertValues();
        values = doc.field("byteMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof Byte));
            Assert.assertEquals(((byte) (1)), val);
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapFloatConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("floatMap", values);
        fillMap(values);
        doc.autoConvertValues();
        values = doc.field("floatMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof Float));
            Assert.assertEquals(((Float) (1.0F)), val);
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapDoubleConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("doubleMap", values);
        fillMap(values);
        doc.autoConvertValues();
        values = doc.field("doubleMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof Double));
            Assert.assertEquals(((Double) (1.0)), val);
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapDecimalConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("decimalMap", values);
        fillMap(values);
        doc.autoConvertValues();
        values = doc.field("decimalMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof BigDecimal));
            Assert.assertTrue(val.toString().contains("1"));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapStringConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("stringMap", values);
        fillMap(values);
        doc.autoConvertValues();
        values = doc.field("stringMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof String));
            Assert.assertTrue(val.toString().contains("1"));
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }

    @Test
    public void testMapDateConversion() {
        ODatabaseRecordThreadLocal.instance().set(db);
        Map<String, Object> values = new HashMap<String, Object>();
        ODocument doc = new ODocument(clazz);
        doc.field("dateMap", values);
        values.put("first", ((byte) (1)));
        values.put("second", ((short) (1)));
        values.put("third", 1);
        values.put("forth", 1L);
        values.put("fifth", 1.0F);
        values.put("sixth", 1.0);
        values.put("eighth", BigDecimal.ONE);
        doc.autoConvertValues();
        values = doc.field("dateMap");
        for (Object val : values.values()) {
            Assert.assertTrue((val instanceof Date));
            Assert.assertEquals(1, ((Date) (val)).getTime());
        }
        ODatabaseRecordThreadLocal.instance().remove();
    }
}

