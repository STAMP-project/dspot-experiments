/**
 * Copyright ? 2013-2014 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.maven;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ProcessClassesMojoTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Log log = Mockito.mock(Log.class);

    private final ProcessClassesMojoTest.FakeToolchainManager toolchainManager = new ProcessClassesMojoTest.FakeToolchainManager();

    private final ProcessMainClassesMojo mojo = new ProcessMainClassesMojo();

    @Test
    public void error_message_lists_the_accepted_targets() throws MojoExecutionException {
        mojo.target = "1.0";
        thrown.expect(MojoExecutionException.class);
        thrown.expectMessage("1.5, 1.6, 1.7, 1.8");
        mojo.execute();
    }

    @Test
    public void java_command_defaults_to_current_jvm() {
        MatcherAssert.assertThat(mojo.getJavaCommand(), Matchers.is(new File(System.getProperty("java.home"), "bin/java").getAbsolutePath()));
    }

    @Test
    public void java_command_from_toolchain_overrides_the_current_jvm() {
        toolchainManager.setJdkToolChain(new ProcessClassesMojoTest.FakeJavaToolChain("jdk-from-toolchain"));
        MatcherAssert.assertThat(mojo.getJavaCommand(), Matchers.is("jdk-from-toolchain/bin/java"));
        Mockito.verify(log).info("Toolchain in retrolambda-maven-plugin: JDK[jdk-from-toolchain]");
    }

    @Test
    public void java_command_from_local_configuration_overrides_the_toolchain() {
        toolchainManager.setJdkToolChain(new ProcessClassesMojoTest.FakeJavaToolChain("jdk-from-toolchain"));
        mojo.java8home = new File("jdk-from-local-configuration");
        MatcherAssert.assertThat(mojo.getJavaCommand().replace('\\', '/'), Matchers.is("jdk-from-local-configuration/bin/java"));
        Mockito.verify(log).warn("Toolchains are ignored, 'java8home' parameter is set to jdk-from-local-configuration");
    }

    private static class FakeToolchainManager implements ToolchainManager {
        private final Map<String, Toolchain> toolChainsByType = new HashMap<String, Toolchain>();

        @Override
        public Toolchain getToolchainFromBuildContext(String type, MavenSession context) {
            return toolChainsByType.get(type);
        }

        public void setJdkToolChain(JavaToolChain toolChain) {
            toolChainsByType.put("jdk", toolChain);
        }
    }

    private static class FakeJavaToolChain extends DefaultJavaToolChain {
        public FakeJavaToolChain(String javaHome) {
            super(null, null);
            setJavaHome(javaHome);
        }

        @Override
        public String findTool(String toolName) {
            return ((getJavaHome()) + "/bin/") + toolName;
        }
    }
}

