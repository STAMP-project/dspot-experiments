/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http.conn.ssl;


import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.junit.Assert;
import org.junit.Test;


public class SdkTLSSocketFactoryTest {
    /**
     * Test when the edge case when the both supported and enabled protocols are null.
     */
    @Test
    public void preparedSocket_NullProtocols() throws NoSuchAlgorithmException {
        SdkTLSSocketFactory f = new SdkTLSSocketFactory(SSLContext.getDefault(), null);
        TestSSLSocket socket = new TestSSLSocket() {
            @Override
            public String[] getSupportedProtocols() {
                return null;
            }

            @Override
            public String[] getEnabledProtocols() {
                return null;
            }
        };
        f.prepareSocket(socket);
        Assert.assertFalse(socket.wereProtocolsEnabled());
    }

    @Test
    public void typical() throws NoSuchAlgorithmException {
        SdkTLSSocketFactory f = new SdkTLSSocketFactory(SSLContext.getDefault(), null);
        TestSSLSocket socket = new TestSSLSocket() {
            @Override
            public String[] getSupportedProtocols() {
                return shuffle(new String[]{ "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" });
            }

            @Override
            public String[] getEnabledProtocols() {
                return shuffle(new String[]{ "SSLv3", "TLSv1" });
            }
        };
        f.prepareSocket(socket);
        Assert.assertArrayEquals(new String[]{ "TLSv1.2", "TLSv1.1", "TLSv1", "SSLv3" }, socket.getCapturedProtocols());
    }

    @Test
    public void noTLS() throws NoSuchAlgorithmException {
        SdkTLSSocketFactory f = new SdkTLSSocketFactory(SSLContext.getDefault(), null);
        TestSSLSocket socket = new TestSSLSocket() {
            @Override
            public String[] getSupportedProtocols() {
                return shuffle(new String[]{ "SSLv2Hello", "SSLv3" });
            }

            @Override
            public String[] getEnabledProtocols() {
                return new String[]{ "SSLv3" };
            }
        };
        f.prepareSocket(socket);
        // For backward compatibility
        Assert.assertArrayEquals(new String[]{ "SSLv3" }, socket.getCapturedProtocols());
    }

    @Test
    public void notIdeal() throws NoSuchAlgorithmException {
        SdkTLSSocketFactory f = new SdkTLSSocketFactory(SSLContext.getDefault(), null);
        TestSSLSocket socket = new TestSSLSocket() {
            @Override
            public String[] getSupportedProtocols() {
                return shuffle(new String[]{ "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1" });
            }

            @Override
            public String[] getEnabledProtocols() {
                return shuffle(new String[]{ "SSLv3", "TLSv1" });
            }
        };
        f.prepareSocket(socket);
        Assert.assertArrayEquals(new String[]{ "TLSv1.1", "TLSv1", "SSLv3" }, socket.getCapturedProtocols());
    }
}

