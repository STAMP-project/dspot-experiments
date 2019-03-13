/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.net.server;


import ch.qos.logback.core.net.mock.MockContext;
import javax.net.ServerSocketFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SSLServerSocketReceiver}.
 *
 * @author Carl Harris
 */
public class SSLServerSocketReceiverTest {
    private MockContext context = new MockContext();

    private MockSSLConfiguration ssl = new MockSSLConfiguration();

    private MockSSLParametersConfiguration parameters = new MockSSLParametersConfiguration();

    private SSLServerSocketReceiver receiver = new SSLServerSocketReceiver();

    @Test
    public void testGetServerSocketFactory() throws Exception {
        ServerSocketFactory socketFactory = receiver.getServerSocketFactory();
        Assert.assertNotNull(socketFactory);
        Assert.assertTrue(ssl.isContextCreated());
        Assert.assertTrue(parameters.isContextInjected());
    }
}

