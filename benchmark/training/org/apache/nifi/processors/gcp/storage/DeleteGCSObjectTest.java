/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.gcp.storage;


import DeleteGCSObject.BUCKET;
import DeleteGCSObject.GENERATION;
import DeleteGCSObject.KEY;
import DeleteGCSObject.REL_FAILURE;
import DeleteGCSObject.REL_SUCCESS;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.common.collect.ImmutableMap;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link DeleteGCSObject}. No connections to the Google Cloud service are made.
 */
public class DeleteGCSObjectTest extends AbstractGCSTest {
    public static final Long GENERATION = 42L;

    static final String KEY = "somefile";

    public static final String BUCKET_ATTR = "gcs.bucket";

    public static final String KEY_ATTR = "gcs.key";

    public static final String GENERATION_ATTR = "gcs.generation";

    @Mock
    Storage storage;

    @Test
    public void testDeleteWithValidArguments() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        runner.enqueue("testdata");
        runner.run();
        Mockito.verify(storage).delete(ArgumentMatchers.eq(BlobId.of(AbstractGCSTest.BUCKET, DeleteGCSObjectTest.KEY, DeleteGCSObjectTest.GENERATION)));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
    }

    @Test
    public void testTwoDeletesWithFlowfileAttributes() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        runner.setProperty(DeleteGCSObject.BUCKET, (("${" + (DeleteGCSObjectTest.BUCKET_ATTR)) + "}"));
        runner.setProperty(DeleteGCSObject.KEY, (("${" + (DeleteGCSObjectTest.KEY_ATTR)) + "}"));
        runner.setProperty(DeleteGCSObject.GENERATION, (("${" + (DeleteGCSObjectTest.GENERATION_ATTR)) + "}"));
        runner.assertValid();
        final String bucket1 = (AbstractGCSTest.BUCKET) + "_1";
        final String bucket2 = (AbstractGCSTest.BUCKET) + "_2";
        final String key1 = (DeleteGCSObjectTest.KEY) + "_1";
        final String key2 = (DeleteGCSObjectTest.KEY) + "_2";
        final Long generation1 = (DeleteGCSObjectTest.GENERATION) + 1L;
        final Long generation2 = (DeleteGCSObjectTest.GENERATION) + 2L;
        runner.enqueue("testdata1", ImmutableMap.of(DeleteGCSObjectTest.BUCKET_ATTR, bucket1, DeleteGCSObjectTest.KEY_ATTR, key1, DeleteGCSObjectTest.GENERATION_ATTR, String.valueOf(generation1)));
        runner.enqueue("testdata2", ImmutableMap.of(DeleteGCSObjectTest.BUCKET_ATTR, bucket2, DeleteGCSObjectTest.KEY_ATTR, key2, DeleteGCSObjectTest.GENERATION_ATTR, String.valueOf(generation2)));
        runner.run(2);
        Mockito.verify(storage).delete(ArgumentMatchers.eq(BlobId.of(bucket1, key1, generation1)));
        Mockito.verify(storage).delete(ArgumentMatchers.eq(BlobId.of(bucket2, key2, generation2)));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 2);
    }

    @Test
    public void testFailureOnException() throws Exception {
        Mockito.reset(storage);
        final TestRunner runner = AbstractGCSTest.buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        runner.enqueue("testdata");
        Mockito.when(storage.delete(ArgumentMatchers.any(BlobId.class))).thenThrow(new StorageException(1, "Test Exception"));
        runner.run();
        runner.assertPenalizeCount(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
        runner.assertTransferCount(REL_FAILURE, 1);
    }
}

