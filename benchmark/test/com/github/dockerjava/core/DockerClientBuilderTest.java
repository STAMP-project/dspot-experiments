package com.github.dockerjava.core;


import com.github.dockerjava.api.command.DockerCmdExecFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class DockerClientBuilderTest {
    // Amount of instances created in test
    private static final int AMOUNT = 100;

    @Test
    public void testConcurrentClientBuilding() throws Exception {
        // we use it to check instance uniqueness
        final Set<DockerCmdExecFactory> instances = Collections.synchronizedSet(new HashSet<DockerCmdExecFactory>());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DockerCmdExecFactory factory = DockerClientBuilder.getDefaultDockerCmdExecFactory();
                // factory created
                Assert.assertNotNull(factory);
                // and is unique
                Assert.assertFalse(instances.contains(factory));
                instances.add(factory);
            }
        };
        DockerClientBuilderTest.parallel(DockerClientBuilderTest.AMOUNT, runnable);
        // set contains all required unique instances
        Assert.assertEquals(instances.size(), DockerClientBuilderTest.AMOUNT);
    }

    private static class ExceptionListener {
        private Throwable exception;

        private synchronized void onException(Throwable e) {
            exception = e;
        }

        private synchronized Throwable getException() {
            return exception;
        }
    }
}

