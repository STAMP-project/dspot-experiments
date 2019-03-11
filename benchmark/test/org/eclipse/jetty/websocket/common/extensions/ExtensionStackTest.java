/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.common.extensions;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.extensions.Extension;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.common.extensions.identity.IdentityExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ExtensionStackTest {
    private static final Logger LOG = Log.getLogger(ExtensionStackTest.class);

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    @Test
    public void testStartIdentity() throws Exception {
        ExtensionStack stack = createExtensionStack();
        try {
            // 1 extension
            List<ExtensionConfig> configs = new ArrayList<>();
            configs.add(ExtensionConfig.parse("identity"));
            stack.negotiate(configs);
            // Setup Listeners
            DummyIncomingFrames session = new DummyIncomingFrames("Session");
            DummyOutgoingFrames connection = new DummyOutgoingFrames("Connection");
            stack.setNextOutgoing(connection);
            stack.setNextIncoming(session);
            // Start
            stack.start();
            // Dump
            ExtensionStackTest.LOG.debug("{}", stack.dump());
            // Should be no change to handlers
            Extension actualIncomingExtension = assertIsExtension("Incoming", stack.getNextIncoming(), IdentityExtension.class);
            Extension actualOutgoingExtension = assertIsExtension("Outgoing", stack.getNextOutgoing(), IdentityExtension.class);
            Assertions.assertEquals(actualIncomingExtension, actualOutgoingExtension);
        } finally {
            stack.stop();
        }
    }

    @Test
    public void testStartIdentityTwice() throws Exception {
        ExtensionStack stack = createExtensionStack();
        try {
            // 1 extension
            List<ExtensionConfig> configs = new ArrayList<>();
            configs.add(ExtensionConfig.parse("identity; id=A"));
            configs.add(ExtensionConfig.parse("identity; id=B"));
            stack.negotiate(configs);
            // Setup Listeners
            DummyIncomingFrames session = new DummyIncomingFrames("Session");
            DummyOutgoingFrames connection = new DummyOutgoingFrames("Connection");
            stack.setNextOutgoing(connection);
            stack.setNextIncoming(session);
            // Start
            stack.start();
            // Dump
            ExtensionStackTest.LOG.debug("{}", stack.dump());
            // Should be no change to handlers
            IdentityExtension actualIncomingExtension = assertIsExtension("Incoming", stack.getNextIncoming(), IdentityExtension.class);
            IdentityExtension actualOutgoingExtension = assertIsExtension("Outgoing", stack.getNextOutgoing(), IdentityExtension.class);
            MatcherAssert.assertThat("Incoming[identity].id", actualIncomingExtension.getParam("id"), Matchers.is("A"));
            MatcherAssert.assertThat("Outgoing[identity].id", actualOutgoingExtension.getParam("id"), Matchers.is("B"));
        } finally {
            stack.stop();
        }
    }

    @Test
    public void testStartNothing() throws Exception {
        ExtensionStack stack = createExtensionStack();
        try {
            // intentionally empty
            List<ExtensionConfig> configs = new ArrayList<>();
            stack.negotiate(configs);
            // Setup Listeners
            DummyIncomingFrames session = new DummyIncomingFrames("Session");
            DummyOutgoingFrames connection = new DummyOutgoingFrames("Connection");
            stack.setNextOutgoing(connection);
            stack.setNextIncoming(session);
            // Start
            stack.start();
            // Dump
            ExtensionStackTest.LOG.debug("{}", stack.dump());
            // Should be no change to handlers
            Assertions.assertEquals(stack.getNextIncoming(), session, "Incoming Handler");
            Assertions.assertEquals(stack.getNextOutgoing(), connection, "Outgoing Handler");
        } finally {
            stack.stop();
        }
    }

    @Test
    public void testToString() {
        ExtensionStack stack = createExtensionStack();
        // Shouldn't cause a NPE.
        ExtensionStackTest.LOG.debug("Shouldn't cause a NPE: {}", stack.toString());
    }

    @Test
    public void testNegotiateChrome32() {
        ExtensionStack stack = createExtensionStack();
        String chromeRequest = "permessage-deflate; client_max_window_bits, x-webkit-deflate-frame";
        List<ExtensionConfig> requestedConfigs = ExtensionConfig.parseList(chromeRequest);
        stack.negotiate(requestedConfigs);
        List<ExtensionConfig> negotiated = stack.getNegotiatedExtensions();
        String response = ExtensionConfig.toHeaderValue(negotiated);
        MatcherAssert.assertThat("Negotiated Extensions", response, Matchers.is("permessage-deflate"));
        ExtensionStackTest.LOG.debug("Shouldn't cause a NPE: {}", stack.toString());
    }
}

