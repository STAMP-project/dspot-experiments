/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling;


import ConversionSchemas.V2_COMPATIBLE;
import DynamoDBMapperConfig.DEFAULT;
import DynamoDBMapperModelFactory.TableFactory;
import S3Link.Factory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.pojos.SubClass;
import com.amazonaws.services.dynamodbv2.pojos.UnannotatedSubClass;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class StandardModelFactoriesV2CompatibleTest {
    protected static final DynamoDBMapperConfig CONFIG = new DynamoDBMapperConfig.Builder().withTypeConverterFactory(DEFAULT.getTypeConverterFactory()).withConversionSchema(V2_COMPATIBLE).build();

    private static final DynamoDBMapperModelFactory factory = StandardModelFactories.of(Factory.of(null));

    private static final TableFactory models = StandardModelFactoriesV2CompatibleTest.factory.getTableFactory(StandardModelFactoriesV2CompatibleTest.CONFIG);

    @Test
    public void testBoolean() {
        Assert.assertEquals("1", convert("getBoolean", true).getN());
        Assert.assertEquals("0", convert("getBoolean", false).getN());
        Assert.assertEquals("1", convert("getBoxedBoolean", true).getN());
        Assert.assertEquals("0", convert("getBoxedBoolean", false).getN());
        Assert.assertEquals(true, convert("getNativeBoolean", true).getBOOL());
        Assert.assertEquals(false, convert("getNativeBoolean", false).getBOOL());
    }

    @Test
    public void testString() {
        Assert.assertEquals("abc", convert("getString", "abc").getS());
        Assert.assertEquals(RandomUUIDMarshaller.randomUUID, convert("getCustomString", "abc").getS());
    }

    @Test
    public void testUuid() {
        UUID uuid = UUID.randomUUID();
        Assert.assertEquals(uuid.toString(), convert("getUuid", uuid).getS());
    }

    @Test
    public void testDate() {
        Assert.assertEquals("1970-01-01T00:00:00.000Z", convert("getDate", new Date(0)).getS());
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(0);
        Assert.assertEquals("1970-01-01T00:00:00.000Z", convert("getCalendar", c).getS());
    }

    @Test
    public void testNumbers() {
        Assert.assertEquals("0", convert("getByte", ((byte) (0))).getN());
        Assert.assertEquals("1", convert("getByte", ((byte) (1))).getN());
        Assert.assertEquals("0", convert("getBoxedByte", ((byte) (0))).getN());
        Assert.assertEquals("1", convert("getBoxedByte", ((byte) (1))).getN());
        Assert.assertEquals("0", convert("getShort", ((short) (0))).getN());
        Assert.assertEquals("1", convert("getShort", ((short) (1))).getN());
        Assert.assertEquals("0", convert("getBoxedShort", ((short) (0))).getN());
        Assert.assertEquals("1", convert("getBoxedShort", ((short) (1))).getN());
        Assert.assertEquals("0", convert("getInt", 0).getN());
        Assert.assertEquals("1", convert("getInt", 1).getN());
        Assert.assertEquals("0", convert("getBoxedInt", 0).getN());
        Assert.assertEquals("1", convert("getBoxedInt", 1).getN());
        Assert.assertEquals("0", convert("getLong", 0L).getN());
        Assert.assertEquals("1", convert("getLong", 1L).getN());
        Assert.assertEquals("0", convert("getBoxedLong", 0L).getN());
        Assert.assertEquals("1", convert("getBoxedLong", 1L).getN());
        Assert.assertEquals("0", convert("getBigInt", BigInteger.ZERO).getN());
        Assert.assertEquals("1", convert("getBigInt", BigInteger.ONE).getN());
        Assert.assertEquals("0.0", convert("getFloat", 0.0F).getN());
        Assert.assertEquals("1.0", convert("getFloat", 1.0F).getN());
        Assert.assertEquals("0.0", convert("getBoxedFloat", 0.0F).getN());
        Assert.assertEquals("1.0", convert("getBoxedFloat", 1.0F).getN());
        Assert.assertEquals("0.0", convert("getDouble", 0.0).getN());
        Assert.assertEquals("1.0", convert("getDouble", 1.0).getN());
        Assert.assertEquals("0.0", convert("getBoxedDouble", 0.0).getN());
        Assert.assertEquals("1.0", convert("getBoxedDouble", 1.0).getN());
        Assert.assertEquals("0", convert("getBigDecimal", BigDecimal.ZERO).getN());
        Assert.assertEquals("1", convert("getBigDecimal", BigDecimal.ONE).getN());
    }

    @Test
    public void testBinary() {
        ByteBuffer value = ByteBuffer.wrap("value".getBytes());
        Assert.assertEquals(value.slice(), convert("getByteArray", "value".getBytes()).getB());
        Assert.assertEquals(value.slice(), convert("getByteBuffer", value.slice()).getB());
    }

    @Test
    public void testBooleanSet() {
        Assert.assertEquals(Collections.singletonList("1"), convert("getBooleanSet", Collections.singleton(true)).getNS());
        Assert.assertEquals(Collections.singletonList("0"), convert("getBooleanSet", Collections.singleton(false)).getNS());
        Assert.assertEquals(Arrays.asList("0", "1"), convert("getBooleanSet", new TreeSet<Boolean>() {
            {
                add(true);
                add(false);
            }
        }).getNS());
    }

    @Test
    public void testStringSet() {
        Assert.assertEquals(Collections.singletonList("a"), convert("getStringSet", Collections.singleton("a")).getSS());
        Assert.assertEquals(Collections.singletonList("b"), convert("getStringSet", Collections.singleton("b")).getSS());
        Assert.assertEquals(Arrays.asList("a", "b", "c"), convert("getStringSet", new TreeSet<String>() {
            {
                add("a");
                add("b");
                add("c");
            }
        }).getSS());
    }

    @Test
    public void testUuidSet() {
        final UUID one = UUID.randomUUID();
        final UUID two = UUID.randomUUID();
        final UUID three = UUID.randomUUID();
        Assert.assertEquals(Collections.singletonList(one.toString()), convert("getUuidSet", Collections.singleton(one)).getSS());
        Assert.assertEquals(Collections.singletonList(two.toString()), convert("getUuidSet", Collections.singleton(two)).getSS());
        Assert.assertEquals(Arrays.asList(one.toString(), two.toString(), three.toString()), convert("getUuidSet", new LinkedHashSet<UUID>() {
            {
                add(one);
                add(two);
                add(three);
            }
        }).getSS());
    }

    @Test
    public void testDateSet() {
        Assert.assertEquals(Collections.singletonList("1970-01-01T00:00:00.000Z"), convert("getDateSet", Collections.singleton(new Date(0))).getSS());
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(0);
        Assert.assertEquals(Collections.singletonList("1970-01-01T00:00:00.000Z"), convert("getCalendarSet", Collections.singleton(c)).getSS());
    }

    @Test
    public void testNumberSet() {
        Assert.assertEquals(Collections.singletonList("0"), convert("getByteSet", Collections.singleton(((byte) (0)))).getNS());
        Assert.assertEquals(Collections.singletonList("0"), convert("getShortSet", Collections.singleton(((short) (0)))).getNS());
        Assert.assertEquals(Collections.singletonList("0"), convert("getIntSet", Collections.singleton(0)).getNS());
        Assert.assertEquals(Collections.singletonList("0"), convert("getLongSet", Collections.singleton(0L)).getNS());
        Assert.assertEquals(Collections.singletonList("0"), convert("getBigIntegerSet", Collections.singleton(BigInteger.ZERO)).getNS());
        Assert.assertEquals(Collections.singletonList("0.0"), convert("getFloatSet", Collections.singleton(0.0F)).getNS());
        Assert.assertEquals(Collections.singletonList("0.0"), convert("getDoubleSet", Collections.singleton(0.0)).getNS());
        Assert.assertEquals(Collections.singletonList("0"), convert("getBigDecimalSet", Collections.singleton(BigDecimal.ZERO)).getNS());
        Assert.assertEquals(Arrays.asList("0", "1", "2"), convert("getLongSet", new TreeSet<Number>() {
            {
                add(0);
                add(1);
                add(2);
            }
        }).getNS());
    }

    @Test
    public void testBinarySet() {
        final ByteBuffer test = ByteBuffer.wrap("test".getBytes());
        final ByteBuffer test2 = ByteBuffer.wrap("test2".getBytes());
        Assert.assertEquals(Collections.singletonList(test.slice()), convert("getByteArraySet", Collections.singleton("test".getBytes())).getBS());
        Assert.assertEquals(Collections.singletonList(test.slice()), convert("getByteBufferSet", Collections.singleton(test.slice())).getBS());
        Assert.assertEquals(Arrays.asList(test.slice(), test2.slice()), convert("getByteBufferSet", new TreeSet<ByteBuffer>() {
            {
                add(test.slice());
                add(test2.slice());
            }
        }).getBS());
    }

    @Test
    public void testObjectSet() {
        Object o = new Object() {
            @Override
            public String toString() {
                return "hello";
            }
        };
        Assert.assertEquals(Collections.singletonList("hello"), convert("getObjectSet", Collections.singleton(o)).getSS());
    }

    @Test
    public void testList() {
        Assert.assertEquals(Arrays.asList(new AttributeValue("a"), new AttributeValue("b"), new AttributeValue("c")), convert("getList", Arrays.asList("a", "b", "c")).getL());
        Assert.assertEquals(Arrays.asList(new AttributeValue().withNULL(true)), convert("getList", Collections.<String>singletonList(null)).getL());
    }

    @Test
    public void testSetList() {
        Assert.assertEquals(Arrays.asList(new AttributeValue().withSS("a"), new AttributeValue().withSS("b"), new AttributeValue().withSS("c")), convert("getSetList", Arrays.asList(Collections.singleton("a"), Collections.singleton("b"), Collections.singleton("c"))).getL());
    }

    @Test
    public void testMap() {
        Assert.assertEquals(new HashMap<String, AttributeValue>() {
            {
                put("a", new AttributeValue("b"));
                put("c", new AttributeValue("d"));
                put("e", new AttributeValue("f"));
            }
        }, convert("getMap", new HashMap<String, String>() {
            {
                put("a", "b");
                put("c", "d");
                put("e", "f");
            }
        }).getM());
        Assert.assertEquals(Collections.singletonMap("a", new AttributeValue().withNULL(true)), convert("getMap", Collections.<String, String>singletonMap("a", null)).getM());
    }

    @Test
    public void testSetMap() {
        Assert.assertEquals(new HashMap<String, AttributeValue>() {
            {
                put("a", new AttributeValue().withSS("a", "b"));
            }
        }, convert("getSetMap", new HashMap<String, Set<String>>() {
            {
                put("a", new TreeSet<String>(Arrays.asList("a", "b")));
            }
        }).getM());
        Assert.assertEquals(new HashMap<String, AttributeValue>() {
            {
                put("a", new AttributeValue().withSS("a"));
                put("b", new AttributeValue().withNULL(true));
            }
        }, convert("getSetMap", new HashMap<String, Set<String>>() {
            {
                put("a", new TreeSet<String>(Arrays.asList("a")));
                put("b", null);
            }
        }).getM());
    }

    @Test
    public void testObject() {
        Assert.assertEquals(new HashMap<String, AttributeValue>() {
            {
                put("name", new AttributeValue("name"));
                put("value", new AttributeValue().withN("123"));
            }
        }, convert("getObject", new SubClass()).getM());
    }

    @Test
    public void testUnannotatedObject() throws Exception {
        try {
            convert(UnannotatedSubClass.class, UnannotatedSubClass.class.getMethod("getChild"), new UnannotatedSubClass());
            Assert.fail("Expected DynamoDBMappingException");
        } catch (DynamoDBMappingException e) {
        }
    }

    @Test
    public void testS3Link() {
        S3ClientCache cache = new S3ClientCache(((AWSCredentialsProvider) (null)));
        S3Link link = new S3Link(cache, "bucket", "key");
        Assert.assertEquals(("{\"s3\":{" + (("\"bucket\":\"bucket\"," + "\"key\":\"key\",") + "\"region\":null}}")), convert("getS3Link", link).getS());
    }
}

