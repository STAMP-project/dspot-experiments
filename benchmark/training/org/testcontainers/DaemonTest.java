package org.testcontainers;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test forks a new JVM, otherwise it's not possible to reliably diff the threads
 */
public class DaemonTest {
    @Test
    public void testThatAllThreadsAreDaemons() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(new File(System.getProperty("java.home")).toPath().resolve("bin").resolve("java").toString(), "-ea", "-classpath", System.getProperty("java.class.path"), DaemonTest.class.getCanonicalName());
        Assert.assertEquals(0, processBuilder.inheritIO().start().waitFor());
    }
}

