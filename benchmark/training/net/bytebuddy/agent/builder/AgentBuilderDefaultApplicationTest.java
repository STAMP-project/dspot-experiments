package net.bytebuddy.agent.builder;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.packaging.SimpleType;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.ClassReflectionInjectionAvailableRule;
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

import static net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy.ENABLED;
import static net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy.release;
import static net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy.ForClassLoader.STRONG;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.BatchAllocator.ForFixedSize.ofSize;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.REDEFINITION;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE;


@RunWith(Parameterized.class)
public class AgentBuilderDefaultApplicationTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String LAMBDA_SAMPLE_FACTORY = "net.bytebuddy.test.precompiled.LambdaSampleFactory";

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    @Rule
    public MethodRule classUnsafeInjectionAvailableRule = new ClassReflectionInjectionAvailableRule();

    private ClassLoader classLoader;

    private final AgentBuilder.PoolStrategy poolStrategy;

    public AgentBuilderDefaultApplicationTest(AgentBuilder.PoolStrategy poolStrategy) {
        this.poolStrategy = poolStrategy;
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testAgentWithoutSelfInitialization() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(INSTANCE).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Foo.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testAgentSelfInitialization() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Bar.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.BarTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Bar.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testAgentSelfInitializationAuxiliaryTypeEager() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Qux.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.QuxTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Qux.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) ((AgentBuilderDefaultApplicationTest.FOO) + (AgentBuilderDefaultApplicationTest.BAR)))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testAgentSelfInitializationAuxiliaryTypeLazy() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.QuxBaz.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.QuxBazTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.QuxBaz.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) ((AgentBuilderDefaultApplicationTest.FOO) + (AgentBuilderDefaultApplicationTest.BAR)))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testAgentWithoutSelfInitializationWithNativeMethodPrefix() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).enableNativeMethodPrefix(AgentBuilderDefaultApplicationTest.QUX).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Baz.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Baz.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
            MatcherAssert.assertThat(type.getDeclaredMethod(((AgentBuilderDefaultApplicationTest.QUX) + (AgentBuilderDefaultApplicationTest.FOO))), CoreMatchers.notNullValue(Method.class));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testRedefinition() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(REDEFINITION).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testRedefinitionWithReset() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(REDEFINITION).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            MatcherAssert.assertThat(classFileTransformer.reset(ByteBuddyAgent.getInstrumentation(), REDEFINITION), CoreMatchers.is(true));
        }
        Class<?> type = classLoader.loadClass(SimpleType.class.getName());
        MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.FOO))));
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testEmptyRedefinition() throws Exception {
        ByteBuddyAgent.getInstrumentation().removeTransformer(new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.any()).disableClassFormatChanges().with(REDEFINE).with(REDEFINITION).installOnByteBuddyAgent());
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testChunkedRedefinition() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(REDEFINITION).with(ofSize(1)).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testEmptyChunkedRedefinition() throws Exception {
        ByteBuddyAgent.getInstrumentation().removeTransformer(new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.any()).disableClassFormatChanges().with(REDEFINE).with(REDEFINITION).with(ofSize(1)).installOnByteBuddyAgent());
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    @IntegrationRule.Enforce
    public void testRedefinitionWithExplicitTypes() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(REDEFINITION).redefineOnly(classLoader.loadClass(SimpleType.class.getName())).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
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
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(RETRANSFORMATION).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testRetransformationWithReset() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ResettableClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(RETRANSFORMATION).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            MatcherAssert.assertThat(classFileTransformer.reset(ByteBuddyAgent.getInstrumentation(), RETRANSFORMATION), CoreMatchers.is(true));
        }
        Class<?> type = classLoader.loadClass(SimpleType.class.getName());
        MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.FOO))));
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testEmptyRetransformation() throws Exception {
        ByteBuddyAgent.getInstrumentation().removeTransformer(new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.any()).disableClassFormatChanges().with(REDEFINE).with(RETRANSFORMATION).installOnByteBuddyAgent());
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testChunkedRetransformation() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(RETRANSFORMATION).with(ofSize(1)).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testChunkedEmptyRetransformation() throws Exception {
        ByteBuddyAgent.getInstrumentation().removeTransformer(new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.any()).disableClassFormatChanges().with(REDEFINE).with(RETRANSFORMATION).with(ofSize(1)).installOnByteBuddyAgent());
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @IntegrationRule.Enforce
    public void testRetransformationWithExplicitTypes() throws Exception {
        // A redefinition reflects on loaded types which are eagerly validated types (Java 7- for redefinition).
        // This causes type equality for outer/inner classes to fail which is why an external class is used.
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(classLoader.loadClass(SimpleType.class.getName()).getName(), CoreMatchers.is(SimpleType.class.getName()));// ensure that class is loaded

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).disableClassFormatChanges().with(REDEFINE).with(RETRANSFORMATION).redefineOnly(classLoader.loadClass(SimpleType.class.getName())).type(ElementMatchers.is(SimpleType.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.FooTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(SimpleType.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testChainedAgent() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        AgentBuilder agentBuilder = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Qux.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.QuxTransformer());
        ClassFileTransformer firstTransformer = agentBuilder.installOnByteBuddyAgent();
        ClassFileTransformer secondTransformer = agentBuilder.installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Qux.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (((AgentBuilderDefaultApplicationTest.FOO) + (AgentBuilderDefaultApplicationTest.BAR)) + (AgentBuilderDefaultApplicationTest.BAR)))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(firstTransformer);
            ByteBuddyAgent.getInstrumentation().removeTransformer(secondTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testSignatureTypesAreAvailableAfterLoad() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.ConstructorTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Foo.class.getName());
            MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(2));
            MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testDecoration() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.QuxAdviceTransformer()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.BarAdviceTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Foo.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (((AgentBuilderDefaultApplicationTest.FOO) + (AgentBuilderDefaultApplicationTest.BAR)) + (AgentBuilderDefaultApplicationTest.QUX)))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testDecorationFallThrough() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.QuxAdviceTransformer()).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.BarAdviceTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Foo.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (((AgentBuilderDefaultApplicationTest.FOO) + (AgentBuilderDefaultApplicationTest.BAR)) + (AgentBuilderDefaultApplicationTest.QUX)))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testDecorationBlocked() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = asTerminalTransformation().type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilderDefaultApplicationTest.BarAdviceTransformer()).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Foo.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) ((AgentBuilderDefaultApplicationTest.FOO) + (AgentBuilderDefaultApplicationTest.QUX)))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testNonCapturingLambda() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Callable.class)).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("call")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            @SuppressWarnings("unchecked")
            Callable<String> instance = ((Callable<String>) (sampleFactory.getDeclaredMethod("nonCapturing").invoke(sampleFactory.getDeclaredConstructor().newInstance())));
            MatcherAssert.assertThat(instance.call(), CoreMatchers.is(AgentBuilderDefaultApplicationTest.BAR));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testNonCapturingLambdaIsConstant() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Callable.class)).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("call")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            MatcherAssert.assertThat(sampleFactory.getDeclaredMethod("nonCapturing").invoke(sampleFactory.getDeclaredConstructor().newInstance()), CoreMatchers.sameInstance(sampleFactory.getDeclaredMethod("nonCapturing").invoke(sampleFactory.getDeclaredConstructor().newInstance())));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testLambdaFactoryIsReset() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).installOn(ByteBuddyAgent.getInstrumentation());
        ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
        @SuppressWarnings("unchecked")
        Callable<String> instance = ((Callable<String>) (sampleFactory.getDeclaredMethod("nonCapturing").invoke(sampleFactory.getDeclaredConstructor().newInstance())));
        MatcherAssert.assertThat(instance.call(), CoreMatchers.is(AgentBuilderDefaultApplicationTest.FOO));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testArgumentCapturingLambda() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Callable.class)).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("call")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            @SuppressWarnings("unchecked")
            Callable<String> instance = ((Callable<String>) (sampleFactory.getDeclaredMethod("argumentCapturing", String.class).invoke(sampleFactory.getDeclaredConstructor().newInstance(), AgentBuilderDefaultApplicationTest.FOO)));
            MatcherAssert.assertThat(instance.call(), CoreMatchers.is(AgentBuilderDefaultApplicationTest.BAR));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testArgumentCapturingLambdaIsNotConstant() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Callable.class)).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("call")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            MatcherAssert.assertThat(sampleFactory.getDeclaredMethod("argumentCapturing", String.class).invoke(sampleFactory.getDeclaredConstructor().newInstance(), AgentBuilderDefaultApplicationTest.FOO), CoreMatchers.not(CoreMatchers.sameInstance(sampleFactory.getDeclaredMethod("argumentCapturing", String.class).invoke(sampleFactory.getDeclaredConstructor().newInstance(), AgentBuilderDefaultApplicationTest.FOO))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testInstanceCapturingLambda() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Callable.class)).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("call")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            @SuppressWarnings("unchecked")
            Callable<String> instance = ((Callable<String>) (sampleFactory.getDeclaredMethod("instanceCapturing").invoke(sampleFactory.getDeclaredConstructor().newInstance())));
            MatcherAssert.assertThat(instance.call(), CoreMatchers.is(AgentBuilderDefaultApplicationTest.BAR));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testNonCapturingLambdaWithArguments() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Class.forName("java.util.function.Function"))).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("apply")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            Object instance = sampleFactory.getDeclaredMethod("nonCapturingWithArguments").invoke(sampleFactory.getDeclaredConstructor().newInstance());
            MatcherAssert.assertThat(instance.getClass().getMethod("apply", Object.class).invoke(instance, AgentBuilderDefaultApplicationTest.FOO), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testCapturingLambdaWithArguments() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Class.forName("java.util.function.Function"))).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("apply")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            Object instance = sampleFactory.getDeclaredMethod("capturingWithArguments", String.class).invoke(sampleFactory.getDeclaredConstructor().newInstance(), AgentBuilderDefaultApplicationTest.FOO);
            MatcherAssert.assertThat(instance.getClass().getMethod("apply", Object.class).invoke(instance, AgentBuilderDefaultApplicationTest.FOO), CoreMatchers.is(((Object) (AgentBuilderDefaultApplicationTest.BAR))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testSerializableLambda() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            @SuppressWarnings("unchecked")
            Callable<String> instance = ((Callable<String>) (sampleFactory.getDeclaredMethod("serializable", String.class).invoke(sampleFactory.getDeclaredConstructor().newInstance(), AgentBuilderDefaultApplicationTest.FOO)));
            MatcherAssert.assertThat(instance.call(), CoreMatchers.is(AgentBuilderDefaultApplicationTest.FOO));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(instance);
            objectOutputStream.close();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
            @SuppressWarnings("unchecked")
            Callable<String> deserialized = ((Callable<String>) (objectInputStream.readObject()));
            MatcherAssert.assertThat(deserialized.call(), CoreMatchers.is(AgentBuilderDefaultApplicationTest.FOO));
            objectInputStream.close();
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testReturnTypeTransformingLambda() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Callable.class)).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("call")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            Runnable instance = ((Runnable) (sampleFactory.getDeclaredMethod("returnTypeTransforming").invoke(sampleFactory.getDeclaredConstructor().newInstance())));
            instance.run();
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @AgentAttachmentRule.Enforce
    @IntegrationRule.Enforce
    public void testInstanceReturningLambda() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassLoader classLoader = lambdaSamples();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(ENABLED).type(ElementMatchers.isSubTypeOf(Callable.class)).transform(new AgentBuilderDefaultApplicationTest.SingleMethodReplacer("call")).installOn(ByteBuddyAgent.getInstrumentation());
        try {
            Class<?> sampleFactory = classLoader.loadClass(AgentBuilderDefaultApplicationTest.LAMBDA_SAMPLE_FACTORY);
            Callable<?> instance = ((Callable<?>) (sampleFactory.getDeclaredMethod("instanceReturning").invoke(sampleFactory.getDeclaredConstructor().newInstance())));
            MatcherAssert.assertThat(instance.call(), CoreMatchers.notNullValue(Object.class));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
            release(classFileTransformer, ByteBuddyAgent.getInstrumentation());
        }
    }

    @Test
    @IntegrationRule.Enforce
    public void testAdviceTransformer() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default().with(poolStrategy).ignore(ElementMatchers.none()).with(INSTANCE).type(ElementMatchers.is(AgentBuilderDefaultApplicationTest.Foo.class), ElementMatchers.is(classLoader)).transform(new AgentBuilder.Transformer.ForAdvice().with(poolStrategy).with(STRONG).include(AgentBuilderDefaultApplicationTest.BarAdvice.class.getClassLoader()).with(Assigner.DEFAULT).withExceptionHandler(new Advice.ExceptionHandler.Simple(Removal.SINGLE)).advice(ElementMatchers.named(AgentBuilderDefaultApplicationTest.FOO), AgentBuilderDefaultApplicationTest.BarAdvice.class.getName())).installOnByteBuddyAgent();
        try {
            Class<?> type = classLoader.loadClass(AgentBuilderDefaultApplicationTest.Foo.class.getName());
            MatcherAssert.assertThat(type.getDeclaredMethod(AgentBuilderDefaultApplicationTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) ((AgentBuilderDefaultApplicationTest.FOO) + (AgentBuilderDefaultApplicationTest.BAR)))));
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    private static class FooTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.method(ElementMatchers.named(AgentBuilderDefaultApplicationTest.FOO)).intercept(FixedValue.value(AgentBuilderDefaultApplicationTest.BAR));
        }
    }

    public static class Foo {
        public String foo() {
            return AgentBuilderDefaultApplicationTest.FOO;
        }
    }

    public static class Baz {
        public String foo() {
            return AgentBuilderDefaultApplicationTest.FOO;
        }
    }

    public static class BarTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            try {
                return builder.method(ElementMatchers.named(AgentBuilderDefaultApplicationTest.FOO)).intercept(MethodDelegation.to(new AgentBuilderDefaultApplicationTest.BarTransformer.Interceptor()));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
        }

        public static class Interceptor {
            public String intercept() {
                return AgentBuilderDefaultApplicationTest.BAR;
            }
        }
    }

    public static class Bar {
        public String foo() {
            return AgentBuilderDefaultApplicationTest.FOO;
        }
    }

    public static class QuxTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            try {
                return builder.method(ElementMatchers.named(AgentBuilderDefaultApplicationTest.FOO)).intercept(MethodDelegation.to(new AgentBuilderDefaultApplicationTest.QuxTransformer.Interceptor()));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
        }

        public static class Interceptor {
            public String intercept(@SuperCall
            Callable<String> zuper) throws Exception {
                return (zuper.call()) + (AgentBuilderDefaultApplicationTest.BAR);
            }
        }
    }

    public static class Qux {
        public String foo() {
            return AgentBuilderDefaultApplicationTest.FOO;
        }
    }

    public static class QuxBazTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            try {
                return builder.method(ElementMatchers.named(AgentBuilderDefaultApplicationTest.FOO)).intercept(MethodDelegation.to(new AgentBuilderDefaultApplicationTest.QuxBazTransformer.Interceptor()));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
        }

        public static class Interceptor {
            // Interceptor cannot reference QuxBaz as the system class loader type does not equal the child-first type
            public String intercept(@Super(proxyType = TargetType.class)
            Object zuper) throws Exception {
                return (zuper.getClass().getClassLoader().loadClass(AgentBuilderDefaultApplicationTest.QuxBaz.class.getName()).getDeclaredMethod("foo").invoke(zuper)) + (AgentBuilderDefaultApplicationTest.BAR);
            }
        }
    }

    public static class QuxBaz {
        public String foo() {
            return AgentBuilderDefaultApplicationTest.FOO;
        }
    }

    public static class ConstructorTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.constructor(ElementMatchers.any()).intercept(SuperMethodCall.INSTANCE);
        }
    }

    private static class SingleMethodReplacer implements AgentBuilder.Transformer {
        private final String methodName;

        public SingleMethodReplacer(String methodName) {
            this.methodName = methodName;
        }

        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.method(ElementMatchers.named(methodName)).intercept(FixedValue.value(AgentBuilderDefaultApplicationTest.BAR));
        }
    }

    public static class BarAdviceTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.visit(Advice.to(AgentBuilderDefaultApplicationTest.BarAdvice.class).on(ElementMatchers.named(AgentBuilderDefaultApplicationTest.FOO)));
        }
    }

    public static class QuxAdviceTransformer implements AgentBuilder.Transformer {
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.visit(Advice.to(AgentBuilderDefaultApplicationTest.QuxAdvice.class).on(ElementMatchers.named(AgentBuilderDefaultApplicationTest.FOO)));
        }
    }

    private static class BarAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false)
        String value) {
            value += AgentBuilderDefaultApplicationTest.BAR;
        }
    }

    private static class QuxAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false)
        String value) {
            value += AgentBuilderDefaultApplicationTest.QUX;
        }
    }
}

