/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.web3j.integration;


import EthLog.LogResult;
import ShhMessages.SshMessage;
import java.math.BigInteger;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.web3j.Web3jConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.ShhMessages;
import org.web3j.protocol.core.methods.response.Transaction;


@Ignore("Requires a locally running Ganache instance")
public class Web3jProducerGanacheTest extends Web3jIntegrationTestSupport {
    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    public void ethClientVersionTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.WEB3_CLIENT_VERSION);
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethNetWeb3Sha3Test() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.WEB3_SHA3);
        exchange.getIn().setBody("0x68656c6c6f20776f726c64");
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue(body.equals("0x47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"));
    }

    @Test
    public void ethNetVersionTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.NET_VERSION);
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethNetListeningTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.NET_LISTENING);
        template.send(exchange);
        Boolean body = exchange.getIn().getBody(Boolean.class);
        assertTrue((body != null));
    }

    @Test
    public void ethProtocolVersionTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_PROTOCOL_VERSION);
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethSyncingTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_SYNCING);
        template.send(exchange);
        Boolean body = exchange.getIn().getBody(Boolean.class);
        assertTrue((body != null));
    }

    @Test
    public void ethCoinbaseTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_COINBASE);
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethMiningTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_MINING);
        template.send(exchange);
        Boolean body = exchange.getIn().getBody(Boolean.class);
        assertTrue((body != null));
    }

    @Test
    public void ethHashrateTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_HASHRATE);
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGasPriceTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GAS_PRICE);
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    @Test
    public void ethAccountsTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_ACCOUNTS);
        template.send(exchange);
        List<String> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }

    @Test
    public void ethBlockNumberTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_BLOCK_NUMBER);
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetBalanceTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_BALANCE);
        exchange.getIn().setHeader(Web3jConstants.ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(Web3jConstants.AT_BLOCK, "0");
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    // Given this contract created at address 0x3B558E3a9ae7944FEe7a3A1010DD10f05a01034B:
    // pragma solidity ^0.4.23;
    // contract Storage {
    // uint pos0;
    // function Storage() {
    // pos0 = 5;
    // }
    // }
    @Test
    public void ethGetStorageAtTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_STORAGE_AT);
        exchange.getIn().setHeader(Web3jConstants.ADDRESS, "0x3B558E3a9ae7944FEe7a3A1010DD10f05a01034B");
        exchange.getIn().setHeader(Web3jConstants.AT_BLOCK, "6");
        exchange.getIn().setHeader(Web3jConstants.POSITION, BigInteger.ZERO);
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetTransactionCountTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_TRANSACTION_COUNT);
        exchange.getIn().setHeader(Web3jConstants.ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(Web3jConstants.AT_BLOCK, "latest");
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetCodeTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_CODE);
        exchange.getIn().setHeader(Web3jConstants.ADDRESS, "0x3B558E3a9ae7944FEe7a3A1010DD10f05a01034B");
        exchange.getIn().setHeader(Web3jConstants.AT_BLOCK, "latest");
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethSignTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_SIGN);
        exchange.getIn().setHeader(Web3jConstants.ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(Web3jConstants.SHA3_HASH_OF_DATA_TO_SIGN, "hello");
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethSendTransactionTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_SEND_TRANSACTION);
        exchange.getIn().setHeader(Web3jConstants.FROM_ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(Web3jConstants.TO_ADDRESS, "0x883E97F42e3cfC2b233DC684574F33B96a0329C4");
        exchange.getIn().setHeader(Web3jConstants.NONCE, BigInteger.valueOf(9L));
        exchange.getIn().setHeader(Web3jConstants.GAS_PRICE, BigInteger.valueOf(10000000000000L));
        exchange.getIn().setHeader(Web3jConstants.GAS_LIMIT, BigInteger.valueOf(30400L));
        exchange.getIn().setHeader(Web3jConstants.VALUE, BigInteger.valueOf(50000000000000L));
        // String data = message.getHeader(Web3jConstants.DATA, configuration::getData, String.class);
        // 
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethCallTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_CALL);
        exchange.getIn().setHeader(Web3jConstants.FROM_ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(Web3jConstants.TO_ADDRESS, "0x3B558E3a9ae7944FEe7a3A1010DD10f05a01034B");
        exchange.getIn().setHeader(Web3jConstants.NONCE, BigInteger.valueOf(9L));
        exchange.getIn().setHeader(Web3jConstants.GAS_PRICE, BigInteger.valueOf(10000000000000L));
        exchange.getIn().setHeader(Web3jConstants.GAS_LIMIT, BigInteger.valueOf(30400L));
        exchange.getIn().setHeader(Web3jConstants.VALUE, BigInteger.valueOf(50000000000000L));
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethEstimateGasTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_ESTIMATE_GAS);
        exchange.getIn().setHeader(Web3jConstants.FROM_ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(Web3jConstants.TO_ADDRESS, "0x3B558E3a9ae7944FEe7a3A1010DD10f05a01034B");
        exchange.getIn().setHeader(Web3jConstants.NONCE, BigInteger.valueOf(9L));
        exchange.getIn().setHeader(Web3jConstants.GAS_PRICE, BigInteger.valueOf(10000000000000L));
        exchange.getIn().setHeader(Web3jConstants.GAS_LIMIT, BigInteger.valueOf(30400L));
        exchange.getIn().setHeader(Web3jConstants.VALUE, BigInteger.valueOf(50000000000000L));
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetBlockByHashTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_BLOCK_BY_HASH);
        exchange.getIn().setHeader(Web3jConstants.FULL_TRANSACTION_OBJECTS, true);
        exchange.getIn().setHeader(Web3jConstants.BLOCK_HASH, "0x1fab3a1cc7f016029e41e72363362caf9bd09388ba94070d6ada37b8757ab19a");
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetBlockByNumberTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_BLOCK_BY_NUMBER);
        exchange.getIn().setHeader(Web3jConstants.FULL_TRANSACTION_OBJECTS, true);
        exchange.getIn().setHeader(Web3jConstants.AT_BLOCK, "latest");
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetTransactionByHashTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_TRANSACTION_BY_HASH);
        exchange.getIn().setHeader(Web3jConstants.TRANSACTION_HASH, "0xb082f44cb2faa0f33056d5a341d1a7be73ecfcc6eb3bcb643ab03016ce4b6772");
        template.send(exchange);
        Transaction body = exchange.getIn().getBody(Transaction.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetTransactionByBlockHashAndIndexTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_TRANSACTION_BY_BLOCK_HASH_AND_INDEX);
        exchange.getIn().setHeader(Web3jConstants.BLOCK_HASH, "0x226aa81c5a7c86caff96af0bdb58739491d4730b629932ca80f3530558282e1d");
        exchange.getIn().setHeader(Web3jConstants.INDEX, BigInteger.ZERO);
        template.send(exchange);
        Transaction body = exchange.getIn().getBody(Transaction.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetTransactionByBlockNumberAndIndexTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_TRANSACTION_BY_BLOCK_NUMBER_AND_INDEX);
        exchange.getIn().setHeader(Web3jConstants.AT_BLOCK, "latest");
        exchange.getIn().setHeader(Web3jConstants.INDEX, BigInteger.ZERO);
        template.send(exchange);
        Transaction body = exchange.getIn().getBody(Transaction.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetCompilers() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_COMPILERS);
        template.send(exchange);
        List<String> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }

    @Test
    public void ethNewFilterTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_NEW_FILTER);
        exchange.getIn().setHeader(Web3jConstants.FROM_BLOCK, "earliest");
        exchange.getIn().setHeader(Web3jConstants.TO_BLOCK, "latest");
        exchange.getIn().setHeader(Web3jConstants.ADDRESSES, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(Web3jConstants.TOPICS, "0x000000000000000000000000a94f5374fce5edbc8e2a8697c15331677e6ebf0b");
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    @Test
    public void ethNewBlockFilterTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_NEW_BLOCK_FILTER);
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    @Test
    public void ethNewPendingTransactionFilterTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_NEW_PENDING_TRANSACTION_FILTER);
        template.send(exchange);
        BigInteger body = exchange.getIn().getBody(BigInteger.class);
        assertTrue((body != null));
    }

    @Test
    public void ethUninstallFilterTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_UNINSTALL_FILTER);
        exchange.getIn().setHeader(Web3jConstants.FILTER_ID, BigInteger.valueOf(8));
        template.send(exchange);
        Boolean body = exchange.getIn().getBody(Boolean.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetFilterChangesTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_FILTER_CHANGES);
        exchange.getIn().setHeader(Web3jConstants.FILTER_ID, BigInteger.valueOf(7));
        template.send(exchange);
        List<EthLog.LogResult> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetFilterLogsTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_FILTER_LOGS);
        exchange.getIn().setHeader(Web3jConstants.FILTER_ID, BigInteger.valueOf(6));
        template.send(exchange);
        List<EthLog.LogResult> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetLogsTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_LOGS);
        exchange.getIn().setHeader(Web3jConstants.FROM_BLOCK, "earliest");
        exchange.getIn().setHeader(Web3jConstants.TO_BLOCK, "latest");
        // exchange.getIn().setHeader(ADDRESSES, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        // exchange.getIn().setHeader(TOPICS, "0x000000000000000000000000a94f5374fce5edbc8e2a8697c15331677e6ebf0b");
        template.send(exchange);
        List<EthLog.LogResult> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }

    @Test
    public void ethGetWorkTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_GET_WORK);
        template.send(exchange);
        List<String> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }

    @Test
    public void ethSubmitWorkTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_SUBMIT_WORK);
        exchange.getIn().setHeader(Web3jConstants.NONCE, "0x0000000000000001");
        exchange.getIn().setHeader(Web3jConstants.HEADER_POW_HASH, "0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef");
        exchange.getIn().setHeader(Web3jConstants.MIX_DIGEST, "0xD1FE5700000000000000000000000000D1FE5700000000000000000000000000");
        template.send(exchange);
        Boolean body = exchange.getIn().getBody(Boolean.class);
        assertTrue((body != null));
    }

    @Test
    public void ethSubmitHashrateTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.ETH_SUBMIT_HASHRATE);
        exchange.getIn().setHeader(Web3jConstants.ETH_HASHRATE, "0x0000000000000000000000000000000000000000000000000000000000500000");
        exchange.getIn().setHeader(Web3jConstants.CLIENT_ID, "0x59daa26581d0acd1fce254fb7e85952f4c09d0915afd33d3886cd914bc7d283c");
        template.send(exchange);
        Boolean body = exchange.getIn().getBody(Boolean.class);
        assertTrue((body != null));
    }

    @Test
    public void shhVersionTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.SHH_VERSION);
        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue((body != null));
    }

    @Test
    public void shhGetFilterChangesTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.SHH_GET_FILTER_CHANGES);
        exchange.getIn().setHeader(Web3jConstants.FILTER_ID, BigInteger.valueOf(123));
        template.send(exchange);
        List<ShhMessages.SshMessage> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }

    @Test
    public void shhGetMessagesTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, Web3jConstants.OPERATION, Web3jConstants.SHH_GET_MESSAGES);
        exchange.getIn().setHeader(Web3jConstants.FILTER_ID, BigInteger.valueOf(123));
        template.send(exchange);
        List<ShhMessages.SshMessage> body = exchange.getIn().getBody(List.class);
        assertTrue((body != null));
    }
}

