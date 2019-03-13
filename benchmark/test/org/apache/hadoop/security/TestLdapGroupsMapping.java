/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.security;


import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import LdapGroupsMapping.BASE_DN_KEY;
import LdapGroupsMapping.BIND_PASSWORD_ALIAS_KEY;
import LdapGroupsMapping.BIND_PASSWORD_KEY;
import LdapGroupsMapping.GROUP_BASE_DN_KEY;
import LdapGroupsMapping.LDAP_KEYSTORE_PASSWORD_KEY;
import LdapGroupsMapping.USER_BASE_DN_KEY;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.alias.CredentialProvider;
import org.apache.hadoop.security.alias.CredentialProviderFactory;
import org.apache.hadoop.security.alias.JavaKeyStoreProvider;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("unchecked")
public class TestLdapGroupsMapping extends TestLdapGroupsMappingBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestLdapGroupsMapping.class);

    /**
     * To construct a LDAP InitialDirContext object, it will firstly initiate a
     * protocol session to server for authentication. After a session is
     * established, a method of authentication is negotiated between the server
     * and the client. When the client is authenticated, the LDAP server will send
     * a bind response, whose message contents are bytes as the
     * {@link #AUTHENTICATE_SUCCESS_MSG}. After receiving this bind response
     * message, the LDAP context is considered connected to the server and thus
     * can issue query requests for determining group membership.
     */
    private static final byte[] AUTHENTICATE_SUCCESS_MSG = new byte[]{ 48, 12, 2, 1, 1, 97, 7, 10, 1, 0, 4, 0, 4, 0 };

    private final String userDN = "CN=some_user,DC=test,DC=com";

    private static final String TEST_LDAP_URL = "ldap://test";

    @Test
    public void testGetGroups() throws NamingException {
        // The search functionality of the mock context is reused, so we will
        // return the user NamingEnumeration first, and then the group
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(getUserNames(), getGroupNames());
        doTestGetGroups(Arrays.asList(getTestGroups()), 2);
    }

    @Test
    public void testGetGroupsWithDifferentBaseDNs() throws Exception {
        Configuration conf = getBaseConf(TestLdapGroupsMapping.TEST_LDAP_URL);
        String userBaseDN = "ou=Users,dc=xxx,dc=com ";
        String groupBaseDN = " ou=Groups,dc=xxx,dc=com";
        conf.set(USER_BASE_DN_KEY, userBaseDN);
        conf.set(GROUP_BASE_DN_KEY, groupBaseDN);
        doTestGetGroupsWithBaseDN(conf, userBaseDN.trim(), groupBaseDN.trim());
    }

    @Test
    public void testGetGroupsWithDefaultBaseDN() throws Exception {
        Configuration conf = getBaseConf(TestLdapGroupsMapping.TEST_LDAP_URL);
        String baseDN = " dc=xxx,dc=com ";
        conf.set(BASE_DN_KEY, baseDN);
        doTestGetGroupsWithBaseDN(conf, baseDN.trim(), baseDN.trim());
    }

    @Test
    public void testGetGroupsWithHierarchy() throws NamingException {
        // The search functionality of the mock context is reused, so we will
        // return the user NamingEnumeration first, and then the group
        // The parent search is run once for each level, and is a different search
        // The parent group is returned once for each group, yet the final list
        // should be unique
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenReturn(getUserNames(), getGroupNames());
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(SearchControls.class))).thenReturn(getParentGroupNames());
        doTestGetGroupsWithParent(Arrays.asList(getTestParentGroups()), 2, 1);
    }

    @Test
    public void testGetGroupsWithConnectionClosed() throws NamingException {
        // The case mocks connection is closed/gc-ed, so the first search call throws CommunicationException,
        // then after reconnected return the user NamingEnumeration first, and then the group
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenThrow(new CommunicationException("Connection is closed")).thenReturn(getUserNames(), getGroupNames());
        // Although connection is down but after reconnected
        // it still should retrieve the result groups
        // 1 is the first failure call
        doTestGetGroups(Arrays.asList(getTestGroups()), (1 + 2));
    }

    @Test
    public void testGetGroupsWithLdapDown() throws NamingException {
        // This mocks the case where Ldap server is down, and always throws CommunicationException
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenThrow(new CommunicationException("Connection is closed"));
        // Ldap server is down, no groups should be retrieved
        doTestGetGroups(Arrays.asList(new String[]{  }), 4);
    }

    @Test
    public void testExtractPassword() throws IOException {
        File testDir = GenericTestUtils.getTestDir();
        testDir.mkdirs();
        File secretFile = new File(testDir, "secret.txt");
        Writer writer = new FileWriter(secretFile);
        writer.write("hadoop");
        writer.close();
        LdapGroupsMapping mapping = new LdapGroupsMapping();
        Assert.assertEquals("hadoop", mapping.extractPassword(secretFile.getPath()));
    }

    @Test
    public void testConfGetPassword() throws Exception {
        File testDir = GenericTestUtils.getTestDir();
        Configuration conf = getBaseConf();
        final Path jksPath = new Path(testDir.toString(), "test.jks");
        final String ourUrl = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri());
        File file = new File(testDir, "test.jks");
        file.delete();
        conf.set(CREDENTIAL_PROVIDER_PATH, ourUrl);
        CredentialProvider provider = CredentialProviderFactory.getProviders(conf).get(0);
        char[] bindpass = new char[]{ 'b', 'i', 'n', 'd', 'p', 'a', 's', 's' };
        char[] storepass = new char[]{ 's', 't', 'o', 'r', 'e', 'p', 'a', 's', 's' };
        // ensure that we get nulls when the key isn't there
        Assert.assertNull(provider.getCredentialEntry(BIND_PASSWORD_KEY));
        Assert.assertNull(provider.getCredentialEntry(LDAP_KEYSTORE_PASSWORD_KEY));
        // create new aliases
        try {
            provider.createCredentialEntry(BIND_PASSWORD_KEY, bindpass);
            provider.createCredentialEntry(LDAP_KEYSTORE_PASSWORD_KEY, storepass);
            provider.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        // make sure we get back the right key
        Assert.assertArrayEquals(bindpass, provider.getCredentialEntry(BIND_PASSWORD_KEY).getCredential());
        Assert.assertArrayEquals(storepass, provider.getCredentialEntry(LDAP_KEYSTORE_PASSWORD_KEY).getCredential());
        LdapGroupsMapping mapping = new LdapGroupsMapping();
        Assert.assertEquals("bindpass", mapping.getPassword(conf, BIND_PASSWORD_KEY, ""));
        Assert.assertEquals("storepass", mapping.getPassword(conf, LDAP_KEYSTORE_PASSWORD_KEY, ""));
        // let's make sure that a password that doesn't exist returns an
        // empty string as currently expected and used to trigger a call to
        // extract password
        Assert.assertEquals("", mapping.getPassword(conf, "invalid-alias", ""));
    }

    @Test
    public void testConfGetPasswordUsingAlias() throws Exception {
        File testDir = GenericTestUtils.getTestDir();
        Configuration conf = getBaseConf();
        final Path jksPath = new Path(testDir.toString(), "test.jks");
        final String ourUrl = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri());
        File file = new File(testDir, "test.jks");
        file.delete();
        conf.set(CREDENTIAL_PROVIDER_PATH, ourUrl);
        // Set alias
        String bindpassAlias = "bindpassAlias";
        conf.set(BIND_PASSWORD_ALIAS_KEY, bindpassAlias);
        CredentialProvider provider = CredentialProviderFactory.getProviders(conf).get(0);
        char[] bindpass = "bindpass".toCharArray();
        // Ensure that we get null when the key isn't there
        Assert.assertNull(provider.getCredentialEntry(bindpassAlias));
        // Create credential for the alias
        provider.createCredentialEntry(bindpassAlias, bindpass);
        provider.flush();
        // Make sure we get back the right key
        Assert.assertArrayEquals(bindpass, provider.getCredentialEntry(bindpassAlias).getCredential());
        LdapGroupsMapping mapping = new LdapGroupsMapping();
        Assert.assertEquals("bindpass", mapping.getPasswordFromCredentialProviders(conf, bindpassAlias, ""));
        // Empty for an invalid alias
        Assert.assertEquals("", mapping.getPasswordFromCredentialProviders(conf, "invalid-alias", ""));
    }

    /**
     * Test that if the {@link LdapGroupsMapping#CONNECTION_TIMEOUT} is set in the
     * configuration, the LdapGroupsMapping connection will timeout by this value
     * if it does not get a LDAP response from the server.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 30000)
    public void testLdapConnectionTimeout() throws IOException, InterruptedException {
        final int connectionTimeoutMs = 3 * 1000;// 3s

        try (ServerSocket serverSock = new ServerSocket(0)) {
            final CountDownLatch finLatch = new CountDownLatch(1);
            // Below we create a LDAP server which will accept a client request;
            // but it will never reply to the bind (connect) request.
            // Client of this LDAP server is expected to get a connection timeout.
            final Thread ldapServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        try (Socket ignored = serverSock.accept()) {
                            finLatch.await();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ldapServer.start();
            final LdapGroupsMapping mapping = new LdapGroupsMapping();
            String ldapUrl = "ldap://localhost:" + (serverSock.getLocalPort());
            final Configuration conf = getBaseConf(ldapUrl, null);
            conf.setInt(LdapGroupsMapping.CONNECTION_TIMEOUT, connectionTimeoutMs);
            mapping.setConf(conf);
            try {
                mapping.doGetGroups("hadoop", 1);
                Assert.fail("The LDAP query should have timed out!");
            } catch (NamingException ne) {
                TestLdapGroupsMapping.LOG.debug("Got the exception while LDAP querying: ", ne);
                GenericTestUtils.assertExceptionContains((("LDAP response read timed out, timeout used:" + connectionTimeoutMs) + "ms"), ne);
                Assert.assertFalse(ne.getMessage().contains("remaining name"));
            } finally {
                finLatch.countDown();
            }
            ldapServer.join();
        }
    }

    /**
     * Test that if the {@link LdapGroupsMapping#READ_TIMEOUT} is set in the
     * configuration, the LdapGroupsMapping query will timeout by this value if
     * it does not get a LDAP response from the server.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 30000)
    public void testLdapReadTimeout() throws IOException, InterruptedException {
        final int readTimeoutMs = 4 * 1000;// 4s

        try (ServerSocket serverSock = new ServerSocket(0)) {
            final CountDownLatch finLatch = new CountDownLatch(1);
            // Below we create a LDAP server which will accept a client request,
            // authenticate it successfully; but it will never reply to the following
            // query request.
            // Client of this LDAP server is expected to get a read timeout.
            final Thread ldapServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        try (Socket clientSock = serverSock.accept()) {
                            IOUtils.skipFully(clientSock.getInputStream(), 1);
                            clientSock.getOutputStream().write(TestLdapGroupsMapping.AUTHENTICATE_SUCCESS_MSG);
                            finLatch.await();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ldapServer.start();
            final LdapGroupsMapping mapping = new LdapGroupsMapping();
            String ldapUrl = "ldap://localhost:" + (serverSock.getLocalPort());
            final Configuration conf = getBaseConf(ldapUrl, null);
            conf.setInt(LdapGroupsMapping.READ_TIMEOUT, readTimeoutMs);
            mapping.setConf(conf);
            try {
                mapping.doGetGroups("hadoop", 1);
                Assert.fail("The LDAP query should have timed out!");
            } catch (NamingException ne) {
                TestLdapGroupsMapping.LOG.debug("Got the exception while LDAP querying: ", ne);
                GenericTestUtils.assertExceptionContains((("LDAP response read timed out, timeout used:" + readTimeoutMs) + "ms"), ne);
                GenericTestUtils.assertExceptionContains("remaining name", ne);
            } finally {
                finLatch.countDown();
            }
            ldapServer.join();
        }
    }

    /**
     * Make sure that when
     * {@link Configuration#getPassword(String)} throws an IOException,
     * {@link LdapGroupsMapping#setConf(Configuration)} does not throw an NPE.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testSetConf() throws Exception {
        Configuration conf = getBaseConf(TestLdapGroupsMapping.TEST_LDAP_URL);
        Configuration mockConf = Mockito.spy(conf);
        Mockito.when(mockConf.getPassword(ArgumentMatchers.anyString())).thenThrow(new IOException("injected IOException"));
        LdapGroupsMapping groupsMapping = getGroupsMapping();
        groupsMapping.setConf(mockConf);
    }
}

