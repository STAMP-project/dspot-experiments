/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.embedded;


import EmbeddedEngine.CONNECTOR_CLASS;
import EmbeddedEngine.ENGINE_NAME;
import EmbeddedEngine.OFFSET_FLUSH_INTERVAL_MS;
import StandaloneConfig.OFFSET_STORAGE_FILE_FILENAME_CONFIG;
import Testing.Files;
import io.debezium.config.Configuration;
import io.debezium.doc.FixFor;
import io.debezium.util.LoggingContext;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.file.FileStreamSourceConnector;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class EmbeddedEngineTest extends AbstractConnectorTest {
    private static final int NUMBER_OF_LINES = 10;

    private static final Path TEST_FILE_PATH = Files.createTestingPath("file-connector-input.txt").toAbsolutePath();

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private File inputFile;

    private int nextConsumedLineNumber;

    private int linesAdded;

    private Configuration connectorConfig;

    @Test
    public void shouldStartAndUseFileConnectorUsingMemoryOffsetStorage() throws Exception {
        // Add initial content to the file ...
        appendLinesToSource(EmbeddedEngineTest.NUMBER_OF_LINES);
        // Start the connector ...
        start(FileStreamSourceConnector.class, connectorConfig);
        // Verify the first 10 lines were found ...
        consumeLines(EmbeddedEngineTest.NUMBER_OF_LINES);
        assertNoRecordsToConsume();
        for (int i = 1; i != 5; ++i) {
            // Add a few more lines, and then verify they are consumed ...
            appendLinesToSource(EmbeddedEngineTest.NUMBER_OF_LINES);
            consumeLines(EmbeddedEngineTest.NUMBER_OF_LINES);
            assertNoRecordsToConsume();
        }
        // Stop the connector ..
        stopConnector();
        // Add several more lines ...
        appendLinesToSource(EmbeddedEngineTest.NUMBER_OF_LINES);
        assertNoRecordsToConsume();
        // Start the connector again ...
        start(FileStreamSourceConnector.class, connectorConfig);
        // Verify that we see the correct line number, meaning that offsets were recorded correctly ...
        consumeLines(EmbeddedEngineTest.NUMBER_OF_LINES);
        assertNoRecordsToConsume();
    }

    @Test
    @FixFor("DBZ-1080")
    public void shouldWorkToUseCustomChangeConsumer() throws Exception {
        // Add initial content to the file ...
        appendLinesToSource(EmbeddedEngineTest.NUMBER_OF_LINES);
        Configuration config = Configuration.copy(connectorConfig).with(ENGINE_NAME, "testing-connector").with(CONNECTOR_CLASS, FileStreamSourceConnector.class).with(OFFSET_STORAGE_FILE_FILENAME_CONFIG, AbstractConnectorTest.OFFSET_STORE_PATH).with(OFFSET_FLUSH_INTERVAL_MS, 0).build();
        CountDownLatch firstLatch = new CountDownLatch(1);
        CountDownLatch allLatch = new CountDownLatch(6);
        // create an engine with our custom class
        engine = EmbeddedEngine.create().using(config).notifying(( records, committer) -> {
            assertThat(records.size()).isGreaterThanOrEqualTo(NUMBER_OF_LINES);
            Integer groupCount = (records.size()) / (NUMBER_OF_LINES);
            for (SourceRecord r : records) {
                committer.markProcessed(r);
            }
            committer.markBatchFinished();
            firstLatch.countDown();
            for (int i = 0; i < groupCount; i++) {
                allLatch.countDown();
            }
        }).using(this.getClass().getClassLoader()).build();
        ExecutorService exec = Executors.newFixedThreadPool(1);
        exec.execute(() -> {
            LoggingContext.forConnector(getClass().getSimpleName(), "", "engine");
            engine.run();
        });
        firstLatch.await(5000, TimeUnit.MILLISECONDS);
        assertThat(firstLatch.getCount()).isEqualTo(0);
        for (int i = 0; i < 5; i++) {
            // Add a few more lines, and then verify they are consumed ...
            appendLinesToSource(EmbeddedEngineTest.NUMBER_OF_LINES);
            Thread.sleep(10);
        }
        allLatch.await(5000, TimeUnit.MILLISECONDS);
        assertThat(allLatch.getCount()).isEqualTo(0);
        // Stop the connector ...
        stopConnector();
    }
}

