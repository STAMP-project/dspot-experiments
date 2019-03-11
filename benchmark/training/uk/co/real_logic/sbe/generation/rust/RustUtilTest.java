package uk.co.real_logic.sbe.generation.rust;


import PrimitiveType.CHAR;
import PrimitiveType.DOUBLE;
import PrimitiveType.FLOAT;
import PrimitiveType.INT16;
import PrimitiveType.INT32;
import PrimitiveType.INT64;
import PrimitiveType.INT8;
import PrimitiveType.UINT16;
import PrimitiveType.UINT32;
import PrimitiveType.UINT64;
import PrimitiveType.UINT8;
import org.junit.Assert;
import org.junit.Test;


public class RustUtilTest {
    @Test(expected = NullPointerException.class)
    public void nullParamToEightBitCharacterThrowsNPE() {
        RustUtil.eightBitCharacter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyParamToEightBitCharacterThrowsIAE() {
        RustUtil.eightBitCharacter("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyCharactersParamToEightBitCharacterThrowsIAE() {
        RustUtil.eightBitCharacter("ABC");
    }

    @Test
    public void happyPathEightBitCharacter() {
        final byte aByte = RustUtil.eightBitCharacter("a");
        Assert.assertEquals('a', ((char) (aByte)));
        Assert.assertEquals("97", Byte.toString(aByte));
    }

    @Test
    public void generateRustLiteralsHappyPaths() {
        Assert.assertEquals("65i8", RustUtil.generateRustLiteral(CHAR, "65"));
        Assert.assertEquals("64.1f64", RustUtil.generateRustLiteral(DOUBLE, "64.1"));
        Assert.assertEquals("f64::NAN", RustUtil.generateRustLiteral(DOUBLE, "NaN"));
        Assert.assertEquals("64.1f32", RustUtil.generateRustLiteral(FLOAT, "64.1"));
        Assert.assertEquals("f32::NAN", RustUtil.generateRustLiteral(FLOAT, "NaN"));
        Assert.assertEquals("65i8", RustUtil.generateRustLiteral(INT8, "65"));
        Assert.assertEquals("65i16", RustUtil.generateRustLiteral(INT16, "65"));
        Assert.assertEquals("65i32", RustUtil.generateRustLiteral(INT32, "65"));
        Assert.assertEquals("65i64", RustUtil.generateRustLiteral(INT64, "65"));
        Assert.assertEquals("65u8", RustUtil.generateRustLiteral(UINT8, "65"));
        Assert.assertEquals("65u16", RustUtil.generateRustLiteral(UINT16, "65"));
        Assert.assertEquals("65u32", RustUtil.generateRustLiteral(UINT32, "65"));
        Assert.assertEquals("65u64", RustUtil.generateRustLiteral(UINT64, "65"));
    }

    @Test(expected = NullPointerException.class)
    public void generateRustLiteralNullPrimitiveTypeParam() {
        RustUtil.generateRustLiteral(null, "65");
    }

    @Test(expected = NullPointerException.class)
    public void generateRustLiteralNullValueParam() {
        RustUtil.generateRustLiteral(INT8, null);
    }

    @Test
    public void methodNameCasing() {
        Assert.assertEquals("", RustUtil.formatMethodName(""));
        Assert.assertEquals("a", RustUtil.formatMethodName("a"));
        Assert.assertEquals("a", RustUtil.formatMethodName("A"));
        Assert.assertEquals("car", RustUtil.formatMethodName("Car"));
        Assert.assertEquals("car", RustUtil.formatMethodName("car"));
        Assert.assertEquals("decode_car", RustUtil.formatMethodName("DecodeCar"));
        Assert.assertEquals("decode_car", RustUtil.formatMethodName("decodeCar"));
        Assert.assertEquals("decode_car", RustUtil.formatMethodName("decode_car"));
        Assert.assertEquals("decode_car", RustUtil.formatMethodName("Decode_car"));
        Assert.assertEquals("decode_car", RustUtil.formatMethodName("decode_Car"));
        Assert.assertEquals("decode_car", RustUtil.formatMethodName("Decode_Car"));
    }
}

