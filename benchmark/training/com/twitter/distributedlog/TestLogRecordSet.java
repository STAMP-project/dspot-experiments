/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog;


import Type.LZ4;
import Type.NONE;
import com.twitter.distributedlog.LogRecordSet.Reader;
import com.twitter.distributedlog.LogRecordSet.Writer;
import com.twitter.distributedlog.exceptions.LogRecordTooLongException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Case for {@link LogRecordSet}
 */
public class TestLogRecordSet {
    @Test(timeout = 60000)
    public void testEmptyRecordSet() throws Exception {
        Writer writer = LogRecordSet.LogRecordSet.newWriter(1024, NONE);
        Assert.assertEquals("zero user bytes", HEADER_LEN, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        ByteBuffer buffer = writer.getBuffer();
        Assert.assertEquals("zero user bytes", HEADER_LEN, buffer.remaining());
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        LogRecordWithDLSN record = new LogRecordWithDLSN(new DLSN(1L, 0L, 0L), 1L, data, 1L);
        record.setRecordSet();
        Reader reader = LogRecordSet.LogRecordSet.of(record);
        Assert.assertNull("Empty record set should return null", reader.nextRecord());
    }

    @Test(timeout = 60000)
    public void testWriteTooLongRecord() throws Exception {
        Writer writer = LogRecordSet.LogRecordSet.newWriter(1024, NONE);
        Assert.assertEquals("zero user bytes", HEADER_LEN, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        ByteBuffer dataBuf = ByteBuffer.allocate(((MAX_LOGRECORD_SIZE) + 1));
        try {
            writer.writeRecord(dataBuf, new com.twitter.util.Promise<DLSN>());
            Assert.fail("Should fail on writing large record");
        } catch (LogRecordTooLongException lrtle) {
            // expected
        }
        Assert.assertEquals("zero user bytes", HEADER_LEN, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        ByteBuffer buffer = writer.getBuffer();
        Assert.assertEquals("zero user bytes", HEADER_LEN, buffer.remaining());
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        LogRecordWithDLSN record = new LogRecordWithDLSN(new DLSN(1L, 0L, 0L), 1L, data, 1L);
        record.setRecordSet();
        Reader reader = LogRecordSet.LogRecordSet.of(record);
        Assert.assertNull("Empty record set should return null", reader.nextRecord());
    }

    @Test(timeout = 20000)
    public void testWriteRecordsNoneCompressed() throws Exception {
        testWriteRecords(NONE);
    }

    @Test(timeout = 20000)
    public void testWriteRecordsLZ4Compressed() throws Exception {
        testWriteRecords(LZ4);
    }
}

