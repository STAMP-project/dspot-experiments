package com.baeldung.SpringCloudTaskFinal;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.task.launcher.TaskLaunchRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringCloudTaskSinkApplication.class)
public class SpringCloudTaskSinkApplicationIntegrationTest {
    @Autowired
    ApplicationContext context;

    @Autowired
    private Sink sink;

    @Test
    public void testTaskLaunch() throws IOException {
        TaskLauncher taskLauncher = context.getBean(TaskLauncher.class);
        Map<String, String> prop = new HashMap<String, String>();
        prop.put("server.port", "0");
        TaskLaunchRequest request = new TaskLaunchRequest(("maven://org.springframework.cloud.task.app:" + "timestamp-task:jar:1.0.1.RELEASE"), null, prop, null, null);
        GenericMessage<TaskLaunchRequest> message = new GenericMessage<TaskLaunchRequest>(request);
        this.sink.input().send(message);
        ArgumentCaptor<AppDeploymentRequest> deploymentRequest = ArgumentCaptor.forClass(AppDeploymentRequest.class);
        Mockito.verify(taskLauncher).launch(deploymentRequest.capture());
        AppDeploymentRequest actualRequest = deploymentRequest.getValue();
        // Verifying the co-ordinate of launched Task here.
        Assert.assertTrue(actualRequest.getCommandlineArguments().isEmpty());
        Assert.assertEquals("0", actualRequest.getDefinition().getProperties().get("server.port"));
        Assert.assertTrue(actualRequest.getResource().toString().contains("org.springframework.cloud.task.app:timestamp-task:jar:1.0.1.RELEASE"));
    }
}

