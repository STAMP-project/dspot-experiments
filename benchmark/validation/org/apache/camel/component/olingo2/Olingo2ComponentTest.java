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
package org.apache.camel.component.olingo2;


import HttpStatusCodes.NOT_FOUND;
import HttpStatusCodes.NO_CONTENT;
import Olingo2AppImpl.METADATA;
import Operation.CREATE;
import Operation.DELETE;
import Operation.UPDATE;
import SystemQueryOption.;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchChangeRequest;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchQueryRequest;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchRequest;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchResponse;
import org.apache.camel.component.olingo2.internal.Olingo2Constants;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.servicedocument.ServiceDocument;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link org.apache.camel.component.olingo2.api.Olingo2App}
 * APIs.
 * <p>
 * The integration test runs against Apache Olingo 2.0 sample server which is
 * dynamically installed and started during the test.
 * </p>
 */
public class Olingo2ComponentTest extends AbstractOlingo2TestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(Olingo2ComponentTest.class);

    private static final int PORT = AvailablePortFinder.getNextAvailable();

    private static final String ID_PROPERTY = "Id";

    private static final String MANUFACTURERS = "Manufacturers";

    private static final String TEST_MANUFACTURER = "Manufacturers('1')";

    private static final String CARS = "Cars";

    private static final String TEST_RESOURCE_CONTENT_ID = "1";

    private static final String ADDRESS = "Address";

    private static final String TEST_RESOURCE = "$1";

    private static final String TEST_RESOURCE_ADDRESS = (Olingo2ComponentTest.TEST_RESOURCE) + "/Address";

    private static final String TEST_CREATE_MANUFACTURER = "DefaultContainer.Manufacturers('123')";

    private static final String TEST_SERVICE_URL = ("http://localhost:" + (Olingo2ComponentTest.PORT)) + "/MyFormula.svc";

    private static Olingo2SampleServer server;

    public Olingo2ComponentTest() {
        setDefaultTestProperty("serviceUri", (("http://localhost:" + (Olingo2ComponentTest.PORT)) + "/MyFormula.svc"));
    }

    @Test
    public void testRead() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // read ServiceDocument
        final ServiceDocument document = requestBodyAndHeaders("direct:READSERVICEDOC", null, headers);
        assertNotNull(document);
        assertFalse("ServiceDocument entity sets", document.getEntitySetsInfo().isEmpty());
        Olingo2ComponentTest.LOG.info("Service document has {} entity sets", document.getEntitySetsInfo().size());
        // parameter type is java.util.Map
        final HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(.top.name(), "5");
        headers.put("CamelOlingo2.queryParams", queryParams);
        // read ODataFeed
        final ODataFeed manufacturers = requestBodyAndHeaders("direct:READFEED", null, headers);
        assertNotNull(manufacturers);
        final List<ODataEntry> manufacturersEntries = manufacturers.getEntries();
        assertFalse("Manufacturers empty entries", manufacturersEntries.isEmpty());
        Olingo2ComponentTest.LOG.info("Manufacturers feed has {} entries", manufacturersEntries.size());
        // read ODataEntry
        headers.clear();
        headers.put(((Olingo2Constants.PROPERTY_PREFIX) + "keyPredicate"), "'1'");
        final ODataEntry manufacturer = requestBodyAndHeaders("direct:READENTRY", null, headers);
        assertNotNull(manufacturer);
        final Map<String, Object> properties = manufacturer.getProperties();
        assertEquals("Manufacturer Id", "1", properties.get(Olingo2ComponentTest.ID_PROPERTY));
        Olingo2ComponentTest.LOG.info("Manufacturer: {}", properties);
    }

    @Test
    public void testCreateUpdateDelete() throws Exception {
        final Map<String, Object> data = getEntityData();
        Map<String, Object> address;
        final ODataEntry manufacturer = requestBody("direct:CREATE", data);
        assertNotNull("Created Manufacturer", manufacturer);
        final Map<String, Object> properties = manufacturer.getProperties();
        assertEquals("Created Manufacturer Id", "123", properties.get(Olingo2ComponentTest.ID_PROPERTY));
        Olingo2ComponentTest.LOG.info("Created Manufacturer: {}", properties);
        // update
        data.put("Name", "MyCarManufacturer Renamed");
        address = ((Map<String, Object>) (data.get("Address")));
        address.put("Street", "Main Street");
        HttpStatusCodes status = requestBody("direct:UPDATE", data);
        assertNotNull("Update status", status);
        assertEquals("Update status", NO_CONTENT.getStatusCode(), status.getStatusCode());
        Olingo2ComponentTest.LOG.info("Update status: {}", status);
        // delete
        status = requestBody("direct:DELETE", null);
        assertNotNull("Delete status", status);
        assertEquals("Delete status", NO_CONTENT.getStatusCode(), status.getStatusCode());
        Olingo2ComponentTest.LOG.info("Delete status: {}", status);
    }

    @Test
    public void testBatch() throws Exception {
        final List<Olingo2BatchRequest> batchParts = new ArrayList<>();
        // 1. Edm query
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(METADATA).build());
        // 2. feed query
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2ComponentTest.MANUFACTURERS).build());
        // 3. read
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2ComponentTest.TEST_MANUFACTURER).build());
        // 4. read with expand
        final HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(.expand.toString(), Olingo2ComponentTest.CARS);
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2ComponentTest.TEST_MANUFACTURER).queryParams(queryParams).build());
        // 5. create
        final Map<String, Object> data = getEntityData();
        batchParts.add(Olingo2BatchChangeRequest.resourcePath(Olingo2ComponentTest.MANUFACTURERS).contentId(Olingo2ComponentTest.TEST_RESOURCE_CONTENT_ID).operation(CREATE).body(data).build());
        // 6. update address in created entry
        final Map<String, Object> updateData = new HashMap<>(data);
        Map<String, Object> address = ((Map<String, Object>) (updateData.get(Olingo2ComponentTest.ADDRESS)));
        address.put("Street", "Main Street");
        batchParts.add(Olingo2BatchChangeRequest.resourcePath(Olingo2ComponentTest.TEST_RESOURCE_ADDRESS).operation(UPDATE).body(address).build());
        // 7. update
        updateData.put("Name", "MyCarManufacturer Renamed");
        batchParts.add(Olingo2BatchChangeRequest.resourcePath(Olingo2ComponentTest.TEST_RESOURCE).operation(UPDATE).body(updateData).build());
        // 8. delete
        batchParts.add(Olingo2BatchChangeRequest.resourcePath(Olingo2ComponentTest.TEST_RESOURCE).operation(DELETE).build());
        // 9. read to verify delete
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2ComponentTest.TEST_CREATE_MANUFACTURER).build());
        // execute batch request
        final List<Olingo2BatchResponse> responseParts = requestBody("direct:BATCH", batchParts);
        assertNotNull("Batch response", responseParts);
        assertEquals("Batch responses expected", 9, responseParts.size());
        final Edm edm = ((Edm) (responseParts.get(0).getBody()));
        assertNotNull(edm);
        Olingo2ComponentTest.LOG.info("Edm entity sets: {}", edm.getEntitySets());
        final ODataFeed feed = ((ODataFeed) (responseParts.get(1).getBody()));
        assertNotNull(feed);
        Olingo2ComponentTest.LOG.info("Read feed: {}", feed.getEntries());
        ODataEntry dataEntry = ((ODataEntry) (responseParts.get(2).getBody()));
        assertNotNull(dataEntry);
        Olingo2ComponentTest.LOG.info("Read entry: {}", dataEntry.getProperties());
        dataEntry = ((ODataEntry) (responseParts.get(3).getBody()));
        assertNotNull(dataEntry);
        Olingo2ComponentTest.LOG.info("Read entry with $expand: {}", dataEntry.getProperties());
        dataEntry = ((ODataEntry) (responseParts.get(4).getBody()));
        assertNotNull(dataEntry);
        Olingo2ComponentTest.LOG.info("Created entry: {}", dataEntry.getProperties());
        int statusCode = responseParts.get(5).getStatusCode();
        assertEquals(NO_CONTENT.getStatusCode(), statusCode);
        Olingo2ComponentTest.LOG.info("Update address status: {}", statusCode);
        statusCode = responseParts.get(6).getStatusCode();
        assertEquals(NO_CONTENT.getStatusCode(), statusCode);
        Olingo2ComponentTest.LOG.info("Update entry status: {}", statusCode);
        statusCode = responseParts.get(7).getStatusCode();
        assertEquals(NO_CONTENT.getStatusCode(), statusCode);
        Olingo2ComponentTest.LOG.info("Delete status: {}", statusCode);
        assertEquals(NOT_FOUND.getStatusCode(), responseParts.get(8).getStatusCode());
        final Exception exception = ((Exception) (responseParts.get(8).getBody()));
        assertNotNull(exception);
        Olingo2ComponentTest.LOG.info("Read deleted entry exception: {}", exception);
    }

    /**
     * Read entity set of the People object and filter already seen items on
     * subsequent exchanges Use a delay since the mock endpoint does not always
     * get the correct number of exchanges before being satisfied.
     *
     * Note:
     * - consumer.splitResults is set to false since this ensures the first returned message
     *   contains all the results. This is preferred for the purposes of this test. The default
     *   will mean the first n messages contain the results (where n is the result total) then
     *   subsequent messages will be empty
     */
    @Test
    public void testConsumerReadFilterAlreadySeen() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        String endpoint = "olingo2://read/Manufacturers?filterAlreadySeen=true&consumer.delay=2&consumer.sendEmptyMessageWhenIdle=true&consumer.splitResult=false";
        int expectedMsgCount = 3;
        MockEndpoint mockEndpoint = getMockEndpoint("mock:consumer-alreadyseen");
        mockEndpoint.expectedMessageCount(expectedMsgCount);
        mockEndpoint.setResultWaitTime(60000);
        final ODataFeed manufacturers = ((ODataFeed) (requestBodyAndHeaders(endpoint, null, headers)));
        assertNotNull(manufacturers);
        int expectedManufacturers = manufacturers.getEntries().size();
        mockEndpoint.assertIsSatisfied();
        for (int i = 0; i < expectedMsgCount; ++i) {
            Object body = mockEndpoint.getExchanges().get(i).getIn().getBody();
            if (i == 0) {
                // 
                // First polled messages contained all the manufacturers
                // 
                assertTrue((body instanceof ODataFeed));
                ODataFeed set = ((ODataFeed) (body));
                assertEquals(expectedManufacturers, set.getEntries().size());
            } else {
                // 
                // Subsequent polling messages should be empty
                // since the filterAlreadySeen property is true
                // 
                assertNull(body);
            }
        }
    }

    /**
     * Read entity set of the People object and with no filter already seen, all
     * items should be present in each message
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProducerReadNoFilterAlreadySeen() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        String endpoint = "direct:read-people-nofilterseen";
        int expectedMsgCount = 3;
        MockEndpoint mockEndpoint = getMockEndpoint("mock:producer-noalreadyseen");
        mockEndpoint.expectedMessageCount(expectedMsgCount);
        int expectedEntities = -1;
        for (int i = 0; i < expectedMsgCount; ++i) {
            final ODataFeed manufacturers = ((ODataFeed) (requestBodyAndHeaders(endpoint, null, headers)));
            assertNotNull(manufacturers);
            if (i == 0) {
                expectedEntities = manufacturers.getEntries().size();
            }
        }
        mockEndpoint.assertIsSatisfied();
        for (int i = 0; i < expectedMsgCount; ++i) {
            Object body = mockEndpoint.getExchanges().get(i).getIn().getBody();
            assertTrue((body instanceof ODataFeed));
            ODataFeed set = ((ODataFeed) (body));
            // 
            // All messages contained all the manufacturers
            // 
            assertEquals(expectedEntities, set.getEntries().size());
        }
    }

    /**
     * Read entity set of the People object and filter already seen items on
     * subsequent exchanges
     */
    @Test
    public void testProducerReadFilterAlreadySeen() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        String endpoint = "direct:read-people-filterseen";
        int expectedMsgCount = 3;
        MockEndpoint mockEndpoint = getMockEndpoint("mock:producer-alreadyseen");
        mockEndpoint.expectedMessageCount(expectedMsgCount);
        int expectedEntities = -1;
        for (int i = 0; i < expectedMsgCount; ++i) {
            final ODataFeed manufacturers = ((ODataFeed) (requestBodyAndHeaders(endpoint, null, headers)));
            assertNotNull(manufacturers);
            if (i == 0) {
                expectedEntities = manufacturers.getEntries().size();
            }
        }
        mockEndpoint.assertIsSatisfied();
        for (int i = 0; i < expectedMsgCount; ++i) {
            Object body = mockEndpoint.getExchanges().get(i).getIn().getBody();
            assertTrue((body instanceof ODataFeed));
            ODataFeed set = ((ODataFeed) (body));
            if (i == 0) {
                // 
                // First polled messages contained all the manufacturers
                // 
                assertEquals(expectedEntities, set.getEntries().size());
            } else {
                // 
                // Subsequent messages should be empty
                // since the filterAlreadySeen property is true
                // 
                assertEquals(0, set.getEntries().size());
            }
        }
    }

    /**
     * Read entity set of the Manufacturers object and split the results
     * into individual messages
     */
    @Test
    public void testConsumerReadSplitResults() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        String endpoint = "olingo2://read/Manufacturers?consumer.splitResult=true";
        int expectedMsgCount = 2;
        MockEndpoint mockEndpoint = getMockEndpoint("mock:consumer-splitresult");
        mockEndpoint.expectedMessageCount(expectedMsgCount);
        final ODataFeed odataFeed = ((ODataFeed) (requestBodyAndHeaders(endpoint, null, headers)));
        assertNotNull(odataFeed);
        mockEndpoint.assertIsSatisfied();
        // 
        // 2 individual messages in the exchange,
        // each containing a different entity.
        // 
        for (int i = 0; i < expectedMsgCount; ++i) {
            Object body = mockEndpoint.getExchanges().get(i).getIn().getBody();
            assertTrue((body instanceof ODataEntry));
            ODataEntry entry = ((ODataEntry) (body));
            Map<String, Object> properties = entry.getProperties();
            assertNotNull(properties);
            Object name = properties.get("Name");
            assertNotNull(name);
            switch (i) {
                case 0 :
                    assertEquals("Star Powered Racing", name);
                    break;
                case 1 :
                    assertEquals("Horse Powered Racing", name);
                    break;
                default :
            }
        }
    }
}

