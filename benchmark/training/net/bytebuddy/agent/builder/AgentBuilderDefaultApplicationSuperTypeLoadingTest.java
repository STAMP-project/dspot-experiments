package net.bytebuddy.agent.builder;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.IntegrationRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.agent.builder.AgentBuilder.DescriptionStrategy.Default.POOL_ONLY;
import static net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.NoOp.INSTANCE;


public class AgentBuilderDefaultApplicationSuperTypeLoadingTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    private ClassLoader classLoader;

    private ExecutorService executorService;

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testSynchronousSuperTypeLoading() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(POOL_ONLY.withSuperTypeLoading()).ignore(ElementMatchers.none()).with(INSTANCE).type(ElementMatchers.isSubTypeOf(AgentBuilderDefaultApplicationSuperTypeLoadingTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationSuperTypeLoadingTest.ConstantTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationSuperTypeLoadingTest.Bar.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationSuperTypeLoadingTest.BAR).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationSuperTypeLoadingTest.BAR))));
            MatcherAssert.assertThat(type.getSuperclass().getDeclaredMethod(AgentBuilderDefaultApplicationSuperTypeLoadingTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testAsynchronousSuperTypeLoading() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(POOL_ONLY.withSuperTypeLoading(executorService)).ignore(ElementMatchers.none()).with(INSTANCE).type(ElementMatchers.isSubTypeOf(AgentBuilderDefaultApplicationSuperTypeLoadingTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationSuperTypeLoadingTest.ConstantTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationSuperTypeLoadingTest.Bar.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationSuperTypeLoadingTest.BAR).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationSuperTypeLoadingTest.BAR))));
            MatcherAssert.assertThat(type.getSuperclass().getDeclaredMethod(AgentBuilderDefaultApplicationSuperTypeLoadingTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationSuperTypeLoadingTest.FOO))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    public static class Foo {
        public String foo() {
            return null;
        }
    }

    public static class Bar extends AgentBuilderDefaultApplicationSuperTypeLoadingTest.Foo {
        public String bar() {
            return null;
        }
    }

    private static class ConstantTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.method(ElementMatchers.isDeclaredBy(typeDescription).and(ElementMatchers.named(AgentBuilderDefaultApplicationSuperTypeLoadingTest.FOO))).intercept(FixedValue.value(AgentBuilderDefaultApplicationSuperTypeLoadingTest.FOO)).method(ElementMatchers.isDeclaredBy(typeDescription).and(ElementMatchers.named(AgentBuilderDefaultApplicationSuperTypeLoadingTest.BAR))).intercept(FixedValue.value(AgentBuilderDefaultApplicationSuperTypeLoadingTest.BAR));
        }
    }
}

