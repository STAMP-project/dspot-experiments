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
package org.apache.flink.fs.s3.common.writer;


import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static S3RecoverableSerializer.INSTANCE;


/**
 * Tests for the {@link S3RecoverableSerializer}.
 */
public class S3RecoverableSerializerTest {
    private final S3RecoverableSerializer serializer = INSTANCE;

    private static final String TEST_OBJECT_NAME = "TEST-OBJECT";

    private static final String TEST_UPLOAD_ID = "TEST-UPLOAD-ID";

    private static final String INCOMPLETE_OBJECT_NAME = "TEST-INCOMPLETE-PART";

    private static final String ETAG_PREFIX = "TEST-ETAG-";

    @Test
    public void serializeEmptyS3Recoverable() throws IOException {
        S3Recoverable originalEmptyRecoverable = S3RecoverableSerializerTest.createTestS3Recoverable(false);
        byte[] serializedRecoverable = serializer.serialize(originalEmptyRecoverable);
        S3Recoverable copiedEmptyRecoverable = serializer.deserialize(1, serializedRecoverable);
        MatcherAssert.assertThat(originalEmptyRecoverable, S3RecoverableSerializerTest.isEqualTo(copiedEmptyRecoverable));
    }

    @Test
    public void serializeS3RecoverableWithoutIncompleteObject() throws IOException {
        S3Recoverable originalNoIncompletePartRecoverable = S3RecoverableSerializerTest.createTestS3Recoverable(false, 1, 5, 9);
        byte[] serializedRecoverable = serializer.serialize(originalNoIncompletePartRecoverable);
        S3Recoverable copiedNoIncompletePartRecoverable = serializer.deserialize(1, serializedRecoverable);
        MatcherAssert.assertThat(originalNoIncompletePartRecoverable, S3RecoverableSerializerTest.isEqualTo(copiedNoIncompletePartRecoverable));
    }

    @Test
    public void serializeS3RecoverableOnlyWithIncompleteObject() throws IOException {
        S3Recoverable originalOnlyIncompletePartRecoverable = S3RecoverableSerializerTest.createTestS3Recoverable(true);
        byte[] serializedRecoverable = serializer.serialize(originalOnlyIncompletePartRecoverable);
        S3Recoverable copiedOnlyIncompletePartRecoverable = serializer.deserialize(1, serializedRecoverable);
        MatcherAssert.assertThat(originalOnlyIncompletePartRecoverable, S3RecoverableSerializerTest.isEqualTo(copiedOnlyIncompletePartRecoverable));
    }

    @Test
    public void serializeS3RecoverableWithCompleteAndIncompleteParts() throws IOException {
        S3Recoverable originalFullRecoverable = S3RecoverableSerializerTest.createTestS3Recoverable(true, 1, 5, 9);
        byte[] serializedRecoverable = serializer.serialize(originalFullRecoverable);
        S3Recoverable copiedFullRecoverable = serializer.deserialize(1, serializedRecoverable);
        MatcherAssert.assertThat(originalFullRecoverable, S3RecoverableSerializerTest.isEqualTo(copiedFullRecoverable));
    }
}

