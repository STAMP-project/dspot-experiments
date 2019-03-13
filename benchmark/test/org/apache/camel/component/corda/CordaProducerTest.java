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
package org.apache.camel.component.corda;


import AttachmentQueryCriteria.AttachmentsQueryCriteria;
import QueryCriteria.VaultQueryCriteria;
import SecureHash.SHA256;
import Sort.Direction;
import Sort.LinearStateAttribute;
import Sort.SortColumn;
import Vault.StateStatus;
import com.google.common.collect.ImmutableSet;
import java.io.InputStream;
import java.security.PublicKey;
import net.corda.core.contracts.OwnableState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.FlowLogic;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.AttachmentQueryCriteria;
import net.corda.core.node.services.vault.AttachmentSort;
import net.corda.core.node.services.vault.EqualityComparisonOperator;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.Sort;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("This integration test requires a locally running corda node such cordapp-template-java")
public class CordaProducerTest extends CordaTestSupport {
    private static final SHA256 TEST_SHA_256 = SecureHash.parse("6D1687C143DF792A011A1E80670A4E4E0C25D0D87A39514409B1ABFC2043581F");

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    public void currentNodeTimeTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.CURRENT_NODE_TIME);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void getProtocolVersionTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.GET_PROTOCOL_VERSION);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void networkMapSnapshotTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.NETWORK_MAP_SNAPSHOT);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void stateMachinesSnapshotTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.STATE_MACHINE_SNAPSHOT);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void stateMachineRecordedTransactionMappingSnapshotTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.STATE_MACHINE_RECORDED_TRANSACTION_MAPPING_SNAPSHOT);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void registeredFlowsTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.REGISTERED_FLOWS);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void clearNetworkMapCacheTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.CLEAR_NETWORK_MAP_CACHE);
        template.send(exchange);
        Object body = exchange.getException();
        assertNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void isFlowsDrainingModeEnabledTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.IS_FLOWS_DRAINING_MODE_ENABLED);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void setFlowsDrainingModeEnabledTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.SET_FLOWS_DRAINING_MODE_ENABLED);
        exchange.getIn().setHeader(CordaConstants.DRAINING_MODE, false);
        template.send(exchange);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void notaryIdentitiesTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.NOTARY_IDENTITIES);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void nodeInfoTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.NODE_INFO);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void addVaultTransactionNoteTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader("Some note", CordaConstants.OPERATION, CordaConstants.ADD_VAULT_TRANSACTION_NOTE);
        exchange.getIn().setHeader(CordaConstants.SECURE_HASH, CordaProducerTest.TEST_SHA_256);
        template.send(exchange);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void getVaultTransactionNotesTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.GET_VAULT_TRANSACTION_NOTES);
        exchange.getIn().setHeader(CordaConstants.SECURE_HASH, CordaProducerTest.TEST_SHA_256);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void uploadAttachmentTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.UPLOAD_ATTACHMENT);
        exchange.getIn().setBody(zipIt(("HELLO" + (System.nanoTime())), "test1.txt"));
        template.send(exchange);
        Object body = exchange.getIn().getHeader(CordaConstants.SECURE_HASH);
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void attachmentExistsTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.ATTACHMENT_EXISTS);
        exchange.getIn().setHeader(CordaConstants.SECURE_HASH, CordaProducerTest.TEST_SHA_256);
        template.send(exchange);
        Boolean body = exchange.getIn().getBody(Boolean.class);
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void openAttachmentTest() throws Exception {
        // Setup node with attachment
        Exchange uploadExchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.UPLOAD_ATTACHMENT);
        uploadExchange.getIn().setBody(zipIt(("HELLO" + (System.nanoTime())), "test2.txt"));
        template.send(uploadExchange);
        Object hash = uploadExchange.getIn().getHeader(CordaConstants.SECURE_HASH);
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.OPEN_ATTACHMENT);
        exchange.getIn().setHeader(CordaConstants.SECURE_HASH, hash);
        template.send(exchange);
        InputStream body = exchange.getIn().getBody(InputStream.class);
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void queryAttachmentsTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.QUERY_ATTACHMENTS);
        AttachmentQueryCriteria.AttachmentsQueryCriteria queryCriteria = new AttachmentQueryCriteria.AttachmentsQueryCriteria(new net.corda.core.node.services.vault.ColumnPredicate.EqualityComparison(EqualityComparisonOperator.EQUAL, "Daredevil"));
        AttachmentSort attachmentSort = null;
        exchange.getIn().setHeader(CordaConstants.ATTACHMENT_QUERY_CRITERIA, queryCriteria);
        exchange.getIn().setHeader(CordaConstants.SORT, attachmentSort);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void nodeInfoFromPartyTest() throws Exception {
        // Expects IntegrationWhiteList is deployed on the node
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.NODE_INFO_FROM_PARTY);
        PublicKey pub = generatePublicKey();
        CordaX500Name cordaX500Name1 = new CordaX500Name("PartyA", "London", "GB");
        Party party = new Party(cordaX500Name1, pub);
        exchange.getIn().setBody(party);
        template.send(exchange);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void notaryPartyFromX500NameTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.NOTARY_PARTY_FROM_X500_NAME);
        CordaX500Name cordaX500Name = new CordaX500Name("Notary", "London", "GB");
        exchange.getIn().setBody(cordaX500Name);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void partiesFromNameTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader("A", CordaConstants.OPERATION, CordaConstants.PARTIES_FROM_NAME);
        exchange.getIn().setHeader(CordaConstants.EXACT_MATCH, false);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void partyFromKeyTest() throws Exception {
        // Expects IntegrationWhiteList is deployed on the node
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.PARTIES_FROM_KEY);
        PublicKey pub = generatePublicKey();
        exchange.getIn().setBody(pub);
        template.send(exchange);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void wellKnownPartyFromX500NameTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.WELL_KNOWN_PARTY_FROM_X500_NAME);
        CordaX500Name cordaX500Name1 = new CordaX500Name("PartyA", "London", "GB");
        exchange.getIn().setBody(cordaX500Name1);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void wellKnownPartyFromAnonymousTest() throws Exception {
        // Expects IntegrationWhiteList is deployed on the node
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.WELL_KNOWN_PARTY_FROM_ANONYMOUS);
        PublicKey pub = generatePublicKey();
        AbstractParty party = new AnonymousParty(pub);
        exchange.getIn().setBody(party);
        template.send(exchange);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void startFlowDynamicTest() throws Exception {
        // Expects CamelFlow is deployed on the node
        Exchange exchange = createExchangeWithBodyAndHeader(null, CordaConstants.OPERATION, CordaConstants.START_FLOW_DYNAMIC);
        String[] args = new String[]{ "Hello" };
        Class<FlowLogic<String>> aClass = ((Class<FlowLogic<String>>) (Class.forName("org.apache.camel.component.corda.CamelFlow")));
        exchange.getIn().setBody(aClass);
        exchange.getIn().setHeader(CordaConstants.ARGUMENTS, args);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        assertEquals("Hello world!", body.toString());
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void vaultQueryTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(OwnableState.class, CordaConstants.OPERATION, CordaConstants.VAULT_QUERY);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void vaultQueryByTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(OwnableState.class, CordaConstants.OPERATION, CordaConstants.VAULT_QUERY_BY);
        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(StateStatus.CONSUMED);
        PageSpecification pageSpec = new PageSpecification(DEFAULT_PAGE_NUM, MAX_PAGE_SIZE);
        Sort.SortColumn sortByUid = new Sort.SortColumn(new net.corda.core.node.services.vault.SortAttribute.Standard(LinearStateAttribute.UUID), Direction.DESC);
        Sort sorting = new Sort(ImmutableSet.of(sortByUid));
        exchange.getIn().setHeader(CordaConstants.QUERY_CRITERIA, criteria);
        exchange.getIn().setHeader(CordaConstants.PAGE_SPECIFICATION, pageSpec);
        exchange.getIn().setHeader(CordaConstants.SORT, sorting);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void vaultQueryByCriteriaTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(OwnableState.class, CordaConstants.OPERATION, CordaConstants.VAULT_QUERY_BY_CRITERIA);
        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(StateStatus.CONSUMED);
        exchange.getIn().setHeader(CordaConstants.QUERY_CRITERIA, criteria);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void vaultQueryByWithPagingSpecTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(OwnableState.class, CordaConstants.OPERATION, CordaConstants.VAULT_QUERY_BY_WITH_PAGING_SPEC);
        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(StateStatus.CONSUMED);
        PageSpecification pageSpec = new PageSpecification(DEFAULT_PAGE_NUM, MAX_PAGE_SIZE);
        exchange.getIn().setHeader(CordaConstants.QUERY_CRITERIA, criteria);
        exchange.getIn().setHeader(CordaConstants.PAGE_SPECIFICATION, pageSpec);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }

    @Test
    public void vaultQueryByWithSortingTest() throws Exception {
        Exchange exchange = createExchangeWithBodyAndHeader(OwnableState.class, CordaConstants.OPERATION, CordaConstants.VAULT_QUERY_BY_WITH_SORTING);
        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(StateStatus.CONSUMED);
        Sort.SortColumn sortByUid = new Sort.SortColumn(new net.corda.core.node.services.vault.SortAttribute.Standard(LinearStateAttribute.UUID), Direction.DESC);
        Sort sorting = new Sort(ImmutableSet.of(sortByUid));
        exchange.getIn().setHeader(CordaConstants.QUERY_CRITERIA, criteria);
        exchange.getIn().setHeader(CordaConstants.SORT, sorting);
        template.send(exchange);
        Object body = exchange.getIn().getBody();
        assertNotNull(body);
        Object exception = exchange.getException();
        assertNull(exception);
    }
}

