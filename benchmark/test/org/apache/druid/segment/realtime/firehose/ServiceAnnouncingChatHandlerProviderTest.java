/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.realtime.firehose;


import org.apache.druid.curator.discovery.ServiceAnnouncer;
import org.apache.druid.server.DruidNode;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class ServiceAnnouncingChatHandlerProviderTest extends EasyMockSupport {
    private static class TestChatHandler implements ChatHandler {}

    private static final String TEST_SERVICE_NAME = "test-service-name";

    private static final String TEST_HOST = "test-host";

    private static final int TEST_PORT = 1234;

    private ServiceAnnouncingChatHandlerProvider chatHandlerProvider;

    @Mock
    private DruidNode node;

    @Mock
    private ServiceAnnouncer serviceAnnouncer;

    @Test
    public void testRegistrationDefault() {
        testRegistrationWithAnnounce(false);
    }

    @Test
    public void testRegistrationWithAnnounce() {
        testRegistrationWithAnnounce(true);
    }

    @Test
    public void testRegistrationWithoutAnnounce() {
        ChatHandler testChatHandler = new ServiceAnnouncingChatHandlerProviderTest.TestChatHandler();
        Assert.assertFalse("bad initial state", chatHandlerProvider.get(ServiceAnnouncingChatHandlerProviderTest.TEST_SERVICE_NAME).isPresent());
        chatHandlerProvider.register(ServiceAnnouncingChatHandlerProviderTest.TEST_SERVICE_NAME, testChatHandler, false);
        Assert.assertTrue("chatHandler did not register", chatHandlerProvider.get(ServiceAnnouncingChatHandlerProviderTest.TEST_SERVICE_NAME).isPresent());
        Assert.assertEquals(testChatHandler, chatHandlerProvider.get(ServiceAnnouncingChatHandlerProviderTest.TEST_SERVICE_NAME).get());
        chatHandlerProvider.unregister(ServiceAnnouncingChatHandlerProviderTest.TEST_SERVICE_NAME);
        Assert.assertFalse("chatHandler did not deregister", chatHandlerProvider.get(ServiceAnnouncingChatHandlerProviderTest.TEST_SERVICE_NAME).isPresent());
    }
}

