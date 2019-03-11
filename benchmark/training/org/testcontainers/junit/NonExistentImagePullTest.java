package org.testcontainers.junit;


import org.junit.Test;
import org.testcontainers.containers.ContainerFetchException;
import org.testcontainers.containers.GenericContainer;


/**
 * Created by rnorth on 20/03/2016.
 */
public class NonExistentImagePullTest {
    @Test(timeout = 60000L)
    public void pullingNonExistentImageFailsGracefully() {
        assertThrows("Pulling a nonexistent container will cause an exception to be thrown", ContainerFetchException.class, () -> {
            return new GenericContainer("richnorth/nonexistent:latest");
        });
    }
}

