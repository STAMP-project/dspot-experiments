/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.repo.timeout;


import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.repository.KettleRepositoryLostException;
import org.pentaho.di.repository.RepositorySecurityManager;


public class RepositoryServiceSessionTimeoutHandlerTest {
    private RepositorySecurityManager repositoryService;

    private SessionTimeoutHandler sessionTimeoutHandler;

    private RepositoryServiceSessionTimeoutHandler metaStoresessionTimeoutHandler;

    @SuppressWarnings("unchecked")
    @Test
    public void testHandlerCallOnException() throws Throwable {
        Mockito.when(repositoryService.getUsers()).thenThrow(KettleRepositoryLostException.class);
        Method method = RepositorySecurityManager.class.getMethod("getUsers");
        metaStoresessionTimeoutHandler.invoke(Mockito.mock(Proxy.class), method, new Object[0]);
        Mockito.verify(sessionTimeoutHandler).handle(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

