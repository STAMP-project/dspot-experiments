/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.mongodb;


import AbstractMongoProcessor.CLIENT_AUTH;
import AbstractMongoProcessor.SSL_CONTEXT_SERVICE;
import AbstractMongoProcessor.URI;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import javax.net.ssl.SSLContext;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.ssl.SSLContextService.ClientAuth;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AbstractMongoProcessorTest {
    AbstractMongoProcessorTest.MockAbstractMongoProcessor processor;

    private TestRunner testRunner;

    @Test
    public void testcreateClientWithSSL() throws Exception {
        SSLContextService sslService = Mockito.mock(SSLContextService.class);
        SSLContext sslContext = Mockito.mock(SSLContext.class);
        Mockito.when(sslService.getIdentifier()).thenReturn("ssl-context");
        Mockito.when(sslService.createSSLContext(ArgumentMatchers.any(ClientAuth.class))).thenReturn(sslContext);
        testRunner.addControllerService("ssl-context", sslService);
        testRunner.enableControllerService(sslService);
        testRunner.setProperty(URI, "mongodb://localhost:27017");
        testRunner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        testRunner.assertValid(sslService);
        processor.createClient(testRunner.getProcessContext());
        Assert.assertNotNull(processor.mongoClient);
        processor.mongoClient = null;
        testRunner.setProperty(CLIENT_AUTH, "WANT");
        processor.createClient(testRunner.getProcessContext());
        Assert.assertNotNull(processor.mongoClient);
    }

    @Test(expected = IllegalStateException.class)
    public void testcreateClientWithSSLBadClientAuth() throws Exception {
        SSLContextService sslService = Mockito.mock(SSLContextService.class);
        SSLContext sslContext = Mockito.mock(SSLContext.class);
        Mockito.when(sslService.getIdentifier()).thenReturn("ssl-context");
        Mockito.when(sslService.createSSLContext(ArgumentMatchers.any(ClientAuth.class))).thenReturn(sslContext);
        testRunner.addControllerService("ssl-context", sslService);
        testRunner.enableControllerService(sslService);
        testRunner.setProperty(URI, "mongodb://localhost:27017");
        testRunner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        testRunner.assertValid(sslService);
        processor.createClient(testRunner.getProcessContext());
        Assert.assertNotNull(processor.mongoClient);
        processor.mongoClient = null;
        testRunner.setProperty(CLIENT_AUTH, "BAD");
        processor.createClient(testRunner.getProcessContext());
    }

    /**
     * Provides a stubbed processor instance for testing
     */
    public static class MockAbstractMongoProcessor extends AbstractMongoProcessor {
        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
            // nothing to do
        }

        @Override
        protected Builder getClientOptions(SSLContext sslContext) {
            return MongoClientOptions.builder();
        }
    }
}

