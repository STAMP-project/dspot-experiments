package brave.propagation;


import B3Propagation.FACTORY;
import Propagation.Factory;
import org.junit.Test;


public class PropagationFactoryTest {
    Factory factory = new Propagation.Factory() {
        @Override
        public <K> Propagation<K> create(Propagation.KeyFactory<K> keyFactory) {
            return null;
        }
    };

    /**
     * 64 bit trace IDs are not consistently mandatory across propagation, yet.
     */
    @Test
    public void requires128BitTraceId_defaultsToFalse() {
        assertThat(factory.requires128BitTraceId()).isFalse();
    }

    /**
     * join (reusing span ID on client and server side) is rarely supported outside B3.
     */
    @Test
    public void supportsJoin_defaultsToFalse() {
        assertThat(FACTORY.supportsJoin()).isTrue();
        assertThat(factory.supportsJoin()).isFalse();
    }

    @Test
    public void decorate_defaultsToReturnSameInstance() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(1).build();
        assertThat(factory.decorate(context)).isSameAs(context);
    }
}

