package brave.spring.rabbit;


import Propagation.Factory;
import Propagation.KeyFactory.STRING;
import Reporter.NOOP;
import brave.Tracing;
import brave.propagation.Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.postprocessor.UnzipPostProcessor;
import org.springframework.cache.interceptor.CacheInterceptor;


public class SpringRabbitTracingTest {
    Tracing tracing = Tracing.newBuilder().currentTraceContext(ThreadLocalCurrentTraceContext.create()).spanReporter(NOOP).build();

    SpringRabbitTracing rabbitTracing = SpringRabbitTracing.create(tracing);

    @Test
    public void decorateRabbitTemplate_adds_by_default() {
        RabbitTemplate template = new RabbitTemplate();
        assertThat(rabbitTracing.decorateRabbitTemplate(template)).extracting("beforePublishPostProcessors").allSatisfy(( postProcessors) -> assertThat(((Collection) (postProcessors))).anyMatch(( postProcessor) -> postProcessor instanceof TracingMessagePostProcessor));
    }

    @Test
    public void decorateRabbitTemplate_skips_when_present() {
        RabbitTemplate template = new RabbitTemplate();
        template.setBeforePublishPostProcessors(new TracingMessagePostProcessor(rabbitTracing));
        assertThat(rabbitTracing.decorateRabbitTemplate(template)).extracting("beforePublishPostProcessors").hasSize(1);
    }

    @Test
    public void decorateRabbitTemplate_appends_when_absent() {
        RabbitTemplate template = new RabbitTemplate();
        template.setBeforePublishPostProcessors(new UnzipPostProcessor());
        assertThat(rabbitTracing.decorateRabbitTemplate(template)).extracting("beforePublishPostProcessors").anySatisfy(( postProcessors) -> assertThat(((Collection) (postProcessors))).anyMatch(( postProcessor) -> postProcessor instanceof TracingMessagePostProcessor));
    }

    @Test
    public void decorateSimpleRabbitListenerContainerFactory_adds_by_default() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        assertThat(rabbitTracing.decorateSimpleRabbitListenerContainerFactory(factory).getAdviceChain()).allMatch(( advice) -> advice instanceof TracingRabbitListenerAdvice);
    }

    @Test
    public void decorateSimpleRabbitListenerContainerFactory_skips_when_present() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAdviceChain(new TracingRabbitListenerAdvice(rabbitTracing));
        assertThat(rabbitTracing.decorateSimpleRabbitListenerContainerFactory(factory).getAdviceChain()).hasSize(1);
    }

    @Test
    public void decorateSimpleRabbitListenerContainerFactory_appends_when_absent() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAdviceChain(new CacheInterceptor());
        assertThat(rabbitTracing.decorateSimpleRabbitListenerContainerFactory(factory).getAdviceChain()).anyMatch(( advice) -> advice instanceof TracingRabbitListenerAdvice);
    }

    @Test
    public void failsFastIfPropagationDoesntSupportSingleHeader() {
        // Fake propagation because B3 by default does support single header extraction!
        Propagation<String> propagation = Mockito.mock(Propagation.class);
        Mockito.when(propagation.extractor(SpringRabbitPropagation.GETTER)).thenReturn(( carrier) -> {
            assertThat(carrier.getHeaders().get("b3")).isNotNull();// sanity check

            return TraceContextOrSamplingFlags.EMPTY;// pretend we couldn't parse

        });
        Propagation.Factory propagationFactory = Mockito.mock(Factory.class);
        Mockito.when(propagationFactory.create(STRING)).thenReturn(propagation);
        assertThatThrownBy(() -> SpringRabbitTracing.newBuilder(Tracing.newBuilder().propagationFactory(propagationFactory).build()).writeB3SingleFormat(true).build()).hasMessage("SpringRabbitTracing.Builder.writeB3SingleFormat set, but Tracing.Builder.propagationFactory cannot parse this format!");
    }
}

