/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.javax.net.ssl;


import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import junit.framework.TestCase;


/**
 * Tests for <code>SSLSessionBindingEvent</code> class constructors and methods.
 */
public class SSLSessionBindingEventTest extends TestCase {
    public final void test_ConstructorLjavax_net_ssl_SSLSessionLjava_lang_String() {
        SSLSession ses = new MySSLSession();
        try {
            SSLSessionBindingEvent event = new SSLSessionBindingEvent(ses, "test");
            if (!("test".equals(event.getName()))) {
                TestCase.fail("incorrect name");
            }
            if (!(event.getSession().equals(ses))) {
                TestCase.fail("incorrect session");
            }
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + e));
        }
        try {
            SSLSessionBindingEvent event = new SSLSessionBindingEvent(null, "test");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            SSLSessionBindingEvent event = new SSLSessionBindingEvent(ses, null);
        } catch (IllegalArgumentException e) {
            TestCase.fail(("Unexpected IllegalArgumentException: " + e));
        }
    }

    /**
     * javax.net.ssl.SSLSessionBindingEvent#getName()
     */
    public void test_getName() {
        SSLSession ses = new MySSLSession();
        SSLSessionBindingEvent event = new SSLSessionBindingEvent(ses, "test");
        TestCase.assertEquals("Incorrect session name", "test", event.getName());
        event = new SSLSessionBindingEvent(ses, null);
        TestCase.assertEquals("Incorrect session name", null, event.getName());
    }

    /**
     * javax.net.ssl.SSLSessionBindingEvent#getSession()
     */
    public void test_getSession() {
        SSLSession ses = new MySSLSession();
        SSLSessionBindingEvent event = new SSLSessionBindingEvent(ses, "test");
        TestCase.assertEquals("Incorrect session", ses, event.getSession());
    }
}

