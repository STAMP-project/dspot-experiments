/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.sqlserver;


import Schema.INT32_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import SnapshotMode.INITIAL;
import SqlServerConnectorConfig.SNAPSHOT_MODE;
import SqlServerConnectorConfig.TABLE_WHITELIST;
import io.debezium.config.Configuration;
import io.debezium.connector.sqlserver.util.TestHelper;
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.source.SourceRecord;
import org.fest.assertions.Assertions;
import org.junit.Test;


/**
 * Integration test to verify behaviour of database with special chars table names.
 *
 * @author Jiri Pechanec
 */
public class SpecialCharsInNamesIT extends AbstractConnectorTest {
    private SqlServerConnection connection;

    @Test
    @FixFor("DBZ-1153")
    public void shouldParseSpecialChars() throws Exception {
        final Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL).with(TABLE_WHITELIST, "dbo\\.UAT WAG CZ\\$Fixed Asset.*").build();
        connection.execute("CREATE TABLE [UAT WAG CZ$Fixed Asset] (id int primary key, [my col$a] varchar(30))", "INSERT INTO [UAT WAG CZ$Fixed Asset] VALUES(1, 'a')");
        TestHelper.enableTableCdc(connection, "UAT WAG CZ$Fixed Asset");
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        SourceRecords records = consumeRecordsByTopic(1);
        Assertions.assertThat(records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset")).hasSize(1);
        SourceRecord record = records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset").get(0);
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (get("after"))), SchemaBuilder.struct().optional().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset.Value").field("id", INT32_SCHEMA).field("my col$a", OPTIONAL_STRING_SCHEMA).build());
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (record.key())), SchemaBuilder.struct().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset.Key").field("id", INT32_SCHEMA).build());
        Assertions.assertThat(getStruct("after").getInt32("id")).isEqualTo(1);
        connection.execute("INSERT INTO [UAT WAG CZ$Fixed Asset] VALUES(2, 'b')");
        records = consumeRecordsByTopic(1);
        Assertions.assertThat(records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset")).hasSize(1);
        record = records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset").get(0);
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (get("after"))), SchemaBuilder.struct().optional().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset.Value").field("id", INT32_SCHEMA).field("my col$a", OPTIONAL_STRING_SCHEMA).build());
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (record.key())), SchemaBuilder.struct().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset.Key").field("id", INT32_SCHEMA).build());
        Assertions.assertThat(getStruct("after").getInt32("id")).isEqualTo(2);
        connection.execute("CREATE TABLE [UAT WAG CZ$Fixed Asset Two] (id int primary key, [my col$] varchar(30), Description varchar(30) NOT NULL)");
        TestHelper.enableTableCdc(connection, "UAT WAG CZ$Fixed Asset Two");
        connection.execute("INSERT INTO [UAT WAG CZ$Fixed Asset Two] VALUES(3, 'b', 'empty')");
        records = consumeRecordsByTopic(1);
        Assertions.assertThat(records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two")).hasSize(1);
        record = records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two").get(0);
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (get("after"))), SchemaBuilder.struct().optional().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two.Value").field("id", INT32_SCHEMA).field("my col$", OPTIONAL_STRING_SCHEMA).field("Description", STRING_SCHEMA).build());
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (record.key())), SchemaBuilder.struct().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two.Key").field("id", INT32_SCHEMA).build());
        Assertions.assertThat(getStruct("after").getInt32("id")).isEqualTo(3);
        connection.execute("UPDATE [UAT WAG CZ$Fixed Asset Two] SET Description='c1' WHERE id=3");
        records = consumeRecordsByTopic(1);
        Assertions.assertThat(records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two")).hasSize(1);
        record = records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two").get(0);
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (get("after"))), SchemaBuilder.struct().optional().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two.Value").field("id", INT32_SCHEMA).field("my col$", OPTIONAL_STRING_SCHEMA).field("Description", STRING_SCHEMA).build());
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (get("before"))), SchemaBuilder.struct().optional().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset_Two.Value").field("id", INT32_SCHEMA).field("my col$", OPTIONAL_STRING_SCHEMA).field("Description", STRING_SCHEMA).build());
        Assertions.assertThat(getStruct("after").getString("Description")).isEqualTo("c1");
        Assertions.assertThat(getStruct("before").getString("Description")).isEqualTo("empty");
        stopConnector();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        connection.execute("INSERT INTO [UAT WAG CZ$Fixed Asset] VALUES(4, 'b')");
        records = consumeRecordsByTopic(1);
        Assertions.assertThat(records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset")).hasSize(1);
        record = records.recordsForTopic("server1.dbo.UAT_WAG_CZ_Fixed_Asset").get(0);
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (get("after"))), SchemaBuilder.struct().optional().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset.Value").field("id", INT32_SCHEMA).field("my col$a", OPTIONAL_STRING_SCHEMA).build());
        assertSchemaMatchesStruct(((org.apache.kafka.connect.data.Struct) (record.key())), SchemaBuilder.struct().name("server1.dbo.UAT_WAG_CZ_Fixed_Asset.Key").field("id", INT32_SCHEMA).build());
        Assertions.assertThat(getStruct("after").getInt32("id")).isEqualTo(4);
    }
}

