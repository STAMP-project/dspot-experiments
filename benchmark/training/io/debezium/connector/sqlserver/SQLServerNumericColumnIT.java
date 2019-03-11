/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.sqlserver;


import DecimalHandlingMode.DOUBLE;
import DecimalHandlingMode.PRECISE;
import DecimalHandlingMode.STRING;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import SnapshotMode.INITIAL;
import SqlServerConnectorConfig.DECIMAL_HANDLING_MODE;
import SqlServerConnectorConfig.SNAPSHOT_MODE;
import SqlServerConnectorConfig.TABLE_WHITELIST;
import io.debezium.config.Configuration;
import io.debezium.connector.sqlserver.util.TestHelper;
import io.debezium.embedded.AbstractConnectorTest;
import java.math.BigDecimal;
import java.util.List;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.fest.assertions.Assertions;
import org.junit.Test;


/**
 * Tests for numeric/decimal columsn with precise, string and decimal options
 *
 * @author Pradeep Mamillapalli
 */
public class SQLServerNumericColumnIT extends AbstractConnectorTest {
    private SqlServerConnection connection;

    /**
     * Insert 1 Record into tablenuma with {@code DecimalHandlingMode.STRING}
     * mode Assertions: - Connector is running - 1 Record are streamed out of
     * cdc - Assert cola, colb, colc, cold are exactly equal to the input
     * values.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void decimalModeConfigString() throws Exception {
        final Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL).with(TABLE_WHITELIST, "dbo.tablenuma").with(DECIMAL_HANDLING_MODE, STRING).build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        TestHelper.waitForSnapshotToBeCompleted();
        connection.execute("INSERT INTO tablenuma VALUES (111.1111, 1111111, 1111111.1, 1111111 );");
        final SourceRecords records = consumeRecordsByTopic(1);
        final List<SourceRecord> tableA = records.recordsForTopic("server1.dbo.tablenuma");
        Assertions.assertThat(tableA).hasSize(1);
        final Struct valueA = ((Struct) (tableA.get(0).value()));
        assertSchema(valueA, OPTIONAL_STRING_SCHEMA);
        Assertions.assertThat(get("cola")).isEqualTo("111.1111");
        Assertions.assertThat(get("colb")).isEqualTo("1111111");
        Assertions.assertThat(get("colc")).isEqualTo("1111111.1");
        Assertions.assertThat(get("cold")).isEqualTo("1111111");
        stopConnector();
    }

    /**
     * Insert 1 Record into tablenumb with {@code DecimalHandlingMode.DOUBLE}
     * mode Assertions: - Connector is running - 1 Record are streamed out of
     * cdc - Assert cola, colb, colc, cold are exactly equal to the input values
     * in double format
     *
     * @throws Exception
     * 		
     */
    @Test
    public void decimalModeConfigDouble() throws Exception {
        final Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL).with(TABLE_WHITELIST, "dbo.tablenumb").with(DECIMAL_HANDLING_MODE, DOUBLE).build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        TestHelper.waitForSnapshotToBeCompleted();
        connection.execute("INSERT INTO tablenumb VALUES (222.2222, 22222, 22222.2, 2222222 );");
        final SourceRecords records = consumeRecordsByTopic(1);
        final List<SourceRecord> results = records.recordsForTopic("server1.dbo.tablenumb");
        Assertions.assertThat(results).hasSize(1);
        final Struct valueA = ((Struct) (results.get(0).value()));
        assertSchema(valueA, OPTIONAL_FLOAT64_SCHEMA);
        Assertions.assertThat(get("cola")).isEqualTo(222.2222);
        Assertions.assertThat(get("colb")).isEqualTo(22222.0);
        Assertions.assertThat(get("colc")).isEqualTo(22222.2);
        Assertions.assertThat(get("cold")).isEqualTo(2222222.0);
        stopConnector();
    }

    /**
     * Insert 1 Record into tablenumc with {@code DecimalHandlingMode.PRECISE}
     * mode Assertions: - Connector is running - 1 Record are streamed out of
     * cdc - Assert cola, colb, colc, cold are bytes
     *
     * @throws Exception
     * 		
     */
    @Test
    public void decimalModeConfigPrecise() throws Exception {
        final Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL).with(TABLE_WHITELIST, "dbo.tablenumc").with(DECIMAL_HANDLING_MODE, PRECISE).build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        TestHelper.waitForSnapshotToBeCompleted();
        connection.execute("INSERT INTO tablenumc VALUES (333.3333, 3333, 3333.3, 33333333 );");
        final SourceRecords records = consumeRecordsByTopic(1);
        final List<SourceRecord> results = records.recordsForTopic("server1.dbo.tablenumc");
        Assertions.assertThat(results).hasSize(1);
        final Struct valueA = ((Struct) (results.get(0).value()));
        Assertions.assertThat(valueA.schema().field("after").schema().field("cola").schema()).isEqualTo(Decimal.builder(4).parameter("connect.decimal.precision", "8").optional().schema());
        Assertions.assertThat(valueA.schema().field("after").schema().field("colb").schema()).isEqualTo(Decimal.builder(0).parameter("connect.decimal.precision", "18").optional().schema());
        Assertions.assertThat(valueA.schema().field("after").schema().field("colc").schema()).isEqualTo(Decimal.builder(1).parameter("connect.decimal.precision", "8").optional().schema());
        Assertions.assertThat(valueA.schema().field("after").schema().field("cold").schema()).isEqualTo(Decimal.builder(0).parameter("connect.decimal.precision", "18").optional().schema());
        Assertions.assertThat(get("cola")).isEqualTo(BigDecimal.valueOf(333.3333));
        Assertions.assertThat(get("colb")).isEqualTo(BigDecimal.valueOf(3333));
        Assertions.assertThat(get("colc")).isEqualTo(BigDecimal.valueOf(3333.3));
        Assertions.assertThat(get("cold")).isEqualTo(BigDecimal.valueOf(33333333));
        stopConnector();
    }
}

