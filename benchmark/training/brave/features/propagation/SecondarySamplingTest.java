package brave.features.propagation;


import Kind.CLIENT;
import Kind.SERVER;
import TraceContext.Extractor;
import TraceContext.Injector;
import brave.Tracer;
import brave.Tracing;
import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.propagation.B3SinglePropagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import zipkin2.Span;


public class SecondarySamplingTest {
    List<Span> zipkin = new ArrayList<>();

    List<MutableSpan> zeus = new ArrayList<>();

    List<MutableSpan> apollo = new ArrayList<>();

    Map<String, FinishedSpanHandler> stateToFinishedSpanHandler = ImmutableMap.of("zeus", new FinishedSpanHandler() {
        @Override
        public boolean handle(TraceContext context, MutableSpan span) {
            return zeus.add(span);
        }
    }, "apollo", new FinishedSpanHandler() {
        @Override
        public boolean handle(TraceContext context, MutableSpan span) {
            return apollo.add(span);
        }
    });

    Tracing tracing = Tracing.newBuilder().addFinishedSpanHandler(new SecondarySampling.FinishedSpanHandler(stateToFinishedSpanHandler)).propagationFactory(new SecondarySampling.PropagationFactory(B3SinglePropagation.FACTORY, stateToFinishedSpanHandler.keySet())).spanReporter(zipkin::add).build();

    Extractor<Map<String, String>> extractor = tracing.propagation().extractor(Map::get);

    Injector<Map<String, String>> injector = tracing.propagation().injector(Map::put);

    Map<String, String> map = new LinkedHashMap<>();

    /**
     * This shows when primary trace status is not sampled, we can send to handlers anyway.
     */
    @Test
    public void integrationTest() {
        map.put("b3", "0");
        map.put("sampling", "zeus:rate=1.0,ttl=3;apollo:sampled=1;wookie:rate=0.05");
        Tracer tracer = tracing.tracer();
        brave.Span span1 = tracer.nextSpan(extractor.extract(map)).name("span1").kind(SERVER).start();
        brave.Span span2 = tracer.newChild(span1.context()).kind(CLIENT).name("span2").start();
        injector.inject(span2.context(), map);
        assertThat(map).containsEntry("sampling", "zeus:ttl=3,sampled=1;apollo:sampled=1;wookie:rate=0.05");
        // hop 1
        brave.Span span3 = tracer.joinSpan(extractor.extract(map).context()).kind(SERVER).start();
        brave.Span span4 = tracer.newChild(span3.context()).kind(CLIENT).name("span3").start();
        injector.inject(span4.context(), map);
        assertThat(map).containsEntry("sampling", "zeus:sampled=1,ttl=2;apollo:sampled=1;wookie:rate=0.05");
        // hop 2
        brave.Span span5 = tracer.joinSpan(extractor.extract(map).context()).kind(SERVER).start();
        brave.Span span6 = tracer.newChild(span5.context()).kind(CLIENT).name("span4").start();
        injector.inject(span6.context(), map);
        assertThat(map).containsEntry("sampling", "zeus:sampled=1,ttl=1;apollo:sampled=1;wookie:rate=0.05");
        // hop 3
        brave.Span span7 = tracer.joinSpan(extractor.extract(map).context()).kind(SERVER).start();
        brave.Span span8 = tracer.newChild(span7.context()).kind(CLIENT).name("span5").start();
        injector.inject(span8.context(), map);
        assertThat(map).containsEntry("sampling", "apollo:sampled=1;wookie:rate=0.05");
        // hop 4
        brave.Span span9 = tracer.joinSpan(extractor.extract(map).context()).kind(SERVER).start();
        brave.Span span10 = tracer.newChild(span9.context()).kind(CLIENT).name("span6").start();
        injector.inject(span10.context(), map);
        assertThat(map).containsEntry("sampling", "apollo:sampled=1;wookie:rate=0.05");
        Arrays.asList(span1, span2, span3, span4, span5, span6, span7, span8, span9, span10).forEach(Span::finish);
        assertThat(zipkin).isEmpty();
        assertThat(zeus).filteredOn(( s) -> (s.kind()) == Kind.SERVER).hasSize(4);
        assertThat(apollo).filteredOn(( s) -> (s.kind()) == Kind.SERVER).hasSize(5);
    }

    @Test
    public void extract_samplesLocalWhenConfigured() {
        map.put("b3", "0");
        map.put("sampling", "apollo:sampled=0;wookie:rate=0.05");
        assertThat(extractor.extract(map).sampledLocal()).isFalse();
        map.put("b3", "0");
        map.put("sampling", "apollo:sampled=0;wookie:sampled=1");
        assertThat(extractor.extract(map).sampledLocal()).isFalse();
        map.put("b3", "0");
        map.put("sampling", "apollo:sampled=1;wookie:rate=0.05");
        assertThat(extractor.extract(map).sampledLocal()).isTrue();
    }

    @Test
    public void extract_convertsConfiguredRateToDecision() {
        map.put("b3", "0");
        map.put("sampling", "zeus:rate=1.0,ttl=3;apollo:sampled=0;wookie:rate=0.05");
        TraceContextOrSamplingFlags extracted = extractor.extract(map);
        SecondarySampling.Extra extra = ((SecondarySampling.Extra) (extracted.extra().get(0)));
        assertThat(extra.states).containsEntry("zeus", ImmutableMap.of("sampled", "1", "ttl", "3")).containsEntry("apollo", ImmutableMap.of("sampled", "0")).containsEntry("wookie", ImmutableMap.of("rate", "0.05"));
    }

    @Test
    public void extract_decrementsTtlWhenConfigured() {
        map.put("b3", "0");
        map.put("sampling", "zeus:sampled=1,ttl=3;apollo:sampled=0,ttl=1;wookie:rate=0.05");
        TraceContextOrSamplingFlags extracted = extractor.extract(map);
        SecondarySampling.Extra extra = ((SecondarySampling.Extra) (extracted.extra().get(0)));
        assertThat(extra.states).containsEntry("zeus", ImmutableMap.of("sampled", "1", "ttl", "2")).doesNotContainKey("apollo").containsEntry("wookie", ImmutableMap.of("rate", "0.05"));
    }

    @Test
    public void injectWritesAllStates() {
        SecondarySampling.Extra extra = new SecondarySampling.Extra();
        extra.states.put("zeus", ImmutableMap.of("rate", "1.0", "ttl", "3"));
        extra.states.put("apollo", ImmutableMap.of("sampled", "0"));
        extra.states.put("wookie", ImmutableMap.of("rate", "0.05"));
        injector.inject(TraceContext.newBuilder().traceId(1L).spanId(2L).sampled(false).extra(Arrays.asList(extra)).build(), map);
        assertThat(map).containsEntry("sampling", "zeus:rate=1.0,ttl=3;apollo:sampled=0;wookie:rate=0.05");
    }
}

