package org.web3j.abi;


import java.math.BigInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Bytes6;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;


public class TypeDecoderTest {
    @Test
    public void testBoolDecode() {
        Assert.assertThat(TypeDecoder.decodeBool("0000000000000000000000000000000000000000000000000000000000000000", 0), CoreMatchers.is(new Bool(false)));
        Assert.assertThat(TypeDecoder.decodeBool("0000000000000000000000000000000000000000000000000000000000000001", 0), CoreMatchers.is(new Bool(true)));
    }

    @Test
    public void testBoolDecodeGivenOffset() {
        // Decode second parameter as Bool
        Assert.assertThat(TypeDecoder.decode(("0000000000000000000000000000000000000000000000007fffffffffffffff" + ("0000000000000000000000000000000000000000000000000000000000000000" + "0000000000000000000000000000000000000000000000007fffffffffffffff")), 64, Bool.class), CoreMatchers.is(new Bool(false)));
        Assert.assertThat(TypeDecoder.decode(("0000000000000000000000000000000000000000000000007fffffffffffffff" + ("0000000000000000000000000000000000000000000000000000000000000001" + "0000000000000000000000000000000000000000000000007fffffffffffffff")), 64, Bool.class), CoreMatchers.is(new Bool(true)));
    }

    @Test
    public void testUintDecode() {
        Assert.assertThat(TypeDecoder.decodeNumeric("0000000000000000000000000000000000000000000000000000000000000000", Uint64.class), CoreMatchers.is(new Uint64(BigInteger.ZERO)));
        Assert.assertThat(TypeDecoder.decodeNumeric("0000000000000000000000000000000000000000000000007fffffffffffffff", Uint64.class), CoreMatchers.is(new Uint64(BigInteger.valueOf(Long.MAX_VALUE))));
        Assert.assertThat(TypeDecoder.decodeNumeric("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", Uint64.class), CoreMatchers.is(new Uint64(new BigInteger("0ffffffffffffffff", 16))));
    }

    @Test
    public void testIntDecode() {
        Assert.assertThat(TypeDecoder.decodeNumeric("0000000000000000000000000000000000000000000000000000000000000000", Int64.class), CoreMatchers.is(new Int64(BigInteger.ZERO)));
        Assert.assertThat(TypeDecoder.decodeNumeric("0000000000000000000000000000000000000000000000007fffffffffffffff", Int64.class), CoreMatchers.is(new Int64(BigInteger.valueOf(Long.MAX_VALUE))));
        Assert.assertThat(TypeDecoder.decodeNumeric("fffffffffffffffffffffffffffffffffffffffffffffff88000000000000000", Int64.class), CoreMatchers.is(new Int64(BigInteger.valueOf(Long.MIN_VALUE))));
        Assert.assertThat(TypeDecoder.decodeNumeric("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", Int64.class), CoreMatchers.is(new Int64(BigInteger.valueOf((-1)))));
        Assert.assertThat(TypeDecoder.decodeNumeric("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", Int256.class), CoreMatchers.is(new Int256(BigInteger.valueOf((-1)))));
    }

    /* TODO: Enable once Solidity supports fixed types - see
    https://github.com/ethereum/solidity/issues/409

    @Test
    public void testUfixedDecode() {
    assertThat(TypeDecoder.decodeNumeric(
    "0000000000000000000000000000000000000000000000000000000000000000",
    Ufixed24x40.class),
    is(new Ufixed24x40(BigInteger.ZERO)));

    assertThat(TypeDecoder.decodeNumeric(
    "0000000000000000000000000000000000000000000000007fffffffffffffff",
    Ufixed24x40.class),
    is(new Ufixed24x40(BigInteger.valueOf(Long.MAX_VALUE))));

    assertThat(TypeDecoder.decodeNumeric(
    "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
    Ufixed.class),
    is(new Ufixed(
    new BigInteger(
    "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
    16
    ))));
    }

    @Test
    public void testFixedDecode() {
    assertThat(TypeDecoder.decodeNumeric(
    "0000000000000000000000000000000000000000000000000000000000000000",
    Fixed24x40.class),
    is(new Fixed24x40(BigInteger.ZERO)));

    assertThat(TypeDecoder.decodeNumeric(
    "0000000000000000000000000000000000000000000000007fffffffffffffff",
    Fixed24x40.class),
    is(new Fixed24x40(BigInteger.valueOf(Long.MAX_VALUE))));

    assertThat(TypeDecoder.decodeNumeric(
    "ffffffffffffffffffffffffffffffffffffffffffffffff8000000000000000",
    Fixed24x40.class),
    is(new Fixed24x40(BigInteger.valueOf(Long.MIN_VALUE))));

    assertThat(TypeDecoder.decodeNumeric(
    "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
    Fixed.class),
    is(new Fixed(BigInteger.valueOf(-1))));
    }
     */
    @Test
    public void testStaticBytes() {
        Bytes6 staticBytes = new Bytes6(new byte[]{ 0, 1, 2, 3, 4, 5 });
        Assert.assertThat(TypeDecoder.decodeBytes("0001020304050000000000000000000000000000000000000000000000000000", Bytes6.class), CoreMatchers.is(staticBytes));
        Bytes empty = new Bytes1(new byte[]{ 0 });
        Assert.assertThat(TypeDecoder.decodeBytes("0000000000000000000000000000000000000000000000000000000000000000", Bytes1.class), CoreMatchers.is(empty));
        Bytes dave = new Bytes4("dave".getBytes());
        Assert.assertThat(TypeDecoder.decodeBytes("6461766500000000000000000000000000000000000000000000000000000000", Bytes4.class), CoreMatchers.is(dave));
    }

    @Test
    public void testDynamicBytes() {
        DynamicBytes dynamicBytes = new DynamicBytes(new byte[]{ 0, 1, 2, 3, 4, 5 });
        Assert.assertThat(TypeDecoder.decodeDynamicBytes(("0000000000000000000000000000000000000000000000000000000000000006"// length
         + "0001020304050000000000000000000000000000000000000000000000000000"), 0), CoreMatchers.is(dynamicBytes));
        DynamicBytes empty = new DynamicBytes(new byte[]{ 0 });
        Assert.assertThat(TypeDecoder.decodeDynamicBytes(("0000000000000000000000000000000000000000000000000000000000000001" + "0000000000000000000000000000000000000000000000000000000000000000"), 0), CoreMatchers.is(empty));
        DynamicBytes dave = new DynamicBytes("dave".getBytes());
        Assert.assertThat(TypeDecoder.decodeDynamicBytes(("0000000000000000000000000000000000000000000000000000000000000004" + "6461766500000000000000000000000000000000000000000000000000000000"), 0), CoreMatchers.is(dave));
        DynamicBytes loremIpsum = new DynamicBytes(("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " + ((((("tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim " + "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ") + "ea commodo consequat. Duis aute irure dolor in reprehenderit in ") + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur ") + "sint occaecat cupidatat non proident, sunt in culpa qui officia ") + "deserunt mollit anim id est laborum.")).getBytes());
        Assert.assertThat(TypeDecoder.decodeDynamicBytes(("00000000000000000000000000000000000000000000000000000000000001bd" + ((((((((((((("4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73" + "656374657475722061646970697363696e6720656c69742c2073656420646f20") + "656975736d6f642074656d706f7220696e6369646964756e74207574206c6162") + "6f726520657420646f6c6f7265206d61676e6120616c697175612e2055742065") + "6e696d206164206d696e696d2076656e69616d2c2071756973206e6f73747275") + "6420657865726369746174696f6e20756c6c616d636f206c61626f726973206e") + "69736920757420616c697175697020657820656120636f6d6d6f646f20636f6e") + "7365717561742e2044756973206175746520697275726520646f6c6f7220696e") + "20726570726568656e646572697420696e20766f6c7570746174652076656c69") + "7420657373652063696c6c756d20646f6c6f726520657520667567696174206e") + "756c6c612070617269617475722e204578636570746575722073696e74206f63") + "63616563617420637570696461746174206e6f6e2070726f6964656e742c2073") + "756e7420696e2063756c706120717569206f666669636961206465736572756e") + "74206d6f6c6c697420616e696d20696420657374206c61626f72756d2e000000")), 0), CoreMatchers.is(loremIpsum));
    }

    @Test
    public void testAddress() {
        Assert.assertThat(TypeDecoder.decodeAddress("000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338"), CoreMatchers.is(new Address("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338")));
    }

    @Test
    public void testUtf8String() {
        Assert.assertThat(TypeDecoder.decodeUtf8String(("000000000000000000000000000000000000000000000000000000000000000d"// length
         + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000"), 0), CoreMatchers.is(new Utf8String("Hello, world!")));
    }

    @Test
    public void testStaticArray() {
        Assert.assertThat(TypeDecoder.decodeStaticArray(("000000000000000000000000000000000000000000000000000000000000000a" + "0000000000000000000000000000000000000000000000007fffffffffffffff"), 0, new TypeReference.StaticArrayTypeReference<org.web3j.abi.datatypes.StaticArray<Uint256>>(2) {}, 2), CoreMatchers.is(new org.web3j.abi.datatypes.StaticArray<Uint256>(new Uint256(BigInteger.TEN), new Uint256(BigInteger.valueOf(Long.MAX_VALUE)))));
        Assert.assertThat(TypeDecoder.decodeStaticArray(("000000000000000000000000000000000000000000000000000000000000000d" + (("48656c6c6f2c20776f726c642100000000000000000000000000000000000000" + "000000000000000000000000000000000000000000000000000000000000000d") + "776f726c64212048656c6c6f2c00000000000000000000000000000000000000")), 0, new TypeReference.StaticArrayTypeReference<org.web3j.abi.datatypes.StaticArray<Utf8String>>(2) {}, 2), IsEqual.equalTo(new org.web3j.abi.datatypes.StaticArray<Utf8String>(new Utf8String("Hello, world!"), new Utf8String("world! Hello,"))));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyStaticArray() {
        Assert.assertThat(TypeDecoder.decodeStaticArray("0000000000000000000000000000000000000000000000000000000000000000", 0, new TypeReference.StaticArrayTypeReference<org.web3j.abi.datatypes.StaticArray<Uint256>>(0) {}, 0), CoreMatchers.is("invalid"));
    }

    @Test
    public void testDynamicArray() {
        Assert.assertThat(// length
        TypeDecoder.decodeDynamicArray("0000000000000000000000000000000000000000000000000000000000000000", 0, new TypeReference<org.web3j.abi.datatypes.DynamicArray<Uint256>>() {}), IsEqual.equalTo(org.web3j.abi.datatypes.DynamicArray.empty("uint256")));
        Assert.assertThat(TypeDecoder.decodeDynamicArray(("0000000000000000000000000000000000000000000000000000000000000002"// length
         + ("000000000000000000000000000000000000000000000000000000000000000a" + "0000000000000000000000000000000000000000000000007fffffffffffffff")), 0, new TypeReference<org.web3j.abi.datatypes.DynamicArray<Uint256>>() {}), IsEqual.equalTo(new org.web3j.abi.datatypes.DynamicArray<Uint256>(new Uint256(BigInteger.TEN), new Uint256(BigInteger.valueOf(Long.MAX_VALUE)))));
        Assert.assertThat(TypeDecoder.decodeDynamicArray(("0000000000000000000000000000000000000000000000000000000000000002"// length
         + ((("000000000000000000000000000000000000000000000000000000000000000d" + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000") + "000000000000000000000000000000000000000000000000000000000000000d") + "776f726c64212048656c6c6f2c00000000000000000000000000000000000000")), 0, new TypeReference<org.web3j.abi.datatypes.DynamicArray<Utf8String>>() {}), IsEqual.equalTo(new org.web3j.abi.datatypes.DynamicArray<Utf8String>(new Utf8String("Hello, world!"), new Utf8String("world! Hello,"))));
    }
}

