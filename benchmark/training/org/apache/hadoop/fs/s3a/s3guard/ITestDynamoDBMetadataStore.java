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
package org.apache.hadoop.fs.s3a.s3guard;


import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ListTagsOfResourceRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import com.amazonaws.services.dynamodbv2.model.Tag;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.s3a.S3AContract;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that {@link DynamoDBMetadataStore} implements {@link MetadataStore}.
 *
 * In this integration test, we use a real AWS DynamoDB. A
 * {@link DynamoDBMetadataStore} object is created in the @BeforeClass method,
 * and shared for all test in the @BeforeClass method. You will be charged
 * bills for AWS S3 or DynamoDB when you run these tests.
 *
 * According to the base class, every test case will have independent contract
 * to create a new {@link S3AFileSystem} instance and initializes it.
 * A table will be created and shared between the tests,
 */
public class ITestDynamoDBMetadataStore extends MetadataStoreTestBase {
    public ITestDynamoDBMetadataStore() {
        super();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ITestDynamoDBMetadataStore.class);

    public static final PrimaryKey VERSION_MARKER_PRIMARY_KEY = createVersionMarkerPrimaryKey(DynamoDBMetadataStore.VERSION_MARKER);

    private S3AFileSystem fileSystem;

    private S3AContract s3AContract;

    private URI fsUri;

    private String bucket;

    private static DynamoDBMetadataStore.DynamoDBMetadataStore ddbmsStatic;

    private static String testDynamoDBTableName;

    /**
     * Each contract has its own S3AFileSystem and DynamoDBMetadataStore objects.
     */
    private class DynamoDBMSContract extends AbstractMSContract {
        DynamoDBMSContract(Configuration conf) {
        }

        DynamoDBMSContract() {
            this(new Configuration());
        }

        @Override
        public S3AFileSystem getFileSystem() {
            return ITestDynamoDBMetadataStore.this.fileSystem;
        }

        @Override
        public DynamoDBMetadataStore.DynamoDBMetadataStore getMetadataStore() {
            return ITestDynamoDBMetadataStore.ddbmsStatic;
        }
    }

    /**
     * This tests that after initialize() using an S3AFileSystem object, the
     * instance should have been initialized successfully, and tables are ACTIVE.
     */
    @Test
    public void testInitialize() throws IOException {
        final S3AFileSystem s3afs = this.fileSystem;
        final String tableName = "testInitialize";
        final Configuration conf = s3afs.getConf();
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(s3afs);
            verifyTableInitialized(tableName, ddbms.getDynamoDB());
            assertNotNull(ddbms.getTable());
            assertEquals(tableName, ddbms.getTable().getTableName());
            String expectedRegion = conf.get(S3GUARD_DDB_REGION_KEY, s3afs.getBucketLocation(bucket));
            assertEquals(("DynamoDB table should be in configured region or the same" + " region as S3 bucket"), expectedRegion, ddbms.getRegion());
            ddbms.destroy();
        }
    }

    /**
     * This tests that after initialize() using a Configuration object, the
     * instance should have been initialized successfully, and tables are ACTIVE.
     */
    @Test
    public void testInitializeWithConfiguration() throws IOException {
        final String tableName = "testInitializeWithConfiguration";
        final Configuration conf = getFileSystem().getConf();
        conf.unset(S3GUARD_DDB_TABLE_NAME_KEY);
        String savedRegion = conf.get(S3GUARD_DDB_REGION_KEY, getFileSystem().getBucketLocation());
        conf.unset(S3GUARD_DDB_REGION_KEY);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(conf);
            fail("Should have failed because the table name is not set!");
        } catch (IllegalArgumentException ignored) {
        }
        // config table name
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(conf);
            fail("Should have failed because as the region is not set!");
        } catch (IllegalArgumentException ignored) {
        }
        // config region
        conf.set(S3GUARD_DDB_REGION_KEY, savedRegion);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(conf);
            verifyTableInitialized(tableName, ddbms.getDynamoDB());
            assertNotNull(ddbms.getTable());
            assertEquals(tableName, ddbms.getTable().getTableName());
            assertEquals("Unexpected key schema found!", keySchema(), ddbms.getTable().describe().getKeySchema());
            ddbms.destroy();
        }
    }

    /**
     * Test that for a large batch write request, the limit is handled correctly.
     */
    @Test
    public void testBatchWrite() throws IOException {
        final int[] numMetasToDeleteOrPut = new int[]{ -1// null
        , 0// empty collection
        , 1// one path
        , S3GUARD_DDB_BATCH_WRITE_REQUEST_LIMIT// exact limit of a batch request
        , (S3GUARD_DDB_BATCH_WRITE_REQUEST_LIMIT) + 1// limit + 1
         };
        DynamoDBMetadataStore.DynamoDBMetadataStore ms = getDynamoMetadataStore();
        for (int numOldMetas : numMetasToDeleteOrPut) {
            for (int numNewMetas : numMetasToDeleteOrPut) {
                doTestBatchWrite(numOldMetas, numNewMetas, ms);
            }
        }
    }

    @Test
    public void testInitExistingTable() throws IOException {
        final DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = getDynamoMetadataStore();
        final String tableName = ddbms.getTable().getTableName();
        verifyTableInitialized(tableName, ddbms.getDynamoDB());
        // create existing table
        ddbms.initTable();
        verifyTableInitialized(tableName, ddbms.getDynamoDB());
    }

    /**
     * Test the low level version check code.
     */
    @Test
    public void testItemVersionCompatibility() throws Throwable {
        verifyVersionCompatibility("table", createVersionMarker(VERSION_MARKER, VERSION, 0));
    }

    /**
     * Test that a version marker entry without the version number field
     * is rejected as incompatible with a meaningful error message.
     */
    @Test
    public void testItemLacksVersion() throws Throwable {
        intercept(IOException.class, E_NOT_VERSION_MARKER, () -> verifyVersionCompatibility("table", new Item().withPrimaryKey(createVersionMarkerPrimaryKey(VERSION_MARKER))));
    }

    /**
     * Delete the version marker and verify that table init fails.
     */
    @Test
    public void testTableVersionRequired() throws Exception {
        String tableName = "testTableVersionRequired";
        Configuration conf = getFileSystem().getConf();
        int maxRetries = conf.getInt(S3GUARD_DDB_MAX_RETRIES, S3GUARD_DDB_MAX_RETRIES_DEFAULT);
        conf.setInt(S3GUARD_DDB_MAX_RETRIES, 3);
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(conf);
            Table table = verifyTableInitialized(tableName, ddbms.getDynamoDB());
            table.deleteItem(ITestDynamoDBMetadataStore.VERSION_MARKER_PRIMARY_KEY);
            // create existing table
            intercept(IOException.class, E_NO_VERSION_MARKER, () -> ddbms.initTable());
            conf.setInt(S3GUARD_DDB_MAX_RETRIES, maxRetries);
            ddbms.destroy();
        }
    }

    /**
     * Set the version value to a different number and verify that
     * table init fails.
     */
    @Test
    public void testTableVersionMismatch() throws Exception {
        String tableName = "testTableVersionMismatch";
        Configuration conf = getFileSystem().getConf();
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(conf);
            Table table = verifyTableInitialized(tableName, ddbms.getDynamoDB());
            table.deleteItem(ITestDynamoDBMetadataStore.VERSION_MARKER_PRIMARY_KEY);
            Item v200 = createVersionMarker(VERSION_MARKER, 200, 0);
            table.putItem(v200);
            // create existing table
            intercept(IOException.class, E_INCOMPATIBLE_VERSION, () -> ddbms.initTable());
            ddbms.destroy();
        }
    }

    /**
     * Test that initTable fails with IOException when table does not exist and
     * table auto-creation is disabled.
     */
    @Test
    public void testFailNonexistentTable() throws IOException {
        final String tableName = "testFailNonexistentTable";
        final S3AFileSystem s3afs = getFileSystem();
        final Configuration conf = s3afs.getConf();
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        conf.unset(S3GUARD_DDB_TABLE_CREATE_KEY);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(s3afs);
            fail(("Should have failed as table does not exist and table auto-creation" + " is disabled"));
        } catch (IOException ignored) {
        }
    }

    /**
     * Test cases about root directory as it is not in the DynamoDB table.
     */
    @Test
    public void testRootDirectory() throws IOException {
        final DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = getDynamoMetadataStore();
        Path rootPath = new Path(new Path(fsUri), "/");
        verifyRootDirectory(ddbms.get(rootPath), true);
        ddbms.put(new PathMetadata(new org.apache.hadoop.fs.s3a.S3AFileStatus(true, new Path(rootPath, "foo"), UserGroupInformation.getCurrentUser().getShortUserName())));
        verifyRootDirectory(ddbms.get(rootPath), false);
    }

    /**
     * Test that when moving nested paths, all its ancestors up to destination
     * root will also be created.
     * Here is the directory tree before move:
     * <pre>
     * testMovePopulateAncestors
     * ??? a
     * ??? ??? b
     * ???     ??? src
     * ???         ??? dir1
     * ???         ??? ??? dir2
     * ???         ??? file1.txt
     * ??? c
     *     ??? d
     *         ??? dest
     * </pre>
     * As part of rename(a/b/src, d/c/dest), S3A will enumerate the subtree at
     * a/b/src.  This test verifies that after the move, the new subtree at
     * 'dest' is reachable from the root (i.e. c/ and c/d exist in the table.
     * DynamoDBMetadataStore depends on this property to do recursive delete
     * without a full table scan.
     */
    @Test
    public void testMovePopulatesAncestors() throws IOException {
        final DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = getDynamoMetadataStore();
        final String testRoot = "/testMovePopulatesAncestors";
        final String srcRoot = testRoot + "/a/b/src";
        final String destRoot = testRoot + "/c/d/e/dest";
        final Path nestedPath1 = strToPath((srcRoot + "/file1.txt"));
        ddbms.put(new PathMetadata(basicFileStatus(nestedPath1, 1024, false)));
        final Path nestedPath2 = strToPath((srcRoot + "/dir1/dir2"));
        ddbms.put(new PathMetadata(basicFileStatus(nestedPath2, 0, true)));
        // We don't put the destRoot path here, since put() would create ancestor
        // entries, and we want to ensure that move() does it, instead.
        // Build enumeration of src / dest paths and do the move()
        final Collection<Path> fullSourcePaths = Lists.newArrayList(strToPath(srcRoot), strToPath((srcRoot + "/dir1")), strToPath((srcRoot + "/dir1/dir2")), strToPath((srcRoot + "/file1.txt")));
        final Collection<PathMetadata> pathsToCreate = Lists.newArrayList(new PathMetadata(basicFileStatus(strToPath(destRoot), 0, true)), new PathMetadata(basicFileStatus(strToPath((destRoot + "/dir1")), 0, true)), new PathMetadata(basicFileStatus(strToPath((destRoot + "/dir1/dir2")), 0, true)), new PathMetadata(basicFileStatus(strToPath((destRoot + "/file1.txt")), 1024, false)));
        ddbms.move(fullSourcePaths, pathsToCreate);
        // assert that all the ancestors should have been populated automatically
        assertCached((testRoot + "/c"));
        assertCached((testRoot + "/c/d"));
        assertCached((testRoot + "/c/d/e"));
        /* /c/d/e/dest */
        assertCached(destRoot);
        // Also check moved files while we're at it
        assertCached((destRoot + "/dir1"));
        assertCached((destRoot + "/dir1/dir2"));
        assertCached((destRoot + "/file1.txt"));
    }

    @Test
    public void testProvisionTable() throws Exception {
        final String tableName = "testProvisionTable-" + (UUID.randomUUID());
        Configuration conf = getFileSystem().getConf();
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(conf);
            DynamoDB dynamoDB = ddbms.getDynamoDB();
            final ProvisionedThroughputDescription oldProvision = dynamoDB.getTable(tableName).describe().getProvisionedThroughput();
            ddbms.provisionTable(((oldProvision.getReadCapacityUnits()) * 2), ((oldProvision.getWriteCapacityUnits()) * 2));
            ddbms.initTable();
            // we have to wait until the provisioning settings are applied,
            // so until the table is ACTIVE again and not in UPDATING
            ddbms.getTable().waitForActive();
            final ProvisionedThroughputDescription newProvision = dynamoDB.getTable(tableName).describe().getProvisionedThroughput();
            ITestDynamoDBMetadataStore.LOG.info("Old provision = {}, new provision = {}", oldProvision, newProvision);
            assertEquals("Check newly provisioned table read capacity units.", ((oldProvision.getReadCapacityUnits()) * 2), newProvision.getReadCapacityUnits().longValue());
            assertEquals("Check newly provisioned table write capacity units.", ((oldProvision.getWriteCapacityUnits()) * 2), newProvision.getWriteCapacityUnits().longValue());
            ddbms.destroy();
        }
    }

    @Test
    public void testDeleteTable() throws Exception {
        final String tableName = "testDeleteTable";
        Path testPath = new Path(new Path(fsUri), ("/" + tableName));
        final S3AFileSystem s3afs = getFileSystem();
        final Configuration conf = s3afs.getConf();
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(s3afs);
            // we can list the empty table
            ddbms.listChildren(testPath);
            DynamoDB dynamoDB = ddbms.getDynamoDB();
            ddbms.destroy();
            verifyTableNotExist(tableName, dynamoDB);
            // delete table once more; be ResourceNotFoundException swallowed silently
            ddbms.destroy();
            verifyTableNotExist(tableName, dynamoDB);
            try {
                // we can no longer list the destroyed table
                ddbms.listChildren(testPath);
                fail("Should have failed after the table is destroyed!");
            } catch (IOException ignored) {
            }
            ddbms.destroy();
        }
    }

    @Test
    public void testTableTagging() throws IOException {
        final Configuration conf = getFileSystem().getConf();
        // clear all table tagging config before this test
        conf.getPropsWithPrefix(S3GUARD_DDB_TABLE_TAG).keySet().forEach(( propKey) -> conf.unset((S3GUARD_DDB_TABLE_TAG + propKey)));
        String tableName = "testTableTagging-" + (UUID.randomUUID());
        conf.set(S3GUARD_DDB_TABLE_NAME_KEY, tableName);
        conf.set(S3GUARD_DDB_TABLE_CREATE_KEY, "true");
        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("hello", "dynamo");
        tagMap.put("tag", "youre it");
        for (Map.Entry<String, String> tagEntry : tagMap.entrySet()) {
            conf.set(((S3GUARD_DDB_TABLE_TAG) + (tagEntry.getKey())), tagEntry.getValue());
        }
        try (DynamoDBMetadataStore.DynamoDBMetadataStore ddbms = new DynamoDBMetadataStore.DynamoDBMetadataStore()) {
            ddbms.initialize(conf);
            assertNotNull(ddbms.getTable());
            assertEquals(tableName, ddbms.getTable().getTableName());
            ListTagsOfResourceRequest listTagsOfResourceRequest = new ListTagsOfResourceRequest().withResourceArn(ddbms.getTable().getDescription().getTableArn());
            List<Tag> tags = ddbms.getAmazonDynamoDB().listTagsOfResource(listTagsOfResourceRequest).getTags();
            assertEquals(tagMap.size(), tags.size());
            for (Tag tag : tags) {
                Assert.assertEquals(tagMap.get(tag.getKey()), tag.getValue());
            }
        }
    }

    @Test
    public void testGetEmptyDirFlagCanSetTrue() throws IOException {
        boolean authoritativeDirectoryListing = true;
        testGetEmptyDirFlagCanSetTrueOrUnknown(authoritativeDirectoryListing);
    }

    @Test
    public void testGetEmptyDirFlagCanSetUnknown() throws IOException {
        boolean authoritativeDirectoryListing = false;
        testGetEmptyDirFlagCanSetTrueOrUnknown(authoritativeDirectoryListing);
    }
}

