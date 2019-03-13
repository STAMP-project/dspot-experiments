/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.serialization;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.commons.io.FileUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class TestSyslogAvroEventSerializer {
    File testFile = new File("src/test/resources/SyslogEvents.avro");

    File schemaFile = new File("src/test/resources/syslog_event.avsc");

    @Test
    public void test() throws FileNotFoundException, IOException {
        // Snappy currently broken on Mac in OpenJDK 7 per FLUME-2012
        Assume.assumeTrue(((!("Mac OS X".equals(System.getProperty("os.name")))) || (!(System.getProperty("java.version").startsWith("1.7.")))));
        // Schema schema = new Schema.Parser().parse(schemaFile);
        // create the file, write some data
        OutputStream out = new FileOutputStream(testFile);
        String builderName = SyslogAvroEventSerializer.Builder.class.getName();
        Context ctx = new Context();
        ctx.put("syncInterval", "4096");
        ctx.put("compressionCodec", "snappy");
        EventSerializer serializer = EventSerializerFactory.getInstance(builderName, ctx, out);
        serializer.afterCreate();// must call this when a file is newly created

        List<Event> events = TestSyslogAvroEventSerializer.generateSyslogEvents();
        for (Event e : events) {
            serializer.write(e);
        }
        serializer.flush();
        serializer.beforeClose();
        out.flush();
        out.close();
        // now try to read the file back
        DatumReader<GenericRecord> reader = new org.apache.avro.generic.GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> fileReader = new DataFileReader<GenericRecord>(testFile, reader);
        GenericRecord record = new org.apache.avro.generic.GenericData.Record(fileReader.getSchema());
        int numEvents = 0;
        while (fileReader.hasNext()) {
            fileReader.next(record);
            int facility = ((Integer) (record.get("facility")));
            int severity = ((Integer) (record.get("severity")));
            long timestamp = ((Long) (record.get("timestamp")));
            String hostname = record.get("hostname").toString();
            String message = record.get("message").toString();
            Assert.assertEquals("Facility should be 1", 1, facility);
            System.out.println(((timestamp + ": ") + message));
            numEvents++;
        } 
        fileReader.close();
        Assert.assertEquals("Should have found a total of 3 events", 3, numEvents);
        FileUtils.forceDelete(testFile);
    }
}

