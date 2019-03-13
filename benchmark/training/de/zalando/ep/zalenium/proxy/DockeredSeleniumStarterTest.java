package de.zalando.ep.zalenium.proxy;


import DockeredSeleniumStarter.DEFAULT_SCREEN_SIZE;
import DockeredSeleniumStarter.DEFAULT_SELENIUM_NODE_PARAMS;
import DockeredSeleniumStarter.DEFAULT_SEL_BROWSER_TIMEOUT_SECS;
import DockeredSeleniumStarter.DEFAULT_TZ;
import DockeredSeleniumStarter.SELENIUM_NODE_PARAMS;
import DockeredSeleniumStarter.ZALENIUM_SCREEN_HEIGHT;
import DockeredSeleniumStarter.ZALENIUM_SCREEN_WIDTH;
import DockeredSeleniumStarter.ZALENIUM_TZ;
import ZaleniumConfiguration.DEFAULT_AMOUNT_DESIRED_CONTAINERS;
import ZaleniumConfiguration.DEFAULT_AMOUNT_DOCKER_SELENIUM_CONTAINERS_RUNNING;
import ZaleniumConfiguration.DEFAULT_CHECK_CONTAINERS_INTERVAL;
import ZaleniumConfiguration.DEFAULT_TIMES_TO_PROCESS_REQUEST;
import ZaleniumConfiguration.DEFAULT_TIME_TO_WAIT_TO_START;
import ZaleniumConfiguration.ZALENIUM_DESIRED_CONTAINERS;
import ZaleniumConfiguration.ZALENIUM_MAX_DOCKER_SELENIUM_CONTAINERS;
import de.zalando.ep.zalenium.container.ContainerClient;
import de.zalando.ep.zalenium.container.ContainerFactory;
import de.zalando.ep.zalenium.container.DockerContainerClient;
import de.zalando.ep.zalenium.container.kubernetes.KubernetesContainerClient;
import de.zalando.ep.zalenium.util.Environment;
import de.zalando.ep.zalenium.util.ZaleniumConfiguration;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openqa.selenium.Dimension;


@SuppressWarnings("Duplicates")
@RunWith(Parameterized.class)
public class DockeredSeleniumStarterTest {
    private ContainerClient containerClient;

    private Supplier<DockerContainerClient> originalDockerContainerClient;

    private KubernetesContainerClient originalKubernetesContainerClient;

    private Supplier<Boolean> originalIsKubernetesValue;

    private Supplier<Boolean> currentIsKubernetesValue;

    public DockeredSeleniumStarterTest(ContainerClient containerClient, Supplier<Boolean> isKubernetes) {
        this.containerClient = containerClient;
        this.currentIsKubernetesValue = isKubernetes;
        this.originalDockerContainerClient = ContainerFactory.getDockerContainerClient();
        this.originalIsKubernetesValue = ContainerFactory.getIsKubernetes();
        this.originalKubernetesContainerClient = ContainerFactory.getKubernetesContainerClient();
    }

    /* Tests checking the environment variables setup to have a given number of containers on startup */
    @Test
    public void fallbackToDefaultAmountOfValuesWhenVariablesAreNotSet() {
        // Mock the environment class that serves as proxy to retrieve env variables
        Environment environment = Mockito.mock(Environment.class, Mockito.withSettings().useConstructor());
        Mockito.when(environment.getEnvVariable(ArgumentMatchers.any(String.class))).thenReturn(null);
        Mockito.when(environment.getIntEnvVariable(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class))).thenCallRealMethod();
        Mockito.when(environment.getStringEnvVariable(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class))).thenCallRealMethod();
        DockeredSeleniumStarter.setEnv(environment);
        DockeredSeleniumStarter.readConfigurationFromEnvVariables();
        ZaleniumConfiguration.setEnv(environment);
        ZaleniumConfiguration.readConfigurationFromEnvVariables();
        Assert.assertEquals(DEFAULT_AMOUNT_DESIRED_CONTAINERS, ZaleniumConfiguration.getDesiredContainersOnStartup());
        Assert.assertEquals(DEFAULT_AMOUNT_DOCKER_SELENIUM_CONTAINERS_RUNNING, ZaleniumConfiguration.getMaxDockerSeleniumContainers());
        Assert.assertEquals(DEFAULT_SCREEN_SIZE.getHeight(), DockeredSeleniumStarter.getConfiguredScreenSize().getHeight());
        Assert.assertEquals(DEFAULT_SCREEN_SIZE.getWidth(), DockeredSeleniumStarter.getConfiguredScreenSize().getWidth());
        Assert.assertEquals(DEFAULT_TZ.getID(), DockeredSeleniumStarter.getConfiguredTimeZone().getID());
        Assert.assertEquals(DEFAULT_SELENIUM_NODE_PARAMS, DockeredSeleniumStarter.getSeleniumNodeParameters());
    }

    @Test
    public void fallbackToDefaultAmountValuesWhenVariablesAreNotIntegers() {
        // Mock the environment class that serves as proxy to retrieve env variables
        Environment environment = Mockito.mock(Environment.class, Mockito.withSettings().useConstructor());
        Mockito.when(environment.getEnvVariable(ZALENIUM_DESIRED_CONTAINERS)).thenReturn("ABC_NON_INTEGER");
        Mockito.when(environment.getEnvVariable(ZALENIUM_MAX_DOCKER_SELENIUM_CONTAINERS)).thenReturn("ABC_NON_INTEGER");
        Mockito.when(environment.getEnvVariable(ZALENIUM_SCREEN_HEIGHT)).thenReturn("ABC_NON_INTEGER");
        Mockito.when(environment.getEnvVariable(ZALENIUM_SCREEN_WIDTH)).thenReturn("ABC_NON_INTEGER");
        Mockito.when(environment.getEnvVariable(ZALENIUM_TZ)).thenReturn("ABC_NON_STANDARD_TIME_ZONE");
        Mockito.when(environment.getIntEnvVariable(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class))).thenCallRealMethod();
        Mockito.when(environment.getStringEnvVariable(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class))).thenCallRealMethod();
        DockeredSeleniumStarter.setEnv(environment);
        DockeredSeleniumStarter.readConfigurationFromEnvVariables();
        ZaleniumConfiguration.setEnv(environment);
        ZaleniumConfiguration.readConfigurationFromEnvVariables();
        Assert.assertEquals(DEFAULT_AMOUNT_DESIRED_CONTAINERS, ZaleniumConfiguration.getDesiredContainersOnStartup());
        Assert.assertEquals(DEFAULT_AMOUNT_DOCKER_SELENIUM_CONTAINERS_RUNNING, ZaleniumConfiguration.getMaxDockerSeleniumContainers());
        Assert.assertEquals(DEFAULT_SCREEN_SIZE.getHeight(), DockeredSeleniumStarter.getConfiguredScreenSize().getHeight());
        Assert.assertEquals(DEFAULT_SCREEN_SIZE.getWidth(), DockeredSeleniumStarter.getConfiguredScreenSize().getWidth());
        Assert.assertEquals(DEFAULT_TZ.getID(), DockeredSeleniumStarter.getConfiguredTimeZone().getID());
    }

    @Test
    public void variablesGrabTheConfiguredEnvironmentVariables() {
        // Mock the environment class that serves as proxy to retrieve env variables
        Environment environment = Mockito.mock(Environment.class, Mockito.withSettings().useConstructor());
        int amountOfDesiredContainers = 7;
        int amountOfMaxContainers = 8;
        int screenWidth = 1440;
        int screenHeight = 810;
        int browserTimeout = 1000;
        String seleniumNodeParams = "-debug";
        TimeZone timeZone = TimeZone.getTimeZone("America/Montreal");
        Mockito.when(environment.getEnvVariable(ZALENIUM_DESIRED_CONTAINERS)).thenReturn(String.valueOf(amountOfDesiredContainers));
        Mockito.when(environment.getEnvVariable(ZALENIUM_MAX_DOCKER_SELENIUM_CONTAINERS)).thenReturn(String.valueOf(amountOfMaxContainers));
        Mockito.when(environment.getEnvVariable(ZALENIUM_SCREEN_HEIGHT)).thenReturn(String.valueOf(screenHeight));
        Mockito.when(environment.getEnvVariable(ZALENIUM_SCREEN_WIDTH)).thenReturn(String.valueOf(screenWidth));
        Mockito.when(environment.getEnvVariable(ZALENIUM_TZ)).thenReturn(timeZone.getID());
        Mockito.when(environment.getEnvVariable(SELENIUM_NODE_PARAMS)).thenReturn(seleniumNodeParams);
        Mockito.when(environment.getEnvVariable("SEL_BROWSER_TIMEOUT_SECS")).thenReturn(String.valueOf(browserTimeout));
        Mockito.when(environment.getIntEnvVariable(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class))).thenCallRealMethod();
        Mockito.when(environment.getStringEnvVariable(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class))).thenCallRealMethod();
        DockeredSeleniumStarter.setEnv(environment);
        DockeredSeleniumStarter.readConfigurationFromEnvVariables();
        ZaleniumConfiguration.setEnv(environment);
        ZaleniumConfiguration.readConfigurationFromEnvVariables();
        Assert.assertEquals(amountOfDesiredContainers, ZaleniumConfiguration.getDesiredContainersOnStartup());
        Assert.assertEquals(amountOfMaxContainers, ZaleniumConfiguration.getMaxDockerSeleniumContainers());
        Assert.assertEquals(screenHeight, DockeredSeleniumStarter.getConfiguredScreenSize().getHeight());
        Assert.assertEquals(screenWidth, DockeredSeleniumStarter.getConfiguredScreenSize().getWidth());
        Assert.assertEquals(timeZone.getID(), DockeredSeleniumStarter.getConfiguredTimeZone().getID());
        Assert.assertEquals(seleniumNodeParams, DockeredSeleniumStarter.getSeleniumNodeParameters());
        Assert.assertEquals(browserTimeout, DockeredSeleniumStarter.getBrowserTimeout());
    }

    @Test
    public void noNegativeValuesAreAllowedForStartup() {
        ZaleniumConfiguration.setDesiredContainersOnStartup((-1));
        ZaleniumConfiguration.setMaxDockerSeleniumContainers((-1));
        ZaleniumConfiguration.setTimeToWaitToStart((-10));
        ZaleniumConfiguration.setWaitForAvailableNodes(true);
        ZaleniumConfiguration.setMaxTimesToProcessRequest((-10));
        ZaleniumConfiguration.setCheckContainersInterval(500);
        DockeredSeleniumStarter.setBrowserTimeout((-100));
        DockeredSeleniumStarter.setConfiguredScreenSize(new Dimension((-1), (-1)));
        Assert.assertEquals(DEFAULT_AMOUNT_DESIRED_CONTAINERS, ZaleniumConfiguration.getDesiredContainersOnStartup());
        Assert.assertEquals(DEFAULT_AMOUNT_DOCKER_SELENIUM_CONTAINERS_RUNNING, ZaleniumConfiguration.getMaxDockerSeleniumContainers());
        Assert.assertEquals(DEFAULT_SCREEN_SIZE.getWidth(), DockeredSeleniumStarter.getConfiguredScreenSize().getWidth());
        Assert.assertEquals(DEFAULT_SCREEN_SIZE.getHeight(), DockeredSeleniumStarter.getConfiguredScreenSize().getHeight());
        Assert.assertEquals(DEFAULT_TIME_TO_WAIT_TO_START, ZaleniumConfiguration.getTimeToWaitToStart());
        Assert.assertEquals(DEFAULT_TIMES_TO_PROCESS_REQUEST, ZaleniumConfiguration.getMaxTimesToProcessRequest());
        Assert.assertEquals(DEFAULT_CHECK_CONTAINERS_INTERVAL, ZaleniumConfiguration.getCheckContainersInterval());
        Assert.assertTrue(ZaleniumConfiguration.isWaitForAvailableNodes());
        Assert.assertEquals(DEFAULT_SEL_BROWSER_TIMEOUT_SECS, DockeredSeleniumStarter.getBrowserTimeout());
    }
}

