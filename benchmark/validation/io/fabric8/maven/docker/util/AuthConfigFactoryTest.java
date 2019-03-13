package io.fabric8.maven.docker.util;


import AuthConfigFactory.DOCKER_LOGIN_DEFAULT_REGISTRY;
import com.google.gson.Gson;
import io.fabric8.maven.docker.access.AuthConfig;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mockit.Mock;
import mockit.Mocked;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;


/**
 *
 *
 * @author roland
 * @since 29.07.14
 */
public class AuthConfigFactoryTest {
    public static final String ECR_NAME = "123456789012.dkr.ecr.bla.amazonaws.com";

    @Mocked
    Settings settings;

    @Mocked
    private Logger log;

    private AuthConfigFactory factory;

    private boolean isPush = true;

    private Gson gson;

    public static final class MockSecDispatcher implements SecDispatcher {
        @Mock
        public String decrypt(String password) {
            return password;
        }
    }

    @Mocked
    PlexusContainer container;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testEmpty() throws Exception {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                Assert.assertNull(factory.createAuthConfig(isPush, false, null, settings, null, "blubberbla:1611"));
            }
        });
    }

    @Test
    public void testSystemProperty() throws Exception {
        System.setProperty("docker.push.username", "roland");
        System.setProperty("docker.push.password", "secret");
        System.setProperty("docker.push.email", "roland@jolokia.org");
        try {
            AuthConfig config = factory.createAuthConfig(true, false, null, settings, null, null);
            verifyAuthConfig(config, "roland", "secret", "roland@jolokia.org");
        } finally {
            System.clearProperty("docker.push.username");
            System.clearProperty("docker.push.password");
            System.clearProperty("docker.push.email");
        }
    }

    @Test
    public void testDockerAuthLogin() throws Exception {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                checkDockerAuthLogin(homeDir, DOCKER_LOGIN_DEFAULT_REGISTRY, null);
                checkDockerAuthLogin(homeDir, "localhost:5000", "localhost:5000");
                checkDockerAuthLogin(homeDir, "https://localhost:5000", "localhost:5000");
            }
        });
    }

    @Test
    public void testDockerLoginNoConfig() throws IOException, MojoExecutionException {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File dir) throws IOException, MojoExecutionException {
                AuthConfig config = factory.createAuthConfig(isPush, false, null, settings, "roland", null);
                Assert.assertNull(config);
            }
        });
    }

    @Test
    public void testDockerLoginFallsBackToAuthWhenCredentialHelperDoesNotMatchDomain() throws IOException, MojoExecutionException {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                writeDockerConfigJson(createDockerConfig(homeDir), null, Collections.singletonMap("registry1", "credHelper1-does-not-exist"));
                AuthConfig config = factory.createAuthConfig(isPush, false, null, settings, "roland", "localhost:5000");
                verifyAuthConfig(config, "roland", "secret", "roland@jolokia.org");
            }
        });
    }

    @Test
    public void testDockerLoginNoAuthConfigFoundWhenCredentialHelperDoesNotMatchDomainOrAuth() throws IOException, MojoExecutionException {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                writeDockerConfigJson(createDockerConfig(homeDir), null, Collections.singletonMap("registry1", "credHelper1-does-not-exist"));
                AuthConfig config = factory.createAuthConfig(isPush, false, null, settings, "roland", "does-not-exist-either:5000");
                Assert.assertNull(config);
            }
        });
    }

    @Test
    public void testDockerLoginSelectCredentialHelper() throws IOException, MojoExecutionException {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                writeDockerConfigJson(createDockerConfig(homeDir), "credsStore-does-not-exist", Collections.singletonMap("registry1", "credHelper1-does-not-exist"));
                expectedException.expect(MojoExecutionException.class);
                expectedException.expectCause(Matchers.<Throwable>allOf(Matchers.instanceOf(IOException.class), Matchers.hasProperty("message", Matchers.startsWith("Failed to start 'docker-credential-credHelper1-does-not-exist version'"))));
                factory.createAuthConfig(isPush, false, null, settings, "roland", "registry1");
            }
        });
    }

    @Test
    public void testDockerLoginSelectCredentialsStore() throws IOException, MojoExecutionException {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                writeDockerConfigJson(createDockerConfig(homeDir), "credsStore-does-not-exist", Collections.singletonMap("registry1", "credHelper1-does-not-exist"));
                expectedException.expect(MojoExecutionException.class);
                expectedException.expectCause(Matchers.<Throwable>allOf(Matchers.instanceOf(IOException.class), Matchers.hasProperty("message", Matchers.startsWith("Failed to start 'docker-credential-credsStore-does-not-exist version'"))));
                factory.createAuthConfig(isPush, false, null, settings, "roland", null);
            }
        });
    }

    @Test
    public void testDockerLoginDefaultToCredentialsStore() throws IOException, MojoExecutionException {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                writeDockerConfigJson(createDockerConfig(homeDir), "credsStore-does-not-exist", Collections.singletonMap("registry1", "credHelper1-does-not-exist"));
                expectedException.expect(MojoExecutionException.class);
                expectedException.expectCause(Matchers.<Throwable>allOf(Matchers.instanceOf(IOException.class), Matchers.hasProperty("message", Matchers.startsWith("Failed to start 'docker-credential-credsStore-does-not-exist version'"))));
                factory.createAuthConfig(isPush, false, null, settings, "roland", "registry2");
            }
        });
    }

    interface HomeDirExecutor {
        void exec(File dir) throws IOException, MojoExecutionException;
    }

    @Test
    public void testOpenShiftConfigFromPluginConfig() throws Exception {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                createOpenShiftConfig(homeDir, "openshift_simple_config.yaml");
                Map<String, String> authConfigMap = new HashMap<>();
                authConfigMap.put("useOpenShiftAuth", "true");
                AuthConfig config = factory.createAuthConfig(isPush, false, authConfigMap, settings, "roland", null);
                verifyAuthConfig(config, "admin", "token123", null);
            }
        });
    }

    @Test
    public void testOpenShiftConfigFromSystemProps() throws Exception {
        try {
            System.setProperty("docker.useOpenShiftAuth", "true");
            executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
                @Override
                public void exec(File homeDir) throws IOException, MojoExecutionException {
                    createOpenShiftConfig(homeDir, "openshift_simple_config.yaml");
                    AuthConfig config = factory.createAuthConfig(isPush, false, null, settings, "roland", null);
                    verifyAuthConfig(config, "admin", "token123", null);
                }
            });
        } finally {
            System.getProperties().remove("docker.useOpenShiftAuth");
        }
    }

    @Test
    public void testOpenShiftConfigFromSystemPropsNegative() throws Exception {
        try {
            System.setProperty("docker.useOpenShiftAuth", "false");
            executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
                @Override
                public void exec(File homeDir) throws IOException, MojoExecutionException {
                    createOpenShiftConfig(homeDir, "openshift_simple_config.yaml");
                    Map<String, String> authConfigMap = new HashMap<>();
                    authConfigMap.put("useOpenShiftAuth", "true");
                    AuthConfig config = factory.createAuthConfig(isPush, false, authConfigMap, settings, "roland", null);
                    Assert.assertNull(config);
                }
            });
        } finally {
            System.getProperties().remove("docker.useOpenShiftAuth");
        }
    }

    @Test
    public void testOpenShiftConfigNotLoggedIn() throws Exception {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                createOpenShiftConfig(homeDir, "openshift_nologin_config.yaml");
                Map<String, String> authConfigMap = new HashMap<>();
                authConfigMap.put("useOpenShiftAuth", "true");
                expectedException.expect(MojoExecutionException.class);
                expectedException.expectMessage(CoreMatchers.containsString("~/.kube/config"));
                factory.createAuthConfig(isPush, false, authConfigMap, settings, "roland", null);
            }
        });
    }

    @Test
    public void testSystemPropertyNoPassword() throws Exception {
        expectedException.expect(MojoExecutionException.class);
        expectedException.expectMessage("No docker.password provided for username secret");
        checkException("docker.username");
    }

    @Test
    public void testFromPluginConfiguration() throws MojoExecutionException {
        Map pluginConfig = new HashMap();
        pluginConfig.put("username", "roland");
        pluginConfig.put("password", "secret");
        pluginConfig.put("email", "roland@jolokia.org");
        AuthConfig config = factory.createAuthConfig(isPush, false, pluginConfig, settings, null, null);
        verifyAuthConfig(config, "roland", "secret", "roland@jolokia.org");
    }

    @Test
    public void testFromPluginConfigurationPull() throws MojoExecutionException {
        Map pullConfig = new HashMap();
        pullConfig.put("username", "roland");
        pullConfig.put("password", "secret");
        pullConfig.put("email", "roland@jolokia.org");
        Map pluginConfig = new HashMap();
        pluginConfig.put("pull", pullConfig);
        AuthConfig config = factory.createAuthConfig(false, false, pluginConfig, settings, null, null);
        verifyAuthConfig(config, "roland", "secret", "roland@jolokia.org");
    }

    @Test
    public void testFromPluginConfigurationFailed() throws MojoExecutionException {
        Map pluginConfig = new HashMap();
        pluginConfig.put("username", "admin");
        expectedException.expect(MojoExecutionException.class);
        expectedException.expectMessage("No 'password' given while using <authConfig> in configuration for mode DEFAULT");
        factory.createAuthConfig(isPush, false, pluginConfig, settings, null, null);
    }

    @Test
    public void testFromSettingsSimple() throws MojoExecutionException {
        setupServers();
        AuthConfig config = factory.createAuthConfig(isPush, false, null, settings, "roland", "test.org");
        Assert.assertNotNull(config);
        verifyAuthConfig(config, "roland", "secret", "roland@jolokia.org");
    }

    @Test
    public void testFromSettingsDefault() throws MojoExecutionException {
        setupServers();
        AuthConfig config = factory.createAuthConfig(isPush, false, null, settings, "fabric8io", "test.org");
        Assert.assertNotNull(config);
        verifyAuthConfig(config, "fabric8io", "secret2", "fabric8io@redhat.com");
    }

    @Test
    public void testFromSettingsDefault2() throws MojoExecutionException {
        setupServers();
        AuthConfig config = factory.createAuthConfig(isPush, false, null, settings, "tanja", null);
        Assert.assertNotNull(config);
        verifyAuthConfig(config, "tanja", "doublesecret", "tanja@jolokia.org");
    }

    @Test
    public void testWrongUserName() throws IOException, MojoExecutionException {
        executeWithTempHomeDir(new AuthConfigFactoryTest.HomeDirExecutor() {
            @Override
            public void exec(File homeDir) throws IOException, MojoExecutionException {
                setupServers();
                Assert.assertNull(factory.createAuthConfig(isPush, false, null, settings, "roland", "another.repo.org"));
            }
        });
    }
}

