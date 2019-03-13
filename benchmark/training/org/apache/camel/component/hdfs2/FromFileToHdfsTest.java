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


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


/**
 *
 */
public class FromFileToHdfsTest extends HdfsTestSupport {
    private static final Path TEMP_DIR = new Path(new File("target/outbox/").getAbsolutePath());

    @Test
    public void testFileToHdfs() throws Exception {
        if (!(canTest())) {
            return;
        }
        NotifyBuilder notify = whenDone(1).create();
        template.sendBodyAndHeader("file:target/inbox", "Hello World", FILE_NAME, "hello.txt");
        notify.matchesMockWaitTime();
        File delete = new File("target/inbox/hello.txt");
        assertTrue(("File should be deleted " + delete), (!(delete.exists())));
        File create = new File(((FromFileToHdfsTest.TEMP_DIR) + "/output.txt"));
        assertTrue(("File should be created " + create), create.exists());
    }

    @Test
    public void testTwoFilesToHdfs() throws Exception {
        if (!(canTest())) {
            return;
        }
        NotifyBuilder notify = whenDone(2).create();
        template.sendBodyAndHeader("file:target/inbox", "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader("file:target/inbox", "Bye World", FILE_NAME, "bye.txt");
        notify.matchesMockWaitTime();
        File delete = new File("target/inbox/hello.txt");
        assertTrue(("File should be deleted " + delete), (!(delete.exists())));
        delete = new File("target/inbox/bye.txt");
        assertTrue(("File should be deleted " + delete), (!(delete.exists())));
        File create = new File(((FromFileToHdfsTest.TEMP_DIR) + "/output.txt"));
        assertTrue(("File should be created " + create), create.exists());
    }
}

