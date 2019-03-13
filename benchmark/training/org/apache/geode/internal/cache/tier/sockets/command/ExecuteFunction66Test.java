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


import ResourcePermissions.DATA_WRITE;
import org.apache.geode.internal.cache.execute.InternalFunctionExecutionService;
import org.apache.geode.internal.cache.tier.sockets.ChunkedMessage;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.OldClientSupportService;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.internal.cache.tier.sockets.command.ExecuteFunction66.FunctionContextImplFactory;
import org.apache.geode.internal.cache.tier.sockets.command.ExecuteFunction66.ServerToClientFunctionResultSender65Factory;
import org.apache.geode.internal.security.AuthorizeRequest;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category(ClientServerTest.class)
public class ExecuteFunction66Test {
    private static final String FUNCTION = "function";

    private static final String FUNCTION_ID = "function_id";

    private static final boolean OPTIMIZE_FOR_WRITE = false;

    private static final Object CALLBACK_ARG = "arg";

    private static final byte[] RESULT = new byte[]{ Integer.valueOf(0).byteValue() };

    private AuthorizeRequest authzRequest;

    private ChunkedMessage functionResponseMessage;

    private Message message;

    private OldClientSupportService oldClientSupportService;

    private SecurityService securityService;

    private ServerConnection serverConnection;

    // the following fields are all accessed in sub-class
    InternalFunctionExecutionService internalFunctionExecutionService;

    ServerToClientFunctionResultSender65Factory serverToClientFunctionResultSender65Factory;

    FunctionContextImplFactory functionContextImplFactory;

    ExecuteFunction66 executeFunction;

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void nonSecureShouldSucceed() throws Exception {
        Mockito.when(oldClientSupportService.getThrowable(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mockito.mock(Throwable.class));
        Mockito.when(securityService.isClientSecurityRequired()).thenReturn(false);
        executeFunction.cmdExecute(message, serverConnection, securityService, 0);
        Mockito.verify(serverToClientFunctionResultSender65Factory).create(ArgumentMatchers.eq(functionResponseMessage), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void withIntegratedSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(oldClientSupportService.getThrowable(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mockito.mock(Throwable.class));
        Mockito.when(securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(securityService.isIntegratedSecurity()).thenReturn(true);
        executeFunction.cmdExecute(message, serverConnection, securityService, 0);
        Mockito.verify(securityService).authorize(DATA_WRITE);
        Mockito.verify(serverToClientFunctionResultSender65Factory).create(ArgumentMatchers.eq(functionResponseMessage), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void withIntegratedSecurityShouldThrowIfNotAuthorized() {
        Mockito.when(securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(securityService.isIntegratedSecurity()).thenReturn(true);
        Mockito.doThrow(new NotAuthorizedException("")).when(securityService).authorize(DATA_WRITE);
        assertThatThrownBy(() -> executeFunction.cmdExecute(message, serverConnection, securityService, 0)).isExactlyInstanceOf(NullPointerException.class);
        Mockito.verify(securityService).authorize(DATA_WRITE);
        // verify(this.chunkedResponseMessage).sendChunk(this.serverConnection);
        Mockito.verifyZeroInteractions(serverToClientFunctionResultSender65Factory);
    }

    @Test
    public void withOldSecurityShouldSucceedIfAuthorized() throws Exception {
        Mockito.when(oldClientSupportService.getThrowable(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mockito.mock(Throwable.class));
        Mockito.when(securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(securityService.isIntegratedSecurity()).thenReturn(false);
        executeFunction.cmdExecute(message, serverConnection, securityService, 0);
        Mockito.verify(authzRequest).executeFunctionAuthorize(ArgumentMatchers.eq(ExecuteFunction66Test.FUNCTION_ID), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(false));
        Mockito.verify(serverToClientFunctionResultSender65Factory).create(ArgumentMatchers.eq(functionResponseMessage), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void withOldSecurityShouldThrowIfNotAuthorized() {
        Mockito.when(securityService.isClientSecurityRequired()).thenReturn(true);
        Mockito.when(securityService.isIntegratedSecurity()).thenReturn(false);
        Mockito.doThrow(new NotAuthorizedException("")).when(authzRequest).executeFunctionAuthorize(ArgumentMatchers.eq(ExecuteFunction66Test.FUNCTION_ID), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(false));
        assertThatThrownBy(() -> executeFunction.cmdExecute(message, serverConnection, securityService, 0)).isExactlyInstanceOf(NullPointerException.class);
        Mockito.verify(securityService).authorize(DATA_WRITE);
        Mockito.verifyZeroInteractions(serverToClientFunctionResultSender65Factory);
    }
}

