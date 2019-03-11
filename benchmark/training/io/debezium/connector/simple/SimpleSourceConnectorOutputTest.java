/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.simple;


import ConnectorConfig.CONNECTOR_CLASS_CONFIG;
import ConnectorConfig.NAME_CONFIG;
import ConnectorConfig.TASKS_MAX_CONFIG;
import SimpleSourceConnector.BATCH_COUNT;
import SimpleSourceConnector.RECORD_COUNT_PER_BATCH;
import SimpleSourceConnector.TOPIC_NAME;
import Testing.Files;
import Testing.Print;
import io.debezium.embedded.ConnectorOutputTest;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.Test;

import static java.nio.file.Files.exists;


/**
 * A test case for the {@link SimpleSourceConnector} that is also able to test and verify the behavior of
 * {@link ConnectorOutputTest}.
 *
 * @author Randall Hauch
 */
public class SimpleSourceConnectorOutputTest extends ConnectorOutputTest {
    protected static final String TOPIC_NAME = "some-topic";

    /**
     * Run the connector with no known expected results so that it generates the results.
     *
     * @throws Exception
     * 		if there is an error
     */
    @Test
    public void shouldGenerateExpected() throws Exception {
        int numBatches = 1;
        int numRecordsPerBatch = 10;
        // Testing.Debug.enable();
        Path dir = Files.createTestingPath("simple/gen-expected").toAbsolutePath();
        Files.delete(dir);
        // Create the configuration file in this directory ...
        Properties config = new Properties();
        config.put(NAME_CONFIG, "simple-connector-1");
        config.put(CONNECTOR_CLASS_CONFIG, SimpleSourceConnector.class.getName());
        config.put(TASKS_MAX_CONFIG, "1");
        config.put(BATCH_COUNT, Integer.toString(numBatches));
        config.put(RECORD_COUNT_PER_BATCH, Integer.toString(numRecordsPerBatch));
        config.put(SimpleSourceConnector.TOPIC_NAME, SimpleSourceConnectorOutputTest.TOPIC_NAME);
        writeConfigurationFileWithDefaultName(dir, config);
        Properties env = new Properties();
        env.put(ConnectorOutputTest.ENV_CONNECTOR_TIMEOUT_IN_SECONDS, "1");
        writeEnvironmentFileWithDefaultName(dir, env);
        Path expectedResults = dir.resolve(ConnectorOutputTest.DEFAULT_EXPECTED_RECORDS_FILENAME);
        assertThat(exists(expectedResults)).isFalse();
        // Run the connector to generate the results ...
        runConnector("gen-expected", dir);
        // Check that the expected records now exist ...
        assertExpectedRecords(expectedResults, numBatches, numRecordsPerBatch);
        // Append a stop command to the expected results ...
        appendStop(expectedResults);
        // Run the connector again (with fresh offsets) to read the expected results ...
        cleanOffsetStorage();
        runConnector("gen-expected", dir);
    }

    /**
     * Run the connector with connector configuration and expected results files, which are read in one step.
     */
    @Test
    public void shouldRunConnectorFromFilesInOneStep() {
        runConnector("simple-test-a", "src/test/resources/simple/test/a");
    }

    /**
     * Run the connector with connector configuration and expected results files, which are read in two steps.
     */
    @Test
    public void shouldRunConnectorFromFilesInTwoSteps() {
        runConnector("simple-test-b", "src/test/resources/simple/test/b");
    }

    /**
     * Run the connector with connector configuration and expected results files, but find a mismatch in the results.
     */
    @Test(expected = AssertionError.class)
    public void shouldRunConnectorFromFilesAndFindMismatch() {
        // Testing.Debug.enable();
        Print.disable();
        runConnector("simple-test-c", "src/test/resources/simple/test/c");
    }

    /**
     * Run the connector with connector configuration and expected results files, which are read in one step.
     * The connector includes timestamps that vary with each run, and the expected results have no no timestamps.
     * However, this test filters out the timestamps from the matching logic.
     */
    @Test
    public void shouldRunConnectorFromFilesInOneStepWithTimestamps() {
        // Testing.Debug.enable();
        runConnector("simple-test-d", "src/test/resources/simple/test/d");
    }
}

