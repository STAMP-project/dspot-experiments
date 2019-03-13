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
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.repository.KettleRepositoryLostException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.shared.SharedObjects;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.repo.controller.RepositoryConnectController;


public class SessionTimeoutHandlerTest {
    private RepositoryConnectController repositoryConnectController;

    private Repository repository;

    private SessionTimeoutHandler sessionTimeoutHandler;

    @Test
    public void handle() throws Throwable {
        Mockito.when(repository.readTransSharedObjects(ArgumentMatchers.any())).thenReturn(Mockito.mock(SharedObjects.class));
        Method method = Repository.class.getMethod("readTransSharedObjects", TransMeta.class);
        sessionTimeoutHandler.handle(repository, Mockito.mock(Exception.class), method, new Object[]{ Mockito.mock(TransMeta.class) });
        Mockito.verify(sessionTimeoutHandler, Mockito.never()).showLoginScreen(ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleSecondExecutionFailed() throws Throwable {
        Mockito.when(repository.readTransSharedObjects(ArgumentMatchers.any())).thenThrow(KettleRepositoryLostException.class).thenReturn(Mockito.mock(SharedObjects.class));
        Method method = Repository.class.getMethod("readTransSharedObjects", TransMeta.class);
        sessionTimeoutHandler.handle(repository, Mockito.mock(Exception.class), method, new Object[]{ Mockito.mock(TransMeta.class) });
        Mockito.verify(sessionTimeoutHandler).showLoginScreen(ArgumentMatchers.any());
    }
}

