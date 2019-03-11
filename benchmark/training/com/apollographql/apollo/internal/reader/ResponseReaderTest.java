package com.apollographql.apollo.internal.reader;


import CacheKey.NO_KEY;
import ResponseField.CustomTypeField;
import ResponseReader.ListItemReader;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ScalarType;
import com.apollographql.apollo.api.internal.Optional;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.Record;
import com.apollographql.apollo.internal.cache.normalized.CacheKeyBuilder;
import com.apollographql.apollo.internal.cache.normalized.ResponseNormalizer;
import com.apollographql.apollo.internal.response.RealResponseReader;
import com.google.common.truth.Truth;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;


public class ResponseReaderTest {
    private final List<ResponseField.Condition> NO_CONDITIONS = Collections.emptyList();

    @Test
    public void readString() throws Exception {
        ResponseField successField = ResponseField.forString("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forString("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", "response1");
        recordSet.put("successFieldName", "response2");
        recordSet.put("classCastExceptionFieldResponseName", 1);
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readString(successField)).isEqualTo("response1");
        try {
            responseReader.readString(classCastExceptionField);
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readInt() throws Exception {
        ResponseField successField = ResponseField.forInt("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forInt("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", BigDecimal.valueOf(1));
        recordSet.put("successFieldName", BigDecimal.valueOf(2));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readInt(successField)).isEqualTo(1);
        try {
            responseReader.readInt(classCastExceptionField);
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readLong() throws Exception {
        ResponseField successField = ResponseField.forLong("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forLong("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", BigDecimal.valueOf(1));
        recordSet.put("successFieldName", BigDecimal.valueOf(2));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readLong(successField)).isEqualTo(1);
        try {
            responseReader.readLong(classCastExceptionField);
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readDouble() throws Exception {
        ResponseField successField = ResponseField.forDouble("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forDouble("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", BigDecimal.valueOf(1.1));
        recordSet.put("successFieldName", BigDecimal.valueOf(2.2));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readDouble(successField)).isEqualTo(1.1);
        try {
            responseReader.readDouble(classCastExceptionField);
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readBoolean() throws Exception {
        ResponseField successField = ResponseField.forBoolean("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forBoolean("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", true);
        recordSet.put("successFieldName", false);
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readBoolean(successField)).isTrue();
        try {
            responseReader.readBoolean(classCastExceptionField);
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readObject() throws Exception {
        final Object responseObject1 = new Object();
        final Object responseObject2 = new Object();
        ResponseField successField = ResponseField.forObject("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forObject("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", responseObject1);
        recordSet.put("successFieldName", responseObject2);
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readObject(successField, new ResponseReader.ObjectReader<Object>() {
            @Override
            public Object read(ResponseReader reader) {
                return responseObject1;
            }
        })).isEqualTo(responseObject1);
        try {
            responseReader.readObject(classCastExceptionField, new ResponseReader.ObjectReader<Object>() {
                @Override
                public Object read(ResponseReader reader) {
                    return reader.readString(ResponseField.forString("anything", "anything", null, true, NO_CONDITIONS));
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readCustom() throws Exception {
        ResponseField successField = ResponseField.forCustomType("successFieldResponseName", "successFieldName", null, false, ResponseReaderTest.DATE_CUSTOM_TYPE, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forCustomType("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, ResponseReaderTest.DATE_CUSTOM_TYPE, NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", "2017-04-16");
        recordSet.put("successFieldName", "2018-04-16");
        recordSet.put("classCastExceptionFieldResponseName", 0);
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readCustomType(((ResponseField.CustomTypeField) (successField)))).isEqualTo(ResponseReaderTest.DATE_TIME_FORMAT.parse("2017-04-16"));
        try {
            responseReader.readCustomType(((ResponseField.CustomTypeField) (classCastExceptionField)));
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readCustomObjectMap() throws Exception {
        ResponseField successField = ResponseField.forCustomType("successFieldResponseName", "successFieldName", null, false, ResponseReaderTest.OBJECT_CUSTOM_TYPE, NO_CONDITIONS);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("string", "string");
        objectMap.put("boolean", true);
        objectMap.put("double", 1.99);
        objectMap.put("float", 2.99F);
        objectMap.put("long", 3L);
        objectMap.put("int", 4);
        objectMap.put("stringList", Arrays.asList("string1", "string2"));
        objectMap.put("booleanList", Arrays.asList("true", "false"));
        objectMap.put("doubleList", Arrays.asList(1.99, 2.99));
        objectMap.put("floatList", Arrays.asList(3.99F, 4.99F, 5.99F));
        objectMap.put("longList", Arrays.asList(5L, 7L));
        objectMap.put("intList", Arrays.asList(8, 9, 10));
        objectMap.put("object", new HashMap<>(objectMap));
        objectMap.put("objectList", Arrays.asList(new HashMap<>(objectMap), new HashMap<>(objectMap)));
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", objectMap);
        recordSet.put("successFieldName", objectMap);
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readCustomType(((ResponseField.CustomTypeField) (successField)))).isEqualTo(("{\"string\":\"string\",\"double\":1.99,\"intList\":[8,9,10],\"doubleList\":[1.99,2.99]," + ((((((((((((((((("\"float\":2.99,\"longList\":[5,7],\"long\":3,\"int\":4,\"objectList\":[{\"string\":\"string\"," + "\"double\":1.99,\"intList\":[8,9,10],\"doubleList\":[1.99,2.99],\"float\":2.99,\"longList\":[5,7],") + "\"long\":3,\"int\":4,\"boolean\":true,\"stringList\":[\"string1\",\"string2\"],") + "\"floatList\":[3.99,4.99,5.99],\"booleanList\":[\"true\",\"false\"],\"object\":{\"string\":\"string\",") + "\"double\":1.99,\"intList\":[8,9,10],\"doubleList\":[1.99,2.99],\"float\":2.99,\"longList\":[5,7],") + "\"long\":3,\"int\":4,\"boolean\":true,\"stringList\":[\"string1\",\"string2\"],") + "\"floatList\":[3.99,4.99,5.99],\"booleanList\":[\"true\",\"false\"]}},{\"string\":\"string\",") + "\"double\":1.99,\"intList\":[8,9,10],\"doubleList\":[1.99,2.99],\"float\":2.99,\"longList\":[5,7],") + "\"long\":3,\"int\":4,\"boolean\":true,\"stringList\":[\"string1\",\"string2\"],") + "\"floatList\":[3.99,4.99,5.99],\"booleanList\":[\"true\",\"false\"],\"object\":{\"string\":\"string\",") + "\"double\":1.99,\"intList\":[8,9,10],\"doubleList\":[1.99,2.99],\"float\":2.99,\"longList\":[5,7],") + "\"long\":3,\"int\":4,\"boolean\":true,\"stringList\":[\"string1\",\"string2\"],") + "\"floatList\":[3.99,4.99,5.99],\"booleanList\":[\"true\",\"false\"]}}],\"boolean\":true,") + "\"stringList\":[\"string1\",\"string2\"],\"floatList\":[3.99,4.99,5.99],") + "\"booleanList\":[\"true\",\"false\"],\"object\":{\"string\":\"string\",\"double\":1.99,") + "\"intList\":[8,9,10],\"doubleList\":[1.99,2.99],\"float\":2.99,\"longList\":[5,7],\"long\":3,\"int\":4,") + "\"boolean\":true,\"stringList\":[\"string1\",\"string2\"],\"floatList\":[3.99,4.99,5.99],") + "\"booleanList\":[\"true\",\"false\"]}}")));
    }

    @Test
    public void readCustomObjectList() throws Exception {
        ResponseField successField = ResponseField.forCustomType("successFieldResponseName", "successFieldName", null, false, ResponseReaderTest.OBJECT_CUSTOM_TYPE, NO_CONDITIONS);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("string", "string");
        objectMap.put("boolean", true);
        List<?> objectList = Arrays.asList(objectMap, objectMap);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", objectList);
        recordSet.put("successFieldName", objectList);
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readCustomType(((ResponseField.CustomTypeField) (successField)))).isEqualTo("[{\"boolean\":true,\"string\":\"string\"},{\"boolean\":true,\"string\":\"string\"}]");
    }

    @Test
    public void readCustomWithDecodedNullValue() throws Exception {
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("responseName", "http:://");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        ResponseField field = ResponseField.forCustomType("responseName", "fieldName", null, false, ResponseReaderTest.URL_CUSTOM_TYPE, NO_CONDITIONS);
        try {
            responseReader.readCustomType(((ResponseField.CustomTypeField) (field)));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        field = ResponseField.forCustomType("responseName", "fieldName", null, true, ResponseReaderTest.URL_CUSTOM_TYPE, NO_CONDITIONS);
        Truth.assertThat(responseReader.readCustomType(((ResponseField.CustomTypeField) (field)))).isNull();
    }

    @Test
    public void readCustomWithDefaultAdapter() throws Exception {
        ResponseField.CustomTypeField stringField = ResponseField.forCustomType("stringField", "stringField", null, false, ResponseReaderTest.scalarTypeFor(String.class), NO_CONDITIONS);
        ResponseField.CustomTypeField booleanField = ResponseField.forCustomType("booleanField", "booleanField", null, false, ResponseReaderTest.scalarTypeFor(Boolean.class), NO_CONDITIONS);
        ResponseField.CustomTypeField integerField = ResponseField.forCustomType("integerField", "integerField", null, false, ResponseReaderTest.scalarTypeFor(Integer.class), NO_CONDITIONS);
        ResponseField.CustomTypeField longField = ResponseField.forCustomType("longField", "longField", null, false, ResponseReaderTest.scalarTypeFor(Long.class), NO_CONDITIONS);
        ResponseField.CustomTypeField floatField = ResponseField.forCustomType("floatField", "floatField", null, false, ResponseReaderTest.scalarTypeFor(Float.class), NO_CONDITIONS);
        ResponseField.CustomTypeField doubleField = ResponseField.forCustomType("doubleField", "doubleField", null, false, ResponseReaderTest.scalarTypeFor(Double.class), NO_CONDITIONS);
        ResponseField.CustomTypeField unsupportedField = ResponseField.forCustomType("unsupportedField", "unsupportedField", null, false, ResponseReaderTest.scalarTypeFor(RuntimeException.class), NO_CONDITIONS);
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put(stringField.responseName(), "string");
        recordSet.put(booleanField.responseName(), true);
        recordSet.put(integerField.responseName(), BigDecimal.valueOf(1));
        recordSet.put(longField.responseName(), BigDecimal.valueOf(2));
        recordSet.put(floatField.responseName(), BigDecimal.valueOf(3.99));
        recordSet.put(doubleField.responseName(), BigDecimal.valueOf(4.99));
        recordSet.put(unsupportedField.responseName(), "smth");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readCustomType(stringField)).isEqualTo("string");
        assertThat(responseReader.readCustomType(booleanField)).isEqualTo(true);
        assertThat(responseReader.readCustomType(integerField)).isEqualTo(1);
        assertThat(responseReader.readCustomType(longField)).isEqualTo(2);
        assertThat(responseReader.readCustomType(floatField)).isEqualTo(3.99F);
        assertThat(responseReader.readCustomType(doubleField)).isEqualTo(4.99);
        try {
            responseReader.readCustomType(unsupportedField);
            Assert.fail("Expect IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void readConditional() throws Exception {
        final Object responseObject1 = new Object();
        final Object responseObject2 = new Object();
        ResponseField successField = ResponseField.forFragment("successFieldResponseName", "successFieldName", Collections.<String>emptyList());
        ResponseField classCastExceptionField = ResponseField.forFragment("classCastExceptionFieldResponseName", "classCastExceptionFieldName", Collections.<String>emptyList());
        Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", "responseObject1");
        recordSet.put("successFieldName", "responseObject2");
        recordSet.put("classCastExceptionFieldResponseName", 1);
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readConditional(successField, new ResponseReader.ConditionalTypeReader<Object>() {
            @Override
            public Object read(String conditionalType, ResponseReader reader) {
                if (conditionalType.equals("responseObject1")) {
                    return responseObject1;
                } else {
                    return responseObject2;
                }
            }
        })).isEqualTo(responseObject1);
        try {
            responseReader.readConditional(classCastExceptionField, new ResponseReader.ConditionalTypeReader<Object>() {
                @Override
                public Object read(String conditionalType, ResponseReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readStringList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", Arrays.asList("value1", "value2", "value3"));
        recordSet.put("successFieldName", Arrays.asList("value4", "value5"));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readString();
            }
        })).isEqualTo(Arrays.asList("value1", "value2", "value3"));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readIntList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3)));
        recordSet.put("successFieldName", Arrays.asList(BigDecimal.valueOf(4), BigDecimal.valueOf(5)));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readInt();
            }
        })).isEqualTo(Arrays.asList(1, 2, 3));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readLongList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3)));
        recordSet.put("successFieldName", Arrays.asList(BigDecimal.valueOf(4), BigDecimal.valueOf(5)));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readInt();
            }
        })).isEqualTo(Arrays.asList(1, 2, 3));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readDoubleList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3)));
        recordSet.put("successFieldName", Arrays.asList(BigDecimal.valueOf(4), BigDecimal.valueOf(5)));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readDouble();
            }
        })).isEqualTo(Arrays.asList(1.0, 2.0, 3.0));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readBooleanList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", Arrays.asList(true, false, true));
        recordSet.put("successFieldName", Arrays.asList(false, false));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readBoolean();
            }
        })).isEqualTo(Arrays.asList(true, false, true));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readCustomList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", Arrays.asList("2017-04-16", "2017-04-17", "2017-04-18"));
        recordSet.put("successFieldName", Arrays.asList("2017-04-19", "2017-04-20"));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readCustomType(ResponseReaderTest.DATE_CUSTOM_TYPE);
            }
        })).isEqualTo(Arrays.asList(ResponseReaderTest.DATE_TIME_FORMAT.parse("2017-04-16"), ResponseReaderTest.DATE_TIME_FORMAT.parse("2017-04-17"), ResponseReaderTest.DATE_TIME_FORMAT.parse("2017-04-18")));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readCustomListWithDecodedNull() throws Exception {
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("responseName", Arrays.asList("http://", "http://"));
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        ResponseField field = ResponseField.forList("responseName", "fieldName", null, false, NO_CONDITIONS);
        Truth.assertThat(responseReader.readList(field, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readCustomType(ResponseReaderTest.URL_CUSTOM_TYPE);
            }
        })).isEmpty();
    }

    @Test
    public void readObjectList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final Object responseObject1 = new Object();
        final Object responseObject2 = new Object();
        final Object responseObject3 = new Object();
        final Object responseObject4 = new Object();
        final Object responseObject5 = new Object();
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", Arrays.asList(responseObject1, responseObject2, responseObject3));
        recordSet.put("successFieldName", Arrays.asList(responseObject4, responseObject5));
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader() {
            int index = 0;

            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readObject(new ResponseReader.ObjectReader<Object>() {
                    @Override
                    public Object read(ResponseReader reader) {
                        return ((List) (recordSet.get("successFieldResponseName"))).get(((index)++));
                    }
                });
            }
        })).isEqualTo(Arrays.asList(responseObject1, responseObject2, responseObject3));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void readListOfScalarList() throws Exception {
        ResponseField successField = ResponseField.forList("successFieldResponseName", "successFieldName", null, false, NO_CONDITIONS);
        ResponseField classCastExceptionField = ResponseField.forList("classCastExceptionFieldResponseName", "classCastExceptionFieldName", null, false, NO_CONDITIONS);
        final List<List<String>> response1 = Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4", "5"));
        final List<List<String>> response2 = Arrays.asList(Arrays.asList("6", "7", "8"), Arrays.asList("9", "0"));
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("successFieldResponseName", response1);
        recordSet.put("successFieldName", response2);
        recordSet.put("classCastExceptionFieldResponseName", "anything");
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(successField, new ResponseReader.ListReader<List<String>>() {
            @Override
            public List<String> read(ResponseReader.ListItemReader reader) {
                return reader.readList(new ResponseReader.ListReader<String>() {
                    @Override
                    public String read(ResponseReader.ListItemReader reader) {
                        return reader.readString();
                    }
                });
            }
        })).isEqualTo(Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4", "5")));
        try {
            responseReader.readList(classCastExceptionField, new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void optionalFieldsIOException() throws Exception {
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(Collections.<String, Object>emptyMap());
        responseReader.readString(ResponseField.forString("stringField", "stringField", null, true, NO_CONDITIONS));
        responseReader.readInt(ResponseField.forInt("intField", "intField", null, true, NO_CONDITIONS));
        responseReader.readLong(ResponseField.forLong("longField", "longField", null, true, NO_CONDITIONS));
        responseReader.readDouble(ResponseField.forDouble("doubleField", "doubleField", null, true, NO_CONDITIONS));
        responseReader.readBoolean(ResponseField.forBoolean("booleanField", "booleanField", null, true, NO_CONDITIONS));
        responseReader.readObject(ResponseField.forObject("objectField", "objectField", null, true, NO_CONDITIONS), new ResponseReader.ObjectReader<Object>() {
            @Override
            public Object read(ResponseReader reader) {
                return null;
            }
        });
        responseReader.readList(ResponseField.forList("scalarListField", "scalarListField", null, true, NO_CONDITIONS), new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return null;
            }
        });
        responseReader.readCustomType(((ResponseField.CustomTypeField) (ResponseField.forCustomType("customTypeField", "customTypeField", null, true, ResponseReaderTest.DATE_CUSTOM_TYPE, NO_CONDITIONS))));
    }

    @Test
    public void mandatoryFieldsIOException() throws Exception {
        final RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(Collections.<String, Object>emptyMap());
        try {
            responseReader.readString(ResponseField.forString("stringField", "stringField", null, false, NO_CONDITIONS));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readInt(ResponseField.forInt("intField", "intField", null, false, NO_CONDITIONS));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readLong(ResponseField.forLong("longField", "longField", null, false, NO_CONDITIONS));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readDouble(ResponseField.forDouble("doubleField", "doubleField", null, false, NO_CONDITIONS));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readBoolean(ResponseField.forBoolean("booleanField", "booleanField", null, false, NO_CONDITIONS));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readObject(ResponseField.forObject("objectField", "objectField", null, false, NO_CONDITIONS), new ResponseReader.ObjectReader<Object>() {
                @Override
                public Object read(ResponseReader reader) {
                    return null;
                }
            });
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readList(ResponseField.forList("scalarListField", "scalarListField", null, false, NO_CONDITIONS), new ResponseReader.ListReader() {
                @Override
                public Object read(ResponseReader.ListItemReader reader) {
                    return null;
                }
            });
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readCustomType(((ResponseField.CustomTypeField) (ResponseField.forCustomType("customTypeField", "customTypeField", null, false, ResponseReaderTest.DATE_CUSTOM_TYPE, NO_CONDITIONS))));
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
        try {
            responseReader.readConditional(ResponseField.forFragment("__typename", "__typename", Collections.<String>emptyList()), new ResponseReader.ConditionalTypeReader<Object>() {
                @Override
                public Object read(String conditionalType, ResponseReader reader) {
                    return null;
                }
            });
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
    }

    @Test
    public void readScalarListWithNulls() throws Exception {
        ResponseField scalarList = ResponseField.forList("list", "list", null, false, NO_CONDITIONS);
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("list", Arrays.asList(null, "item1", "item2", null, "item3", null));
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(scalarList, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readString();
            }
        })).isEqualTo(Arrays.asList("item1", "item2", "item3"));
    }

    @Test
    public void read_object_list_with_nulls() throws Exception {
        final ResponseField listField = ResponseField.forList("list", "list", null, false, NO_CONDITIONS);
        final ResponseField indexField = ResponseField.forList("index", "index", null, false, NO_CONDITIONS);
        final List responseObjects = Arrays.asList(new Object(), new Object(), new Object());
        final Map<String, Object> recordSet = new HashMap<>();
        recordSet.put("list", Arrays.asList(null, new com.apollographql.apollo.api.internal.UnmodifiableMapBuilder<String, Object>(1).put("index", "0").build(), new com.apollographql.apollo.api.internal.UnmodifiableMapBuilder<String, Object>(1).put("index", "1").build(), null, new com.apollographql.apollo.api.internal.UnmodifiableMapBuilder<String, Object>(1).put("index", "2").build(), null));
        RealResponseReader<Map<String, Object>> responseReader = ResponseReaderTest.responseReader(recordSet);
        assertThat(responseReader.readList(listField, new ResponseReader.ListReader() {
            @Override
            public Object read(ResponseReader.ListItemReader reader) {
                return reader.readObject(new ResponseReader.ObjectReader<Object>() {
                    @Override
                    public Object read(ResponseReader reader) {
                        return responseObjects.get(Integer.parseInt(reader.readString(indexField)));
                    }
                });
            }
        })).isEqualTo(responseObjects);
    }

    private static final ScalarType OBJECT_CUSTOM_TYPE = new ScalarType() {
        @Override
        public String typeName() {
            return String.class.getName();
        }

        @Override
        public Class javaType() {
            return String.class;
        }
    };

    private static final ScalarType DATE_CUSTOM_TYPE = new ScalarType() {
        @Override
        public String typeName() {
            return Date.class.getName();
        }

        @Override
        public Class javaType() {
            return Date.class;
        }
    };

    private static final ScalarType URL_CUSTOM_TYPE = new ScalarType() {
        @Override
        public String typeName() {
            return URL.class.getName();
        }

        @Override
        public Class javaType() {
            return URL.class;
        }
    };

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyy-mm-dd");

    private static final Operation EMPTY_OPERATION = new Operation() {
        @Override
        public String queryDocument() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Variables variables() {
            return EMPTY_VARIABLES;
        }

        @Override
        public ResponseFieldMapper responseFieldMapper() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object wrapData(Data data) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public OperationName name() {
            return null;
        }

        @NotNull
        @Override
        public String operationId() {
            return "";
        }
    };

    @SuppressWarnings("unchecked")
    private static final ResponseNormalizer NO_OP_NORMALIZER = new ResponseNormalizer() {
        @Override
        public void willResolveRootQuery(Operation operation) {
        }

        @Override
        public void willResolve(ResponseField field, Operation.Variables variables) {
        }

        @Override
        public void didResolve(ResponseField field, Operation.Variables variables) {
        }

        @Override
        public void didResolveScalar(Object value) {
        }

        @Override
        public void willResolveObject(ResponseField field, Optional objectSource) {
        }

        @Override
        public void didResolveObject(ResponseField Field, Optional objectSource) {
        }

        @NotNull
        @Override
        public CacheKey resolveCacheKey(@NotNull
        ResponseField field, @NotNull
        Object record) {
            return CacheKey.NO_KEY;
        }

        @Override
        public void didResolveList(List array) {
        }

        @Override
        public void willResolveElement(int atIndex) {
        }

        @Override
        public void didResolveElement(int atIndex) {
        }

        @Override
        public void didResolveNull() {
        }

        @Override
        public Collection<Record> records() {
            return Collections.emptyList();
        }

        @Override
        public Set<String> dependentKeys() {
            return Collections.emptySet();
        }

        @NotNull
        @Override
        public CacheKeyBuilder cacheKeyBuilder() {
            return new CacheKeyBuilder() {
                @NotNull
                @Override
                public String build(@NotNull
                ResponseField field, @NotNull
                Operation.Variables variables) {
                    return NO_KEY.key();
                }
            };
        }
    };
}

