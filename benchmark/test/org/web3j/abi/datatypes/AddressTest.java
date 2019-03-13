package org.web3j.abi.datatypes;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AddressTest {
    @Test
    public void testToString() {
        Assert.assertThat(new Address("00052b08330e05d731e38c856c1043288f7d9744").toString(), Is.is("0x00052b08330e05d731e38c856c1043288f7d9744"));
        Assert.assertThat(new Address("0x00052b08330e05d731e38c856c1043288f7d9744").toString(), Is.is("0x00052b08330e05d731e38c856c1043288f7d9744"));
    }
}

