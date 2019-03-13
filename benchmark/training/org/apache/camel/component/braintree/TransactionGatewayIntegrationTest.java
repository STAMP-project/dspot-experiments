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
package org.apache.camel.component.braintree;


import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionCloneRequest;
import com.braintreegateway.TransactionRefundRequest;
import com.braintreegateway.TransactionRequest;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.apache.camel.component.braintree.internal.BraintreeApiCollection;
import org.apache.camel.component.braintree.internal.TransactionGatewayApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransactionGatewayIntegrationTest extends AbstractBraintreeTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionGatewayIntegrationTest.class);

    private static final String PATH_PREFIX = BraintreeApiCollection.getCollection().getApiName(TransactionGatewayApiMethod.class).getName();

    private BraintreeGateway gateway;

    private final List<String> transactionIds;

    // *************************************************************************
    // 
    // *************************************************************************
    public TransactionGatewayIntegrationTest() {
        this.gateway = null;
        this.transactionIds = new LinkedList<>();
    }

    // *************************************************************************
    // 
    // *************************************************************************
    @Test
    public void testSale() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> result = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(true).done(), Result.class);
        assertNotNull("sale result", result);
        assertTrue(result.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info("Transaction done - id={}", result.getTarget().getId());
        this.transactionIds.add(result.getTarget().getId());
    }

    @Test
    public void testCloneTransaction() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(false).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info("Transaction done - id={}", createResult.getTarget().getId());
        this.transactionIds.add(createResult.getTarget().getId());
        final Result<Transaction> cloneResult = requestBodyAndHeaders("direct://CLONETRANSACTION", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("id", createResult.getTarget().getId()).add("cloneRequest", new TransactionCloneRequest().amount(new BigDecimal("99.00")).options().submitForSettlement(true).done()).build(), Result.class);
        assertNotNull("clone result", cloneResult);
        assertTrue(cloneResult.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info("Clone Transaction done - clonedId={}, id={}", createResult.getTarget().getId(), cloneResult.getTarget().getId());
        this.transactionIds.add(cloneResult.getTarget().getId());
    }

    @Test
    public void testFind() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(false).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info("Transaction done - id={}", createResult.getTarget().getId());
        this.transactionIds.add(createResult.getTarget().getId());
        // using String message body for single parameter "id"
        final Transaction result = requestBody("direct://FIND", createResult.getTarget().getId());
        assertNotNull("find result", result);
        TransactionGatewayIntegrationTest.LOG.info("Transaction found - id={}", result.getId());
    }

    @Test
    public void testSubmitForSettlementWithId() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(false).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info("Transaction done - id={}", createResult.getTarget().getId());
        this.transactionIds.add(createResult.getTarget().getId());
        final Result<Transaction> result = requestBody("direct://SUBMITFORSETTLEMENT_WITH_ID", createResult.getTarget().getId(), Result.class);
        assertNotNull("Submit For Settlement result", result);
        TransactionGatewayIntegrationTest.LOG.debug("Transaction submitted for settlement - id={}", result.getTarget().getId());
    }

    @Test
    public void testSubmitForSettlementWithIdAndAmount() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(false).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info("Transaction done - id={}", createResult.getTarget().getId());
        this.transactionIds.add(createResult.getTarget().getId());
        final Result<Transaction> result = requestBodyAndHeaders("direct://SUBMITFORSETTLEMENT_WITH_ID_ADN_AMOUNT", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("id", createResult.getTarget().getId()).add("amount", new BigDecimal("100.00")).build(), Result.class);
        assertNotNull("Submit For Settlement result", result);
        TransactionGatewayIntegrationTest.LOG.debug("Transaction submitted for settlement - id={}", result.getTarget().getId());
    }

    @Test
    public void testSubmitForSettlementWithRequest() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(false).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info("Transaction done - id={}", createResult.getTarget().getId());
        this.transactionIds.add(createResult.getTarget().getId());
        final Result<Transaction> result = requestBodyAndHeaders("direct://SUBMITFORSETTLEMENT_WITH_REQUEST", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("id", createResult.getTarget().getId()).add("request", new TransactionRequest().amount(new BigDecimal("100.00"))).build(), Result.class);
        assertNotNull("Submit For Settlement result", result);
        TransactionGatewayIntegrationTest.LOG.debug("Transaction submitted for settlement - id={}", result.getTarget().getId());
    }

    @Test
    public void testRefund() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(true).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        String createId = createResult.getTarget().getId();
        final Result<Transaction> settleResult = this.gateway.testing().settle(createId);
        assertNotNull("settle result", settleResult);
        assertTrue(settleResult.isSuccess());
        final Result<Transaction> result = requestBody("direct://REFUND_WITH_ID", createId, Result.class);
        assertNotNull("Request Refund result", result);
        assertTrue(result.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info(String.format("Refund id(%s) created for transaction id(%s)", result.getTarget().getId(), createId));
    }

    @Test
    public void testRefundWithAmount() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(true).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        String createId = createResult.getTarget().getId();
        final Result<Transaction> settleResult = this.gateway.testing().settle(createId);
        assertNotNull("settle result", settleResult);
        assertTrue(settleResult.isSuccess());
        final Result<Transaction> result = requestBodyAndHeaders("direct://REFUND", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("id", createId).add("amount", new BigDecimal("99.00")).build(), Result.class);
        assertNotNull("Request Refund result", result);
        assertTrue(result.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info(String.format("Refund id(%s) created for transaction id(%s)", result.getTarget().getId(), createId));
    }

    @Test
    public void testRefundWithRequest() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        final Result<Transaction> createResult = requestBody("direct://SALE", new TransactionRequest().amount(new BigDecimal("100.00")).paymentMethodNonce("fake-valid-nonce").options().submitForSettlement(true).done(), Result.class);
        assertNotNull("sale result", createResult);
        assertTrue(createResult.isSuccess());
        String createId = createResult.getTarget().getId();
        final Result<Transaction> settleResult = this.gateway.testing().settle(createId);
        assertNotNull("settle result", settleResult);
        assertTrue(settleResult.isSuccess());
        final Result<Transaction> result = requestBodyAndHeaders("direct://REFUND", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("id", createId).add("refundRequest", new TransactionRefundRequest().amount(new BigDecimal("100.00"))).build(), Result.class);
        assertNotNull("Request Refund result", result);
        assertTrue(result.isSuccess());
        TransactionGatewayIntegrationTest.LOG.info(String.format("Refund id(%s) created for transaction id(%s)", result.getTarget().getId(), createId));
    }
}

