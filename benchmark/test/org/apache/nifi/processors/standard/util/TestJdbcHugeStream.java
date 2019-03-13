/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard.util;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test streaming using large number of result set rows. 1. Read data from
 * database. 2. Create Avro schema from ResultSet meta data. 3. Read rows from
 * ResultSet and write rows to Avro writer stream (Avro will create record for
 * each row). 4. And finally read records from Avro stream to verify all data is
 * present in Avro stream.
 *
 *
 * Sql query will return all combinations from 3 table. For example when each
 * table contain 1000 rows, result set will be 1 000 000 000 rows.
 */
public class TestJdbcHugeStream {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void readSend2StreamHuge_FileBased() throws IOException, ClassNotFoundException, SQLException {
        // remove previous test database, if any
        folder.delete();
        try (final Connection con = createConnection(folder.getRoot().getAbsolutePath())) {
            TestJdbcHugeStream.loadTestData2Database(con, 100, 100, 100);
            try (final Statement st = con.createStatement()) {
                // Notice!
                // Following select is deliberately invalid!
                // For testing we need huge amount of rows, so where part is not
                // used.
                final ResultSet resultSet = st.executeQuery(("select " + (((("  PER.ID as PersonId, PER.NAME as PersonName, PER.CODE as PersonCode" + ", PRD.ID as ProductId,PRD.NAME as ProductName,PRD.CODE as ProductCode") + ", REL.ID as RelId,    REL.NAME as RelName,    REL.CODE as RelCode") + ", ROW_NUMBER() OVER () as rownr ") + " from persons PER, products PRD, relationships REL")));
                final OutputStream outStream = new FileOutputStream("target/data.avro");
                final long nrOfRows = JdbcCommon.convertToAvroStream(resultSet, outStream, false);
                // Deserialize bytes to records
                final InputStream instream = new FileInputStream("target/data.avro");
                final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
                try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
                    GenericRecord record = null;
                    long recordsFromStream = 0;
                    while (dataFileReader.hasNext()) {
                        // Reuse record object by passing it to next(). This
                        // saves us from
                        // allocating and garbage collecting many objects for
                        // files with many items.
                        record = dataFileReader.next(record);
                        recordsFromStream += 1;
                    } 
                    System.out.println(("total nr of records from stream: " + recordsFromStream));
                    Assert.assertEquals(nrOfRows, recordsFromStream);
                }
            }
        }
    }

    // ================================================ helpers
    // ===============================================
    static String dropPersons = "drop table persons";

    static String dropProducts = "drop table products";

    static String dropRelationships = "drop table relationships";

    static String createPersons = "create table persons (id integer, name varchar(100), code integer)";

    static String createProducts = "create table products (id integer, name varchar(100), code integer)";

    static String createRelationships = "create table relationships (id integer,name varchar(100), code integer)";

    static Random rng = new Random(53495);
}

