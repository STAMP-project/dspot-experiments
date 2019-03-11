/**
 * Copyright 2016-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling;


import DynamoDBAttributeType.B;
import DynamoDBAttributeType.M;
import DynamoDBAttributeType.N;
import DynamoDBAttributeType.S;
import DynamoDBAutoGenerateStrategy.ALWAYS;
import DynamoDBAutoGenerateStrategy.CREATE;
import DynamoDBConvertedBool.Format;
import DynamoDBMapperConfig.DEFAULT;
import DynamoDBMapperModelFactory.TableFactory;
import KeyType.HASH;
import KeyType.RANGE;
import S3Link.Factory;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.pojos.AutoKeyAndVal;
import com.amazonaws.services.dynamodbv2.pojos.DateRange;
import com.amazonaws.services.dynamodbv2.pojos.KeyAndVal;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static DynamoDBAutoGenerateStrategy.ALWAYS;
import static DynamoDBAutoGenerateStrategy.CREATE;


/**
 * Unit tests for {@link DynamoDBMapperModelFactory.TableFactory}.
 */
public class StandardModelFactoriesTest {
    private static final DynamoDBMapperModelFactory factory = StandardModelFactories.of(Factory.of(null));

    private static final TableFactory models = StandardModelFactoriesTest.factory.getTableFactory(DEFAULT);

    /**
     * Test mappings.
     */
    @Test
    public void testHashAndRangeKey() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBHashKey(attributeName = "hk")
            public String getKey() {
                return super.getKey();
            }

            @DynamoDBRangeKey(attributeName = "rk")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        StandardModelFactoriesTest.assertFieldKeyType(HASH, model.field("hk"), model);
        StandardModelFactoriesTest.assertFieldKeyType(RANGE, model.field("rk"), model);
    }

    /**
     * Test mappings.
     */
    @Test(expected = DynamoDBMappingException.class)
    public void testHashAndRangeKeyConflict() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBHashKey
            @DynamoDBRangeKey
            public String getKey() {
                return super.getKey();
            }
        };
        StandardModelFactoriesTest.getTable(obj);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testNamed() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBNamed("value")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        Assert.assertEquals(2, model.fields().size());
        Assert.assertNotNull(model.field("key"));
        Assert.assertNotNull(model.field("value"));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAttributeTypeAsNumber() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBTyped(DynamoDBAttributeType.N)
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(N, val.attributeType());
    }

    @Test
    public void testAttributeTypeAsAttributeValueNumber() {
        final Object obj = new AutoKeyAndVal<AttributeValue>() {
            @DynamoDBTyped(DynamoDBAttributeType.N)
            public AttributeValue getVal() {
                return super.getVal();
            }

            public void setVal(final AttributeValue val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, AttributeValue> val = model.field("val");
        Assert.assertEquals(N, val.attributeType());
        Assert.assertEquals("123", val.convert(new AttributeValue().withN("123")).getN());
        Assert.assertEquals("123", val.unconvert(new AttributeValue().withN("123")).getN());
    }

    @Test
    public void testAttributeTypeAsAttributeValueMap() {
        final Object obj = new AutoKeyAndVal<AttributeValue>() {
            @DynamoDBTyped(DynamoDBAttributeType.M)
            public AttributeValue getVal() {
                return super.getVal();
            }

            public void setVal(final AttributeValue val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, AttributeValue> val = model.field("val");
        Assert.assertEquals(M, val.attributeType());
        Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
        map.put("A", new AttributeValue().withN("123"));
        map = Collections.unmodifiableMap(map);
        Assert.assertEquals("123", val.convert(new AttributeValue().withM(map)).getM().get("A").getN());
        Assert.assertEquals("123", val.unconvert(new AttributeValue().withM(map)).getM().get("A").getN());
    }

    /**
     * Test mappings.
     */
    @Test
    public void testScalarAttributeStringTimeZone() {
        final Object obj = new AutoKeyAndVal<TimeZone>() {
            @DynamoDBHashKey
            public String getKey() {
                return super.getKey();
            }

            @DynamoDBScalarAttribute(type = ScalarAttributeType.S)
            public TimeZone getVal() {
                return super.getVal();
            }

            public void setVal(final TimeZone val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, TimeZone> val = model.field("val");
        Assert.assertEquals(S, val.attributeType());
        Assert.assertEquals("America/New_York", val.convert(TimeZone.getTimeZone("America/New_York")).getS());
        Assert.assertEquals("America/New_York", val.unconvert(new AttributeValue().withS("America/New_York")).getID());
    }

    /**
     * Test mappings.
     */
    @Test
    public void testScalarAttributeStringLocale() {
        final Object obj = new AutoKeyAndVal<Locale>() {
            @DynamoDBHashKey
            public String getKey() {
                return super.getKey();
            }

            @DynamoDBScalarAttribute(type = ScalarAttributeType.S)
            public Locale getVal() {
                return super.getVal();
            }

            public void setVal(final Locale val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Locale> val = model.field("val");
        Assert.assertEquals(S, val.attributeType());
        Assert.assertEquals("en-CA", val.convert(new Locale("en", "CA")).getS());
        Assert.assertEquals("en-CA", val.unconvert(new AttributeValue().withS("en-CA")).toString().replaceAll("_", "-"));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testScalarAttributeBinaryUuid() {
        final Object obj = new AutoKeyAndVal<UUID>() {
            @DynamoDBHashKey
            public String getKey() {
                return super.getKey();
            }

            @DynamoDBScalarAttribute(type = ScalarAttributeType.B)
            public UUID getVal() {
                return super.getVal();
            }

            public void setVal(final UUID val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        Assert.assertEquals(B, model.field("val").attributeType());
        final UUID val = UUID.randomUUID();
        final AttributeValue converted = model.field("val").convert(val);
        Assert.assertNotNull(converted.getB());
        Assert.assertEquals(val, model.field("val").unconvert(converted));
    }

    @Test
    public void testScalarAttributeAttributeName() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBHashKey
            public String getKey() {
                return super.getKey();
            }

            @DynamoDBScalarAttribute(attributeName = "value", type = ScalarAttributeType.S)
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.models.getTable(((Class<Object>) (obj.getClass())));
        final DynamoDBMapperFieldModel<Object, String> val = model.field("value");
        Assert.assertEquals(S, val.attributeType());
    }

    /**
     * Test mappings.
     */
    @Test
    public void testIgnore() {
        final Object obj = new AutoKeyAndVal<String>() {
            private String ignore;

            @DynamoDBAttribute(attributeName = "value")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }

            @DynamoDBIgnore
            @DynamoDBAttribute(attributeName = "ignore")
            public String getIgnore() {
                return this.ignore;
            }

            public void setIgnore(final String ignore) {
                this.ignore = ignore;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        Assert.assertEquals(2, model.fields().size());
        Assert.assertNotNull(model.field("key"));
        Assert.assertNotNull(model.field("value"));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testConvertedBool() {
        final Object obj = new AutoKeyAndVal<Boolean>() {
            @DynamoDBConvertedBool(Format.Y_N)
            public Boolean getVal() {
                return super.getVal();
            }

            public void setVal(final Boolean val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Boolean> val = model.field("val");
        Assert.assertEquals(S, val.attributeType());
        Assert.assertEquals("Y", val.convert(Boolean.TRUE).getS());
        Assert.assertEquals(Boolean.TRUE, val.unconvert(new AttributeValue().withS("Y")));
        Assert.assertEquals("N", val.convert(Boolean.FALSE).getS());
        Assert.assertEquals(Boolean.FALSE, val.unconvert(new AttributeValue().withS("N")));
        Assert.assertEquals(null, val.convert(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedHashKeyString() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBAttribute
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> key = model.field("key");
        StandardModelFactoriesTest.assertFieldKeyType(HASH, key, model);
        Assert.assertEquals(CREATE, key.getGenerateStrategy());
        Assert.assertNotNull(key.generate(null));
        Assert.assertNotNull(key.generate(UUID.randomUUID().toString()));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedRangeKeyUuid() {
        final Object obj = new AutoKeyAndVal<UUID>() {
            @DynamoDBRangeKey
            @DynamoDBAutoGeneratedKey
            public UUID getVal() {
                return super.getVal();
            }

            public void setVal(final UUID val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        StandardModelFactoriesTest.assertFieldKeyType(RANGE, val, model);
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertNotNull(val.generate(null));
        Assert.assertNotNull(val.generate(UUID.randomUUID()));
    }

    /**
     * Test mappings.
     */
    @Test(expected = DynamoDBMappingException.class)
    public void testAutoGeneratedConflict() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBHashKey
            @DynamoDBAutoGeneratedKey
            @DynamoDBVersionAttribute
            public String getKey() {
                return super.getKey();
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
    }

    /**
     * Test mappings.
     */
    @Test(expected = DynamoDBMappingException.class)
    public void testAutoGeneratedVersionUuid() {
        final Object obj = new AutoKeyAndVal<UUID>() {
            @DynamoDBVersionAttribute
            public UUID getVal() {
                return super.getVal();
            }

            public void setVal(final UUID val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        val.generate(null);// <- should fail

    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionBigInteger() {
        final Object obj = new AutoKeyAndVal<BigInteger>() {
            @DynamoDBVersionAttribute
            public BigInteger getVal() {
                return super.getVal();
            }

            public void setVal(final BigInteger val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(true, val.versioned());
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertEquals(BigInteger.ONE, val.generate(null));
        Assert.assertEquals(BigInteger.valueOf(((int) (2))), val.generate(BigInteger.ONE));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionByte() {
        final Object obj = new AutoKeyAndVal<Byte>() {
            @DynamoDBVersionAttribute
            public Byte getVal() {
                return super.getVal();
            }

            public void setVal(final Byte val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(true, val.versioned());
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertEquals(Byte.valueOf(((byte) (1))), val.generate(null));
        Assert.assertEquals(Byte.valueOf(((byte) (2))), val.generate(Byte.valueOf(((byte) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionBytePrimitive() {
        final Object obj = new AutoKeyAndVal<String>() {
            private byte rvn;

            @DynamoDBAttribute
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }

            @DynamoDBVersionAttribute
            public byte getRvn() {
                return this.rvn;
            }

            public void setRvn(final byte rvn) {
                this.rvn = rvn;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> rvn = model.field("rvn");
        Assert.assertEquals(true, rvn.versioned());
        Assert.assertEquals(ALWAYS, rvn.getGenerateStrategy());
        Assert.assertEquals(Byte.valueOf(((byte) (1))), rvn.generate(null));
        Assert.assertEquals(Byte.valueOf(((byte) (2))), rvn.generate(Byte.valueOf(((byte) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionInteger() {
        final Object obj = new AutoKeyAndVal<Integer>() {
            @DynamoDBVersionAttribute
            public Integer getVal() {
                return super.getVal();
            }

            public void setVal(final Integer val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(true, val.versioned());
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertEquals(Integer.valueOf(((int) (1))), val.generate(null));
        Assert.assertEquals(Integer.valueOf(((int) (2))), val.generate(Integer.valueOf(((int) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionIntegerPrimitive() {
        final Object obj = new AutoKeyAndVal<String>() {
            private int rvn;

            @DynamoDBAttribute
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }

            @DynamoDBVersionAttribute
            public int getRvn() {
                return this.rvn;
            }

            public void setRvn(final int rvn) {
                this.rvn = rvn;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> rvn = model.field("rvn");
        Assert.assertEquals(true, rvn.versioned());
        Assert.assertEquals(ALWAYS, rvn.getGenerateStrategy());
        Assert.assertEquals(Integer.valueOf(((int) (1))), rvn.generate(null));
        Assert.assertEquals(Integer.valueOf(((int) (2))), rvn.generate(Integer.valueOf(((int) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionLong() {
        final Object obj = new AutoKeyAndVal<Long>() {
            @DynamoDBVersionAttribute
            public Long getVal() {
                return super.getVal();
            }

            public void setVal(final Long val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(true, val.versioned());
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertEquals(Long.valueOf(((long) (1))), val.generate(null));
        Assert.assertEquals(Long.valueOf(((long) (2))), val.generate(Long.valueOf(((long) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionLongPrimitive() {
        final Object obj = new AutoKeyAndVal<String>() {
            private long rvn;

            @DynamoDBAttribute
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }

            @DynamoDBVersionAttribute
            public long getRvn() {
                return this.rvn;
            }

            public void setRvn(final long rvn) {
                this.rvn = rvn;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> rvn = model.field("rvn");
        Assert.assertEquals(true, rvn.versioned());
        Assert.assertEquals(ALWAYS, rvn.getGenerateStrategy());
        Assert.assertEquals(Long.valueOf(((long) (1))), rvn.generate(null));
        Assert.assertEquals(Long.valueOf(((long) (2))), rvn.generate(Long.valueOf(((long) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionShort() {
        final Object obj = new AutoKeyAndVal<Short>() {
            @DynamoDBVersionAttribute
            public Short getVal() {
                return super.getVal();
            }

            public void setVal(final Short val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(true, val.versioned());
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertEquals(Short.valueOf(((short) (1))), val.generate(null));
        Assert.assertEquals(Short.valueOf(((short) (2))), val.generate(Short.valueOf(((short) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedVersionShortPrimitive() {
        final Object obj = new AutoKeyAndVal<String>() {
            private short rvn;

            @DynamoDBAttribute
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }

            @DynamoDBVersionAttribute
            public short getRvn() {
                return this.rvn;
            }

            public void setRvn(final short rvn) {
                this.rvn = rvn;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> rvn = model.field("rvn");
        Assert.assertEquals(true, rvn.versioned());
        Assert.assertEquals(ALWAYS, rvn.getGenerateStrategy());
        Assert.assertEquals(Short.valueOf(((short) (1))), rvn.generate(null));
        Assert.assertEquals(Short.valueOf(((short) (2))), rvn.generate(Short.valueOf(((short) (1)))));
    }

    /**
     * Test mappings.
     */
    @Test(expected = DynamoDBMappingException.class)
    public void testAutoGeneratedTimestampUuid() {
        final Object obj = new AutoKeyAndVal<UUID>() {
            @DynamoDBAutoGeneratedTimestamp
            public UUID getVal() {
                return super.getVal();
            }

            public void setVal(final UUID val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedTimestampCalendar() {
        final Object obj = new AutoKeyAndVal<Calendar>() {
            @DynamoDBAutoGeneratedTimestamp
            public Calendar getVal() {
                return super.getVal();
            }

            public void setVal(final Calendar val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertNotNull(val.generate(null));
        Assert.assertNotNull(val.generate(Calendar.getInstance()));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedTimestampDateKey() {
        final Object obj = new AutoKeyAndVal<Date>() {
            @DynamoDBRangeKey
            @DynamoDBAutoGeneratedTimestamp(strategy = CREATE)
            public Date getVal() {
                return super.getVal();
            }

            public void setVal(final Date val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        StandardModelFactoriesTest.assertFieldKeyType(RANGE, val, model);
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertNotNull(val.generate(null));
        Assert.assertNotNull(val.generate(new Date()));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedTimestampDateVal() {
        final Object obj = new AutoKeyAndVal<Date>() {
            @DynamoDBAutoGeneratedTimestamp
            public Date getVal() {
                return super.getVal();
            }

            public void setVal(final Date val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertNotNull(val.generate(null));
        Assert.assertNotNull(val.generate(new Date()));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedTimestampLong() {
        final Object obj = new AutoKeyAndVal<Long>() {
            @DynamoDBAutoGeneratedTimestamp
            public Long getVal() {
                return super.getVal();
            }

            public void setVal(final Long val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(ALWAYS, val.getGenerateStrategy());
        Assert.assertNotNull(val.generate(null));
        Assert.assertNotNull(val.generate(System.currentTimeMillis()));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultByteBuffer() {
        final Object obj = new AutoKeyAndVal<ByteBuffer>() {
            @DynamoDBAutoGeneratedDefault("default-val")
            public ByteBuffer getVal() {
                return super.getVal();
            }

            public void setVal(final ByteBuffer val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertNotNull(val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultBigDecimal() {
        final Object obj = new AutoKeyAndVal<BigDecimal>() {
            @DynamoDBAutoGeneratedDefault("1234.5")
            public BigDecimal getVal() {
                return super.getVal();
            }

            public void setVal(final BigDecimal val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(BigDecimal.valueOf(1234.5), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultBigInteger() {
        final Object obj = new AutoKeyAndVal<BigInteger>() {
            @DynamoDBAutoGeneratedDefault("1234")
            public BigInteger getVal() {
                return super.getVal();
            }

            public void setVal(final BigInteger val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(BigInteger.valueOf(1234), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultBoolean_true() {
        final Object obj = new AutoKeyAndVal<Boolean>() {
            @DynamoDBAutoGeneratedDefault("true")
            public Boolean getVal() {
                return super.getVal();
            }

            public void setVal(final Boolean val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Boolean.TRUE, val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultBoolean_0() {
        final Object obj = new AutoKeyAndVal<Boolean>() {
            @DynamoDBAutoGeneratedDefault("0")
            public Boolean getVal() {
                return super.getVal();
            }

            public void setVal(final Boolean val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Boolean.FALSE, val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultBoolean_1() {
        final Object obj = new AutoKeyAndVal<Boolean>() {
            @DynamoDBAutoGeneratedDefault("1")
            public Boolean getVal() {
                return super.getVal();
            }

            public void setVal(final Boolean val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Boolean.TRUE, val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultBoolean_y() {
        final Object obj = new AutoKeyAndVal<Boolean>() {
            @DynamoDBAutoGeneratedDefault("y")
            public Boolean getVal() {
                return super.getVal();
            }

            public void setVal(final Boolean val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Boolean.TRUE, val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultBoolean_Y() {
        final Object obj = new AutoKeyAndVal<Boolean>() {
            @DynamoDBAutoGeneratedDefault("Y")
            public Boolean getVal() {
                return super.getVal();
            }

            public void setVal(final Boolean val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Boolean.TRUE, val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultByte() {
        final Object obj = new AutoKeyAndVal<Byte>() {
            @DynamoDBAutoGeneratedDefault("1")
            public Byte getVal() {
                return super.getVal();
            }

            public void setVal(final Byte val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Byte.valueOf(((byte) (1))), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultCharacter() {
        final Object obj = new AutoKeyAndVal<Character>() {
            @DynamoDBAutoGeneratedDefault("A")
            public Character getVal() {
                return super.getVal();
            }

            public void setVal(final Character val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Character.valueOf('A'), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultCurrency() {
        final Object obj = new AutoKeyAndVal<Currency>() {
            @DynamoDBAutoGeneratedDefault("CAD")
            public Currency getVal() {
                return super.getVal();
            }

            public void setVal(final Currency val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Currency.getInstance("CAD"), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultDouble() {
        final Object obj = new AutoKeyAndVal<Double>() {
            @DynamoDBAutoGeneratedDefault("1234.5")
            public Double getVal() {
                return super.getVal();
            }

            public void setVal(final Double val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Double.valueOf(1234.5), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultEnum() {
        final Object obj = new AutoKeyAndVal<TimeUnit>() {
            @DynamoDBTypeConvertedEnum
            @DynamoDBAutoGeneratedDefault("SECONDS")
            public TimeUnit getVal() {
                return super.getVal();
            }

            public void setVal(final TimeUnit val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(TimeUnit.SECONDS, val.generate(null));
        Assert.assertEquals(TimeUnit.SECONDS, val.generate(TimeUnit.MILLISECONDS));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultFloat() {
        final Object obj = new AutoKeyAndVal<Float>() {
            @DynamoDBAutoGeneratedDefault("1234.5")
            public Float getVal() {
                return super.getVal();
            }

            public void setVal(final Float val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Float.valueOf(1234.5F), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultInteger() {
        final Object obj = new AutoKeyAndVal<Integer>() {
            @DynamoDBAutoGeneratedDefault("1234")
            public Integer getVal() {
                return super.getVal();
            }

            public void setVal(final Integer val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Integer.valueOf(((int) (1234))), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultLong() {
        final Object obj = new AutoKeyAndVal<Long>() {
            @DynamoDBAutoGeneratedDefault("1234")
            public Long getVal() {
                return super.getVal();
            }

            public void setVal(final Long val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Long.valueOf(((long) (1234))), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultShort() {
        final Object obj = new AutoKeyAndVal<Short>() {
            @DynamoDBAutoGeneratedDefault("1234")
            public Short getVal() {
                return super.getVal();
            }

            public void setVal(final Short val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(Short.valueOf(((short) (1234))), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultString() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBAutoGeneratedDefault("default-val")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals("default-val", val.generate(null));
        Assert.assertEquals("default-val", val.generate("not-default"));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultTimeZone() {
        final Object obj = new AutoKeyAndVal<TimeZone>() {
            @DynamoDBAutoGeneratedDefault("America/New_York")
            public TimeZone getVal() {
                return super.getVal();
            }

            public void setVal(final TimeZone val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(TimeZone.getTimeZone("America/New_York"), val.generate(null));
        Assert.assertEquals(TimeZone.getTimeZone("America/New_York"), val.generate(TimeZone.getTimeZone("America/Los_Angeles")));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testAutoGeneratedDefaultUuid() {
        final Object obj = new AutoKeyAndVal<UUID>() {
            @DynamoDBAutoGeneratedDefault("12345678-1234-1234-1234-123456789012")
            public UUID getVal() {
                return super.getVal();
            }

            public void setVal(final UUID val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("val");
        Assert.assertEquals(CREATE, val.getGenerateStrategy());
        Assert.assertEquals(UUID.fromString("12345678-1234-1234-1234-123456789012"), val.generate(null));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testIndexHashKeyGlobalSecondaryIndexName() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBIndexHashKey(attributeName = "gsi_hk", globalSecondaryIndexName = "gsi")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> gsi_hk = model.field("gsi_hk");
        StandardModelFactoriesTest.assertFieldGsiNames(Arrays.asList("gsi"), HASH, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldGsiNames(null, RANGE, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldLsiNames(null, gsi_hk, model);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testIndexHashKeyGlobalSecondaryIndexNames() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBIndexHashKey(attributeName = "gsi_hk", globalSecondaryIndexNames = "gsi")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> gsi_hk = model.field("gsi_hk");
        StandardModelFactoriesTest.assertFieldGsiNames(Arrays.asList("gsi"), HASH, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldGsiNames(null, RANGE, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldLsiNames(null, gsi_hk, model);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testIndexRangeKeyGlobalSecondaryIndexName() {
        final Object obj = new AutoKeyAndVal<String>() {
            private String gsi;

            @DynamoDBIndexHashKey(attributeName = "gsi_hk", globalSecondaryIndexName = "gsi")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }

            @DynamoDBIndexRangeKey(attributeName = "gsi_rk", globalSecondaryIndexName = "gsi")
            public String getGsi() {
                return this.gsi;
            }

            public void setGsi(final String gsi) {
                this.gsi = gsi;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> gsi_hk = model.field("gsi_hk");
        StandardModelFactoriesTest.assertFieldGsiNames(Arrays.asList("gsi"), HASH, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldGsiNames(null, RANGE, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldLsiNames(null, gsi_hk, model);
        final DynamoDBMapperFieldModel<Object, Object> gsi_rk = model.field("gsi_rk");
        StandardModelFactoriesTest.assertFieldGsiNames(null, HASH, gsi_rk, model);
        StandardModelFactoriesTest.assertFieldGsiNames(Arrays.asList("gsi"), RANGE, gsi_rk, model);
        StandardModelFactoriesTest.assertFieldLsiNames(null, gsi_rk, model);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testIndexRangeKeyGlobalSecondaryIndexNames() {
        final Object obj = new AutoKeyAndVal<String>() {
            private String gsi;

            @DynamoDBIndexHashKey(attributeName = "gsi_hk", globalSecondaryIndexName = "gsi")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }

            @DynamoDBIndexRangeKey(attributeName = "gsi_rk", globalSecondaryIndexNames = "gsi")
            public String getGsi() {
                return this.gsi;
            }

            public void setGsi(final String gsi) {
                this.gsi = gsi;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> gsi_hk = model.field("gsi_hk");
        StandardModelFactoriesTest.assertFieldGsiNames(Arrays.asList("gsi"), HASH, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldGsiNames(null, RANGE, gsi_hk, model);
        StandardModelFactoriesTest.assertFieldLsiNames(null, gsi_hk, model);
        final DynamoDBMapperFieldModel<Object, Object> gsi_rk = model.field("gsi_rk");
        StandardModelFactoriesTest.assertFieldGsiNames(null, HASH, gsi_rk, model);
        StandardModelFactoriesTest.assertFieldGsiNames(Arrays.asList("gsi"), RANGE, gsi_rk, model);
        StandardModelFactoriesTest.assertFieldLsiNames(null, gsi_rk, model);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testIndexRangeKeyiLocalSecondaryIndexName() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBIndexRangeKey(attributeName = "lsi_rk", localSecondaryIndexName = "lsi")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> lsi_rk = model.field("lsi_rk");
        StandardModelFactoriesTest.assertFieldLsiNames(Arrays.asList("lsi"), lsi_rk, model);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testIndexRangeKeyLocalSecondaryIndexNames() {
        final Object obj = new AutoKeyAndVal<String>() {
            @DynamoDBIndexRangeKey(attributeName = "lsi_rk", localSecondaryIndexNames = "lsi")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(final String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> lsi_rk = model.field("lsi_rk");
        StandardModelFactoriesTest.assertFieldLsiNames(Arrays.asList("lsi"), lsi_rk, model);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testFlattened() {
        final Object obj = new AutoKeyAndVal<DateRange>() {
            @DynamoDBFlattened(attributes = { @DynamoDBAttribute(mappedBy = "start", attributeName = "DateRangeStart"), @DynamoDBAttribute(mappedBy = "end", attributeName = "DateRangeEnd") })
            public DateRange getVal() {
                return super.getVal();
            }

            public void setVal(final DateRange val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        Assert.assertEquals(3, model.fields().size());
        Assert.assertEquals("DateRangeStart", model.field("DateRangeStart").name());
        Assert.assertEquals("DateRangeEnd", model.field("DateRangeEnd").name());
    }

    /**
     * Test mappings.
     */
    @Test
    public void testFlattenedNotAllSpecified() {
        final Object obj = new AutoKeyAndVal<DateRange>() {
            @DynamoDBFlattened(attributes = { @DynamoDBAttribute(mappedBy = "start", attributeName = "DateRangeStart") })
            public DateRange getVal() {
                return super.getVal();
            }

            public void setVal(final DateRange val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        Assert.assertEquals(2, model.fields().size());
        Assert.assertEquals("DateRangeStart", model.field("DateRangeStart").name());
    }

    /**
     * Test mappings.
     */
    @Test(expected = DynamoDBMappingException.class)
    public void testFlattenedInvalidMappedBy() {
        final Object obj = new AutoKeyAndVal<DateRange>() {
            @DynamoDBFlattened(attributes = { @DynamoDBAttribute(mappedBy = "xstart", attributeName = "DateRangeStart"), @DynamoDBAttribute(mappedBy = "xend", attributeName = "DateRangeEnd") })
            public DateRange getVal() {
                return super.getVal();
            }

            public void setVal(final DateRange val) {
                super.setVal(val);
            }
        };
        StandardModelFactoriesTest.getTable(obj);
    }

    /**
     * Test mappings.
     */
    @Test
    public void testFlattenedMultipleSameType() {
        final Object obj = new AutoKeyAndVal<com.amazonaws.services.dynamodbv2.pojos.Currency>() {
            private com.amazonaws.services.dynamodbv2.pojos.Currency other;

            @DynamoDBFlattened(attributes = { @DynamoDBAttribute(mappedBy = "amount", attributeName = "firstAmount"), @DynamoDBAttribute(mappedBy = "unit", attributeName = "firstUnit") })
            public com.amazonaws.services.dynamodbv2.pojos.Currency getVal() {
                return super.getVal();
            }

            public void setVal(final com.amazonaws.services.dynamodbv2.pojos.Currency val) {
                super.setVal(val);
            }

            @DynamoDBFlattened(attributes = { @DynamoDBAttribute(mappedBy = "amount", attributeName = "secondAmount"), @DynamoDBAttribute(mappedBy = "unit", attributeName = "secondUnit") })
            public com.amazonaws.services.dynamodbv2.pojos.Currency getOther() {
                return this.other;
            }

            public void setOther(final com.amazonaws.services.dynamodbv2.pojos.Currency other) {
                this.other = other;
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        Assert.assertEquals(5, model.fields().size());
        Assert.assertEquals("firstAmount", model.field("firstAmount").name());
        Assert.assertEquals("firstUnit", model.field("firstUnit").name());
        Assert.assertEquals("secondAmount", model.field("secondAmount").name());
        Assert.assertEquals("secondUnit", model.field("secondUnit").name());
    }

    /**
     * Test mappings.
     */
    @Test
    public void testTableAndDocument() {
        StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.TableAndDocument.class);
    }

    @DynamoDBDocument
    @DynamoDBTable(tableName = "")
    public static class TableAndDocument extends AutoKeyAndVal<String> {
        public String getVal() {
            return super.getVal();
        }

        public void setVal(final String val) {
            super.setVal(val);
        }
    }

    /**
     * Test mappings.
     */
    @Test
    public void testInheritedWithNoTable() {
        final Object obj = new KeyAndVal<String, String>() {
            @DynamoDBHashKey(attributeName = "hk")
            public String getKey() {
                return super.getKey();
            }

            public void setKey(String key) {
                super.setKey(key);
            }

            @DynamoDBAttribute(attributeName = "value")
            public String getVal() {
                return super.getVal();
            }

            public void setVal(String val) {
                super.setVal(val);
            }
        };
        final DynamoDBMapperTableModel<Object> model = StandardModelFactoriesTest.getTable(obj);
        final DynamoDBMapperFieldModel<Object, Object> key = model.field("hk");
        Assert.assertNotNull(key);
        Assert.assertEquals(HASH, key.keyType());
        Assert.assertEquals(S, key.attributeType());
        final DynamoDBMapperFieldModel<Object, Object> val = model.field("value");
        Assert.assertNotNull(val);
        Assert.assertEquals(S, val.attributeType());
    }

    /**
     * Test mappings to make sure the bridge method is ruled out.
     */
    @Test
    public void testFindRelevantGettersWithBridgeMethod() {
        final DynamoDBMapperTableModel<StandardModelFactoriesTest.SubClass> model = StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.SubClass.class);
        Assert.assertEquals("only two getter should be returned", 2, model.fields().size());
        Assert.assertEquals("return type should be Integer rather than Object", N, model.field("t").attributeType());
    }

    @DynamoDBTable(tableName = "")
    private abstract static class SuperGenericClass<T> {
        private String id;

        @DynamoDBHashKey
        public final String getId() {
            return this.id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        public abstract T getT();

        public abstract void setT(T t);
    }

    @DynamoDBTable(tableName = "GenericString")
    private static class SubClass extends StandardModelFactoriesTest.SuperGenericClass<Integer> {
        private Integer t;

        @Override
        public Integer getT() {
            return t;
        }

        @Override
        public void setT(Integer t) {
            this.t = t;
        }
    }

    /**
     * Test mappings.
     */
    @Test
    public void testNonMappedInheritedProperties() {
        final DynamoDBMapperTableModel<StandardModelFactoriesTest.NonMappedInheritedProperties> model = StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.NonMappedInheritedProperties.class);
        Assert.assertEquals(2, model.fields().size());
        Assert.assertNotNull(model.field("doUse"));
    }

    public abstract class AbstractNonMappedInheritedProperties {
        private String doNotUse;

        public String getDoNotUse() {
            return this.doNotUse;
        }

        public void setDoNotUse(final String doNotUse) {
            this.doNotUse = doNotUse;
        }
    }

    @DynamoDBTable(tableName = "aws-java-sdk-test")
    public class NonMappedInheritedProperties extends StandardModelFactoriesTest.AbstractNonMappedInheritedProperties {
        private String id;

        private String doUse;

        @DynamoDBHashKey
        public final String getId() {
            return this.id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        public String getDoUse() {
            return this.doUse;
        }

        public void setDoUse(final String doUse) {
            this.doUse = doUse;
        }
    }

    /**
     * Test mappings.
     */
    @Test
    public void testInheritedProperties() {
        final DynamoDBMapperTableModel<StandardModelFactoriesTest.BaseTablePojo> model1 = StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.BaseTablePojo.class);
        Assert.assertEquals(3, model1.fields().size());
        Assert.assertNotNull(model1.field("hashKeyOnField"));
        Assert.assertNotNull(model1.field("rangeKeyOnGetter"));
        final DynamoDBMapperTableModel<StandardModelFactoriesTest.TablePojoSubclass> model2 = StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.TablePojoSubclass.class);
        Assert.assertEquals(4, model2.fields().size());
        Assert.assertNotNull(model2.field("hashKeyOnField"));
        Assert.assertNotNull(model2.field("rangeKeyOnGetter"));
    }

    @DynamoDBTable(tableName = "table")
    private static class BaseTablePojo {
        @DynamoDBHashKey
        private String hashKeyOnField;

        private String rangeKeyOnGetter;

        private String attrNoAnnotation;

        @DynamoDBIgnore
        private String ignoredAttr;

        public String getHashKeyOnField() {
            return hashKeyOnField;
        }

        public void setHashKeyOnField(String hashKeyOnField) {
            this.hashKeyOnField = hashKeyOnField;
        }

        @DynamoDBRangeKey
        public String getRangeKeyOnGetter() {
            return rangeKeyOnGetter;
        }

        public void setRangeKeyOnGetter(String rangeKeyOnGetter) {
            this.rangeKeyOnGetter = rangeKeyOnGetter;
        }

        public String getAttrNoAnnotation() {
            return attrNoAnnotation;
        }

        public void setAttrNoAnnotation(String attrNoAnnotation) {
            this.attrNoAnnotation = attrNoAnnotation;
        }

        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }

    @DynamoDBTable(tableName = "table")
    private static class TablePojoSubclass extends StandardModelFactoriesTest.BaseTablePojo {
        private String ignoredAttr;

        @Override
        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        @Override
        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }

    /**
     * Test mappings.
     */
    @Test
    public void testPojoWithGetterAnnotations() {
        StandardModelFactoriesTest.PojoAsserts.assertAll(StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.PojoWithGetterAnnotations.class));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testPojoWithFieldAnnotations() {
        StandardModelFactoriesTest.PojoAsserts.assertAll(StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.PojoWithFieldAnnotations.class));
    }

    /**
     * Test mappings.
     */
    @Test
    public void testPojoWithMixedAnnotations() {
        StandardModelFactoriesTest.PojoAsserts.assertAll(StandardModelFactoriesTest.models.getTable(StandardModelFactoriesTest.PojoWithMixedAnnotations.class));
    }

    /**
     * Pojo field assersions.
     */
    private static enum PojoAsserts {

        hashKey(KeyType.HASH, null),
        rangeKey(KeyType.RANGE, DynamoDBAutoGenerateStrategy.CREATE),
        indexHashKey(null, null),
        indexRangeKey(null, null),
        actualAttrName(null, null),
        versionedAttr(null, ALWAYS),
        marshallingAttr(null, null);
        private final DynamoDBAutoGenerateStrategy generateStrategy;

        private final KeyType keyType;

        private PojoAsserts(final KeyType keyType, final DynamoDBAutoGenerateStrategy generateStrategy) {
            this.generateStrategy = generateStrategy;
            this.keyType = keyType;
        }

        public static <T> void assertAll(final DynamoDBMapperTableModel<T> model) {
            for (final StandardModelFactoriesTest.PojoAsserts asserts : StandardModelFactoriesTest.PojoAsserts.values()) {
                final DynamoDBMapperFieldModel<T, Object> field = model.field(asserts.name());
                Assert.assertNotNull(field);
                StandardModelFactoriesTest.assertFieldKeyType(asserts.keyType, field, model);
                Assert.assertEquals(asserts.generateStrategy, field.getGenerateStrategy());
                Assert.assertEquals(0, field.localSecondaryIndexNames().size());
            }
            Assert.assertEquals(StandardModelFactoriesTest.PojoAsserts.values().length, model.fields().size());
        }
    }

    /**
     * A POJO model that uses getter annotations.
     */
    @DynamoDBTable(tableName = "table")
    private static class PojoWithGetterAnnotations {
        private String hashKey;

        private String rangeKey;

        private String indexHashKey;

        private String indexRangeKey;

        private String annotatedAttr;

        private Long versionedAttr;

        private String marshallingAttr;

        private String ignoredAttr;

        @DynamoDBHashKey
        public String getHashKey() {
            return hashKey;
        }

        public void setHashKey(String hashKey) {
            this.hashKey = hashKey;
        }

        @DynamoDBRangeKey
        @DynamoDBAutoGeneratedKey
        public String getRangeKey() {
            return rangeKey;
        }

        public void setRangeKey(String rangeKey) {
            this.rangeKey = rangeKey;
        }

        @DynamoDBIndexHashKey(globalSecondaryIndexName = "index")
        public String getIndexHashKey() {
            return indexHashKey;
        }

        public void setIndexHashKey(String indexHashKey) {
            this.indexHashKey = indexHashKey;
        }

        @DynamoDBIndexRangeKey(globalSecondaryIndexName = "index")
        public String getIndexRangeKey() {
            return indexRangeKey;
        }

        public void setIndexRangeKey(String indexRangeKey) {
            this.indexRangeKey = indexRangeKey;
        }

        @DynamoDBAttribute(attributeName = "actualAttrName")
        public String getAnnotatedAttr() {
            return annotatedAttr;
        }

        public void setAnnotatedAttr(String annotatedAttr) {
            this.annotatedAttr = annotatedAttr;
        }

        @DynamoDBVersionAttribute
        public Long getVersionedAttr() {
            return versionedAttr;
        }

        public void setVersionedAttr(Long versionedAttr) {
            this.versionedAttr = versionedAttr;
        }

        @DynamoDBTypeConverted(converter = RandomUUIDMarshaller.class)
        public String getMarshallingAttr() {
            return marshallingAttr;
        }

        public void setMarshallingAttr(String marshallingAttr) {
            this.marshallingAttr = marshallingAttr;
        }

        @DynamoDBIgnore
        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }

    /**
     * The same model as defined in PojoWithGetterAnnotations, but uses field
     * annotations instead.
     */
    @DynamoDBTable(tableName = "table")
    private static class PojoWithFieldAnnotations {
        @DynamoDBHashKey
        private String hashKey;

        @DynamoDBRangeKey
        @DynamoDBAutoGeneratedKey
        private String rangeKey;

        @DynamoDBIndexHashKey(globalSecondaryIndexName = "index")
        private String indexHashKey;

        @DynamoDBIndexRangeKey(globalSecondaryIndexName = "index")
        private String indexRangeKey;

        @DynamoDBAttribute(attributeName = "actualAttrName")
        private String annotatedAttr;

        @DynamoDBVersionAttribute
        private Long versionedAttr;

        @DynamoDBTypeConverted(converter = RandomUUIDMarshaller.class)
        private String marshallingAttr;

        @DynamoDBIgnore
        private String ignoredAttr;

        public String getHashKey() {
            return hashKey;
        }

        public void setHashKey(String hashKey) {
            this.hashKey = hashKey;
        }

        public String getRangeKey() {
            return rangeKey;
        }

        public void setRangeKey(String rangeKey) {
            this.rangeKey = rangeKey;
        }

        public String getIndexHashKey() {
            return indexHashKey;
        }

        public void setIndexHashKey(String indexHashKey) {
            this.indexHashKey = indexHashKey;
        }

        public String getIndexRangeKey() {
            return indexRangeKey;
        }

        public void setIndexRangeKey(String indexRangeKey) {
            this.indexRangeKey = indexRangeKey;
        }

        public String getAnnotatedAttr() {
            return annotatedAttr;
        }

        public void setAnnotatedAttr(String annotatedAttr) {
            this.annotatedAttr = annotatedAttr;
        }

        public Long getVersionedAttr() {
            return versionedAttr;
        }

        public void setVersionedAttr(Long versionedAttr) {
            this.versionedAttr = versionedAttr;
        }

        public String getMarshallingAttr() {
            return marshallingAttr;
        }

        public void setMarshallingAttr(String marshallingAttr) {
            this.marshallingAttr = marshallingAttr;
        }

        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }

    /**
     * The same model as defined in PojoWithGetterAnnotations, but uses both getter and field
     * annotations.
     */
    @DynamoDBTable(tableName = "table")
    private static class PojoWithMixedAnnotations {
        @DynamoDBHashKey
        private String hashKey;

        private String rangeKey;

        @DynamoDBIndexHashKey(globalSecondaryIndexName = "index")
        private String indexHashKey;

        private String indexRangeKey;

        @DynamoDBAttribute(attributeName = "actualAttrName")
        private String annotatedAttr;

        private Long versionedAttr;

        @DynamoDBTypeConverted(converter = RandomUUIDMarshaller.class)
        private String marshallingAttr;

        private String ignoredAttr;

        public String getHashKey() {
            return hashKey;
        }

        public void setHashKey(String hashKey) {
            this.hashKey = hashKey;
        }

        @DynamoDBRangeKey
        @DynamoDBAutoGeneratedKey
        public String getRangeKey() {
            return rangeKey;
        }

        public void setRangeKey(String rangeKey) {
            this.rangeKey = rangeKey;
        }

        public String getIndexHashKey() {
            return indexHashKey;
        }

        public void setIndexHashKey(String indexHashKey) {
            this.indexHashKey = indexHashKey;
        }

        @DynamoDBIndexRangeKey(globalSecondaryIndexName = "index")
        public String getIndexRangeKey() {
            return indexRangeKey;
        }

        public void setIndexRangeKey(String indexRangeKey) {
            this.indexRangeKey = indexRangeKey;
        }

        public String getAnnotatedAttr() {
            return annotatedAttr;
        }

        public void setAnnotatedAttr(String annotatedAttr) {
            this.annotatedAttr = annotatedAttr;
        }

        @DynamoDBVersionAttribute
        public Long getVersionedAttr() {
            return versionedAttr;
        }

        public void setVersionedAttr(Long versionedAttr) {
            this.versionedAttr = versionedAttr;
        }

        public String getMarshallingAttr() {
            return marshallingAttr;
        }

        public void setMarshallingAttr(String marshallingAttr) {
            this.marshallingAttr = marshallingAttr;
        }

        @DynamoDBIgnore
        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }
}

