/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.job.entries.ftpput;


import com.enterprisedt.net.ftp.FTPClient;
import java.net.InetAddress;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class JobEntryFTPPUTTest {
    private JobEntryFTPPUT entry;

    private FTPClient ftpClient;

    @Test
    public void createFtpClient_SetsLocalhostByDefault() throws Exception {
        entry.setServerName(null);
        entry.createAndSetUpFtpClient();
        Assert.assertEquals("localhost", getHostFromClient());
    }

    @Test
    public void createFtpClient_DoesNotChangePortByDefault() throws Exception {
        entry.setServerPort(null);
        entry.createAndSetUpFtpClient();
        Mockito.verify(ftpClient, Mockito.never()).setRemotePort(ArgumentMatchers.anyInt());
    }

    @Test
    public void createFtpClient_UsesProxyIfSet() throws Exception {
        entry.setProxyHost("localhost");
        entry.setProxyPort("123");
        entry.createAndSetUpFtpClient();
        Assert.assertEquals("localhost", getHostFromClient());
        // we cannot distinguish values of proxy and target server, as both of them refers to localhost
        // that is why I put invocation counter here
        Mockito.verify(ftpClient, Mockito.times(2)).setRemoteAddr(InetAddress.getByName("localhost"));
        Mockito.verify(ftpClient).setRemotePort(123);
    }

    @Test
    public void createFtpClient_UsesTimeoutIfSet() throws Exception {
        entry.setTimeout(10);
        entry.createAndSetUpFtpClient();
        Mockito.verify(ftpClient).setTimeout(10);
    }
}

