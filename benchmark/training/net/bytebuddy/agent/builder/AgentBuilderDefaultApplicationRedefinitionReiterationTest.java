package net.bytebuddy.agent.builder;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


public class AgentBuilderDefaultApplicationRedefinitionReiterationTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private ClassLoader classLoader;

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testAdviceWithoutLoadedClasses() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        ClassFileTransformer classFileTransformer = installInstrumentation();
        try {
            assertAdvice();
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testAdviceWithOneLoadedClass() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        classLoader.loadClass(AgentBuilderDefaultApplicationRedefinitionReiterationTest.Foo.class.getName());
        ClassFileTransformer classFileTransformer = installInstrumentation();
        try {
            assertAdvice();
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    @Test
    @AgentAttachmentRule.Enforce(retransformsClasses = true)
    public void testAdviceWithTwoLoadedClasses() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
        classLoader.loadClass(AgentBuilderDefaultApplicationRedefinitionReiterationTest.Foo.class.getName());
        classLoader.loadClass(AgentBuilderDefaultApplicationRedefinitionReiterationTest.Bar.class.getName());
        ClassFileTransformer classFileTransformer = installInstrumentation();
        try {
            assertAdvice();
        } finally {
            ByteBuddyAgent.getInstrumentation().removeTransformer(classFileTransformer);
        }
    }

    public static class Foo {
        @SuppressWarnings("unused")
        public AgentBuilderDefaultApplicationRedefinitionReiterationTest.Bar createBar() throws Exception {
            return new AgentBuilderDefaultApplicationRedefinitionReiterationTest.Bar();
        }
    }

    public static class Bar {
        private String x = AgentBuilderDefaultApplicationRedefinitionReiterationTest.QUX;

        public void append(String x) {
            this.x += x;
        }

        public String toString() {
            return x;
        }
    }

    private static class FooAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        AgentBuilderDefaultApplicationRedefinitionReiterationTest.Bar value) {
            value.append(AgentBuilderDefaultApplicationRedefinitionReiterationTest.FOO);
        }
    }

    private static class BarAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false)
        String value) {
            value += AgentBuilderDefaultApplicationRedefinitionReiterationTest.BAR;
        }
    }
}

