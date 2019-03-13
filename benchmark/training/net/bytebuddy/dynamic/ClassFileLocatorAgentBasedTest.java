package net.bytebuddy.dynamic;


import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.ClassFileLocator.AgentBased.ClassLoadingDelegate.Explicit.of;
import static net.bytebuddy.dynamic.ClassFileLocator.AgentBased.fromInstalledAgent;


public class ClassFileLocatorAgentBasedTest {
    private static final String FOO = "foo";

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    public void testStrategyCreation() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        MatcherAssert.assertThat(ClassReloadingStrategy.fromInstalledAgent(), CoreMatchers.notNullValue());
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testExtraction() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileLocator classFileLocator = fromInstalledAgent(getClass().getClassLoader());
        ClassFileLocator.Resolution resolution = classFileLocator.locate(ClassFileLocatorAgentBasedTest.Foo.class.getName());
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.notNullValue(byte[].class));
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    @JavaVersionRule.Enforce(value = 8, atMost = 8)
    public void testExtractionOfInflatedMethodAccessor() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        Method bar = ClassFileLocatorAgentBasedTest.Foo.class.getDeclaredMethod("bar");
        for (int i = 0; i < 20; i++) {
            bar.invoke(new ClassFileLocatorAgentBasedTest.Foo());
        }
        Field field = Method.class.getDeclaredField("methodAccessor");
        field.setAccessible(true);
        Object methodAccessor = field.get(bar);
        Field delegate = methodAccessor.getClass().getDeclaredField("delegate");
        delegate.setAccessible(true);
        Class<?> delegateClass = delegate.get(methodAccessor).getClass();
        ClassFileLocator classFileLocator = fromInstalledAgent(delegateClass.getClassLoader());
        ClassFileLocator.Resolution resolution = classFileLocator.locate(delegateClass.getName());
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.notNullValue(byte[].class));
    }

    @Test
    public void testExplicitLookupBootstrapClassLoader() throws Exception {
        ClassFileLocator.AgentBased.ClassLoadingDelegate classLoadingDelegate = of(Object.class);
        MatcherAssert.assertThat(classLoadingDelegate.getClassLoader(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
        MatcherAssert.assertThat(classLoadingDelegate.locate(Object.class.getName()), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(classLoadingDelegate.locate(String.class.getName()), CoreMatchers.<Class<?>>is(String.class));
    }

    @Test
    public void testExplicitLookup() throws Exception {
        ClassFileLocator.AgentBased.ClassLoadingDelegate classLoadingDelegate = of(ClassFileLocatorAgentBasedTest.Foo.class);
        MatcherAssert.assertThat(classLoadingDelegate.getClassLoader(), CoreMatchers.is(ClassFileLocatorAgentBasedTest.Foo.class.getClassLoader()));
        MatcherAssert.assertThat(classLoadingDelegate.locate(ClassFileLocatorAgentBasedTest.Foo.class.getName()), CoreMatchers.<Class<?>>is(ClassFileLocatorAgentBasedTest.Foo.class));
        MatcherAssert.assertThat(classLoadingDelegate.locate(Object.class.getName()), CoreMatchers.<Class<?>>is(Object.class));
    }

    @Test
    public void testExtractingTransformerHandlesNullValue() throws Exception {
        MatcherAssert.assertThat(new ClassFileLocator.AgentBased.ExtractionClassFileTransformer(Mockito.mock(ClassLoader.class), ClassFileLocatorAgentBasedTest.FOO).transform(Mockito.mock(ClassLoader.class), ClassFileLocatorAgentBasedTest.FOO, Object.class, Mockito.mock(ProtectionDomain.class), new byte[0]), CoreMatchers.nullValue(byte[].class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonCompatible() throws Exception {
        new ClassFileLocator.AgentBased(Mockito.mock(Instrumentation.class), getClass().getClassLoader());
    }

    private static class Foo {
        int foo;

        int bar;

        void bar() {
        }
    }
}

