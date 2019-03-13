/**
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.conscrypt;


import javax.net.ssl.SSLSession;
import junit.framework.TestCase;
import libcore.javax.net.ssl.FakeSSLSession;


public final class ClientSessionContextTest extends TestCase {
    public void testSimpleAddition() {
        ClientSessionContext context = new ClientSessionContext();
        SSLSession a = new ClientSessionContextTest.ValidSSLSession("a");
        SSLSession b = new ClientSessionContextTest.ValidSSLSession("b");
        context.putSession(a);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a }, new SSLSession[]{ b });
        context.putSession(b);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a, b }, new SSLSession[0]);
    }

    public void testTrimToSize() {
        ClientSessionContext context = new ClientSessionContext();
        ClientSessionContextTest.ValidSSLSession a = new ClientSessionContextTest.ValidSSLSession("a");
        ClientSessionContextTest.ValidSSLSession b = new ClientSessionContextTest.ValidSSLSession("b");
        ClientSessionContextTest.ValidSSLSession c = new ClientSessionContextTest.ValidSSLSession("c");
        ClientSessionContextTest.ValidSSLSession d = new ClientSessionContextTest.ValidSSLSession("d");
        context.putSession(a);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a }, new SSLSession[]{ b, c, d });
        context.putSession(b);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a, b }, new SSLSession[]{ c, d });
        context.putSession(c);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a, b, c }, new SSLSession[]{ d });
        context.putSession(d);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a, b, c, d }, new SSLSession[0]);
        context.setSessionCacheSize(2);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ c, d }, new SSLSession[]{ a, b });
    }

    public void testImplicitRemovalOfOldest() {
        ClientSessionContext context = new ClientSessionContext();
        context.setSessionCacheSize(2);
        ClientSessionContextTest.ValidSSLSession a = new ClientSessionContextTest.ValidSSLSession("a");
        ClientSessionContextTest.ValidSSLSession b = new ClientSessionContextTest.ValidSSLSession("b");
        ClientSessionContextTest.ValidSSLSession c = new ClientSessionContextTest.ValidSSLSession("c");
        ClientSessionContextTest.ValidSSLSession d = new ClientSessionContextTest.ValidSSLSession("d");
        context.putSession(a);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a }, new SSLSession[]{ b, c, d });
        context.putSession(b);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ a, b }, new SSLSession[]{ c, d });
        context.putSession(c);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ b, c }, new SSLSession[]{ a, d });
        context.putSession(d);
        ClientSessionContextTest.assertSessionContextContents(context, new SSLSession[]{ c, d }, new SSLSession[]{ a, b });
    }

    static class ValidSSLSession extends FakeSSLSession {
        ValidSSLSession(String host) {
            super(host);
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }
}

