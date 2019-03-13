package brave.internal;


import B3Propagation.FACTORY;
import Propagation.Factory;
import brave.ScopedSpan;
import brave.Tracing;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public abstract class ExtraFactoryTest<E, F extends ExtraFactory<E>> {
    protected F factory = newFactory();

    protected final Factory propagationFactory = new Propagation.Factory() {
        @Override
        public <K> Propagation<K> create(Propagation.KeyFactory<K> keyFactory) {
            return FACTORY.create(keyFactory);
        }

        @Override
        public TraceContext decorate(TraceContext context) {
            return factory.decorate(context);
        }
    };

    protected TraceContext context = propagationFactory.decorate(TraceContext.newBuilder().traceId(1L).spanId(2L).sampled(true).build());

    @Test
    public void decorate_empty() {
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(1L))).extra()).containsExactly(1L, create());
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(1L, 2L))).extra()).containsExactly(1L, 2L, create());
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(create()))).extra()).containsExactly(create());
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(create(), 1L))).extra()).containsExactly(create(), 1L);
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(1L, create()))).extra()).containsExactly(1L, create());
        E claimedBySelf = factory.create();
        factory.tryToClaim(claimedBySelf, context.traceId(), context.spanId());
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(claimedBySelf))).extra()).containsExactly(create());
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(claimedBySelf, 1L))).extra()).containsExactly(create(), 1L);
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(1L, claimedBySelf))).extra()).containsExactly(1L, create());
        E claimedByOther = factory.create();
        tryToClaim(claimedBySelf, 99L, 99L);
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(claimedByOther))).extra()).containsExactly(create());
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(claimedByOther, 1L))).extra()).containsExactly(create(), 1L);
        assertThat(factory.decorate(ExtraFactoryTest.contextWithExtra(context, Arrays.asList(1L, claimedByOther))).extra()).containsExactly(1L, create());
    }

    @Test
    public void idempotent() {
        List<Object> originalExtra = context.extra();
        assertThat(propagationFactory.decorate(context).extra()).isSameAs(originalExtra);
    }

    @Test
    public void toSpan_selfLinksContext() {
        try (Tracing t = Tracing.newBuilder().propagationFactory(propagationFactory).build()) {
            ScopedSpan parent = t.tracer().startScopedSpan("parent");
            try {
                E extra = ((E) (parent.context().extra().get(0)));
                assertAssociatedWith(extra, parent.context().traceId(), parent.context().spanId());
            } finally {
                parent.finish();
            }
        }
    }
}

