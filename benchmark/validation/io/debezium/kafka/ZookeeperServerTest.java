/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.kafka;


import io.debezium.util.Testing;
import java.io.File;
import java.util.function.Consumer;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class ZookeeperServerTest {
    private ZookeeperServer server;

    private File dataDir;

    @Test
    public void shouldStartServerAndRemoveData() throws Exception {
        Testing.debug("Running 1");
        server.startup();
        server.onEachDirectory(this::assertValidDataDirectory);
        server.shutdown(true);
        server.onEachDirectory(this::assertDoesNotExist);
    }

    @Test
    public void shouldStartServerAndLeaveData() throws Exception {
        Testing.debug("Running 2");
        server.startup();
        server.onEachDirectory(this::assertValidDataDirectory);
        server.shutdown(false);
        server.onEachDirectory(this::assertValidDataDirectory);
    }
}

