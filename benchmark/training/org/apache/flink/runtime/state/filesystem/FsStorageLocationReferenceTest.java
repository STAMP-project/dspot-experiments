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
package org.apache.flink.runtime.state.filesystem;


import java.util.Random;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.CheckpointStorageLocationReference;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the encoding / decoding of storage location references.
 */
public class FsStorageLocationReferenceTest extends TestLogger {
    @Test
    public void testEncodeAndDecode() throws Exception {
        final Path path = FsStorageLocationReferenceTest.randomPath(new Random());
        try {
            CheckpointStorageLocationReference ref = AbstractFsCheckpointStorage.encodePathAsReference(path);
            Path decoded = AbstractFsCheckpointStorage.decodePathFromReference(ref);
            Assert.assertEquals(path, decoded);
        } catch (Exception | Error e) {
            // if something goes wrong, help by printing the problematic path
            log.error(("ERROR FOR PATH " + path));
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodingTooShortReference() {
        AbstractFsCheckpointStorage.decodePathFromReference(new CheckpointStorageLocationReference(new byte[2]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodingGarbage() {
        final byte[] bytes = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        AbstractFsCheckpointStorage.decodePathFromReference(new CheckpointStorageLocationReference(bytes));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodingDefaultReference() {
        AbstractFsCheckpointStorage.decodePathFromReference(CheckpointStorageLocationReference.getDefault());
    }
}

