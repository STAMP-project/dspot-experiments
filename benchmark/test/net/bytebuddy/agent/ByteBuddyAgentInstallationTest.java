package net.bytebuddy.agent;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


public class ByteBuddyAgentInstallationTest {
    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @AgentAttachmentRule.Enforce
    public void testAgentInstallation() throws Exception {
        MatcherAssert.assertThat(ByteBuddyAgent.install(), CoreMatchers.instanceOf(Instrumentation.class));
    }

    // To avoid unsupported duplicate binding of native library to two class loaders.
    @Test
    @AgentAttachmentRule.Enforce
    @JavaVersionRule.Enforce(9)
    public void testAgentInstallationOtherClassLoader() throws Exception {
        ByteBuddyAgentInstallationTest.resetField();
        MatcherAssert.assertThat(new ClassLoader(null) {
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                InputStream in = getResourceAsStream(((name.replace('.', '/')) + ".class"));
                if (in == null) {
                    throw new ClassNotFoundException(name);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                try {
                    while ((length = in.read(buffer)) != (-1)) {
                        out.write(buffer, 0, length);
                    } 
                } catch (IOException exception) {
                    throw new AssertionError(exception);
                }
                byte[] binaryRepresentation = out.toByteArray();
                return defineClass(name, binaryRepresentation, 0, binaryRepresentation.length);
            }

            public InputStream getResourceAsStream(String name) {
                return ByteBuddyAgentInstallationTest.class.getClassLoader().getResourceAsStream(name);
            }
        }.loadClass(ByteBuddyAgent.class.getName()).getDeclaredMethod("install").invoke(null), CoreMatchers.instanceOf(Instrumentation.class));
    }

    // To avoid unsupported duplicate binding of native library to two class loaders.
    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(9)
    public void testNoInstrumentation() throws Exception {
        ByteBuddyAgentInstallationTest.resetField();
        ByteBuddyAgent.getInstrumentation();
    }
}

