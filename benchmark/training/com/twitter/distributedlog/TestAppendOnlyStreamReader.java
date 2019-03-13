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


import com.twitter.distributedlog.exceptions.EndOfStreamException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAppendOnlyStreamReader extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestAppendOnlyStreamReader.class);

    @Rule
    public TestName testNames = new TestName();

    @Test(timeout = 60000)
    public void testSkipToSkipsBytesWithImmediateFlush() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        skipForwardThenSkipBack(name, confLocal);
    }

    @Test(timeout = 60000)
    public void testSkipToSkipsBytesWithLargerLogRecords() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize((1024 * 100));
        confLocal.setPeriodicFlushFrequencyMilliSeconds((1000 * 60));
        skipForwardThenSkipBack(name, confLocal);
    }

    @Test(timeout = 60000)
    public void testSkipToSkipsBytesUntilEndOfStream() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogManager dlmwrite = createNewDLM(TestDistributedLogBase.conf, name);
        DistributedLogManager dlmreader = createNewDLM(TestDistributedLogBase.conf, name);
        long txid = 1;
        AppendOnlyStreamWriter writer = dlmwrite.getAppendOnlyStreamWriter();
        writer.write(DLMTestUtil.repeatString("abc", 5).getBytes());
        writer.markEndOfStream();
        writer.force(false);
        writer.close();
        AppendOnlyStreamReader reader = dlmreader.getAppendOnlyStreamReader();
        byte[] bytesIn = new byte[9];
        int read = reader.read(bytesIn, 0, 9);
        Assert.assertEquals(9, read);
        Assert.assertTrue(Arrays.equals(DLMTestUtil.repeatString("abc", 3).getBytes(), bytesIn));
        Assert.assertTrue(reader.skipTo(15));
        try {
            read = reader.read(bytesIn, 0, 1);
            Assert.fail("Should have thrown");
        } catch (EndOfStreamException ex) {
        }
        Assert.assertTrue(reader.skipTo(0));
        try {
            reader.skipTo(16);
            Assert.fail("Should have thrown");
        } catch (EndOfStreamException ex) {
        }
    }

    @Test(timeout = 60000)
    public void testSkipToreturnsFalseIfPositionDoesNotExistYetForUnSealedStream() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogManager dlmwrite = createNewDLM(TestDistributedLogBase.conf, name);
        DistributedLogManager dlmreader = createNewDLM(TestDistributedLogBase.conf, name);
        long txid = 1;
        AppendOnlyStreamWriter writer = dlmwrite.getAppendOnlyStreamWriter();
        writer.write(DLMTestUtil.repeatString("abc", 5).getBytes());
        writer.close();
        final AppendOnlyStreamReader reader = dlmreader.getAppendOnlyStreamReader();
        byte[] bytesIn = new byte[9];
        int read = reader.read(bytesIn, 0, 9);
        Assert.assertEquals(9, read);
        Assert.assertTrue(Arrays.equals(DLMTestUtil.repeatString("abc", 3).getBytes(), bytesIn));
        Assert.assertFalse(reader.skipTo(16));
        Assert.assertFalse(reader.skipTo(16));
        AppendOnlyStreamWriter writer2 = dlmwrite.getAppendOnlyStreamWriter();
        writer2.write(DLMTestUtil.repeatString("abc", 5).getBytes());
        writer2.close();
        Assert.assertTrue(reader.skipTo(16));
        byte[] bytesIn2 = new byte[5];
        read = reader.read(bytesIn2, 0, 5);
        Assert.assertEquals(5, read);
        Assert.assertTrue(Arrays.equals("bcabc".getBytes(), bytesIn2));
    }

    @Test(timeout = 60000)
    public void testSkipToForNoPositionChange() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogManager dlmwrite = createNewDLM(TestDistributedLogBase.conf, name);
        DistributedLogManager dlmreader = createNewDLM(TestDistributedLogBase.conf, name);
        long txid = 1;
        AppendOnlyStreamWriter writer = dlmwrite.getAppendOnlyStreamWriter();
        writer.write(DLMTestUtil.repeatString("abc", 5).getBytes());
        writer.close();
        final AppendOnlyStreamReader reader = dlmreader.getAppendOnlyStreamReader();
        Assert.assertTrue(reader.skipTo(0));
        byte[] bytesIn = new byte[4];
        int read = reader.read(bytesIn, 0, 4);
        Assert.assertEquals(4, read);
        Assert.assertEquals(new String("abca"), new String(bytesIn));
        Assert.assertTrue(reader.skipTo(reader.position()));
        Assert.assertTrue(reader.skipTo(1));
        read = reader.read(bytesIn, 0, 4);
        Assert.assertEquals(4, read);
        Assert.assertEquals(new String("bcab"), new String(bytesIn));
    }
}

