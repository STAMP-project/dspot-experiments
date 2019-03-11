/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.realtime.firehose;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.Row;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.MapInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.metadata.MetadataStorageConnectorConfig;
import org.apache.druid.metadata.SQLFirehoseDatabaseConnector;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.segment.TestHelper;
import org.junit.Rule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;


public class SqlFirehoseFactoryTest {
    private static final List<File> FIREHOSE_TMP_DIRS = new ArrayList<>();

    private static File TEST_DIR;

    private final String TABLE_NAME_1 = "FOOS_TABLE_1";

    private final String TABLE_NAME_2 = "FOOS_TABLE_2";

    private final List<String> SQLLIST1 = ImmutableList.of("SELECT timestamp,a,b FROM FOOS_TABLE_1");

    private final List<String> SQLLIST2 = ImmutableList.of("SELECT timestamp,a,b FROM FOOS_TABLE_1", "SELECT timestamp,a,b FROM FOOS_TABLE_2");

    @Rule
    public final TestDerbyConnector.DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    private final ObjectMapper mapper = TestHelper.makeSmileMapper();

    private final MapInputRowParser parser = new MapInputRowParser(new org.apache.druid.data.input.impl.TimeAndDimsParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("timestamp", "a", "b")), new ArrayList(), new ArrayList())));

    private TestDerbyConnector derbyConnector;

    private SqlFirehoseFactoryTest.TestDerbyFirehoseConnector derbyFirehoseConnector;

    @Test
    public void testWithoutCacheAndFetch() throws Exception {
        createAndUpdateTable(TABLE_NAME_1);
        final SqlFirehoseFactory factory = new SqlFirehoseFactory(SQLLIST1, 0L, 0L, 0L, 0L, true, derbyFirehoseConnector, mapper);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = createFirehoseTmpDir("testWithoutCacheAndFetch");
        try (Firehose firehose = factory.connect(parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        assertResult(rows, SQLLIST1);
        assertNumRemainingCacheFiles(firehoseTmpDir, 0);
        dropTable(TABLE_NAME_1);
    }

    @Test
    public void testWithoutCache() throws IOException {
        createAndUpdateTable(TABLE_NAME_1);
        final SqlFirehoseFactory factory = new SqlFirehoseFactory(SQLLIST1, 0L, null, null, null, true, derbyFirehoseConnector, mapper);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = createFirehoseTmpDir("testWithoutCache");
        try (Firehose firehose = factory.connect(parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        assertResult(rows, SQLLIST1);
        assertNumRemainingCacheFiles(firehoseTmpDir, 0);
        dropTable(TABLE_NAME_1);
    }

    @Test
    public void testWithCacheAndFetch() throws IOException {
        createAndUpdateTable(TABLE_NAME_1);
        createAndUpdateTable(TABLE_NAME_2);
        final SqlFirehoseFactory factory = new SqlFirehoseFactory(SQLLIST2, null, null, 0L, null, true, derbyFirehoseConnector, mapper);
        final List<Row> rows = new ArrayList<>();
        final File firehoseTmpDir = createFirehoseTmpDir("testWithCacheAndFetch");
        try (Firehose firehose = factory.connect(parser, firehoseTmpDir)) {
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
        }
        assertResult(rows, SQLLIST2);
        assertNumRemainingCacheFiles(firehoseTmpDir, 2);
        dropTable(TABLE_NAME_1);
        dropTable(TABLE_NAME_2);
    }

    private static class TestDerbyFirehoseConnector extends SQLFirehoseDatabaseConnector {
        private final DBI dbi;

        private TestDerbyFirehoseConnector(MetadataStorageConnectorConfig metadataStorageConnectorConfig, DBI dbi) {
            final BasicDataSource datasource = getDatasource(metadataStorageConnectorConfig);
            datasource.setDriverClassLoader(getClass().getClassLoader());
            datasource.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
            this.dbi = dbi;
        }

        @Override
        public DBI getDBI() {
            return dbi;
        }
    }
}

