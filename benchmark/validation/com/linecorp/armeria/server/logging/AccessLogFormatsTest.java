/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.logging;


import AccessLogFormats.COMBINED;
import AccessLogFormats.COMMON;
import AccessLogType.LOCAL_IP_ADDRESS;
import AccessLogType.REMOTE_HOST;
import AccessLogType.REMOTE_IP_ADDRESS;
import AccessLogType.RFC931;
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaderNames.COOKIE;
import HttpHeaderNames.REFERER;
import HttpHeaderNames.USER_AGENT;
import HttpMethod.GET;
import HttpStatus.BAD_REQUEST;
import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import NetUtil.LOCALHOST;
import RequestLogAvailability.COMPLETE;
import RequestLogAvailability.REQUEST_END;
import com.linecorp.armeria.common.DefaultRpcRequest;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.common.logging.RequestLogBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.ServiceRequestContextBuilder;
import com.linecorp.armeria.server.logging.AccessLogComponent.AttributeComponent;
import com.linecorp.armeria.server.logging.AccessLogComponent.CommonComponent;
import com.linecorp.armeria.server.logging.AccessLogComponent.HttpHeaderComponent;
import io.netty.util.AttributeKey;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;

import static TimestampComponent.defaultDateTimeFormatter;
import static TimestampComponent.defaultZoneId;


public class AccessLogFormatsTest {
    private static final Duration duration = Duration.ofMillis(1000000000L);

    // The timestamp of first commit in Armeria project.
    private static final long requestStartTimeMillis = 1447656026L * 1000;

    private static final long requestStartTimeMicros = (AccessLogFormatsTest.requestStartTimeMillis) * 1000;

    private static final long requestStartTimeNanos = 42424242424242L;// Some random number.


    private static final long requestEndTimeNanos = (AccessLogFormatsTest.requestStartTimeNanos) + (AccessLogFormatsTest.duration.toNanos());

    @Test
    public void parseSuccess() {
        List<AccessLogComponent> format;
        AccessLogComponent entry;
        HttpHeaderComponent headerEntry;
        CommonComponent commonComponentEntry;
        assertThat(AccessLogFormats.parseCustom("%h %l")).usingRecursiveFieldByFieldElementComparator().containsSequence(AccessLogComponent.ofPredefinedCommon(REMOTE_HOST), AccessLogComponent.ofText(" "), AccessLogComponent.ofPredefinedCommon(RFC931));
        format = AccessLogFormats.parseCustom("%200,302{Referer}i");
        assertThat(format.size()).isOne();
        entry = format.get(0);
        assertThat(entry).isInstanceOf(HttpHeaderComponent.class);
        headerEntry = ((HttpHeaderComponent) (entry));
        assertThat(headerEntry.condition()).isNotNull();
        assertThat(headerEntry.condition().apply(HttpHeaders.of(OK))).isTrue();
        assertThat(headerEntry.condition().apply(HttpHeaders.of(BAD_REQUEST))).isFalse();
        assertThat(headerEntry.headerName().toString()).isEqualToIgnoringCase(REFERER.toString());
        format = AccessLogFormats.parseCustom("%!200,302{User-Agent}i");
        assertThat(format.size()).isOne();
        entry = format.get(0);
        assertThat(entry).isInstanceOf(HttpHeaderComponent.class);
        headerEntry = ((HttpHeaderComponent) (entry));
        assertThat(headerEntry.condition()).isNotNull();
        assertThat(headerEntry.condition().apply(HttpHeaders.of(OK))).isFalse();
        assertThat(headerEntry.condition().apply(HttpHeaders.of(BAD_REQUEST))).isTrue();
        assertThat(headerEntry.headerName().toString()).isEqualToIgnoringCase(USER_AGENT.toString());
        format = AccessLogFormats.parseCustom("%200b");
        assertThat(format.size()).isOne();
        entry = format.get(0);
        assertThat(entry).isInstanceOf(CommonComponent.class);
        commonComponentEntry = ((CommonComponent) (entry));
        assertThat(commonComponentEntry.condition()).isNotNull();
        assertThat(commonComponentEntry.condition().apply(HttpHeaders.of(OK))).isTrue();
        assertThat(commonComponentEntry.condition().apply(HttpHeaders.of(BAD_REQUEST))).isFalse();
        format = AccessLogFormats.parseCustom("%!200b");
        assertThat(format.size()).isOne();
        entry = format.get(0);
        assertThat(entry).isInstanceOf(CommonComponent.class);
        commonComponentEntry = ((CommonComponent) (entry));
        assertThat(commonComponentEntry.condition()).isNotNull();
        assertThat(commonComponentEntry.condition().apply(HttpHeaders.of(OK))).isFalse();
        assertThat(commonComponentEntry.condition().apply(HttpHeaders.of(BAD_REQUEST))).isTrue();
        assertThat(AccessLogFormats.parseCustom("").isEmpty()).isTrue();
        format = AccessLogFormats.parseCustom(("%{com.linecorp.armeria.server.logging.AccessLogFormatsTest$Attr#KEY" + ":com.linecorp.armeria.server.logging.AccessLogFormatsTest$AttributeStringfier}j"));
        assertThat(format.size()).isOne();
        entry = format.get(0);
        assertThat(entry).isInstanceOf(AttributeComponent.class);
        final AttributeComponent attrEntry = ((AttributeComponent) (entry));
        assertThat(attrEntry.key().toString()).isEqualTo("com.linecorp.armeria.server.logging.AccessLogFormatsTest$Attr#KEY");
        // Typo, but successful.
        assertThat(AccessLogFormats.parseCustom("%h00,300{abc}")).usingRecursiveFieldByFieldElementComparator().containsSequence(AccessLogComponent.ofPredefinedCommon(REMOTE_HOST), AccessLogComponent.ofText("00,300{abc}"));
        assertThat(AccessLogFormats.parseCustom("%a %{c}a %A")).usingRecursiveFieldByFieldElementComparator().containsSequence(AccessLogComponent.ofPredefinedCommon(REMOTE_IP_ADDRESS), AccessLogComponent.ofText(" "), AccessLogComponent.ofPredefinedCommon(REMOTE_IP_ADDRESS, "c"), AccessLogComponent.ofText(" "), AccessLogComponent.ofPredefinedCommon(LOCAL_IP_ADDRESS));
    }

    @Test
    public void parseFailure() {
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%x")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%!{abc}i")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%{abci")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%{abc}")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%200,300{abc}")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%200,30x{abc}")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%200,300{abc")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AccessLogFormats.parseCustom("%x00,300{abc}")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void formatMessage() {
        final HttpRequest req = HttpRequest.of(HttpHeaders.of(GET, "/armeria/log").add(USER_AGENT, "armeria/x.y.z").add(REFERER, "http://log.example.com").add(COOKIE, "a=1;b=2"));
        final ServiceRequestContext ctx = ServiceRequestContextBuilder.of(req).requestStartTime(AccessLogFormatsTest.requestStartTimeNanos, AccessLogFormatsTest.requestStartTimeMicros).build();
        ctx.attr(AccessLogFormatsTest.Attr.ATTR_KEY).set(new AccessLogFormatsTest.Attr("line"));
        final RequestLog log = ctx.log();
        final RequestLogBuilder logBuilder = ctx.logBuilder();
        logBuilder.endRequest();
        assertThat(ctx.log().isAvailable(REQUEST_END)).isTrue();
        assertThat(log.isAvailable(REQUEST_END)).isTrue();
        logBuilder.responseHeaders(HttpHeaders.of(OK).addObject(CONTENT_TYPE, PLAIN_TEXT_UTF_8));
        logBuilder.responseLength(1024);
        logBuilder.endResponse();
        final String localhostAddress = LOCALHOST.getHostAddress();
        final String timestamp = defaultDateTimeFormatter.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(AccessLogFormatsTest.requestStartTimeMillis), defaultZoneId));
        String message;
        List<AccessLogComponent> format;
        message = AccessLogger.format(COMMON, log);
        assertThat(message).isEqualTo((((localhostAddress + " - - ") + timestamp) + " \"GET /armeria/log h2c\" 200 1024"));
        message = AccessLogger.format(COMBINED, log);
        assertThat(message).isEqualTo(((((localhostAddress + " - - ") + timestamp) + " \"GET /armeria/log h2c\" 200 1024") + " \"http://log.example.com\" \"armeria/x.y.z\" \"a=1;b=2\""));
        // Check conditions with custom formats.
        format = AccessLogFormats.parseCustom(("%h %l %u %t \"%r\" %s %b \"%200,302{Referer}i\" \"%!200,304{User-Agent}i\"" + " some-text %{Non-Existing-Header}i"));
        message = AccessLogger.format(format, log);
        assertThat(message).isEqualTo(((((localhostAddress + " - - ") + timestamp) + " \"GET /armeria/log h2c\" 200 1024") + " \"http://log.example.com\" \"-\" some-text -"));
        format = AccessLogFormats.parseCustom(("%h %l %u %t \"%r\" %s %b \"%!200,302{Referer}i\" \"%200,304{User-Agent}i\"" + " some-text %{Non-Existing-Header}i"));
        message = AccessLogger.format(format, log);
        assertThat(message).isEqualTo(((((localhostAddress + " - - ") + timestamp) + " \"GET /armeria/log h2c\" 200 1024") + " \"-\" \"armeria/x.y.z\" some-text -"));
        format = AccessLogFormats.parseCustom(("%{com.linecorp.armeria.server.logging.AccessLogFormatsTest$Attr#KEY" + ":com.linecorp.armeria.server.logging.AccessLogFormatsTest$AttributeStringfier}j"));
        message = AccessLogger.format(format, log);
        assertThat(message).isEqualTo("(line)");
        format = AccessLogFormats.parseCustom("%{com.linecorp.armeria.server.logging.AccessLogFormatsTest$Attr#KEY}j");
        message = AccessLogger.format(format, log);
        assertThat(message).isEqualTo("LINE");
        format = AccessLogFormats.parseCustom("%{content-type}o");
        assertThat(AccessLogger.format(format, log)).isEqualTo(PLAIN_TEXT_UTF_8.toString());
    }

    @Test
    public void logClientAddress() throws Exception {
        final InetSocketAddress remote = new InetSocketAddress(InetAddress.getByName("10.1.0.1"), 5000);
        final ServiceRequestContext ctx = ServiceRequestContextBuilder.of(HttpRequest.of(GET, "/")).remoteAddress(remote).clientAddress(InetAddress.getByName("10.0.0.1")).build();
        List<AccessLogComponent> format;
        // Client IP address
        format = AccessLogFormats.parseCustom("%a");
        assertThat(AccessLogger.format(format, ctx.log())).isEqualTo("10.0.0.1");
        // Remote IP address of a channel
        format = AccessLogFormats.parseCustom("%{c}a");
        assertThat(AccessLogger.format(format, ctx.log())).isEqualTo("10.1.0.1");
    }

    @Test
    public void requestLogAvailabilityException() {
        final String expectedLogMessage = "\"GET /armeria/log#rpcMethod h2\" 200 1024";
        final ServiceRequestContext ctx = ServiceRequestContextBuilder.of(HttpRequest.of(HttpHeaders.of(GET, "/armeria/log").add(USER_AGENT, "armeria/x.y.z").add(REFERER, "http://log.example.com").add(COOKIE, "a=1;b=2"))).build();
        final RequestLog log = ctx.log();
        final RequestLogBuilder logBuilder = ctx.logBuilder();
        // AccessLogger#format will be called after response is finished.
        log.addListener(( l) -> assertThat(AccessLogger.format(AccessLogFormats.COMMON, l)).endsWith(expectedLogMessage), COMPLETE);
        // RequestLogAvailabilityException will be raised inside AccessLogger#format before injecting each
        // component to RequestLog. So we cannot get the expected log message here.
        assertThat(AccessLogger.format(COMMON, log)).doesNotEndWith(expectedLogMessage);
        logBuilder.requestContent(new DefaultRpcRequest(Object.class, "rpcMethod"), null);
        assertThat(AccessLogger.format(COMMON, log)).doesNotEndWith(expectedLogMessage);
        logBuilder.endRequest();
        assertThat(AccessLogger.format(COMMON, log)).doesNotEndWith(expectedLogMessage);
        logBuilder.responseHeaders(HttpHeaders.of(OK));
        assertThat(AccessLogger.format(COMMON, log)).doesNotEndWith(expectedLogMessage);
        logBuilder.responseLength(1024);
        assertThat(AccessLogger.format(COMMON, log)).doesNotEndWith(expectedLogMessage);
        logBuilder.endResponse();
    }

    @Test
    public void requestLogComponent() {
        final ServiceRequestContext ctx = ServiceRequestContextBuilder.of(HttpRequest.of(GET, "/armeria/log")).requestStartTime(AccessLogFormatsTest.requestStartTimeNanos, AccessLogFormatsTest.requestStartTimeMicros).build();
        final RequestLog log = ctx.log();
        final RequestLogBuilder logBuilder = ctx.logBuilder();
        List<AccessLogComponent> format;
        final Instant requestStartTime = Instant.ofEpochMilli(AccessLogFormatsTest.requestStartTimeMillis);
        logBuilder.endRequest(new IllegalArgumentException("detail_message"), AccessLogFormatsTest.requestEndTimeNanos);
        format = AccessLogFormats.parseCustom(("%{requestStartTimeMillis}L " + ("%{requestEndTimeMillis}L " + "%{requestDurationMillis}L")));
        assertThat(AccessLogger.format(format, log)).isEqualTo(String.join(" ", String.valueOf(requestStartTime.toEpochMilli()), String.valueOf(requestStartTime.plus(AccessLogFormatsTest.duration).toEpochMilli()), String.valueOf(AccessLogFormatsTest.duration.toMillis())));
        format = AccessLogFormats.parseCustom("\"%{requestCause}L\"");
        // assertThat(AccessLogger.format(format, log)).isEqualTo("\"-\"");
        assertThat(AccessLogger.format(format, log)).isEqualTo((('"' + (IllegalArgumentException.class.getSimpleName())) + ": detail_message\""));
        format = AccessLogFormats.parseCustom(("%{responseStartTimeMillis}L " + ("%{responseEndTimeMillis}L " + "%{responseDurationMillis}L")));
        // No values.
        assertThat(AccessLogger.format(format, log)).isEqualTo("- - -");
        final Duration latency = Duration.ofSeconds(3);
        final Instant responseStartTime = requestStartTime.plus(latency);
        final long responseStartTimeNanos = (AccessLogFormatsTest.requestStartTimeNanos) + (latency.toNanos());
        logBuilder.startResponse(responseStartTimeNanos, ((responseStartTime.toEpochMilli()) * 1000));
        logBuilder.endResponse(new IllegalArgumentException(), (responseStartTimeNanos + (AccessLogFormatsTest.duration.toNanos())));
        assertThat(AccessLogger.format(format, log)).isEqualTo(String.join(" ", String.valueOf(responseStartTime.toEpochMilli()), String.valueOf(responseStartTime.plus(AccessLogFormatsTest.duration).toEpochMilli()), String.valueOf(AccessLogFormatsTest.duration.toMillis())));
        format = AccessLogFormats.parseCustom("\"%{responseCause}L\"");
        // assertThat(AccessLogger.format(format, log)).isEqualTo("\"-\"");
        assertThat(AccessLogger.format(format, log)).isEqualTo((('"' + (IllegalArgumentException.class.getSimpleName())) + '"'));
    }

    @Test
    public void requestLogWithEmptyCause() {
        final ServiceRequestContext ctx = ServiceRequestContext.of(HttpRequest.of(GET, "/"));
        final RequestLog log = ctx.log();
        final RequestLogBuilder logBuilder = ctx.logBuilder();
        final List<AccessLogComponent> format = AccessLogFormats.parseCustom("%{requestCause}L %{responseCause}L");
        logBuilder.endRequest();
        logBuilder.endResponse();
        assertThat(AccessLogger.format(format, log)).isEqualTo("- -");
    }

    @Test
    public void timestamp() {
        final ServiceRequestContext ctx = ServiceRequestContextBuilder.of(HttpRequest.of(GET, "/")).requestStartTime(AccessLogFormatsTest.requestStartTimeNanos, AccessLogFormatsTest.requestStartTimeMicros).build();
        final RequestLog log = ctx.log();
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%t"), log)).isEqualTo(AccessLogFormatsTest.formatString(defaultDateTimeFormatter, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{BASIC_ISO_DATE}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.BASIC_ISO_DATE, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_LOCAL_DATE}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_LOCAL_DATE, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_OFFSET_DATE}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_OFFSET_DATE, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_DATE}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_DATE, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_LOCAL_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_LOCAL_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_OFFSET_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_OFFSET_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_LOCAL_DATE_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_LOCAL_DATE_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_OFFSET_DATE_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_OFFSET_DATE_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_ZONED_DATE_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_ZONED_DATE_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_DATE_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_DATE_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_ORDINAL_DATE}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_ORDINAL_DATE, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_WEEK_DATE}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_WEEK_DATE, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{ISO_INSTANT}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ISO_INSTANT, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{RFC_1123_DATE_TIME}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.RFC_1123_DATE_TIME, AccessLogFormatsTest.requestStartTimeMillis));
        assertThat(AccessLogger.format(AccessLogFormats.parseCustom("%{yyyy MM dd}t"), log)).isEqualTo(AccessLogFormatsTest.formatString(DateTimeFormatter.ofPattern("yyyy MM dd"), AccessLogFormatsTest.requestStartTimeMillis));
    }

    public static class Attr {
        static final AttributeKey<AccessLogFormatsTest.Attr> ATTR_KEY = AttributeKey.valueOf(AccessLogFormatsTest.Attr.class, "KEY");

        private final String member;

        Attr(String member) {
            this.member = member;
        }

        public String member() {
            return member;
        }

        @Override
        public String toString() {
            return member().toUpperCase();
        }
    }

    public static class AttributeStringfier implements Function<AccessLogFormatsTest.Attr, String> {
        @Override
        public String apply(AccessLogFormatsTest.Attr attr) {
            return ('(' + (attr.member())) + ')';
        }
    }
}

