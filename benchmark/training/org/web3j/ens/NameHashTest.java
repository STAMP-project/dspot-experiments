package org.web3j.ens;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NameHashTest {
    @Test
    public void testNameHash() {
        Assert.assertThat(NameHash.nameHash(""), CoreMatchers.is("0x0000000000000000000000000000000000000000000000000000000000000000"));
        Assert.assertThat(NameHash.nameHash("eth"), CoreMatchers.is("0x93cdeb708b7545dc668eb9280176169d1c33cfd8ed6f04690a0bcc88a93fc4ae"));
        Assert.assertThat(NameHash.nameHash("foo.eth"), CoreMatchers.is("0xde9b09fd7c5f901e23a3f19fecc54828e9c848539801e86591bd9801b019f84f"));
    }

    @Test
    public void testNormalise() {
        Assert.assertThat(NameHash.normalise("foo"), CoreMatchers.is("foo"));
        Assert.assertThat(NameHash.normalise("foo.bar.baz.eth"), CoreMatchers.is("foo.bar.baz.eth"));
        Assert.assertThat(NameHash.normalise("fOo.eth"), CoreMatchers.is("foo.eth"));
        Assert.assertThat(NameHash.normalise("foo-bar.eth"), CoreMatchers.is("foo-bar.eth"));
    }

    @Test
    public void testNormaliseInvalid() {
        testInvalidName("foo..bar");
        testInvalidName("ba\\u007Fr.eth");
        testInvalidName("-baz.eth-");
        testInvalidName("foo_bar.eth");
    }
}

