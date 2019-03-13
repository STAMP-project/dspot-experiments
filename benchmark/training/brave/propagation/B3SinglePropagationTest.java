package brave.propagation;


import SamplingFlags.EMPTY;
import SamplingFlags.NOT_SAMPLED;
import brave.test.propagation.PropagationTest;
import java.util.function.Supplier;
import org.junit.Test;

import static Propagation.B3_SINGLE_STRING;


public class B3SinglePropagationTest extends PropagationTest<String> {
    static class PropagationSupplier implements Supplier<Propagation<String>> {
        @Override
        public Propagation<String> get() {
            return B3_SINGLE_STRING;
        }
    }

    @Test
    public void extractTraceContext_sampledFalse() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("b3", "0");
        SamplingFlags result = propagation.extractor(mapEntry).extract(map).samplingFlags();
        assertThat(result).isEqualTo(NOT_SAMPLED);
    }

    @Test
    public void extractTraceContext_malformed() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("b3", "not-a-tumor");
        SamplingFlags result = propagation.extractor(mapEntry).extract(map).samplingFlags();
        assertThat(result).isEqualTo(EMPTY);
    }

    @Test
    public void extractTraceContext_malformed_uuid() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("b3", "b970dafd-0d95-40aa-95d8-1d8725aebe40");
        SamplingFlags result = propagation.extractor(mapEntry).extract(map).samplingFlags();
        assertThat(result).isEqualTo(EMPTY);
    }

    @Test
    public void extractTraceContext_debug_with_ids() {
        MapEntry<String> mapEntry = new MapEntry();
        map.put("b3", "4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-d");
        TraceContext result = propagation.extractor(mapEntry).extract(map).context();
        assertThat(result.debug()).isTrue();
    }
}

