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
package org.apache.kafka.common.security.oauthbearer;


import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import org.apache.kafka.common.security.oauthbearer.internals.OAuthBearerSaslClientCallbackHandler;
import org.junit.Assert;
import org.junit.Test;


public class OAuthBearerSaslClienCallbackHandlerTest {
    @Test(expected = IOException.class)
    public void testWithZeroTokens() throws Throwable {
        OAuthBearerSaslClientCallbackHandler handler = OAuthBearerSaslClienCallbackHandlerTest.createCallbackHandler();
        try {
            Subject.doAs(new Subject(), ((PrivilegedExceptionAction<Void>) (() -> {
                OAuthBearerTokenCallback callback = new OAuthBearerTokenCallback();
                handler.handle(new Callback[]{ callback });
                return null;
            })));
        } catch (PrivilegedActionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testWithPotentiallyMultipleTokens() throws Exception {
        OAuthBearerSaslClientCallbackHandler handler = OAuthBearerSaslClienCallbackHandlerTest.createCallbackHandler();
        Subject.doAs(new Subject(), ((PrivilegedExceptionAction<Void>) (() -> {
            final int maxTokens = 4;
            final Set<Object> privateCredentials = Subject.getSubject(AccessController.getContext()).getPrivateCredentials();
            privateCredentials.clear();
            for (int num = 1; num <= maxTokens; ++num) {
                privateCredentials.add(OAuthBearerSaslClienCallbackHandlerTest.createTokenWithLifetimeMillis(num));
                OAuthBearerTokenCallback callback = new OAuthBearerTokenCallback();
                handler.handle(new Callback[]{ callback });
                Assert.assertEquals(num, callback.token().lifetimeMs());
            }
            return null;
        })));
    }
}

