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
package org.apache.camel.component.olingo4;


import Constants.METADATA;
import HttpStatusCode.NOT_FOUND;
import HttpStatusCode.NO_CONTENT;
import Operation.CREATE;
import Operation.DELETE;
import Operation.UPDATE;
import SystemQueryOptionKind.EXPAND;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.olingo4.api.Olingo4App;
import org.apache.camel.component.olingo4.api.Olingo4ResponseHandler;
import org.apache.camel.component.olingo4.api.batch.Olingo4BatchChangeRequest;
import org.apache.camel.component.olingo4.api.batch.Olingo4BatchQueryRequest;
import org.apache.camel.component.olingo4.api.batch.Olingo4BatchRequest;
import org.apache.camel.component.olingo4.api.batch.Olingo4BatchResponse;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.serialization.ODataReader;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration test for
 * {@link org.apache.camel.component.olingo4.api.impl.Olingo4AppImpl} using the
 * sample OData 4.0 remote TripPin service published on
 * http://services.odata.org/TripPinRESTierService.
 */
public class Olingo4AppAPITest {
    private static final Logger LOG = LoggerFactory.getLogger(Olingo4AppAPITest.class);

    private static final long TIMEOUT = 10;

    private static final String PEOPLE = "People";

    private static final String TEST_PEOPLE = "People('russellwhyte')";

    private static final String TEST_AIRLINE = "Airlines('FM')";

    private static final String TRIPS = "Trips";

    private static final String TEST_CREATE_RESOURCE_CONTENT_ID = "1";

    private static final String TEST_UPDATE_RESOURCE_CONTENT_ID = "2";

    private static final String TEST_CREATE_KEY = "'lewisblack'";

    private static final String TEST_CREATE_PEOPLE = (((Olingo4AppAPITest.PEOPLE) + "(") + (Olingo4AppAPITest.TEST_CREATE_KEY)) + ")";

    private static final String TEST_AIRPORT = "Airports('KSFO')";

    private static final String TEST_AIRPORTS_SIMPLE_PROPERTY = (Olingo4AppAPITest.TEST_AIRPORT) + "/Name";

    private static final String TEST_AIRPORTS_COMPLEX_PROPERTY = (Olingo4AppAPITest.TEST_AIRPORT) + "/Location";

    private static final String TEST_AIRPORTS_SIMPLE_PROPERTY_VALUE = (Olingo4AppAPITest.TEST_AIRPORTS_SIMPLE_PROPERTY) + "/$value";

    private static final String COUNT_OPTION = "/$count";

    private static final String TEST_SERVICE_BASE_URL = "http://services.odata.org/TripPinRESTierService";

    private static final ContentType TEST_FORMAT = ContentType.APPLICATION_JSON;

    private static final String TEST_FORMAT_STRING = Olingo4AppAPITest.TEST_FORMAT.toString();

    private static Olingo4App olingoApp;

    private static Edm edm;

    private final ODataClient odataClient = ODataClientFactory.getClient();

    private final ClientObjectFactory objFactory = odataClient.getObjectFactory();

    private final ODataReader reader = odataClient.getReader();

    @Test
    public void testServiceDocument() throws Exception {
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<ClientServiceDocument> responseHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.read(null, "", null, null, responseHandler);
        final ClientServiceDocument serviceDocument = responseHandler.await();
        final Map<String, URI> entitySets = serviceDocument.getEntitySets();
        Assert.assertEquals("Service Entity Sets", 4, entitySets.size());
        Olingo4AppAPITest.LOG.info("Service Document Entries:  {}", entitySets);
    }

    @Test
    public void testReadEntitySet() throws Exception {
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<ClientEntitySet> responseHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.PEOPLE, null, null, responseHandler);
        final ClientEntitySet entitySet = responseHandler.await();
        Assert.assertNotNull(entitySet);
        Assert.assertEquals("Entity set count", 20, entitySet.getEntities().size());
        Olingo4AppAPITest.LOG.info("Entities:  {}", Olingo4AppAPITest.prettyPrint(entitySet));
    }

    @Test
    public void testReadUnparsedEntitySet() throws Exception {
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<InputStream> responseHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.uread(Olingo4AppAPITest.edm, Olingo4AppAPITest.PEOPLE, null, null, responseHandler);
        final InputStream rawEntitySet = responseHandler.await();
        Assert.assertNotNull("Data entity set", rawEntitySet);
        final ClientEntitySet entitySet = reader.readEntitySet(rawEntitySet, Olingo4AppAPITest.TEST_FORMAT);
        Assert.assertEquals("Entity set count", 20, entitySet.getEntities().size());
        Olingo4AppAPITest.LOG.info("Entries:  {}", Olingo4AppAPITest.prettyPrint(entitySet));
    }

    @Test
    public void testReadEntity() throws Exception {
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<ClientEntity> responseHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_AIRLINE, null, null, responseHandler);
        ClientEntity entity = responseHandler.await();
        Assert.assertEquals("Shanghai Airline", entity.getProperty("Name").getValue().toString());
        Olingo4AppAPITest.LOG.info("Single Entity:  {}", Olingo4AppAPITest.prettyPrint(entity));
        responseHandler.reset();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_PEOPLE, null, null, responseHandler);
        entity = responseHandler.await();
        Assert.assertEquals("Russell", entity.getProperty("FirstName").getValue().toString());
        Olingo4AppAPITest.LOG.info("Single Entry:  {}", Olingo4AppAPITest.prettyPrint(entity));
        responseHandler.reset();
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put(EXPAND.toString(), Olingo4AppAPITest.TRIPS);
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_PEOPLE, queryParams, null, responseHandler);
        ClientEntity entityExpanded = responseHandler.await();
        Olingo4AppAPITest.LOG.info("Single People Entiry with expanded Trips relation:  {}", Olingo4AppAPITest.prettyPrint(entityExpanded));
    }

    @Test
    public void testReadUnparsedEntity() throws Exception {
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<InputStream> responseHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.uread(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_AIRLINE, null, null, responseHandler);
        InputStream rawEntity = responseHandler.await();
        Assert.assertNotNull("Data entity", rawEntity);
        ClientEntity entity = reader.readEntity(rawEntity, Olingo4AppAPITest.TEST_FORMAT);
        Assert.assertEquals("Shanghai Airline", entity.getProperty("Name").getValue().toString());
        Olingo4AppAPITest.LOG.info("Single Entity:  {}", Olingo4AppAPITest.prettyPrint(entity));
        responseHandler.reset();
        Olingo4AppAPITest.olingoApp.uread(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_PEOPLE, null, null, responseHandler);
        rawEntity = responseHandler.await();
        entity = reader.readEntity(rawEntity, Olingo4AppAPITest.TEST_FORMAT);
        Assert.assertEquals("Russell", entity.getProperty("FirstName").getValue().toString());
        Olingo4AppAPITest.LOG.info("Single Entity:  {}", Olingo4AppAPITest.prettyPrint(entity));
        responseHandler.reset();
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put(EXPAND.toString(), Olingo4AppAPITest.TRIPS);
        Olingo4AppAPITest.olingoApp.uread(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_PEOPLE, queryParams, null, responseHandler);
        rawEntity = responseHandler.await();
        entity = reader.readEntity(rawEntity, Olingo4AppAPITest.TEST_FORMAT);
        Olingo4AppAPITest.LOG.info("Single People Entiry with expanded Trips relation:  {}", Olingo4AppAPITest.prettyPrint(entity));
    }

    @Test
    public void testReadUpdateProperties() throws Exception {
        // test simple property Airports.Name
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<ClientPrimitiveValue> propertyHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_AIRPORTS_SIMPLE_PROPERTY, null, null, propertyHandler);
        ClientPrimitiveValue name = propertyHandler.await();
        Assert.assertEquals("San Francisco International Airport", name.toString());
        Olingo4AppAPITest.LOG.info("Airport name property value {}", name.asPrimitive());
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<ClientPrimitiveValue> valueHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_AIRPORTS_SIMPLE_PROPERTY_VALUE, null, null, valueHandler);
        ClientPrimitiveValue nameValue = valueHandler.await();
        Assert.assertEquals("San Francisco International Airport", name.toString());
        Olingo4AppAPITest.LOG.info("Airport name property value {}", nameValue);
        Olingo4AppAPITest.TestOlingo4ResponseHandler<HttpStatusCode> statusHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        // All properties updates (simple and complex) are performing through
        // ClientEntity object
        ClientEntity clientEntity = objFactory.newEntity(null);
        clientEntity.getProperties().add(objFactory.newPrimitiveProperty("MiddleName", objFactory.newPrimitiveValueBuilder().buildString("Middle")));
        Olingo4AppAPITest.olingoApp.update(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_PEOPLE, null, clientEntity, statusHandler);
        HttpStatusCode statusCode = statusHandler.await();
        Assert.assertEquals(NO_CONTENT, statusCode);
        Olingo4AppAPITest.LOG.info("Name property updated with status {}", statusCode.getStatusCode());
        // Check for updated property by reading entire entity
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<ClientEntity> responseHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_PEOPLE, null, null, responseHandler);
        ClientEntity entity = responseHandler.await();
        Assert.assertEquals("Middle", entity.getProperty("MiddleName").getValue().toString());
        Olingo4AppAPITest.LOG.info("Updated Single Entity:  {}", Olingo4AppAPITest.prettyPrint(entity));
    }

    @Test
    public void testReadCount() throws Exception {
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<Long> countHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, ((Olingo4AppAPITest.PEOPLE) + (Olingo4AppAPITest.COUNT_OPTION)), null, null, countHandler);
        Long count = countHandler.await();
        Assert.assertEquals(20, count.intValue());
        Olingo4AppAPITest.LOG.info("People count: {}", count);
    }

    @Test
    public void testCreateUpdateDeleteEntity() throws Exception {
        // create an entity to update
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<ClientEntity> entryHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.create(Olingo4AppAPITest.edm, Olingo4AppAPITest.PEOPLE, null, createEntity(), entryHandler);
        ClientEntity createdEntity = entryHandler.await();
        Olingo4AppAPITest.LOG.info("Created Entity:  {}", Olingo4AppAPITest.prettyPrint(createdEntity));
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<HttpStatusCode> statusHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        ClientEntity updateEntity = createEntity();
        updateEntity.getProperties().add(objFactory.newPrimitiveProperty("MiddleName", objFactory.newPrimitiveValueBuilder().buildString("Middle")));
        Olingo4AppAPITest.olingoApp.update(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_CREATE_PEOPLE, null, updateEntity, statusHandler);
        statusHandler.await();
        statusHandler.reset();
        updateEntity = createEntity();
        updateEntity.getProperties().add(objFactory.newPrimitiveProperty("MiddleName", objFactory.newPrimitiveValueBuilder().buildString("Middle Patched")));
        Olingo4AppAPITest.olingoApp.patch(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_CREATE_PEOPLE, null, updateEntity, statusHandler);
        statusHandler.await();
        entryHandler.reset();
        Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_CREATE_PEOPLE, null, null, entryHandler);
        ClientEntity updatedEntity = entryHandler.await();
        Olingo4AppAPITest.LOG.info("Updated Entity successfully:  {}", Olingo4AppAPITest.prettyPrint(updatedEntity));
        statusHandler.reset();
        Olingo4AppAPITest.olingoApp.delete(Olingo4AppAPITest.TEST_CREATE_PEOPLE, null, statusHandler);
        HttpStatusCode statusCode = statusHandler.await();
        Olingo4AppAPITest.LOG.info("Deletion of Entity was successful:  {}: {}", statusCode.getStatusCode(), statusCode.getInfo());
        try {
            Olingo4AppAPITest.LOG.info("Verify Delete Entity");
            entryHandler.reset();
            Olingo4AppAPITest.olingoApp.read(Olingo4AppAPITest.edm, Olingo4AppAPITest.TEST_CREATE_PEOPLE, null, null, entryHandler);
            entryHandler.await();
            Assert.fail("Entity not deleted!");
        } catch (Exception e) {
            Olingo4AppAPITest.LOG.info("Deleted entity not found: {}", e.getMessage());
        }
    }

    @Test
    public void testBatchRequest() throws Exception {
        final List<Olingo4BatchRequest> batchParts = new ArrayList<>();
        // 1. Edm query
        batchParts.add(Olingo4BatchQueryRequest.resourcePath(METADATA).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).build());
        // 2. Query entity set
        batchParts.add(Olingo4BatchQueryRequest.resourcePath(Olingo4AppAPITest.PEOPLE).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).build());
        // 3. Read entity
        batchParts.add(Olingo4BatchQueryRequest.resourcePath(Olingo4AppAPITest.TEST_PEOPLE).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).build());
        // 4. Read with expand
        final HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(EXPAND.toString(), Olingo4AppAPITest.TRIPS);
        batchParts.add(Olingo4BatchQueryRequest.resourcePath(Olingo4AppAPITest.TEST_PEOPLE).queryParams(queryParams).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).build());
        // 5. Create entity
        final ClientEntity clientEntity = createEntity();
        batchParts.add(Olingo4BatchChangeRequest.resourcePath(Olingo4AppAPITest.PEOPLE).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).contentId(Olingo4AppAPITest.TEST_CREATE_RESOURCE_CONTENT_ID).operation(CREATE).body(clientEntity).build());
        // 6. Update entity
        clientEntity.getProperties().add(objFactory.newPrimitiveProperty("MiddleName", objFactory.newPrimitiveValueBuilder().buildString("Lewis")));
        batchParts.add(Olingo4BatchChangeRequest.resourcePath(Olingo4AppAPITest.TEST_CREATE_PEOPLE).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).contentId(Olingo4AppAPITest.TEST_UPDATE_RESOURCE_CONTENT_ID).operation(UPDATE).body(clientEntity).build());
        // 7. Delete entity
        batchParts.add(Olingo4BatchChangeRequest.resourcePath(Olingo4AppAPITest.TEST_CREATE_PEOPLE).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).operation(DELETE).build());
        // 8. Read to verify entity delete
        batchParts.add(Olingo4BatchQueryRequest.resourcePath(Olingo4AppAPITest.TEST_CREATE_PEOPLE).resourceUri(Olingo4AppAPITest.TEST_SERVICE_BASE_URL).build());
        final Olingo4AppAPITest.TestOlingo4ResponseHandler<List<Olingo4BatchResponse>> responseHandler = new Olingo4AppAPITest.TestOlingo4ResponseHandler<>();
        Olingo4AppAPITest.olingoApp.batch(Olingo4AppAPITest.edm, null, batchParts, responseHandler);
        final List<Olingo4BatchResponse> responseParts = responseHandler.await(15, TimeUnit.MINUTES);
        Assert.assertEquals("Batch responses expected", 8, responseParts.size());
        Assert.assertNotNull(responseParts.get(0).getBody());
        final ClientEntitySet clientEntitySet = ((ClientEntitySet) (responseParts.get(1).getBody()));
        Assert.assertNotNull(clientEntitySet);
        Olingo4AppAPITest.LOG.info("Batch entity set:  {}", Olingo4AppAPITest.prettyPrint(clientEntitySet));
        ClientEntity returnClientEntity = ((ClientEntity) (responseParts.get(2).getBody()));
        Assert.assertNotNull(returnClientEntity);
        Olingo4AppAPITest.LOG.info("Batch read entity:  {}", Olingo4AppAPITest.prettyPrint(returnClientEntity));
        returnClientEntity = ((ClientEntity) (responseParts.get(3).getBody()));
        Assert.assertNotNull(returnClientEntity);
        Olingo4AppAPITest.LOG.info("Batch read entity with expand:  {}", Olingo4AppAPITest.prettyPrint(returnClientEntity));
        ClientEntity createdClientEntity = ((ClientEntity) (responseParts.get(4).getBody()));
        Assert.assertNotNull(createdClientEntity);
        Assert.assertEquals(Olingo4AppAPITest.TEST_CREATE_RESOURCE_CONTENT_ID, responseParts.get(4).getContentId());
        Olingo4AppAPITest.LOG.info("Batch created entity:  {}", Olingo4AppAPITest.prettyPrint(returnClientEntity));
        Assert.assertEquals(NO_CONTENT.getStatusCode(), responseParts.get(5).getStatusCode());
        Assert.assertEquals(Olingo4AppAPITest.TEST_UPDATE_RESOURCE_CONTENT_ID, responseParts.get(5).getContentId());
        Assert.assertEquals(NO_CONTENT.getStatusCode(), responseParts.get(6).getStatusCode());
        Assert.assertEquals(NOT_FOUND.getStatusCode(), responseParts.get(7).getStatusCode());
    }

    private static final class TestOlingo4ResponseHandler<T> implements Olingo4ResponseHandler<T> {
        private T response;

        private Exception error;

        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onResponse(T response, Map<String, String> responseHeaders) {
            this.response = response;
            if (Olingo4AppAPITest.LOG.isDebugEnabled()) {
                if (response instanceof ClientEntitySet) {
                    Olingo4AppAPITest.LOG.debug("Received response: {}", Olingo4AppAPITest.prettyPrint(((ClientEntitySet) (response))));
                } else
                    if (response instanceof ClientEntity) {
                        Olingo4AppAPITest.LOG.debug("Received response: {}", Olingo4AppAPITest.prettyPrint(((ClientEntity) (response))));
                    } else {
                        Olingo4AppAPITest.LOG.debug("Received response: {}", response);
                    }

            }
            latch.countDown();
        }

        @Override
        public void onException(Exception ex) {
            error = ex;
            latch.countDown();
        }

        @Override
        public void onCanceled() {
            error = new IllegalStateException("Request Canceled");
            latch.countDown();
        }

        public T await() throws Exception {
            return await(Olingo4AppAPITest.TIMEOUT, TimeUnit.SECONDS);
        }

        public T await(long timeout, TimeUnit unit) throws Exception {
            Assert.assertTrue("Timeout waiting for response", latch.await(timeout, unit));
            if ((error) != null) {
                throw error;
            }
            Assert.assertNotNull("Response", response);
            return response;
        }

        public void reset() {
            latch.countDown();
            latch = new CountDownLatch(1);
            response = null;
            error = null;
        }
    }
}

