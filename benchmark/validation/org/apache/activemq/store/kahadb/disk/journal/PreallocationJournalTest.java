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
package org.apache.activemq.store.kahadb.disk.journal;


import java.io.File;
import java.util.Random;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.apache.activemq.store.kahadb.data.KahaTraceCommand;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by ceposta
 * <a href="http://christianposta.com/blog>http://christianposta.com/blog</a>.
 */
public class PreallocationJournalTest {
    private static final Logger LOG = LoggerFactory.getLogger(PreallocationJournalTest.class);

    @Test
    public void testSparseFilePreallocation() throws Exception {
        executeTest("sparse_file");
    }

    @Test
    public void testOSCopyPreallocation() throws Exception {
        executeTest("os_kernel_copy");
    }

    @Test
    public void testZerosPreallocation() throws Exception {
        executeTest("zeros");
    }

    @Test
    public void testZerosLoop() throws Exception {
        Random rand = new Random();
        int randInt = rand.nextInt(100);
        File dataDirectory = new File(("./target/activemq-data/kahadb" + randInt));
        KahaDBStore store = new KahaDBStore();
        store.setJournalMaxFileLength(((5 * 1024) * 1024));
        store.deleteAllMessages();
        store.setDirectory(dataDirectory);
        store.setPreallocationStrategy("zeros");
        store.start();
        final File journalLog = new File(dataDirectory, "db-1.log");
        Assert.assertTrue("file exists", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return journalLog.exists();
            }
        }));
        KahaTraceCommand traceCommand = new KahaTraceCommand();
        traceCommand.setMessage(new String(new byte[(2 * 1024) * 1024]));
        Location location = null;
        for (int i = 0; i < 20; i++) {
            location = store.store(traceCommand);
        }
        PreallocationJournalTest.LOG.info(("Last location:" + location));
        PreallocationJournalTest.LOG.info(("Store journal files:" + (store.getJournal().getFiles().size())));
    }
}

