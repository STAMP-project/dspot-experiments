package org.web3j.ens;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.tx.ChainId;


public class ContractsTest {
    @Test
    public void testResolveRegistryContract() {
        Assert.assertThat(Contracts.resolveRegistryContract(((ChainId.MAINNET) + "")), CoreMatchers.is(Contracts.MAINNET));
        Assert.assertThat(Contracts.resolveRegistryContract(((ChainId.ROPSTEN) + "")), CoreMatchers.is(Contracts.ROPSTEN));
        Assert.assertThat(Contracts.resolveRegistryContract(((ChainId.RINKEBY) + "")), CoreMatchers.is(Contracts.RINKEBY));
    }

    @Test(expected = EnsResolutionException.class)
    public void testResolveRegistryContractInvalid() {
        Contracts.resolveRegistryContract(((ChainId.NONE) + ""));
    }
}

