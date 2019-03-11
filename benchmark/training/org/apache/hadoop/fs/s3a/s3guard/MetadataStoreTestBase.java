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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.s3a.S3ATestUtils;
import org.apache.hadoop.fs.s3a.Tristate;
import org.apache.hadoop.test.HadoopTestBase;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main test class for MetadataStore implementations.
 * Implementations should each create a test by subclassing this and
 * overriding {@link #createContract()}.
 * If your implementation may return missing results for recently set paths,
 * override {@link MetadataStoreTestBase#allowMissing()}.
 */
public abstract class MetadataStoreTestBase extends HadoopTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataStoreTestBase.class);

    /**
     * Some dummy values for sanity-checking FileStatus contents.
     */
    static final long BLOCK_SIZE = (32 * 1024) * 1024;

    static final int REPLICATION = 1;

    static final FsPermission PERMISSION = new FsPermission(((short) (493)));

    static final String OWNER = "bob";

    static final String GROUP = "uncles";

    private final long accessTime = System.currentTimeMillis();

    private final long modTime = (accessTime) - 5000;

    /**
     * The MetadataStore contract used to test against.
     */
    private AbstractMSContract contract;

    private MetadataStore ms;

    /**
     * Test that we can get the whole sub-tree by iterating DescendantsIterator.
     *
     * The tree is similar to or same as the example in code comment.
     */
    @Test
    public void testDescendantsIterator() throws Exception {
        final String[] tree = new String[]{ "/dir1", "/dir1/dir2", "/dir1/dir3", "/dir1/dir2/file1", "/dir1/dir2/file2", "/dir1/dir3/dir4", "/dir1/dir3/dir5", "/dir1/dir3/dir4/file3", "/dir1/dir3/dir5/file4", "/dir1/dir3/dir6" };
        doTestDescendantsIterator(DescendantsIterator.class, tree, tree);
    }

    /**
     * Test that we can get the correct subset of the tree with
     * MetadataStoreListFilesIterator.
     *
     * The tree is similar to or same as the example in code comment.
     */
    @Test
    public void testMetadataStoreListFilesIterator() throws Exception {
        final String[] wholeTree = new String[]{ "/dir1", "/dir1/dir2", "/dir1/dir3", "/dir1/dir2/file1", "/dir1/dir2/file2", "/dir1/dir3/dir4", "/dir1/dir3/dir5", "/dir1/dir3/dir4/file3", "/dir1/dir3/dir5/file4", "/dir1/dir3/dir6" };
        final String[] leafNodes = new String[]{ "/dir1/dir2/file1", "/dir1/dir2/file2", "/dir1/dir3/dir4/file3", "/dir1/dir3/dir5/file4" };
        doTestDescendantsIterator(MetadataStoreListFilesIterator.class, wholeTree, leafNodes);
    }

    @Test
    public void testPutNew() throws Exception {
        /* create three dirs /da1, /da2, /da3 */
        createNewDirs("/da1", "/da2", "/da3");
        /* It is caller's responsibility to set up ancestor entries beyond the
        containing directory.  We only track direct children of the directory.
        Thus this will not affect entry for /da1.
         */
        ms.put(new PathMetadata(makeFileStatus("/da1/db1/fc1", 100)));
        assertEmptyDirs("/da2", "/da3");
        assertDirectorySize("/da1/db1", 1);
        /* Check contents of dir status. */
        PathMetadata dirMeta = ms.get(strToPath("/da1"));
        if ((!(allowMissing())) || (dirMeta != null)) {
            verifyDirStatus(dirMeta.getFileStatus());
        }
        /* This already exists, and should silently replace it. */
        ms.put(new PathMetadata(makeDirStatus("/da1/db1")));
        /* If we had putNew(), and used it above, this would be empty again. */
        assertDirectorySize("/da1", 1);
        assertEmptyDirs("/da2", "/da3");
        /* Ensure new files update correct parent dirs. */
        ms.put(new PathMetadata(makeFileStatus("/da1/db1/fc1", 100)));
        ms.put(new PathMetadata(makeFileStatus("/da1/db1/fc2", 200)));
        assertDirectorySize("/da1", 1);
        assertDirectorySize("/da1/db1", 2);
        assertEmptyDirs("/da2", "/da3");
        PathMetadata meta = ms.get(strToPath("/da1/db1/fc2"));
        if ((!(allowMissing())) || (meta != null)) {
            assertNotNull("Get file after put new.", meta);
            verifyFileStatus(meta.getFileStatus(), 200);
        }
    }

    @Test
    public void testPutOverwrite() throws Exception {
        final String filePath = "/a1/b1/c1/some_file";
        final String dirPath = "/a1/b1/c1/d1";
        ms.put(new PathMetadata(makeFileStatus(filePath, 100)));
        ms.put(new PathMetadata(makeDirStatus(dirPath)));
        PathMetadata meta = ms.get(strToPath(filePath));
        if ((!(allowMissing())) || (meta != null)) {
            verifyFileStatus(meta.getFileStatus(), 100);
        }
        ms.put(new PathMetadata(basicFileStatus(strToPath(filePath), 9999, false)));
        meta = ms.get(strToPath(filePath));
        if ((!(allowMissing())) || (meta != null)) {
            verifyFileStatus(meta.getFileStatus(), 9999);
        }
    }

    @Test
    public void testRootDirPutNew() throws Exception {
        Path rootPath = strToPath("/");
        ms.put(new PathMetadata(makeFileStatus("/file1", 100)));
        DirListingMetadata dir = ms.listChildren(rootPath);
        if ((!(allowMissing())) || (dir != null)) {
            assertNotNull("Root dir cached", dir);
            assertFalse("Root not fully cached", dir.isAuthoritative());
            assertNotNull("have root dir file listing", dir.getListing());
            assertEquals("One file in root dir", 1, dir.getListing().size());
            assertEquals("file1 in root dir", strToPath("/file1"), dir.getListing().iterator().next().getFileStatus().getPath());
        }
    }

    @Test
    public void testDelete() throws Exception {
        setUpDeleteTest();
        ms.delete(strToPath("/ADirectory1/db1/file2"));
        /* Ensure delete happened. */
        assertDirectorySize("/ADirectory1/db1", 1);
        PathMetadata meta = ms.get(strToPath("/ADirectory1/db1/file2"));
        assertTrue("File deleted", ((meta == null) || (meta.isDeleted())));
    }

    @Test
    public void testDeleteSubtree() throws Exception {
        deleteSubtreeHelper("");
    }

    @Test
    public void testDeleteSubtreeHostPath() throws Exception {
        deleteSubtreeHelper(contract.getFileSystem().getUri().toString());
    }

    /* Some implementations might not support this.  It was useful to test
    correctness of the LocalMetadataStore implementation, but feel free to
    override this to be a no-op.
     */
    @Test
    public void testDeleteRecursiveRoot() throws Exception {
        setUpDeleteTest();
        ms.deleteSubtree(strToPath("/"));
        assertDeleted("/ADirectory1");
        assertDeleted("/ADirectory2");
        assertDeleted("/ADirectory2/db1");
        assertDeleted("/ADirectory2/db1/file1");
        assertDeleted("/ADirectory2/db1/file2");
    }

    @Test
    public void testDeleteNonExisting() throws Exception {
        // Path doesn't exist, but should silently succeed
        ms.delete(strToPath("/bobs/your/uncle"));
        // Ditto.
        ms.deleteSubtree(strToPath("/internets"));
    }

    @Test
    public void testGet() throws Exception {
        final String filePath = "/a1/b1/c1/some_file";
        final String dirPath = "/a1/b1/c1/d1";
        ms.put(new PathMetadata(makeFileStatus(filePath, 100)));
        ms.put(new PathMetadata(makeDirStatus(dirPath)));
        PathMetadata meta = ms.get(strToPath(filePath));
        if ((!(allowMissing())) || (meta != null)) {
            assertNotNull("Get found file", meta);
            verifyFileStatus(meta.getFileStatus(), 100);
        }
        if (!((ms) instanceof NullMetadataStore)) {
            ms.delete(strToPath(filePath));
            meta = ms.get(strToPath(filePath));
            assertTrue("Tombstone not left for deleted file", meta.isDeleted());
        }
        meta = ms.get(strToPath(dirPath));
        if ((!(allowMissing())) || (meta != null)) {
            assertNotNull("Get found file (dir)", meta);
            assertTrue("Found dir", meta.getFileStatus().isDirectory());
        }
        meta = ms.get(strToPath("/bollocks"));
        assertNull("Don't get non-existent file", meta);
    }

    @Test
    public void testGetEmptyDir() throws Exception {
        final String dirPath = "/a1/b1/c1/d1";
        // Creates /a1/b1/c1/d1 as an empty dir
        setupListStatus();
        // 1. Tell MetadataStore (MS) that there are zero children
        /* authoritative */
        /* zero children */
        putListStatusFiles(dirPath, true);
        // 2. Request a file status for dir, including whether or not the dir
        // is empty.
        PathMetadata meta = ms.get(strToPath(dirPath), true);
        // 3. Check that either (a) the MS doesn't track whether or not it is
        // empty (which is allowed), or (b) the MS knows the dir is empty.
        if ((!(allowMissing())) || (meta != null)) {
            assertNotNull("Get should find meta for dir", meta);
            assertNotEquals("Dir is empty or unknown", Tristate.FALSE, meta.isEmptyDirectory());
        }
    }

    @Test
    public void testGetNonEmptyDir() throws Exception {
        final String dirPath = "/a1/b1/c1";
        // Creates /a1/b1/c1 as an non-empty dir
        setupListStatus();
        // Request a file status for dir, including whether or not the dir
        // is empty.
        PathMetadata meta = ms.get(strToPath(dirPath), true);
        // MetadataStore knows /a1/b1/c1 has at least one child.  It is valid
        // for it to answer either (a) UNKNOWN: the MS doesn't track whether
        // or not the dir is empty, or (b) the MS knows the dir is non-empty.
        if ((!(allowMissing())) || (meta != null)) {
            assertNotNull("Get should find meta for dir", meta);
            assertNotEquals("Dir is non-empty or unknown", Tristate.TRUE, meta.isEmptyDirectory());
        }
    }

    @Test
    public void testGetDirUnknownIfEmpty() throws Exception {
        final String dirPath = "/a1/b1/c1/d1";
        // 1. Create /a1/b1/c1/d1 as an empty dir, but do not tell MetadataStore
        // (MS) whether or not it has any children.
        setupListStatus();
        // 2. Request a file status for dir, including whether or not the dir
        // is empty.
        PathMetadata meta = ms.get(strToPath(dirPath), true);
        // 3. Assert MS reports isEmptyDir as UNKONWN: We haven't told MS
        // whether or not the directory has any children.
        if ((!(allowMissing())) || (meta != null)) {
            assertNotNull("Get should find meta for dir", meta);
            assertEquals("Dir empty is unknown", Tristate.UNKNOWN, meta.isEmptyDirectory());
        }
    }

    @Test
    public void testListChildren() throws Exception {
        setupListStatus();
        DirListingMetadata dirMeta;
        dirMeta = ms.listChildren(strToPath("/"));
        if (!(allowMissing())) {
            assertNotNull(dirMeta);
            /* Cache has no way of knowing it has all entries for root unless we
            specifically tell it via put() with
            DirListingMetadata.isAuthoritative = true
             */
            assertFalse("Root dir is not cached, or partially cached", dirMeta.isAuthoritative());
            assertListingsEqual(dirMeta.getListing(), "/a1", "/a2");
        }
        dirMeta = ms.listChildren(strToPath("/a1"));
        if ((!(allowMissing())) || (dirMeta != null)) {
            dirMeta = dirMeta.withoutTombstones();
            assertListingsEqual(dirMeta.getListing(), "/a1/b1", "/a1/b2");
        }
        dirMeta = ms.listChildren(strToPath("/a1/b1"));
        if ((!(allowMissing())) || (dirMeta != null)) {
            assertListingsEqual(dirMeta.getListing(), "/a1/b1/file1", "/a1/b1/file2", "/a1/b1/c1");
        }
    }

    @Test
    public void testListChildrenAuthoritative() throws IOException {
        Assume.assumeTrue(("MetadataStore should be capable for authoritative " + "storage of directories to run this test."), S3ATestUtils.metadataStorePersistsAuthoritativeBit(ms));
        setupListStatus();
        DirListingMetadata dirMeta = ms.listChildren(strToPath("/a1/b1"));
        dirMeta.setAuthoritative(true);
        dirMeta.put(makeFileStatus("/a1/b1/file_new", 100));
        ms.put(dirMeta);
        dirMeta = ms.listChildren(strToPath("/a1/b1"));
        assertListingsEqual(dirMeta.getListing(), "/a1/b1/file1", "/a1/b1/file2", "/a1/b1/c1", "/a1/b1/file_new");
        assertTrue(dirMeta.isAuthoritative());
    }

    @Test
    public void testDirListingRoot() throws Exception {
        commonTestPutListStatus("/");
    }

    @Test
    public void testPutDirListing() throws Exception {
        commonTestPutListStatus("/a");
    }

    @Test
    public void testInvalidListChildren() throws Exception {
        setupListStatus();
        assertNull("missing path returns null", ms.listChildren(strToPath("/a1/b1x")));
    }

    @Test
    public void testMove() throws Exception {
        // Create test dir structure
        createNewDirs("/a1", "/a2", "/a3");
        createNewDirs("/a1/b1", "/a1/b2");
        putListStatusFiles("/a1/b1", false, "/a1/b1/file1", "/a1/b1/file2");
        // Assert root listing as expected
        Collection<PathMetadata> entries;
        DirListingMetadata dirMeta = ms.listChildren(strToPath("/"));
        if ((!(allowMissing())) || (dirMeta != null)) {
            dirMeta = dirMeta.withoutTombstones();
            assertNotNull("Listing root", dirMeta);
            entries = dirMeta.getListing();
            assertListingsEqual(entries, "/a1", "/a2", "/a3");
        }
        // Assert src listing as expected
        dirMeta = ms.listChildren(strToPath("/a1/b1"));
        if ((!(allowMissing())) || (dirMeta != null)) {
            assertNotNull("Listing /a1/b1", dirMeta);
            entries = dirMeta.getListing();
            assertListingsEqual(entries, "/a1/b1/file1", "/a1/b1/file2");
        }
        // Do the move(): rename(/a1/b1, /b1)
        Collection<Path> srcPaths = Arrays.asList(strToPath("/a1/b1"), strToPath("/a1/b1/file1"), strToPath("/a1/b1/file2"));
        ArrayList<PathMetadata> destMetas = new ArrayList<>();
        destMetas.add(new PathMetadata(makeDirStatus("/b1")));
        destMetas.add(new PathMetadata(makeFileStatus("/b1/file1", 100)));
        destMetas.add(new PathMetadata(makeFileStatus("/b1/file2", 100)));
        ms.move(srcPaths, destMetas);
        // Assert src is no longer there
        dirMeta = ms.listChildren(strToPath("/a1"));
        if ((!(allowMissing())) || (dirMeta != null)) {
            assertNotNull("Listing /a1", dirMeta);
            entries = dirMeta.withoutTombstones().getListing();
            assertListingsEqual(entries, "/a1/b2");
        }
        PathMetadata meta = ms.get(strToPath("/a1/b1/file1"));
        assertTrue("Src path deleted", ((meta == null) || (meta.isDeleted())));
        // Assert dest looks right
        meta = ms.get(strToPath("/b1/file1"));
        if ((!(allowMissing())) || (meta != null)) {
            assertNotNull("dest file not null", meta);
            verifyFileStatus(meta.getFileStatus(), 100);
        }
        dirMeta = ms.listChildren(strToPath("/b1"));
        if ((!(allowMissing())) || (dirMeta != null)) {
            assertNotNull("dest listing not null", dirMeta);
            entries = dirMeta.getListing();
            assertListingsEqual(entries, "/b1/file1", "/b1/file2");
        }
    }

    /**
     * Test that the MetadataStore differentiates between the same path in two
     * different buckets.
     */
    @Test
    public void testMultiBucketPaths() throws Exception {
        String p1 = "s3a://bucket-a/path1";
        String p2 = "s3a://bucket-b/path2";
        // Make sure we start out empty
        PathMetadata meta = ms.get(new Path(p1));
        assertNull("Path should not be present yet.", meta);
        meta = ms.get(new Path(p2));
        assertNull("Path2 should not be present yet.", meta);
        // Put p1, assert p2 doesn't match
        ms.put(new PathMetadata(makeFileStatus(p1, 100)));
        meta = ms.get(new Path(p2));
        assertNull("Path 2 should not match path 1.", meta);
        // Make sure delete is correct as well
        if (!(allowMissing())) {
            ms.delete(new Path(p2));
            meta = ms.get(new Path(p1));
            assertNotNull("Path should not have been deleted", meta);
        }
        ms.delete(new Path(p1));
    }

    @Test
    public void testPruneFiles() throws Exception {
        Assume.assumeTrue(supportsPruning());
        createNewDirs("/pruneFiles");
        long oldTime = MetadataStoreTestBase.getTime();
        ms.put(new PathMetadata(makeFileStatus("/pruneFiles/old", 1, oldTime, oldTime)));
        DirListingMetadata ls2 = ms.listChildren(strToPath("/pruneFiles"));
        if (!(allowMissing())) {
            assertListingsEqual(ls2.getListing(), "/pruneFiles/old");
        }
        // It's possible for the Local implementation to get from /pruneFiles/old's
        // modification time to here in under 1ms, causing it to not get pruned
        Thread.sleep(1);
        long cutoff = System.currentTimeMillis();
        long newTime = MetadataStoreTestBase.getTime();
        ms.put(new PathMetadata(makeFileStatus("/pruneFiles/new", 1, newTime, newTime)));
        DirListingMetadata ls;
        ls = ms.listChildren(strToPath("/pruneFiles"));
        if (!(allowMissing())) {
            assertListingsEqual(ls.getListing(), "/pruneFiles/new", "/pruneFiles/old");
        }
        ms.prune(cutoff);
        ls = ms.listChildren(strToPath("/pruneFiles"));
        if (allowMissing()) {
            assertDeleted("/pruneFiles/old");
        } else {
            assertListingsEqual(ls.getListing(), "/pruneFiles/new");
        }
    }

    @Test
    public void testPruneDirs() throws Exception {
        Assume.assumeTrue(supportsPruning());
        // We only test that files, not dirs, are removed during prune.
        // We specifically allow directories to remain, as it is more robust
        // for DynamoDBMetadataStore's prune() implementation: If a
        // file was created in a directory while it was being pruned, it would
        // violate the invariant that all ancestors of a file exist in the table.
        createNewDirs("/pruneDirs/dir");
        long oldTime = MetadataStoreTestBase.getTime();
        ms.put(new PathMetadata(makeFileStatus("/pruneDirs/dir/file", 1, oldTime, oldTime)));
        // It's possible for the Local implementation to get from the old
        // modification time to here in under 1ms, causing it to not get pruned
        Thread.sleep(1);
        long cutoff = MetadataStoreTestBase.getTime();
        ms.prune(cutoff);
        assertDeleted("/pruneDirs/dir/file");
    }

    @Test
    public void testPruneUnsetsAuthoritative() throws Exception {
        String rootDir = "/unpruned-root-dir";
        String grandparentDir = rootDir + "/pruned-grandparent-dir";
        String parentDir = grandparentDir + "/pruned-parent-dir";
        String staleFile = parentDir + "/stale-file";
        String freshFile = rootDir + "/fresh-file";
        String[] directories = new String[]{ rootDir, grandparentDir, parentDir };
        createNewDirs(rootDir, grandparentDir, parentDir);
        long time = System.currentTimeMillis();
        ms.put(new PathMetadata(new FileStatus(0, false, 0, 0, (time - 1), strToPath(staleFile)), Tristate.FALSE, false));
        ms.put(new PathMetadata(new FileStatus(0, false, 0, 0, (time + 1), strToPath(freshFile)), Tristate.FALSE, false));
        // set parent dir as authoritative
        if (!(allowMissing())) {
            DirListingMetadata parentDirMd = ms.listChildren(strToPath(parentDir));
            parentDirMd.setAuthoritative(true);
            ms.put(parentDirMd);
        }
        ms.prune(time);
        DirListingMetadata listing;
        for (String directory : directories) {
            Path path = strToPath(directory);
            if ((ms.get(path)) != null) {
                listing = ms.listChildren(path);
                assertFalse(listing.isAuthoritative());
            }
        }
    }

    @Test
    public void testPrunePreservesAuthoritative() throws Exception {
        String rootDir = "/unpruned-root-dir";
        String grandparentDir = rootDir + "/pruned-grandparent-dir";
        String parentDir = grandparentDir + "/pruned-parent-dir";
        String staleFile = parentDir + "/stale-file";
        String freshFile = rootDir + "/fresh-file";
        String[] directories = new String[]{ rootDir, grandparentDir, parentDir };
        // create dirs
        createNewDirs(rootDir, grandparentDir, parentDir);
        long time = System.currentTimeMillis();
        ms.put(new PathMetadata(new FileStatus(0, false, 0, 0, (time + 1), strToPath(staleFile)), Tristate.FALSE, false));
        ms.put(new PathMetadata(new FileStatus(0, false, 0, 0, (time + 1), strToPath(freshFile)), Tristate.FALSE, false));
        if (!(allowMissing())) {
            // set parent dir as authoritative
            DirListingMetadata parentDirMd = ms.listChildren(strToPath(parentDir));
            parentDirMd.setAuthoritative(true);
            ms.put(parentDirMd);
            // prune the ms
            ms.prune(time);
            // get the directory listings
            DirListingMetadata rootDirMd = ms.listChildren(strToPath(rootDir));
            DirListingMetadata grandParentDirMd = ms.listChildren(strToPath(grandparentDir));
            parentDirMd = ms.listChildren(strToPath(parentDir));
            // assert that parent dir is still authoritative (no removed elements
            // during prune)
            assertFalse(rootDirMd.isAuthoritative());
            assertFalse(grandParentDirMd.isAuthoritative());
            assertTrue(parentDirMd.isAuthoritative());
        }
    }

    @Test
    public void testPutDirListingMetadataPutsFileMetadata() throws IOException {
        boolean authoritative = true;
        String[] filenames = new String[]{ "/dir1/file1", "/dir1/file2", "/dir1/file3" };
        String dirPath = "/dir1";
        ArrayList<PathMetadata> metas = new ArrayList<>(filenames.length);
        for (String filename : filenames) {
            metas.add(new PathMetadata(makeFileStatus(filename, 100)));
        }
        DirListingMetadata dirMeta = new DirListingMetadata(strToPath(dirPath), metas, authoritative);
        ms.put(dirMeta);
        if (!(allowMissing())) {
            assertDirectorySize(dirPath, filenames.length);
            PathMetadata metadata;
            for (String fileName : filenames) {
                metadata = ms.get(strToPath(fileName));
                assertNotNull(String.format("PathMetadata for file %s should not be null.", fileName), metadata);
            }
        }
    }

    @Test
    public void testPutRetainsIsDeletedInParentListing() throws Exception {
        final Path path = strToPath("/a/b");
        final FileStatus fileStatus = basicFileStatus(path, 0, false);
        PathMetadata pm = new PathMetadata(fileStatus);
        pm.setIsDeleted(true);
        ms.put(pm);
        if (!(allowMissing())) {
            final PathMetadata pathMetadata = ms.listChildren(path.getParent()).get(path);
            assertTrue("isDeleted should be true on the parent listing", pathMetadata.isDeleted());
        }
    }
}

