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
package org.pentaho.di.job.entries.sftp;


import SFTPClient.ENV_PARAM_USERAUTH_GSSAPI;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.net.InetAddress;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleJobException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class SFTPClientTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private int port = 22;

    private String username = "admin";

    private String password = "password";

    private Session session = Mockito.mock(Session.class);

    private ChannelSftp channel = Mockito.mock(ChannelSftp.class);

    private InetAddress server = Mockito.mock(InetAddress.class);

    private JSch jSch = Mockito.mock(JSch.class);

    /**
     * Given SFTP connection configuration, and -Duserauth.gssapi.enabled param was NOT passed on application start.
     * <br/>
     * When SFTP Client is instantiated, then preferred authentications list should not contain
     * GSS API Authentication.
     */
    @Test
    public void shouldExcludeGssapiFromPreferredAuthenticationsByDefault() throws Exception {
        new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        };
        Mockito.verify(session).setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
    }

    /**
     * Given SFTP connection configuration, and -Duserauth.gssapi.enabled param
     * was passed on application start with correct value.
     * <br/>
     * When SFTP Client is instantiated, then preferred authentications list should contain
     * GSS API Authentication as the last one.
     */
    @Test
    public void shouldIncludeGssapiToPreferredAuthenticationsIfSpecified() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "true");
        new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        };
        Mockito.verify(session).setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password,gssapi-with-mic");
    }

    /**
     * Given SFTP connection configuration, and -Duserauth.gssapi.enabled param
     * was passed on application start with incorrect value.
     * <br/>
     * When SFTP Client is instantiated, then preferred authentications list should not contain
     * GSS API Authentication.
     */
    @Test
    public void shouldIncludeGssapiToPreferredAuthenticationsIfOnlySpecifiedCorrectly() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "yes");
        new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        };
        Mockito.verify(session).setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
    }

    /**
     * Can't create root folder. An exception is expected.
     */
    @Test(expected = KettleJobException.class)
    public void folderCreationEmptyTest() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "yes");
        SFTPClient client = new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        };
        client.login(password);
        client.createFolder("//");
    }

    /**
     * Create a folder under the current user's home.
     */
    @Test
    public void folderCreation_Relative_Simple() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "yes");
        SFTPClient client = Mockito.spy(new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        });
        Mockito.doReturn("/home/admin").when(client).pwd();
        client.login(password);
        client.createFolder("myfolder");
        Mockito.verify(channel, Mockito.times(1)).mkdir(ArgumentMatchers.anyString());
        Mockito.verify(channel, Mockito.times(1)).mkdir("/home/admin/myfolder");
    }

    /**
     * Create a folder with nested folders under the current user's home.
     */
    @Test
    public void folderCreation_Relative_Nested() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "yes");
        SFTPClient client = Mockito.spy(new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        });
        Mockito.doReturn("/home/admin").when(client).pwd();
        client.login(password);
        client.createFolder("myfolder/subfolder/finalfolder");
        Mockito.verify(channel, Mockito.times(3)).mkdir(ArgumentMatchers.anyString());
        Mockito.verify(channel, Mockito.times(1)).mkdir("/home/admin/myfolder");
        Mockito.verify(channel, Mockito.times(1)).mkdir("/home/admin/myfolder/subfolder");
        Mockito.verify(channel, Mockito.times(1)).mkdir("/home/admin/myfolder/subfolder/finalfolder");
    }

    /**
     * Create a folder under an existing folder given an absolute path.
     */
    @Test
    public void folderCreation_Absolute_Simple() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "yes");
        SFTPClient client = Mockito.spy(new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        });
        Mockito.doReturn(true).when(client).folderExists("/var");
        Mockito.doReturn(true).when(client).folderExists("/var/ftproot");
        client.login(password);
        client.createFolder("/var/ftproot/myfolder");
        Mockito.verify(channel, Mockito.times(1)).mkdir(ArgumentMatchers.anyString());
        Mockito.verify(channel, Mockito.times(1)).mkdir("/var/ftproot/myfolder");
    }

    /**
     * Create a folder under an existing folder given an absolute path.
     * The specified folder ends with a slash.
     */
    @Test
    public void folderCreation_Absolute_TrailingSlash() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "yes");
        SFTPClient client = Mockito.spy(new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        });
        Mockito.doReturn(true).when(client).folderExists("/var");
        Mockito.doReturn(true).when(client).folderExists("/var/ftproot");
        client.login(password);
        client.createFolder("/var/ftproot/myfolder/");
        Mockito.verify(channel, Mockito.times(1)).mkdir(ArgumentMatchers.anyString());
        Mockito.verify(channel, Mockito.times(1)).mkdir("/var/ftproot/myfolder");
    }

    /**
     * Create a folder with nested folders under an existing folder given an absolute path.
     */
    @Test
    public void folderCreation_Absolute_Nested() throws Exception {
        System.setProperty(ENV_PARAM_USERAUTH_GSSAPI, "yes");
        SFTPClient client = Mockito.spy(new SFTPClient(server, port, username) {
            @Override
            JSch createJSch() {
                return jSch;
            }
        });
        Mockito.doReturn(true).when(client).folderExists("/var");
        Mockito.doReturn(true).when(client).folderExists("/var/ftproot");
        client.login(password);
        client.createFolder("/var/ftproot/myfolder/subfolder/finalfolder");
        Mockito.verify(channel, Mockito.times(3)).mkdir(ArgumentMatchers.anyString());
        Mockito.verify(channel, Mockito.times(1)).mkdir("/var/ftproot/myfolder");
        Mockito.verify(channel, Mockito.times(1)).mkdir("/var/ftproot/myfolder/subfolder");
        Mockito.verify(channel, Mockito.times(1)).mkdir("/var/ftproot/myfolder/subfolder/finalfolder");
    }
}

