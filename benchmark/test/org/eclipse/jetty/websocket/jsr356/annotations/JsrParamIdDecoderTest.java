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
package org.eclipse.jetty.websocket.jsr356.annotations;


import java.util.Date;
import org.eclipse.jetty.websocket.jsr356.MessageType;
import org.eclipse.jetty.websocket.jsr356.decoders.DateDecoder;
import org.eclipse.jetty.websocket.jsr356.metadata.DecoderMetadata;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class JsrParamIdDecoderTest {
    @Test
    public void testMatchDateDecoder() {
        DecoderMetadata metadata = new DecoderMetadata(DateDecoder.class, Date.class, MessageType.TEXT, false);
        JsrParamIdDecoder paramId = new JsrParamIdDecoder(metadata);
        JsrCallable callable = getOnMessageCallableFrom(DateTextSocket.class, "onMessage");
        Param param = new Param(0, Date.class, null);
        MatcherAssert.assertThat("Match for Decoder", paramId.process(param, callable), Matchers.is(true));
    }
}

