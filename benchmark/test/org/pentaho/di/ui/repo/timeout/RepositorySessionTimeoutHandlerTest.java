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


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.repository.KettleRepositoryLostException;
import org.pentaho.di.repository.ReconnectableRepository;
import org.pentaho.di.ui.repo.controller.RepositoryConnectController;
import org.pentaho.metastore.api.IMetaStore;


public class RepositorySessionTimeoutHandlerTest {
    private ReconnectableRepository repository;

    private RepositoryConnectController repositoryConnectController;

    private RepositorySessionTimeoutHandler timeoutHandler;

    @Test
    public void connectedToRepository() {
        Mockito.when(repository.isConnected()).thenReturn(true);
        Assert.assertTrue(timeoutHandler.connectedToRepository());
    }

    @Test
    public void connectedToRepositoryReturnsFalse() {
        Mockito.when(repository.isConnected()).thenReturn(false);
        Assert.assertFalse(timeoutHandler.connectedToRepository());
    }

    @Test
    public void wrapMetastoreWithTimeoutHandler() throws Throwable {
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        Mockito.doThrow(KettleRepositoryLostException.class).when(metaStore).createNamespace(ArgumentMatchers.any());
        SessionTimeoutHandler sessionTimeoutHandler = Mockito.mock(SessionTimeoutHandler.class);
        IMetaStore wrappedMetaStore = RepositorySessionTimeoutHandler.wrapMetastoreWithTimeoutHandler(metaStore, sessionTimeoutHandler);
        wrappedMetaStore.createNamespace("TEST_NAMESPACE");
        Mockito.verify(sessionTimeoutHandler).handle(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

