/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets.command;


import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.Part;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class GetClientPartitionAttributesCommand66Test {
    private static final String REGION_NAME = "region1";

    @Mock
    private SecurityService securityService;

    @Mock
    private Message message;

    @Mock
    private ServerConnection serverConnection;

    @Mock
    private InternalCache cache;

    @Mock
    private Part regionNamePart;

    @Mock
    private Message responseMessage;

    @InjectMocks
    private GetClientPartitionAttributesCommand66 getClientPartitionAttributesCommand66;

    @Test
    public void noSecuirtyShouldSucceed() throws Exception {
        Mockito.when(this.securityService.isClientSecurityRequired()).thenReturn(false);
        this.getClientPartitionAttributesCommand66.cmdExecute(this.message, this.serverConnection, this.securityService, 0);
        Mockito.verify(this.responseMessage).send();
    }
}

