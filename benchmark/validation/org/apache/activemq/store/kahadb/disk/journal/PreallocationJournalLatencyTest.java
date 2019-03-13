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


import Journal.PreallocationStrategy.CHUNKED_ZEROS;
import Journal.PreallocationStrategy.OS_KERNEL_COPY;
import Journal.PreallocationStrategy.SPARSE_FILE;
import java.util.Random;
import org.apache.activemq.management.TimeStatisticImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PreallocationJournalLatencyTest {
    private static final Logger LOG = LoggerFactory.getLogger(PreallocationJournalLatencyTest.class);

    final Random rand = new Random();

    @Test
    public void preallocationLatency() throws Exception {
        TimeStatisticImpl sparse = executeTest(SPARSE_FILE.name());
        TimeStatisticImpl chunked_zeros = executeTest(CHUNKED_ZEROS.name());
        // TimeStatisticImpl zeros = executeTest(Journal.PreallocationStrategy.ZEROS.name());
        TimeStatisticImpl kernel = executeTest(OS_KERNEL_COPY.name());
        PreallocationJournalLatencyTest.LOG.info(("  sparse: " + sparse));
        PreallocationJournalLatencyTest.LOG.info((" chunked: " + chunked_zeros));
        // LOG.info("   zeros: " + zeros);
        PreallocationJournalLatencyTest.LOG.info(("  kernel: " + kernel));
    }
}

