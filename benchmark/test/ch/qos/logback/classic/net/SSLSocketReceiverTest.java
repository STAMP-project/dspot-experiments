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
package ch.qos.logback.classic.net;


import java.net.InetAddress;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SSLSocketReceiver}.
 *
 * @author Carl Harris
 */
public class SSLSocketReceiverTest {
    private SSLSocketReceiver remote = new SSLSocketReceiver();

    @Test
    public void testUsingDefaultConfig() throws Exception {
        // should be able to start successfully with no SSL configuration at all
        remote.setRemoteHost(InetAddress.getLocalHost().getHostAddress());
        remote.setPort(6000);
        remote.start();
        Assert.assertNotNull(remote.getSocketFactory());
    }
}

