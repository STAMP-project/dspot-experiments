package org.web3j.utils;


import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.exceptions.MessageDecodingException;
import org.web3j.exceptions.MessageEncodingException;


public class NumericTest {
    private static final byte[] HEX_RANGE_ARRAY = new byte[]{ Numeric.asByte(0, 1), Numeric.asByte(2, 3), Numeric.asByte(4, 5), Numeric.asByte(6, 7), Numeric.asByte(8, 9), Numeric.asByte(10, 11), Numeric.asByte(12, 13), Numeric.asByte(14, 15) };

    private static final String HEX_RANGE_STRING = "0x0123456789abcdef";

    @Test
    public void testQuantityEncodeLeadingZero() {
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefixSafe(BigInteger.valueOf(0L)), CoreMatchers.equalTo("0x00"));
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefixSafe(BigInteger.valueOf(1024L)), CoreMatchers.equalTo("0x400"));
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefixSafe(BigInteger.valueOf(Long.MAX_VALUE)), CoreMatchers.equalTo("0x7fffffffffffffff"));
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefixSafe(new BigInteger("204516877000845695339750056077105398031")), CoreMatchers.equalTo("0x99dc848b94efc27edfad28def049810f"));
    }

    @Test
    public void testQuantityDecode() {
        MatcherAssert.assertThat(Numeric.decodeQuantity("0x0"), CoreMatchers.equalTo(BigInteger.valueOf(0L)));
        MatcherAssert.assertThat(Numeric.decodeQuantity("0x400"), CoreMatchers.equalTo(BigInteger.valueOf(1024L)));
        MatcherAssert.assertThat(Numeric.decodeQuantity("0x0"), CoreMatchers.equalTo(BigInteger.valueOf(0L)));
        MatcherAssert.assertThat(Numeric.decodeQuantity("0x7fffffffffffffff"), CoreMatchers.equalTo(BigInteger.valueOf(Long.MAX_VALUE)));
        MatcherAssert.assertThat(Numeric.decodeQuantity("0x99dc848b94efc27edfad28def049810f"), CoreMatchers.equalTo(new BigInteger("204516877000845695339750056077105398031")));
    }

    @Test
    public void testQuantityDecodeLeadingZero() {
        MatcherAssert.assertThat(Numeric.decodeQuantity("0x0400"), CoreMatchers.equalTo(BigInteger.valueOf(1024L)));
        MatcherAssert.assertThat(Numeric.decodeQuantity("0x001"), CoreMatchers.equalTo(BigInteger.valueOf(1L)));
    }

    @Test(expected = MessageDecodingException.class)
    public void testQuantityDecodeMissingPrefix() {
        Numeric.decodeQuantity("ff");
    }

    @Test(expected = MessageDecodingException.class)
    public void testQuantityDecodeMissingValue() {
        Numeric.decodeQuantity("0x");
    }

    @Test
    public void testQuantityEncode() {
        MatcherAssert.assertThat(Numeric.encodeQuantity(BigInteger.valueOf(0)), CoreMatchers.is("0x0"));
        MatcherAssert.assertThat(Numeric.encodeQuantity(BigInteger.valueOf(1)), CoreMatchers.is("0x1"));
        MatcherAssert.assertThat(Numeric.encodeQuantity(BigInteger.valueOf(1024)), CoreMatchers.is("0x400"));
        MatcherAssert.assertThat(Numeric.encodeQuantity(BigInteger.valueOf(Long.MAX_VALUE)), CoreMatchers.is("0x7fffffffffffffff"));
        MatcherAssert.assertThat(Numeric.encodeQuantity(new BigInteger("204516877000845695339750056077105398031")), CoreMatchers.is("0x99dc848b94efc27edfad28def049810f"));
    }

    @Test(expected = MessageEncodingException.class)
    public void testQuantityEncodeNegative() {
        Numeric.encodeQuantity(BigInteger.valueOf((-1)));
    }

    @Test
    public void testCleanHexPrefix() {
        MatcherAssert.assertThat(Numeric.cleanHexPrefix(""), CoreMatchers.is(""));
        MatcherAssert.assertThat(Numeric.cleanHexPrefix("0123456789abcdef"), CoreMatchers.is("0123456789abcdef"));
        MatcherAssert.assertThat(Numeric.cleanHexPrefix("0x"), CoreMatchers.is(""));
        MatcherAssert.assertThat(Numeric.cleanHexPrefix("0x0123456789abcdef"), CoreMatchers.is("0123456789abcdef"));
    }

    @Test
    public void testPrependHexPrefix() {
        MatcherAssert.assertThat(Numeric.prependHexPrefix(""), CoreMatchers.is("0x"));
        MatcherAssert.assertThat(Numeric.prependHexPrefix("0x0123456789abcdef"), CoreMatchers.is("0x0123456789abcdef"));
        MatcherAssert.assertThat(Numeric.prependHexPrefix("0x"), CoreMatchers.is("0x"));
        MatcherAssert.assertThat(Numeric.prependHexPrefix("0123456789abcdef"), CoreMatchers.is("0x0123456789abcdef"));
    }

    @Test
    public void testToHexStringWithPrefix() {
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefix(BigInteger.TEN), CoreMatchers.is("0xa"));
    }

    @Test
    public void testToHexStringNoPrefix() {
        MatcherAssert.assertThat(Numeric.toHexStringNoPrefix(BigInteger.TEN), CoreMatchers.is("a"));
    }

    @Test
    public void testToBytesPadded() {
        MatcherAssert.assertThat(Numeric.toBytesPadded(BigInteger.TEN, 1), CoreMatchers.is(new byte[]{ 10 }));
        MatcherAssert.assertThat(Numeric.toBytesPadded(BigInteger.TEN, 8), CoreMatchers.is(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 10 }));
        MatcherAssert.assertThat(Numeric.toBytesPadded(BigInteger.valueOf(Integer.MAX_VALUE), 4), CoreMatchers.is(new byte[]{ 127, ((byte) (255)), ((byte) (255)), ((byte) (255)) }));
    }

    @Test(expected = RuntimeException.class)
    public void testToBytesPaddedInvalid() {
        Numeric.toBytesPadded(BigInteger.valueOf(Long.MAX_VALUE), 7);
    }

    @Test
    public void testHexStringToByteArray() {
        MatcherAssert.assertThat(Numeric.hexStringToByteArray(""), CoreMatchers.is(new byte[]{  }));
        MatcherAssert.assertThat(Numeric.hexStringToByteArray("0"), CoreMatchers.is(new byte[]{ 0 }));
        MatcherAssert.assertThat(Numeric.hexStringToByteArray("1"), CoreMatchers.is(new byte[]{ 1 }));
        MatcherAssert.assertThat(Numeric.hexStringToByteArray(NumericTest.HEX_RANGE_STRING), CoreMatchers.is(NumericTest.HEX_RANGE_ARRAY));
        MatcherAssert.assertThat(Numeric.hexStringToByteArray("0x123"), CoreMatchers.is(new byte[]{ 1, 35 }));
    }

    @Test
    public void testToHexString() {
        MatcherAssert.assertThat(Numeric.toHexString(new byte[]{  }), CoreMatchers.is("0x"));
        MatcherAssert.assertThat(Numeric.toHexString(new byte[]{ 1 }), CoreMatchers.is("0x01"));
        MatcherAssert.assertThat(Numeric.toHexString(NumericTest.HEX_RANGE_ARRAY), CoreMatchers.is(NumericTest.HEX_RANGE_STRING));
    }

    @Test
    public void testToHexStringNoPrefixZeroPadded() {
        MatcherAssert.assertThat(Numeric.toHexStringNoPrefixZeroPadded(BigInteger.ZERO, 5), CoreMatchers.is("00000"));
        MatcherAssert.assertThat(Numeric.toHexStringNoPrefixZeroPadded(new BigInteger("11c52b08330e05d731e38c856c1043288f7d9744", 16), 40), CoreMatchers.is("11c52b08330e05d731e38c856c1043288f7d9744"));
        MatcherAssert.assertThat(Numeric.toHexStringNoPrefixZeroPadded(new BigInteger("01c52b08330e05d731e38c856c1043288f7d9744", 16), 40), CoreMatchers.is("01c52b08330e05d731e38c856c1043288f7d9744"));
    }

    @Test
    public void testToHexStringWithPrefixZeroPadded() {
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefixZeroPadded(BigInteger.ZERO, 5), CoreMatchers.is("0x00000"));
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefixZeroPadded(new BigInteger("01c52b08330e05d731e38c856c1043288f7d9744", 16), 40), CoreMatchers.is("0x01c52b08330e05d731e38c856c1043288f7d9744"));
        MatcherAssert.assertThat(Numeric.toHexStringWithPrefixZeroPadded(new BigInteger("01c52b08330e05d731e38c856c1043288f7d9744", 16), 40), CoreMatchers.is("0x01c52b08330e05d731e38c856c1043288f7d9744"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToHexStringZeroPaddedNegative() {
        Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf((-1)), 20);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToHexStringZeroPaddedTooLargs() {
        Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf((-1)), 5);
    }

    @Test
    public void testIsIntegerValue() {
        Assert.assertTrue(Numeric.isIntegerValue(BigDecimal.ZERO));
        Assert.assertTrue(Numeric.isIntegerValue(BigDecimal.ZERO));
        Assert.assertTrue(Numeric.isIntegerValue(BigDecimal.valueOf(Long.MAX_VALUE)));
        Assert.assertTrue(Numeric.isIntegerValue(BigDecimal.valueOf(Long.MIN_VALUE)));
        Assert.assertTrue(Numeric.isIntegerValue(new BigDecimal("9999999999999999999999999999999999999999999999999999999999999999.0")));
        Assert.assertTrue(Numeric.isIntegerValue(new BigDecimal("-9999999999999999999999999999999999999999999999999999999999999999.0")));
        TestCase.assertFalse(Numeric.isIntegerValue(BigDecimal.valueOf(0.1)));
        TestCase.assertFalse(Numeric.isIntegerValue(BigDecimal.valueOf((-0.1))));
        TestCase.assertFalse(Numeric.isIntegerValue(BigDecimal.valueOf(1.1)));
        TestCase.assertFalse(Numeric.isIntegerValue(BigDecimal.valueOf((-1.1))));
    }

    @Test
    public void testHandleNPE() {
        TestCase.assertFalse(Numeric.containsHexPrefix(null));
        TestCase.assertFalse(Numeric.containsHexPrefix(""));
    }
}

