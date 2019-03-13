package org.web3j.abi.datatypes;


import java.math.BigInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class FixedPointTypeTest {
    @Test
    public void testConvert() {
        Assert.assertThat(FixedPointType.convert(BigInteger.valueOf(2), BigInteger.valueOf(2)), CoreMatchers.is(new BigInteger("220000000000000000000000000000000", 16)));
        Assert.assertThat(FixedPointType.convert(BigInteger.valueOf(8), BigInteger.valueOf(8)), CoreMatchers.is(new BigInteger("880000000000000000000000000000000", 16)));
        Assert.assertThat(FixedPointType.convert(BigInteger.valueOf(43775), BigInteger.valueOf(4369)), CoreMatchers.is(new BigInteger("AAFF11110000000000000000000000000000", 16)));
    }
}

