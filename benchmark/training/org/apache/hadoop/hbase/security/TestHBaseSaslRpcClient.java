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
package org.apache.hadoop.hbase.security;


import AuthMethod.DIGEST;
import AuthMethod.KERBEROS;
import AuthMethod.SIMPLE;
import SaslUtil.QualityOfProtection;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.Sasl;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.security.AbstractHBaseSaslRpcClient.SaslClientCallbackHandler;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static AuthMethod.DIGEST;


@Category({ SecurityTests.class, SmallTests.class })
public class TestHBaseSaslRpcClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseSaslRpcClient.class);

    static {
        System.setProperty("java.security.krb5.realm", "DOMAIN.COM");
        System.setProperty("java.security.krb5.kdc", "DOMAIN.COM");
    }

    static final String DEFAULT_USER_NAME = "principal";

    static final String DEFAULT_USER_PASSWORD = "password";

    private static final Logger LOG = Logger.getLogger(TestHBaseSaslRpcClient.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testSaslClientUsesGivenRpcProtection() throws Exception {
        Token<? extends TokenIdentifier> token = createTokenMockWithCredentials(TestHBaseSaslRpcClient.DEFAULT_USER_NAME, TestHBaseSaslRpcClient.DEFAULT_USER_PASSWORD);
        for (SaslUtil.QualityOfProtection qop : QualityOfProtection.values()) {
            String negotiatedQop = new HBaseSaslRpcClient(DIGEST, token, "principal/host@DOMAIN.COM", false, qop.name(), false) {
                public String getQop() {
                    return saslProps.get(Sasl.QOP);
                }
            }.getQop();
            Assert.assertEquals(negotiatedQop, qop.getSaslQop());
        }
    }

    @Test
    public void testSaslClientCallbackHandler() throws UnsupportedCallbackException {
        final Token<? extends TokenIdentifier> token = createTokenMock();
        Mockito.when(token.getIdentifier()).thenReturn(Bytes.toBytes(TestHBaseSaslRpcClient.DEFAULT_USER_NAME));
        Mockito.when(token.getPassword()).thenReturn(Bytes.toBytes(TestHBaseSaslRpcClient.DEFAULT_USER_PASSWORD));
        final NameCallback nameCallback = Mockito.mock(NameCallback.class);
        final PasswordCallback passwordCallback = Mockito.mock(PasswordCallback.class);
        final RealmCallback realmCallback = Mockito.mock(RealmCallback.class);
        final RealmChoiceCallback realmChoiceCallback = Mockito.mock(RealmChoiceCallback.class);
        Callback[] callbackArray = new Callback[]{ nameCallback, passwordCallback, realmCallback, realmChoiceCallback };
        final SaslClientCallbackHandler saslClCallbackHandler = new SaslClientCallbackHandler(token);
        saslClCallbackHandler.handle(callbackArray);
        Mockito.verify(nameCallback).setName(ArgumentMatchers.anyString());
        Mockito.verify(realmCallback).setText(ArgumentMatchers.any());
        Mockito.verify(passwordCallback).setPassword(ArgumentMatchers.any());
    }

    @Test
    public void testSaslClientCallbackHandlerWithException() {
        final Token<? extends TokenIdentifier> token = createTokenMock();
        Mockito.when(token.getIdentifier()).thenReturn(Bytes.toBytes(TestHBaseSaslRpcClient.DEFAULT_USER_NAME));
        Mockito.when(token.getPassword()).thenReturn(Bytes.toBytes(TestHBaseSaslRpcClient.DEFAULT_USER_PASSWORD));
        final SaslClientCallbackHandler saslClCallbackHandler = new SaslClientCallbackHandler(token);
        try {
            saslClCallbackHandler.handle(new Callback[]{ Mockito.mock(TextOutputCallback.class) });
        } catch (UnsupportedCallbackException expEx) {
            // expected
        } catch (Exception ex) {
            Assert.fail(("testSaslClientCallbackHandlerWithException error : " + (ex.getMessage())));
        }
    }

    @Test
    public void testHBaseSaslRpcClientCreation() throws Exception {
        // creation kerberos principal check section
        Assert.assertFalse(assertSuccessCreationKerberosPrincipal(null));
        Assert.assertFalse(assertSuccessCreationKerberosPrincipal("DOMAIN.COM"));
        Assert.assertFalse(assertSuccessCreationKerberosPrincipal("principal/DOMAIN.COM"));
        if (!(assertSuccessCreationKerberosPrincipal("principal/localhost@DOMAIN.COM"))) {
            // XXX: This can fail if kerberos support in the OS is not sane, see HBASE-10107.
            // For now, don't assert, just warn
            TestHBaseSaslRpcClient.LOG.warn("Could not create a SASL client with valid Kerberos credential");
        }
        // creation digest principal check section
        Assert.assertFalse(assertSuccessCreationDigestPrincipal(null, null));
        Assert.assertFalse(assertSuccessCreationDigestPrincipal("", ""));
        Assert.assertFalse(assertSuccessCreationDigestPrincipal("", null));
        Assert.assertFalse(assertSuccessCreationDigestPrincipal(null, ""));
        Assert.assertTrue(assertSuccessCreationDigestPrincipal(TestHBaseSaslRpcClient.DEFAULT_USER_NAME, TestHBaseSaslRpcClient.DEFAULT_USER_PASSWORD));
        // creation simple principal check section
        Assert.assertFalse(assertSuccessCreationSimplePrincipal("", ""));
        Assert.assertFalse(assertSuccessCreationSimplePrincipal(null, null));
        Assert.assertFalse(assertSuccessCreationSimplePrincipal(TestHBaseSaslRpcClient.DEFAULT_USER_NAME, TestHBaseSaslRpcClient.DEFAULT_USER_PASSWORD));
        // exceptions check section
        Assert.assertTrue(assertIOExceptionThenSaslClientIsNull(TestHBaseSaslRpcClient.DEFAULT_USER_NAME, TestHBaseSaslRpcClient.DEFAULT_USER_PASSWORD));
        Assert.assertTrue(assertIOExceptionWhenGetStreamsBeforeConnectCall(TestHBaseSaslRpcClient.DEFAULT_USER_NAME, TestHBaseSaslRpcClient.DEFAULT_USER_PASSWORD));
    }

    @Test
    public void testAuthMethodReadWrite() throws IOException {
        DataInputBuffer in = new DataInputBuffer();
        DataOutputBuffer out = new DataOutputBuffer();
        assertAuthMethodRead(in, SIMPLE);
        assertAuthMethodRead(in, KERBEROS);
        assertAuthMethodRead(in, DIGEST);
        assertAuthMethodWrite(out, SIMPLE);
        assertAuthMethodWrite(out, KERBEROS);
        assertAuthMethodWrite(out, DIGEST);
    }
}

