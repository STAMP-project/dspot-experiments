package org.stagemonitor.tracing.anonymization;


import SpanUtils.IPV4_STRING;
import Tags.PEER_HOST_IPV4;
import Tags.PEER_HOST_IPV6;
import io.opentracing.mock.MockTracer;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.core.util.InetAddresses;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class AnonymizingSpanEventListenerTest {
    private static final Inet4Address IPV4 = ((Inet4Address) (InetAddresses.forString("123.123.123.123")));

    private static final Inet4Address IPV4_ANONYMIZED = ((Inet4Address) (InetAddresses.forString("123.123.123.0")));

    private static final Inet6Address IPV6 = ((Inet6Address) (InetAddresses.forString("2001:0db8:85a3:0000:0000:8a2e:0370:7334")));

    private static final Inet6Address IPV6_ANONYMIZED = ((Inet6Address) (InetAddresses.forString("2001:0db8:85a3:0000:0000:0000:0000:0000")));

    private TracingPlugin tracingPlugin;

    private MockTracer mockTracer = new MockTracer();

    @Test
    public void testAnonymizeUserNameAndIpV4() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(true);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        final SpanWrapper span = createSpanIpv4();
        assertThat(span.getTags()).containsEntry("username", "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");
        assertThat(span.getTags()).doesNotContainKey("username_disclosed");
        assertThat(span.getTags()).containsEntry(PEER_HOST_IPV4.getKey(), InetAddresses.inetAddressToInt(AnonymizingSpanEventListenerTest.IPV4_ANONYMIZED));
    }

    @Test
    public void testAnonymizeIpv6() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(false);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        final SpanWrapper span = createSpan("test", AnonymizingSpanEventListenerTest.IPV6.getHostAddress());
        assertThat(span.getTags()).containsEntry("username", "test");
        assertThat(span.getTags()).doesNotContainKey("username_disclosed");
        assertThat(span.getTags()).containsEntry(PEER_HOST_IPV6.getKey(), AnonymizingSpanEventListenerTest.IPV6_ANONYMIZED.getHostAddress());
    }

    @Test
    public void testAnonymizeIpV4() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(false);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        final SpanWrapper span = createSpanIpv4();
        assertThat(span.getTags()).containsEntry("username", "test");
        assertThat(span.getTags()).doesNotContainKey("username_disclosed");
        assertThat(span.getTags()).containsEntry(PEER_HOST_IPV4.getKey(), InetAddresses.inetAddressToInt(AnonymizingSpanEventListenerTest.IPV4_ANONYMIZED));
    }

    @Test
    public void testDiscloseUserNameAndIp() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(true);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        Mockito.when(tracingPlugin.getDiscloseUsers()).thenReturn(Collections.singleton("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3"));
        final SpanWrapper span = createSpanIpv4();
        assertThat(span.getTags()).containsEntry("username", "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");
        assertThat(span.getTags()).containsEntry("username_disclosed", "test");
        assertThat(span.getTags()).containsEntry(PEER_HOST_IPV4.getKey(), InetAddresses.inetAddressToInt(AnonymizingSpanEventListenerTest.IPV4));
    }

    @Test
    public void testDiscloseIp() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(false);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        Mockito.when(tracingPlugin.getDiscloseUsers()).thenReturn(Collections.singleton("test"));
        final SpanWrapper span = createSpanIpv4();
        assertThat(span.getTags()).containsEntry("username", "test");
        assertThat(span.getTags()).containsEntry("username_disclosed", "test");
        assertThat(span.getTags()).containsEntry(PEER_HOST_IPV4.getKey(), InetAddresses.inetAddressToInt(AnonymizingSpanEventListenerTest.IPV4));
    }

    @Test
    public void testNull() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(true);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        final SpanWrapper span = createSpan(null, null);
        assertThat(span.getTags()).doesNotContainKey("username");
        assertThat(span.getTags()).doesNotContainKey("username_disclosed");
        assertThat(span.getTags()).doesNotContainKey(IPV4_STRING);
    }

    @Test
    public void testNullUser() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(true);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        final SpanWrapper span = createSpan(null, AnonymizingSpanEventListenerTest.IPV4.getHostAddress());
        assertThat(span.getTags()).doesNotContainKey("username");
        assertThat(span.getTags()).doesNotContainKey("username_disclosed");
        assertThat(span.getTags()).containsEntry(PEER_HOST_IPV4.getKey(), InetAddresses.inetAddressToInt(AnonymizingSpanEventListenerTest.IPV4_ANONYMIZED));
    }

    @Test
    public void testNullIp() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(true);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(true);
        final SpanWrapper span = createSpan("test", null);
        assertThat(span.getTags()).containsEntry("username", "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");
        assertThat(span.getTags()).doesNotContainKey("username_disclosed");
        assertThat(span.getTags()).doesNotContainKey(IPV4_STRING);
    }

    @Test
    public void testDontAnonymize() throws Exception {
        Mockito.when(tracingPlugin.isPseudonymizeUserNames()).thenReturn(false);
        Mockito.when(tracingPlugin.isAnonymizeIPs()).thenReturn(false);
        final SpanWrapper span = createSpanIpv4();
        assertThat(span.getTags()).containsEntry("username", "test");
        assertThat(span.getTags()).doesNotContainKey("username_disclosed");
        assertThat(span.getTags()).containsEntry(PEER_HOST_IPV4.getKey(), InetAddresses.inetAddressToInt(AnonymizingSpanEventListenerTest.IPV4));
    }
}

