package com.github.dockerjava.cmd.swarm;


import ServiceRestartCondition.NONE;
import TaskState.SHUTDOWN;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.ContainerSpec;
import com.github.dockerjava.api.model.ServiceModeConfig;
import com.github.dockerjava.api.model.ServiceReplicatedModeOptions;
import com.github.dockerjava.api.model.ServiceRestartPolicy;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.api.model.Task;
import com.github.dockerjava.api.model.TaskSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class LogSwarmObjectIT extends SwarmCmdIT {
    @Test
    public void testLogsCmd() throws IOException, InterruptedException {
        String snippet = "hello world";
        DockerClient docker1 = startDockerInDocker();
        docker1.initializeSwarmCmd(new SwarmSpec()).exec();
        TaskSpec taskSpec = new TaskSpec().withContainerSpec(new ContainerSpec().withImage("busybox").withCommand(Arrays.asList("echo", snippet))).withRestartPolicy(new ServiceRestartPolicy().withCondition(NONE));
        ServiceSpec serviceSpec = new ServiceSpec().withMode(new ServiceModeConfig().withReplicated(new ServiceReplicatedModeOptions().withReplicas(1))).withTaskTemplate(taskSpec).withName("log-worker");
        String serviceId = docker1.createServiceCmd(serviceSpec).exec().getId();
        int since = ((int) (System.currentTimeMillis())) / 1000;
        // wait the service to end
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks = docker1.listTasksCmd().withServiceFilter(serviceId).withStateFilter(SHUTDOWN).exec();
            if ((tasks.size()) == 1) {
                break;
            } else {
                TimeUnit.SECONDS.sleep(3);
            }
        }
        Assert.assertThat(tasks.size(), Is.is(1));
        String taskId = tasks.get(0).getId();
        // check service log
        validateLog(docker1.logServiceCmd(serviceId).withStdout(true), snippet);
        // check task log
        validateLog(docker1.logTaskCmd(taskId).withStdout(true), snippet);
        // check details/context
        validateLog(docker1.logServiceCmd(serviceId).withStdout(true).withDetails(true), ("com.docker.swarm.service.id=" + serviceId));
        validateLog(docker1.logTaskCmd(taskId).withStdout(true).withDetails(true), ("com.docker.swarm.service.id=" + serviceId));
        // check since
        validateLog(docker1.logServiceCmd(serviceId).withStdout(true).withSince(since), snippet);
        validateLog(docker1.logTaskCmd(taskId).withStdout(true).withSince(since), snippet);
        docker1.removeServiceCmd(serviceId).exec();
    }
}

