package org.web3j.crypto;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;


public class HashTest {
    @Test
    public void testSha3() {
        byte[] input = new byte[]{ Numeric.asByte(6, 8), Numeric.asByte(6, 5), Numeric.asByte(6, 12), Numeric.asByte(6, 12), Numeric.asByte(6, 15), Numeric.asByte(2, 0), Numeric.asByte(7, 7), Numeric.asByte(6, 15), Numeric.asByte(7, 2), Numeric.asByte(6, 12), Numeric.asByte(6, 4) };
        byte[] expected = new byte[]{ Numeric.asByte(4, 7), Numeric.asByte(1, 7), Numeric.asByte(3, 2), Numeric.asByte(8, 5), Numeric.asByte(10, 8), Numeric.asByte(13, 7), Numeric.asByte(3, 4), Numeric.asByte(1, 14), Numeric.asByte(5, 14), Numeric.asByte(9, 7), Numeric.asByte(2, 15), Numeric.asByte(12, 6), Numeric.asByte(7, 7), Numeric.asByte(2, 8), Numeric.asByte(6, 3), Numeric.asByte(8, 4), Numeric.asByte(15, 8), Numeric.asByte(0, 2), Numeric.asByte(15, 8), Numeric.asByte(14, 15), Numeric.asByte(4, 2), Numeric.asByte(10, 5), Numeric.asByte(14, 12), Numeric.asByte(5, 15), Numeric.asByte(0, 3), Numeric.asByte(11, 11), Numeric.asByte(15, 10), Numeric.asByte(2, 5), Numeric.asByte(4, 12), Numeric.asByte(11, 0), Numeric.asByte(1, 15), Numeric.asByte(10, 13) };
        byte[] result = Hash.sha3(input);
        Assert.assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void testSha3HashHex() {
        Assert.assertThat(Hash.sha3(""), CoreMatchers.is("0xc5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470"));
        Assert.assertThat(Hash.sha3("68656c6c6f20776f726c64"), CoreMatchers.is("0x47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"));
    }

    @Test
    public void testSha3String() {
        Assert.assertThat(Hash.sha3String(""), CoreMatchers.is("0xc5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470"));
        Assert.assertThat(Hash.sha3String("EVWithdraw(address,uint256,bytes32)"), CoreMatchers.is("0x953d0c27f84a9649b0e121099ffa9aeb7ed83e65eaed41d3627f895790c72d41"));
    }

    @Test
    public void testByte() {
        Assert.assertThat(Numeric.asByte(0, 0), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(Numeric.asByte(1, 0), CoreMatchers.is(((byte) (16))));
        Assert.assertThat(Numeric.asByte(15, 15), CoreMatchers.is(((byte) (255))));
        Assert.assertThat(Numeric.asByte(12, 5), CoreMatchers.is(((byte) (197))));
    }
}

