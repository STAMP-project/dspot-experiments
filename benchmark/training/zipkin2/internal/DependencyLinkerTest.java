/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.internal;


import Kind.CLIENT;
import Kind.CONSUMER;
import Kind.PRODUCER;
import Kind.SERVER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import zipkin2.DependencyLink;
import zipkin2.Span;


public class DependencyLinkerTest {
    static final List<Span> TRACE = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "web", null, false), DependencyLinkerTest.span2("a", "a", "b", CLIENT, "web", "app", false), DependencyLinkerTest.span2("a", "a", "b", SERVER, "app", "web", false).toBuilder().shared(true).build(), DependencyLinkerTest.span2("a", "b", "c", CLIENT, "app", "db", true));

    List<String> messages = new ArrayList<>();

    Logger logger = new Logger("", null) {
        {
            setLevel(Level.ALL);
        }

        @Override
        public void log(Level level, String msg) {
            assertThat(level).isEqualTo(Level.FINE);
            messages.add(msg);
        }
    };

    @Test
    public void baseCase() {
        assertThat(new DependencyLinker().link()).isEmpty();
    }

    @Test
    public void linksSpans() {
        assertThat(new DependencyLinker().putTrace(DependencyLinkerTest.TRACE).link()).containsExactly(DependencyLink.newBuilder().parent("web").child("app").callCount(1L).build(), DependencyLink.newBuilder().parent("app").child("db").callCount(1L).errorCount(1L).build());
    }

    /**
     * Some don't propagate the server's parent ID which creates a race condition. Try to unwind it.
     *
     * <p>See https://github.com/openzipkin/zipkin/pull/1745
     */
    @Test
    public void linksSpans_serverMissingParentId() {
        List<Span> trace = // below the parent ID is null as it wasn't propagated
        Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "arn", null, false), DependencyLinkerTest.span2("a", "a", "b", CLIENT, "arn", "link", false), DependencyLinkerTest.span2("a", null, "b", SERVER, "link", "arn", false).toBuilder().shared(true).build());
        // trace is actually reported in reverse order
        Collections.reverse(trace);
        assertThat(new DependencyLinker().putTrace(trace).link()).containsExactly(DependencyLink.newBuilder().parent("arn").child("link").callCount(1L).build());
    }

    /**
     * In case of a late error, we should know which trace ID is being processed
     */
    @Test
    public void logsTraceId() {
        new DependencyLinker(logger).putTrace(DependencyLinkerTest.TRACE);
        assertThat(messages).contains("building trace tree: traceId=000000000000000a");
    }

    @Test
    public void messagingSpansDontLinkWithoutBroker_consumer() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", PRODUCER, "producer", null, false), DependencyLinkerTest.span2("a", "a", "b", CONSUMER, "consumer", "kafka", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("kafka").child("consumer").callCount(1L).build());
    }

    @Test
    public void messagingSpansDontLinkWithoutBroker_producer() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", PRODUCER, "producer", "kafka", false), DependencyLinkerTest.span2("a", "a", "b", CONSUMER, "consumer", null, false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("producer").child("kafka").callCount(1L).build());
    }

    @Test
    public void messagingWithBroker_both_sides_same() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", PRODUCER, "producer", "kafka", false), DependencyLinkerTest.span2("a", "a", "b", CONSUMER, "consumer", "kafka", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("producer").child("kafka").callCount(1L).build(), DependencyLink.newBuilder().parent("kafka").child("consumer").callCount(1L).build());
    }

    @Test
    public void messagingWithBroker_different() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", PRODUCER, "producer", "kafka1", false), DependencyLinkerTest.span2("a", "a", "b", CONSUMER, "consumer", "kafka2", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("producer").child("kafka1").callCount(1L).build(), DependencyLink.newBuilder().parent("kafka2").child("consumer").callCount(1L).build());
    }

    /**
     * Shows we don't assume there's a direct link between producer and consumer.
     */
    @Test
    public void messagingWithoutBroker_noLinks() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", PRODUCER, "producer", null, false), DependencyLinkerTest.span2("a", "a", "b", CONSUMER, "consumer", null, false));
        assertThat(new DependencyLinker().putTrace(trace).link()).isEmpty();
    }

    /**
     * When a server is the child of a producer span, make a link as it is really an RPC
     */
    @Test
    public void producerLinksToServer_childSpan() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", PRODUCER, "producer", null, false), DependencyLinkerTest.span2("a", "a", "b", SERVER, "server", null, false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("producer").child("server").callCount(1L).build());
    }

    /**
     * Servers most often join a span vs create a child. Make sure this works when a producer is used
     * instead of a client.
     */
    @Test
    public void producerLinksToServer_sameSpan() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", PRODUCER, "producer", null, false), DependencyLinkerTest.span2("a", null, "a", SERVER, "server", null, false).toBuilder().shared(true).build());
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("producer").child("server").callCount(1L).build());
    }

    /**
     * Client might be used for historical reasons instead of PRODUCER. Don't link as the server-side
     * is authoritative.
     */
    @Test
    public void clientDoesntLinkToConsumer_child() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", null, false), DependencyLinkerTest.span2("a", "a", "b", CONSUMER, "consumer", null, false));
        assertThat(new DependencyLinker().putTrace(trace).link()).isEmpty();
    }

    /**
     * A root span can be a client-originated trace or a server receipt which knows its peer. In these
     * cases, the peer is known and kind establishes the direction.
     */
    @Test
    public void linksSpansDirectedByKind() {
        List<Span> validRootSpans = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "server", "client", false), DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", "server", false).toBuilder().shared(true).build());
        for (Span span : validRootSpans) {
            assertThat(new DependencyLinker().putTrace(Arrays.asList(span)).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(1L).build());
        }
    }

    @Test
    public void callsAgainstTheSameLinkIncreasesCallCount_span() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "client", null, false), DependencyLinkerTest.span2("a", "a", "b", CLIENT, null, "server", false), DependencyLinkerTest.span2("a", "a", "c", CLIENT, null, "server", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(2L).build());
    }

    @Test
    public void callsAgainstTheSameLinkIncreasesCallCount_trace() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "client", null, false), DependencyLinkerTest.span2("a", "a", "b", CLIENT, null, "server", false));
        assertThat(new DependencyLinker().putTrace(trace).putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(2L).build());
    }

    /**
     * Spans don't always include both the client and server service. When you know the kind, you can
     * link these without duplicating call count.
     */
    @Test
    public void singleHostSpansResultInASingleCallCount() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", null, false), DependencyLinkerTest.span2("a", "a", "b", SERVER, "server", null, false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(1L).build());
    }

    @Test
    public void singleHostSpansResultInASingleErrorCount() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", null, true), DependencyLinkerTest.span2("a", "a", "b", SERVER, "server", null, true));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(1L).errorCount(1L).build());
    }

    @Test
    public void singleHostSpansResultInASingleErrorCount_sameId() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", null, true), DependencyLinkerTest.span2("a", null, "a", SERVER, "server", null, true).toBuilder().shared(true).build());
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(1L).errorCount(1L).build());
    }

    @Test
    public void singleHostSpansResultInASingleCallCount_defersNameToServer() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", "server", false), DependencyLinkerTest.span2("a", "a", "b", SERVER, "server", null, false));
        assertThat(new DependencyLinker(logger).putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(1L).build());
    }

    @Test
    public void singleHostSpans_multipleChildren() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", null, false), DependencyLinkerTest.span2("a", "a", "b", SERVER, "server", "client", true), DependencyLinkerTest.span2("a", "a", "c", SERVER, "server", "client", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(2L).errorCount(1L).build());
    }

    @Test
    public void singleHostSpans_multipleChildren_defersNameToServer() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", "server", false), DependencyLinkerTest.span2("a", "a", "b", SERVER, "server", null, false), DependencyLinkerTest.span2("a", "a", "c", SERVER, "server", null, false));
        assertThat(new DependencyLinker(logger).putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(2L).build());
    }

    /**
     * Spans are sometimes intermediated by an unknown type of span. Prefer the nearest server when
     * accounting for them.
     */
    @Test
    public void intermediatedClientSpansMissingLocalServiceNameLinkToNearestServer() {
        List<Span> trace = // possibly a local fan-out span
        Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "client", null, false), DependencyLinkerTest.span2("a", "a", "b", null, null, null, false), DependencyLinkerTest.span2("a", "b", "c", CLIENT, "server", null, false), DependencyLinkerTest.span2("a", "b", "d", CLIENT, "server", null, false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(2L).build());
    }

    @Test
    public void errorsOnUninstrumentedLinks() {
        List<Span> trace = // there's no remote here, so we shouldn't see any error count
        Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "client", null, false), DependencyLinkerTest.span2("a", "a", "b", null, null, null, false), DependencyLinkerTest.span2("a", "b", "c", CLIENT, "server", null, true), DependencyLinkerTest.span2("a", "b", "d", CLIENT, "server", null, true));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("client").child("server").callCount(2L).build());
    }

    @Test
    public void errorsOnInstrumentedLinks() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "foo", null, false), DependencyLinkerTest.span2("a", "a", "b", null, null, null, false), DependencyLinkerTest.span2("a", "b", "c", CLIENT, "bar", "baz", true), DependencyLinkerTest.span2("a", "b", "d", CLIENT, "bar", "baz", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("foo").child("bar").callCount(2L).build(), DependencyLink.newBuilder().parent("bar").child("baz").callCount(2L).errorCount(1L).build());
    }

    @Test
    public void linkWithErrorIsLogged() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", "b", "c", CLIENT, "foo", "bar", true));
        new DependencyLinker(logger).putTrace(trace).link();
        assertThat(messages).contains("incrementing error link foo -> bar");
    }

    /**
     * Tag indicates a failed span, not an annotation
     */
    @Test
    public void annotationNamedErrorDoesntIncrementErrorCount() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", "b", "c", CLIENT, "foo", "bar", false).toBuilder().addAnnotation(1L, "error").build());
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("foo").child("bar").callCount(1L).build());
    }

    /**
     * A loopback span is direction-agnostic, so can be linked properly regardless of kind.
     */
    @Test
    public void linksLoopbackSpans() {
        List<Span> validRootSpans = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "service", "service", false), DependencyLinkerTest.span2("b", null, "b", CLIENT, "service", "service", false));
        for (Span span : validRootSpans) {
            assertThat(new DependencyLinker().putTrace(Arrays.asList(span)).link()).containsOnly(DependencyLink.newBuilder().parent("service").child("service").callCount(1L).build());
        }
    }

    @Test
    public void noSpanKindTreatedSameAsClient() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", null, "some-client", "web", false), DependencyLinkerTest.span2("a", "a", "b", null, "web", "app", false), DependencyLinkerTest.span2("a", "b", "c", null, "app", "db", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("some-client").child("web").callCount(1L).build(), DependencyLink.newBuilder().parent("web").child("app").callCount(1L).build(), DependencyLink.newBuilder().parent("app").child("db").callCount(1L).build());
    }

    @Test
    public void noSpanKindWithError() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", null, "some-client", "web", false), DependencyLinkerTest.span2("a", "a", "b", null, "web", "app", true), DependencyLinkerTest.span2("a", "b", "c", null, "app", "db", false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("some-client").child("web").callCount(1L).build(), DependencyLink.newBuilder().parent("web").child("app").callCount(1L).errorCount(1L).build(), DependencyLink.newBuilder().parent("app").child("db").callCount(1L).build());
    }

    /**
     * A dependency link is between two services. We cannot link if we don't know both service names.
     */
    @Test
    public void cannotLinkSingleSpanWithoutBothServiceNames() {
        List<Span> incompleteRootSpans = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, null, null, false), DependencyLinkerTest.span2("a", null, "a", SERVER, "server", null, false), DependencyLinkerTest.span2("a", null, "a", SERVER, null, "client", false), DependencyLinkerTest.span2("a", null, "a", CLIENT, null, null, false), DependencyLinkerTest.span2("a", null, "a", CLIENT, "client", null, false), DependencyLinkerTest.span2("a", null, "a", CLIENT, null, "server", false));
        for (Span span : incompleteRootSpans) {
            assertThat(new DependencyLinker(logger).putTrace(Arrays.asList(span)).link()).isEmpty();
        }
    }

    @Test
    public void doesntLinkUnrelatedSpansWhenMissingRootSpan() {
        String missingParentId = "a";
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", missingParentId, "b", SERVER, "service1", null, false), DependencyLinkerTest.span2("a", missingParentId, "c", SERVER, "service2", null, false));
        assertThat(new DependencyLinker(logger).putTrace(trace).link()).isEmpty();
    }

    @Test
    public void linksRelatedSpansWhenMissingRootSpan() {
        String missingParentId = "a";
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", missingParentId, "b", SERVER, "service1", null, false), DependencyLinkerTest.span2("a", "b", "c", SERVER, "service2", null, false));
        assertThat(new DependencyLinker(logger).putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("service1").child("service2").callCount(1L).build());
    }

    /**
     * Client+Server spans that don't share IDs are treated as server spans missing their peer
     */
    @Test
    public void linksSingleHostSpans() {
        List<Span> singleHostSpans = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "web", null, false), DependencyLinkerTest.span2("a", "a", "b", SERVER, "app", null, false));
        assertThat(new DependencyLinker().putTrace(singleHostSpans).link()).containsOnly(DependencyLink.newBuilder().parent("web").child("app").callCount(1L).build());
    }

    @Test
    public void linksSingleHostSpans_errorOnClient() {
        List<Span> trace = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", CLIENT, "web", null, true), DependencyLinkerTest.span2("a", "a", "b", SERVER, "app", null, false));
        assertThat(new DependencyLinker().putTrace(trace).link()).containsOnly(DependencyLink.newBuilder().parent("web").child("app").callCount(1L).errorCount(1L).build());
    }

    /**
     * Creates a link when there's a span missing, in this case 2L which is an RPC from web to app
     */
    @Test
    public void missingSpan() {
        List<Span> singleHostSpans = Arrays.asList(DependencyLinkerTest.span2("a", null, "a", SERVER, "web", null, false), DependencyLinkerTest.span2("a", "a", "b", CLIENT, "app", null, false));
        assertThat(new DependencyLinker(logger).putTrace(singleHostSpans).link()).containsOnly(DependencyLink.newBuilder().parent("web").child("app").callCount(1L).build());
        assertThat(messages).contains("detected missing link to client span");
    }

    @Test
    public void merge() {
        List<DependencyLink> links = Arrays.asList(DependencyLink.newBuilder().parent("foo").child("bar").callCount(2L).errorCount(1L).build(), DependencyLink.newBuilder().parent("foo").child("bar").callCount(2L).errorCount(2L).build(), DependencyLink.newBuilder().parent("foo").child("foo").callCount(1L).build());
        assertThat(DependencyLinker.merge(links)).containsExactly(DependencyLink.newBuilder().parent("foo").child("bar").callCount(4L).errorCount(3L).build(), DependencyLink.newBuilder().parent("foo").child("foo").callCount(1L).build());
    }

    @Test
    public void merge_error() {
        List<DependencyLink> links = Arrays.asList(DependencyLink.newBuilder().parent("client").child("server").callCount(2L).build(), DependencyLink.newBuilder().parent("client").child("server").callCount(2L).build(), DependencyLink.newBuilder().parent("client").child("client").callCount(1L).build());
        assertThat(DependencyLinker.merge(links)).containsExactly(DependencyLink.newBuilder().parent("client").child("server").callCount(4L).build(), DependencyLink.newBuilder().parent("client").child("client").callCount(1L).build());
    }
}

