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
package org.apache.ambari.server.serveraction.kerberos;


import ADKerberosOperationHandler.KERBEROS_ENV_AD_CREATE_ATTRIBUTES_TEMPLATE;
import ADKerberosOperationHandler.KERBEROS_ENV_LDAP_URL;
import ADKerberosOperationHandler.KERBEROS_ENV_PRINCIPAL_CONTAINER_DN;
import com.google.inject.Injector;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import junit.framework.Assert;
import org.apache.ambari.server.security.credential.PrincipalKeyCredential;
import org.easymock.Capture;
import org.junit.Test;


public class ADKerberosOperationHandlerTest extends KerberosOperationHandlerTest {
    private static final String DEFAULT_ADMIN_PRINCIPAL = "cluser_admin@HDP01.LOCAL";

    private static final String DEFAULT_ADMIN_PASSWORD = "Hadoop12345";

    private static final String DEFAULT_LDAP_URL = "ldaps://10.0.100.4";

    private static final String DEFAULT_PRINCIPAL_CONTAINER_DN = "ou=HDP,DC=HDP01,DC=LOCAL";

    private static final String DEFAULT_REALM = "HDP01.LOCAL";

    private static final Map<String, String> KERBEROS_ENV_MAP;

    static {
        Map<String, String> map = new HashMap<>(KerberosOperationHandlerTest.DEFAULT_KERBEROS_ENV_MAP);
        map.put(KERBEROS_ENV_PRINCIPAL_CONTAINER_DN, ADKerberosOperationHandlerTest.DEFAULT_PRINCIPAL_CONTAINER_DN);
        map.put(KERBEROS_ENV_LDAP_URL, ADKerberosOperationHandlerTest.DEFAULT_LDAP_URL);
        KERBEROS_ENV_MAP = Collections.unmodifiableMap(map);
    }

    private static Method methodCreateInitialLdapContext;

    private Injector injector;

    private LdapContext ldapContext;

    @Test(expected = KerberosKDCConnectionException.class)
    public void testOpenExceptionLdapUrlNotProvided() throws Exception {
        KerberosOperationHandler handler = new ADKerberosOperationHandler();
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        Map<String, String> kerberosEnvMap = new HashMap<String, String>() {
            {
                put(KERBEROS_ENV_PRINCIPAL_CONTAINER_DN, ADKerberosOperationHandlerTest.DEFAULT_PRINCIPAL_CONTAINER_DN);
            }
        };
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, kerberosEnvMap);
        handler.close();
    }

    @Test(expected = KerberosLDAPContainerException.class)
    public void testOpenExceptionPrincipalContainerDnNotProvided() throws Exception {
        KerberosOperationHandler handler = new ADKerberosOperationHandler();
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        Map<String, String> kerberosEnvMap = new HashMap<String, String>() {
            {
                put(KERBEROS_ENV_LDAP_URL, ADKerberosOperationHandlerTest.DEFAULT_LDAP_URL);
            }
        };
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, kerberosEnvMap);
        handler.close();
    }

    @Test(expected = KerberosAdminAuthenticationException.class)
    public void testOpenExceptionAdminCredentialsNotProvided() throws Exception {
        KerberosOperationHandler handler = new ADKerberosOperationHandler();
        handler.open(null, ADKerberosOperationHandlerTest.DEFAULT_REALM, getKerberosEnv());
        handler.close();
    }

    @Test(expected = KerberosKDCConnectionException.class)
    public void testOpenExceptionNoLdaps() throws Exception {
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, "hello");
        KerberosOperationHandler handler = new ADKerberosOperationHandler();
        Map<String, String> kerberosEnvMap = new HashMap<String, String>() {
            {
                put(KERBEROS_ENV_LDAP_URL, "ldap://this_wont_work");
                put(KERBEROS_ENV_PRINCIPAL_CONTAINER_DN, ADKerberosOperationHandlerTest.DEFAULT_PRINCIPAL_CONTAINER_DN);
            }
        };
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, kerberosEnvMap);
        handler.close();
    }

    @Test(expected = KerberosAdminAuthenticationException.class)
    public void testTestAdministratorCredentialsIncorrectAdminPassword() throws Exception {
        Injector injector = getInjector();
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, "wrong");
        ADKerberosOperationHandler handler = createMockBuilder(ADKerberosOperationHandler.class).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createInitialLdapContext", Properties.class, Control[].class)).createNiceMock();
        injector.injectMembers(handler);
        expect(handler.createInitialLdapContext(anyObject(Properties.class), anyObject(Control[].class))).andAnswer(new org.easymock.IAnswer<LdapContext>() {
            @Override
            public LdapContext answer() throws Throwable {
                throw new AuthenticationException();
            }
        }).once();
        replayAll();
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, getKerberosEnv());
        handler.testAdministratorCredentials();
        handler.close();
    }

    @Test(expected = KerberosAdminAuthenticationException.class)
    public void testTestAdministratorCredentialsIncorrectAdminPrincipal() throws Exception {
        Injector injector = getInjector();
        PrincipalKeyCredential kc = new PrincipalKeyCredential("wrong", ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        ADKerberosOperationHandler handler = createMockBuilder(ADKerberosOperationHandler.class).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createInitialLdapContext", Properties.class, Control[].class)).createNiceMock();
        injector.injectMembers(handler);
        expect(handler.createInitialLdapContext(anyObject(Properties.class), anyObject(Control[].class))).andAnswer(new org.easymock.IAnswer<LdapContext>() {
            @Override
            public LdapContext answer() throws Throwable {
                throw new AuthenticationException();
            }
        }).once();
        replayAll();
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, getKerberosEnv());
        handler.testAdministratorCredentials();
        handler.close();
    }

    @Test(expected = KerberosKDCConnectionException.class)
    public void testTestAdministratorCredentialsKDCConnectionException() throws Exception {
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        ADKerberosOperationHandler handler = createMockedHandler(ADKerberosOperationHandlerTest.methodCreateInitialLdapContext);
        expect(handler.createInitialLdapContext(anyObject(Properties.class), anyObject(Control[].class))).andAnswer(new org.easymock.IAnswer<LdapContext>() {
            @Override
            public LdapContext answer() throws Throwable {
                throw new CommunicationException();
            }
        }).once();
        replayAll();
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, getKerberosEnv());
        handler.testAdministratorCredentials();
        handler.close();
    }

    @Test
    public void testTestAdministratorCredentialsSuccess() throws Exception {
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        ADKerberosOperationHandler handler = createMockedHandler(ADKerberosOperationHandlerTest.methodCreateInitialLdapContext);
        expect(handler.createInitialLdapContext(anyObject(Properties.class), anyObject(Control[].class))).andAnswer(new org.easymock.IAnswer<LdapContext>() {
            @Override
            public LdapContext answer() throws Throwable {
                LdapContext ldapContext = createNiceMock(LdapContext.class);
                expect(ldapContext.search(anyObject(String.class), anyObject(String.class), anyObject(SearchControls.class))).andAnswer(new org.easymock.IAnswer<javax.naming.NamingEnumeration<SearchResult>>() {
                    @Override
                    public javax.naming.NamingEnumeration<SearchResult> answer() throws Throwable {
                        @SuppressWarnings("unchecked")
                        javax.naming.NamingEnumeration<SearchResult> result = createNiceMock(javax.naming.NamingEnumeration.class);
                        expect(result.hasMore()).andReturn(false).once();
                        replay(result);
                        return result;
                    }
                }).once();
                return ldapContext;
            }
        }).once();
        replayAll();
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, getKerberosEnv());
        handler.testAdministratorCredentials();
        handler.close();
    }

    @Test
    public void testProcessCreateTemplateDefault() throws Exception {
        Injector injector = getInjector();
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        Capture<Name> capturedName = newCapture(CaptureType.ALL);
        Capture<Attributes> capturedAttributes = newCapture(CaptureType.ALL);
        ADKerberosOperationHandler handler = createMockBuilder(ADKerberosOperationHandler.class).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createInitialLdapContext", Properties.class, Control[].class)).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createSearchControls")).createNiceMock();
        injector.injectMembers(handler);
        @SuppressWarnings("unchecked")
        javax.naming.NamingEnumeration<SearchResult> searchResult = createNiceMock(javax.naming.NamingEnumeration.class);
        expect(searchResult.hasMore()).andReturn(false).once();
        LdapContext ldapContext = createNiceMock(LdapContext.class);
        expect(ldapContext.search(anyObject(String.class), anyObject(String.class), anyObject(SearchControls.class))).andReturn(searchResult).once();
        expect(ldapContext.createSubcontext(capture(capturedName), capture(capturedAttributes))).andReturn(createNiceMock(DirContext.class)).anyTimes();
        expect(handler.createInitialLdapContext(anyObject(Properties.class), anyObject(Control[].class))).andReturn(ldapContext).once();
        expect(handler.createSearchControls()).andAnswer(new org.easymock.IAnswer<SearchControls>() {
            @Override
            public SearchControls answer() throws Throwable {
                SearchControls searchControls = createNiceMock(SearchControls.class);
                replay(searchControls);
                return searchControls;
            }
        }).once();
        replayAll();
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, getKerberosEnv());
        handler.createPrincipal("nn/c6501.ambari.apache.org", "secret", true);
        handler.createPrincipal(("hdfs@" + (ADKerberosOperationHandlerTest.DEFAULT_REALM)), "secret", false);
        handler.close();
        List<Attributes> attributesList = capturedAttributes.getValues();
        Attributes attributes;
        attributes = attributesList.get(0);
        String[] objectClasses = new String[]{ "top", "person", "organizationalPerson", "user" };
        Assert.assertNotNull(attributes);
        Assert.assertEquals(7, attributes.size());
        Assert.assertNotNull(attributes.get("objectClass"));
        Assert.assertEquals(objectClasses.length, attributes.get("objectClass").size());
        for (int i = 0; i < (objectClasses.length); i++) {
            Assert.assertEquals(objectClasses[i], attributes.get("objectClass").get(i));
        }
        Assert.assertNotNull(attributes.get("cn"));
        Assert.assertEquals("nn/c6501.ambari.apache.org", attributes.get("cn").get());
        Assert.assertNotNull(attributes.get("servicePrincipalName"));
        Assert.assertEquals("nn/c6501.ambari.apache.org", attributes.get("servicePrincipalName").get());
        Assert.assertNotNull(attributes.get("userPrincipalName"));
        Assert.assertEquals("nn/c6501.ambari.apache.org@HDP01.LOCAL", attributes.get("userPrincipalName").get());
        Assert.assertNotNull(attributes.get("unicodePwd"));
        Assert.assertEquals("\"secret\"", new String(((byte[]) (attributes.get("unicodePwd").get())), Charset.forName("UTF-16LE")));
        Assert.assertNotNull(attributes.get("accountExpires"));
        Assert.assertEquals("0", attributes.get("accountExpires").get());
        Assert.assertNotNull(attributes.get("userAccountControl"));
        Assert.assertEquals("66048", attributes.get("userAccountControl").get());
        attributes = attributesList.get(1);
        Assert.assertNotNull(attributes);
        Assert.assertEquals(6, attributes.size());
        Assert.assertNotNull(attributes.get("objectClass"));
        Assert.assertEquals(objectClasses.length, attributes.get("objectClass").size());
        for (int i = 0; i < (objectClasses.length); i++) {
            Assert.assertEquals(objectClasses[i], attributes.get("objectClass").get(i));
        }
        Assert.assertNotNull(attributes.get("cn"));
        Assert.assertEquals("hdfs", attributes.get("cn").get());
        Assert.assertNotNull(attributes.get("userPrincipalName"));
        Assert.assertEquals("hdfs@HDP01.LOCAL", attributes.get("userPrincipalName").get());
        Assert.assertNotNull(attributes.get("unicodePwd"));
        Assert.assertEquals("\"secret\"", new String(((byte[]) (attributes.get("unicodePwd").get())), Charset.forName("UTF-16LE")));
        Assert.assertNotNull(attributes.get("accountExpires"));
        Assert.assertEquals("0", attributes.get("accountExpires").get());
        Assert.assertNotNull(attributes.get("userAccountControl"));
        Assert.assertEquals("66048", attributes.get("userAccountControl").get());
    }

    @Test
    public void testProcessCreateTemplateCustom() throws Exception {
        Injector injector = getInjector();
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        Map<String, String> kerberosEnvMap = new HashMap<>(getKerberosEnv());
        kerberosEnvMap.put(KERBEROS_ENV_AD_CREATE_ATTRIBUTES_TEMPLATE, ("" + ((((((((((((((((("#set( $user = \"${principal_primary}-${principal_digest}\" )" + "{") + "  \"objectClass\": [") + "    \"top\",") + "    \"person\",") + "    \"organizationalPerson\",") + "    \"user\"") + "  ],") + "  \"cn\": \"$user\",") + "  \"sAMAccountName\": \"$user.substring(0,20)\",") + "  #if( $is_service )") + "  \"servicePrincipalName\": \"$principal_name\",") + "  #end") + "  \"userPrincipalName\": \"$normalized_principal\",") + "  \"unicodePwd\": \"$password\",") + "  \"accountExpires\": \"0\",") + "  \"userAccountControl\": \"66048\"") + "}")));
        Capture<Name> capturedName = newCapture();
        Capture<Attributes> capturedAttributes = newCapture();
        ADKerberosOperationHandler handler = createMockBuilder(ADKerberosOperationHandler.class).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createInitialLdapContext", Properties.class, Control[].class)).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createSearchControls")).createNiceMock();
        injector.injectMembers(handler);
        @SuppressWarnings("unchecked")
        javax.naming.NamingEnumeration<SearchResult> searchResult = createNiceMock(javax.naming.NamingEnumeration.class);
        expect(searchResult.hasMore()).andReturn(false).once();
        LdapContext ldapContext = createNiceMock(LdapContext.class);
        expect(ldapContext.search(anyObject(String.class), anyObject(String.class), anyObject(SearchControls.class))).andReturn(searchResult).once();
        expect(ldapContext.createSubcontext(capture(capturedName), capture(capturedAttributes))).andReturn(createNiceMock(DirContext.class)).once();
        expect(handler.createInitialLdapContext(anyObject(Properties.class), anyObject(Control[].class))).andReturn(ldapContext).once();
        expect(handler.createSearchControls()).andAnswer(new org.easymock.IAnswer<SearchControls>() {
            @Override
            public SearchControls answer() throws Throwable {
                SearchControls searchControls = createNiceMock(SearchControls.class);
                replay(searchControls);
                return searchControls;
            }
        }).once();
        replayAll();
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, kerberosEnvMap);
        handler.createPrincipal("nn/c6501.ambari.apache.org", "secret", true);
        handler.close();
        Attributes attributes = capturedAttributes.getValue();
        String[] objectClasses = new String[]{ "top", "person", "organizationalPerson", "user" };
        Assert.assertNotNull(attributes);
        Assert.assertEquals(8, attributes.size());
        Assert.assertNotNull(attributes.get("objectClass"));
        Assert.assertEquals(objectClasses.length, attributes.get("objectClass").size());
        for (int i = 0; i < (objectClasses.length); i++) {
            Assert.assertEquals(objectClasses[i], attributes.get("objectClass").get(i));
        }
        Assert.assertNotNull(attributes.get("cn"));
        Assert.assertEquals("nn-995e1580db28198e7fda1417ab5d894c877937d2", attributes.get("cn").get());
        Assert.assertNotNull(attributes.get("servicePrincipalName"));
        Assert.assertEquals("nn/c6501.ambari.apache.org", attributes.get("servicePrincipalName").get());
        Assert.assertNotNull(attributes.get("userPrincipalName"));
        Assert.assertEquals("nn/c6501.ambari.apache.org@HDP01.LOCAL", attributes.get("userPrincipalName").get());
        Assert.assertNotNull(attributes.get("sAMAccountName"));
        Assert.assertTrue(((attributes.get("sAMAccountName").get().toString().length()) <= 20));
        Assert.assertEquals("nn-995e1580db28198e7", attributes.get("sAMAccountName").get());
        Assert.assertNotNull(attributes.get("unicodePwd"));
        Assert.assertEquals("\"secret\"", new String(((byte[]) (attributes.get("unicodePwd").get())), Charset.forName("UTF-16LE")));
        Assert.assertNotNull(attributes.get("accountExpires"));
        Assert.assertEquals("0", attributes.get("accountExpires").get());
        Assert.assertNotNull(attributes.get("userAccountControl"));
        Assert.assertEquals("66048", attributes.get("userAccountControl").get());
    }

    @Test
    public void testDigests() throws Exception {
        Injector injector = getInjector();
        PrincipalKeyCredential kc = new PrincipalKeyCredential(ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PRINCIPAL, ADKerberosOperationHandlerTest.DEFAULT_ADMIN_PASSWORD);
        Map<String, String> kerberosEnvMap = new HashMap<>(getKerberosEnv());
        kerberosEnvMap.put(KERBEROS_ENV_AD_CREATE_ATTRIBUTES_TEMPLATE, ("" + (((("{" + "\"principal_digest\": \"$principal_digest\",") + "\"principal_digest_256\": \"$principal_digest_256\",") + "\"principal_digest_512\": \"$principal_digest_512\"") + "}")));
        Capture<Attributes> capturedAttributes = newCapture();
        ADKerberosOperationHandler handler = createMockBuilder(ADKerberosOperationHandler.class).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createInitialLdapContext", Properties.class, Control[].class)).addMockedMethod(ADKerberosOperationHandler.class.getDeclaredMethod("createSearchControls")).createNiceMock();
        injector.injectMembers(handler);
        @SuppressWarnings("unchecked")
        javax.naming.NamingEnumeration<SearchResult> searchResult = createNiceMock(javax.naming.NamingEnumeration.class);
        expect(searchResult.hasMore()).andReturn(false).once();
        LdapContext ldapContext = createNiceMock(LdapContext.class);
        expect(ldapContext.search(anyObject(String.class), anyObject(String.class), anyObject(SearchControls.class))).andReturn(searchResult).once();
        expect(ldapContext.createSubcontext(anyObject(Name.class), capture(capturedAttributes))).andReturn(createNiceMock(DirContext.class)).once();
        expect(handler.createInitialLdapContext(anyObject(Properties.class), anyObject(Control[].class))).andReturn(ldapContext).once();
        expect(handler.createSearchControls()).andAnswer(new org.easymock.IAnswer<SearchControls>() {
            @Override
            public SearchControls answer() throws Throwable {
                SearchControls searchControls = createNiceMock(SearchControls.class);
                replay(searchControls);
                return searchControls;
            }
        }).once();
        replayAll();
        handler.open(kc, ADKerberosOperationHandlerTest.DEFAULT_REALM, kerberosEnvMap);
        handler.createPrincipal("nn/c6501.ambari.apache.org", "secret", true);
        handler.close();
        Attributes attributes = capturedAttributes.getValue();
        Assert.assertNotNull(attributes);
        Assert.assertEquals("995e1580db28198e7fda1417ab5d894c877937d2", attributes.get("principal_digest").get());
        Assert.assertEquals("b65bc066d11ac8b1beb31dc84035d9c204736f823decf8dfedda05a30e4ae410", attributes.get("principal_digest_256").get());
        Assert.assertEquals("f48de28bc0467d764f5b04dbf04d35ff329a80277614be35eda0d0deed7f1c074cc5b0e0dc361130fdb078e09eb0ca545b9c653388192508ef382af89bd3a80c", attributes.get("principal_digest_512").get());
    }

    @Test
    public void testCreateLdapContextSSLSocketFactoryTrusting() throws Exception {
        testCreateLdapContextSSLSocketFactory(true);
    }

    @Test
    public void testCreateLdapContextSSLSocketFactoryNonTrusting() throws Exception {
        testCreateLdapContextSSLSocketFactory(false);
    }
}

