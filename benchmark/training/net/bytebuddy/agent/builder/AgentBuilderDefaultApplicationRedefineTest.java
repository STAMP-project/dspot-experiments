package net.bytebuddy.agent.builder;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.packaging.SimpleOptionalType;
import net.bytebuddy.test.packaging.SimpleType;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.IntegrationRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.REDEFINITION;


@RunWith(Parameterized.class)
public class AgentBuilderDefaultApplicationRedefineTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private final AgentBuilder.DescriptionStrategy descriptionStrategy;

    public AgentBuilderDefaultApplicationRedefineTest(AgentBuilder.DescriptionStrategy descriptionStrategy) {
        this.descriptionStrategy = descriptionStrategy;
    }

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    private ClassLoader simpleTypeLoader;

    private ClassLoader optionalTypeLoader;

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testRedefinition() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(simpleTypeLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINITION).with(descriptionStrategy).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(simpleTypeLoader)).transform(new AgentBuilderDefaultApplicationRedefineTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = simpleTypeLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationRedefineTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationRedefineTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testRedefinitionOptionalType() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(optionalTypeLoader.loadClass(SimpleOptionalType.class.getName()).getName(), CoreMatchers.is(SimpleOptionalType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default(new ByteBuddy().with(TypeValidation.DISABLED)).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINITION).with(descriptionStrategy).type(ElementMatchers.is(SimpleOptionalType.class), ElementMatchers.is(optionalTypeLoader)).transform(new AgentBuilderDefaultApplicationRedefineTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = optionalTypeLoader.loadClass(SimpleOptionalType.class.getName());
            // The hybrid strategy cannot transform optional types.
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationRedefineTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationRedefineTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testRetransformation() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(simpleTypeLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINITION).with(descriptionStrategy).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(simpleTypeLoader)).transform(new AgentBuilderDefaultApplicationRedefineTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = simpleTypeLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationRedefineTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationRedefineTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testRetransformationDecorated() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(simpleTypeLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().ignore(ElementMatchers.none()).with(DECORATE).disableClassFormatChanges().with(REDEFINITION).with(descriptionStrategy).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(simpleTypeLoader)).transform(new AgentBuilderDefaultApplicationRedefineTest.BarTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = simpleTypeLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationRedefineTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationRedefineTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testRetransformationOptionalType() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(optionalTypeLoader.loadClass(SimpleOptionalType.class.getName()).getName(), CoreMatchers.is(SimpleOptionalType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default(new ByteBuddy().with(TypeValidation.DISABLED)).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINITION).with(descriptionStrategy).type(ElementMatchers.is(SimpleOptionalType.class), ElementMatchers.is(optionalTypeLoader)).transform(new AgentBuilderDefaultApplicationRedefineTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = optionalTypeLoader.loadClass(SimpleOptionalType.class.getName());
            // The hybrid strategy cannot transform optional types.
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationRedefineTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationRedefineTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    private static class FooTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.method(ElementMatchers.named(AgentBuilderDefaultApplicationRedefineTest.FOO)).intercept(FixedValue.value(AgentBuilderDefaultApplicationRedefineTest.BAR));
        }
    }

    private static class BarTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.visit(Advice.to(AgentBuilderDefaultApplicationRedefineTest.BarTransformer.class).on(ElementMatchers.named(AgentBuilderDefaultApplicationRedefineTest.FOO)));
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false)
        String value) {
            value = AgentBuilderDefaultApplicationRedefineTest.BAR;
        }
    }
}

