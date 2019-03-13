/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.rest;


import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.router.Router;
import java.io.IOException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests functionality of {@link AsyncRequestResponseHandlerFactory}.
 */
public class AsyncRequestResponseHandlerFactoryTest {
    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    /**
     * Tests the instantiation of an {@link AsyncRequestResponseHandler} instance through the
     * {@link AsyncRequestResponseHandlerFactory}.
     *
     * @throws InstantiationException
     * 		
     */
    @Test
    public void getAsyncRequestResponseHandlerTest() throws IOException, InstantiationException {
        Properties properties = new Properties();
        VerifiableProperties verifiableProperties = new VerifiableProperties(properties);
        Router router = new com.github.ambry.router.InMemoryRouter(verifiableProperties, new MockClusterMap());
        // Get response handler.
        AsyncRequestResponseHandlerFactory responseHandlerFactory = new AsyncRequestResponseHandlerFactory(1, AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY);
        RestResponseHandler restResponseHandler = responseHandlerFactory.getRestResponseHandler();
        Assert.assertNotNull("No RestResponseHandler returned", restResponseHandler);
        Assert.assertEquals("Did not receive an AsyncRequestResponseHandler instance", AsyncRequestResponseHandler.class.getCanonicalName(), restResponseHandler.getClass().getCanonicalName());
        BlobStorageService blobStorageService = new MockBlobStorageService(verifiableProperties, restResponseHandler, router);
        // Get request handler.
        AsyncRequestResponseHandlerFactory requestHandlerFactory = new AsyncRequestResponseHandlerFactory(1, AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY, blobStorageService);
        RestRequestHandler restRequestHandler = requestHandlerFactory.getRestRequestHandler();
        Assert.assertNotNull("No RestRequestHandler returned", restRequestHandler);
        Assert.assertEquals("Did not receive an AsyncRequestResponseHandler instance", AsyncRequestResponseHandler.class.getCanonicalName(), restRequestHandler.getClass().getCanonicalName());
        // make sure they are same instance
        Assert.assertEquals("Instances of AsyncRequestResponseHandler are not the same", restResponseHandler, restRequestHandler);
        // make sure the instance starts and shuts down OK.
        restRequestHandler.start();
        restRequestHandler.shutdown();
    }

    /**
     * Tests instantiation of {@link AsyncRequestResponseHandlerFactory} with bad input.
     */
    @Test
    public void getFactoryTestWithBadInputTest() throws IOException {
        VerifiableProperties verifiableProperties = new VerifiableProperties(new Properties());
        Router router = new com.github.ambry.router.InMemoryRouter(verifiableProperties, new MockClusterMap());
        MockRestRequestResponseHandler restRequestResponseHandler = new MockRestRequestResponseHandler();
        BlobStorageService blobStorageService = new MockBlobStorageService(verifiableProperties, restRequestResponseHandler, router);
        // RestResponseHandlerFactory constructor.
        // handlerCount = 0
        try {
            new AsyncRequestResponseHandlerFactory(0, AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY);
            Assert.fail("Instantiation should have failed because response handler count is 0");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // handlerCount < 0
        try {
            new AsyncRequestResponseHandlerFactory((-1), AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY);
            Assert.fail("Instantiation should have failed because response handler count is less than 0");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // MetricRegistry null.
        try {
            new AsyncRequestResponseHandlerFactory(1, null);
            Assert.fail("Instantiation should have failed because one of the arguments was null");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // RestRequestHandlerFactory constructor.
        // handlerCount = 0
        try {
            new AsyncRequestResponseHandlerFactory(0, AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY, blobStorageService);
            Assert.fail("Instantiation should have failed because request handler count is 0");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // handlerCount < 0
        try {
            new AsyncRequestResponseHandlerFactory((-1), AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY, blobStorageService);
            Assert.fail("Instantiation should have failed because request handler count is less than 0");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // MetricRegistry null.
        try {
            new AsyncRequestResponseHandlerFactory(1, null, blobStorageService);
            Assert.fail("Instantiation should have failed because one of the arguments was null");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // BlobStorageService null.
        try {
            new AsyncRequestResponseHandlerFactory(1, AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY, null);
            Assert.fail("Instantiation should have failed because one of the arguments was null");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // Different instances of MetricRegistry during construction of different instances of the factory.
        new AsyncRequestResponseHandlerFactory(1, AsyncRequestResponseHandlerFactoryTest.METRIC_REGISTRY);
        try {
            new AsyncRequestResponseHandlerFactory(1, new MetricRegistry(), blobStorageService);
            Assert.fail("Instantiation should have failed because different instances of MetricRegistry was provided");
        } catch (IllegalStateException e) {
            // expected. nothing to do.
        }
    }
}

