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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.olingo2.api.Olingo2App;
import org.apache.camel.component.olingo2.api.Olingo2ResponseHandler;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchChangeRequest;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchQueryRequest;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchRequest;
import org.apache.camel.component.olingo2.api.batch.Olingo2BatchResponse;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.http.entity.ContentType;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntitySetInfo;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.servicedocument.Collection;
import org.apache.olingo.odata2.api.servicedocument.ServiceDocument;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration test for {@link org.apache.camel.component.olingo2.api.impl.Olingo2AppImpl}
 * using the sample Olingo2 Server dynamically downloaded and started during the test.
 */
public class Olingo2AppAPITest {
    private static final Logger LOG = LoggerFactory.getLogger(Olingo2AppAPITest.class);

    private static final int PORT = AvailablePortFinder.getNextAvailable();

    private static final long TIMEOUT = 10;

    private static final String MANUFACTURERS = "Manufacturers";

    private static final String FQN_MANUFACTURERS = "DefaultContainer.Manufacturers";

    private static final String ADDRESS = "Address";

    private static final String CARS = "Cars";

    private static final String TEST_KEY = "'1'";

    private static final String TEST_CREATE_KEY = "'123'";

    private static final String TEST_MANUFACTURER = (((Olingo2AppAPITest.FQN_MANUFACTURERS) + "(") + (Olingo2AppAPITest.TEST_KEY)) + ")";

    private static final String TEST_CREATE_MANUFACTURER = (((Olingo2AppAPITest.MANUFACTURERS) + "(") + (Olingo2AppAPITest.TEST_CREATE_KEY)) + ")";

    private static final String TEST_RESOURCE_CONTENT_ID = "1";

    private static final String TEST_RESOURCE = "$" + (Olingo2AppAPITest.TEST_RESOURCE_CONTENT_ID);

    private static final char NEW_LINE = '\n';

    private static final String TEST_CAR = "Manufacturers('1')/Cars('1')";

    private static final String TEST_MANUFACTURER_FOUNDED_PROPERTY = "Manufacturers('1')/Founded";

    private static final String TEST_MANUFACTURER_FOUNDED_VALUE = "Manufacturers('1')/Founded/$value";

    private static final String FOUNDED_PROPERTY = "Founded";

    private static final String TEST_MANUFACTURER_ADDRESS_PROPERTY = "Manufacturers('1')/Address";

    private static final String TEST_MANUFACTURER_LINKS_CARS = "Manufacturers('1')/$links/Cars";

    private static final String TEST_CAR_LINK_MANUFACTURER = "Cars('1')/$links/Manufacturer";

    private static final String COUNT_OPTION = "/$count";

    private static final String TEST_SERVICE_URL = ("http://localhost:" + (Olingo2AppAPITest.PORT)) + "/MyFormula.svc";

    // private static final String TEST_SERVICE_URL = "http://localhost:8080/cars-annotations-sample/MyFormula.svc";
    // private static final ContentType TEST_FORMAT = ContentType.APPLICATION_XML_CS_UTF_8;
    private static final ContentType TEST_FORMAT = ContentType.APPLICATION_JSON;

    private static final String TEST_FORMAT_STRING = Olingo2AppAPITest.TEST_FORMAT.toString();

    // private static final Pattern LINK_PATTERN = Pattern.compile("[^(]+\\('([^']+)'\\)");
    private static final String ID_PROPERTY = "Id";

    private static Olingo2App olingoApp;

    private static Edm edm;

    private static Map<String, EdmEntitySet> edmEntitySetMap;

    private static Olingo2SampleServer server;

    @Test
    public void testServiceDocument() throws Exception {
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<ServiceDocument> responseHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(null, "", null, null, responseHandler);
        final ServiceDocument serviceDocument = responseHandler.await();
        final List<Collection> collections = serviceDocument.getAtomInfo().getWorkspaces().get(0).getCollections();
        Assert.assertEquals("Service Atom Collections", 3, collections.size());
        Olingo2AppAPITest.LOG.info("Service Atom Collections:  {}", collections);
        final List<EdmEntitySetInfo> entitySetsInfo = serviceDocument.getEntitySetsInfo();
        Assert.assertEquals("Service Entity Sets", 3, entitySetsInfo.size());
        Olingo2AppAPITest.LOG.info("Service Document Entries:  {}", entitySetsInfo);
    }

    @Test
    public void testReadFeed() throws Exception {
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<ODataFeed> responseHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.MANUFACTURERS, null, null, responseHandler);
        final ODataFeed dataFeed = responseHandler.await();
        Assert.assertNotNull("Data feed", dataFeed);
        Olingo2AppAPITest.LOG.info("Entries:  {}", Olingo2AppAPITest.prettyPrint(dataFeed));
    }

    @Test
    public void testReadUnparsedFeed() throws Exception {
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<InputStream> responseHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.uread(Olingo2AppAPITest.edm, Olingo2AppAPITest.MANUFACTURERS, null, null, responseHandler);
        final InputStream rawfeed = responseHandler.await();
        Assert.assertNotNull("Data feed", rawfeed);
        // for this test, we just let EP to verify the stream data
        final ODataFeed dataFeed = EntityProvider.readFeed(Olingo2AppAPITest.TEST_FORMAT_STRING, Olingo2AppAPITest.edmEntitySetMap.get(Olingo2AppAPITest.MANUFACTURERS), rawfeed, EntityProviderReadProperties.init().build());
        Olingo2AppAPITest.LOG.info("Entries:  {}", Olingo2AppAPITest.prettyPrint(dataFeed));
    }

    @Test
    public void testReadEntry() throws Exception {
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<ODataEntry> responseHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER, null, null, responseHandler);
        ODataEntry entry = responseHandler.await();
        Olingo2AppAPITest.LOG.info("Single Entry:  {}", Olingo2AppAPITest.prettyPrint(entry));
        responseHandler.reset();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_CAR, null, null, responseHandler);
        entry = responseHandler.await();
        Olingo2AppAPITest.LOG.info("Single Entry:  {}", Olingo2AppAPITest.prettyPrint(entry));
        responseHandler.reset();
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put(.expand.toString(), Olingo2AppAPITest.CARS);
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER, queryParams, null, responseHandler);
        ODataEntry entryExpanded = responseHandler.await();
        Olingo2AppAPITest.LOG.info("Single Entry with expanded Cars relation:  {}", Olingo2AppAPITest.prettyPrint(entryExpanded));
    }

    @Test
    public void testReadUnparsedEntry() throws Exception {
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<InputStream> responseHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.uread(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER, null, null, responseHandler);
        InputStream rawentry = responseHandler.await();
        ODataEntry entry = EntityProvider.readEntry(Olingo2AppAPITest.TEST_FORMAT_STRING, Olingo2AppAPITest.edmEntitySetMap.get(Olingo2AppAPITest.MANUFACTURERS), rawentry, EntityProviderReadProperties.init().build());
        Olingo2AppAPITest.LOG.info("Single Entry:  {}", Olingo2AppAPITest.prettyPrint(entry));
        responseHandler.reset();
        Olingo2AppAPITest.olingoApp.uread(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_CAR, null, null, responseHandler);
        rawentry = responseHandler.await();
        entry = EntityProvider.readEntry(Olingo2AppAPITest.TEST_FORMAT_STRING, Olingo2AppAPITest.edmEntitySetMap.get(Olingo2AppAPITest.CARS), rawentry, EntityProviderReadProperties.init().build());
        Olingo2AppAPITest.LOG.info("Single Entry:  {}", Olingo2AppAPITest.prettyPrint(entry));
        responseHandler.reset();
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put(.expand.toString(), Olingo2AppAPITest.CARS);
        Olingo2AppAPITest.olingoApp.uread(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER, queryParams, null, responseHandler);
        rawentry = responseHandler.await();
        ODataEntry entryExpanded = EntityProvider.readEntry(Olingo2AppAPITest.TEST_FORMAT_STRING, Olingo2AppAPITest.edmEntitySetMap.get(Olingo2AppAPITest.MANUFACTURERS), rawentry, EntityProviderReadProperties.init().build());
        Olingo2AppAPITest.LOG.info("Single Entry with expanded Cars relation:  {}", Olingo2AppAPITest.prettyPrint(entryExpanded));
    }

    @Test
    public void testReadUpdateProperties() throws Exception {
        // test simple property Manufacturer.Founded
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<Map<String, Object>> propertyHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER_FOUNDED_PROPERTY, null, null, propertyHandler);
        Calendar founded = ((Calendar) (propertyHandler.await().get(Olingo2AppAPITest.FOUNDED_PROPERTY)));
        Olingo2AppAPITest.LOG.info("Founded property {}", founded);
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<Calendar> valueHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER_FOUNDED_VALUE, null, null, valueHandler);
        founded = valueHandler.await();
        Olingo2AppAPITest.LOG.info("Founded property {}", founded);
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<HttpStatusCodes> statusHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put(Olingo2AppAPITest.FOUNDED_PROPERTY, new Date());
        // olingoApp.update(edm, TEST_MANUFACTURER_FOUNDED_PROPERTY, properties, statusHandler);
        // requires a plain Date for XML
        Olingo2AppAPITest.olingoApp.update(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER_FOUNDED_PROPERTY, null, new Date(), statusHandler);
        Olingo2AppAPITest.LOG.info("Founded property updated with status {}", statusHandler.await().getStatusCode());
        statusHandler.reset();
        Olingo2AppAPITest.olingoApp.update(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER_FOUNDED_VALUE, null, new Date(), statusHandler);
        Olingo2AppAPITest.LOG.info("Founded property updated with status {}", statusHandler.await().getStatusCode());
        // test complex property Manufacturer.Address
        propertyHandler.reset();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER_ADDRESS_PROPERTY, null, null, propertyHandler);
        final Map<String, Object> address = propertyHandler.await();
        Olingo2AppAPITest.LOG.info("Address property {}", Olingo2AppAPITest.prettyPrint(address, 0));
        statusHandler.reset();
        address.clear();
        // Olingo2 sample server MERGE/PATCH behaves like PUT!!!
        // address.put("Street", "Main Street");
        address.put("Street", "Star Street 137");
        address.put("City", "Stuttgart");
        address.put("ZipCode", "70173");
        address.put("Country", "Germany");
        // olingoApp.patch(edm, TEST_MANUFACTURER_ADDRESS_PROPERTY, address, statusHandler);
        Olingo2AppAPITest.olingoApp.merge(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER_ADDRESS_PROPERTY, null, address, statusHandler);
        Olingo2AppAPITest.LOG.info("Address property updated with status {}", statusHandler.await().getStatusCode());
    }

    @Test
    public void testReadDeleteCreateLinks() throws Exception {
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<List<String>> linksHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_MANUFACTURER_LINKS_CARS, null, null, linksHandler);
        final List<String> links = linksHandler.await();
        Assert.assertFalse(links.isEmpty());
        Olingo2AppAPITest.LOG.info("Read links: {}", links);
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<String> linkHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_CAR_LINK_MANUFACTURER, null, null, linkHandler);
        final String link = linkHandler.await();
        Olingo2AppAPITest.LOG.info("Read link: {}", link);
        // Deleting relationships through links is not supported in Olingo2 at the time of writing this test
        /* final TestOlingo2ResponseHandler<HttpStatusCodes> statusHandler =
        new TestOlingo2ResponseHandler<HttpStatusCodes>();

        final ArrayList<Map<String, Object>> carKeys = new ArrayList<Map<String, Object>>();
        for (String carLink : links) {
        final Matcher matcher = LINK_PATTERN.matcher(carLink);
        assertTrue("Link pattern " + carLink, matcher.matches());
        final String carId = matcher.group(1);

        final HashMap<String, Object> keys = new HashMap<String, Object>();
        keys.put(ID_PROPERTY, carId);
        carKeys.add(keys);

        // delete manufacturer->car link
        statusHandler.reset();
        final String resourcePath = TEST_MANUFACTURER_LINKS_CARS + "('" + carId + "')";
        olingoApp.delete(resourcePath, statusHandler);

        assertEquals("Delete car link " + resourcePath, HttpStatusCodes.OK.getStatusCode(),
        statusHandler.await().getStatusCode());
        }

        // add links to all Cars
        statusHandler.reset();
        olingoApp.create(edm, TEST_MANUFACTURER_LINKS_CARS, carKeys, statusHandler);

        assertEquals("Links update", HttpStatusCodes.ACCEPTED.getStatusCode(), statusHandler.await().getStatusCode());

        // delete car->manufacturer link
        statusHandler.reset();
        olingoApp.delete(TEST_CAR_LINK_MANUFACTURER, statusHandler);

        assertEquals("Delete manufacturer link " + TEST_CAR_LINK_MANUFACTURER, HttpStatusCodes.OK.getStatusCode(),
        statusHandler.await().getStatusCode());

        // add link to Manufacturer
        statusHandler.reset();
        final HashMap<String, Object> manufacturerKey = new HashMap<String, Object>();
        manufacturerKey.put(ID_PROPERTY, "1");

        olingoApp.create(edm, TEST_CAR_LINK_MANUFACTURER, manufacturerKey, statusHandler);

        assertEquals("Link update", HttpStatusCodes.ACCEPTED.getStatusCode(), statusHandler.await().getStatusCode());
         */
    }

    @Test
    public void testReadCount() throws Exception {
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<Long> countHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, ((Olingo2AppAPITest.MANUFACTURERS) + (Olingo2AppAPITest.COUNT_OPTION)), null, null, countHandler);
        Olingo2AppAPITest.LOG.info("Manufacturers count: {}", countHandler.await());
        countHandler.reset();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, ((Olingo2AppAPITest.TEST_MANUFACTURER) + (Olingo2AppAPITest.COUNT_OPTION)), null, null, countHandler);
        Olingo2AppAPITest.LOG.info("Manufacturer count: {}", countHandler.await());
        countHandler.reset();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, ((Olingo2AppAPITest.TEST_MANUFACTURER_LINKS_CARS) + (Olingo2AppAPITest.COUNT_OPTION)), null, null, countHandler);
        Olingo2AppAPITest.LOG.info("Manufacturers links count: {}", countHandler.await());
        countHandler.reset();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, ((Olingo2AppAPITest.TEST_CAR_LINK_MANUFACTURER) + (Olingo2AppAPITest.COUNT_OPTION)), null, null, countHandler);
        Olingo2AppAPITest.LOG.info("Manufacturer link count: {}", countHandler.await());
    }

    @Test
    public void testCreateUpdateDeleteEntry() throws Exception {
        // create entry to update
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<ODataEntry> entryHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.create(Olingo2AppAPITest.edm, Olingo2AppAPITest.MANUFACTURERS, null, getEntityData(), entryHandler);
        ODataEntry createdEntry = entryHandler.await();
        Olingo2AppAPITest.LOG.info("Created Entry:  {}", Olingo2AppAPITest.prettyPrint(createdEntry));
        Map<String, Object> data = getEntityData();
        @SuppressWarnings("unchecked")
        Map<String, Object> address = ((Map<String, Object>) (data.get(Olingo2AppAPITest.ADDRESS)));
        data.put("Name", "MyCarManufacturer Renamed");
        address.put("Street", "Main Street");
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<HttpStatusCodes> statusHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        Olingo2AppAPITest.olingoApp.update(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_CREATE_MANUFACTURER, null, data, statusHandler);
        statusHandler.await();
        statusHandler.reset();
        data.put("Name", "MyCarManufacturer Patched");
        Olingo2AppAPITest.olingoApp.patch(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_CREATE_MANUFACTURER, null, data, statusHandler);
        statusHandler.await();
        entryHandler.reset();
        Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_CREATE_MANUFACTURER, null, null, entryHandler);
        ODataEntry updatedEntry = entryHandler.await();
        Olingo2AppAPITest.LOG.info("Updated Entry successfully:  {}", Olingo2AppAPITest.prettyPrint(updatedEntry));
        statusHandler.reset();
        Olingo2AppAPITest.olingoApp.delete(Olingo2AppAPITest.TEST_CREATE_MANUFACTURER, null, statusHandler);
        HttpStatusCodes statusCode = statusHandler.await();
        Olingo2AppAPITest.LOG.info("Deletion of Entry was successful:  {}: {}", statusCode.getStatusCode(), statusCode.getInfo());
        try {
            Olingo2AppAPITest.LOG.info("Verify Delete Entry");
            entryHandler.reset();
            Olingo2AppAPITest.olingoApp.read(Olingo2AppAPITest.edm, Olingo2AppAPITest.TEST_CREATE_MANUFACTURER, null, null, entryHandler);
            entryHandler.await();
            Assert.fail("Entry not deleted!");
        } catch (Exception e) {
            Olingo2AppAPITest.LOG.info("Deleted entry not found: {}", e.getMessage());
        }
    }

    @Test
    public void testBatchRequest() throws Exception {
        final List<Olingo2BatchRequest> batchParts = new ArrayList<>();
        // Edm query
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(METADATA).build());
        // feed query
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2AppAPITest.MANUFACTURERS).build());
        // read
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2AppAPITest.TEST_MANUFACTURER).build());
        // read with expand
        final HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(.expand.toString(), Olingo2AppAPITest.CARS);
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2AppAPITest.TEST_MANUFACTURER).queryParams(queryParams).build());
        // create
        final Map<String, Object> data = getEntityData();
        batchParts.add(Olingo2BatchChangeRequest.resourcePath(Olingo2AppAPITest.MANUFACTURERS).contentId(Olingo2AppAPITest.TEST_RESOURCE_CONTENT_ID).operation(CREATE).body(data).build());
        // update
        final Map<String, Object> updateData = new HashMap<>(data);
        @SuppressWarnings("unchecked")
        Map<String, Object> address = ((Map<String, Object>) (updateData.get(Olingo2AppAPITest.ADDRESS)));
        updateData.put("Name", "MyCarManufacturer Renamed");
        address.put("Street", "Main Street");
        batchParts.add(Olingo2BatchChangeRequest.resourcePath(Olingo2AppAPITest.TEST_RESOURCE).operation(UPDATE).body(updateData).build());
        // delete
        batchParts.add(Olingo2BatchChangeRequest.resourcePath(Olingo2AppAPITest.TEST_RESOURCE).operation(DELETE).build());
        final Olingo2AppAPITest.TestOlingo2ResponseHandler<List<Olingo2BatchResponse>> responseHandler = new Olingo2AppAPITest.TestOlingo2ResponseHandler<>();
        // read to verify delete
        batchParts.add(Olingo2BatchQueryRequest.resourcePath(Olingo2AppAPITest.TEST_CREATE_MANUFACTURER).build());
        Olingo2AppAPITest.olingoApp.batch(Olingo2AppAPITest.edm, null, batchParts, responseHandler);
        final List<Olingo2BatchResponse> responseParts = responseHandler.await(15, TimeUnit.MINUTES);
        Assert.assertEquals("Batch responses expected", 8, responseParts.size());
        Assert.assertNotNull(responseParts.get(0).getBody());
        final ODataFeed feed = ((ODataFeed) (responseParts.get(1).getBody()));
        Assert.assertNotNull(feed);
        Olingo2AppAPITest.LOG.info("Batch feed:  {}", Olingo2AppAPITest.prettyPrint(feed));
        ODataEntry dataEntry = ((ODataEntry) (responseParts.get(2).getBody()));
        Assert.assertNotNull(dataEntry);
        Olingo2AppAPITest.LOG.info("Batch read entry:  {}", Olingo2AppAPITest.prettyPrint(dataEntry));
        dataEntry = ((ODataEntry) (responseParts.get(3).getBody()));
        Assert.assertNotNull(dataEntry);
        Olingo2AppAPITest.LOG.info("Batch read entry with expand:  {}", Olingo2AppAPITest.prettyPrint(dataEntry));
        dataEntry = ((ODataEntry) (responseParts.get(4).getBody()));
        Assert.assertNotNull(dataEntry);
        Olingo2AppAPITest.LOG.info("Batch create entry:  {}", Olingo2AppAPITest.prettyPrint(dataEntry));
        Assert.assertEquals(NO_CONTENT.getStatusCode(), responseParts.get(5).getStatusCode());
        Assert.assertEquals(NO_CONTENT.getStatusCode(), responseParts.get(6).getStatusCode());
        Assert.assertEquals(NOT_FOUND.getStatusCode(), responseParts.get(7).getStatusCode());
        final Exception exception = ((Exception) (responseParts.get(7).getBody()));
        Assert.assertNotNull(exception);
        Olingo2AppAPITest.LOG.info("Batch retrieve deleted entry:  {}", exception);
    }

    private static final class TestOlingo2ResponseHandler<T> implements Olingo2ResponseHandler<T> {
        private T response;

        private Exception error;

        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onResponse(T response, Map<String, String> responseHeaders) {
            this.response = response;
            if (Olingo2AppAPITest.LOG.isDebugEnabled()) {
                if (response instanceof ODataFeed) {
                    Olingo2AppAPITest.LOG.debug("Received response: {}", Olingo2AppAPITest.prettyPrint(((ODataFeed) (response))));
                } else
                    if (response instanceof ODataEntry) {
                        Olingo2AppAPITest.LOG.debug("Received response: {}", Olingo2AppAPITest.prettyPrint(((ODataEntry) (response))));
                    } else {
                        Olingo2AppAPITest.LOG.debug("Received response: {}", response);
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
            return await(Olingo2AppAPITest.TIMEOUT, TimeUnit.SECONDS);
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

