package net.bytebuddy.dynamic.loading;


import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.concurrent.Callable;
import junit.framework.TestCase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.RandomString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.AgentBased.of;
import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.PersistenceHandler.MANIFEST;
import static net.bytebuddy.dynamic.loading.ClassReloadingStrategy.Strategy.REDEFINITION;
import static net.bytebuddy.dynamic.loading.ClassReloadingStrategy.Strategy.RETRANSFORMATION;


public class ClassReloadingStrategyTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String LAMBDA_SAMPLE_FACTORY = "net.bytebuddy.test.precompiled.LambdaSampleFactory";

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testStrategyCreation() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(ClassReloadingStrategy.fromInstalledAgent(), CoreMatchers.notNullValue());
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testFromAgentClassReloadingStrategy() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassReloadingStrategyTest.Foo foo = new ClassReloadingStrategyTest.Foo();
        MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.fromInstalledAgent();
        new ByteBuddy().redefine(ClassReloadingStrategyTest.Foo.class).method(ElementMatchers.named(ClassReloadingStrategyTest.FOO)).intercept(FixedValue.value(ClassReloadingStrategyTest.BAR)).make().load(ClassReloadingStrategyTest.Foo.class.getClassLoader(), classReloadingStrategy);
        try {
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.BAR));
        } finally {
            classReloadingStrategy.reset(ClassReloadingStrategyTest.Foo.class);
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        }
    }

    // Wait for mechanism in sun.misc.Unsafe to define class.
    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @JavaVersionRule.Enforce(atMost = 10)
    public void testFromAgentClassWithAuxiliaryReloadingStrategy() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassReloadingStrategyTest.Foo foo = new ClassReloadingStrategyTest.Foo();
        MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.fromInstalledAgent();
        String randomName = (ClassReloadingStrategyTest.FOO) + (RandomString.make());
        new ByteBuddy().redefine(ClassReloadingStrategyTest.Foo.class).method(ElementMatchers.named(ClassReloadingStrategyTest.FOO)).intercept(FixedValue.value(ClassReloadingStrategyTest.BAR)).make().include(new ByteBuddy().subclass(Object.class).name(randomName).make()).load(ClassReloadingStrategyTest.Foo.class.getClassLoader(), classReloadingStrategy);
        try {
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.BAR));
        } finally {
            classReloadingStrategy.reset(ClassReloadingStrategyTest.Foo.class);
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        }
        MatcherAssert.assertThat(Class.forName(randomName), CoreMatchers.notNullValue(Class.class));
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testClassRedefinitionRenamingWithStackMapFrames() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.fromInstalledAgent();
        ClassReloadingStrategyTest.Bar bar = new ClassReloadingStrategyTest.Bar();
        new ByteBuddy().redefine(ClassReloadingStrategyTest.Qux.class).name(ClassReloadingStrategyTest.Bar.class.getName()).make().load(ClassReloadingStrategyTest.Bar.class.getClassLoader(), classReloadingStrategy);
        try {
            MatcherAssert.assertThat(bar.foo(), CoreMatchers.is(ClassReloadingStrategyTest.BAR));
        } finally {
            classReloadingStrategy.reset(ClassReloadingStrategyTest.Bar.class);
            MatcherAssert.assertThat(bar.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    public void testRedefinitionReloadingStrategy() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassReloadingStrategyTest.Foo foo = new ClassReloadingStrategyTest.Foo();
        MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.fromInstalledAgent(REDEFINITION);
        new ByteBuddy().redefine(ClassReloadingStrategyTest.Foo.class).method(ElementMatchers.named(ClassReloadingStrategyTest.FOO)).intercept(FixedValue.value(ClassReloadingStrategyTest.BAR)).make().load(ClassReloadingStrategyTest.Foo.class.getClassLoader(), classReloadingStrategy);
        try {
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.BAR));
        } finally {
            classReloadingStrategy.reset(ClassReloadingStrategyTest.Foo.class);
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testRetransformationReloadingStrategy() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassReloadingStrategyTest.Foo foo = new ClassReloadingStrategyTest.Foo();
        MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.fromInstalledAgent(RETRANSFORMATION);
        new ByteBuddy().redefine(ClassReloadingStrategyTest.Foo.class).method(ElementMatchers.named(ClassReloadingStrategyTest.FOO)).intercept(FixedValue.value(ClassReloadingStrategyTest.BAR)).make().load(ClassReloadingStrategyTest.Foo.class.getClassLoader(), classReloadingStrategy);
        try {
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.BAR));
        } finally {
            classReloadingStrategy.reset(ClassReloadingStrategyTest.Foo.class);
            MatcherAssert.assertThat(foo.foo(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        }
    }

    @Test
    public void testPreregisteredType() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        Mockito.when(instrumentation.isRedefineClassesSupported()).thenReturn(true);
        Mockito.when(instrumentation.getInitiatedClasses(classLoader)).thenReturn(new Class<?>[0]);
        ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.of(instrumentation).preregistered(Object.class);
        ArgumentCaptor<ClassDefinition> classDefinition = ArgumentCaptor.forClass(ClassDefinition.class);
        classReloadingStrategy.load(classLoader, Collections.singletonMap(TypeDescription.OBJECT, new byte[]{ 1, 2, 3 }));
        Mockito.verify(instrumentation).redefineClasses(classDefinition.capture());
        TestCase.assertEquals(Object.class, classDefinition.getValue().getDefinitionClass());
        MatcherAssert.assertThat(classDefinition.getValue().getDefinitionClassFile(), CoreMatchers.is(new byte[]{ 1, 2, 3 }));
    }

    @Test
    public void testRetransformationDiscovery() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        MatcherAssert.assertThat(ClassReloadingStrategy.of(instrumentation), CoreMatchers.notNullValue(ClassReloadingStrategy.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonCompatible() throws Exception {
        ClassReloadingStrategy.of(Mockito.mock(Instrumentation.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoRedefinition() throws Exception {
        new ClassReloadingStrategy(Mockito.mock(Instrumentation.class), REDEFINITION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoRetransformation() throws Exception {
        new ClassReloadingStrategy(Mockito.mock(Instrumentation.class), RETRANSFORMATION);
    }

    @Test
    public void testResetNotSupported() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        new ClassReloadingStrategy(instrumentation, RETRANSFORMATION).reset();
    }

    @Test
    public void testEngineSelfReport() throws Exception {
        MatcherAssert.assertThat(REDEFINITION.isRedefinition(), CoreMatchers.is(true));
        MatcherAssert.assertThat(RETRANSFORMATION.isRedefinition(), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(value = 8, atMost = 8)
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testAnonymousType() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(Class.forName(ClassReloadingStrategyTest.LAMBDA_SAMPLE_FACTORY)), MANIFEST);
        Instrumentation instrumentation = ByteBuddyAgent.install();
        Class<?> factory = classLoader.loadClass(ClassReloadingStrategyTest.LAMBDA_SAMPLE_FACTORY);
        @SuppressWarnings("unchecked")
        Callable<String> instance = ((Callable<String>) (factory.getDeclaredMethod("nonCapturing").invoke(factory.getDeclaredConstructor().newInstance())));
        // Anonymous types can only be reset to their original format, if a retransformation is applied.
        ClassReloadingStrategy classReloadingStrategy = new ClassReloadingStrategy(instrumentation, RETRANSFORMATION).preregistered(instance.getClass());
        ClassFileLocator classFileLocator = of(instrumentation, instance.getClass());
        try {
            MatcherAssert.assertThat(instance.call(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
            new ByteBuddy().redefine(instance.getClass(), classFileLocator).method(ElementMatchers.named("call")).intercept(FixedValue.value(ClassReloadingStrategyTest.BAR)).make().load(instance.getClass().getClassLoader(), classReloadingStrategy);
            MatcherAssert.assertThat(instance.call(), CoreMatchers.is(ClassReloadingStrategyTest.BAR));
        } finally {
            classReloadingStrategy.reset(classFileLocator, instance.getClass());
            MatcherAssert.assertThat(instance.call(), CoreMatchers.is(ClassReloadingStrategyTest.FOO));
        }
    }

    @Test
    public void testResetEmptyNoEffectImplicitLocator() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ClassReloadingStrategy.of(instrumentation).reset();
        Mockito.verify(instrumentation, Mockito.times(2)).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
    }

    @Test
    public void testResetEmptyNoEffect() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(instrumentation.isRetransformClassesSupported()).thenReturn(true);
        ClassReloadingStrategy.of(instrumentation).reset(classFileLocator);
        Mockito.verify(instrumentation, Mockito.times(2)).isRetransformClassesSupported();
        Mockito.verifyNoMoreInteractions(instrumentation);
        Mockito.verifyZeroInteractions(classFileLocator);
    }

    @Test
    public void testTransformerHandlesNullValue() throws Exception {
        MatcherAssert.assertThat(new ClassReloadingStrategy.Strategy.ClassRedefinitionTransformer(Collections.<Class<?>, ClassDefinition>emptyMap()).transform(Mockito.mock(ClassLoader.class), ClassReloadingStrategyTest.FOO, Object.class, Mockito.mock(ProtectionDomain.class), new byte[0]), CoreMatchers.nullValue(byte[].class));
    }

    @SuppressWarnings("unused")
    public static class Foo {
        private long a1;

        private long a2;

        private long a3;

        private long a4;

        private long a5;

        private long a6;

        private long a7;

        private long a8;

        private long a9;

        private long a10;

        private long b2;

        private long b3;

        private long b4;

        private long b5;

        private long b6;

        private long b7;

        private long b8;

        private long b9;

        private long b10;

        private long c1;

        private long c2;

        private long c3;

        private long c4;

        private long c5;

        private long c6;

        private long c7;

        private long c8;

        private long c9;

        private long c10;

        private long d1;

        private long d2;

        private long d3;

        private long d4;

        private long d5;

        private long d6;

        private long d7;

        private long d8;

        private long d9;

        /**
         * Padding to increase class file size.
         */
        private long d10;

        public String foo() {
            return ClassReloadingStrategyTest.FOO;
        }
    }

    public static class Bar {
        @SuppressWarnings("unused")
        public String foo() {
            ClassReloadingStrategyTest.Bar bar = new ClassReloadingStrategyTest.Bar();
            return (Math.random()) < 0 ? ClassReloadingStrategyTest.FOO : ClassReloadingStrategyTest.FOO;
        }
    }

    public static class Qux {
        @SuppressWarnings("unused")
        public String foo() {
            ClassReloadingStrategyTest.Qux qux = new ClassReloadingStrategyTest.Qux();
            return (Math.random()) < 0 ? ClassReloadingStrategyTest.BAR : ClassReloadingStrategyTest.BAR;
        }
    }
}

