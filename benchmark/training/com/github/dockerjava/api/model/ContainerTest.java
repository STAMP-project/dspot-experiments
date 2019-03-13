package com.github.dockerjava.api.model;


import RemoteApiVersion.VERSION_1_22;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.dockerjava.test.serdes.JSONSamples;
import java.io.IOException;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class ContainerTest {
    @Test
    public void serderJson1() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, Container.class);
        final List<Container> containers = JSONSamples.testRoundTrip(VERSION_1_22, "containers/json/filter1.json", type);
        MatcherAssert.assertThat(containers.size(), IsEqual.equalTo(1));
        final Container container = containers.get(0);
        MatcherAssert.assertThat(container.getImageId(), IsEqual.equalTo("sha256:0cb40641836c461bc97c793971d84d758371ed682042457523e4ae701efe7ec9"));
        MatcherAssert.assertThat(container.getSizeRootFs(), IsEqual.equalTo(1113554L));
        final ContainerHostConfig hostConfig = container.getHostConfig();
        MatcherAssert.assertThat(hostConfig, Matchers.notNullValue());
        MatcherAssert.assertThat(hostConfig.getNetworkMode(), IsEqual.equalTo("default"));
    }
}

