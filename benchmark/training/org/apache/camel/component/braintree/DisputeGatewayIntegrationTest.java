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
import com.braintreegateway.Dispute;
import com.braintreegateway.DisputeEvidence;
import com.braintreegateway.DisputeSearchRequest;
import com.braintreegateway.DocumentUpload;
import com.braintreegateway.FileEvidenceRequest;
import com.braintreegateway.PaginatedCollection;
import com.braintreegateway.Result;
import com.braintreegateway.TextEvidenceRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.braintree.internal.BraintreeApiCollection;
import org.apache.camel.component.braintree.internal.DisputeGatewayApiMethod;
import org.apache.camel.component.braintree.internal.DocumentUploadGatewayApiMethod;
import org.apache.camel.component.braintree.internal.TransactionGatewayApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DisputeGatewayIntegrationTest extends AbstractBraintreeTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DisputeGatewayIntegrationTest.class);

    private static final String PATH_PREFIX = BraintreeApiCollection.getCollection().getApiName(DisputeGatewayApiMethod.class).getName();

    private static final String TRANSACTION_PATH_PREFIX = BraintreeApiCollection.getCollection().getApiName(TransactionGatewayApiMethod.class).getName();

    private static final String DOCUMENT_UPLOAD_PATH_PREFIX = BraintreeApiCollection.getCollection().getApiName(DocumentUploadGatewayApiMethod.class).getName();

    private BraintreeGateway gateway;

    @Test
    public void testAccept() throws Exception {
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        final Result result = requestBody("direct://ACCEPT", createdDispute.getId());
        assertNotNull("accept result", result);
        assertTrue("accept result success", result.isSuccess());
        final Dispute finalizedDispute = requestBody("direct://FIND", createdDispute.getId());
        assertNotNull("accepted dispute", finalizedDispute);
        assertEquals(Dispute.Status.ACCEPTED, finalizedDispute.getStatus());
    }

    @Test
    public void testAddFileEvidence() throws Exception {
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        DocumentUpload uploadedDocument = uploadDocument();
        final Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("CamelBraintree.disputeId", createdDispute.getId());
        headers.put("CamelBraintree.documentId", uploadedDocument.getId());
        final Result<DisputeEvidence> result = requestBodyAndHeaders("direct://ADDFILEEVIDENCE", null, headers);
        assertNotNull("addFileEvidence result", result);
        assertTrue("addFileEvidence result success", result.isSuccess());
    }

    @Test
    public void testAddFileEvidenceOne() throws Exception {
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        DocumentUpload uploadedDocument = uploadDocument();
        final Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("CamelBraintree.disputeId", createdDispute.getId());
        FileEvidenceRequest fileEvidenceRequest = new FileEvidenceRequest().documentId(uploadedDocument.getId());
        headers.put("CamelBraintree.fileEvidenceRequest", fileEvidenceRequest);
        final Result<DisputeEvidence> result = requestBodyAndHeaders("direct://ADDFILEEVIDENCE_1", null, headers);
        assertNotNull("addFileEvidence result", result);
        assertTrue("addFileEvidence result success", result.isSuccess());
    }

    @Test
    public void testAddTextEvidence() throws Exception {
        final String textEvidence = "Text Evidence";
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        final Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("CamelBraintree.id", createdDispute.getId());
        headers.put("CamelBraintree.content", textEvidence);
        final Result<DisputeEvidence> result = requestBodyAndHeaders("direct://ADDTEXTEVIDENCE", null, headers);
        assertNotNull("addTextEvidence result", result);
        assertTrue("addTextEvidence result success", result.isSuccess());
        DisputeEvidence disputeEvidence = result.getTarget();
        assertEquals(textEvidence, disputeEvidence.getComment());
    }

    @Test
    public void testAddTextEvidenceOne() throws Exception {
        final String textEvidence = "Text Evidence";
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        final Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("CamelBraintree.id", createdDispute.getId());
        TextEvidenceRequest textEvidenceRequest = new TextEvidenceRequest().content(textEvidence);
        headers.put("CamelBraintree.textEvidenceRequest", textEvidenceRequest);
        final Result<DisputeEvidence> result = requestBodyAndHeaders("direct://ADDTEXTEVIDENCE_1", null, headers);
        assertNotNull("addTextEvidence result", result);
        assertTrue("addTextEvidence result success", result.isSuccess());
        DisputeEvidence disputeEvidence = result.getTarget();
        assertEquals(textEvidence, disputeEvidence.getComment());
    }

    @Test
    public void testFinalize() throws Exception {
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        final Result result = requestBody("direct://FINALIZE", createdDispute.getId());
        assertNotNull("finalize result", result);
        assertTrue("finalize result success", result.isSuccess());
        final Dispute finalizedDispute = requestBody("direct://FIND", createdDispute.getId());
        assertNotNull("finalized dispute", finalizedDispute);
        assertEquals(Dispute.Status.DISPUTED, finalizedDispute.getStatus());
    }

    @Test
    public void testFind() throws Exception {
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        final Dispute foundDispute = requestBody("direct://FIND", createdDispute.getId());
        assertNotNull("found dispute", foundDispute);
        assertEquals(Dispute.Status.OPEN, foundDispute.getStatus());
    }

    @Test
    public void testRemoveEvidence() throws Exception {
        final String textEvidence = "Text Evidence";
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        final Map<String, Object> addTextEvidenceHeaders = new HashMap<String, Object>();
        addTextEvidenceHeaders.put("CamelBraintree.id", createdDispute.getId());
        addTextEvidenceHeaders.put("CamelBraintree.content", textEvidence);
        final Result<DisputeEvidence> addTextEvidenceResult = requestBodyAndHeaders("direct://ADDTEXTEVIDENCE", null, addTextEvidenceHeaders);
        assertNotNull("addTextEvidence result", addTextEvidenceResult);
        assertTrue("addTextEvidence result success", addTextEvidenceResult.isSuccess());
        DisputeEvidence disputeEvidence = addTextEvidenceResult.getTarget();
        assertEquals(textEvidence, disputeEvidence.getComment());
        final Map<String, Object> removeTextEvidenceHeaders = new HashMap<String, Object>();
        removeTextEvidenceHeaders.put("CamelBraintree.disputeId", createdDispute.getId());
        removeTextEvidenceHeaders.put("CamelBraintree.evidenceId", disputeEvidence.getId());
        final Result removeTextEvidenceResult = requestBodyAndHeaders("direct://REMOVEEVIDENCE", null, removeTextEvidenceHeaders);
        assertNotNull("removeEvidence result", removeTextEvidenceResult);
        assertTrue("removeEvidence result success", removeTextEvidenceResult.isSuccess());
    }

    @Test
    public void testSearch() throws Exception {
        Dispute createdDispute = createDispute();
        assertEquals(Dispute.Status.OPEN, createdDispute.getStatus());
        DisputeSearchRequest query = new DisputeSearchRequest().id().is(createdDispute.getId());
        final PaginatedCollection<Dispute> result = requestBody("direct://SEARCH", query);
        assertNotNull("search result", result);
        for (Dispute foundDispute : result) {
            assertEquals(createdDispute.getId(), foundDispute.getId());
        }
    }
}

