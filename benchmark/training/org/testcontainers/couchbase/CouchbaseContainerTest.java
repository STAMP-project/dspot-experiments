package org.testcontainers.couchbase;


import org.junit.Assert;
import org.junit.Test;

import static CouchbaseContainer.DOCKER_IMAGE_NAME;
import static CouchbaseContainer.VERSION;


public class CouchbaseContainerTest {
    @Test
    public void shouldUseCorrectDockerImage() {
        CouchbaseContainer couchbaseContainer = new CouchbaseContainer().withClusterAdmin("admin", "foobar");
        Assert.assertEquals(((DOCKER_IMAGE_NAME) + (VERSION)), couchbaseContainer.getDockerImageName());
    }

    @Test
    public void shouldStopWithoutThrowingException() {
        CouchbaseContainer couchbaseContainer = new CouchbaseContainer();
        couchbaseContainer.start();
        couchbaseContainer.stop();
    }
}

