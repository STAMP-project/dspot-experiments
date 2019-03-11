/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.transports.tcp.transport.netty;


import DisconnectReason.BAD_REQUEST;
import DisconnectReason.INTERNAL_ERROR;
import io.netty.channel.ChannelHandlerContext;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.server.BadRequestException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class KaaTcpExceptionHandlerTest {
    private KaaTcpExceptionHandler kaaTcpExceptionHandler = new KaaTcpExceptionHandler();

    private ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);

    @Test
    public void badRequestExceptionTest() throws Exception {
        kaaTcpExceptionHandler.exceptionCaught(ctx, new BadRequestException("Bad request"));
        Mockito.verify(ctx).writeAndFlush(ArgumentMatchers.any(Object.class));
        ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(ctx).writeAndFlush(argumentCaptor.capture());
        Assert.assertTrue(Arrays.equals(argumentCaptor.getValue(), getMessageByteArrayForReason(BAD_REQUEST)));
        Mockito.verify(ctx).close();
    }

    @Test
    public void internalErrorExceptionTest() throws Exception {
        kaaTcpExceptionHandler.exceptionCaught(ctx, new Exception("Internal error occurred"));
        Mockito.verify(ctx).writeAndFlush(ArgumentMatchers.any(Object.class));
        ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(ctx).writeAndFlush(argumentCaptor.capture());
        Assert.assertTrue(Arrays.equals(argumentCaptor.getValue(), getMessageByteArrayForReason(INTERNAL_ERROR)));
        Mockito.verify(ctx).close();
    }
}

