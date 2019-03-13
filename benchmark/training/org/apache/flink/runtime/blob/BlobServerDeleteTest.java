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
package org.apache.flink.runtime.blob;


import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests how DELETE requests behave.
 */
public class BlobServerDeleteTest extends TestLogger {
    private final Random rnd = new Random();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testDeleteTransient1() throws IOException {
        testDeleteTransient(null, new JobID());
    }

    @Test
    public void testDeleteTransient2() throws IOException {
        testDeleteTransient(new JobID(), null);
    }

    @Test
    public void testDeleteTransient3() throws IOException {
        testDeleteTransient(null, null);
    }

    @Test
    public void testDeleteTransient4() throws IOException {
        testDeleteTransient(new JobID(), new JobID());
    }

    @Test
    public void testDeleteTransient5() throws IOException {
        JobID jobId = new JobID();
        testDeleteTransient(jobId, jobId);
    }

    @Test
    public void testDeleteTransientAlreadyDeletedNoJob() throws IOException {
        testDeleteTransientAlreadyDeleted(null);
    }

    @Test
    public void testDeleteTransientAlreadyDeletedForJob() throws IOException {
        testDeleteTransientAlreadyDeleted(new JobID());
    }

    @Test
    public void testDeleteTransientFailsNoJob() throws IOException {
        testDeleteTransientFails(null);
    }

    @Test
    public void testDeleteTransientFailsForJob() throws IOException {
        testDeleteTransientFails(new JobID());
    }

    @Test
    public void testJobCleanup() throws IOException, InterruptedException {
        testJobCleanup(BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testJobCleanupHa() throws IOException, InterruptedException {
        testJobCleanup(BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testConcurrentDeleteOperationsNoJobTransient() throws IOException, InterruptedException, ExecutionException {
        testConcurrentDeleteOperations(null);
    }

    @Test
    public void testConcurrentDeleteOperationsForJobTransient() throws IOException, InterruptedException, ExecutionException {
        testConcurrentDeleteOperations(new JobID());
    }
}

