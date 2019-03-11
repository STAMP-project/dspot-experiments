/**
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.kms.it;


import KeyManagementServiceGrpc.KeyManagementServiceBlockingStub;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import com.google.cloud.ServiceOptions;
import org.junit.Assert;
import org.junit.Test;


public class ITKmsTest {
    private static final String KMS_KEY_RING_LOCATION = "us";

    private static final String KMS_KEY_RING_NAME = "gcs_test_kms_key_ring";

    private static Metadata requestParamsHeader = new Metadata();

    private static Metadata.Key<String> requestParamsKey = Key.of("x-goog-request-params", ASCII_STRING_MARSHALLER);

    private static KeyManagementServiceBlockingStub kmsStub;

    @Test
    public void ensureKmsKeyRingExists() {
        String projectId = ServiceOptions.getDefaultProjectId();
        KeyRing keyRing = ITKmsTest.getKeyRing(ITKmsTest.kmsStub, projectId);
        Assert.assertNotNull(keyRing);
    }
}

