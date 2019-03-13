package net.bytebuddy.dynamic.loading;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.ClassReflectionInjectionAvailableRule;
import net.bytebuddy.utility.RandomString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


public class ClassLoadingStrategyForBootstrapInjectionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule classInjectionAvailableRule = new ClassReflectionInjectionAvailableRule();

    private File file;

    @Test
    @AgentAttachmentRule.Enforce
    public void testBootstrapInjection() throws Exception {
        ClassLoadingStrategy<ClassLoader> bootstrapStrategy = new ClassLoadingStrategy.ForBootstrapInjection(ByteBuddyAgent.install(), file);
        String name = (ClassLoadingStrategyForBootstrapInjectionTest.FOO) + (RandomString.make());
        DynamicType dynamicType = new ByteBuddy().subclass(Object.class).name(name).make();
        Map<TypeDescription, Class<?>> loaded = bootstrapStrategy.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, Collections.singletonMap(dynamicType.getTypeDescription(), dynamicType.getBytes()));
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.get(dynamicType.getTypeDescription()).getName(), CoreMatchers.is(name));
        MatcherAssert.assertThat(loaded.get(dynamicType.getTypeDescription()).getClassLoader(), CoreMatchers.nullValue(ClassLoader.class));
    }

    @Test
    @AgentAttachmentRule.Enforce
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testClassLoaderInjection() throws Exception {
        ClassLoadingStrategy<ClassLoader> bootstrapStrategy = new ClassLoadingStrategy.ForBootstrapInjection(ByteBuddyAgent.install(), file);
        String name = (ClassLoadingStrategyForBootstrapInjectionTest.BAR) + (RandomString.make());
        ClassLoader classLoader = new URLClassLoader(new URL[0], null);
        DynamicType dynamicType = new ByteBuddy().subclass(Object.class).name(name).make();
        Map<TypeDescription, Class<?>> loaded = bootstrapStrategy.load(classLoader, Collections.singletonMap(dynamicType.getTypeDescription(), dynamicType.getBytes()));
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.get(dynamicType.getTypeDescription()).getName(), CoreMatchers.is(name));
        MatcherAssert.assertThat(loaded.get(dynamicType.getTypeDescription()).getClassLoader(), CoreMatchers.is(classLoader));
    }
}

