/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.protocols.postgres;


import io.crate.action.sql.SQLOperations;
import io.crate.auth.AlwaysOKNullAuthentication;
import io.crate.test.integration.CrateUnitTest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.ssl.SslHandler;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class SslReqHandlerTest extends CrateUnitTest {
    private EmbeddedChannel channel;

    @Test
    public void testSslReqHandler() {
        PostgresWireProtocol ctx = // use a simple ssl context
        new PostgresWireProtocol(Mockito.mock(SQLOperations.class), new AlwaysOKNullAuthentication(), SslReqHandlerTest.getSelfSignedSslContext());
        channel = new EmbeddedChannel(ctx.decoder, ctx.handler);
        SslReqHandlerTest.sendSslRequest(channel);
        // We should get back an 'S'...
        ByteBuf responseBuffer = channel.readOutbound();
        byte response = responseBuffer.readByte();
        assertEquals(response, 'S');
        // ...and continue encrypted (ssl handler)
        assertThat(channel.pipeline().first(), Matchers.instanceOf(SslHandler.class));
    }
}

