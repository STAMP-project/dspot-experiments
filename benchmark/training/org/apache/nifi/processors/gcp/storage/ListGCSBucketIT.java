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


import ListGCSBucket.BUCKET;
import ListGCSBucket.PREFIX;
import ListGCSBucket.REL_SUCCESS;
import ListGCSBucket.USE_GENERATIONS;
import StorageAttributes.GENERATION_ATTR;
import com.google.cloud.storage.BucketInfo;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link ListGCSBucket} which actually use Google Cloud resources.
 */
public class ListGCSBucketIT extends AbstractGCSIT {
    private static final byte[] CONTENT = new byte[]{ 12, 13, 14 };

    @Test
    public void testSimpleList() throws Exception {
        putTestFile("a", ListGCSBucketIT.CONTENT);
        putTestFile("b/c", ListGCSBucketIT.CONTENT);
        putTestFile("d/e", ListGCSBucketIT.CONTENT);
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new ListGCSBucket());
        runner.setProperty(ListGCSBucket.BUCKET, AbstractGCSIT.BUCKET);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "a");
        flowFiles.get(1).assertAttributeEquals("filename", "b/c");
        flowFiles.get(2).assertAttributeEquals("filename", "d/e");
    }

    @Test
    public void testSimpleListWithPrefix() throws Exception {
        putTestFile("a", ListGCSBucketIT.CONTENT);
        putTestFile("b/c", ListGCSBucketIT.CONTENT);
        putTestFile("d/e", ListGCSBucketIT.CONTENT);
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new ListGCSBucket());
        runner.setProperty(ListGCSBucket.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PREFIX, "b/");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "b/c");
    }

    @Test
    public void testSimpleListWithPrefixAndGenerations() throws Exception {
        // enable versioning
        AbstractGCSIT.storage.update(BucketInfo.newBuilder(AbstractGCSIT.BUCKET).setVersioningEnabled(true).build());
        putTestFile("generations/a", ListGCSBucketIT.CONTENT);
        putTestFile("generations/a", ListGCSBucketIT.CONTENT);
        putTestFile("generations/b", ListGCSBucketIT.CONTENT);
        putTestFile("generations/c", ListGCSBucketIT.CONTENT);
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new ListGCSBucket());
        runner.setProperty(ListGCSBucket.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PREFIX, "generations/");
        runner.setProperty(USE_GENERATIONS, "true");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "generations/a");
        flowFiles.get(1).assertAttributeEquals("filename", "generations/a");
        flowFiles.get(2).assertAttributeEquals("filename", "generations/b");
        flowFiles.get(3).assertAttributeEquals("filename", "generations/c");
        Assert.assertNotEquals(flowFiles.get(0).getAttribute(GENERATION_ATTR), flowFiles.get(1).getAttribute(GENERATION_ATTR));
    }

    @Test
    public void testCheckpointing() throws Exception {
        putTestFile("checkpoint/a", ListGCSBucketIT.CONTENT);
        putTestFile("checkpoint/b/c", ListGCSBucketIT.CONTENT);
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new ListGCSBucket());
        runner.setProperty(ListGCSBucket.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PREFIX, "checkpoint/");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "checkpoint/a");
        flowFiles.get(1).assertAttributeEquals("filename", "checkpoint/b/c");
        putTestFile("checkpoint/d/e", ListGCSBucketIT.CONTENT);
        runner.run();
        // Should only retrieve 1 new file (for a total of 3)
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "checkpoint/a");
        flowFiles.get(1).assertAttributeEquals("filename", "checkpoint/b/c");
        flowFiles.get(2).assertAttributeEquals("filename", "checkpoint/d/e");
    }
}

