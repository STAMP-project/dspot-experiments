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


import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class TestJdbcCommonConvertToAvro {
    private static final boolean SIGNED = true;

    private static final boolean UNSIGNED = false;

    @Parameterized.Parameter
    public TestJdbcCommonConvertToAvro.TestParams testParams;

    static class TestParams {
        int sqlType;

        int precision;

        boolean signed;

        TestParams(int sqlType, int precision, boolean signed) {
            this.sqlType = sqlType;
            this.precision = precision;
            this.signed = signed;
        }

        private String humanReadableType() {
            switch (sqlType) {
                case Types.TINYINT :
                    return "TINYINT";
                case Types.INTEGER :
                    return "INTEGER";
                case Types.SMALLINT :
                    return "SMALLINT";
                case Types.BIGINT :
                    return "BIGINT";
                default :
                    return "UNKNOWN - ADD TO LIST";
            }
        }

        private String humanReadableSigned() {
            if (signed)
                return "SIGNED";

            return "UNSIGNED";
        }

        public String toString() {
            return String.format("TestParams(SqlType=%s, Precision=%s, Signed=%s)", humanReadableType(), precision, humanReadableSigned());
        }
    }

    @Test
    public void testConvertToAvroStreamForNumbers() throws IOException, SQLException {
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(testParams.sqlType);
        Mockito.when(metadata.isSigned(1)).thenReturn(testParams.signed);
        Mockito.when(metadata.getPrecision(1)).thenReturn(testParams.precision);
        Mockito.when(metadata.getColumnName(1)).thenReturn("t_int");
        Mockito.when(metadata.getTableName(1)).thenReturn("table");
        final ResultSet rs = JdbcCommonTestUtils.resultSetReturningMetadata(metadata);
        final int ret = 0;
        Mockito.when(rs.getObject(Mockito.anyInt())).thenReturn(ret);
        final InputStream instream = JdbcCommonTestUtils.convertResultSetToAvroInputStream(rs);
        final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
        try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
            GenericRecord record = null;
            while (dataFileReader.hasNext()) {
                record = dataFileReader.next(record);
                Assert.assertEquals(Integer.toString(ret), record.get("t_int").toString());
            } 
        }
    }
}

