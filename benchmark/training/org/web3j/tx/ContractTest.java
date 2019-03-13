package org.web3j.tx;


import Contract.EventValuesWithLog;
import Contract.GAS_LIMIT;
import ManagedTransaction.GAS_PRICE;
import SampleKeys.CREDENTIALS;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Async;

import static Contract.BIN_NOT_PROVIDED;


public class ContractTest extends ManagedTransactionTester {
    private static final String TEST_CONTRACT_BINARY = "12345";

    private ContractTest.TestContract contract;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetContractAddress() {
        Assert.assertThat(getContractAddress(), CoreMatchers.is(ManagedTransactionTester.ADDRESS));
    }

    @Test
    public void testGetContractTransactionReceipt() {
        Assert.assertFalse(getTransactionReceipt().isPresent());
    }

    @Test
    public void testDeploy() throws Exception {
        TransactionReceipt transactionReceipt = createTransactionReceipt();
        Contract deployedContract = deployContract(transactionReceipt);
        Assert.assertThat(deployedContract.getContractAddress(), CoreMatchers.is(ManagedTransactionTester.ADDRESS));
        Assert.assertTrue(deployedContract.getTransactionReceipt().isPresent());
        Assert.assertThat(deployedContract.getTransactionReceipt().get(), CoreMatchers.equalTo(transactionReceipt));
    }

    @Test
    public void testContractDeployFails() throws Exception {
        thrown.expect(TransactionException.class);
        thrown.expectMessage("Transaction has failed with status: 0x0. Gas used: 1. (not-enough gas?)");
        TransactionReceipt transactionReceipt = createFailedTransactionReceipt();
        deployContract(transactionReceipt);
    }

    @Test
    public void testContractDeployWithNullStatusSucceeds() throws Exception {
        TransactionReceipt transactionReceipt = createTransactionReceiptWithStatus(null);
        Contract deployedContract = deployContract(transactionReceipt);
        Assert.assertThat(deployedContract.getContractAddress(), CoreMatchers.is(ManagedTransactionTester.ADDRESS));
        Assert.assertTrue(deployedContract.getTransactionReceipt().isPresent());
        Assert.assertThat(deployedContract.getTransactionReceipt().get(), CoreMatchers.equalTo(transactionReceipt));
    }

    @Test
    public void testIsValid() throws Exception {
        prepareEthGetCode(ContractTest.TEST_CONTRACT_BINARY);
        Contract contract = deployContract(createTransactionReceipt());
        Assert.assertTrue(contract.isValid());
    }

    @Test
    public void testIsValidDifferentCode() throws Exception {
        prepareEthGetCode(((ContractTest.TEST_CONTRACT_BINARY) + "0"));
        Contract contract = deployContract(createTransactionReceipt());
        Assert.assertFalse(contract.isValid());
    }

    @Test
    public void testIsValidEmptyCode() throws Exception {
        prepareEthGetCode("");
        Contract contract = deployContract(createTransactionReceipt());
        Assert.assertFalse(contract.isValid());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsValidNoBinThrows() throws Exception {
        TransactionManager txManager = Mockito.mock(TransactionManager.class);
        ContractTest.TestContract contract = new ContractTest.TestContract(BIN_NOT_PROVIDED, ManagedTransactionTester.ADDRESS, web3j, txManager, new DefaultGasProvider());
        isValid();
    }

    @Test(expected = RuntimeException.class)
    public void testDeployInvalidContractAddress() throws Throwable {
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setTransactionHash(ManagedTransactionTester.TRANSACTION_HASH);
        prepareTransaction(transactionReceipt);
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(BigInteger.TEN)));
        try {
            ContractTest.TestContract.deployRemoteCall(ContractTest.TestContract.class, web3j, CREDENTIALS, GAS_PRICE, GAS_LIMIT, "0xcafed00d", encodedConstructor, BigInteger.ZERO).send();
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testCallSingleValue() throws Exception {
        // Example taken from FunctionReturnDecoderTest
        EthCall ethCall = new EthCall();
        ethCall.setResult(("0x0000000000000000000000000000000000000000000000000000000000000020" + "0000000000000000000000000000000000000000000000000000000000000000"));
        prepareCall(ethCall);
        Assert.assertThat(contract.callSingleValue().send(), CoreMatchers.equalTo(new Utf8String("")));
    }

    @Test
    public void testCallSingleValueEmpty() throws Exception {
        // Example taken from FunctionReturnDecoderTest
        EthCall ethCall = new EthCall();
        ethCall.setResult("0x");
        prepareCall(ethCall);
        Assert.assertNull(contract.callSingleValue().send());
    }

    @Test
    public void testCallMultipleValue() throws Exception {
        EthCall ethCall = new EthCall();
        ethCall.setResult(("0x0000000000000000000000000000000000000000000000000000000000000037" + "0000000000000000000000000000000000000000000000000000000000000007"));
        prepareCall(ethCall);
        Assert.assertThat(contract.callMultipleValue().send(), CoreMatchers.equalTo(Arrays.asList(new Uint256(BigInteger.valueOf(55)), new Uint256(BigInteger.valueOf(7)))));
    }

    @Test
    public void testCallMultipleValueEmpty() throws Exception {
        EthCall ethCall = new EthCall();
        ethCall.setResult("0x");
        prepareCall(ethCall);
        Assert.assertThat(contract.callMultipleValue().send(), CoreMatchers.equalTo(Collections.emptyList()));
    }

    @Test
    public void testTransaction() throws Exception {
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setTransactionHash(ManagedTransactionTester.TRANSACTION_HASH);
        transactionReceipt.setStatus("0x1");
        prepareTransaction(transactionReceipt);
        Assert.assertThat(contract.performTransaction(new Address(BigInteger.TEN), new Uint256(BigInteger.ONE)).send(), CoreMatchers.is(transactionReceipt));
    }

    @Test
    public void testTransactionFailed() throws Exception {
        thrown.expect(TransactionException.class);
        thrown.expectMessage("Transaction has failed with status: 0x0. Gas used: 1. (not-enough gas?)");
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setTransactionHash(ManagedTransactionTester.TRANSACTION_HASH);
        transactionReceipt.setStatus("0x0");
        transactionReceipt.setGasUsed("0x1");
        prepareTransaction(transactionReceipt);
        contract.performTransaction(new Address(BigInteger.TEN), new Uint256(BigInteger.ONE)).send();
    }

    @Test
    public void testProcessEvent() {
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        Log log = new Log();
        log.setTopics(// encoded function
        // indexed value
        Arrays.asList("0xfceb437c298f40d64702ac26411b2316e79f3c28ffa60edfc891ad4fc8ab82ca", "0000000000000000000000003d6cb163f7c72d20b0fcd6baae5889329d138a4a"));
        // non-indexed value
        log.setData("0000000000000000000000000000000000000000000000000000000000000001");
        transactionReceipt.setLogs(Arrays.asList(log));
        EventValues eventValues = contract.processEvent(transactionReceipt).get(0);
        Assert.assertThat(eventValues.getIndexedValues(), CoreMatchers.equalTo(Collections.singletonList(new Address("0x3d6cb163f7c72d20b0fcd6baae5889329d138a4a"))));
        Assert.assertThat(eventValues.getNonIndexedValues(), CoreMatchers.equalTo(Collections.singletonList(new Uint256(BigInteger.ONE))));
    }

    @Test
    public void testProcessEventForLogWithoutTopics() {
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        final Log log = new Log();
        log.setTopics(Collections.emptyList());
        // non-indexed value
        log.setData("0000000000000000000000000000000000000000000000000000000000000001");
        transactionReceipt.setLogs(Arrays.asList(log));
        final List<EventValues> eventValues = contract.processEvent(transactionReceipt);
        Assert.assertTrue("No events expected", eventValues.isEmpty());
    }

    @Test(expected = TransactionException.class)
    public void testTimeout() throws Throwable {
        prepareTransaction(null);
        TransactionManager transactionManager = getVerifiedTransactionManager(CREDENTIALS, 1, 1);
        contract = new ContractTest.TestContract(ManagedTransactionTester.ADDRESS, web3j, transactionManager, new DefaultGasProvider());
        testErrorScenario();
    }

    @Test(expected = RuntimeException.class)
    @SuppressWarnings("unchecked")
    public void testInvalidTransactionResponse() throws Throwable {
        prepareNonceRequest();
        EthSendTransaction ethSendTransaction = new EthSendTransaction();
        ethSendTransaction.setError(new Response.Error(1, "Invalid transaction"));
        Request<?, EthSendTransaction> rawTransactionRequest = Mockito.mock(Request.class);
        Mockito.when(rawTransactionRequest.sendAsync()).thenReturn(Async.run(() -> ethSendTransaction));
        Mockito.when(web3j.ethSendRawTransaction(ArgumentMatchers.any(String.class))).thenReturn(((Request) (rawTransactionRequest)));
        testErrorScenario();
    }

    @Test
    public void testSetGetAddresses() throws Exception {
        Assert.assertNull(getDeployedAddress("1"));
        setDeployedAddress("1", "0x000000000000add0e00000000000");
        TestCase.assertNotNull(getDeployedAddress("1"));
        setDeployedAddress("2", "0x000000000000add0e00000000000");
        TestCase.assertNotNull(getDeployedAddress("2"));
    }

    @Test
    public void testSetGetGasPrice() {
        Assert.assertEquals(GAS_PRICE, getGasPrice());
        BigInteger newPrice = GAS_PRICE.multiply(BigInteger.valueOf(2));
        setGasPrice(newPrice);
        Assert.assertEquals(newPrice, getGasPrice());
    }

    @Test
    public void testStaticGasProvider() throws IOException, TransactionException {
        StaticGasProvider gasProvider = new StaticGasProvider(BigInteger.TEN, BigInteger.ONE);
        TransactionManager txManager = Mockito.mock(TransactionManager.class);
        Mockito.when(txManager.executeTransaction(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new TransactionReceipt());
        contract = new ContractTest.TestContract(ManagedTransactionTester.ADDRESS, web3j, txManager, gasProvider);
        Function func = new Function("test", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        contract.executeTransaction(func);
        Mockito.verify(txManager).executeTransaction(ArgumentMatchers.eq(BigInteger.TEN), ArgumentMatchers.eq(BigInteger.ONE), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test(expected = RuntimeException.class)
    @SuppressWarnings("unchecked")
    public void testInvalidTransactionReceipt() throws Throwable {
        prepareNonceRequest();
        prepareTransactionRequest();
        EthGetTransactionReceipt ethGetTransactionReceipt = new EthGetTransactionReceipt();
        ethGetTransactionReceipt.setError(new Response.Error(1, "Invalid transaction receipt"));
        Request<?, EthGetTransactionReceipt> getTransactionReceiptRequest = Mockito.mock(Request.class);
        Mockito.when(getTransactionReceiptRequest.sendAsync()).thenReturn(Async.run(() -> ethGetTransactionReceipt));
        Mockito.when(web3j.ethGetTransactionReceipt(ManagedTransactionTester.TRANSACTION_HASH)).thenReturn(((Request) (getTransactionReceiptRequest)));
        testErrorScenario();
    }

    @Test
    public void testExtractEventParametersWithLogGivenATransactionReceipt() {
        final java.util.function.Function<String, Event> eventFactory = ( name) -> new Event(name, Collections.emptyList());
        final BiFunction<Integer, Event, Log> logFactory = ( logIndex, event) -> new Log(false, ("" + logIndex), "0", "0x0", "0x0", "0", ("0x" + logIndex), "", "", Collections.singletonList(EventEncoder.encode(event)));
        final Event testEvent1 = eventFactory.apply("TestEvent1");
        final Event testEvent2 = eventFactory.apply("TestEvent2");
        final List<Log> logs = Arrays.asList(logFactory.apply(0, testEvent1), logFactory.apply(1, testEvent2));
        final TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setLogs(logs);
        final List<Contract.EventValuesWithLog> eventValuesWithLogs1 = contract.extractEventParametersWithLog(testEvent1, transactionReceipt);
        Assert.assertEquals(eventValuesWithLogs1.size(), 1);
        Assert.assertEquals(eventValuesWithLogs1.get(0).getLog(), logs.get(0));
        final List<Contract.EventValuesWithLog> eventValuesWithLogs2 = contract.extractEventParametersWithLog(testEvent2, transactionReceipt);
        Assert.assertEquals(eventValuesWithLogs2.size(), 1);
        Assert.assertEquals(eventValuesWithLogs2.get(0).getLog(), logs.get(1));
    }

    private static class TestContract extends Contract {
        public TestContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
            super(ContractTest.TEST_CONTRACT_BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
        }

        public TestContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
            this(ContractTest.TEST_CONTRACT_BINARY, contractAddress, web3j, transactionManager, gasProvider);
        }

        public TestContract(String binary, String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
            super(binary, contractAddress, web3j, transactionManager, gasProvider);
        }

        public RemoteCall<Utf8String> callSingleValue() {
            Function function = new Function("call", Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
            return executeRemoteCallSingleValueReturn(function);
        }

        public RemoteCall<List<Type>> callMultipleValue() throws InterruptedException, ExecutionException {
            Function function = new Function("call", Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
            return executeRemoteCallMultipleValueReturn(function);
        }

        public RemoteCall<TransactionReceipt> performTransaction(Address address, Uint256 amount) {
            Function function = new Function("approve", Arrays.<Type>asList(address, amount), Collections.<TypeReference<?>>emptyList());
            return executeRemoteCallTransaction(function);
        }

        public List<EventValues> processEvent(TransactionReceipt transactionReceipt) {
            Event event = new Event("Event", Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
            return extractEventParameters(event, transactionReceipt);
        }
    }
}

