package org.testcontainers.utility;


import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;


public class DirectoryTarResourceTest {
    @Test
    public void simpleRecursiveFileTest() throws TimeoutException {
        WaitingConsumer wait = new WaitingConsumer();
        final ToStringConsumer toString = new ToStringConsumer();
        // 'src' is expected to be the project base directory, so all source code/resources should be copied in
        File directory = new File("src");
        GenericContainer container = new GenericContainer(new ImageFromDockerfile().withDockerfileFromBuilder(( builder) -> builder.from("alpine:3.3").copy("/tmp/foo", "/foo").cmd("cat /foo/test/resources/test-recursive-file.txt").build()).withFileFromFile("/tmp/foo", directory)).withStartupCheckStrategy(new OneShotStartupCheckStrategy()).withLogConsumer(wait.andThen(toString));
        container.start();
        wait.waitUntilEnd(60, TimeUnit.SECONDS);
        final String results = toString.toUtf8String();
        assertTrue("The container has a file that was copied in via a recursive copy", results.contains("Used for DirectoryTarResourceTest"));
    }

    @Test
    public void simpleRecursiveFileWithPermissionTest() throws TimeoutException {
        WaitingConsumer wait = new WaitingConsumer();
        final ToStringConsumer toString = new ToStringConsumer();
        GenericContainer container = new GenericContainer(new ImageFromDockerfile().withDockerfileFromBuilder(( builder) -> builder.from("alpine:3.3").copy("/tmp/foo", "/foo").cmd("ls", "-al", "/").build()).withFileFromFile("/tmp/foo", new File("/mappable-resource/test-resource.txt"), 492)).withStartupCheckStrategy(new OneShotStartupCheckStrategy()).withLogConsumer(wait.andThen(toString));
        container.start();
        wait.waitUntilEnd(60, TimeUnit.SECONDS);
        String listing = toString.toUtf8String();
        assertThat("Listing shows that file is copied with mode requested.", Arrays.asList(listing.split("\\n")), DirectoryTarResourceTest.exactlyNItems(1, CoreMatchers.allOf(CoreMatchers.containsString("-rwxr-xr--"), CoreMatchers.containsString("foo"))));
    }

    @Test
    public void simpleRecursiveClasspathResourceTest() throws TimeoutException {
        // This test combines the copying of classpath resources from JAR files with the recursive TAR approach, to allow JARed classpath resources to be copied in to an image
        WaitingConsumer wait = new WaitingConsumer();
        final ToStringConsumer toString = new ToStringConsumer();
        GenericContainer container = // here we use /org/junit as a directory that really should exist on the classpath
        new GenericContainer(new ImageFromDockerfile().withDockerfileFromBuilder(( builder) -> builder.from("alpine:3.3").copy("/tmp/foo", "/foo").cmd("ls -lRt /foo").build()).withFileFromClasspath("/tmp/foo", "/recursive/dir")).withStartupCheckStrategy(new OneShotStartupCheckStrategy()).withLogConsumer(wait.andThen(toString));
        container.start();
        wait.waitUntilEnd(60, TimeUnit.SECONDS);
        final String results = toString.toUtf8String();
        // ExternalResource.class is known to exist in a subdirectory of /org/junit so should be successfully copied in
        assertTrue("The container has a file that was copied in via a recursive copy from a JAR resource", results.contains("content.txt"));
    }
}

