package brave.internal;


import org.junit.Test;


// code originally imported from zipkin.UtilTest
public class HexCodecTest {
    @Test
    public void lowerHexToUnsignedLong_downgrades128bitIdsByDroppingHighBits() {
        assertThat(HexCodec.lowerHexToUnsignedLong("463ac35c9f6413ad48485a3953bb6124")).isEqualTo(HexCodec.lowerHexToUnsignedLong("48485a3953bb6124"));
    }

    @Test
    public void lowerHexToUnsignedLongTest() {
        assertThat(HexCodec.lowerHexToUnsignedLong("ffffffffffffffff")).isEqualTo((-1));
        assertThat(HexCodec.lowerHexToUnsignedLong(Long.toHexString(Long.MAX_VALUE))).isEqualTo(Long.MAX_VALUE);
        try {
            HexCodec.lowerHexToUnsignedLong("0");// invalid

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong(Character.toString(((char) ('9' + 1))));// invalid

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong(Character.toString(((char) ('0' - 1))));// invalid

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong(Character.toString(((char) ('f' + 1))));// invalid

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong(Character.toString(((char) ('a' - 1))));// invalid

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong("fffffffffffffffffffffffffffffffff");// too long

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong("");// too short

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong("rs");// bad charset

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
    }

    @Test
    public void toLowerHex_minValue() {
        assertThat(HexCodec.toLowerHex(Long.MAX_VALUE)).isEqualTo("7fffffffffffffff");
    }

    @Test
    public void toLowerHex_midValue() {
        assertThat(HexCodec.toLowerHex(3405691582L)).isEqualTo("00000000cafebabe");
    }

    @Test
    public void toLowerHex_fixedLength() {
        assertThat(HexCodec.toLowerHex(0L)).isEqualTo("0000000000000000");
    }
}

