package de.zalando.ep.zalenium.prometheus;


import CollectorRegistry.defaultRegistry;
import de.zalando.ep.zalenium.container.ContainerCreationStatus;
import de.zalando.ep.zalenium.proxy.AutoStartProxySet.ContainerStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class ContainerStatusCollectorExportsTest {
    @Test
    public void testEmptyMapBothValuesZero() {
        Map<ContainerCreationStatus, ContainerStatus> map = new HashMap<ContainerCreationStatus, ContainerStatus>();
        new ContainerStatusCollectorExports(map).register(defaultRegistry);
        Double startingValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "starting" });
        Double runningValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "running" });
        MatcherAssert.assertThat(startingValue, CoreMatchers.equalTo(0.0));
        MatcherAssert.assertThat(runningValue, CoreMatchers.equalTo(0.0));
    }

    @Test
    public void testIncrementChanges() {
        // Given an empty map
        Map<ContainerCreationStatus, ContainerStatus> map = new HashMap<ContainerCreationStatus, ContainerStatus>();
        new ContainerStatusCollectorExports(map).register(defaultRegistry);
        // We get an empty values for both states
        Double startingValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "starting" });
        Double runningValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "running" });
        MatcherAssert.assertThat(startingValue, CoreMatchers.equalTo(0.0));
        MatcherAssert.assertThat(runningValue, CoreMatchers.equalTo(0.0));
        // After we add 2 starting containers
        ContainerStatus container1 = new ContainerStatus("123", 0L);
        ContainerStatus container2 = new ContainerStatus("1234", 0L);
        map.put(Mockito.mock(ContainerCreationStatus.class), container1);
        map.put(Mockito.mock(ContainerCreationStatus.class), container2);
        // We expect 2 starting and 0 running
        startingValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "starting" });
        runningValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "running" });
        MatcherAssert.assertThat(startingValue, CoreMatchers.equalTo(2.0));
        MatcherAssert.assertThat(runningValue, CoreMatchers.equalTo(0.0));
        // Once we add a started time
        container1.setTimeStarted(Optional.of(0L));
        container2.setTimeStarted(Optional.of(0L));
        // We expect 0 starting and 2 running
        startingValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "starting" });
        runningValue = defaultRegistry.getSampleValue("selenium_containers", new String[]{ "state" }, new String[]{ "running" });
        MatcherAssert.assertThat(startingValue, CoreMatchers.equalTo(0.0));
        MatcherAssert.assertThat(runningValue, CoreMatchers.equalTo(2.0));
    }
}

