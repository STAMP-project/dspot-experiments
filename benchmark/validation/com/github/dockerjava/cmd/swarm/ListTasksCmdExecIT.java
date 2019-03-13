package com.github.dockerjava.cmd.swarm;


import TaskState.RUNNING;
import com.github.dockerjava.api.command.CreateServiceResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ContainerSpec;
import com.github.dockerjava.api.model.ServiceModeConfig;
import com.github.dockerjava.api.model.ServiceReplicatedModeOptions;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.api.model.Task;
import com.github.dockerjava.api.model.TaskSpec;
import com.github.dockerjava.cmd.CmdIT;
import com.github.dockerjava.junit.DockerRule;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListTasksCmdExecIT extends SwarmCmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(CreateServiceCmdExecIT.class);

    private static final String SERVICE_NAME = "inspect_task";

    private static final String TASK_LABEL_KEY = "com.github.dockerjava.usage";

    private static final String TASK_LABEL_VALUE = "test";

    @Test
    public void testListTasks() throws DockerException {
        dockerRule.getClient().initializeSwarmCmd(new SwarmSpec()).withListenAddr("127.0.0.1").withAdvertiseAddr("127.0.0.1").exec();
        Map<String, String> taskLabels = Collections.singletonMap(ListTasksCmdExecIT.TASK_LABEL_KEY, ListTasksCmdExecIT.TASK_LABEL_VALUE);
        CreateServiceResponse response = dockerRule.getClient().createServiceCmd(new ServiceSpec().withName(ListTasksCmdExecIT.SERVICE_NAME).withMode(new ServiceModeConfig().withReplicated(new ServiceReplicatedModeOptions().withReplicas(2))).withTaskTemplate(new TaskSpec().withContainerSpec(new ContainerSpec().withImage(DockerRule.DEFAULT_IMAGE))).withLabels(taskLabels)).exec();
        String serviceId = response.getId();
        // filtering with service id
        List<Task> tasks = dockerRule.getClient().listTasksCmd().withServiceFilter(serviceId).exec();
        MatcherAssert.assertThat(tasks, Matchers.hasSize(2));
        String taskId = tasks.get(0).getId();
        String secondId = tasks.get(1).getId();
        // filtering with unique id
        tasks = dockerRule.getClient().listTasksCmd().withIdFilter(taskId).exec();
        MatcherAssert.assertThat(tasks, Matchers.hasSize(1));
        MatcherAssert.assertThat(tasks.get(0).getId(), Matchers.is(taskId));
        // filtering with multiple id
        tasks = dockerRule.getClient().listTasksCmd().withIdFilter(secondId, taskId).exec();
        // filtering node id
        String nodeId = tasks.get(0).getNodeId();
        tasks = dockerRule.getClient().listTasksCmd().withNodeFilter(nodeId).exec();
        MatcherAssert.assertThat(tasks.get(0).getNodeId(), Matchers.is(nodeId));
        // filtering with state
        tasks = dockerRule.getClient().listTasksCmd().withStateFilter(RUNNING).exec();
        MatcherAssert.assertThat(tasks, Matchers.hasSize(2));
        // filter labels
        tasks = dockerRule.getClient().listTasksCmd().withLabelFilter(taskLabels).exec();
        MatcherAssert.assertThat(tasks, Matchers.hasSize(2));
        tasks = dockerRule.getClient().listTasksCmd().withLabelFilter((((ListTasksCmdExecIT.TASK_LABEL_KEY) + "=") + (ListTasksCmdExecIT.TASK_LABEL_VALUE))).exec();
        MatcherAssert.assertThat(tasks, Matchers.hasSize(2));
        dockerRule.getClient().removeServiceCmd(ListTasksCmdExecIT.SERVICE_NAME).exec();
    }
}

