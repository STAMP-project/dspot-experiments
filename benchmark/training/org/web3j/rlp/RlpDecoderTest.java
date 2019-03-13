package org.web3j.rlp;


import java.math.BigInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;


public class RlpDecoderTest {
    /**
     * Examples taken from https://github.com/ethereum/wiki/wiki/RLP#examples.
     * For further examples see https://github.com/ethereum/tests/tree/develop/RLPTests.
     */
    @Test
    public void testRLPDecode() {
        // big positive number should stay positive after encoding-decoding
        // https://github.com/web3j/web3j/issues/562
        long value = 3000000000L;
        Assert.assertThat(RlpString.create(BigInteger.valueOf(value)).asPositiveBigInteger().longValue(), CoreMatchers.equalTo(value));
        // empty array of binary
        Assert.assertTrue(getValues().isEmpty());
        // The string "dog" = [ 0x83, 'd', 'o', 'g' ]
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create("dog")));
        // The list [ "cat", "dog" ] = [ 0xc8, 0x83, 'c', 'a', 't', 0x83, 'd', 'o', 'g' ]
        RlpList rlpList = ((RlpList) (getValues().get(0)));
        Assert.assertThat(rlpList.getValues().get(0), CoreMatchers.is(RlpString.create("cat")));
        Assert.assertThat(rlpList.getValues().get(1), CoreMatchers.is(RlpString.create("dog")));
        // The empty string ('null') = [ 0x80 ]
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create("")));
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create(new byte[]{  })));
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create(BigInteger.ZERO)));
        // The empty list = [ 0xc0 ]
        Assert.assertThat(getValues().get(0), CoreMatchers.instanceOf(RlpList.class));
        Assert.assertTrue(getValues().isEmpty());
        // The encoded integer 0 ('\x00') = [ 0x00 ]
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create(BigInteger.valueOf(0).byteValue())));
        // The encoded integer 15 ('\x0f') = [ 0x0f ]
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create(BigInteger.valueOf(15).byteValue())));
        // The encoded integer 1024 ('\x04\x00') = [ 0x82, 0x04, 0x00 ]
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create(BigInteger.valueOf(1024))));
        // The set theoretical representation of three,
        // [ [], [[]], [ [], [[]] ] ] = [ 0xc7, 0xc0, 0xc1, 0xc0, 0xc3, 0xc0, 0xc1, 0xc0 ]
        rlpList = RlpDecoder.decode(new byte[]{ ((byte) (199)), ((byte) (192)), ((byte) (193)), ((byte) (192)), ((byte) (195)), ((byte) (192)), ((byte) (193)), ((byte) (192)) });
        Assert.assertThat(rlpList, CoreMatchers.instanceOf(RlpList.class));
        Assert.assertThat(rlpList.getValues().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(rlpList.getValues().get(0), CoreMatchers.instanceOf(RlpList.class));
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(3));
        Assert.assertThat(getValues().get(0), CoreMatchers.instanceOf(RlpList.class));
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(0));
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(getValues().get(0), CoreMatchers.instanceOf(RlpList.class));
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(0));
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(1));
        // The string "Lorem ipsum dolor sit amet,
        // consectetur adipisicing elit" =
        // [ 0xb8, 0x38, 'L', 'o', 'r', 'e', 'm', ' ', ... , 'e', 'l', 'i', 't' ]
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create("Lorem ipsum dolor sit amet, consectetur adipisicing elit")));
        // https://github.com/paritytech/parity/blob/master/util/rlp/tests/tests.rs#L239
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create(new byte[]{ 0 })));
        rlpList = RlpDecoder.decode(new byte[]{ ((byte) (198)), ((byte) (130)), ((byte) (122)), ((byte) (119)), ((byte) (193)), ((byte) (4)), ((byte) (1)) });
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(3));
        Assert.assertThat(getValues().get(0), CoreMatchers.instanceOf(RlpString.class));
        Assert.assertThat(getValues().get(1), CoreMatchers.instanceOf(RlpList.class));
        Assert.assertThat(getValues().get(2), CoreMatchers.instanceOf(RlpString.class));
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create("zw")));
        Assert.assertThat(getValues().get(0), CoreMatchers.is(RlpString.create(4)));
        Assert.assertThat(getValues().get(2), CoreMatchers.is(RlpString.create(1)));
        // payload more than 55 bytes
        String data = "F86E12F86B80881BC16D674EC8000094CD2A3D9F938E13CD947EC05ABC7FE734D" + (("F8DD8268609184E72A00064801BA0C52C114D4F5A3BA904A9B3036E5E118FE0DBB987" + "FE3955DA20F2CD8F6C21AB9CA06BA4C2874299A55AD947DBC98A25EE895AABF6B625C") + "26C435E84BFD70EDF2F69");
        byte[] payload = Numeric.hexStringToByteArray(data);
        rlpList = RlpDecoder.decode(payload);
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(getValues().get(0), CoreMatchers.instanceOf(RlpString.class));
        Assert.assertThat(getValues().get(1), CoreMatchers.instanceOf(RlpList.class));
        Assert.assertThat(getValues().size(), CoreMatchers.equalTo(9));
    }
}

