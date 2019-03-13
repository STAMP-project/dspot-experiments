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


import CoreAttributes.FILENAME;
import DeleteGCSObject.BUCKET;
import DeleteGCSObject.KEY;
import DeleteGCSObject.REL_SUCCESS;
import com.google.common.collect.ImmutableMap;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link DeleteGCSObject} which actually use Google Cloud resources.
 */
public class DeleteGCSObjectIT extends AbstractGCSIT {
    static final String KEY = "delete-me";

    @Test
    public void testSimpleDeleteWithFilename() throws Exception {
        putTestFile(DeleteGCSObjectIT.KEY, new byte[]{ 7, 8, 9 });
        Assert.assertTrue(fileExists(DeleteGCSObjectIT.KEY));
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new DeleteGCSObject());
        runner.setProperty(DeleteGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.assertValid();
        runner.enqueue("testdata", ImmutableMap.of(FILENAME.key(), DeleteGCSObjectIT.KEY));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        Assert.assertFalse(fileExists(DeleteGCSObjectIT.KEY));
    }

    @Test
    public void testSimpleDeleteWithPropertySet() throws Exception {
        putTestFile(DeleteGCSObjectIT.KEY, new byte[]{ 7, 8, 9 });
        Assert.assertTrue(fileExists(DeleteGCSObjectIT.KEY));
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new DeleteGCSObject());
        runner.setProperty(DeleteGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(DeleteGCSObject.KEY, DeleteGCSObjectIT.KEY);
        runner.assertValid();
        runner.enqueue("testdata", ImmutableMap.of("filename", "different-filename"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
        Assert.assertFalse(fileExists(DeleteGCSObjectIT.KEY));
    }

    @Test
    public void testDeleteNonExistentFile() throws Exception {
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new DeleteGCSObject());
        runner.setProperty(DeleteGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.assertValid();
        runner.enqueue("testdata", ImmutableMap.of("filename", "nonexistant-file"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
    }
}

