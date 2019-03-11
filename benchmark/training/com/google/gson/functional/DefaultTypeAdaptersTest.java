/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.JavaVersion;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import junit.framework.TestCase;


/**
 * Functional test for Json serialization and deserialization for common classes for which default
 * support is provided in Gson. The tests for Map types are available in {@link MapTest}.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class DefaultTypeAdaptersTest extends TestCase {
    private Gson gson;

    private TimeZone oldTimeZone;

    public void testClassSerialization() {
        try {
            gson.toJson(String.class);
        } catch (UnsupportedOperationException expected) {
        }
        // Override with a custom type adapter for class.
        gson = new GsonBuilder().registerTypeAdapter(Class.class, new DefaultTypeAdaptersTest.MyClassTypeAdapter()).create();
        TestCase.assertEquals("\"java.lang.String\"", gson.toJson(String.class));
    }

    public void testClassDeserialization() {
        try {
            gson.fromJson("String.class", String.class.getClass());
        } catch (UnsupportedOperationException expected) {
        }
        // Override with a custom type adapter for class.
        gson = new GsonBuilder().registerTypeAdapter(Class.class, new DefaultTypeAdaptersTest.MyClassTypeAdapter()).create();
        TestCase.assertEquals(String.class, gson.fromJson("java.lang.String", Class.class));
    }

    public void testUrlSerialization() throws Exception {
        String urlValue = "http://google.com/";
        URL url = new URL(urlValue);
        TestCase.assertEquals("\"http://google.com/\"", gson.toJson(url));
    }

    public void testUrlDeserialization() {
        String urlValue = "http://google.com/";
        String json = "\'http:\\/\\/google.com\\/\'";
        URL target = gson.fromJson(json, URL.class);
        TestCase.assertEquals(urlValue, target.toExternalForm());
        gson.fromJson((('"' + urlValue) + '"'), URL.class);
        TestCase.assertEquals(urlValue, target.toExternalForm());
    }

    public void testUrlNullSerialization() throws Exception {
        DefaultTypeAdaptersTest.ClassWithUrlField target = new DefaultTypeAdaptersTest.ClassWithUrlField();
        TestCase.assertEquals("{}", gson.toJson(target));
    }

    public void testUrlNullDeserialization() {
        String json = "{}";
        DefaultTypeAdaptersTest.ClassWithUrlField target = gson.fromJson(json, DefaultTypeAdaptersTest.ClassWithUrlField.class);
        TestCase.assertNull(target.url);
    }

    private static class ClassWithUrlField {
        URL url;
    }

    public void testUriSerialization() throws Exception {
        String uriValue = "http://google.com/";
        URI uri = new URI(uriValue);
        TestCase.assertEquals("\"http://google.com/\"", gson.toJson(uri));
    }

    public void testUriDeserialization() {
        String uriValue = "http://google.com/";
        String json = ('"' + uriValue) + '"';
        URI target = gson.fromJson(json, URI.class);
        TestCase.assertEquals(uriValue, target.toASCIIString());
    }

    public void testNullSerialization() throws Exception {
        testNullSerializationAndDeserialization(Boolean.class);
        testNullSerializationAndDeserialization(Byte.class);
        testNullSerializationAndDeserialization(Short.class);
        testNullSerializationAndDeserialization(Integer.class);
        testNullSerializationAndDeserialization(Long.class);
        testNullSerializationAndDeserialization(Double.class);
        testNullSerializationAndDeserialization(Float.class);
        testNullSerializationAndDeserialization(Number.class);
        testNullSerializationAndDeserialization(Character.class);
        testNullSerializationAndDeserialization(String.class);
        testNullSerializationAndDeserialization(StringBuilder.class);
        testNullSerializationAndDeserialization(StringBuffer.class);
        testNullSerializationAndDeserialization(BigDecimal.class);
        testNullSerializationAndDeserialization(BigInteger.class);
        testNullSerializationAndDeserialization(TreeSet.class);
        testNullSerializationAndDeserialization(ArrayList.class);
        testNullSerializationAndDeserialization(HashSet.class);
        testNullSerializationAndDeserialization(Properties.class);
        testNullSerializationAndDeserialization(URL.class);
        testNullSerializationAndDeserialization(URI.class);
        testNullSerializationAndDeserialization(UUID.class);
        testNullSerializationAndDeserialization(Locale.class);
        testNullSerializationAndDeserialization(InetAddress.class);
        testNullSerializationAndDeserialization(BitSet.class);
        testNullSerializationAndDeserialization(Date.class);
        testNullSerializationAndDeserialization(GregorianCalendar.class);
        testNullSerializationAndDeserialization(Calendar.class);
        testNullSerializationAndDeserialization(Time.class);
        testNullSerializationAndDeserialization(Timestamp.class);
        testNullSerializationAndDeserialization(java.sql.Date.class);
        testNullSerializationAndDeserialization(Enum.class);
        testNullSerializationAndDeserialization(Class.class);
    }

    public void testUuidSerialization() throws Exception {
        String uuidValue = "c237bec1-19ef-4858-a98e-521cf0aad4c0";
        UUID uuid = UUID.fromString(uuidValue);
        TestCase.assertEquals((('"' + uuidValue) + '"'), gson.toJson(uuid));
    }

    public void testUuidDeserialization() {
        String uuidValue = "c237bec1-19ef-4858-a98e-521cf0aad4c0";
        String json = ('"' + uuidValue) + '"';
        UUID target = gson.fromJson(json, UUID.class);
        TestCase.assertEquals(uuidValue, target.toString());
    }

    public void testLocaleSerializationWithLanguage() {
        Locale target = new Locale("en");
        TestCase.assertEquals("\"en\"", gson.toJson(target));
    }

    public void testLocaleDeserializationWithLanguage() {
        String json = "\"en\"";
        Locale locale = gson.fromJson(json, Locale.class);
        TestCase.assertEquals("en", locale.getLanguage());
    }

    public void testLocaleSerializationWithLanguageCountry() {
        Locale target = Locale.CANADA_FRENCH;
        TestCase.assertEquals("\"fr_CA\"", gson.toJson(target));
    }

    public void testLocaleDeserializationWithLanguageCountry() {
        String json = "\"fr_CA\"";
        Locale locale = gson.fromJson(json, Locale.class);
        TestCase.assertEquals(Locale.CANADA_FRENCH, locale);
    }

    public void testLocaleSerializationWithLanguageCountryVariant() {
        Locale target = new Locale("de", "DE", "EURO");
        String json = gson.toJson(target);
        TestCase.assertEquals("\"de_DE_EURO\"", json);
    }

    public void testLocaleDeserializationWithLanguageCountryVariant() {
        String json = "\"de_DE_EURO\"";
        Locale locale = gson.fromJson(json, Locale.class);
        TestCase.assertEquals("de", locale.getLanguage());
        TestCase.assertEquals("DE", locale.getCountry());
        TestCase.assertEquals("EURO", locale.getVariant());
    }

    public void testBigDecimalFieldSerialization() {
        DefaultTypeAdaptersTest.ClassWithBigDecimal target = new DefaultTypeAdaptersTest.ClassWithBigDecimal("-122.01e-21");
        String json = gson.toJson(target);
        String actual = json.substring(((json.indexOf(':')) + 1), json.indexOf('}'));
        TestCase.assertEquals(target.value, new BigDecimal(actual));
    }

    public void testBigDecimalFieldDeserialization() {
        DefaultTypeAdaptersTest.ClassWithBigDecimal expected = new DefaultTypeAdaptersTest.ClassWithBigDecimal("-122.01e-21");
        String json = expected.getExpectedJson();
        DefaultTypeAdaptersTest.ClassWithBigDecimal actual = gson.fromJson(json, DefaultTypeAdaptersTest.ClassWithBigDecimal.class);
        TestCase.assertEquals(expected.value, actual.value);
    }

    public void testBadValueForBigDecimalDeserialization() {
        try {
            gson.fromJson("{\"value\"=1.5e-1.0031}", DefaultTypeAdaptersTest.ClassWithBigDecimal.class);
            TestCase.fail("Exponent of a BigDecimal must be an integer value.");
        } catch (JsonParseException expected) {
        }
    }

    public void testBigIntegerFieldSerialization() {
        DefaultTypeAdaptersTest.ClassWithBigInteger target = new DefaultTypeAdaptersTest.ClassWithBigInteger("23232323215323234234324324324324324324");
        String json = gson.toJson(target);
        TestCase.assertEquals(target.getExpectedJson(), json);
    }

    public void testBigIntegerFieldDeserialization() {
        DefaultTypeAdaptersTest.ClassWithBigInteger expected = new DefaultTypeAdaptersTest.ClassWithBigInteger("879697697697697697697697697697697697");
        String json = expected.getExpectedJson();
        DefaultTypeAdaptersTest.ClassWithBigInteger actual = gson.fromJson(json, DefaultTypeAdaptersTest.ClassWithBigInteger.class);
        TestCase.assertEquals(expected.value, actual.value);
    }

    public void testOverrideBigIntegerTypeAdapter() throws Exception {
        gson = new GsonBuilder().registerTypeAdapter(BigInteger.class, new DefaultTypeAdaptersTest.NumberAsStringAdapter(BigInteger.class)).create();
        TestCase.assertEquals("\"123\"", gson.toJson(new BigInteger("123"), BigInteger.class));
        TestCase.assertEquals(new BigInteger("123"), gson.fromJson("\"123\"", BigInteger.class));
    }

    public void testOverrideBigDecimalTypeAdapter() throws Exception {
        gson = new GsonBuilder().registerTypeAdapter(BigDecimal.class, new DefaultTypeAdaptersTest.NumberAsStringAdapter(BigDecimal.class)).create();
        TestCase.assertEquals("\"1.1\"", gson.toJson(new BigDecimal("1.1"), BigDecimal.class));
        TestCase.assertEquals(new BigDecimal("1.1"), gson.fromJson("\"1.1\"", BigDecimal.class));
    }

    public void testSetSerialization() throws Exception {
        Gson gson = new Gson();
        HashSet<String> s = new HashSet<String>();
        s.add("blah");
        String json = gson.toJson(s);
        TestCase.assertEquals("[\"blah\"]", json);
        json = gson.toJson(s, Set.class);
        TestCase.assertEquals("[\"blah\"]", json);
    }

    public void testBitSetSerialization() throws Exception {
        Gson gson = new Gson();
        BitSet bits = new BitSet();
        bits.set(1);
        bits.set(3, 6);
        bits.set(9);
        String json = gson.toJson(bits);
        TestCase.assertEquals("[0,1,0,1,1,1,0,0,0,1]", json);
    }

    public void testBitSetDeserialization() throws Exception {
        BitSet expected = new BitSet();
        expected.set(0);
        expected.set(2, 6);
        expected.set(8);
        Gson gson = new Gson();
        String json = gson.toJson(expected);
        TestCase.assertEquals(expected, gson.fromJson(json, BitSet.class));
        json = "[1,0,1,1,1,1,0,0,1,0,0,0]";
        TestCase.assertEquals(expected, gson.fromJson(json, BitSet.class));
        json = "[\"1\",\"0\",\"1\",\"1\",\"1\",\"1\",\"0\",\"0\",\"1\"]";
        TestCase.assertEquals(expected, gson.fromJson(json, BitSet.class));
        json = "[true,false,true,true,true,true,false,false,true,false,false]";
        TestCase.assertEquals(expected, gson.fromJson(json, BitSet.class));
    }

    public void testDefaultDateSerialization() {
        Date now = new Date(1315806903103L);
        String json = gson.toJson(now);
        if (JavaVersion.isJava9OrLater()) {
            TestCase.assertEquals("\"Sep 11, 2011, 10:55:03 PM\"", json);
        } else {
            TestCase.assertEquals("\"Sep 11, 2011 10:55:03 PM\"", json);
        }
    }

    public void testDefaultDateDeserialization() {
        String json = "'Dec 13, 2009 07:18:02 AM'";
        Date extracted = gson.fromJson(json, Date.class);
        assertEqualsDate(extracted, 2009, 11, 13);
        assertEqualsTime(extracted, 7, 18, 2);
    }

    public void testDefaultJavaSqlDateSerialization() {
        java.sql.Date instant = new java.sql.Date(1259875082000L);
        String json = gson.toJson(instant);
        TestCase.assertEquals("\"Dec 3, 2009\"", json);
    }

    public void testDefaultJavaSqlDateDeserialization() {
        String json = "'Dec 3, 2009'";
        java.sql.Date extracted = gson.fromJson(json, java.sql.Date.class);
        assertEqualsDate(extracted, 2009, 11, 3);
    }

    public void testDefaultJavaSqlTimestampSerialization() {
        Timestamp now = new Timestamp(1259875082000L);
        String json = gson.toJson(now);
        if (JavaVersion.isJava9OrLater()) {
            TestCase.assertEquals("\"Dec 3, 2009, 1:18:02 PM\"", json);
        } else {
            TestCase.assertEquals("\"Dec 3, 2009 1:18:02 PM\"", json);
        }
    }

    public void testDefaultJavaSqlTimestampDeserialization() {
        String json = "'Dec 3, 2009 1:18:02 PM'";
        Timestamp extracted = gson.fromJson(json, Timestamp.class);
        assertEqualsDate(extracted, 2009, 11, 3);
        assertEqualsTime(extracted, 13, 18, 2);
    }

    public void testDefaultJavaSqlTimeSerialization() {
        Time now = new Time(1259875082000L);
        String json = gson.toJson(now);
        TestCase.assertEquals("\"01:18:02 PM\"", json);
    }

    public void testDefaultJavaSqlTimeDeserialization() {
        String json = "'1:18:02 PM'";
        Time extracted = gson.fromJson(json, Time.class);
        assertEqualsTime(extracted, 13, 18, 2);
    }

    public void testDefaultDateSerializationUsingBuilder() throws Exception {
        Gson gson = new GsonBuilder().create();
        Date now = new Date(1315806903103L);
        String json = gson.toJson(now);
        if (JavaVersion.isJava9OrLater()) {
            TestCase.assertEquals("\"Sep 11, 2011, 10:55:03 PM\"", json);
        } else {
            TestCase.assertEquals("\"Sep 11, 2011 10:55:03 PM\"", json);
        }
    }

    public void testDefaultDateDeserializationUsingBuilder() throws Exception {
        Gson gson = new GsonBuilder().create();
        Date now = new Date(1315806903103L);
        String json = gson.toJson(now);
        Date extracted = gson.fromJson(json, Date.class);
        TestCase.assertEquals(now.toString(), extracted.toString());
    }

    public void testDefaultCalendarSerialization() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertTrue(json.contains("year"));
        TestCase.assertTrue(json.contains("month"));
        TestCase.assertTrue(json.contains("dayOfMonth"));
        TestCase.assertTrue(json.contains("hourOfDay"));
        TestCase.assertTrue(json.contains("minute"));
        TestCase.assertTrue(json.contains("second"));
    }

    public void testDefaultCalendarDeserialization() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = "{year:2009,month:2,dayOfMonth:11,hourOfDay:14,minute:29,second:23}";
        Calendar cal = gson.fromJson(json, Calendar.class);
        TestCase.assertEquals(2009, cal.get(Calendar.YEAR));
        TestCase.assertEquals(2, cal.get(Calendar.MONTH));
        TestCase.assertEquals(11, cal.get(Calendar.DAY_OF_MONTH));
        TestCase.assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
        TestCase.assertEquals(29, cal.get(Calendar.MINUTE));
        TestCase.assertEquals(23, cal.get(Calendar.SECOND));
    }

    public void testDefaultGregorianCalendarSerialization() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertTrue(json.contains("year"));
        TestCase.assertTrue(json.contains("month"));
        TestCase.assertTrue(json.contains("dayOfMonth"));
        TestCase.assertTrue(json.contains("hourOfDay"));
        TestCase.assertTrue(json.contains("minute"));
        TestCase.assertTrue(json.contains("second"));
    }

    public void testDefaultGregorianCalendarDeserialization() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = "{year:2009,month:2,dayOfMonth:11,hourOfDay:14,minute:29,second:23}";
        GregorianCalendar cal = gson.fromJson(json, GregorianCalendar.class);
        TestCase.assertEquals(2009, cal.get(Calendar.YEAR));
        TestCase.assertEquals(2, cal.get(Calendar.MONTH));
        TestCase.assertEquals(11, cal.get(Calendar.DAY_OF_MONTH));
        TestCase.assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
        TestCase.assertEquals(29, cal.get(Calendar.MINUTE));
        TestCase.assertEquals(23, cal.get(Calendar.SECOND));
    }

    public void testDateSerializationWithPattern() throws Exception {
        String pattern = "yyyy-MM-dd";
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
        Date now = new Date(1315806903103L);
        String json = gson.toJson(now);
        TestCase.assertEquals("\"2011-09-11\"", json);
    }

    public void testDateSerializationWithPatternNotOverridenByTypeAdapter() throws Exception {
        String pattern = "yyyy-MM-dd";
        Gson gson = new GsonBuilder().setDateFormat(pattern).registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(1315806903103L);
            }
        }).create();
        Date now = new Date(1315806903103L);
        String json = gson.toJson(now);
        TestCase.assertEquals("\"2011-09-11\"", json);
    }

    // http://code.google.com/p/google-gson/issues/detail?id=230
    public void testDateSerializationInCollection() throws Exception {
        Type listOfDates = new TypeToken<List<Date>>() {}.getType();
        TimeZone defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            List<Date> dates = Arrays.asList(new Date(0));
            String json = gson.toJson(dates, listOfDates);
            TestCase.assertEquals("[\"1970-01-01\"]", json);
            TestCase.assertEquals(0L, gson.<List<Date>>fromJson("[\"1970-01-01\"]", listOfDates).get(0).getTime());
        } finally {
            TimeZone.setDefault(defaultTimeZone);
            Locale.setDefault(defaultLocale);
        }
    }

    // http://code.google.com/p/google-gson/issues/detail?id=230
    public void testTimestampSerialization() throws Exception {
        TimeZone defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        try {
            Timestamp timestamp = new Timestamp(0L);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String json = gson.toJson(timestamp, Timestamp.class);
            TestCase.assertEquals("\"1970-01-01\"", json);
            TestCase.assertEquals(0, gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime());
        } finally {
            TimeZone.setDefault(defaultTimeZone);
            Locale.setDefault(defaultLocale);
        }
    }

    // http://code.google.com/p/google-gson/issues/detail?id=230
    public void testSqlDateSerialization() throws Exception {
        TimeZone defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        try {
            java.sql.Date sqlDate = new java.sql.Date(0L);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String json = gson.toJson(sqlDate, Timestamp.class);
            TestCase.assertEquals("\"1970-01-01\"", json);
            TestCase.assertEquals(0, gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime());
        } finally {
            TimeZone.setDefault(defaultTimeZone);
            Locale.setDefault(defaultLocale);
        }
    }

    public void testJsonPrimitiveSerialization() {
        TestCase.assertEquals("5", gson.toJson(new JsonPrimitive(5), JsonElement.class));
        TestCase.assertEquals("true", gson.toJson(new JsonPrimitive(true), JsonElement.class));
        TestCase.assertEquals("\"foo\"", gson.toJson(new JsonPrimitive("foo"), JsonElement.class));
        TestCase.assertEquals("\"a\"", gson.toJson(new JsonPrimitive('a'), JsonElement.class));
    }

    public void testJsonPrimitiveDeserialization() {
        TestCase.assertEquals(new JsonPrimitive(5), gson.fromJson("5", JsonElement.class));
        TestCase.assertEquals(new JsonPrimitive(5), gson.fromJson("5", JsonPrimitive.class));
        TestCase.assertEquals(new JsonPrimitive(true), gson.fromJson("true", JsonElement.class));
        TestCase.assertEquals(new JsonPrimitive(true), gson.fromJson("true", JsonPrimitive.class));
        TestCase.assertEquals(new JsonPrimitive("foo"), gson.fromJson("\"foo\"", JsonElement.class));
        TestCase.assertEquals(new JsonPrimitive("foo"), gson.fromJson("\"foo\"", JsonPrimitive.class));
        TestCase.assertEquals(new JsonPrimitive('a'), gson.fromJson("\"a\"", JsonElement.class));
        TestCase.assertEquals(new JsonPrimitive('a'), gson.fromJson("\"a\"", JsonPrimitive.class));
    }

    public void testJsonNullSerialization() {
        TestCase.assertEquals("null", gson.toJson(JsonNull.INSTANCE, JsonElement.class));
        TestCase.assertEquals("null", gson.toJson(JsonNull.INSTANCE, JsonNull.class));
    }

    public void testNullJsonElementSerialization() {
        TestCase.assertEquals("null", gson.toJson(null, JsonElement.class));
        TestCase.assertEquals("null", gson.toJson(null, JsonNull.class));
    }

    public void testJsonArraySerialization() {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(1));
        array.add(new JsonPrimitive(2));
        array.add(new JsonPrimitive(3));
        TestCase.assertEquals("[1,2,3]", gson.toJson(array, JsonElement.class));
    }

    public void testJsonArrayDeserialization() {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(1));
        array.add(new JsonPrimitive(2));
        array.add(new JsonPrimitive(3));
        String json = "[1,2,3]";
        TestCase.assertEquals(array, gson.fromJson(json, JsonElement.class));
        TestCase.assertEquals(array, gson.fromJson(json, JsonArray.class));
    }

    public void testJsonObjectSerialization() {
        JsonObject object = new JsonObject();
        object.add("foo", new JsonPrimitive(1));
        object.add("bar", new JsonPrimitive(2));
        TestCase.assertEquals("{\"foo\":1,\"bar\":2}", gson.toJson(object, JsonElement.class));
    }

    public void testJsonObjectDeserialization() {
        JsonObject object = new JsonObject();
        object.add("foo", new JsonPrimitive(1));
        object.add("bar", new JsonPrimitive(2));
        String json = "{\"foo\":1,\"bar\":2}";
        JsonElement actual = gson.fromJson(json, JsonElement.class);
        TestCase.assertEquals(object, actual);
        JsonObject actualObj = gson.fromJson(json, JsonObject.class);
        TestCase.assertEquals(object, actualObj);
    }

    public void testJsonNullDeserialization() {
        TestCase.assertEquals(JsonNull.INSTANCE, gson.fromJson("null", JsonElement.class));
        TestCase.assertEquals(JsonNull.INSTANCE, gson.fromJson("null", JsonNull.class));
    }

    public void testJsonElementTypeMismatch() {
        try {
            gson.fromJson("\"abc\"", JsonObject.class);
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expected a com.google.gson.JsonObject but was com.google.gson.JsonPrimitive", expected.getMessage());
        }
    }

    private static class ClassWithBigDecimal {
        BigDecimal value;

        ClassWithBigDecimal(String value) {
            this.value = new BigDecimal(value);
        }

        String getExpectedJson() {
            return ("{\"value\":" + (value.toEngineeringString())) + "}";
        }
    }

    private static class ClassWithBigInteger {
        BigInteger value;

        ClassWithBigInteger(String value) {
            this.value = new BigInteger(value);
        }

        String getExpectedJson() {
            return ("{\"value\":" + (value)) + "}";
        }
    }

    public void testPropertiesSerialization() {
        Properties props = new Properties();
        props.setProperty("foo", "bar");
        String json = gson.toJson(props);
        String expected = "{\"foo\":\"bar\"}";
        TestCase.assertEquals(expected, json);
    }

    public void testPropertiesDeserialization() {
        String json = "{foo:'bar'}";
        Properties props = gson.fromJson(json, Properties.class);
        TestCase.assertEquals("bar", props.getProperty("foo"));
    }

    public void testTreeSetSerialization() {
        TreeSet<String> treeSet = new TreeSet<String>();
        treeSet.add("Value1");
        String json = gson.toJson(treeSet);
        TestCase.assertEquals("[\"Value1\"]", json);
    }

    public void testTreeSetDeserialization() {
        String json = "['Value1']";
        Type type = new TypeToken<TreeSet<String>>() {}.getType();
        TreeSet<String> treeSet = gson.fromJson(json, type);
        TestCase.assertTrue(treeSet.contains("Value1"));
    }

    public void testStringBuilderSerialization() {
        StringBuilder sb = new StringBuilder("abc");
        String json = gson.toJson(sb);
        TestCase.assertEquals("\"abc\"", json);
    }

    public void testStringBuilderDeserialization() {
        StringBuilder sb = gson.fromJson("'abc'", StringBuilder.class);
        TestCase.assertEquals("abc", sb.toString());
    }

    public void testStringBufferSerialization() {
        StringBuffer sb = new StringBuffer("abc");
        String json = gson.toJson(sb);
        TestCase.assertEquals("\"abc\"", json);
    }

    public void testStringBufferDeserialization() {
        StringBuffer sb = gson.fromJson("'abc'", StringBuffer.class);
        TestCase.assertEquals("abc", sb.toString());
    }

    @SuppressWarnings("rawtypes")
    private static class MyClassTypeAdapter extends TypeAdapter<Class> {
        @Override
        public void write(JsonWriter out, Class value) throws IOException {
            out.value(value.getName());
        }

        @Override
        public Class read(JsonReader in) throws IOException {
            String className = in.nextString();
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
    }

    static class NumberAsStringAdapter extends TypeAdapter<Number> {
        private final Constructor<? extends Number> constructor;

        NumberAsStringAdapter(Class<? extends Number> type) throws Exception {
            this.constructor = type.getConstructor(String.class);
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value.toString());
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            try {
                return constructor.newInstance(in.nextString());
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
    }
}

