package net.bytebuddy.dynamic.loading;


import java.io.File;
import java.util.Collections;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.utility.RandomString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target.BOOTSTRAP;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target.SYSTEM;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.of;


public class ClassInjectorUsingInstrumentationTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    private File folder;

    @Test
    @AgentAttachmentRule.Enforce
    public void testBootstrapInjection() throws Exception {
        ClassInjector classInjector = of(folder, BOOTSTRAP, ByteBuddyAgent.install());
        String name = (ClassInjectorUsingInstrumentationTest.FOO) + (RandomString.make());
        DynamicType dynamicType = new ByteBuddy().subclass(Object.class).name(name).make();
        Map<TypeDescription, Class<?>> types = classInjector.inject(Collections.singletonMap(dynamicType.getTypeDescription(), dynamicType.getBytes()));
        MatcherAssert.assertThat(types.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(types.get(dynamicType.getTypeDescription()).getName(), CoreMatchers.is(name));
        MatcherAssert.assertThat(types.get(dynamicType.getTypeDescription()).getClassLoader(), CoreMatchers.nullValue(ClassLoader.class));
    }

    @Test
    @AgentAttachmentRule.Enforce
    public void testSystemInjection() throws Exception {
        ClassInjector classInjector = of(folder, SYSTEM, ByteBuddyAgent.install());
        String name = (ClassInjectorUsingInstrumentationTest.BAR) + (RandomString.make());
        DynamicType dynamicType = new ByteBuddy().subclass(Object.class).name(name).make();
        Map<TypeDescription, Class<?>> types = classInjector.inject(Collections.singletonMap(dynamicType.getTypeDescription(), dynamicType.getBytes()));
        MatcherAssert.assertThat(types.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(types.get(dynamicType.getTypeDescription()).getName(), CoreMatchers.is(name));
        MatcherAssert.assertThat(types.get(dynamicType.getTypeDescription()).getClassLoader(), CoreMatchers.is(ClassLoader.getSystemClassLoader()));
    }

    @Test
    @AgentAttachmentRule.Enforce
    public void testAvailable() {
        MatcherAssert.assertThat(isAvailable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(isAlive(), CoreMatchers.is(true));
    }
}

