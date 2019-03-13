package io.fabric8.maven.docker.service;


import io.fabric8.maven.docker.access.ContainerCreateConfig;
import io.fabric8.maven.docker.access.ContainerHostConfig;
import io.fabric8.maven.docker.access.DockerAccess;
import io.fabric8.maven.docker.access.DockerAccessException;
import io.fabric8.maven.docker.config.Arguments;
import io.fabric8.maven.docker.config.RunImageConfiguration;
import io.fabric8.maven.docker.config.VolumeConfiguration;
import io.fabric8.maven.docker.log.LogOutputSpec;
import io.fabric8.maven.docker.log.LogOutputSpecFactory;
import io.fabric8.maven.docker.util.Logger;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test need to be refactored. In fact, testing Mojos must be setup correctly at all. Blame on me that there are so
 * few tests ...
 */
public class RunServiceTest {
    private ContainerCreateConfig containerConfig;

    @Mocked
    private MavenProject project;

    @Mocked
    private MavenSession session;

    @Mocked
    private DockerAccess docker;

    @Mocked
    private Logger log;

    @Mocked
    private QueryService queryService;

    private Properties properties;

    private RunImageConfiguration runConfig;

    private RunService runService;

    private ContainerHostConfig startConfig;

    private ContainerTracker tracker;

    @Test
    public void testCreateContainerAllConfig() throws Exception {
        /* -
        this is really two tests in one
         - verify the start dockerRunner calls all the methods to build the container configs
         - the container configs produce the correct json when all options are specified

        it didn't seem worth the effort to build a separate test to verify the json and then mock/verify all the calls here
         */
        new Expectations() {
            {
                queryService.getContainerName("redisContainer1");
                result = "db1";
                queryService.getContainerName("redisContainer2");
                result = "db2";
                queryService.getContainerName("parentContainer");
                result = "parentContainer";
                queryService.getContainerName("otherContainer");
                result = "otherContainer";
            }
        };
        givenARunConfiguration();
        givenAnImageConfiguration("redis3", "db1", "redisContainer1");
        givenAnImageConfiguration("redis3", "db2", "redisContainer2");
        givenAnImageConfiguration("parent", "parentName", "parentContainer");
        givenAnImageConfiguration("other_name", "other:ro", "otherContainer");
        whenCreateContainerConfig("base");
        thenContainerConfigIsValid();
        thenStartConfigIsValid();
    }

    // ===========================================================
    private String container = "testContainer";

    private int SHUTDOWN_WAIT = 500;

    private int KILL_AFTER = 1000;

    private VolumeConfiguration volumeConfiguration = new VolumeConfiguration.Builder().name("sqlserver-backup-dev").driver("rexray").opts(Collections.singletonMap("size", "50")).build();

    @Test
    public void shutdownWithoutKeepingContainers() throws Exception {
        new Expectations() {
            {
                docker.stopContainer(container, 0);
                log.debug(anyString, ((Object[]) (any)));
                minTimes = 1;
                docker.removeContainer(container, false);
                log.info(withSubstring("Stop"), anyString, withSubstring("removed"), withSubstring(container.substring(0, 12)), anyLong);
            }
        };
        long start = System.currentTimeMillis();
        runService.stopContainer(container, createImageConfig(SHUTDOWN_WAIT, 0), false, false);
        Assert.assertTrue((("Waited for at least " + (SHUTDOWN_WAIT)) + " ms"), (((System.currentTimeMillis()) - start) >= (SHUTDOWN_WAIT)));
    }

    @Test
    public void shutdownWithoutKeepingContainersAndRemovingVolumes() throws Exception {
        new Expectations() {
            {
                docker.stopContainer(container, 0);
                log.debug(anyString, ((Object[]) (any)));
                minTimes = 1;
                docker.removeContainer(container, true);
                log.info(withSubstring("Stop"), anyString, withSubstring("removed"), withSubstring(container.substring(0, 12)), anyLong);
            }
        };
        long start = System.currentTimeMillis();
        runService.stopContainer(container, createImageConfig(SHUTDOWN_WAIT, 0), false, true);
        Assert.assertTrue((("Waited for at least " + (SHUTDOWN_WAIT)) + " ms"), (((System.currentTimeMillis()) - start) >= (SHUTDOWN_WAIT)));
    }

    @Test
    public void shutdownWithKeepingContainer() throws Exception {
        new Expectations() {
            {
                docker.stopContainer(container, 0);
                log.info(withSubstring("Stop"), anyString, withNotEqual(" and removed"), withSubstring(container.substring(0, 12)), anyLong);
            }
        };
        long start = System.currentTimeMillis();
        runService.stopContainer(container, createImageConfig(SHUTDOWN_WAIT, 0), true, false);
        Assert.assertTrue("No wait", (((System.currentTimeMillis()) - start) < (SHUTDOWN_WAIT)));
    }

    @Test
    public void shutdownWithPreStopExecConfig() throws Exception {
        new Expectations() {
            {
                docker.createExecContainer(container, ((Arguments) (withNotNull())));
                result = "execContainerId";
                docker.startExecContainer("execContainerId", ((LogOutputSpec) (any)));
                docker.stopContainer(container, 0);
                log.info(withSubstring("Stop"), anyString, withNotEqual(" and removed"), withSubstring(container.substring(0, 12)), anyLong);
            }
        };
        long start = System.currentTimeMillis();
        runService.stopContainer(container, createImageConfigWithExecConfig(SHUTDOWN_WAIT), true, false);
        Assert.assertTrue("No wait", (((System.currentTimeMillis()) - start) < (SHUTDOWN_WAIT)));
    }

    @Test
    public void testWithoutWait() throws Exception {
        new Expectations() {
            {
                docker.stopContainer(container, 0);
                log.debug(anyString);
                times = 0;
                docker.removeContainer(container, false);
                log.info(withSubstring("Stop"), anyString, withSubstring("removed"), withSubstring(container.substring(0, 12)), anyLong);
            }
        };
        long start = System.currentTimeMillis();
        runService.stopContainer(container, createImageConfig(0, 0), false, false);
        Assert.assertTrue("No wait", (((System.currentTimeMillis()) - start) < (SHUTDOWN_WAIT)));
    }

    @Test(expected = DockerAccessException.class)
    public void testWithException() throws Exception {
        new Expectations() {
            {
                docker.stopContainer(container, 0);
                result = new DockerAccessException("Test");
            }
        };
        runService.stopContainer(container, createImageConfig(SHUTDOWN_WAIT, 0), false, false);
    }

    @Test
    public void testVolumesDuringStart() throws DockerAccessException {
        ServiceHub hub = new ServiceHubFactory().createServiceHub(project, session, docker, log, new LogOutputSpecFactory(true, true, null));
        List<String> volumeBinds = Collections.singletonList("sqlserver-backup-dev:/var/opt/mssql/data");
        List<VolumeConfiguration> volumeConfigurations = Collections.singletonList(volumeConfiguration);
        List<String> createdVolumes = runService.createVolumesAsPerVolumeBinds(hub, volumeBinds, volumeConfigurations);
        Assert.assertEquals(createdVolumes.get(0), volumeConfigurations.get(0).getName());
        Assert.assertTrue(createdVolumes.contains(volumeConfigurations.get(0).getName()));
    }

    final class LogInfoMatchingExpectations extends Expectations {
        LogInfoMatchingExpectations(String container, boolean withRemove) {
            log.info(withSubstring("Stop"), anyString, (withRemove ? withSubstring("removed") : withNotEqual(" and removed")), withSubstring(container.substring(0, 12)), anyLong);
        }
    }
}

