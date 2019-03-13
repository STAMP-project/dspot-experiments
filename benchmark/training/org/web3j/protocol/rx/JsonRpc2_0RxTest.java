package org.web3j.protocol.rx;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthUninstallFilter;
import org.web3j.protocol.core.methods.response.Transaction;


public class JsonRpc2_0RxTest {
    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private Web3j web3j;

    private Web3jService web3jService;

    @Test
    public void testReplayBlocksFlowable() throws Exception {
        List<EthBlock> ethBlocks = Arrays.asList(createBlock(0), createBlock(1), createBlock(2));
        OngoingStubbing<EthBlock> stubbing = Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthBlock.class)));
        for (EthBlock ethBlock : ethBlocks) {
            stubbing = stubbing.thenReturn(ethBlock);
        }
        Flowable<EthBlock> flowable = web3j.replayPastBlocksFlowable(new DefaultBlockParameterNumber(BigInteger.ZERO), new DefaultBlockParameterNumber(BigInteger.valueOf(2)), false);
        CountDownLatch transactionLatch = new CountDownLatch(ethBlocks.size());
        CountDownLatch completedLatch = new CountDownLatch(1);
        List<EthBlock> results = new java.util.ArrayList(ethBlocks.size());
        Disposable subscription = flowable.subscribe(( result) -> {
            results.add(result);
            transactionLatch.countDown();
        }, ( throwable) -> fail(throwable.getMessage()), () -> completedLatch.countDown());
        transactionLatch.await(1, TimeUnit.SECONDS);
        Assert.assertThat(results, CoreMatchers.equalTo(ethBlocks));
        subscription.dispose();
        completedLatch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(subscription.isDisposed());
    }

    @Test
    public void testReplayBlocksDescendingFlowable() throws Exception {
        List<EthBlock> ethBlocks = Arrays.asList(createBlock(2), createBlock(1), createBlock(0));
        OngoingStubbing<EthBlock> stubbing = Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthBlock.class)));
        for (EthBlock ethBlock : ethBlocks) {
            stubbing = stubbing.thenReturn(ethBlock);
        }
        Flowable<EthBlock> flowable = web3j.replayPastBlocksFlowable(new DefaultBlockParameterNumber(BigInteger.ZERO), new DefaultBlockParameterNumber(BigInteger.valueOf(2)), false, false);
        CountDownLatch transactionLatch = new CountDownLatch(ethBlocks.size());
        CountDownLatch completedLatch = new CountDownLatch(1);
        List<EthBlock> results = new java.util.ArrayList(ethBlocks.size());
        Disposable subscription = flowable.subscribe(( result) -> {
            results.add(result);
            transactionLatch.countDown();
        }, ( throwable) -> fail(throwable.getMessage()), () -> completedLatch.countDown());
        transactionLatch.await(1, TimeUnit.SECONDS);
        Assert.assertThat(results, CoreMatchers.equalTo(ethBlocks));
        subscription.dispose();
        completedLatch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(subscription.isDisposed());
    }

    @Test
    public void testReplayPastBlocksFlowable() throws Exception {
        List<EthBlock> expected = Arrays.asList(createBlock(0), createBlock(1), createBlock(2), createBlock(3), createBlock(4));
        List<EthBlock> ethBlocks = // greatest block
        // greatest block
        Arrays.asList(expected.get(2), expected.get(0), expected.get(1), expected.get(2), expected.get(4), expected.get(3), expected.get(4), expected.get(4));// greatest block

        OngoingStubbing<EthBlock> stubbing = Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthBlock.class)));
        for (EthBlock ethBlock : ethBlocks) {
            stubbing = stubbing.thenReturn(ethBlock);
        }
        EthFilter ethFilter = objectMapper.readValue(("{\n" + ((("  \"id\":1,\n" + "  \"jsonrpc\": \"2.0\",\n") + "  \"result\": \"0x1\"\n") + "}")), EthFilter.class);
        EthLog ethLog = objectMapper.readValue(("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":[" + ("\"0x31c2342b1e0b8ffda1507fbffddf213c4b3c1e819ff6a84b943faabb0ebf2403\"" + "]}")), EthLog.class);
        EthUninstallFilter ethUninstallFilter = objectMapper.readValue("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":true}", EthUninstallFilter.class);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthFilter.class))).thenReturn(ethFilter);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthLog.class))).thenReturn(ethLog);
        Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthUninstallFilter.class))).thenReturn(ethUninstallFilter);
        Flowable<EthBlock> flowable = web3j.replayPastBlocksFlowable(new DefaultBlockParameterNumber(BigInteger.ZERO), false);
        CountDownLatch transactionLatch = new CountDownLatch(expected.size());
        CountDownLatch completedLatch = new CountDownLatch(1);
        List<EthBlock> results = new java.util.ArrayList(expected.size());
        Disposable subscription = flowable.subscribe(( result) -> {
            results.add(result);
            transactionLatch.countDown();
        }, ( throwable) -> fail(throwable.getMessage()), () -> completedLatch.countDown());
        transactionLatch.await(1250, TimeUnit.MILLISECONDS);
        Assert.assertThat(results, CoreMatchers.equalTo(expected));
        subscription.dispose();
        completedLatch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(subscription.isDisposed());
    }

    @Test
    public void testReplayTransactionsFlowable() throws Exception {
        List<EthBlock> ethBlocks = Arrays.asList(createBlockWithTransactions(0, Arrays.asList(createTransaction("0x1234"), createTransaction("0x1235"), createTransaction("0x1236"))), createBlockWithTransactions(1, Arrays.asList(createTransaction("0x2234"), createTransaction("0x2235"), createTransaction("0x2236"))), createBlockWithTransactions(2, Arrays.asList(createTransaction("0x3234"), createTransaction("0x3235"))));
        OngoingStubbing<EthBlock> stubbing = Mockito.when(web3jService.send(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(EthBlock.class)));
        for (EthBlock ethBlock : ethBlocks) {
            stubbing = stubbing.thenReturn(ethBlock);
        }
        List<Transaction> expectedTransactions = ethBlocks.stream().flatMap(( it) -> it.getBlock().getTransactions().stream()).map(( it) -> ((Transaction) (it.get()))).collect(Collectors.toList());
        Flowable<Transaction> flowable = web3j.replayPastTransactionsFlowable(new DefaultBlockParameterNumber(BigInteger.ZERO), new DefaultBlockParameterNumber(BigInteger.valueOf(2)));
        CountDownLatch transactionLatch = new CountDownLatch(expectedTransactions.size());
        CountDownLatch completedLatch = new CountDownLatch(1);
        List<Transaction> results = new java.util.ArrayList(expectedTransactions.size());
        Disposable subscription = flowable.subscribe(( result) -> {
            results.add(result);
            transactionLatch.countDown();
        }, ( throwable) -> fail(throwable.getMessage()), () -> completedLatch.countDown());
        transactionLatch.await(1, TimeUnit.SECONDS);
        Assert.assertThat(results, CoreMatchers.equalTo(expectedTransactions));
        subscription.dispose();
        completedLatch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(subscription.isDisposed());
    }
}

