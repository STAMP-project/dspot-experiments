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
package org.apache.camel.component.azure.queue;


import com.microsoft.azure.storage.queue.CloudQueue;
import java.net.URI;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class QueueServiceComponentConfigurationTest extends CamelTestSupport {
    @Test
    public void testCreateEndpointWithMinConfigForClientOnly() throws Exception {
        CloudQueue client = new CloudQueue(URI.create("https://camelazure.queue.core.windows.net/testqueue/messages"), newAccountKeyCredentials());
        context.getRegistry().bind("azureQueueClient", client);
        QueueServiceComponent component = new QueueServiceComponent(context);
        QueueServiceEndpoint endpoint = ((QueueServiceEndpoint) (component.createEndpoint("azure-queue://camelazure/testqueue?azureQueueClient=#azureQueueClient")));
        doTestCreateEndpointWithMinConfig(endpoint, true);
    }

    @Test
    public void testCreateEndpointWithMinConfigForCredsOnly() throws Exception {
        registerCredentials();
        QueueServiceComponent component = new QueueServiceComponent(context);
        QueueServiceEndpoint endpoint = ((QueueServiceEndpoint) (component.createEndpoint("azure-queue://camelazure/testqueue?credentials=#creds")));
        doTestCreateEndpointWithMinConfig(endpoint, false);
    }

    @Test
    public void testCreateEndpointWithMaxConfig() throws Exception {
        registerCredentials();
        QueueServiceComponent component = new QueueServiceComponent(context);
        QueueServiceEndpoint endpoint = ((QueueServiceEndpoint) (component.createEndpoint(("azure-queue://camelazure/testqueue?credentials=#creds" + "&operation=addMessage&queuePrefix=prefix&messageTimeToLive=100&messageVisibilityDelay=10"))));
        doTestCreateEndpointWithMaxConfig(endpoint, false);
    }

    @Test
    public void testNoCredentials() throws Exception {
        QueueServiceComponent component = new QueueServiceComponent(context);
        try {
            component.createEndpoint("azure-queue://camelazure/testqueue");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Credentials must be specified.", ex.getMessage());
        }
    }

    @Test
    public void testTooManyPathSegments() throws Exception {
        QueueServiceComponent component = new QueueServiceComponent(context);
        try {
            component.createEndpoint("azure-queue://camelazure/testqueue/1");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Only the account and queue names must be specified.", ex.getMessage());
        }
    }

    @Test
    public void testTooFewPathSegments() throws Exception {
        QueueServiceComponent component = new QueueServiceComponent(context);
        try {
            component.createEndpoint("azure-queue://camelazure?operation=addMessage");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("The queue name must be specified.", ex.getMessage());
        }
    }
}

