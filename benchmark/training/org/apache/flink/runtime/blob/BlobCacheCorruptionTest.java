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
import org.apache.flink.api.common.JobID;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests how GET requests react to corrupt files when downloaded via a {@link BlobCacheService}.
 *
 * <p>Successful GET requests are tested in conjunction wit the PUT requests.
 */
public class BlobCacheCorruptionTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetFailsFromCorruptFile1() throws IOException {
        testGetFailsFromCorruptFile(null, BlobType.TRANSIENT_BLOB, false);
    }

    @Test
    public void testGetFailsFromCorruptFile2() throws IOException {
        testGetFailsFromCorruptFile(new JobID(), BlobType.TRANSIENT_BLOB, false);
    }

    @Test
    public void testGetFailsFromCorruptFile3() throws IOException {
        testGetFailsFromCorruptFile(new JobID(), BlobType.PERMANENT_BLOB, false);
    }

    @Test
    public void testGetFailsFromCorruptFile4() throws IOException {
        testGetFailsFromCorruptFile(new JobID(), BlobType.PERMANENT_BLOB, true);
    }
}

