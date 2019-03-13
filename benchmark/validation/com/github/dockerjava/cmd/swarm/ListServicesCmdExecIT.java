package com.github.dockerjava.cmd.swarm;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateServiceResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ContainerSpec;
import com.github.dockerjava.api.model.Service;
import com.github.dockerjava.api.model.ServiceModeConfig;
import com.github.dockerjava.api.model.ServiceReplicatedModeOptions;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.api.model.TaskSpec;
import com.github.dockerjava.junit.DockerRule;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListServicesCmdExecIT extends SwarmCmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(CreateServiceCmdExecIT.class);

    private static final String SERVICE_NAME = "inspect_service";

    private static final String LABEL_KEY = "com.github.dockerjava.usage";

    private static final String LABEL_VALUE = "test";

    @Test
    public void testListServices() throws DockerException {
        DockerClient docker1 = startDockerInDocker();
        docker1.initializeSwarmCmd(new SwarmSpec()).exec();
        Map<String, String> serviceLabels = Collections.singletonMap(ListServicesCmdExecIT.LABEL_KEY, ListServicesCmdExecIT.LABEL_VALUE);
        CreateServiceResponse response = docker1.createServiceCmd(new ServiceSpec().withLabels(serviceLabels).withName(ListServicesCmdExecIT.SERVICE_NAME).withMode(new ServiceModeConfig().withReplicated(new ServiceReplicatedModeOptions().withReplicas(1))).withTaskTemplate(new TaskSpec().withContainerSpec(new ContainerSpec().withImage(DockerRule.DEFAULT_IMAGE)))).exec();
        String serviceId = response.getId();
        // filtering with service id
        List<Service> services = docker1.listServicesCmd().withIdFilter(Collections.singletonList(serviceId)).exec();
        MatcherAssert.assertThat(services, Matchers.hasSize(1));
        // filtering with service name
        services = docker1.listServicesCmd().withNameFilter(Collections.singletonList(ListServicesCmdExecIT.SERVICE_NAME)).exec();
        MatcherAssert.assertThat(services, Matchers.hasSize(1));
        // filter labels
        services = docker1.listServicesCmd().withLabelFilter(serviceLabels).exec();
        MatcherAssert.assertThat(services, Matchers.hasSize(1));
        docker1.removeServiceCmd(ListServicesCmdExecIT.SERVICE_NAME).exec();
    }
}

