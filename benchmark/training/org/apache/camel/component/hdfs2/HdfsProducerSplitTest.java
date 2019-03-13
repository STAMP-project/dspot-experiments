/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.hdfs2;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


public class HdfsProducerSplitTest extends HdfsTestSupport {
    private static final Path BASE_FILE = new Path(new File("target/test/test-camel-simple-write-BASE_FILE").getAbsolutePath());

    @Test
    public void testSimpleWriteFileWithMessageSplit() throws Exception {
        doTest(1);
    }

    @Test
    public void testSimpleWriteFileWithBytesSplit() throws Exception {
        doTest(2);
    }

    @Test
    public void testSimpleWriteFileWithIdleSplit() throws Exception {
        if (!(canTest())) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            template.sendBody("direct:start3", ("CIAO" + i));
            Thread.sleep(2000);
        }
        // stop Camel to flush and close file stream
        stopCamelContext();
        FileSystem fs = FileSystem.get(new Configuration());
        FileStatus[] status = fs.listStatus(new Path((("file:///" + (HdfsProducerSplitTest.BASE_FILE.toUri())) + "3")));
        assertEquals(3, status.length);
        for (int i = 0; i < 3; i++) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(status[i].getPath())));
            assertTrue(br.readLine().startsWith("CIAO"));
            assertNull(br.readLine());
        }
    }

    @Test
    public void testSimpleWriteFileWithMessageIdleSplit() throws Exception {
        doTest(4);
    }

    @Test
    public void testSimpleWriteFileWithBytesIdleSplit() throws Exception {
        doTest(5);
    }
}

