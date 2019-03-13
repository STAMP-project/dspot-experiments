package com.orientechnologies.orient.core.record.impl;


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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class BinaryComparatorCompareTest extends AbstractComparatorTest {
    @Test
    public void testInteger() {
        testCompareNumber(INTEGER, 10);
    }

    @Test
    public void testLong() {
        testCompareNumber(LONG, 10L);
    }

    @Test
    public void testShort() {
        testCompareNumber(SHORT, ((short) (10)));
    }

    @Test
    public void testByte() {
        testCompareNumber(BYTE, ((byte) (10)));
    }

    @Test
    public void testFloat() {
        testCompareNumber(FLOAT, 10.0F);
    }

    @Test
    public void testDouble() {
        testCompareNumber(DOUBLE, 10.0);
    }

    @Test
    public void testDatetime() throws ParseException {
        testCompareNumber(DATETIME, 10L);
        final SimpleDateFormat format = new SimpleDateFormat(OStorageConfiguration.DEFAULT_DATETIME_FORMAT);
        String now1 = format.format(new Date());
        Date now = format.parse(now1);
        Assert.assertEquals(comparator.compare(field(DATETIME, now), field(STRING, format.format(now))), 0);
        Assert.assertTrue(((comparator.compare(field(DATETIME, new Date(((now.getTime()) + 1))), field(STRING, format.format(now)))) > 0));
        Assert.assertTrue(((comparator.compare(field(DATETIME, new Date(((now.getTime()) - 1))), field(STRING, format.format(now)))) < 0));
    }

    @Test
    public void testBinary() throws ParseException {
        final byte[] b1 = new byte[]{ 0, 1, 2, 3 };
        final byte[] b2 = new byte[]{ 0, 1, 2, 4 };
        final byte[] b3 = new byte[]{ 1, 1, 2, 4 };
        Assert.assertTrue(((comparator.compare(field(BINARY, b1), field(BINARY, b1))) == 0));
        Assert.assertFalse(((comparator.compare(field(BINARY, b1), field(BINARY, b2))) > 1));
        Assert.assertFalse(((comparator.compare(field(BINARY, b1), field(BINARY, b3))) > 1));
    }

    @Test
    public void testLinks() throws ParseException {
        Assert.assertTrue(((comparator.compare(field(LINK, new ORecordId(1, 2)), field(LINK, new ORecordId(1, 2)))) == 0));
        Assert.assertTrue(((comparator.compare(field(LINK, new ORecordId(1, 2)), field(LINK, new ORecordId(2, 1)))) < 0));
        Assert.assertTrue(((comparator.compare(field(LINK, new ORecordId(1, 2)), field(LINK, new ORecordId(0, 2)))) > 0));
        Assert.assertTrue(((comparator.compare(field(LINK, new ORecordId(1, 2)), field(STRING, new ORecordId(1, 2).toString()))) == 0));
        Assert.assertTrue(((comparator.compare(field(LINK, new ORecordId(1, 2)), field(STRING, new ORecordId(0, 2).toString()))) > 0));
    }

    @Test
    public void testString() {
        Assert.assertEquals(comparator.compare(field(STRING, "test"), field(STRING, "test")), 0);
        Assert.assertTrue(((comparator.compare(field(STRING, "test2"), field(STRING, "test"))) > 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "test"), field(STRING, "test2"))) < 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "t"), field(STRING, "te"))) < 0));
        // DEF COLLATE
        Assert.assertEquals(comparator.compare(field(STRING, "test", new ODefaultCollate()), field(STRING, "test")), 0);
        Assert.assertTrue(((comparator.compare(field(STRING, "test2", new ODefaultCollate()), field(STRING, "test"))) > 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "test", new ODefaultCollate()), field(STRING, "test2"))) < 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "t", new ODefaultCollate()), field(STRING, "te"))) < 0));
        Assert.assertEquals(comparator.compare(field(STRING, "test", new ODefaultCollate()), field(STRING, "test", new ODefaultCollate())), 0);
        Assert.assertTrue(((comparator.compare(field(STRING, "test2", new ODefaultCollate()), field(STRING, "test", new ODefaultCollate()))) > 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "test", new ODefaultCollate()), field(STRING, "test2", new ODefaultCollate()))) < 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "t", new ODefaultCollate()), field(STRING, "te", new ODefaultCollate()))) < 0));
        Assert.assertEquals(comparator.compare(field(STRING, "test"), field(STRING, "test", new ODefaultCollate())), 0);
        Assert.assertTrue(((comparator.compare(field(STRING, "test2"), field(STRING, "test", new ODefaultCollate()))) > 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "test"), field(STRING, "test2", new ODefaultCollate()))) < 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "t"), field(STRING, "te", new ODefaultCollate()))) < 0));
        // CASE INSENSITIVE COLLATE
        Assert.assertEquals(comparator.compare(field(STRING, "test"), field(STRING, "test", new OCaseInsensitiveCollate())), 0);
        Assert.assertTrue(((comparator.compare(field(STRING, "test2"), field(STRING, "test", new OCaseInsensitiveCollate()))) > 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "test"), field(STRING, "test2", new OCaseInsensitiveCollate()))) < 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "t"), field(STRING, "te", new OCaseInsensitiveCollate()))) < 0));
        Assert.assertEquals(comparator.compare(field(STRING, "test"), field(STRING, "TEST", new OCaseInsensitiveCollate())), 0);
        Assert.assertEquals(comparator.compare(field(STRING, "TEST"), field(STRING, "TEST", new OCaseInsensitiveCollate())), 0);
        Assert.assertEquals(comparator.compare(field(STRING, "TE"), field(STRING, "te", new OCaseInsensitiveCollate())), 0);
        Assert.assertTrue(((comparator.compare(field(STRING, "test2"), field(STRING, "TEST", new OCaseInsensitiveCollate()))) > 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "test"), field(STRING, "TEST2", new OCaseInsensitiveCollate()))) < 0));
        Assert.assertTrue(((comparator.compare(field(STRING, "t"), field(STRING, "tE", new OCaseInsensitiveCollate()))) < 0));
    }

    @Test
    public void testDecimal() {
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(DECIMAL, new BigDecimal(10))), 0);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(DECIMAL, new BigDecimal(11))), (-1));
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(DECIMAL, new BigDecimal(9))), 1);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(SHORT, new Short(((short) (10))))), 0);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(SHORT, new Short(((short) (11))))), (-1));
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(SHORT, new Short(((short) (9))))), 1);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(INTEGER, new Integer(10))), 0);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(INTEGER, new Integer(11))), (-1));
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(INTEGER, new Integer(9))), 1);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(LONG, new Long(10))), 0);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(LONG, new Long(11))), (-1));
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(LONG, new Long(9))), 1);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(FLOAT, new Float(10))), 0);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(FLOAT, new Float(11))), (-1));
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(FLOAT, new Float(9))), 1);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(DOUBLE, new Double(10))), 0);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(DOUBLE, new Double(11))), (-1));
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(DOUBLE, new Double(9))), 1);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(BYTE, new Byte(((byte) (10))))), 0);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(BYTE, new Byte(((byte) (11))))), (-1));
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(BYTE, new Byte(((byte) (9))))), 1);
        Assert.assertEquals(comparator.compare(field(DECIMAL, new BigDecimal(10)), field(STRING, "10")), 0);
        Assert.assertTrue(((comparator.compare(field(DECIMAL, new BigDecimal(10)), field(STRING, "11"))) < 0));
        Assert.assertTrue(((comparator.compare(field(DECIMAL, new BigDecimal(10)), field(STRING, "9"))) < 0));
        Assert.assertTrue(((comparator.compare(field(DECIMAL, new BigDecimal(20)), field(STRING, "11"))) > 0));
    }

    @Test
    public void testBoolean() {
        Assert.assertEquals(comparator.compare(field(BOOLEAN, true), field(BOOLEAN, true)), 0);
        Assert.assertEquals(comparator.compare(field(BOOLEAN, true), field(BOOLEAN, false)), 1);
        Assert.assertEquals(comparator.compare(field(BOOLEAN, false), field(BOOLEAN, true)), (-1));
        Assert.assertTrue(((comparator.compare(field(BOOLEAN, true), field(STRING, "true"))) == 0));
        Assert.assertTrue(((comparator.compare(field(BOOLEAN, false), field(STRING, "false"))) == 0));
        Assert.assertTrue(((comparator.compare(field(BOOLEAN, false), field(STRING, "true"))) < 0));
        Assert.assertTrue(((comparator.compare(field(BOOLEAN, true), field(STRING, "false"))) > 0));
    }
}

