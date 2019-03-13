/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a.s3guard;


import Destroy.NAME;
import S3GuardTool.Import;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.AbstractS3ATestBase;
import org.apache.hadoop.fs.s3a.MultipartTestUtils;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3ATestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Test;

import static BucketInfo.ENCRYPTION_FLAG;
import static BucketInfo.GUARDED_FLAG;
import static BucketInfo.UNGUARDED_FLAG;


/**
 * Test S3Guard related CLI commands against a LocalMetadataStore.
 * Also responsible for testing the non s3guard-specific commands that, for
 * now, live under the s3guard CLI command.
 */
public class ITestS3GuardToolLocal extends AbstractS3GuardToolTestBase {
    private static final String LOCAL_METADATA = "local://metadata";

    private static final String[] ABORT_FORCE_OPTIONS = new String[]{ "-abort", "-force", "-verbose" };

    @Test
    public void testImportCommand() throws Exception {
        S3AFileSystem fs = getFileSystem();
        MetadataStore ms = getMetadataStore();
        Path parent = path("test-import");
        fs.mkdirs(parent);
        Path dir = new Path(parent, "a");
        fs.mkdirs(dir);
        Path emptyDir = new Path(parent, "emptyDir");
        fs.mkdirs(emptyDir);
        for (int i = 0; i < 10; i++) {
            String child = String.format("file-%d", i);
            try (FSDataOutputStream out = fs.create(new Path(dir, child))) {
                out.write(1);
            }
        }
        S3GuardTool.S3GuardTool.Import cmd = new S3GuardTool.S3GuardTool.Import(fs.getConf());
        cmd.setStore(ms);
        S3GuardToolTestHelper.exec(cmd, "import", parent.toString());
        DirListingMetadata children = ms.listChildren(dir);
        assertEquals("Unexpected number of paths imported", 10, children.getListing().size());
        assertEquals("Expected 2 items: empty directory and a parent directory", 2, ms.listChildren(parent).getListing().size());
        // assertTrue(children.isAuthoritative());
    }

    @Test
    public void testDestroyBucketExistsButNoTable() throws Throwable {
        run(NAME, "-meta", ITestS3GuardToolLocal.LOCAL_METADATA, S3ATestUtils.getLandsatCSVFile(getConfiguration()));
    }

    @Test
    public void testImportNoFilesystem() throws Throwable {
        final Import importer = new S3GuardTool.S3GuardTool.Import(getConfiguration());
        importer.setStore(getMetadataStore());
        intercept(IOException.class, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return importer.run(new String[]{ "import", "-meta", ITestS3GuardToolLocal.LOCAL_METADATA, AbstractS3GuardToolTestBase.S3A_THIS_BUCKET_DOES_NOT_EXIST });
            }
        });
    }

    @Test
    public void testInfoBucketAndRegionNoFS() throws Throwable {
        intercept(FileNotFoundException.class, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return run(BucketInfo.NAME, "-meta", ITestS3GuardToolLocal.LOCAL_METADATA, "-region", "any-region", AbstractS3GuardToolTestBase.S3A_THIS_BUCKET_DOES_NOT_EXIST);
            }
        });
    }

    @Test
    public void testInitNegativeRead() throws Throwable {
        runToFailure(INVALID_ARGUMENT, Init.NAME, "-meta", ITestS3GuardToolLocal.LOCAL_METADATA, "-region", "eu-west-1", READ_FLAG, "-10");
    }

    @Test
    public void testInit() throws Throwable {
        run(Init.NAME, "-meta", ITestS3GuardToolLocal.LOCAL_METADATA, "-region", "us-west-1");
    }

    @Test
    public void testInitTwice() throws Throwable {
        run(Init.NAME, "-meta", ITestS3GuardToolLocal.LOCAL_METADATA, "-region", "us-west-1");
        run(Init.NAME, "-meta", ITestS3GuardToolLocal.LOCAL_METADATA, "-region", "us-west-1");
    }

    @Test
    public void testLandsatBucketUnguarded() throws Throwable {
        run(BucketInfo.NAME, ("-" + (UNGUARDED_FLAG)), S3ATestUtils.getLandsatCSVFile(getConfiguration()));
    }

    @Test
    public void testLandsatBucketRequireGuarded() throws Throwable {
        runToFailure(E_BAD_STATE, BucketInfo.NAME, ("-" + (GUARDED_FLAG)), S3ATestUtils.getLandsatCSVFile(this.getConfiguration()));
    }

    @Test
    public void testLandsatBucketRequireUnencrypted() throws Throwable {
        run(BucketInfo.NAME, ("-" + (ENCRYPTION_FLAG)), "none", S3ATestUtils.getLandsatCSVFile(getConfiguration()));
    }

    @Test
    public void testLandsatBucketRequireEncrypted() throws Throwable {
        runToFailure(E_BAD_STATE, BucketInfo.NAME, ("-" + (ENCRYPTION_FLAG)), "AES256", S3ATestUtils.getLandsatCSVFile(this.getConfiguration()));
    }

    @Test
    public void testStoreInfo() throws Throwable {
        S3GuardTool.S3GuardTool.BucketInfo cmd = new S3GuardTool.S3GuardTool.BucketInfo(getFileSystem().getConf());
        cmd.setStore(getMetadataStore());
        String output = S3GuardToolTestHelper.exec(cmd, cmd.getName(), ("-" + (S3GuardTool.BucketInfo.GUARDED_FLAG)), getFileSystem().getUri().toString());
        AbstractS3ATestBase.LOG.info("Exec output=\n{}", output);
    }

    @Test
    public void testSetCapacity() throws Throwable {
        S3GuardTool.S3GuardTool cmd = new S3GuardTool.S3GuardTool.SetCapacity(getFileSystem().getConf());
        cmd.setStore(getMetadataStore());
        String output = S3GuardToolTestHelper.exec(cmd, cmd.getName(), ("-" + (READ_FLAG)), "100", ("-" + (WRITE_FLAG)), "100", getFileSystem().getUri().toString());
        AbstractS3ATestBase.LOG.info("Exec output=\n{}", output);
    }

    private static final String UPLOAD_PREFIX = "test-upload-prefix";

    private static final String UPLOAD_NAME = "test-upload";

    @Test
    public void testUploads() throws Throwable {
        S3AFileSystem fs = getFileSystem();
        Path path = path((((ITestS3GuardToolLocal.UPLOAD_PREFIX) + "/") + (ITestS3GuardToolLocal.UPLOAD_NAME)));
        describe("Cleaning up any leftover uploads from previous runs.");
        // 1. Make sure key doesn't already exist
        MultipartTestUtils.clearAnyUploads(fs, path);
        // 2. Confirm no uploads are listed via API
        MultipartTestUtils.assertNoUploadsAt(fs, path.getParent());
        // 3. Confirm no uploads are listed via CLI
        describe("Confirming CLI lists nothing.");
        assertNumUploads(path, 0);
        // 4. Create a upload part
        describe("Uploading single part.");
        MultipartTestUtils.createPartUpload(fs, fs.pathToKey(path), 128, 1);
        try {
            // 5. Confirm it exists via API..
            /* 5 seconds until failure */
            /* one second retry interval */
            LambdaTestUtils.eventually(5000, 1000, () -> {
                assertEquals("Should be one upload", 1, countUploadsAt(fs, path));
            });
            // 6. Confirm part exists via CLI, direct path and parent path
            describe("Confirming CLI lists one part");
            LambdaTestUtils.eventually(5000, 1000, () -> {
                assertNumUploads(path, 1);
            });
            LambdaTestUtils.eventually(5000, 1000, () -> {
                assertNumUploads(path.getParent(), 1);
            });
            // 7. Use CLI to delete part, assert it worked
            describe("Deleting part via CLI");
            assertNumDeleted(fs, path, 1);
            // 8. Confirm deletion via API
            describe("Confirming deletion via API");
            assertEquals("Should be no uploads", 0, MultipartTestUtils.countUploadsAt(fs, path));
            // 9. Confirm no uploads are listed via CLI
            describe("Confirming CLI lists nothing.");
            assertNumUploads(path, 0);
        } catch (Throwable t) {
            // Clean up on intermediate failure
            MultipartTestUtils.clearAnyUploads(fs, path);
            throw t;
        }
    }

    @Test
    public void testUploadListByAge() throws Throwable {
        S3AFileSystem fs = getFileSystem();
        Path path = path((((ITestS3GuardToolLocal.UPLOAD_PREFIX) + "/") + (ITestS3GuardToolLocal.UPLOAD_NAME)));
        describe("Cleaning up any leftover uploads from previous runs.");
        // 1. Make sure key doesn't already exist
        MultipartTestUtils.clearAnyUploads(fs, path);
        // 2. Create a upload part
        describe("Uploading single part.");
        MultipartTestUtils.createPartUpload(fs, fs.pathToKey(path), 128, 1);
        try {
            // 3. Confirm it exists via API.. may want to wrap with
            // LambdaTestUtils.eventually() ?
            LambdaTestUtils.eventually(5000, 1000, () -> {
                assertEquals("Should be one upload", 1, countUploadsAt(fs, path));
            });
            // 4. Confirm part does appear in listing with long age filter
            describe("Confirming CLI older age doesn't list");
            assertNumUploadsAge(path, 0, 600);
            // 5. Confirm part does not get deleted with long age filter
            describe("Confirming CLI older age doesn't delete");
            uploadCommandAssertCount(fs, ITestS3GuardToolLocal.ABORT_FORCE_OPTIONS, path, 0, 600);
            // 6. Wait a second and then assert the part is in listing of things at
            // least a second old
            describe("Sleeping 1 second then confirming upload still there");
            Thread.sleep(1000);
            LambdaTestUtils.eventually(5000, 1000, () -> {
                assertNumUploadsAge(path, 1, 1);
            });
            // 7. Assert deletion works when age filter matches
            describe("Doing aged deletion");
            uploadCommandAssertCount(fs, ITestS3GuardToolLocal.ABORT_FORCE_OPTIONS, path, 1, 1);
            describe("Confirming age deletion happened");
            assertEquals("Should be no uploads", 0, MultipartTestUtils.countUploadsAt(fs, path));
        } catch (Throwable t) {
            // Clean up on intermediate failure
            MultipartTestUtils.clearAnyUploads(fs, path);
            throw t;
        }
    }

    @Test
    public void testUploadNegativeExpect() throws Throwable {
        runToFailure(E_BAD_STATE, Uploads.NAME, "-expect", "1", path("/we/are/almost/postive/this/doesnt/exist/fhfsadfoijew").toString());
    }
}

