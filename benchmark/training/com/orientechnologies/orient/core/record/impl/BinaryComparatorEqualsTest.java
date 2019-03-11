package com.orientechnologies.orient.core.record.impl;


import OCaseInsensitiveCollate.NAME;
import OType.BINARY;
import OType.BOOLEAN;
import OType.BYTE;
import OType.DATETIME;
import OType.DECIMAL;
import OType.DOUBLE;
import OType.FLOAT;
import OType.INTEGER;
import OType.LINK;
import OType.LONG;
import OType.SHORT;
import OType.STRING;
import com.orientechnologies.orient.core.collate.OCaseInsensitiveCollate;
import com.orientechnologies.orient.core.collate.ODefaultCollate;
import com.orientechnologies.orient.core.config.OStorageConfiguration;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.OBinaryField;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class BinaryComparatorEqualsTest extends AbstractComparatorTest {
    @Test
    public void testInteger() {
        testEquals(INTEGER, 10);
    }

    @Test
    public void testLong() {
        testEquals(LONG, 10L);
    }

    @Test
    public void testShort() {
        testEquals(SHORT, ((short) (10)));
    }

    @Test
    public void testByte() {
        testEquals(BYTE, ((byte) (10)));
    }

    @Test
    public void testFloat() {
        testEquals(FLOAT, 10.0F);
    }

    @Test
    public void testDouble() {
        testEquals(DOUBLE, 10.0);
    }

    @Test
    public void testDatetime() throws ParseException {
        testEquals(DATETIME, 10L);
        final SimpleDateFormat format = new SimpleDateFormat(OStorageConfiguration.DEFAULT_DATETIME_FORMAT);
        String now1 = format.format(new Date());
        Date now = format.parse(now1);
        Assert.assertTrue(comparator.isEqual(field(DATETIME, now), field(STRING, format.format(now))));
        Assert.assertFalse(comparator.isEqual(field(DATETIME, new Date(((now.getTime()) + 1))), field(STRING, format.format(now))));
        Assert.assertFalse(comparator.isEqual(field(DATETIME, new Date(((now.getTime()) - 1))), field(STRING, format.format(now))));
    }

    @Test
    public void testBinary() throws ParseException {
        final byte[] b1 = new byte[]{ 0, 1, 2, 3 };
        final byte[] b2 = new byte[]{ 0, 1, 2, 4 };
        final byte[] b3 = new byte[]{ 1, 1, 2, 4 };
        Assert.assertTrue(comparator.isEqual(field(BINARY, b1), field(BINARY, b1)));
        Assert.assertFalse(comparator.isEqual(field(BINARY, b1), field(BINARY, b2)));
        Assert.assertFalse(comparator.isEqual(field(BINARY, b1), field(BINARY, b3)));
    }

    @Test
    public void testLinks() throws ParseException {
        Assert.assertTrue(comparator.isEqual(field(LINK, new ORecordId(1, 2)), field(LINK, new ORecordId(1, 2))));
        Assert.assertFalse(comparator.isEqual(field(LINK, new ORecordId(1, 2)), field(LINK, new ORecordId(2, 1))));
        Assert.assertFalse(comparator.isEqual(field(LINK, new ORecordId(1, 2)), field(LINK, new ORecordId(0, 2))));
        Assert.assertTrue(comparator.isEqual(field(LINK, new ORecordId(1, 2)), field(STRING, new ORecordId(1, 2).toString())));
        Assert.assertFalse(comparator.isEqual(field(LINK, new ORecordId(1, 2)), field(STRING, new ORecordId(0, 2).toString())));
    }

    @Test
    public void testString() {
        Assert.assertTrue(comparator.isEqual(field(STRING, "test"), field(STRING, "test")));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test2"), field(STRING, "test")));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test"), field(STRING, "test2")));
        Assert.assertFalse(comparator.isEqual(field(STRING, "t"), field(STRING, "te")));
        // DEF COLLATE
        Assert.assertTrue(comparator.isEqual(field(STRING, "test", new ODefaultCollate()), field(STRING, "test")));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test2", new ODefaultCollate()), field(STRING, "test")));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test", new ODefaultCollate()), field(STRING, "test2")));
        Assert.assertFalse(comparator.isEqual(field(STRING, "t", new ODefaultCollate()), field(STRING, "te")));
        Assert.assertTrue(comparator.isEqual(field(STRING, "test", new ODefaultCollate()), field(STRING, "test", new ODefaultCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test2", new ODefaultCollate()), field(STRING, "test", new ODefaultCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test", new ODefaultCollate()), field(STRING, "test2", new ODefaultCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "t", new ODefaultCollate()), field(STRING, "te", new ODefaultCollate())));
        Assert.assertTrue(comparator.isEqual(field(STRING, "test"), field(STRING, "test", new ODefaultCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test2"), field(STRING, "test", new ODefaultCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test"), field(STRING, "test2", new ODefaultCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "t"), field(STRING, "te", new ODefaultCollate())));
        // CASE INSENSITIVE COLLATE
        Assert.assertTrue(comparator.isEqual(field(STRING, "test"), field(STRING, "test", new OCaseInsensitiveCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test2"), field(STRING, "test", new OCaseInsensitiveCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test"), field(STRING, "test2", new OCaseInsensitiveCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "t"), field(STRING, "te", new OCaseInsensitiveCollate())));
        Assert.assertTrue(comparator.isEqual(field(STRING, "test"), field(STRING, "TEST", new OCaseInsensitiveCollate())));
        Assert.assertTrue(comparator.isEqual(field(STRING, "TEST"), field(STRING, "TEST", new OCaseInsensitiveCollate())));
        Assert.assertTrue(comparator.isEqual(field(STRING, "TE"), field(STRING, "te", new OCaseInsensitiveCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test2"), field(STRING, "TEST", new OCaseInsensitiveCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "test"), field(STRING, "TEST2", new OCaseInsensitiveCollate())));
        Assert.assertFalse(comparator.isEqual(field(STRING, "t"), field(STRING, "tE", new OCaseInsensitiveCollate())));
    }

    @Test
    public void testDecimal() {
        Assert.assertTrue(comparator.isEqual(field(DECIMAL, new BigDecimal(10)), field(DECIMAL, new BigDecimal(10))));
        Assert.assertFalse(comparator.isEqual(field(DECIMAL, new BigDecimal(10)), field(DECIMAL, new BigDecimal(11))));
        Assert.assertFalse(comparator.isEqual(field(DECIMAL, new BigDecimal(10)), field(DECIMAL, new BigDecimal(9))));
    }

    @Test
    public void testBoolean() {
        Assert.assertTrue(comparator.isEqual(field(BOOLEAN, true), field(BOOLEAN, true)));
        Assert.assertFalse(comparator.isEqual(field(BOOLEAN, true), field(BOOLEAN, false)));
        Assert.assertFalse(comparator.isEqual(field(BOOLEAN, false), field(BOOLEAN, true)));
        Assert.assertTrue(comparator.isEqual(field(BOOLEAN, true), field(STRING, "true")));
        Assert.assertTrue(comparator.isEqual(field(BOOLEAN, false), field(STRING, "false")));
        Assert.assertFalse(comparator.isEqual(field(BOOLEAN, false), field(STRING, "true")));
        Assert.assertFalse(comparator.isEqual(field(BOOLEAN, true), field(STRING, "false")));
    }

    @Test
    public void testBinaryFieldCopy() {
        final OBinaryField f = field(BYTE, 10, new OCaseInsensitiveCollate()).copy();
        Assert.assertEquals(f.type, BYTE);
        Assert.assertNotNull(f.bytes);
        Assert.assertEquals(f.collate.getName(), NAME);
    }

    @Test
    public void testBinaryComparable() {
        for (OType t : OType.values()) {
            switch (t) {
                case INTEGER :
                case LONG :
                case DATETIME :
                case SHORT :
                case STRING :
                case DOUBLE :
                case FLOAT :
                case BYTE :
                case BOOLEAN :
                case DATE :
                case BINARY :
                case LINK :
                case DECIMAL :
                    Assert.assertTrue(comparator.isBinaryComparable(t));
                    break;
                default :
                    Assert.assertFalse(comparator.isBinaryComparable(t));
            }
        }
    }
}

