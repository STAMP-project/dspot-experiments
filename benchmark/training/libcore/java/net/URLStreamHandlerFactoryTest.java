/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.net;


import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import junit.framework.TestCase;
import libcore.java.net.customstreamhandler.http.Handler;


public final class URLStreamHandlerFactoryTest extends TestCase {
    private URLStreamHandlerFactory oldFactory;

    private Field factoryField;

    private boolean isCreateURLStreamHandlerCalled;

    public void testCreateURLStreamHandler() throws Exception {
        URLStreamHandlerFactoryTest.TestURLStreamHandlerFactory shf = new URLStreamHandlerFactoryTest.TestURLStreamHandlerFactory();
        TestCase.assertFalse(isCreateURLStreamHandlerCalled);
        URL.setURLStreamHandlerFactory(shf);
        URL url = new URL("http://android.com/");
        URLConnection connection = url.openConnection();
        TestCase.assertTrue((connection instanceof Handler.HandlerURLConnection));
        try {
            URL.setURLStreamHandlerFactory(shf);
            TestCase.fail();
        } catch (Error expected) {
        }
        try {
            URL.setURLStreamHandlerFactory(null);
            TestCase.fail();
        } catch (Error expected) {
        }
    }

    public void testInstallCustomProtocolHandler() throws Exception {
        // clear cached protocol handlers if they exist
        factoryField.set(null, null);
        URL.setURLStreamHandlerFactory(oldFactory);
        try {
            System.setProperty("java.protocol.handler.pkgs", getHandlerPackageName());
            URLConnection connection = new URL("http://android.com/").openConnection();
            TestCase.assertTrue((connection instanceof Handler.HandlerURLConnection));
        } finally {
            System.clearProperty("java.protocol.handler.pkgs");
        }
    }

    public void testFirstUseIsCached() throws Exception {
        // clear cached protocol handlers if they exist
        factoryField.set(null, null);
        URL.setURLStreamHandlerFactory(oldFactory);
        // creating a connection should use the platform's default stream handler
        URLConnection connection1 = new URL("http://android.com/").openConnection();
        TestCase.assertFalse((connection1 instanceof Handler.HandlerURLConnection));
        try {
            // set the property and get another connection. The property should not be honored
            System.setProperty("java.protocol.handler.pkgs", getHandlerPackageName());
            URLConnection connection2 = new URL("http://android.com/").openConnection();
            TestCase.assertFalse((connection2 instanceof Handler.HandlerURLConnection));
        } finally {
            System.clearProperty("java.protocol.handler.pkgs");
        }
    }

    class TestURLStreamHandlerFactory implements URLStreamHandlerFactory {
        @Override
        public URLStreamHandler createURLStreamHandler(String protocol) {
            isCreateURLStreamHandlerCalled = true;
            return new Handler();
        }
    }
}

