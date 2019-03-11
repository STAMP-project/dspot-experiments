package org.web3j.ens;


import ChainId.MAINNET;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.NetVersion;


public class EnsResolverTest {
    private Web3j web3j;

    private Web3jService web3jService;

    private EnsResolver ensResolver;

    @Test
    public void testResolve() throws Exception {
        configureSyncing(false);
        configureLatestBlock(((System.currentTimeMillis()) / 1000));// block timestamp is in seconds

        NetVersion netVersion = new NetVersion();
        netVersion.setResult(Byte.toString(MAINNET));
        String resolverAddress = "0x0000000000000000000000004c641fb9bad9b60ef180c31f56051ce826d21a9a";
        String contractAddress = "0x00000000000000000000000019e03255f667bdfd50a32722df860b1eeaf4d635";
        EthCall resolverAddressResponse = new EthCall();
        resolverAddressResponse.setResult(resolverAddress);
        EthCall contractAddressResponse = new EthCall();
        contractAddressResponse.setResult(contractAddress);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(NetVersion.class))).thenReturn(netVersion);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthCall.class))).thenReturn(resolverAddressResponse);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthCall.class))).thenReturn(contractAddressResponse);
        Assert.assertThat(ensResolver.resolve("web3j.eth"), CoreMatchers.is("0x19e03255f667bdfd50a32722df860b1eeaf4d635"));
    }

    @Test
    public void testReverseResolve() throws Exception {
        configureSyncing(false);
        configureLatestBlock(((System.currentTimeMillis()) / 1000));// block timestamp is in seconds

        NetVersion netVersion = new NetVersion();
        netVersion.setResult(Byte.toString(MAINNET));
        String resolverAddress = "0x0000000000000000000000004c641fb9bad9b60ef180c31f56051ce826d21a9a";
        String contractName = "0x0000000000000000000000000000000000000000000000000000000000000020" + (TypeEncoder.encode(new Utf8String("web3j.eth")));
        System.err.println(contractName);
        EthCall resolverAddressResponse = new EthCall();
        resolverAddressResponse.setResult(resolverAddress);
        EthCall contractNameResponse = new EthCall();
        contractNameResponse.setResult(contractName);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(NetVersion.class))).thenReturn(netVersion);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthCall.class))).thenReturn(resolverAddressResponse);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthCall.class))).thenReturn(contractNameResponse);
        Assert.assertThat(ensResolver.reverseResolve("0x19e03255f667bdfd50a32722df860b1eeaf4d635"), CoreMatchers.is("web3j.eth"));
    }

    @Test
    public void testIsSyncedSyncing() throws Exception {
        configureSyncing(true);
        Assert.assertFalse(ensResolver.isSynced());
    }

    @Test
    public void testIsSyncedFullySynced() throws Exception {
        configureSyncing(false);
        configureLatestBlock(((System.currentTimeMillis()) / 1000));// block timestamp is in seconds

        Assert.assertTrue(ensResolver.isSynced());
    }

    @Test
    public void testIsSyncedBelowThreshold() throws Exception {
        configureSyncing(false);
        configureLatestBlock((((System.currentTimeMillis()) / 1000) - (EnsResolver.DEFAULT_SYNC_THRESHOLD)));
        Assert.assertFalse(ensResolver.isSynced());
    }

    @Test
    public void testIsEnsName() {
        Assert.assertTrue(EnsResolver.isValidEnsName("eth"));
        Assert.assertTrue(EnsResolver.isValidEnsName("web3.eth"));
        Assert.assertTrue(EnsResolver.isValidEnsName("0x19e03255f667bdfd50a32722df860b1eeaf4d635.eth"));
        Assert.assertFalse(EnsResolver.isValidEnsName("0x19e03255f667bdfd50a32722df860b1eeaf4d635"));
        Assert.assertFalse(EnsResolver.isValidEnsName("19e03255f667bdfd50a32722df860b1eeaf4d635"));
        Assert.assertTrue(EnsResolver.isValidEnsName(""));
    }
}

