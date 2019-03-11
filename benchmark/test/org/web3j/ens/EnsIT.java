package org.web3j.ens;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;


public class EnsIT {
    @Test
    public void testEns() throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        EnsResolver ensResolver = new EnsResolver(web3j);
        Assert.assertThat(ensResolver.resolve("web3j.test"), CoreMatchers.is("0x19e03255f667bdfd50a32722df860b1eeaf4d635"));
    }
}

