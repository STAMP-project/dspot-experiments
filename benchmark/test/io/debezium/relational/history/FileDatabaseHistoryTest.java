/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.relational.history;


import java.nio.file.Path;
import org.junit.Test;

import static io.debezium.util.Testing.Files.createTestingPath;


/**
 *
 *
 * @author Randall Hauch
 */
public class FileDatabaseHistoryTest extends AbstractDatabaseHistoryTest {
    private static final Path TEST_FILE_PATH = createTestingPath("dbHistory.log");

    @Override
    @Test
    public void shouldRecordChangesAndRecoverToVariousPoints() {
        super.shouldRecordChangesAndRecoverToVariousPoints();
    }
}

