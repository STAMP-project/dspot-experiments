package com.google.protobuf;


import Internal.UTF_8;
import com.google.protobuf.Utf8.Processor;
import com.google.protobuf.Utf8.SafeProcessor;
import com.google.protobuf.Utf8.UnsafeProcessor;
import java.util.logging.Logger;
import junit.framework.TestCase;


public class DecodeUtf8Test extends TestCase {
    private static Logger logger = Logger.getLogger(DecodeUtf8Test.class.getName());

    private static final Processor SAFE_PROCESSOR = new SafeProcessor();

    private static final Processor UNSAFE_PROCESSOR = new UnsafeProcessor();

    public void testRoundTripAllValidChars() throws Exception {
        for (int i = Character.MIN_CODE_POINT; i < (Character.MAX_CODE_POINT); i++) {
            if ((i < (Character.MIN_SURROGATE)) || (i > (Character.MAX_SURROGATE))) {
                String str = new String(Character.toChars(i));
                assertRoundTrips(str);
            }
        }
    }

    // Test all 1, 2, 3 invalid byte combinations. Valid ones would have been covered above.
    public void testOneByte() throws Exception {
        int valid = 0;
        for (int i = Byte.MIN_VALUE; i <= (Byte.MAX_VALUE); i++) {
            ByteString bs = ByteString.copyFrom(new byte[]{ ((byte) (i)) });
            if (!(bs.isValidUtf8())) {
                assertInvalid(bs.toByteArray());
            } else {
                valid++;
            }
        }
        TestCase.assertEquals(IsValidUtf8TestUtil.EXPECTED_ONE_BYTE_ROUNDTRIPPABLE_COUNT, valid);
    }

    public void testTwoBytes() throws Exception {
        int valid = 0;
        for (int i = Byte.MIN_VALUE; i <= (Byte.MAX_VALUE); i++) {
            for (int j = Byte.MIN_VALUE; j <= (Byte.MAX_VALUE); j++) {
                ByteString bs = ByteString.copyFrom(new byte[]{ ((byte) (i)), ((byte) (j)) });
                if (!(bs.isValidUtf8())) {
                    assertInvalid(bs.toByteArray());
                } else {
                    valid++;
                }
            }
        }
        TestCase.assertEquals(IsValidUtf8TestUtil.EXPECTED_TWO_BYTE_ROUNDTRIPPABLE_COUNT, valid);
    }

    public void testThreeBytes() throws Exception {
        // Travis' OOM killer doesn't like this test
        if ((System.getenv("TRAVIS")) == null) {
            int count = 0;
            int valid = 0;
            for (int i = Byte.MIN_VALUE; i <= (Byte.MAX_VALUE); i++) {
                for (int j = Byte.MIN_VALUE; j <= (Byte.MAX_VALUE); j++) {
                    for (int k = Byte.MIN_VALUE; k <= (Byte.MAX_VALUE); k++) {
                        byte[] bytes = new byte[]{ ((byte) (i)), ((byte) (j)), ((byte) (k)) };
                        ByteString bs = ByteString.copyFrom(bytes);
                        if (!(bs.isValidUtf8())) {
                            assertInvalid(bytes);
                        } else {
                            valid++;
                        }
                        count++;
                        if ((count % 1000000L) == 0) {
                            DecodeUtf8Test.logger.info((("Processed " + (count / 1000000L)) + " million characters"));
                        }
                    }
                }
            }
            TestCase.assertEquals(IsValidUtf8TestUtil.EXPECTED_THREE_BYTE_ROUNDTRIPPABLE_COUNT, valid);
        }
    }

    /**
     * Tests that round tripping of a sample of four byte permutations work.
     */
    public void testInvalid_4BytesSamples() throws Exception {
        // Bad trailing bytes
        assertInvalid(240, 164, 173, 127);
        assertInvalid(240, 164, 173, 192);
        // Special cases for byte2
        assertInvalid(240, 143, 173, 162);
        assertInvalid(244, 144, 173, 162);
    }

    public void testRealStrings() throws Exception {
        // English
        assertRoundTrips("The quick brown fox jumps over the lazy dog");
        // German
        assertRoundTrips("Quizdeltagerne spiste jordb\u00e6r med fl\u00f8de, mens cirkusklovnen");
        // Japanese
        assertRoundTrips("\u3044\u308d\u306f\u306b\u307b\u3078\u3068\u3061\u308a\u306c\u308b\u3092");
        // Hebrew
        assertRoundTrips(("\u05d3\u05d2 \u05e1\u05e7\u05e8\u05df \u05e9\u05d8 \u05d1\u05d9\u05dd " + (("\u05de\u05d0\u05d5\u05db\u05d6\u05d1 \u05d5\u05dc\u05e4\u05ea\u05e2" + " \u05de\u05e6\u05d0 \u05dc\u05d5 \u05d7\u05d1\u05e8\u05d4 ") + "\u05d0\u05d9\u05da \u05d4\u05e7\u05dc\u05d9\u05d8\u05d4")));
        // Thai
        assertRoundTrips((" \u0e08\u0e07\u0e1d\u0e48\u0e32\u0e1f\u0e31\u0e19\u0e1e\u0e31\u0e12" + "\u0e19\u0e32\u0e27\u0e34\u0e0a\u0e32\u0e01\u0e32\u0e23"));
        // Chinese
        assertRoundTrips("\u8fd4\u56de\u94fe\u4e2d\u7684\u4e0b\u4e00\u4e2a\u4ee3\u7406\u9879\u9009\u62e9\u5668");
        // Chinese with 4-byte chars
        assertRoundTrips(("\ud841\udf0e\ud841\udf31\ud841\udf79\ud843\udc53\ud843\udc78" + ("\ud843\udc96\ud843\udccf\ud843\udcd5\ud843\udd15\ud843\udd7c\ud843\udd7f" + "\ud843\ude0e\ud843\ude0f\ud843\ude77\ud843\ude9d\ud843\udea2")));
        // Mixed
        assertRoundTrips(("The quick brown \u3044\u308d\u306f\u306b\u307b\u3078\u8fd4\u56de\u94fe" + "\u4e2d\u7684\u4e0b\u4e00"));
    }

    public void testOverlong() throws Exception {
        assertInvalid(192, 175);
        assertInvalid(224, 128, 175);
        assertInvalid(240, 128, 128, 175);
        // Max overlong
        assertInvalid(193, 191);
        assertInvalid(224, 159, 191);
        assertInvalid(240, 143, 191, 191);
        // null overlong
        assertInvalid(192, 128);
        assertInvalid(224, 128, 128);
        assertInvalid(240, 128, 128, 128);
    }

    public void testIllegalCodepoints() throws Exception {
        // Single surrogate
        assertInvalid(237, 160, 128);
        assertInvalid(237, 173, 191);
        assertInvalid(237, 174, 128);
        assertInvalid(237, 175, 191);
        assertInvalid(237, 176, 128);
        assertInvalid(237, 190, 128);
        assertInvalid(237, 191, 191);
        // Paired surrogates
        assertInvalid(237, 160, 128, 237, 176, 128);
        assertInvalid(237, 160, 128, 237, 191, 191);
        assertInvalid(237, 173, 191, 237, 176, 128);
        assertInvalid(237, 173, 191, 237, 191, 191);
        assertInvalid(237, 174, 128, 237, 176, 128);
        assertInvalid(237, 174, 128, 237, 191, 191);
        assertInvalid(237, 175, 191, 237, 176, 128);
        assertInvalid(237, 175, 191, 237, 191, 191);
    }

    public void testBufferSlice() throws Exception {
        String str = "The quick brown fox jumps over the lazy dog";
        assertRoundTrips(str, 10, 4);
        assertRoundTrips(str, str.length(), 0);
    }

    public void testInvalidBufferSlice() throws Exception {
        byte[] bytes = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);
        assertInvalidSlice(bytes, ((bytes.length) - 3), 4);
        assertInvalidSlice(bytes, bytes.length, 1);
        assertInvalidSlice(bytes, ((bytes.length) + 1), 0);
        assertInvalidSlice(bytes, 0, ((bytes.length) + 1));
    }
}

