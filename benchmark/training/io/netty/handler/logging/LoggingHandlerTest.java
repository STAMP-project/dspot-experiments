/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.logging;


import CharsetUtil.UTF_8;
import LogLevel.INFO;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.embedded.EmbeddedChannel;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import static LogLevel.INFO;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;


/**
 * Verifies the correct functionality of the {@link LoggingHandler}.
 */
public class LoggingHandlerTest {
    private static final String LOGGER_NAME = LoggingHandler.class.getName();

    private static final Logger rootLogger = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));

    private static final Logger logger = ((Logger) (LoggerFactory.getLogger(LoggingHandlerTest.LOGGER_NAME)));

    private static final List<Appender<ILoggingEvent>> oldAppenders = new ArrayList<Appender<ILoggingEvent>>();

    /**
     * Custom logback appender which gets used to match on log messages.
     */
    private Appender<ILoggingEvent> appender;

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullLogLevel() {
        LogLevel level = null;
        new LoggingHandler(level);
    }

    @Test
    public void shouldApplyCustomLogLevel() {
        LoggingHandler handler = new LoggingHandler(INFO);
        Assert.assertEquals(INFO, handler.level());
    }

    @Test
    public void shouldLogChannelActive() {
        new EmbeddedChannel(new LoggingHandler());
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+ACTIVE$")));
    }

    @Test
    public void shouldLogChannelWritabilityChanged() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        // this is used to switch the channel to become unwritable
        channel.config().setWriteBufferLowWaterMark(5);
        channel.config().setWriteBufferHighWaterMark(10);
        channel.write("hello", channel.newPromise());
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+WRITABILITY CHANGED$")));
    }

    @Test
    public void shouldLogChannelRegistered() {
        new EmbeddedChannel(new LoggingHandler());
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+REGISTERED$")));
    }

    @Test
    public void shouldLogChannelClose() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.close().await();
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+CLOSE$")));
    }

    @Test
    public void shouldLogChannelConnect() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.connect(new InetSocketAddress(80)).await();
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+CONNECT: 0.0.0.0/0.0.0.0:80$")));
    }

    @Test
    public void shouldLogChannelConnectWithLocalAddress() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.connect(new InetSocketAddress(80), new InetSocketAddress(81)).await();
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher("^\\[id: 0xembedded, L:embedded - R:embedded\\] CONNECT: 0.0.0.0/0.0.0.0:80, 0.0.0.0/0.0.0.0:81$")));
    }

    @Test
    public void shouldLogChannelDisconnect() throws Exception {
        EmbeddedChannel channel = new LoggingHandlerTest.DisconnectingEmbeddedChannel(new LoggingHandler());
        channel.connect(new InetSocketAddress(80)).await();
        channel.disconnect().await();
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+DISCONNECT$")));
    }

    @Test
    public void shouldLogChannelInactive() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.pipeline().fireChannelInactive();
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+INACTIVE$")));
    }

    @Test
    public void shouldLogChannelBind() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.bind(new InetSocketAddress(80));
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+BIND: 0.0.0.0/0.0.0.0:80$")));
    }

    @Test
    @SuppressWarnings("RedundantStringConstructorCall")
    public void shouldLogChannelUserEvent() throws Exception {
        String userTriggered = "iAmCustom!";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.pipeline().fireUserEventTriggered(new String(userTriggered));
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(((".+USER_EVENT: " + userTriggered) + '$'))));
    }

    @Test
    public void shouldLogChannelException() throws Exception {
        String msg = "illegalState";
        Throwable cause = new IllegalStateException(msg);
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.pipeline().fireExceptionCaught(cause);
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(((((".+EXCEPTION: " + (cause.getClass().getCanonicalName())) + ": ") + msg) + '$'))));
    }

    @Test
    public void shouldLogDataWritten() throws Exception {
        String msg = "hello";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.writeOutbound(msg);
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(((".+WRITE: " + msg) + '$'))));
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+FLUSH$")));
    }

    @Test
    public void shouldLogNonByteBufDataRead() throws Exception {
        String msg = "hello";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.writeInbound(msg);
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(((".+READ: " + msg) + '$'))));
        String handledMsg = channel.readInbound();
        Assert.assertThat(msg, CoreMatchers.is(CoreMatchers.sameInstance(handledMsg)));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldLogByteBufDataRead() throws Exception {
        ByteBuf msg = Unpooled.copiedBuffer("hello", UTF_8);
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.writeInbound(msg);
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(((".+READ: " + (msg.readableBytes())) + "B$"))));
        ByteBuf handledMsg = channel.readInbound();
        Assert.assertThat(msg, CoreMatchers.is(CoreMatchers.sameInstance(handledMsg)));
        handledMsg.release();
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldLogEmptyByteBufDataRead() throws Exception {
        ByteBuf msg = Unpooled.EMPTY_BUFFER;
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.writeInbound(msg);
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+READ: 0B$")));
        ByteBuf handledMsg = channel.readInbound();
        Assert.assertThat(msg, CoreMatchers.is(CoreMatchers.sameInstance(handledMsg)));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldLogByteBufHolderDataRead() throws Exception {
        ByteBufHolder msg = new DefaultByteBufHolder(Unpooled.copiedBuffer("hello", UTF_8)) {
            @Override
            public String toString() {
                return "foobar";
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.writeInbound(msg);
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+READ: foobar, 5B$")));
        ByteBufHolder handledMsg = channel.readInbound();
        Assert.assertThat(msg, CoreMatchers.is(CoreMatchers.sameInstance(handledMsg)));
        handledMsg.release();
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldLogChannelReadComplete() throws Exception {
        ByteBuf msg = Unpooled.EMPTY_BUFFER;
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler());
        channel.writeInbound(msg);
        Mockito.verify(appender).doAppend(ArgumentMatchers.argThat(new LoggingHandlerTest.RegexLogMatcher(".+READ COMPLETE$")));
    }

    /**
     * A custom EasyMock matcher that matches on Logback messages.
     */
    private static final class RegexLogMatcher implements ArgumentMatcher<ILoggingEvent> {
        private final String expected;

        private String actualMsg;

        RegexLogMatcher(String expected) {
            this.expected = expected;
        }

        @Override
        @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
        public boolean matches(ILoggingEvent actual) {
            // Match only the first line to skip the validation of hex-dump format.
            actualMsg = actual.getMessage().split("(?s)[\\r\\n]+")[0];
            return actualMsg.matches(expected);
        }
    }

    private static final class DisconnectingEmbeddedChannel extends EmbeddedChannel {
        private DisconnectingEmbeddedChannel(ChannelHandler... handlers) {
            super(handlers);
        }

        @Override
        public ChannelMetadata metadata() {
            return new ChannelMetadata(true);
        }
    }
}

