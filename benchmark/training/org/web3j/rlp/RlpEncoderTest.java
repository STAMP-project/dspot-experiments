package org.web3j.rlp;


import java.math.BigInteger;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RlpEncoderTest {
    /**
     * Examples taken from https://github.com/ethereum/wiki/wiki/RLP#examples.
     *
     * <p>For further examples see https://github.com/ethereum/tests/tree/develop/RLPTests.
     */
    @Test
    public void testEncode() {
        Assert.assertThat(RlpEncoder.encode(RlpString.create("dog")), CoreMatchers.is(new byte[]{ ((byte) (131)), 'd', 'o', 'g' }));
        Assert.assertThat(RlpEncoder.encode(new RlpList(RlpString.create("cat"), RlpString.create("dog"))), CoreMatchers.is(new byte[]{ ((byte) (200)), ((byte) (131)), 'c', 'a', 't', ((byte) (131)), 'd', 'o', 'g' }));
        Assert.assertThat(RlpEncoder.encode(RlpString.create("")), CoreMatchers.is(new byte[]{ ((byte) (128)) }));
        Assert.assertThat(RlpEncoder.encode(RlpString.create(new byte[]{  })), CoreMatchers.is(new byte[]{ ((byte) (128)) }));
        Assert.assertThat(RlpEncoder.encode(new RlpList()), CoreMatchers.is(new byte[]{ ((byte) (192)) }));
        Assert.assertThat(RlpEncoder.encode(RlpString.create(BigInteger.valueOf(15))), CoreMatchers.is(new byte[]{ ((byte) (15)) }));
        Assert.assertThat(RlpEncoder.encode(RlpString.create(BigInteger.valueOf(1024))), CoreMatchers.is(new byte[]{ ((byte) (130)), ((byte) (4)), ((byte) (0)) }));
        Assert.assertThat(RlpEncoder.encode(new RlpList(new RlpList(), new RlpList(new RlpList()), new RlpList(new RlpList(), new RlpList(new RlpList())))), CoreMatchers.is(new byte[]{ ((byte) (199)), ((byte) (192)), ((byte) (193)), ((byte) (192)), ((byte) (195)), ((byte) (192)), ((byte) (193)), ((byte) (192)) }));
        Assert.assertThat(RlpEncoder.encode(RlpString.create("Lorem ipsum dolor sit amet, consectetur adipisicing elit")), CoreMatchers.is(new byte[]{ ((byte) (184)), ((byte) (56)), 'L', 'o', 'r', 'e', 'm', ' ', 'i', 'p', 's', 'u', 'm', ' ', 'd', 'o', 'l', 'o', 'r', ' ', 's', 'i', 't', ' ', 'a', 'm', 'e', 't', ',', ' ', 'c', 'o', 'n', 's', 'e', 'c', 't', 'e', 't', 'u', 'r', ' ', 'a', 'd', 'i', 'p', 'i', 's', 'i', 'c', 'i', 'n', 'g', ' ', 'e', 'l', 'i', 't' }));
        Assert.assertThat(RlpEncoder.encode(RlpString.create(BigInteger.ZERO)), CoreMatchers.is(new byte[]{ ((byte) (128)) }));
        // https://github.com/paritytech/parity-common/blob/master/rlp/tests/tests.rs#L237
        Assert.assertThat(RlpEncoder.encode(RlpString.create(new byte[]{ 0 })), CoreMatchers.is(new byte[]{ ((byte) (0)) }));
        Assert.assertThat(RlpEncoder.encode(new RlpList(RlpString.create("zw"), new RlpList(RlpString.create(4)), RlpString.create(1))), CoreMatchers.is(new byte[]{ ((byte) (198)), ((byte) (130)), ((byte) (122)), ((byte) (119)), ((byte) (193)), ((byte) (4)), ((byte) (1)) }));
        // 55 bytes. See https://github.com/web3j/web3j/issues/519
        byte[] encodeMe = new byte[55];
        Arrays.fill(encodeMe, ((byte) (0)));
        byte[] expectedEncoding = new byte[56];
        expectedEncoding[0] = ((byte) (183));
        System.arraycopy(encodeMe, 0, expectedEncoding, 1, encodeMe.length);
        Assert.assertThat(RlpEncoder.encode(RlpString.create(encodeMe)), CoreMatchers.is(expectedEncoding));
    }
}

