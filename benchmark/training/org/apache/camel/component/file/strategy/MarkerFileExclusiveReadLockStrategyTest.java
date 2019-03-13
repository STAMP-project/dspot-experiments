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
package org.apache.camel.component.file.strategy;


import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the MarkerFileExclusiveReadLockStrategy in a multi-threaded scenario.
 */
public class MarkerFileExclusiveReadLockStrategyTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MarkerFileExclusiveReadLockStrategyTest.class);

    private static final int NUMBER_OF_THREADS = 5;

    private AtomicInteger numberOfFilesProcessed = new AtomicInteger(0);

    @Test
    public void testMultithreadedLocking() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        mock.expectedFileExists("target/data/marker/out/file1.dat");
        mock.expectedFileExists("target/data/marker/out/file2.dat");
        writeFiles();
        assertMockEndpointsSatisfied();
        String content = context.getTypeConverter().convertTo(String.class, new File("target/data/marker/out/file1.dat"));
        String[] lines = content.split(TestSupport.LS);
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(("Line " + i), lines[i]);
        }
        content = context.getTypeConverter().convertTo(String.class, new File("target/data/marker/out/file2.dat"));
        lines = content.split(TestSupport.LS);
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(("Line " + i), lines[i]);
        }
        waitUntilCompleted();
        MarkerFileExclusiveReadLockStrategyTest.assertFileDoesNotExists("target/data/marker/in/file1.dat.camelLock");
        MarkerFileExclusiveReadLockStrategyTest.assertFileDoesNotExists("target/data/marker/in/file2.dat.camelLock");
        MarkerFileExclusiveReadLockStrategyTest.assertFileDoesNotExists("target/data/marker/in/file1.dat");
        MarkerFileExclusiveReadLockStrategyTest.assertFileDoesNotExists("target/data/marker/in/file2.dat");
        Assert.assertEquals(2, this.numberOfFilesProcessed.get());
    }
}

