package org.web3j.utils;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class BytesTest {
    @Test
    public void testTrimLeadingZeroes() {
        Assert.assertThat(Bytes.trimLeadingZeroes(new byte[]{  }), CoreMatchers.is(new byte[]{  }));
        Assert.assertThat(Bytes.trimLeadingZeroes(new byte[]{ 0 }), CoreMatchers.is(new byte[]{ 0 }));
        Assert.assertThat(Bytes.trimLeadingZeroes(new byte[]{ 1 }), CoreMatchers.is(new byte[]{ 1 }));
        Assert.assertThat(Bytes.trimLeadingZeroes(new byte[]{ 0, 1 }), CoreMatchers.is(new byte[]{ 1 }));
        Assert.assertThat(Bytes.trimLeadingZeroes(new byte[]{ 0, 0, 1 }), CoreMatchers.is(new byte[]{ 1 }));
        Assert.assertThat(Bytes.trimLeadingZeroes(new byte[]{ 0, 0, 1, 0 }), CoreMatchers.is(new byte[]{ 1, 0 }));
    }
}

