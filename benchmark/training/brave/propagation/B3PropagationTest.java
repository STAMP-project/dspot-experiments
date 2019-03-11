package brave.propagation;


import SamplingFlags.EMPTY;
import SamplingFlags.NOT_SAMPLED;
import brave.test.propagation.PropagationTest;
import java.util.function.Supplier;
import org.junit.Test;

import static Propagation.B3_STRING;


public class B3PropagationTest extends PropagationTest<String> {
    static class PropagationSupplier implements Supplier<Propagation<String>> {
        @Override
        public Propagation<String> get() {
            return B3_STRING;
        }
    }

    @Test
    public void extractTraceContext_sampledFalse() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("X-B3-Sampled", "false");
        SamplingFlags result = propagation.extractor(mapEntry).extract(map).samplingFlags();
        assertThat(result).isEqualTo(NOT_SAMPLED);
    }

    @Test
    public void extractTraceContext_sampledFalseUpperCase() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("X-B3-Sampled", "FALSE");
        SamplingFlags result = propagation.extractor(mapEntry).extract(map).samplingFlags();
        assertThat(result).isEqualTo(NOT_SAMPLED);
    }

    @Test
    public void extractTraceContext_malformed() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("X-B3-TraceId", "463ac35c9f6413ad48485a3953bb6124");// ok

        map.put("X-B3-SpanId", "48485a3953bb6124");// ok

        map.put("X-B3-ParentSpanId", "-");// not ok

        SamplingFlags result = propagation.extractor(mapEntry).extract(map).samplingFlags();
        assertThat(result).isEqualTo(EMPTY);
    }

    @Test
    public void extractTraceContext_malformed_sampled() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("X-B3-TraceId", "-");// not ok

        map.put("X-B3-Sampled", "1");// ok

        SamplingFlags result = propagation.extractor(mapEntry).extract(map).samplingFlags();
        assertThat(result).isEqualTo(EMPTY);
    }

    @Test
    public void extractTraceContext_debug_with_ids() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("X-B3-TraceId", "463ac35c9f6413ad48485a3953bb6124");// ok

        map.put("X-B3-SpanId", "48485a3953bb6124");// ok

        map.put("X-B3-Flags", "1");// accidentally missing sampled flag

        TraceContext result = propagation.extractor(mapEntry).extract(map).context();
        assertThat(result.sampled()).isTrue();
    }

    @Test
    public void extractTraceContext_singleHeaderFormat() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("b3", "4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7");
        TraceContext result = propagation.extractor(mapEntry).extract(map).context();
        assertThat(result.traceIdString()).isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
        assertThat(result.spanIdString()).isEqualTo("00f067aa0ba902b7");
    }
}

