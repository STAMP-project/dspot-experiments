package org.web3j.abi.datatypes;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class StaticArrayTest {
    @Test
    public void canBeInstantiatedWithLessThan32Elements() {
        final StaticArray<Uint> array = new StaticArray(arrayOfUints(32));
        MatcherAssert.assertThat(array.getValue().size(), CoreMatchers.equalTo(32));
    }

    @Test
    public void canBeInstantiatedWithSizeMatchingType() {
        final StaticArray<Uint> array = new org.web3j.abi.datatypes.generated.StaticArray3(arrayOfUints(3));
        MatcherAssert.assertThat(array.getValue().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void throwsIfSizeDoesntMatchType() {
        try {
            new org.web3j.abi.datatypes.generated.StaticArray3(arrayOfUints(4));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo("Expected array of type [StaticArray3] to have [3] elements."));
        }
    }

    @Test
    public void throwsIfSizeIsAboveMaxOf32() {
        try {
            new StaticArray(arrayOfUints(33));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo("Static arrays with a length greater than 32 are not supported."));
        }
    }
}

