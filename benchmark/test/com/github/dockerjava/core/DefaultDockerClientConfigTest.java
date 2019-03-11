package com.github.dockerjava.core;


import AuthConfig.DEFAULT_SERVER_ADDRESS;
import DefaultDockerClientConfig.API_VERSION;
import DefaultDockerClientConfig.Builder;
import DefaultDockerClientConfig.DOCKER_CERT_PATH;
import DefaultDockerClientConfig.DOCKER_CONFIG;
import DefaultDockerClientConfig.DOCKER_HOST;
import DefaultDockerClientConfig.DOCKER_TLS_VERIFY;
import DefaultDockerClientConfig.REGISTRY_EMAIL;
import DefaultDockerClientConfig.REGISTRY_PASSWORD;
import DefaultDockerClientConfig.REGISTRY_URL;
import DefaultDockerClientConfig.REGISTRY_USERNAME;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.AuthConfigurations;
import com.google.common.io.Resources;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang.SerializationUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class DefaultDockerClientConfigTest {
    public static final DefaultDockerClientConfig EXAMPLE_CONFIG = DefaultDockerClientConfigTest.newExampleConfig();

    @Test
    public void equals() throws Exception {
        Assert.assertEquals(DefaultDockerClientConfigTest.EXAMPLE_CONFIG, DefaultDockerClientConfigTest.newExampleConfig());
    }

    @Test
    public void environmentDockerHost() throws Exception {
        // given docker host in env
        Map<String, String> env = new HashMap<String, String>();
        env.put(DOCKER_HOST, "tcp://baz:8768");
        // and it looks to be SSL disabled
        env.remove("DOCKER_CERT_PATH");
        // given default cert path
        Properties systemProperties = new Properties();
        systemProperties.setProperty("user.name", "someUserName");
        systemProperties.setProperty("user.home", DefaultDockerClientConfigTest.homeDir());
        // when you build a config
        DefaultDockerClientConfig config = buildConfig(env, systemProperties);
        Assert.assertEquals(config.getDockerHost(), URI.create("tcp://baz:8768"));
    }

    @Test
    public void environment() throws Exception {
        // given a default config in env properties
        Map<String, String> env = new HashMap<String, String>();
        env.put(DOCKER_HOST, "tcp://foo");
        env.put(API_VERSION, "apiVersion");
        env.put(REGISTRY_USERNAME, "registryUsername");
        env.put(REGISTRY_PASSWORD, "registryPassword");
        env.put(REGISTRY_EMAIL, "registryEmail");
        env.put(REGISTRY_URL, "registryUrl");
        env.put(DOCKER_CONFIG, "dockerConfig");
        env.put(DOCKER_CERT_PATH, DefaultDockerClientConfigTest.dockerCertPath());
        env.put(DOCKER_TLS_VERIFY, "1");
        // when you build a config
        DefaultDockerClientConfig config = buildConfig(env, new Properties());
        // then we get the example object
        Assert.assertEquals(config, DefaultDockerClientConfigTest.EXAMPLE_CONFIG);
    }

    @Test
    public void defaults() throws Exception {
        // given default cert path
        Properties systemProperties = new Properties();
        systemProperties.setProperty("user.name", "someUserName");
        systemProperties.setProperty("user.home", DefaultDockerClientConfigTest.homeDir());
        // when you build config
        DefaultDockerClientConfig config = buildConfig(Collections.<String, String>emptyMap(), systemProperties);
        // then the cert path is as expected
        Assert.assertEquals(config.getDockerHost(), URI.create("unix:///var/run/docker.sock"));
        Assert.assertEquals(config.getRegistryUsername(), "someUserName");
        Assert.assertEquals(config.getRegistryUrl(), DEFAULT_SERVER_ADDRESS);
        Assert.assertEquals(config.getApiVersion(), RemoteApiVersion.unknown());
        Assert.assertEquals(config.getDockerConfigPath(), ((DefaultDockerClientConfigTest.homeDir()) + "/.docker"));
        Assert.assertNull(config.getSSLConfig());
    }

    @Test
    public void systemProperties() throws Exception {
        // given system properties based on the example
        Properties systemProperties = new Properties();
        systemProperties.put(DOCKER_HOST, "tcp://foo");
        systemProperties.put(API_VERSION, "apiVersion");
        systemProperties.put(REGISTRY_USERNAME, "registryUsername");
        systemProperties.put(REGISTRY_PASSWORD, "registryPassword");
        systemProperties.put(REGISTRY_EMAIL, "registryEmail");
        systemProperties.put(REGISTRY_URL, "registryUrl");
        systemProperties.put(DOCKER_CONFIG, "dockerConfig");
        systemProperties.put(DOCKER_CERT_PATH, DefaultDockerClientConfigTest.dockerCertPath());
        systemProperties.put(DOCKER_TLS_VERIFY, "1");
        // when you build new config
        DefaultDockerClientConfig config = buildConfig(Collections.<String, String>emptyMap(), systemProperties);
        // then it is the same as the example
        Assert.assertEquals(config, DefaultDockerClientConfigTest.EXAMPLE_CONFIG);
    }

    @Test
    public void serializableTest() {
        final byte[] serialized = SerializationUtils.serialize(DefaultDockerClientConfigTest.EXAMPLE_CONFIG);
        final DefaultDockerClientConfig deserialized = ((DefaultDockerClientConfig) (SerializationUtils.deserialize(serialized)));
        MatcherAssert.assertThat("Deserialized object mush match source object", deserialized, Matchers.equalTo(DefaultDockerClientConfigTest.EXAMPLE_CONFIG));
    }

    @Test
    public void testSslContextEmpty() throws Exception {
        new DefaultDockerClientConfig(URI.create("tcp://foo"), "dockerConfig", "apiVersion", "registryUrl", "registryUsername", "registryPassword", "registryEmail", null);
    }

    @Test
    public void testTlsVerifyAndCertPath() throws Exception {
        new DefaultDockerClientConfig(URI.create("tcp://foo"), "dockerConfig", "apiVersion", "registryUrl", "registryUsername", "registryPassword", "registryEmail", new LocalDirectorySSLConfig(DefaultDockerClientConfigTest.dockerCertPath()));
    }

    @Test(expected = DockerClientException.class)
    public void testWrongHostScheme() throws Exception {
        new DefaultDockerClientConfig(URI.create("http://foo"), "dockerConfig", "apiVersion", "registryUrl", "registryUsername", "registryPassword", "registryEmail", null);
    }

    @Test
    public void testTcpHostScheme() throws Exception {
        new DefaultDockerClientConfig(URI.create("tcp://foo"), "dockerConfig", "apiVersion", "registryUrl", "registryUsername", "registryPassword", "registryEmail", null);
    }

    @Test
    public void testUnixHostScheme() throws Exception {
        new DefaultDockerClientConfig(URI.create("unix://foo"), "dockerConfig", "apiVersion", "registryUrl", "registryUsername", "registryPassword", "registryEmail", null);
    }

    @Test
    public void withDockerTlsVerify() throws Exception {
        DefaultDockerClientConfig.Builder builder = new DefaultDockerClientConfig.Builder();
        Field field = builder.getClass().getDeclaredField("dockerTlsVerify");
        field.setAccessible(true);
        builder.withDockerTlsVerify("");
        MatcherAssert.assertThat(((Boolean) (field.get(builder))), Is.is(false));
        builder.withDockerTlsVerify("false");
        MatcherAssert.assertThat(((Boolean) (field.get(builder))), Is.is(false));
        builder.withDockerTlsVerify("FALSE");
        MatcherAssert.assertThat(((Boolean) (field.get(builder))), Is.is(false));
        builder.withDockerTlsVerify("true");
        MatcherAssert.assertThat(((Boolean) (field.get(builder))), Is.is(true));
        builder.withDockerTlsVerify("TRUE");
        MatcherAssert.assertThat(((Boolean) (field.get(builder))), Is.is(true));
        builder.withDockerTlsVerify("0");
        MatcherAssert.assertThat(((Boolean) (field.get(builder))), Is.is(false));
        builder.withDockerTlsVerify("1");
        MatcherAssert.assertThat(((Boolean) (field.get(builder))), Is.is(true));
    }

    @Test
    public void testGetAuthConfigurationsFromDockerCfg() throws URISyntaxException {
        File cfgFile = new File(Resources.getResource("com.github.dockerjava.core/registry.v1").toURI());
        DefaultDockerClientConfig clientConfig = new DefaultDockerClientConfig(URI.create("unix://foo"), cfgFile.getAbsolutePath(), "apiVersion", "registryUrl", "registryUsername", "registryPassword", "registryEmail", null);
        AuthConfigurations authConfigurations = clientConfig.getAuthConfigurations();
        MatcherAssert.assertThat(authConfigurations, Matchers.notNullValue());
        MatcherAssert.assertThat(authConfigurations.getConfigs().get("https://test.docker.io/v1/"), Matchers.notNullValue());
        AuthConfig authConfig = authConfigurations.getConfigs().get("https://test.docker.io/v1/");
        MatcherAssert.assertThat(authConfig.getUsername(), Matchers.equalTo("user"));
        MatcherAssert.assertThat(authConfig.getPassword(), Matchers.equalTo("password"));
    }

    @Test
    public void testGetAuthConfigurationsFromConfigJson() throws URISyntaxException {
        File cfgFile = new File(Resources.getResource("com.github.dockerjava.core/registry.v2").toURI());
        DefaultDockerClientConfig clientConfig = new DefaultDockerClientConfig(URI.create("unix://foo"), cfgFile.getAbsolutePath(), "apiVersion", "registryUrl", "registryUsername", "registryPassword", "registryEmail", null);
        AuthConfigurations authConfigurations = clientConfig.getAuthConfigurations();
        MatcherAssert.assertThat(authConfigurations, Matchers.notNullValue());
        MatcherAssert.assertThat(authConfigurations.getConfigs().get("https://test.docker.io/v2/"), Matchers.notNullValue());
        AuthConfig authConfig = authConfigurations.getConfigs().get("https://test.docker.io/v2/");
        MatcherAssert.assertThat(authConfig.getUsername(), Matchers.equalTo("user"));
        MatcherAssert.assertThat(authConfig.getPassword(), Matchers.equalTo("password"));
    }
}

