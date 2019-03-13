package brave.features.sampler;


import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import brave.sampler.DeclarativeSampler;
import brave.sampler.Sampler;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zipkin2.Span;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AspectJSamplerTest.Config.class)
public class AspectJSamplerTest {
    // Don't use static configuration in real life. This is only to satisfy the unit test runner
    static List<Span> spans = new ArrayList<>();

    static AtomicReference<Tracing> tracing = new AtomicReference<>();

    @Autowired
    AspectJSamplerTest.Service service;

    @Test
    public void traced() {
        service.traced();
        assertThat(AspectJSamplerTest.spans).isNotEmpty();
    }

    @Test
    public void notTraced() {
        service.notTraced();
        assertThat(AspectJSamplerTest.spans).isEmpty();
    }

    @Configuration
    @EnableAspectJAutoProxy
    @Import({ AspectJSamplerTest.Service.class, AspectJSamplerTest.TracingAspect.class })
    static class Config {}

    @Component
    @Aspect
    static class TracingAspect {
        DeclarativeSampler<AspectJSamplerTest.Traced> declarativeSampler = DeclarativeSampler.create(AspectJSamplerTest.Traced::sampleRate);

        @Around("@annotation(traced)")
        public Object traceThing(ProceedingJoinPoint pjp, AspectJSamplerTest.Traced traced) throws Throwable {
            // When there is no trace in progress, this overrides the decision based on the annotation
            Sampler decideUsingAnnotation = declarativeSampler.toSampler(traced);
            Tracer tracer = AspectJSamplerTest.tracing.get().tracer().withSampler(decideUsingAnnotation);
            // This code looks the same as if there was no declarative override
            ScopedSpan span = tracer.startScopedSpan("");
            try {
                return pjp.proceed();
            } catch (RuntimeException | Error e) {
                span.error(e);
                throw e;
            } finally {
                span.finish();
            }
        }
    }

    // aop only works for public methods.. the type can be package private though
    @Component
    static class Service {
        // these two methods set different rates. This shows that instances are independent
        @AspectJSamplerTest.Traced
        public void traced() {
        }

        @AspectJSamplerTest.Traced(sampleRate = 0.0F)
        public void notTraced() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Traced {
        float sampleRate() default 1.0F;
    }
}

