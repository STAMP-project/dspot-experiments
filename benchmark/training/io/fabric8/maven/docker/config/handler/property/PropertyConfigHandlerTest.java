/**
 * Copyright 2014 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.maven.docker.config.handler.property;


import CleanupMode.NONE;
import ConfigHelper.NameFormatter.IDENTITY;
import ConfigKey.CLEANUP;
import ConfigKey.DOCKER_ARCHIVE;
import ConfigKey.DOCKER_FILE;
import ConfigKey.DOCKER_FILE_DIR;
import ConfigKey.FILTER;
import ConfigKey.FROM;
import ConfigKey.NAME;
import ConfigKey.NOCACHE;
import ConfigKey.OPTIMISE;
import ConfigKey.SKIP_BUILD;
import ConfigKey.SKIP_RUN;
import ConfigKey.ULIMITS;
import PropertyMode.Fallback;
import PropertyMode.Override;
import io.fabric8.maven.docker.config.AssemblyConfiguration;
import io.fabric8.maven.docker.config.BuildImageConfiguration;
import io.fabric8.maven.docker.config.CleanupMode;
import io.fabric8.maven.docker.config.ImageConfiguration;
import io.fabric8.maven.docker.config.LogConfiguration;
import io.fabric8.maven.docker.config.RunImageConfiguration;
import io.fabric8.maven.docker.config.UlimitConfig;
import io.fabric8.maven.docker.config.handler.AbstractConfigHandlerTest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import mockit.Mocked;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;


/**
 *
 *
 * @author roland
 * @since 05/12/14
 */
public class PropertyConfigHandlerTest extends AbstractConfigHandlerTest {
    private PropertyConfigHandler configHandler;

    private ImageConfiguration imageConfiguration;

    @Mocked
    private MavenProject project;

    @Test
    public void testSkipBuild() {
        Assert.assertFalse(resolveExternalImageConfig(getSkipTestData(SKIP_BUILD, false)).getBuildConfiguration().skip());
        Assert.assertTrue(resolveExternalImageConfig(getSkipTestData(SKIP_BUILD, true)).getBuildConfiguration().skip());
        Assert.assertFalse(resolveExternalImageConfig(new String[]{ k(NAME), "image", k(FROM), "busybox" }).getBuildConfiguration().skip());
    }

    @Test
    public void testSkipRun() {
        Assert.assertFalse(resolveExternalImageConfig(getSkipTestData(SKIP_RUN, false)).getRunConfiguration().skip());
        Assert.assertTrue(resolveExternalImageConfig(getSkipTestData(SKIP_RUN, true)).getRunConfiguration().skip());
        Assert.assertFalse(resolveExternalImageConfig(new String[]{ k(NAME), "image" }).getRunConfiguration().skip());
    }

    @Test
    public void testType() throws Exception {
        Assert.assertNotNull(configHandler.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmpty() throws Exception {
        resolveImage(imageConfiguration, props());
    }

    @Test
    public void testPorts() {
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.name", "demo", "docker.ports.1", "jolokia.port:8080", "docker.ports.2", "9090", "docker.ports.3", "0.0.0.0:80:80", "docker.from", "busybox"));
        Assert.assertEquals(1, configs.size());
        RunImageConfiguration runConfig = configs.get(0).getRunConfiguration();
        List<String> portsAsList = runConfig.getPorts();
        String[] ports = new ArrayList<>(portsAsList).toArray(new String[portsAsList.size()]);
        Assert.assertArrayEquals(new String[]{ "jolokia.port:8080", "9090", "0.0.0.0:80:80" }, ports);
        BuildImageConfiguration buildConfig = configs.get(0).getBuildConfiguration();
        ports = new ArrayList(buildConfig.getPorts()).toArray(new String[buildConfig.getPorts().size()]);
        Assert.assertArrayEquals(new String[]{ "8080", "9090", "80" }, ports);
    }

    @Test
    public void testPortsFromConfigAndProperties() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(new java.util.HashMap<String, String>()).buildConfig(new BuildImageConfiguration.Builder().ports(asList("1234")).build()).runConfig(new RunImageConfiguration.Builder().ports(asList("jolokia.port:1234")).build()).build();
        makeExternalConfigUse(Override);
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.name", "demo", "docker.ports.1", "9090", "docker.ports.2", "0.0.0.0:80:80", "docker.from", "busybox"));
        Assert.assertEquals(1, configs.size());
        RunImageConfiguration runConfig = configs.get(0).getRunConfiguration();
        List<String> portsAsList = runConfig.getPorts();
        String[] ports = new ArrayList<>(portsAsList).toArray(new String[portsAsList.size()]);
        Assert.assertArrayEquals(new String[]{ "9090", "0.0.0.0:80:80", "jolokia.port:1234" }, ports);
        BuildImageConfiguration buildConfig = configs.get(0).getBuildConfiguration();
        ports = new ArrayList(buildConfig.getPorts()).toArray(new String[buildConfig.getPorts().size()]);
        Assert.assertArrayEquals(new String[]{ "9090", "80", "1234" }, ports);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPropertyMode() {
        makeExternalConfigUse(Override);
        imageConfiguration.getExternalConfig().put("mode", "invalid");
        resolveImage(imageConfiguration, props());
    }

    @Test
    public void testRunCommands() {
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.run.1", "foo", "docker.run.2", "bar", "docker.run.3", "wibble"));
        Assert.assertEquals(1, configs.size());
        BuildImageConfiguration buildConfig = configs.get(0).getBuildConfiguration();
        String[] runCommands = new ArrayList(buildConfig.getRunCmds()).toArray(new String[buildConfig.getRunCmds().size()]);
        Assert.assertArrayEquals(new String[]{ "foo", "bar", "wibble" }, runCommands);
    }

    @Test
    public void testRunCommandsFromPropertiesAndConfig() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(new java.util.HashMap<String, String>()).buildConfig(new BuildImageConfiguration.Builder().runCmds(asList("some", "ignored", "value")).build()).build();
        makeExternalConfigUse(Override);
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.run.1", "propconf", "docker.run.2", "withrun", "docker.run.3", "used"));
        Assert.assertEquals(1, configs.size());
        BuildImageConfiguration buildConfig = configs.get(0).getBuildConfiguration();
        String[] runCommands = new ArrayList(buildConfig.getRunCmds()).toArray(new String[buildConfig.getRunCmds().size()]);
        Assert.assertArrayEquals(new String[]{ "propconf", "withrun", "used" }, runCommands);
    }

    @Test
    public void testRunCommandsFromConfigAndProperties() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Fallback)).buildConfig(new BuildImageConfiguration.Builder().runCmds(asList("some", "configured", "value")).build()).build();
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.run.1", "this", "docker.run.2", "is", "docker.run.3", "ignored"));
        Assert.assertEquals(1, configs.size());
        BuildImageConfiguration buildConfig = configs.get(0).getBuildConfiguration();
        String[] runCommands = new ArrayList(buildConfig.getRunCmds()).toArray(new String[buildConfig.getRunCmds().size()]);
        Assert.assertArrayEquals(new String[]{ "some", "configured", "value" }, runCommands);
    }

    @Test
    public void testEntrypoint() {
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.entrypoint", "/entrypoint.sh --from-property"));
        Assert.assertEquals(1, configs.size());
        BuildImageConfiguration buildConfig = configs.get(0).getBuildConfiguration();
        Assert.assertArrayEquals(new String[]{ "/entrypoint.sh", "--from-property" }, buildConfig.getEntryPoint().asStrings().toArray());
    }

    @Test
    public void testEntrypointExecFromConfig() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Fallback)).buildConfig(new BuildImageConfiguration.Builder().entryPoint(new io.fabric8.maven.docker.config.Arguments(asList("/entrypoint.sh", "--from-property"))).build()).build();
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo"));
        Assert.assertEquals(1, configs.size());
        BuildImageConfiguration buildConfig = configs.get(0).getBuildConfiguration();
        Assert.assertArrayEquals(new String[]{ "/entrypoint.sh", "--from-property" }, buildConfig.getEntryPoint().asStrings().toArray());
    }

    @Test
    public void testDefaultLogEnabledConfiguration() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Override)).buildConfig(new BuildImageConfiguration.Builder().build()).build();
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo"));
        Assert.assertEquals(1, configs.size());
        RunImageConfiguration runConfiguration = configs.get(0).getRunConfiguration();
        Assert.assertNull(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertFalse(runConfiguration.getLogConfiguration().isActivated());
        // If any log property is set, enabled shall be true by default
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.color", "green"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertNull(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("green", runConfiguration.getLogConfiguration().getColor());
        // If image configuration has non-blank log configuration, it should become enabled
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Override)).runConfig(new RunImageConfiguration.Builder().log(new LogConfiguration.Builder().color("red").build()).build()).build();
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertNull(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("red", runConfiguration.getLogConfiguration().getColor());
        // and if set by property, still enabled but overrides
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.color", "yellow"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertNull(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("yellow", runConfiguration.getLogConfiguration().getColor());
        // Fallback works as well
        makeExternalConfigUse(Fallback);
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.color", "yellow"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertNull(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("red", runConfiguration.getLogConfiguration().getColor());
    }

    @Test
    public void testExplicitLogEnabledConfiguration() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Override)).runConfig(new RunImageConfiguration.Builder().log(new LogConfiguration.Builder().color("red").build()).build()).build();
        // Explicitly enabled
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.enabled", "true"));
        RunImageConfiguration runConfiguration = getRunImageConfiguration(configs);
        Assert.assertTrue(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("red", runConfiguration.getLogConfiguration().getColor());
        // Explicitly disabled
        makeExternalConfigUse(Override);
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.color", "yellow", "docker.log.enabled", "false"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertFalse(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertFalse(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("yellow", runConfiguration.getLogConfiguration().getColor());
        // Disabled by config
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Fallback)).runConfig(new RunImageConfiguration.Builder().log(new LogConfiguration.Builder().enabled(false).color("red").build()).build()).build();
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertFalse(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertFalse(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("red", runConfiguration.getLogConfiguration().getColor());
        // Enabled by property, with override
        makeExternalConfigUse(Override);
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.enabled", "true"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertTrue(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("red", runConfiguration.getLogConfiguration().getColor());
        // Disabled with property too
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.enabled", "false"));
        runConfiguration = getRunImageConfiguration(configs);
        Assert.assertFalse(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertFalse(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("red", runConfiguration.getLogConfiguration().getColor());
    }

    @Test
    public void testLogFile() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Override)).runConfig(new RunImageConfiguration.Builder().log(new LogConfiguration.Builder().file("myfile").build()).build()).build();
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo"));
        Assert.assertEquals(1, configs.size());
        RunImageConfiguration runConfiguration = configs.get(0).getRunConfiguration();
        Assert.assertNull(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("myfile", runConfiguration.getLogConfiguration().getFileLocation());
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(externalConfigMode(Override)).runConfig(new RunImageConfiguration.Builder().build()).build();
        configs = resolveImage(imageConfiguration, props("docker.from", "base", "docker.name", "demo", "docker.log.file", "myfilefromprop"));
        Assert.assertEquals(1, configs.size());
        runConfiguration = configs.get(0).getRunConfiguration();
        Assert.assertNull(runConfiguration.getLogConfiguration().isEnabled());
        Assert.assertTrue(runConfiguration.getLogConfiguration().isActivated());
        Assert.assertEquals("myfilefromprop", runConfiguration.getLogConfiguration().getFileLocation());
    }

    @Test
    public void testBuildFromDockerFileMerged() {
        imageConfiguration = new ImageConfiguration.Builder().name("myimage").externalConfig(externalConfigMode(Override)).buildConfig(new BuildImageConfiguration.Builder().dockerFile("/some/path").build()).build();
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props());
        Assert.assertEquals(1, configs.size());
        BuildImageConfiguration buildConfiguration = configs.get(0).getBuildConfiguration();
        Assert.assertNotNull(buildConfiguration);
        buildConfiguration.initAndValidate(null);
        Path absolutePath = Paths.get(".").toAbsolutePath();
        String expectedPath = (((absolutePath.getRoot()) + "some") + (File.separator)) + "path";
        Assert.assertEquals(expectedPath, buildConfiguration.getDockerFile().getAbsolutePath());
    }

    @Test
    public void testEnvAndLabels() throws Exception {
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "baase", "docker.name", "demo", "docker.env.HOME", "/tmp", "docker.env.root.dir", "/bla", "docker.labels.version", "1.0.0", "docker.labels.blub.bla.foobar", "yep"));
        Assert.assertEquals(1, configs.size());
        ImageConfiguration calcConfig = configs.get(0);
        for (Map env : new Map[]{ calcConfig.getBuildConfiguration().getEnv(), calcConfig.getRunConfiguration().getEnv() }) {
            Assert.assertEquals(2, env.size());
            Assert.assertEquals("/tmp", env.get("HOME"));
            Assert.assertEquals("/bla", env.get("root.dir"));
        }
        for (Map labels : new Map[]{ calcConfig.getBuildConfiguration().getLabels(), calcConfig.getRunConfiguration().getLabels() }) {
            Assert.assertEquals(2, labels.size());
            Assert.assertEquals("1.0.0", labels.get("version"));
            Assert.assertEquals("yep", labels.get("blub.bla.foobar"));
        }
    }

    @Test
    public void testSpecificEnv() throws Exception {
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "baase", "docker.name", "demo", "docker.envBuild.HOME", "/tmp", "docker.envRun.root.dir", "/bla"));
        Assert.assertEquals(1, configs.size());
        ImageConfiguration calcConfig = configs.get(0);
        Map<String, String> env;
        env = calcConfig.getBuildConfiguration().getEnv();
        Assert.assertEquals(1, env.size());
        Assert.assertEquals("/tmp", env.get("HOME"));
        env = calcConfig.getRunConfiguration().getEnv();
        Assert.assertEquals(1, env.size());
        Assert.assertEquals("/bla", env.get("root.dir"));
    }

    @Test
    public void testMergedEnv() throws Exception {
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props("docker.from", "baase", "docker.name", "demo", "docker.env.HOME", "/tmp", "docker.envBuild.HOME", "/var/tmp", "docker.envRun.root.dir", "/bla"));
        Assert.assertEquals(1, configs.size());
        ImageConfiguration calcConfig = configs.get(0);
        Map<String, String> env;
        env = calcConfig.getBuildConfiguration().getEnv();
        Assert.assertEquals(1, env.size());
        Assert.assertEquals("/var/tmp", env.get("HOME"));
        env = calcConfig.getRunConfiguration().getEnv();
        Assert.assertEquals(2, env.size());
        Assert.assertEquals("/tmp", env.get("HOME"));
        Assert.assertEquals("/bla", env.get("root.dir"));
    }

    @Test
    public void testAssembly() throws Exception {
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props(getTestAssemblyData()));
        Assert.assertEquals(1, configs.size());
        AssemblyConfiguration config = configs.get(0).getBuildConfiguration().getAssemblyConfiguration();
        Assert.assertEquals("user", config.getUser());
        Assert.assertEquals("project", config.getDescriptorRef());
        Assert.assertFalse(config.exportTargetDir());
        Assert.assertTrue(config.isIgnorePermissions());
    }

    @Test
    public void testNoCleanup() throws Exception {
        String[] testData = new String[]{ k(NAME), "image", k(CLEANUP), "none", k(FROM), "base" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(NONE, config.getBuildConfiguration().cleanupMode());
    }

    @Test
    public void testNoBuildConfig() throws Exception {
        String[] testData = new String[]{ k(NAME), "image" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertNull(config.getBuildConfiguration());
    }

    @Test
    public void testDockerfile() throws Exception {
        String[] testData = new String[]{ k(NAME), "image", k(DOCKER_FILE_DIR), "src/main/docker/", k(FROM), "busybox" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        config.initAndValidate(IDENTITY, null);
        Assert.assertTrue(config.getBuildConfiguration().isDockerFileMode());
        Assert.assertEquals(new File("src/main/docker/Dockerfile"), config.getBuildConfiguration().getDockerFile());
    }

    @Test
    public void testDockerArchive() {
        String[] testData = new String[]{ k(NAME), "image", k(DOCKER_ARCHIVE), "dockerLoad.tar", k(FROM), "busybox" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        config.initAndValidate(IDENTITY, null);
        Assert.assertFalse(config.getBuildConfiguration().isDockerFileMode());
        Assert.assertEquals(new File("dockerLoad.tar"), config.getBuildConfiguration().getDockerArchive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDockerFileArchiveConfig() {
        String[] testData = new String[]{ k(NAME), "image", k(DOCKER_FILE_DIR), "src/main/docker/", k(DOCKER_ARCHIVE), "dockerLoad.tar", k(FROM), "base" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        config.initAndValidate(IDENTITY, null);
    }

    @Test
    public void testNoCacheDisabled() throws Exception {
        String[] testData = new String[]{ k(NAME), "image", k(NOCACHE), "false", k(FROM), "base" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(false, config.getBuildConfiguration().nocache());
    }

    @Test
    public void testNoCacheEnabled() throws Exception {
        String[] testData = new String[]{ k(NAME), "image", k(NOCACHE), "true", k(FROM), "base" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(true, config.getBuildConfiguration().nocache());
    }

    @Test
    public void testNoOptimise() throws Exception {
        String[] testData = new String[]{ k(NAME), "image", k(OPTIMISE), "false", k(FROM), "base" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(false, config.getBuildConfiguration().optimise());
    }

    @Test
    public void testDockerFile() {
        String[] testData = new String[]{ k(NAME), "image", k(DOCKER_FILE), "file" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertNotNull(config.getBuildConfiguration());
    }

    @Test
    public void testDockerFileDir() {
        String[] testData = new String[]{ k(NAME), "image", k(DOCKER_FILE_DIR), "dir" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertNotNull(config.getBuildConfiguration());
    }

    @Test
    public void testFilterDefault() {
        String[] testData = new String[]{ k(NAME), "image", k(FROM), "base" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(BuildImageConfiguration.DEFAULT_FILTER, config.getBuildConfiguration().getFilter());
    }

    @Test
    public void testFilter() {
        String filter = "@";
        String[] testData = new String[]{ k(NAME), "image", k(FROM), "base", k(FILTER), filter };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(filter, config.getBuildConfiguration().getFilter());
    }

    @Test
    public void testCleanupDefault() {
        String[] testData = new String[]{ k(NAME), "image", k(FROM), "base" };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(BuildImageConfiguration.DEFAULT_CLEANUP, config.getBuildConfiguration().cleanupMode().toParameter());
    }

    @Test
    public void testCleanup() {
        CleanupMode mode = CleanupMode.REMOVE;
        String[] testData = new String[]{ k(NAME), "image", k(FROM), "base", k(CLEANUP), mode.toParameter() };
        ImageConfiguration config = resolveExternalImageConfig(testData);
        Assert.assertEquals(mode, config.getBuildConfiguration().cleanupMode());
    }

    @Test
    public void testUlimit() {
        imageConfiguration = new ImageConfiguration.Builder().externalConfig(new java.util.HashMap<String, String>()).runConfig(new RunImageConfiguration.Builder().ulimits(asList(new UlimitConfig("memlock", 100, 50), new UlimitConfig("nfile", 1024, 512))).build()).build();
        makeExternalConfigUse(Override);
        // TODO: Does Replace make sense here or should we Merge?
        // If merge, it should probably have some more smarts on the ulimit name?
        List<ImageConfiguration> configs = resolveImage(imageConfiguration, props(k(NAME), "image", k(FROM), "base", ((k(ULIMITS)) + ".1"), "memlock=10:10", ((k(ULIMITS)) + ".2"), "memlock=:-1", ((k(ULIMITS)) + ".3"), "memlock=1024:", ((k(ULIMITS)) + ".4"), "memlock=2048"));
        Assert.assertEquals(1, configs.size());
        RunImageConfiguration runConfig = configs.get(0).getRunConfiguration();
        List<UlimitConfig> ulimits = runConfig.getUlimits();
        Assert.assertEquals(4, ulimits.size());
        assertUlimitEquals(ulimit("memlock", 10, 10), runConfig.getUlimits().get(0));
        assertUlimitEquals(ulimit("memlock", null, (-1)), runConfig.getUlimits().get(1));
        assertUlimitEquals(ulimit("memlock", 1024, null), runConfig.getUlimits().get(2));
        assertUlimitEquals(ulimit("memlock", 2048, null), runConfig.getUlimits().get(3));
    }

    @Test
    public void testNoAssembly() throws Exception {
        Properties props = props(k(NAME), "image");
        // List<ImageConfiguration> configs = configHandler.resolve(imageConfiguration, props);
        // assertEquals(1, configs.size());
        // AssemblyConfiguration config = configs.get(0).getBuildConfiguration().getAssemblyConfiguration();
        // assertNull(config);
    }

    @Test
    public void testResolve() {
        ImageConfiguration resolved = resolveExternalImageConfig(getTestData());
        validateBuildConfiguration(resolved.getBuildConfiguration());
        validateRunConfiguration(resolved.getRunConfiguration());
        // validateWaitConfiguraion(resolved.getRunConfiguration().getWaitConfiguration());
    }
}

