package org.web3j.protocol.core;


import DefaultBlockParameterName.LATEST;
import EthBlock.Block;
import EthLog.LogResult;
import java.math.BigInteger;
import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthCoinbase;
import org.web3j.protocol.core.methods.response.EthCompileSolidity;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetBlockTransactionCountByHash;
import org.web3j.protocol.core.methods.response.EthGetBlockTransactionCountByNumber;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthGetCompilers;
import org.web3j.protocol.core.methods.response.EthGetStorageAt;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthGetUncleCountByBlockHash;
import org.web3j.protocol.core.methods.response.EthGetUncleCountByBlockNumber;
import org.web3j.protocol.core.methods.response.EthHashrate;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthMining;
import org.web3j.protocol.core.methods.response.EthProtocolVersion;
import org.web3j.protocol.core.methods.response.EthSyncing;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.EthUninstallFilter;
import org.web3j.protocol.core.methods.response.NetListening;
import org.web3j.protocol.core.methods.response.NetPeerCount;
import org.web3j.protocol.core.methods.response.NetVersion;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.core.methods.response.Web3Sha3;

import static DefaultBlockParameterName.EARLIEST;
import static DefaultBlockParameterName.LATEST;


/**
 * JSON-RPC 2.0 Integration Tests.
 */
public class CoreIT {
    private Web3j web3j;

    private IntegrationTestConfig config = new TestnetConfig();

    public CoreIT() {
    }

    @Test
    public void testWeb3ClientVersion() throws Exception {
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        System.out.println(("Ethereum client version: " + clientVersion));
        TestCase.assertFalse(clientVersion.isEmpty());
    }

    @Test
    public void testWeb3Sha3() throws Exception {
        Web3Sha3 web3Sha3 = web3j.web3Sha3("0x68656c6c6f20776f726c64").send();
        MatcherAssert.assertThat(web3Sha3.getResult(), CoreMatchers.is("0x47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"));
    }

    @Test
    public void testNetVersion() throws Exception {
        NetVersion netVersion = web3j.netVersion().send();
        TestCase.assertFalse(netVersion.getNetVersion().isEmpty());
    }

    @Test
    public void testNetListening() throws Exception {
        NetListening netListening = web3j.netListening().send();
        Assert.assertTrue(netListening.isListening());
    }

    @Test
    public void testNetPeerCount() throws Exception {
        NetPeerCount netPeerCount = web3j.netPeerCount().send();
        Assert.assertTrue(((netPeerCount.getQuantity().signum()) == 1));
    }

    @Test
    public void testEthProtocolVersion() throws Exception {
        EthProtocolVersion ethProtocolVersion = web3j.ethProtocolVersion().send();
        TestCase.assertFalse(ethProtocolVersion.getProtocolVersion().isEmpty());
    }

    @Test
    public void testEthSyncing() throws Exception {
        EthSyncing ethSyncing = web3j.ethSyncing().send();
        Assert.assertNotNull(ethSyncing.getResult());
    }

    @Test
    public void testEthCoinbase() throws Exception {
        EthCoinbase ethCoinbase = web3j.ethCoinbase().send();
        Assert.assertNotNull(ethCoinbase.getAddress());
    }

    @Test
    public void testEthMining() throws Exception {
        EthMining ethMining = web3j.ethMining().send();
        Assert.assertNotNull(ethMining.getResult());
    }

    @Test
    public void testEthHashrate() throws Exception {
        EthHashrate ethHashrate = web3j.ethHashrate().send();
        MatcherAssert.assertThat(ethHashrate.getHashrate(), CoreMatchers.is(BigInteger.ZERO));
    }

    @Test
    public void testEthGasPrice() throws Exception {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        Assert.assertTrue(((ethGasPrice.getGasPrice().signum()) == 1));
    }

    @Test
    public void testEthAccounts() throws Exception {
        EthAccounts ethAccounts = web3j.ethAccounts().send();
        Assert.assertNotNull(ethAccounts.getAccounts());
    }

    @Test
    public void testEthBlockNumber() throws Exception {
        EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
        Assert.assertTrue(((ethBlockNumber.getBlockNumber().signum()) == 1));
    }

    @Test
    public void testEthGetBalance() throws Exception {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(config.validAccount(), DefaultBlockParameter.valueOf("latest")).send();
        Assert.assertTrue(((ethGetBalance.getBalance().signum()) == 1));
    }

    @Test
    public void testEthGetStorageAt() throws Exception {
        EthGetStorageAt ethGetStorageAt = web3j.ethGetStorageAt(config.validContractAddress(), BigInteger.valueOf(0), DefaultBlockParameter.valueOf("latest")).send();
        MatcherAssert.assertThat(ethGetStorageAt.getData(), CoreMatchers.is(config.validContractAddressPositionZero()));
    }

    @Test
    public void testEthGetTransactionCount() throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(config.validAccount(), DefaultBlockParameter.valueOf("latest")).send();
        Assert.assertTrue(((ethGetTransactionCount.getTransactionCount().signum()) == 1));
    }

    @Test
    public void testEthGetBlockTransactionCountByHash() throws Exception {
        EthGetBlockTransactionCountByHash ethGetBlockTransactionCountByHash = web3j.ethGetBlockTransactionCountByHash(config.validBlockHash()).send();
        MatcherAssert.assertThat(ethGetBlockTransactionCountByHash.getTransactionCount(), CoreMatchers.equalTo(config.validBlockTransactionCount()));
    }

    @Test
    public void testEthGetBlockTransactionCountByNumber() throws Exception {
        EthGetBlockTransactionCountByNumber ethGetBlockTransactionCountByNumber = web3j.ethGetBlockTransactionCountByNumber(DefaultBlockParameter.valueOf(config.validBlock())).send();
        MatcherAssert.assertThat(ethGetBlockTransactionCountByNumber.getTransactionCount(), CoreMatchers.equalTo(config.validBlockTransactionCount()));
    }

    @Test
    public void testEthGetUncleCountByBlockHash() throws Exception {
        EthGetUncleCountByBlockHash ethGetUncleCountByBlockHash = web3j.ethGetUncleCountByBlockHash(config.validBlockHash()).send();
        MatcherAssert.assertThat(ethGetUncleCountByBlockHash.getUncleCount(), CoreMatchers.equalTo(config.validBlockUncleCount()));
    }

    @Test
    public void testEthGetUncleCountByBlockNumber() throws Exception {
        EthGetUncleCountByBlockNumber ethGetUncleCountByBlockNumber = web3j.ethGetUncleCountByBlockNumber(DefaultBlockParameter.valueOf("latest")).send();
        MatcherAssert.assertThat(ethGetUncleCountByBlockNumber.getUncleCount(), CoreMatchers.equalTo(config.validBlockUncleCount()));
    }

    @Test
    public void testEthGetCode() throws Exception {
        EthGetCode ethGetCode = web3j.ethGetCode(config.validContractAddress(), DefaultBlockParameter.valueOf(config.validBlock())).send();
        MatcherAssert.assertThat(ethGetCode.getCode(), CoreMatchers.is(config.validContractCode()));
    }

    @Test
    public void testEthCall() throws Exception {
        EthCall ethCall = web3j.ethCall(config.buildTransaction(), DefaultBlockParameter.valueOf("latest")).send();
        MatcherAssert.assertThat(LATEST.getValue(), CoreMatchers.is("latest"));
        MatcherAssert.assertThat(ethCall.getValue(), CoreMatchers.is("0x"));
    }

    @Test
    public void testEthEstimateGas() throws Exception {
        EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(config.buildTransaction()).send();
        Assert.assertTrue(((ethEstimateGas.getAmountUsed().signum()) == 1));
    }

    @Test
    public void testEthGetBlockByHashReturnHashObjects() throws Exception {
        EthBlock ethBlock = web3j.ethGetBlockByHash(config.validBlockHash(), false).send();
        EthBlock.Block block = ethBlock.getBlock();
        Assert.assertNotNull(ethBlock.getBlock());
        MatcherAssert.assertThat(block.getNumber(), CoreMatchers.equalTo(config.validBlock()));
        MatcherAssert.assertThat(block.getTransactions().size(), CoreMatchers.is(config.validBlockTransactionCount().intValue()));
    }

    @Test
    public void testEthGetBlockByHashReturnFullTransactionObjects() throws Exception {
        EthBlock ethBlock = web3j.ethGetBlockByHash(config.validBlockHash(), true).send();
        EthBlock.Block block = ethBlock.getBlock();
        Assert.assertNotNull(ethBlock.getBlock());
        MatcherAssert.assertThat(block.getNumber(), CoreMatchers.equalTo(config.validBlock()));
        MatcherAssert.assertThat(block.getTransactions().size(), CoreMatchers.equalTo(config.validBlockTransactionCount().intValue()));
    }

    @Test
    public void testEthGetBlockByNumberReturnHashObjects() throws Exception {
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(config.validBlock()), false).send();
        EthBlock.Block block = ethBlock.getBlock();
        Assert.assertNotNull(ethBlock.getBlock());
        MatcherAssert.assertThat(block.getNumber(), CoreMatchers.equalTo(config.validBlock()));
        MatcherAssert.assertThat(block.getTransactions().size(), CoreMatchers.equalTo(config.validBlockTransactionCount().intValue()));
    }

    @Test
    public void testEthGetBlockByNumberReturnTransactionObjects() throws Exception {
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(config.validBlock()), true).send();
        EthBlock.Block block = ethBlock.getBlock();
        Assert.assertNotNull(ethBlock.getBlock());
        MatcherAssert.assertThat(block.getNumber(), CoreMatchers.equalTo(config.validBlock()));
        MatcherAssert.assertThat(block.getTransactions().size(), CoreMatchers.equalTo(config.validBlockTransactionCount().intValue()));
    }

    @Test
    public void testEthGetTransactionByHash() throws Exception {
        EthTransaction ethTransaction = web3j.ethGetTransactionByHash(config.validTransactionHash()).send();
        Assert.assertTrue(ethTransaction.getTransaction().isPresent());
        Transaction transaction = ethTransaction.getTransaction().get();
        MatcherAssert.assertThat(transaction.getBlockHash(), CoreMatchers.is(config.validBlockHash()));
    }

    @Test
    public void testEthGetTransactionByBlockHashAndIndex() throws Exception {
        BigInteger index = BigInteger.ONE;
        EthTransaction ethTransaction = web3j.ethGetTransactionByBlockHashAndIndex(config.validBlockHash(), index).send();
        Assert.assertTrue(ethTransaction.getTransaction().isPresent());
        Transaction transaction = ethTransaction.getTransaction().get();
        MatcherAssert.assertThat(transaction.getBlockHash(), CoreMatchers.is(config.validBlockHash()));
        MatcherAssert.assertThat(transaction.getTransactionIndex(), CoreMatchers.equalTo(index));
    }

    @Test
    public void testEthGetTransactionByBlockNumberAndIndex() throws Exception {
        BigInteger index = BigInteger.ONE;
        EthTransaction ethTransaction = web3j.ethGetTransactionByBlockNumberAndIndex(DefaultBlockParameter.valueOf(config.validBlock()), index).send();
        Assert.assertTrue(ethTransaction.getTransaction().isPresent());
        Transaction transaction = ethTransaction.getTransaction().get();
        MatcherAssert.assertThat(transaction.getBlockHash(), CoreMatchers.is(config.validBlockHash()));
        MatcherAssert.assertThat(transaction.getTransactionIndex(), CoreMatchers.equalTo(index));
    }

    @Test
    public void testEthGetTransactionReceipt() throws Exception {
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(config.validTransactionHash()).send();
        Assert.assertTrue(ethGetTransactionReceipt.getTransactionReceipt().isPresent());
        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().get();
        MatcherAssert.assertThat(transactionReceipt.getTransactionHash(), CoreMatchers.is(config.validTransactionHash()));
    }

    @Test
    public void testEthGetUncleByBlockHashAndIndex() throws Exception {
        EthBlock ethBlock = web3j.ethGetUncleByBlockHashAndIndex(config.validUncleBlockHash(), BigInteger.ZERO).send();
        Assert.assertNotNull(ethBlock.getBlock());
    }

    @Test
    public void testEthGetUncleByBlockNumberAndIndex() throws Exception {
        EthBlock ethBlock = web3j.ethGetUncleByBlockNumberAndIndex(DefaultBlockParameter.valueOf(config.validUncleBlock()), BigInteger.ZERO).send();
        Assert.assertNotNull(ethBlock.getBlock());
    }

    @Test
    public void testEthGetCompilers() throws Exception {
        EthGetCompilers ethGetCompilers = web3j.ethGetCompilers().send();
        Assert.assertNotNull(ethGetCompilers.getCompilers());
    }

    @Test
    public void testEthCompileSolidity() throws Exception {
        String sourceCode = "pragma solidity ^0.4.0;" + ((("\ncontract test { function multiply(uint a) returns(uint d) {" + "   return a * 7;   } }") + "\ncontract test2 { function multiply2(uint a) returns(uint d) {") + "   return a * 7;   } }");
        EthCompileSolidity ethCompileSolidity = web3j.ethCompileSolidity(sourceCode).send();
        Assert.assertNotNull(ethCompileSolidity.getCompiledSolidity());
        MatcherAssert.assertThat(ethCompileSolidity.getCompiledSolidity().get("test2").getInfo().getSource(), CoreMatchers.is(sourceCode));
    }

    @Test
    public void testFiltersByFilterId() throws Exception {
        EthFilter ethFilter = new EthFilter(EARLIEST, LATEST, config.validContractAddress());
        String eventSignature = config.encodedEvent();
        ethFilter.addSingleTopic(eventSignature);
        // eth_newFilter
        org.web3j.protocol.core.methods.response.EthFilter ethNewFilter = web3j.ethNewFilter(ethFilter).send();
        BigInteger filterId = ethNewFilter.getFilterId();
        // eth_getFilterLogs
        EthLog ethFilterLogs = web3j.ethGetFilterLogs(filterId).send();
        List<EthLog.LogResult> filterLogs = ethFilterLogs.getLogs();
        TestCase.assertFalse(filterLogs.isEmpty());
        // eth_getFilterChanges - nothing will have changed in this interval
        EthLog ethLog = web3j.ethGetFilterChanges(filterId).send();
        Assert.assertTrue(ethLog.getLogs().isEmpty());
        // eth_uninstallFilter
        EthUninstallFilter ethUninstallFilter = web3j.ethUninstallFilter(filterId).send();
        Assert.assertTrue(ethUninstallFilter.isUninstalled());
    }

    @Test
    public void testEthNewBlockFilter() throws Exception {
        org.web3j.protocol.core.methods.response.EthFilter ethNewBlockFilter = web3j.ethNewBlockFilter().send();
        Assert.assertNotNull(ethNewBlockFilter.getFilterId());
    }

    @Test
    public void testEthNewPendingTransactionFilter() throws Exception {
        org.web3j.protocol.core.methods.response.EthFilter ethNewPendingTransactionFilter = web3j.ethNewPendingTransactionFilter().send();
        Assert.assertNotNull(ethNewPendingTransactionFilter.getFilterId());
    }

    @Test
    public void testEthGetLogs() throws Exception {
        EthFilter ethFilter = new EthFilter(EARLIEST, LATEST, config.validContractAddress());
        ethFilter.addSingleTopic(config.encodedEvent());
        EthLog ethLog = web3j.ethGetLogs(ethFilter).send();
        List<EthLog.LogResult> logs = ethLog.getLogs();
        TestCase.assertFalse(logs.isEmpty());
    }
}

