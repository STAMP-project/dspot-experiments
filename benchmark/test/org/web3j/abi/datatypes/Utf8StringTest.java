package org.web3j.abi.datatypes;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class Utf8StringTest {
    @Test
    public void testToString() {
        Assert.assertThat(new Utf8String("").toString(), Is.is(""));
        Assert.assertThat(new Utf8String("string").toString(), Is.is("string"));
    }
}

