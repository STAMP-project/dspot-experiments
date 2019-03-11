package net.bytebuddy.agent.builder;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.packaging.SimpleType;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.IntegrationRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;


public class AgentBuilderDefaultApplicationResubmissionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final long TIMEOUT = 1L;

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    private ClassLoader classLoader;

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testResubmission() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        try {
            MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
            ClassFileTransformer classFileTransformer = new AgentBuilder.Default(new ByteBuddy().with(TypeValidation.DISABLED)).ignore(ElementMatchers.none()).disableClassFormatChanges().with(INSTANCE).with(RETRANSFORMATION).withResubmission(new AgentBuilder.RedefinitionStrategy.ResubmissionScheduler() {
                public boolean isAlive() {
                    return true;
                }

                public AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.Cancelable schedule(final Runnable job) {
                    return new AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.Cancelable.ForFuture(scheduledExecutorService.scheduleWithFixedDelay(job, AgentBuilderDefaultApplicationResubmissionTest.TIMEOUT, AgentBuilderDefaultApplicationResubmissionTest.TIMEOUT, TimeUnit.SECONDS));
                }
            }).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationResubmissionTest.SampleTransformer()).installOnByteBuddyAgent();
            try {
                Class<?> type = classLoader.loadClass(SimpleType.class.getName());
                Thread.sleep(TimeUnit.SECONDS.toMillis(((AgentBuilderDefaultApplicationResubmissionTest.TIMEOUT) * 3)));
                MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationResubmissionTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationResubmissionTest.BAR))));
            } finally {
                ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            }
        } finally {
            scheduledExecutorService.shutdown();
        }
    }

    @Test
    public void testResubmissionCancelationNonOperational() throws Exception {
        AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.Cancelable.NoOp.INSTANCE.cancel();
    }

    @Test
    public void testResubmissionCancelationForFuture() throws Exception {
        Future<?> future = Mockito.mock(Future.class);
        new AgentBuilder.RedefinitionStrategy.ResubmissionScheduler.Cancelable.ForFuture(future).cancel();
        Mockito.verify(future).cancel(true);
        Mockito.verifyNoMoreInteractions(future);
    }

    private static class SampleTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.method(ElementMatchers.named(AgentBuilderDefaultApplicationResubmissionTest.FOO)).intercept(FixedValue.value(AgentBuilderDefaultApplicationResubmissionTest.BAR));
        }
    }
}

