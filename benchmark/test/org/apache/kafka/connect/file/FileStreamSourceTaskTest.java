/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.file;


import FileStreamSourceConnector.FILE_CONFIG;
import FileStreamSourceConnector.TASK_BATCH_SIZE_CONFIG;
import FileStreamSourceTask.FILENAME_FIELD;
import FileStreamSourceTask.POSITION_FIELD;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class FileStreamSourceTaskTest extends EasyMockSupport {
    private static final String TOPIC = "test";

    private File tempFile;

    private Map<String, String> config;

    private OffsetStorageReader offsetStorageReader;

    private SourceTaskContext context;

    private FileStreamSourceTask task;

    private boolean verifyMocks = false;

    @Test
    public void testNormalLifecycle() throws IOException, InterruptedException {
        expectOffsetLookupReturnNone();
        replay();
        task.start(config);
        OutputStream os = Files.newOutputStream(tempFile.toPath());
        Assert.assertEquals(null, task.poll());
        os.write("partial line".getBytes());
        os.flush();
        Assert.assertEquals(null, task.poll());
        os.write(" finished\n".getBytes());
        os.flush();
        List<SourceRecord> records = task.poll();
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(FileStreamSourceTaskTest.TOPIC, records.get(0).topic());
        Assert.assertEquals("partial line finished", records.get(0).value());
        Assert.assertEquals(Collections.singletonMap(FILENAME_FIELD, tempFile.getAbsolutePath()), records.get(0).sourcePartition());
        Assert.assertEquals(Collections.singletonMap(POSITION_FIELD, 22L), records.get(0).sourceOffset());
        Assert.assertEquals(null, task.poll());
        // Different line endings, and make sure the final \r doesn't result in a line until we can
        // read the subsequent byte.
        os.write("line1\rline2\r\nline3\nline4\n\r".getBytes());
        os.flush();
        records = task.poll();
        Assert.assertEquals(4, records.size());
        Assert.assertEquals("line1", records.get(0).value());
        Assert.assertEquals(Collections.singletonMap(FILENAME_FIELD, tempFile.getAbsolutePath()), records.get(0).sourcePartition());
        Assert.assertEquals(Collections.singletonMap(POSITION_FIELD, 28L), records.get(0).sourceOffset());
        Assert.assertEquals("line2", records.get(1).value());
        Assert.assertEquals(Collections.singletonMap(FILENAME_FIELD, tempFile.getAbsolutePath()), records.get(1).sourcePartition());
        Assert.assertEquals(Collections.singletonMap(POSITION_FIELD, 35L), records.get(1).sourceOffset());
        Assert.assertEquals("line3", records.get(2).value());
        Assert.assertEquals(Collections.singletonMap(FILENAME_FIELD, tempFile.getAbsolutePath()), records.get(2).sourcePartition());
        Assert.assertEquals(Collections.singletonMap(POSITION_FIELD, 41L), records.get(2).sourceOffset());
        Assert.assertEquals("line4", records.get(3).value());
        Assert.assertEquals(Collections.singletonMap(FILENAME_FIELD, tempFile.getAbsolutePath()), records.get(3).sourcePartition());
        Assert.assertEquals(Collections.singletonMap(POSITION_FIELD, 47L), records.get(3).sourceOffset());
        os.write("subsequent text".getBytes());
        os.flush();
        records = task.poll();
        Assert.assertEquals(1, records.size());
        Assert.assertEquals("", records.get(0).value());
        Assert.assertEquals(Collections.singletonMap(FILENAME_FIELD, tempFile.getAbsolutePath()), records.get(0).sourcePartition());
        Assert.assertEquals(Collections.singletonMap(POSITION_FIELD, 48L), records.get(0).sourceOffset());
        os.close();
        task.stop();
    }

    @Test
    public void testBatchSize() throws IOException, InterruptedException {
        expectOffsetLookupReturnNone();
        replay();
        config.put(TASK_BATCH_SIZE_CONFIG, "5000");
        task.start(config);
        OutputStream os = Files.newOutputStream(tempFile.toPath());
        for (int i = 0; i < 10000; i++) {
            os.write("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\n".getBytes());
        }
        os.flush();
        List<SourceRecord> records = task.poll();
        Assert.assertEquals(5000, records.size());
        records = task.poll();
        Assert.assertEquals(5000, records.size());
        os.close();
        task.stop();
    }

    @Test
    public void testMissingFile() throws InterruptedException {
        replay();
        String data = "line\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        config.remove(FILE_CONFIG);
        task.start(config);
        List<SourceRecord> records = task.poll();
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(FileStreamSourceTaskTest.TOPIC, records.get(0).topic());
        Assert.assertEquals("line", records.get(0).value());
        task.stop();
    }
}

