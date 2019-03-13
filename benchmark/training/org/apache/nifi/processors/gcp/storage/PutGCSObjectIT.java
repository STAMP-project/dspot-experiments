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


import Acl.Entity.Type;
import Acl.Role;
import ListGCSBucket.REL_FAILURE;
import ListGCSBucket.REL_SUCCESS;
import PutGCSObject.ACL;
import PutGCSObject.ACL_BUCKET_OWNER_READ;
import PutGCSObject.BUCKET;
import PutGCSObject.ENCRYPTION_KEY;
import PutGCSObject.KEY;
import PutGCSObject.OVERWRITE;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link PutGCSObject} which actually use Google Cloud resources.
 */
public class PutGCSObjectIT extends AbstractGCSIT {
    private static final String KEY = "delete-me";

    private static final byte[] CONTENT = new byte[]{ 12, 13, 14 };

    @Test
    public void testSimplePut() throws Exception {
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new PutGCSObject());
        runner.setProperty(PutGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PutGCSObject.KEY, PutGCSObjectIT.KEY);
        runner.enqueue(PutGCSObjectIT.CONTENT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertTrue(fileEquals(PutGCSObjectIT.KEY, PutGCSObjectIT.CONTENT));
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeNotExists(StorageAttributes.ENCRYPTION_ALGORITHM_ATTR);
        for (Map.Entry<String, String> entry : flowFile.getAttributes().entrySet()) {
            System.out.println((((entry.getKey()) + ":") + (entry.getValue())));
        }
    }

    @Test
    public void testEncryptedPut() throws Exception {
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new PutGCSObject());
        runner.setProperty(PutGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PutGCSObject.KEY, PutGCSObjectIT.KEY);
        runner.setProperty(PutGCSObject.ENCRYPTION_KEY, AbstractGCSIT.ENCRYPTION_KEY);
        runner.enqueue(PutGCSObjectIT.CONTENT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertTrue(fileEqualsEncrypted(PutGCSObjectIT.KEY, PutGCSObjectIT.CONTENT));
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeExists(StorageAttributes.ENCRYPTION_ALGORITHM_ATTR);
        for (Map.Entry<String, String> entry : flowFile.getAttributes().entrySet()) {
            System.out.println((((entry.getKey()) + ":") + (entry.getValue())));
        }
    }

    @Test
    public void testPutWithAcl() throws Exception {
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new PutGCSObject());
        runner.setProperty(PutGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PutGCSObject.KEY, PutGCSObjectIT.KEY);
        runner.setProperty(ACL, ACL_BUCKET_OWNER_READ);
        runner.enqueue(PutGCSObjectIT.CONTENT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertTrue(fileEquals(PutGCSObjectIT.KEY, PutGCSObjectIT.CONTENT));
        final Blob blob = AbstractGCSIT.storage.get(BlobId.of(AbstractGCSIT.BUCKET, PutGCSObjectIT.KEY));
        boolean userIsOwner = false;
        boolean projectOwnerIsReader = false;
        for (Acl acl : blob.listAcls()) {
            if (((acl.getEntity().getType()) == (Type.USER)) && ((acl.getRole()) == (Role.OWNER))) {
                userIsOwner = true;
            }
            if (((acl.getEntity().getType()) == (Type.PROJECT)) && ((acl.getRole()) == (Role.READER))) {
                projectOwnerIsReader = true;
            }
        }
        Assert.assertTrue(userIsOwner);
        Assert.assertTrue(projectOwnerIsReader);
    }

    @Test
    public void testPutWithOverwrite() throws Exception {
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new PutGCSObject());
        runner.setProperty(PutGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PutGCSObject.KEY, PutGCSObjectIT.KEY);
        putTestFile(PutGCSObjectIT.KEY, new byte[]{ 1, 2 });
        runner.enqueue(PutGCSObjectIT.CONTENT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertTrue(fileEquals(PutGCSObjectIT.KEY, PutGCSObjectIT.CONTENT));
    }

    @Test
    public void testPutWithNoOverwrite() throws Exception {
        final TestRunner runner = AbstractGCSIT.buildNewRunner(new PutGCSObject());
        runner.setProperty(PutGCSObject.BUCKET, AbstractGCSIT.BUCKET);
        runner.setProperty(PutGCSObject.KEY, PutGCSObjectIT.KEY);
        runner.setProperty(OVERWRITE, "false");
        putTestFile(PutGCSObjectIT.KEY, new byte[]{ 1, 2 });
        runner.enqueue(PutGCSObjectIT.CONTENT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }
}

