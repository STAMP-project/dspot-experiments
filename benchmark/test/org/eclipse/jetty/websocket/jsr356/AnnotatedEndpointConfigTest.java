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
package org.eclipse.jetty.websocket.jsr356;


import java.net.URI;
import java.util.List;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.jsr356.decoders.DateDecoder;
import org.eclipse.jetty.websocket.jsr356.encoders.TimeEncoder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AnnotatedEndpointConfigTest {
    private static Server server;

    private static EchoHandler handler;

    private static URI serverUri;

    private static ClientEndpointConfig ceconfig;

    private static EndpointConfig config;

    private static Session session;

    private static AnnotatedEndpointClient socket;

    @Test
    public void testTextMax() throws Exception {
        MatcherAssert.assertThat("Client Text Max", AnnotatedEndpointConfigTest.socket.session.getMaxTextMessageBufferSize(), Matchers.is(111222));
    }

    @Test
    public void testBinaryMax() throws Exception {
        MatcherAssert.assertThat("Client Binary Max", AnnotatedEndpointConfigTest.socket.session.getMaxBinaryMessageBufferSize(), Matchers.is(333444));
    }

    @Test
    public void testSubProtocols() throws Exception {
        List<String> subprotocols = AnnotatedEndpointConfigTest.ceconfig.getPreferredSubprotocols();
        MatcherAssert.assertThat("Client Preferred SubProtocols", subprotocols, Matchers.contains("chat", "echo"));
    }

    @Test
    public void testDecoders() throws Exception {
        List<Class<? extends Decoder>> decoders = AnnotatedEndpointConfigTest.config.getDecoders();
        MatcherAssert.assertThat("Decoders", decoders, Matchers.notNullValue());
        Class<?> expectedClass = DateDecoder.class;
        boolean hasExpectedDecoder = false;
        for (Class<? extends Decoder> decoder : decoders) {
            if (expectedClass.isAssignableFrom(decoder)) {
                hasExpectedDecoder = true;
            }
        }
        Assertions.assertTrue(hasExpectedDecoder, ("Client Decoders has " + (expectedClass.getName())));
    }

    @Test
    public void testEncoders() throws Exception {
        List<Class<? extends Encoder>> encoders = AnnotatedEndpointConfigTest.config.getEncoders();
        MatcherAssert.assertThat("Encoders", encoders, Matchers.notNullValue());
        Class<?> expectedClass = TimeEncoder.class;
        boolean hasExpectedEncoder = false;
        for (Class<? extends Encoder> encoder : encoders) {
            if (expectedClass.isAssignableFrom(encoder)) {
                hasExpectedEncoder = true;
            }
        }
        Assertions.assertTrue(hasExpectedEncoder, ("Client Encoders has " + (expectedClass.getName())));
    }

    @Test
    public void testConfigurator() throws Exception {
        ClientEndpointConfig ceconfig = ((ClientEndpointConfig) (AnnotatedEndpointConfigTest.config));
        MatcherAssert.assertThat("Client Configurator", ceconfig.getConfigurator(), Matchers.instanceOf(AnnotatedEndpointConfigurator.class));
    }
}

