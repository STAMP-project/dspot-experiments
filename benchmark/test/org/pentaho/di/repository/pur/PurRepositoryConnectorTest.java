/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.repository.pur;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class PurRepositoryConnectorTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testPDI12439PurRepositoryConnectorDoesntNPEAfterMultipleDisconnects() {
        PurRepository mockPurRepository = Mockito.mock(PurRepository.class);
        PurRepositoryMeta mockPurRepositoryMeta = Mockito.mock(PurRepositoryMeta.class);
        RootRef mockRootRef = Mockito.mock(RootRef.class);
        PurRepositoryConnector purRepositoryConnector = new PurRepositoryConnector(mockPurRepository, mockPurRepositoryMeta, mockRootRef);
        purRepositoryConnector.disconnect();
        purRepositoryConnector.disconnect();
    }

    @Test
    public void testConnect() {
        PurRepository mockPurRepository = Mockito.mock(PurRepository.class);
        PurRepositoryMeta mockPurRepositoryMeta = Mockito.mock(PurRepositoryMeta.class);
        PurRepositoryLocation location = Mockito.mock(PurRepositoryLocation.class);
        RootRef mockRootRef = Mockito.mock(RootRef.class);
        PurRepositoryConnector purRepositoryConnector = Mockito.spy(new PurRepositoryConnector(mockPurRepository, mockPurRepositoryMeta, mockRootRef));
        Mockito.doReturn(location).when(mockPurRepositoryMeta).getRepositoryLocation();
        Mockito.doReturn("").when(location).getUrl();
        ExecutorService service = Mockito.mock(ExecutorService.class);
        Mockito.doReturn(service).when(purRepositoryConnector).getExecutor();
        Future future = Mockito.mock(Future.class);
        try {
            Mockito.doReturn("U1").when(future).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Future future2 = Mockito.mock(Future.class);
        try {
            Mockito.doReturn(false).when(future2).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Future future3 = Mockito.mock(Future.class);
        try {
            Mockito.doReturn(null).when(future3).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Mockito.when(service.submit(ArgumentMatchers.any(Callable.class))).thenReturn(future2).thenReturn(future3).thenReturn(future3).thenReturn(future);
        try {
            RepositoryConnectResult res = purRepositoryConnector.connect("userNam", "password");
            Assert.assertEquals("U1", res.getUser().getLogin());
        } catch (KettleException e) {
            e.printStackTrace();
        }
    }
}

