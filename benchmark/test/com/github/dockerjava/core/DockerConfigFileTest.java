/**
 * Copyright (C) 2014 SignalFuse, Inc.
 */
package com.github.dockerjava.core;


import AuthConfig.DEFAULT_SERVER_ADDRESS;
import com.github.dockerjava.api.model.AuthConfig;
import java.io.File;
import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DockerConfigFileTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private final File FILESROOT = new File(DockerConfigFileTest.class.getResource("/testAuthConfigFile").getFile());

    @Test
    public void emptyFile() throws IOException {
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("The Auth Config file is empty");
        runTest("emptyFile");
    }

    @Test
    public void tooSmallFile() throws IOException {
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("The Auth Config file is empty");
        runTest("tooSmallFile");
    }

    @Test
    public void invalidJsonInvalidAuth() throws IOException {
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("Invalid auth configuration file");
        runTest("invalidJsonInvalidAuth");
    }

    @Test
    public void invalidLegacyAuthLine() throws IOException {
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("Invalid Auth config file");
        runTest("invalidLegacyAuthLine");
    }

    @Test
    public void invalidLegacyInvalidAuth() throws IOException {
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("Invalid auth configuration file");
        runTest("invalidLegacyInvalidAuth");
    }

    @Test
    public void invalidLegacyEmailLine() throws IOException {
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("Invalid Auth config file");
        runTest("invalidLegacyEmailLine");
    }

    @Test
    public void validLegacyJson() throws IOException {
        AuthConfig authConfig1 = new AuthConfig().withEmail("foo@example.com").withUsername("foo").withPassword("bar").withRegistryAddress("quay.io");
        AuthConfig authConfig2 = new AuthConfig().withEmail("moo@example.com").withUsername("foo1").withPassword("bar1").withRegistryAddress(DEFAULT_SERVER_ADDRESS);
        DockerConfigFile expected = new DockerConfigFile();
        expected.addAuthConfig(authConfig1);
        expected.addAuthConfig(authConfig2);
        Assert.assertThat(runTest("validLegacyJson"), Is.is(expected));
    }

    @Test
    public void validJsonWithUnknown() throws IOException {
        AuthConfig authConfig1 = new AuthConfig().withRegistryAddress("192.168.99.100:32768");
        AuthConfig authConfig2 = new AuthConfig().withEmail("foo@example.com").withUsername("foo").withPassword("bar").withRegistryAddress("https://index.docker.io/v1/");
        DockerConfigFile expected = new DockerConfigFile();
        expected.addAuthConfig(authConfig1);
        expected.addAuthConfig(authConfig2);
        DockerConfigFile actual = runTest("validJsonWithUnknown");
        Assert.assertThat(actual, Is.is(expected));
    }

    @Test
    public void validJsonWithOnlyUnknown() throws IOException {
        DockerConfigFile expected = new DockerConfigFile();
        DockerConfigFile actual = runTest("validJsonWithOnlyUnknown");
        Assert.assertThat(actual, Is.is(expected));
    }

    @Test
    public void validLegacy() throws IOException {
        AuthConfig authConfig = new AuthConfig().withEmail("foo@example.com").withUsername("foo").withPassword("bar").withRegistryAddress(DEFAULT_SERVER_ADDRESS);
        DockerConfigFile expected = new DockerConfigFile();
        expected.addAuthConfig(authConfig);
        Assert.assertThat(runTest("validLegacy"), Is.is(expected));
    }

    @Test
    public void validDockerConfig() throws IOException {
        AuthConfig authConfig1 = new AuthConfig().withEmail("foo@example.com").withUsername("foo").withPassword("bar").withRegistryAddress("quay.io");
        AuthConfig authConfig2 = new AuthConfig().withEmail("moo@example.com").withUsername("foo1").withPassword("bar1").withRegistryAddress(DEFAULT_SERVER_ADDRESS);
        DockerConfigFile expected = new DockerConfigFile();
        expected.addAuthConfig(authConfig1);
        expected.addAuthConfig(authConfig2);
        Assert.assertThat(runTest("validDockerConfig"), Is.is(expected));
    }

    @Test
    public void nonExistent() throws IOException {
        DockerConfigFile expected = new DockerConfigFile();
        Assert.assertThat(runTest("idontexist"), Is.is(expected));
    }
}

