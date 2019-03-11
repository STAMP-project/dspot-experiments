package org.altbeacon.beacon;


import java.util.Arrays;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/* HOW TO SEE DEBUG LINES FROM YOUR UNIT TESTS:

1. set a line like this at the start of your test:
org.robolectric.shadows.ShadowLog.stream = System.err;
2. run the tests from the command line
3. Look at the test report file in your web browser, e.g.
file:///Users/dyoung/workspace/AndroidProximityLibrary/build/reports/tests/index.html
4. Expand the System.err section
 */
@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class IdentifierTest {
    @Test
    public void testEqualsNormalizationIgnoresCase() {
        Identifier identifier1 = Identifier.parse("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6");
        Identifier identifier2 = Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6");
        Assert.assertTrue("Identifiers of different case should match", identifier1.equals(identifier2));
    }

    @Test
    public void testToStringNormalizesCase() {
        Identifier identifier1 = Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6");
        Assert.assertEquals("Identifiers of different case should match", "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6", identifier1.toString());
    }

    @Test
    public void testToStringEqualsUuid() {
        Identifier identifier1 = Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6");
        Assert.assertEquals("uuidString of Identifier should match", "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6", identifier1.toUuidString());
    }

    @Test
    public void testToUuidEqualsToUuidString() {
        Identifier identifier1 = Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6");
        Assert.assertEquals("uuidString of Identifier should match", identifier1.toUuid().toString(), identifier1.toUuidString());
    }

    @Test
    public void testToByteArrayConvertsUuids() {
        Identifier identifier1 = Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6");
        byte[] bytes = identifier1.toByteArrayOfSpecifiedEndianness(true);
        Assert.assertEquals("byte array is correct length", bytes.length, 16);
        Assert.assertEquals("first byte of uuid converted properly", 47, ((bytes[0]) & 255));
        Assert.assertEquals("second byte of uuid converted properly", 35, ((bytes[1]) & 255));
        Assert.assertEquals("last byte of uuid converted properly", 166, ((bytes[15]) & 255));
    }

    @Test
    public void testToByteArrayConvertsUuidsAsLittleEndian() {
        Identifier identifier1 = Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6");
        byte[] bytes = identifier1.toByteArrayOfSpecifiedEndianness(false);
        Assert.assertEquals("byte array is correct length", bytes.length, 16);
        Assert.assertEquals("first byte of uuid converted properly", 166, ((bytes[0]) & 255));
        Assert.assertEquals("last byte of uuid converted properly", 47, ((bytes[15]) & 255));
    }

    @Test
    public void testToByteArrayConvertsHex() {
        Identifier identifier1 = Identifier.parse("0x010203040506");
        byte[] bytes = identifier1.toByteArrayOfSpecifiedEndianness(true);
        Assert.assertEquals("byte array is correct length", bytes.length, 6);
        Assert.assertEquals("first byte of hex is converted properly", 1, ((bytes[0]) & 255));
        Assert.assertEquals("last byte of hex is converted properly", 6, ((bytes[5]) & 255));
    }

    @Test
    public void testToByteArrayConvertsDecimal() {
        Identifier identifier1 = Identifier.parse("65534");
        byte[] bytes = identifier1.toByteArrayOfSpecifiedEndianness(true);
        Assert.assertEquals("byte array is correct length", bytes.length, 2);
        Assert.assertEquals("reported byte array is correct length", identifier1.getByteCount(), 2);
        Assert.assertEquals("first byte of decimal converted properly", 255, ((bytes[0]) & 255));
        Assert.assertEquals("last byte of decimal converted properly", 254, ((bytes[1]) & 255));
    }

    @Test
    public void testToByteArrayConvertsInt() {
        Identifier identifier1 = Identifier.fromInt(65534);
        byte[] bytes = identifier1.toByteArrayOfSpecifiedEndianness(true);
        Assert.assertEquals("byte array is correct length", bytes.length, 2);
        Assert.assertEquals("reported byte array is correct length", identifier1.getByteCount(), 2);
        Assert.assertEquals("conversion back equals original value", identifier1.toInt(), 65534);
        Assert.assertEquals("first byte of decimal converted properly", 255, ((bytes[0]) & 255));
        Assert.assertEquals("last byte of decimal converted properly", 254, ((bytes[1]) & 255));
    }

    @Test
    public void testToByteArrayFromByteArray() {
        byte[] value = new byte[]{ ((byte) (255)), ((byte) (171)), 18, 37 };
        Identifier identifier1 = Identifier.fromBytes(value, 0, value.length, false);
        byte[] bytes = identifier1.toByteArrayOfSpecifiedEndianness(true);
        Assert.assertEquals("byte array is correct length", bytes.length, 4);
        Assert.assertEquals("correct string representation", identifier1.toString(), "0xffab1225");
        Assert.assertTrue("arrays equal", Arrays.equals(value, bytes));
        Assert.assertNotSame("arrays are copied", bytes, value);
    }

    @Test
    public void testComparableDifferentLength() {
        byte[] value1 = new byte[]{ ((byte) (255)), ((byte) (171)), 18, 37 };
        Identifier identifier1 = Identifier.fromBytes(value1, 0, value1.length, false);
        byte[] value2 = new byte[]{ ((byte) (255)), ((byte) (171)), 18, 37, 17, 17 };
        Identifier identifier2 = Identifier.fromBytes(value2, 0, value2.length, false);
        Assert.assertEquals("identifier1 is smaller than identifier2", identifier1.compareTo(identifier2), (-1));
        Assert.assertEquals("identifier2 is larger than identifier1", identifier2.compareTo(identifier1), 1);
    }

    @Test
    public void testComparableSameLength() {
        byte[] value1 = new byte[]{ ((byte) (255)), ((byte) (171)), 18, 37, 34, 37 };
        Identifier identifier1 = Identifier.fromBytes(value1, 0, value1.length, false);
        byte[] value2 = new byte[]{ ((byte) (255)), ((byte) (171)), 18, 37, 17, 17 };
        Identifier identifier2 = Identifier.fromBytes(value2, 0, value2.length, false);
        Assert.assertEquals("identifier1 is equal to identifier2", identifier1.compareTo(identifier1), 0);
        Assert.assertEquals("identifier1 is larger than identifier2", identifier1.compareTo(identifier2), 1);
        Assert.assertEquals("identifier2 is smaller than identifier1", identifier2.compareTo(identifier1), (-1));
    }

    @Test
    public void testParseIntegerMaxInclusive() {
        Identifier.parse("65535");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIntegerAboveMax() {
        Identifier.parse("65536");
    }

    @Test
    public void testParseIntegerMinInclusive() {
        Identifier.parse("0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIntegerBelowMin() {
        Identifier.parse("-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIntegerWayTooBig() {
        Identifier.parse("3133742");
    }

    /* This is here because Identifier.parse wrongly accepts UUIDs without
    dashes, but we want to be backward compatible.
     */
    @Test
    public void testParseInvalidUuid() {
        UUID ref = UUID.fromString("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6");
        Identifier id = Identifier.parse("2f234454cf6d4a0fadf2f4911ba9ffa6");
        Assert.assertEquals("Malformed UUID was parsed as expected.", id.toUuid(), ref);
    }

    @Test
    public void testParseHexWithNoPrefix() {
        Identifier id = Identifier.parse("abcd");
        Assert.assertEquals("Should parse and get back equivalent decimal value for small numbers", "43981", id.toString());
    }

    @Test
    public void testParseBigHexWithNoPrefix() {
        Identifier id = Identifier.parse("123456789abcdef");
        Assert.assertEquals("Should parse and get prefixed hex value for big numbers", "0x0123456789abcdef", id.toString());
    }

    @Test
    public void testParseZeroPrefixedDecimalNumberAsHex() {
        Identifier id = Identifier.parse("0010");
        Assert.assertEquals("Should be treated as hex in parse, but converted back to decimal because it is small", "16", id.toString());
    }

    @Test
    public void testParseNonZeroPrefixedDecimalNumberAsDecimal() {
        Identifier id = Identifier.parse("10");
        Assert.assertEquals("Should be treated as decimal", "10", id.toString());
    }

    @Test
    public void testParseDecimalNumberWithSpecifiedLength() {
        Identifier id = Identifier.parse("10", 8);
        Assert.assertEquals("Should be treated as hex because it is long", "0x000000000000000a", id.toString());
        Assert.assertEquals("Byte count should be as specified", 8, id.getByteCount());
    }

    @Test
    public void testParseDecimalNumberWithSpecifiedShortLength() {
        Identifier id = Identifier.parse("10", 2);
        Assert.assertEquals("Should be treated as decimal because it is short", "10", id.toString());
        Assert.assertEquals("Byte count should be as specified", 2, id.getByteCount());
    }

    @Test
    public void testParseHexNumberWithSpecifiedLength() {
        Identifier id = Identifier.parse("2fffffffffffffffffff", 10);
        Assert.assertEquals("Should be treated as hex because it is long", "0x2fffffffffffffffffff", id.toString());
        Assert.assertEquals("Byte count should be as specified", 10, id.getByteCount());
    }

    @Test
    public void testParseZeroAsInteger() {
        Identifier id = Identifier.parse("0");
        Assert.assertEquals("Should be treated as int because it is a common integer", "0", id.toString());
        Assert.assertEquals("Byte count should be 2 for integers", 2, id.getByteCount());
    }
}

