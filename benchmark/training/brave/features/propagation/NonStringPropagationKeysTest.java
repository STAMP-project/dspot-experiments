package brave.features.propagation;


import B3Propagation.FACTORY;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import io.grpc.Metadata;
import org.junit.Test;


/**
 * This shows propagation keys don't need to be Strings. For example, we can propagate over gRPC
 */
public class NonStringPropagationKeysTest {
    TraceContext context = TraceContext.newBuilder().traceId(1).spanId(2).sampled(true).build();

    Propagation<Metadata.Key<String>> grpcPropagation = FACTORY.create(( name) -> Metadata.Key.of(name, Metadata.ASCII_STRING_MARSHALLER));

    TraceContext.Extractor<Metadata> extractor = grpcPropagation.extractor(Metadata::get);

    TraceContext.Injector<Metadata> injector = grpcPropagation.injector(Metadata::put);

    @Test
    public void injectExtractTraceContext() {
        Metadata metadata = new Metadata();
        injector.inject(context, metadata);
        assertThat(metadata.keys()).containsExactly("x-b3-traceid", "x-b3-spanid", "x-b3-sampled");
        assertThat(extractor.extract(metadata).context()).isEqualTo(context);
    }
}

