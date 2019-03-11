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
package org.apache.nifi.csv;


import CSVFormat.DEFAULT;
import QuoteMode.ALL;
import RecordFieldType.DATE;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import com.google.common.base.Throwables;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordSchema;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ITApacheCSVRecordReader {
    private final CSVFormat format = DEFAULT.withFirstRecordAsHeader().withTrim().withQuote('"');

    @Test
    public void testParserPerformance() throws IOException, MalformedRecordException {
        // Generates about 130MB of data
        final int NUM_LINES = 2500000;
        StringBuilder sb = new StringBuilder("id,name,balance,address,city,state,zipCode,country\n");
        for (int i = 0; i < NUM_LINES; i++) {
            sb.append("1,John Doe,4750.89D,123 My Street,My City,MS,11111,USA\n");
        }
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream bais = new ByteArrayInputStream(sb.toString().getBytes());final CSVRecordReader reader = new CSVRecordReader(bais, Mockito.mock(ComponentLog.class), schema, format, true, false, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), "UTF-8")) {
            Record record;
            int numRecords = 0;
            while ((record = reader.nextRecord()) != null) {
                Assert.assertNotNull(record);
                numRecords++;
            } 
            Assert.assertEquals(NUM_LINES, numRecords);
        }
    }

    @Test
    public void testExceptionThrownOnParseProblem() throws IOException, MalformedRecordException {
        CSVFormat csvFormat = DEFAULT.withFirstRecordAsHeader().withQuoteMode(ALL).withTrim().withDelimiter(',');
        final int NUM_LINES = 25;
        StringBuilder sb = new StringBuilder("\"id\",\"name\",\"balance\"");
        for (int i = 0; i < NUM_LINES; i++) {
            sb.append(String.format("\"%s\",\"John Doe\",\"4750.89D\"\n", i));
        }
        // cause a parse problem
        sb.append(String.format("\"%s\"dieParser,\"John Doe\",\"4750.89D\"\n", NUM_LINES));
        sb.append(String.format("\"%s\",\"John Doe\",\"4750.89D\"\n", (NUM_LINES + 1)));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(createStringFields(new String[]{ "id", "name", "balance" }));
        try (final InputStream bais = new ByteArrayInputStream(sb.toString().getBytes());final CSVRecordReader reader = new CSVRecordReader(bais, Mockito.mock(ComponentLog.class), schema, csvFormat, true, false, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), "UTF-8")) {
            while ((reader.nextRecord()) != null) {
            } 
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.instanceOf(MalformedRecordException.class));
            Assert.assertThat(Throwables.getRootCause(e), CoreMatchers.instanceOf(IOException.class));
        }
    }
}

