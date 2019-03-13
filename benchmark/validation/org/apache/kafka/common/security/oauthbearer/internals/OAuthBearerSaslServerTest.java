/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.security.oauthbearer.internals;


import OAuthBearerLoginModule.OAUTHBEARER_MECHANISM;
import SaslConfigs.SASL_JAAS_CONFIG;
import SaslInternalConfigs.CREDENTIAL_LIFETIME_MS_SASL_NEGOTIATED_PROPERTY_KEY;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.common.errors.SaslAuthenticationException;
import org.apache.kafka.common.security.JaasContext;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerExtensionsValidatorCallback;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenMock;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerUnsecuredLoginCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerUnsecuredValidatorCallbackHandler;
import org.junit.Assert;
import org.junit.Test;


public class OAuthBearerSaslServerTest {
    private static final String USER = "user";

    private static final Map<String, ?> CONFIGS;

    static {
        String jaasConfigText = (("org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule Required" + " unsecuredLoginStringClaim_sub=\"") + (OAuthBearerSaslServerTest.USER)) + "\";";
        Map<String, Object> tmp = new HashMap<>();
        tmp.put(SASL_JAAS_CONFIG, new Password(jaasConfigText));
        CONFIGS = Collections.unmodifiableMap(tmp);
    }

    private static final AuthenticateCallbackHandler LOGIN_CALLBACK_HANDLER;

    static {
        LOGIN_CALLBACK_HANDLER = new OAuthBearerUnsecuredLoginCallbackHandler();
        OAuthBearerSaslServerTest.LOGIN_CALLBACK_HANDLER.configure(OAuthBearerSaslServerTest.CONFIGS, OAUTHBEARER_MECHANISM, JaasContext.loadClientContext(OAuthBearerSaslServerTest.CONFIGS).configurationEntries());
    }

    private static final AuthenticateCallbackHandler VALIDATOR_CALLBACK_HANDLER;

    private static final AuthenticateCallbackHandler EXTENSIONS_VALIDATOR_CALLBACK_HANDLER;

    static {
        VALIDATOR_CALLBACK_HANDLER = new OAuthBearerUnsecuredValidatorCallbackHandler();
        OAuthBearerSaslServerTest.VALIDATOR_CALLBACK_HANDLER.configure(OAuthBearerSaslServerTest.CONFIGS, OAUTHBEARER_MECHANISM, JaasContext.loadClientContext(OAuthBearerSaslServerTest.CONFIGS).configurationEntries());
        // only validate extensions "firstKey" and "secondKey"
        EXTENSIONS_VALIDATOR_CALLBACK_HANDLER = new OAuthBearerUnsecuredValidatorCallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
                for (Callback callback : callbacks) {
                    if (callback instanceof OAuthBearerValidatorCallback) {
                        OAuthBearerValidatorCallback validationCallback = ((OAuthBearerValidatorCallback) (callback));
                        validationCallback.token(new OAuthBearerTokenMock());
                    } else
                        if (callback instanceof OAuthBearerExtensionsValidatorCallback) {
                            OAuthBearerExtensionsValidatorCallback extensionsCallback = ((OAuthBearerExtensionsValidatorCallback) (callback));
                            extensionsCallback.valid("firstKey");
                            extensionsCallback.valid("secondKey");
                        } else
                            throw new UnsupportedCallbackException(callback);


                }
            }
        };
    }

    private OAuthBearerSaslServer saslServer;

    @Test
    public void noAuthorizationIdSpecified() throws Exception {
        byte[] nextChallenge = saslServer.evaluateResponse(clientInitialResponse(null));
        // also asserts that no authentication error is thrown if OAuthBearerExtensionsValidatorCallback is not supported
        Assert.assertTrue("Next challenge is not empty", ((nextChallenge.length) == 0));
    }

    @Test
    public void negotiatedProperty() throws Exception {
        saslServer.evaluateResponse(clientInitialResponse(OAuthBearerSaslServerTest.USER));
        OAuthBearerToken token = ((OAuthBearerToken) (saslServer.getNegotiatedProperty("OAUTHBEARER.token")));
        Assert.assertNotNull(token);
        Assert.assertEquals(token.lifetimeMs(), saslServer.getNegotiatedProperty(CREDENTIAL_LIFETIME_MS_SASL_NEGOTIATED_PROPERTY_KEY));
    }

    /**
     * SASL Extensions that are validated by the callback handler should be accessible through the {@code #getNegotiatedProperty()} method
     */
    @Test
    public void savesCustomExtensionAsNegotiatedProperty() throws Exception {
        Map<String, String> customExtensions = new HashMap<>();
        customExtensions.put("firstKey", "value1");
        customExtensions.put("secondKey", "value2");
        byte[] nextChallenge = saslServer.evaluateResponse(clientInitialResponse(null, false, customExtensions));
        Assert.assertTrue("Next challenge is not empty", ((nextChallenge.length) == 0));
        Assert.assertEquals("value1", saslServer.getNegotiatedProperty("firstKey"));
        Assert.assertEquals("value2", saslServer.getNegotiatedProperty("secondKey"));
    }

    /**
     * SASL Extensions that were not recognized (neither validated nor invalidated)
     * by the callback handler must not be accessible through the {@code #getNegotiatedProperty()} method
     */
    @Test
    public void unrecognizedExtensionsAreNotSaved() throws Exception {
        saslServer = new OAuthBearerSaslServer(OAuthBearerSaslServerTest.EXTENSIONS_VALIDATOR_CALLBACK_HANDLER);
        Map<String, String> customExtensions = new HashMap<>();
        customExtensions.put("firstKey", "value1");
        customExtensions.put("secondKey", "value1");
        customExtensions.put("thirdKey", "value1");
        byte[] nextChallenge = saslServer.evaluateResponse(clientInitialResponse(null, false, customExtensions));
        Assert.assertTrue("Next challenge is not empty", ((nextChallenge.length) == 0));
        Assert.assertNull("Extensions not recognized by the server must be ignored", saslServer.getNegotiatedProperty("thirdKey"));
    }

    /**
     * If the callback handler handles the `OAuthBearerExtensionsValidatorCallback`
     *  and finds an invalid extension, SaslServer should throw an authentication exception
     */
    @Test(expected = SaslAuthenticationException.class)
    public void throwsAuthenticationExceptionOnInvalidExtensions() throws Exception {
        OAuthBearerUnsecuredValidatorCallbackHandler invalidHandler = new OAuthBearerUnsecuredValidatorCallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
                for (Callback callback : callbacks) {
                    if (callback instanceof OAuthBearerValidatorCallback) {
                        OAuthBearerValidatorCallback validationCallback = ((OAuthBearerValidatorCallback) (callback));
                        validationCallback.token(new OAuthBearerTokenMock());
                    } else
                        if (callback instanceof OAuthBearerExtensionsValidatorCallback) {
                            OAuthBearerExtensionsValidatorCallback extensionsCallback = ((OAuthBearerExtensionsValidatorCallback) (callback));
                            extensionsCallback.error("firstKey", "is not valid");
                            extensionsCallback.error("secondKey", "is not valid either");
                        } else
                            throw new UnsupportedCallbackException(callback);


                }
            }
        };
        saslServer = new OAuthBearerSaslServer(invalidHandler);
        Map<String, String> customExtensions = new HashMap<>();
        customExtensions.put("firstKey", "value");
        customExtensions.put("secondKey", "value");
        saslServer.evaluateResponse(clientInitialResponse(null, false, customExtensions));
    }

    @Test
    public void authorizatonIdEqualsAuthenticationId() throws Exception {
        byte[] nextChallenge = saslServer.evaluateResponse(clientInitialResponse(OAuthBearerSaslServerTest.USER));
        Assert.assertTrue("Next challenge is not empty", ((nextChallenge.length) == 0));
    }

    @Test(expected = SaslAuthenticationException.class)
    public void authorizatonIdNotEqualsAuthenticationId() throws Exception {
        saslServer.evaluateResponse(clientInitialResponse(((OAuthBearerSaslServerTest.USER) + "x")));
    }

    @Test
    public void illegalToken() throws Exception {
        byte[] bytes = saslServer.evaluateResponse(clientInitialResponse(null, true, Collections.emptyMap()));
        String challenge = new String(bytes, StandardCharsets.UTF_8);
        Assert.assertEquals("{\"status\":\"invalid_token\"}", challenge);
    }
}

