/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.ssh;


import com.trilead.ssh2.Connection;
import com.trilead.ssh2.HTTPProxyData;
import com.trilead.ssh2.ServerHostKeyVerifier;
import java.io.ByteArrayInputStream;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.vfs.KettleVFS;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ SSHData.class, KettleVFS.class })
public class SSHDataTest {
    @Mock
    Connection connection;

    @Mock
    FileObject fileObject;

    @Mock
    FileContent fileContent;

    @Mock
    VariableSpace variableSpace;

    String server = "testServerUrl";

    String keyFilePath = "keyFilePath";

    String passPhrase = "passPhrase";

    String username = "username";

    String password = "password";

    String proxyUsername = "proxyUsername";

    String proxyPassword = "proxyPassword";

    String proxyHost = "proxyHost";

    int port = 22;

    int proxyPort = 23;

    @Test
    public void testOpenConnection_1() throws Exception {
        Mockito.when(connection.authenticateWithPassword(username, password)).thenReturn(true);
        Assert.assertNotNull(SSHData.OpenConnection(server, port, username, password, false, null, null, 0, null, null, 0, null, null));
        Mockito.verify(connection).connect();
        Mockito.verify(connection).authenticateWithPassword(username, password);
    }

    @Test(expected = KettleException.class)
    public void testOpenConnection_2() throws Exception {
        Mockito.when(connection.authenticateWithPassword(username, password)).thenReturn(false);
        SSHData.OpenConnection(server, port, username, password, false, null, null, 0, null, null, 0, null, null);
        Mockito.verify(connection).connect();
        Mockito.verify(connection).authenticateWithPassword(username, password);
    }

    @Test(expected = KettleException.class)
    public void testOpenConnectionUseKey_1() throws Exception {
        Mockito.when(fileObject.exists()).thenReturn(false);
        SSHData.OpenConnection(server, port, null, null, true, null, null, 0, null, null, 0, null, null);
        Mockito.verify(fileObject).exists();
    }

    @Test
    public void testOpenConnectionUseKey_2() throws Exception {
        Mockito.when(fileObject.exists()).thenReturn(true);
        Mockito.when(fileObject.getContent()).thenReturn(fileContent);
        Mockito.when(fileContent.getSize()).thenReturn(1000L);
        Mockito.when(fileContent.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 1, 2, 3, 4, 5 }));
        Mockito.when(variableSpace.environmentSubstitute(passPhrase)).thenReturn(passPhrase);
        Mockito.when(connection.authenticateWithPublicKey(ArgumentMatchers.eq(username), Matchers.<char[]>any(), ArgumentMatchers.eq(passPhrase))).thenReturn(true);
        SSHData.OpenConnection(server, port, username, null, true, keyFilePath, passPhrase, 0, variableSpace, null, 0, null, null);
        Mockito.verify(connection).connect();
        Mockito.verify(connection).authenticateWithPublicKey(ArgumentMatchers.eq(username), Matchers.<char[]>any(), ArgumentMatchers.eq(passPhrase));
    }

    @Test
    public void testOpenConnectionProxy() throws Exception {
        Mockito.when(connection.authenticateWithPassword(username, password)).thenReturn(true);
        Assert.assertNotNull(SSHData.OpenConnection(server, port, username, password, false, null, null, 0, null, proxyHost, proxyPort, proxyUsername, proxyPassword));
        Mockito.verify(connection).connect();
        Mockito.verify(connection).authenticateWithPassword(username, password);
        Mockito.verify(connection).setProxyData(ArgumentMatchers.any(HTTPProxyData.class));
    }

    @Test
    public void testOpenConnectionTimeOut() throws Exception {
        Mockito.when(connection.authenticateWithPassword(username, password)).thenReturn(true);
        Assert.assertNotNull(SSHData.OpenConnection(server, port, username, password, false, null, null, 100, null, null, proxyPort, proxyUsername, proxyPassword));
        Mockito.verify(connection).connect(ArgumentMatchers.isNull(ServerHostKeyVerifier.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq((100 * 1000)));
    }
}

