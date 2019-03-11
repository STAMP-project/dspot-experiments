package com.github.dockerjava.cmd;


import ch.lambdaj.Lambda;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.junit.DockerAssume;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListContainersCmdIT extends CmdIT {
    private static final Logger LOG = LoggerFactory.getLogger(ListContainersCmdIT.class);

    private static final String DEFAULT_IMAGE = "busybox";

    private Map<String, String> testLabel;

    @Test
    public void testListContainers() throws Exception {
        List<Container> containers = dockerRule.getClient().listContainersCmd().withLabelFilter(testLabel).withShowAll(true).exec();
        MatcherAssert.assertThat(containers, Matchers.notNullValue());
        ListContainersCmdIT.LOG.info("Container List: {}", containers);
        int size = containers.size();
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).withCmd("echo").exec();
        MatcherAssert.assertThat(container1.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getConfig().getImage(), Matchers.is(Matchers.equalTo(ListContainersCmdIT.DEFAULT_IMAGE)));
        dockerRule.getClient().startContainerCmd(container1.getId()).exec();
        ListContainersCmdIT.LOG.info(("container id: " + (container1.getId())));
        List<Container> containers2 = dockerRule.getClient().listContainersCmd().withLabelFilter(testLabel).withShowAll(true).exec();
        for (Container container : containers2) {
            ListContainersCmdIT.LOG.info(((("listContainer: id=" + (container.getId())) + " image=") + (container.getImage())));
        }
        MatcherAssert.assertThat((size + 1), Matchers.is(Matchers.equalTo(containers2.size())));
        Matcher matcher = Matchers.hasItem(hasField("id", Matchers.startsWith(container1.getId())));
        MatcherAssert.assertThat(containers2, matcher);
        List<Container> filteredContainers = Lambda.filter(hasField("id", Matchers.startsWith(container1.getId())), containers2);
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(Matchers.equalTo(1)));
        for (Container container : filteredContainers) {
            ListContainersCmdIT.LOG.info(("filteredContainer: " + container));
        }
        Container container2 = filteredContainers.get(0);
        MatcherAssert.assertThat(container2.getCommand(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(container2.getImage(), Matchers.startsWith(ListContainersCmdIT.DEFAULT_IMAGE));
    }

    @Test
    public void testListContainersWithLabelsFilter() throws Exception {
        // list with filter by Map label
        dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withCmd("echo").withLabels(testLabel).exec();
        List<Container> filteredContainersByMap = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(testLabel).exec();
        MatcherAssert.assertThat(filteredContainersByMap.size(), Matchers.is(1));
        Container container3 = filteredContainersByMap.get(0);
        MatcherAssert.assertThat(container3.getCommand(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(container3.getImage(), Matchers.startsWith(ListContainersCmdIT.DEFAULT_IMAGE));
        // List by string label
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(Collections.singletonList(("test=" + (testLabel.get("test"))))).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        container3 = filteredContainers.get(0);
        MatcherAssert.assertThat(container3.getCommand(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(container3.getImage(), Matchers.startsWith(ListContainersCmdIT.DEFAULT_IMAGE));
        Assert.assertEquals(testLabel.get("test"), container3.getLabels().get("test"));
    }

    @Test
    public void testNameFilter() throws Exception {
        String testUUID = testLabel.get("test");
        String id1;
        String id2;
        id1 = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).withName(("nameFilterTest1-" + testUUID)).exec().getId();
        id2 = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).withName(("nameFilterTest2-" + testUUID)).exec().getId();
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withNameFilter(Arrays.asList(("nameFilterTest1-" + testUUID), ("nameFilterTest2-" + testUUID))).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(2));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.isOneOf(id1, id2));
        MatcherAssert.assertThat(filteredContainers.get(1).getId(), Matchers.isOneOf(id1, id2));
    }

    @Test
    public void testIdsFilter() throws Exception {
        String id1;
        String id2;
        id1 = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).exec().getId();
        id2 = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).exec().getId();
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withIdFilter(Arrays.asList(id1, id2)).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(2));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.isOneOf(id1, id2));
        MatcherAssert.assertThat(filteredContainers.get(1).getId(), Matchers.isOneOf(id1, id2));
    }

    @Test
    public void testStatusFilter() throws Exception {
        String id1;
        String id2;
        id1 = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withCmd("sh", "-c", "sleep 99999").withLabels(testLabel).exec().getId();
        id2 = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withCmd("sh", "-c", "sleep 99999").withLabels(testLabel).exec().getId();
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(testLabel).withStatusFilter(Collections.singletonList("created")).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(2));
        MatcherAssert.assertThat(filteredContainers.get(1).getId(), Matchers.isOneOf(id1, id2));
        dockerRule.getClient().startContainerCmd(id1).exec();
        filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(testLabel).withStatusFilter(Collections.singletonList("running")).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.is(id1));
        dockerRule.getClient().pauseContainerCmd(id1).exec();
        filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(testLabel).withStatusFilter(Collections.singletonList("paused")).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.is(id1));
        dockerRule.getClient().unpauseContainerCmd(id1).exec();
        dockerRule.getClient().stopContainerCmd(id1).exec();
        filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(testLabel).withStatusFilter(Collections.singletonList("exited")).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.is(id1));
    }

    @Test
    public void testVolumeFilter() throws Exception {
        String id;
        dockerRule.getClient().createVolumeCmd().withName("TestFilterVolume").withDriver("local").exec();
        id = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).withHostConfig(HostConfig.newHostConfig().withBinds(new com.github.dockerjava.api.model.Bind("TestFilterVolume", new Volume("/test")))).exec().getId();
        dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).exec();
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(testLabel).withVolumeFilter(Collections.singletonList("TestFilterVolume")).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.is(id));
    }

    @Test
    public void testNetworkFilter() throws Exception {
        String id;
        dockerRule.getClient().createNetworkCmd().withName("TestFilterNetwork").withDriver("bridge").exec();
        id = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).withHostConfig(HostConfig.newHostConfig().withNetworkMode("TestFilterNetwork")).exec().getId();
        dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).exec();
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withShowAll(true).withLabelFilter(testLabel).withNetworkFilter(Collections.singletonList("TestFilterNetwork")).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.is(id));
    }

    @Test
    public void testAncestorFilter() throws Exception {
        // ancestor filters are broken in swarm
        // https://github.com/docker/swarm/issues/1716
        DockerAssume.assumeNotSwarm(dockerRule.getClient());
        dockerRule.getClient().pullImageCmd("busybox").withTag("1.24").exec(new PullImageResultCallback()).awaitCompletion();
        dockerRule.getClient().createContainerCmd("busybox:1.24").withLabels(testLabel).exec();
        String imageId = dockerRule.getClient().inspectImageCmd(ListContainersCmdIT.DEFAULT_IMAGE).exec().getId();
        String id = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).exec().getId();
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withLabelFilter(testLabel).withShowAll(true).withAncestorFilter(Collections.singletonList(imageId)).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.is(id));
    }

    @Test
    public void testExitedFilter() throws Exception {
        dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).exec();
        String id = dockerRule.getClient().createContainerCmd(ListContainersCmdIT.DEFAULT_IMAGE).withLabels(testLabel).withCmd("sh", "-c", "exit 42").exec().getId();
        dockerRule.getClient().startContainerCmd(id).exec();
        Integer status = dockerRule.getClient().waitContainerCmd(id).exec(new WaitContainerResultCallback()).awaitStatusCode();
        MatcherAssert.assertThat(status, Matchers.is(42));
        List<Container> filteredContainers = dockerRule.getClient().listContainersCmd().withLabelFilter(testLabel).withShowAll(true).withExitedFilter(42).exec();
        MatcherAssert.assertThat(filteredContainers.size(), Matchers.is(1));
        MatcherAssert.assertThat(filteredContainers.get(0).getId(), Matchers.is(id));
    }
}

