package com.jsoniter;


import com.jsoniter.spi.JsonException;
import java.io.IOException;
import java.math.BigInteger;
import junit.framework.TestCase;


// import java.math.BigDecimal;
// import java.math.BigInteger;
public class TestInteger extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    private boolean isStreaming;

    public void test_char() throws IOException {
        Character c = JsonIterator.deserialize("50", Character.class);
        TestCase.assertEquals(50, ((int) (c)));
    }

    public void test_positive_negative_int() throws IOException {
        TestCase.assertEquals(0, parseInt("0"));
        TestCase.assertEquals(4321, parseInt("4321"));
        TestCase.assertEquals(54321, parseInt("54321"));
        TestCase.assertEquals(654321, parseInt("654321"));
        TestCase.assertEquals(7654321, parseInt("7654321"));
        TestCase.assertEquals(87654321, parseInt("87654321"));
        TestCase.assertEquals(987654321, parseInt("987654321"));
        TestCase.assertEquals(2147483647, parseInt("2147483647"));
        TestCase.assertEquals((-4321), parseInt("-4321"));
        TestCase.assertEquals(-2147483648, parseInt("-2147483648"));
    }

    public void test_positive_negative_long() throws IOException {
        TestCase.assertEquals(0L, parseLong("0"));
        TestCase.assertEquals(4321L, parseLong("4321"));
        TestCase.assertEquals(54321L, parseLong("54321"));
        TestCase.assertEquals(654321L, parseLong("654321"));
        TestCase.assertEquals(7654321L, parseLong("7654321"));
        TestCase.assertEquals(87654321L, parseLong("87654321"));
        TestCase.assertEquals(987654321L, parseLong("987654321"));
        TestCase.assertEquals(9223372036854775807L, parseLong("9223372036854775807"));
        TestCase.assertEquals((-4321L), parseLong("-4321"));
        TestCase.assertEquals(-9223372036854775808L, parseLong("-9223372036854775808"));
    }

    public void test_max_min_int() throws IOException {
        TestCase.assertEquals(Integer.MAX_VALUE, parseInt(Integer.toString(Integer.MAX_VALUE)));
        TestCase.assertEquals(((Integer.MAX_VALUE) - 1), parseInt(Integer.toString(((Integer.MAX_VALUE) - 1))));
        TestCase.assertEquals(((Integer.MIN_VALUE) + 1), parseInt(Integer.toString(((Integer.MIN_VALUE) + 1))));
        TestCase.assertEquals(Integer.MIN_VALUE, parseInt(Integer.toString(Integer.MIN_VALUE)));
    }

    public void test_max_min_long() throws IOException {
        TestCase.assertEquals(Long.MAX_VALUE, parseLong(Long.toString(Long.MAX_VALUE)));
        TestCase.assertEquals(((Long.MAX_VALUE) - 1), parseLong(Long.toString(((Long.MAX_VALUE) - 1))));
        TestCase.assertEquals(((Long.MIN_VALUE) + 1), parseLong(Long.toString(((Long.MIN_VALUE) + 1))));
        TestCase.assertEquals(Long.MIN_VALUE, parseLong(Long.toString(Long.MIN_VALUE)));
    }

    public void test_large_number() throws IOException {
        try {
            JsonIterator.deserialize("2147483648", Integer.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
        for (int i = 300000000; i < 2000000000; i += 10000000) {
            try {
                JsonIterator.deserialize((i + "0"), Integer.class);
                TestCase.fail();
            } catch (JsonException e) {
            }
            try {
                JsonIterator.deserialize(((-i) + "0"), Integer.class);
                TestCase.fail();
            } catch (JsonException e) {
            }
        }
        try {
            JsonIterator.deserialize("9223372036854775808", Long.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
        for (long i = 1000000000000000000L; i < 9000000000000000000L; i += 100000000000000000L) {
            try {
                JsonIterator.deserialize((i + "0"), Long.class);
                TestCase.fail();
            } catch (JsonException e) {
            }
            try {
                JsonIterator.deserialize(((-i) + "0"), Long.class);
                TestCase.fail();
            } catch (JsonException e) {
            }
        }
    }

    public void test_byte() throws IOException {
        Byte val = JsonIterator.deserialize("120", Byte.class);
        TestCase.assertEquals(Byte.valueOf(((byte) (120))), val);
        byte[] vals = JsonIterator.deserialize("[120]", byte[].class);
        TestCase.assertEquals(((byte) (120)), vals[0]);
    }

    public void test_leading_zero() throws IOException {
        TestCase.assertEquals(Integer.valueOf(0), JsonIterator.deserialize("0", int.class));
        TestCase.assertEquals(Long.valueOf(0), JsonIterator.deserialize("0", long.class));
        try {
            JsonIterator.deserialize("01", int.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
        try {
            JsonIterator.deserialize("02147483647", int.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
        try {
            JsonIterator.deserialize("01", long.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
        try {
            JsonIterator.deserialize("09223372036854775807", long.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
        /* FIXME if we should fail on parsing of leading zeroes for other numbers
        try {
        JsonIterator.deserialize("01", double.class);
        fail();
        } catch (JsonException e) {
        }
        try {
        JsonIterator.deserialize("01", float.class);
        fail();
        } catch (JsonException e) {
        }
        try {
        JsonIterator.deserialize("01", BigInteger.class);
        fail();
        } catch (JsonException e) {
        }
        try {
        JsonIterator.deserialize("01", BigDecimal.class);
        fail();
        } catch (JsonException e) {
        }
         */
    }

    public void test_max_int() throws IOException {
        int[] ints = JsonIterator.deserialize("[2147483647,-2147483648]", int[].class);
        TestCase.assertEquals(Integer.MAX_VALUE, ints[0]);
        TestCase.assertEquals(Integer.MIN_VALUE, ints[1]);
    }

    public void testBigInteger() {
        BigInteger number = JsonIterator.deserialize("100", BigInteger.class);
        TestCase.assertEquals(new BigInteger("100"), number);
    }

    public void testChooseInteger() {
        Object number = JsonIterator.deserialize("100", Object.class);
        TestCase.assertEquals(100, number);
    }

    public void testChooseLong() {
        Object number = JsonIterator.deserialize(Long.valueOf(Long.MAX_VALUE).toString(), Object.class);
        TestCase.assertEquals(Long.MAX_VALUE, number);
    }
}

