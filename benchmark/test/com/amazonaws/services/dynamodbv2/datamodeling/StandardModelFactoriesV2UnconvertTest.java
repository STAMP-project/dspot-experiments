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


import ConversionSchemas.V2;
import DynamoDBMapperConfig.DEFAULT;
import DynamoDBMapperModelFactory.TableFactory;
import Region.US_Standard;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.pojos.SubClass;
import com.amazonaws.services.dynamodbv2.pojos.UnannotatedSubClass;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class StandardModelFactoriesV2UnconvertTest {
    protected static final DynamoDBMapperConfig CONFIG = new DynamoDBMapperConfig.Builder().withTypeConverterFactory(DEFAULT.getTypeConverterFactory()).withConversionSchema(V2).build();

    private static final DynamoDBMapperModelFactory factory = StandardModelFactories.of(new S3Link.Factory(new S3ClientCache(((AWSCredentialsProvider) (null)))));

    private static final TableFactory models = StandardModelFactoriesV2UnconvertTest.factory.getTableFactory(StandardModelFactoriesV2UnconvertTest.CONFIG);

    @Test
    public void testBoolean() {
        Assert.assertEquals(false, unconvert("getBoolean", "setBoolean", new AttributeValue().withN("0")));
        Assert.assertEquals(true, unconvert("getBoolean", "setBoolean", new AttributeValue().withN("1")));
        Assert.assertEquals(false, unconvert("getBoolean", "setBoolean", new AttributeValue().withBOOL(false)));
        Assert.assertEquals(true, unconvert("getBoolean", "setBoolean", new AttributeValue().withBOOL(true)));
        Assert.assertEquals(false, unconvert("getBoxedBoolean", "setBoxedBoolean", new AttributeValue().withN("0")));
        Assert.assertEquals(true, unconvert("getBoxedBoolean", "setBoxedBoolean", new AttributeValue().withN("1")));
        Assert.assertEquals(false, unconvert("getBoxedBoolean", "setBoxedBoolean", new AttributeValue().withBOOL(false)));
        Assert.assertEquals(true, unconvert("getBoxedBoolean", "setBoxedBoolean", new AttributeValue().withBOOL(true)));
    }

    @Test
    public void testString() {
        Assert.assertEquals("test", unconvert("getString", "setString", new AttributeValue("test")));
        Assert.assertNull(unconvert("getCustomString", "setCustomString", new AttributeValue("ignoreme")));
    }

    @Test
    public void testUuid() {
        UUID uuid = UUID.randomUUID();
        Assert.assertEquals(uuid, unconvert("getUuid", "setUuid", new AttributeValue(uuid.toString())));
    }

    @Test
    public void testDate() {
        Assert.assertEquals(new Date(0), unconvert("getDate", "setDate", new AttributeValue("1970-01-01T00:00:00.000Z")));
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(0);
        Assert.assertEquals(c, unconvert("getCalendar", "setCalendar", new AttributeValue("1970-01-01T00:00:00.000Z")));
    }

    @Test
    public void testNumbers() {
        Assert.assertEquals(((byte) (1)), unconvert("getByte", "setByte", new AttributeValue().withN("1")));
        Assert.assertEquals(((byte) (1)), unconvert("getBoxedByte", "setBoxedByte", new AttributeValue().withN("1")));
        Assert.assertEquals(((short) (1)), unconvert("getShort", "setShort", new AttributeValue().withN("1")));
        Assert.assertEquals(((short) (1)), unconvert("getBoxedShort", "setBoxedShort", new AttributeValue().withN("1")));
        Assert.assertEquals(1, unconvert("getInt", "setInt", new AttributeValue().withN("1")));
        Assert.assertEquals(1, unconvert("getBoxedInt", "setBoxedInt", new AttributeValue().withN("1")));
        Assert.assertEquals(1L, unconvert("getLong", "setLong", new AttributeValue().withN("1")));
        Assert.assertEquals(1L, unconvert("getBoxedLong", "setBoxedLong", new AttributeValue().withN("1")));
        Assert.assertEquals(BigInteger.ONE, unconvert("getBigInt", "setBigInt", new AttributeValue().withN("1")));
        Assert.assertEquals(1.5F, unconvert("getFloat", "setFloat", new AttributeValue().withN("1.5")));
        Assert.assertEquals(1.5F, unconvert("getBoxedFloat", "setBoxedFloat", new AttributeValue().withN("1.5")));
        Assert.assertEquals(1.5, unconvert("getDouble", "setDouble", new AttributeValue().withN("1.5")));
        Assert.assertEquals(1.5, unconvert("getBoxedDouble", "setBoxedDouble", new AttributeValue().withN("1.5")));
        Assert.assertEquals(BigDecimal.ONE, unconvert("getBigDecimal", "setBigDecimal", new AttributeValue().withN("1")));
    }

    @Test
    public void testBinary() {
        ByteBuffer test = ByteBuffer.wrap("test".getBytes());
        Assert.assertTrue(Arrays.equals("test".getBytes(), ((byte[]) (unconvert("getByteArray", "setByteArray", new AttributeValue().withB(test.slice()))))));
        Assert.assertEquals(test.slice(), unconvert("getByteBuffer", "setByteBuffer", new AttributeValue().withB(test.slice())));
    }

    @Test
    public void testBooleanSet() {
        Assert.assertEquals(new HashSet<Boolean>() {
            {
                add(true);
            }
        }, unconvert("getBooleanSet", "setBooleanSet", new AttributeValue().withNS("1")));
        Assert.assertEquals(new HashSet<Boolean>() {
            {
                add(false);
            }
        }, unconvert("getBooleanSet", "setBooleanSet", new AttributeValue().withNS("0")));
        Assert.assertEquals(new HashSet<Boolean>() {
            {
                add(true);
                add(false);
            }
        }, unconvert("getBooleanSet", "setBooleanSet", new AttributeValue().withNS("0", "1")));
        Assert.assertEquals(new HashSet<Boolean>() {
            {
                add(true);
            }
        }, unconvert("getBooleanSet", "setBooleanSet", new AttributeValue().withL(new AttributeValue().withBOOL(true))));
        Assert.assertEquals(new HashSet<Boolean>() {
            {
                add(false);
            }
        }, unconvert("getBooleanSet", "setBooleanSet", new AttributeValue().withL(new AttributeValue().withBOOL(false))));
        Assert.assertEquals(new HashSet<Boolean>() {
            {
                add(false);
                add(true);
            }
        }, unconvert("getBooleanSet", "setBooleanSet", new AttributeValue().withL(new AttributeValue().withBOOL(false), new AttributeValue().withBOOL(true))));
        Assert.assertEquals(new HashSet<Boolean>() {
            {
                add(null);
            }
        }, unconvert("getBooleanSet", "setBooleanSet", new AttributeValue().withL(new AttributeValue().withNULL(true))));
    }

    @Test
    public void testStringSet() {
        Assert.assertNull(unconvert("getStringSet", "setStringSet", new AttributeValue().withNULL(true)));
        Assert.assertEquals(new HashSet<String>() {
            {
                add("a");
                add("b");
            }
        }, unconvert("getStringSet", "setStringSet", new AttributeValue().withSS("a", "b")));
    }

    @Test
    public void testUuidSet() {
        Assert.assertNull(unconvert("getUuidSet", "setUuidSet", new AttributeValue().withNULL(true)));
        final UUID one = UUID.randomUUID();
        final UUID two = UUID.randomUUID();
        Assert.assertEquals(new HashSet<UUID>() {
            {
                add(one);
                add(two);
            }
        }, unconvert("getUuidSet", "setUuidSet", new AttributeValue().withSS(one.toString(), two.toString())));
    }

    @Test
    public void testDateSet() {
        Assert.assertEquals(Collections.singleton(new Date(0)), unconvert("getDateSet", "setDateSet", new AttributeValue().withSS("1970-01-01T00:00:00.000Z")));
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(0);
        Assert.assertEquals(Collections.singleton(c), unconvert("getCalendarSet", "setCalendarSet", new AttributeValue().withSS("1970-01-01T00:00:00.000Z")));
    }

    @Test
    public void testNumberSet() {
        Assert.assertNull(unconvert("getByteSet", "setByteSet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getShortSet", "setShortSet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getIntSet", "setIntSet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getLongSet", "setLongSet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getBigIntegerSet", "setBigIntegerSet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getFloatSet", "setFloatSet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getDoubleSet", "setDoubleSet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getBigDecimalSet", "setBigDecimalSet", new AttributeValue().withNULL(true)));
        Assert.assertEquals(new HashSet<Byte>() {
            {
                add(((byte) (1)));
            }
        }, unconvert("getByteSet", "setByteSet", new AttributeValue().withNS("1")));
        Assert.assertEquals(new HashSet<Short>() {
            {
                add(((short) (1)));
            }
        }, unconvert("getShortSet", "setShortSet", new AttributeValue().withNS("1")));
        Assert.assertEquals(new HashSet<Integer>() {
            {
                add(1);
            }
        }, unconvert("getIntSet", "setIntSet", new AttributeValue().withNS("1")));
        Assert.assertEquals(new HashSet<Long>() {
            {
                add(1L);
            }
        }, unconvert("getLongSet", "setLongSet", new AttributeValue().withNS("1")));
        Assert.assertEquals(new HashSet<BigInteger>() {
            {
                add(BigInteger.ONE);
            }
        }, unconvert("getBigIntegerSet", "setBigIntegerSet", new AttributeValue().withNS("1")));
        Assert.assertEquals(new HashSet<Float>() {
            {
                add(1.5F);
            }
        }, unconvert("getFloatSet", "setFloatSet", new AttributeValue().withNS("1.5")));
        Assert.assertEquals(new HashSet<Double>() {
            {
                add(1.5);
            }
        }, unconvert("getDoubleSet", "setDoubleSet", new AttributeValue().withNS("1.5")));
        Assert.assertEquals(new HashSet<BigDecimal>() {
            {
                add(BigDecimal.ONE);
            }
        }, unconvert("getBigDecimalSet", "setBigDecimalSet", new AttributeValue().withNS("1")));
    }

    @Test
    public void testBinarySet() {
        Assert.assertNull(unconvert("getByteArraySet", "setByteArraySet", new AttributeValue().withNULL(true)));
        Assert.assertNull(unconvert("getByteBufferSet", "setByteBufferSet", new AttributeValue().withNULL(true)));
        ByteBuffer test = ByteBuffer.wrap("test".getBytes());
        Set<byte[]> result = ((Set<byte[]>) (unconvert("getByteArraySet", "setByteArraySet", new AttributeValue().withBS(test.slice()))));
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(Arrays.equals("test".getBytes(), result.iterator().next()));
        Assert.assertEquals(Collections.singleton(test.slice()), unconvert("getByteBufferSet", "setByteBufferSet", new AttributeValue().withBS(test.slice())));
    }

    @Test
    public void testObjectSet() {
        Object result = unconvert("getObjectSet", "setObjectSet", new AttributeValue().withL(new AttributeValue().withM(new HashMap<String, AttributeValue>() {
            {
                put("name", new AttributeValue("name"));
                put("value", new AttributeValue().withN("123"));
                put("null", new AttributeValue().withNULL(true));
            }
        })));
        Assert.assertEquals(Collections.singleton(new SubClass()), result);
        result = unconvert("getObjectSet", "setObjectSet", new AttributeValue().withL(new AttributeValue().withNULL(true)));
        Assert.assertEquals(Collections.<SubClass>singleton(null), result);
    }

    @Test
    public void testList() {
        Assert.assertNull(unconvert("getList", "setList", new AttributeValue().withNULL(true)));
        Assert.assertEquals(Arrays.asList("a", "b", "c"), unconvert("getList", "setList", new AttributeValue().withL(new AttributeValue("a"), new AttributeValue("b"), new AttributeValue("c"))));
        Assert.assertEquals(Arrays.asList("a", null), unconvert("getList", "setList", new AttributeValue().withL(new AttributeValue("a"), new AttributeValue().withNULL(true))));
    }

    @Test
    public void testObjectList() {
        Assert.assertNull(unconvert("getObjectList", "setObjectList", new AttributeValue().withNULL(true)));
        Assert.assertEquals(Arrays.asList(new SubClass(), null), unconvert("getObjectList", "setObjectList", new AttributeValue().withL(new AttributeValue().withM(new HashMap<String, AttributeValue>() {
            {
                put("name", new AttributeValue("name"));
                put("value", new AttributeValue().withN("123"));
                put("null", new AttributeValue().withNULL(true));
            }
        }), new AttributeValue().withNULL(true))));
    }

    @Test
    public void testSetList() {
        Assert.assertNull(unconvert("getSetList", "setSetList", new AttributeValue().withNULL(true)));
        Assert.assertEquals(Arrays.asList(new Set[]{ null }), unconvert("getSetList", "setSetList", new AttributeValue().withL(new AttributeValue().withNULL(true))));
        Assert.assertEquals(Arrays.asList(Collections.singleton("a")), unconvert("getSetList", "setSetList", new AttributeValue().withL(new AttributeValue().withSS("a"))));
    }

    @Test
    public void testMap() {
        Assert.assertNull(unconvert("getMap", "setMap", new AttributeValue().withNULL(true)));
        Assert.assertEquals(new HashMap<String, String>() {
            {
                put("a", "b");
                put("c", "d");
            }
        }, unconvert("getMap", "setMap", new AttributeValue().withM(new HashMap<String, AttributeValue>() {
            {
                put("a", new AttributeValue("b"));
                put("c", new AttributeValue("d"));
            }
        })));
        Assert.assertEquals(new HashMap<String, String>() {
            {
                put("a", null);
            }
        }, unconvert("getMap", "setMap", new AttributeValue().withM(new HashMap<String, AttributeValue>() {
            {
                put("a", new AttributeValue().withNULL(true));
            }
        })));
    }

    @Test
    public void testSetMap() {
        Assert.assertNull(unconvert("getSetMap", "setSetMap", new AttributeValue().withNULL(true)));
        Assert.assertEquals(new HashMap<String, Set<String>>() {
            {
                put("a", null);
                put("b", new TreeSet<String>(Arrays.asList("a", "b")));
            }
        }, unconvert("getSetMap", "setSetMap", new AttributeValue().withM(new HashMap<String, AttributeValue>() {
            {
                put("a", new AttributeValue().withNULL(true));
                put("b", new AttributeValue().withSS("a", "b"));
            }
        })));
    }

    @Test
    public void testObject() {
        Assert.assertNull(unconvert("getObject", "setObject", new AttributeValue().withNULL(true)));
        Assert.assertEquals(new SubClass(), unconvert("getObject", "setObject", new AttributeValue().withM(new HashMap<String, AttributeValue>() {
            {
                put("name", new AttributeValue("name"));
                put("value", new AttributeValue().withN("123"));
            }
        })));
        Assert.assertEquals(new SubClass(), unconvert("getObject", "setObject", new AttributeValue().withM(new HashMap<String, AttributeValue>() {
            {
                put("name", new AttributeValue("name"));
                put("value", new AttributeValue().withN("123"));
                put("null", new AttributeValue().withNULL(true));
            }
        })));
    }

    @Test
    public void testUnannotatedObject() throws Exception {
        Method getter = UnannotatedSubClass.class.getMethod("getChild");
        Method setter = UnannotatedSubClass.class.getMethod("setChild", UnannotatedSubClass.class);
        try {
            unconvert(UnannotatedSubClass.class, getter, setter, new AttributeValue().withS(""));
            Assert.fail("Expected DynamoDBMappingException");
        } catch (DynamoDBMappingException e) {
        }
    }

    @Test
    public void testS3Link() {
        S3Link link = ((S3Link) (unconvert("getS3Link", "setS3Link", new AttributeValue(("{\"s3\":{" + (("\"bucket\":\"bucket\"," + "\"key\":\"key\",") + "\"region\":null}}"))))));
        Assert.assertEquals("bucket", link.getBucketName());
        Assert.assertEquals("key", link.getKey());
        Assert.assertEquals(US_Standard, link.getS3Region());
    }
}

