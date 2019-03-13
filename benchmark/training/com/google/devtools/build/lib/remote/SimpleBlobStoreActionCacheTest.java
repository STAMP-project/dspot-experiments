/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.remote;


import ActionResult.Builder;
import build.bazel.remote.execution.v2.Action;
import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.Command;
import build.bazel.remote.execution.v2.Digest;
import build.bazel.remote.execution.v2.Directory;
import build.bazel.remote.execution.v2.DirectoryNode;
import build.bazel.remote.execution.v2.FileNode;
import build.bazel.remote.execution.v2.Tree;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.devtools.build.lib.actions.ActionInputHelper;
import com.google.devtools.build.lib.remote.util.DigestUtil;
import com.google.devtools.build.lib.remote.util.Utils;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.DigestHashFunction;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.Path;
import io.grpc.Context;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SimpleBlobStoreActionCache}.
 */
@RunWith(JUnit4.class)
public class SimpleBlobStoreActionCacheTest {
    private static final DigestUtil DIGEST_UTIL = new DigestUtil(DigestHashFunction.SHA256);

    private FileSystem fs;

    private Path execRoot;

    private FakeActionInputFileCache fakeFileCache;

    private Context withEmptyMetadata;

    private Context prevContext;

    private Retrier retrier;

    private static ListeningScheduledExecutorService retryService;

    @Test
    public void testDownloadEmptyBlob() throws Exception {
        SimpleBlobStoreActionCache client = newClient();
        Digest emptyDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(new byte[0]);
        // Will not call the mock Bytestream interface at all.
        assertThat(Utils.getFromFuture(client.downloadBlob(emptyDigest))).isEmpty();
    }

    @Test
    public void testDownloadBlob() throws Exception {
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        Digest digest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("abcdefg");
        map.put(digest.getHash(), "abcdefg".getBytes(Charsets.UTF_8));
        final SimpleBlobStoreActionCache client = newClient(map);
        assertThat(new String(Utils.getFromFuture(client.downloadBlob(digest)), StandardCharsets.UTF_8)).isEqualTo("abcdefg");
    }

    @Test
    public void testDownloadAllResults() throws Exception {
        Digest fooDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("foo-contents");
        Digest barDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("bar-contents");
        Digest emptyDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(new byte[0]);
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        map.put(fooDigest.getHash(), "foo-contents".getBytes(Charsets.UTF_8));
        map.put(barDigest.getHash(), "bar-contents".getBytes(Charsets.UTF_8));
        SimpleBlobStoreActionCache client = newClient(map);
        ActionResult.Builder result = ActionResult.newBuilder();
        result.addOutputFilesBuilder().setPath("a/foo").setDigest(fooDigest);
        result.addOutputFilesBuilder().setPath("b/empty").setDigest(emptyDigest);
        result.addOutputFilesBuilder().setPath("a/bar").setDigest(barDigest).setIsExecutable(true);
        client.download(result.build(), execRoot, null);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/foo"))).isEqualTo(fooDigest);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("b/empty"))).isEqualTo(emptyDigest);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/bar"))).isEqualTo(barDigest);
        assertThat(execRoot.getRelative("a/bar").isExecutable()).isTrue();
    }

    @Test
    public void testDownloadDirectory() throws Exception {
        Digest fooDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("foo-contents");
        Digest quxDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("qux-contents");
        Tree barTreeMessage = Tree.newBuilder().setRoot(Directory.newBuilder().addFiles(FileNode.newBuilder().setName("qux").setDigest(quxDigest).setIsExecutable(true))).build();
        Digest barTreeDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(barTreeMessage);
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        map.put(fooDigest.getHash(), "foo-contents".getBytes(Charsets.UTF_8));
        map.put(barTreeDigest.getHash(), barTreeMessage.toByteArray());
        map.put(quxDigest.getHash(), "qux-contents".getBytes(Charsets.UTF_8));
        SimpleBlobStoreActionCache client = newClient(map);
        ActionResult.Builder result = ActionResult.newBuilder();
        result.addOutputFilesBuilder().setPath("a/foo").setDigest(fooDigest);
        result.addOutputDirectoriesBuilder().setPath("a/bar").setTreeDigest(barTreeDigest);
        client.download(result.build(), execRoot, null);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/foo"))).isEqualTo(fooDigest);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/bar/qux"))).isEqualTo(quxDigest);
        assertThat(execRoot.getRelative("a/bar/qux").isExecutable()).isTrue();
    }

    @Test
    public void testDownloadDirectoryEmpty() throws Exception {
        Tree barTreeMessage = Tree.newBuilder().setRoot(Directory.newBuilder()).build();
        Digest barTreeDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(barTreeMessage);
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        map.put(barTreeDigest.getHash(), barTreeMessage.toByteArray());
        SimpleBlobStoreActionCache client = newClient(map);
        ActionResult.Builder result = ActionResult.newBuilder();
        result.addOutputDirectoriesBuilder().setPath("a/bar").setTreeDigest(barTreeDigest);
        client.download(result.build(), execRoot, null);
        assertThat(execRoot.getRelative("a/bar").isDirectory()).isTrue();
    }

    @Test
    public void testDownloadDirectoryNested() throws Exception {
        Digest fooDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("foo-contents");
        Digest quxDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("qux-contents");
        Directory wobbleDirMessage = Directory.newBuilder().addFiles(FileNode.newBuilder().setName("qux").setDigest(quxDigest)).build();
        Digest wobbleDirDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(wobbleDirMessage);
        Tree barTreeMessage = Tree.newBuilder().setRoot(Directory.newBuilder().addFiles(FileNode.newBuilder().setName("qux").setDigest(quxDigest).setIsExecutable(true)).addDirectories(DirectoryNode.newBuilder().setName("wobble").setDigest(wobbleDirDigest))).addChildren(wobbleDirMessage).build();
        Digest barTreeDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(barTreeMessage);
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        map.put(fooDigest.getHash(), "foo-contents".getBytes(Charsets.UTF_8));
        map.put(barTreeDigest.getHash(), barTreeMessage.toByteArray());
        map.put(quxDigest.getHash(), "qux-contents".getBytes(Charsets.UTF_8));
        SimpleBlobStoreActionCache client = newClient(map);
        ActionResult.Builder result = ActionResult.newBuilder();
        result.addOutputFilesBuilder().setPath("a/foo").setDigest(fooDigest);
        result.addOutputDirectoriesBuilder().setPath("a/bar").setTreeDigest(barTreeDigest);
        client.download(result.build(), execRoot, null);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/foo"))).isEqualTo(fooDigest);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/bar/wobble/qux"))).isEqualTo(quxDigest);
        assertThat(execRoot.getRelative("a/bar/wobble/qux").isExecutable()).isFalse();
    }

    @Test
    public void testDownloadDirectoriesWithSameHash() throws Exception {
        // Test that downloading an output directory works when two Directory
        // protos have the same hash i.e. because they have the same name and contents or are empty.
        /* /bar/foo/file
        /foo/file
         */
        Digest fileDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("file");
        FileNode file = FileNode.newBuilder().setName("file").setDigest(fileDigest).build();
        Directory fooDir = Directory.newBuilder().addFiles(file).build();
        Digest fooDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(fooDir);
        DirectoryNode fooDirNode = DirectoryNode.newBuilder().setName("foo").setDigest(fooDigest).build();
        Directory barDir = Directory.newBuilder().addDirectories(fooDirNode).build();
        Digest barDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(barDir);
        DirectoryNode barDirNode = DirectoryNode.newBuilder().setName("bar").setDigest(barDigest).build();
        Directory rootDir = Directory.newBuilder().addDirectories(fooDirNode).addDirectories(barDirNode).build();
        Tree tree = Tree.newBuilder().setRoot(rootDir).addChildren(barDir).addChildren(fooDir).addChildren(fooDir).build();
        Digest treeDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(tree);
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        map.put(fileDigest.getHash(), "file".getBytes(Charsets.UTF_8));
        map.put(treeDigest.getHash(), tree.toByteArray());
        SimpleBlobStoreActionCache client = newClient(map);
        ActionResult.Builder result = ActionResult.newBuilder();
        result.addOutputDirectoriesBuilder().setPath("a/").setTreeDigest(treeDigest);
        client.download(result.build(), execRoot, null);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/bar/foo/file"))).isEqualTo(fileDigest);
        assertThat(SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(execRoot.getRelative("a/foo/file"))).isEqualTo(fileDigest);
    }

    @Test
    public void testUploadBlob() throws Exception {
        final Digest digest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("abcdefg");
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        final SimpleBlobStoreActionCache client = newClient(map);
        byte[] bytes = "abcdefg".getBytes(StandardCharsets.UTF_8);
        assertThat(client.uploadBlob(bytes)).isEqualTo(digest);
        assertThat(map.keySet()).containsExactly(digest.getHash());
        assertThat(map.entrySet()).hasSize(1);
        assertThat(map.get(digest.getHash())).isEqualTo(bytes);
    }

    @Test
    public void testUploadBlobAtMostOnce() throws Exception {
        final Digest digest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("abcdefg");
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        final SimpleBlobStoreActionCache client = newClient(map);
        byte[] bytes = "abcdefg".getBytes(StandardCharsets.UTF_8);
        assertThat(client.uploadBlob(bytes)).isEqualTo(digest);
        assertThat(map.keySet()).containsExactly(digest.getHash());
        assertThat(map.entrySet()).hasSize(1);
        assertThat(map.get(digest.getHash())).isEqualTo(bytes);
        // Blob store does not get upload more than once during a single build.
        map.remove(digest.getHash());
        assertThat(client.uploadBlob(bytes)).isEqualTo(digest);
        assertThat(map.entrySet()).hasSize(0);
    }

    @Test
    public void testUploadDirectory() throws Exception {
        final Digest fooDigest = fakeFileCache.createScratchInput(ActionInputHelper.fromPath("a/foo"), "xyz");
        final Digest quxDigest = fakeFileCache.createScratchInput(ActionInputHelper.fromPath("bar/qux"), "abc");
        final Digest barDigest = fakeFileCache.createScratchInputDirectory(ActionInputHelper.fromPath("bar"), Tree.newBuilder().setRoot(Directory.newBuilder().addFiles(FileNode.newBuilder().setIsExecutable(true).setName("qux").setDigest(quxDigest).build()).build()).build());
        final Path fooFile = execRoot.getRelative("a/foo");
        final Path quxFile = execRoot.getRelative("bar/qux");
        quxFile.setExecutable(true);
        final Path barDir = execRoot.getRelative("bar");
        Command cmd = Command.newBuilder().addOutputFiles("bla").build();
        final Digest cmdDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(cmd);
        Action action = Action.newBuilder().setCommandDigest(cmdDigest).build();
        final Digest actionDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(action);
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        final SimpleBlobStoreActionCache client = newClient(map);
        ActionResult.Builder result = ActionResult.newBuilder();
        /* uploadAction= */
        client.upload(result, SimpleBlobStoreActionCacheTest.DIGEST_UTIL.asActionKey(actionDigest), action, cmd, execRoot, ImmutableList.<Path>of(fooFile, barDir), true);
        ActionResult.Builder expectedResult = ActionResult.newBuilder();
        expectedResult.addOutputFilesBuilder().setPath("a/foo").setDigest(fooDigest);
        expectedResult.addOutputDirectoriesBuilder().setPath("bar").setTreeDigest(barDigest);
        assertThat(result.build()).isEqualTo(expectedResult.build());
        assertThat(map.keySet()).containsExactly(fooDigest.getHash(), quxDigest.getHash(), barDigest.getHash(), cmdDigest.getHash(), actionDigest.getHash());
    }

    @Test
    public void testUploadDirectoryEmpty() throws Exception {
        final Digest barDigest = fakeFileCache.createScratchInputDirectory(ActionInputHelper.fromPath("bar"), Tree.newBuilder().setRoot(Directory.newBuilder().build()).build());
        final Path barDir = execRoot.getRelative("bar");
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        final SimpleBlobStoreActionCache client = newClient(map);
        ActionResult result = uploadDirectory(client, ImmutableList.<Path>of(barDir));
        ActionResult.Builder expectedResult = ActionResult.newBuilder();
        expectedResult.addOutputDirectoriesBuilder().setPath("bar").setTreeDigest(barDigest);
        assertThat(result).isEqualTo(expectedResult.build());
        assertThat(map.keySet()).contains(barDigest.getHash());
    }

    @Test
    public void testUploadDirectoryNested() throws Exception {
        final Digest wobbleDigest = fakeFileCache.createScratchInput(ActionInputHelper.fromPath("bar/test/wobble"), "xyz");
        final Digest quxDigest = fakeFileCache.createScratchInput(ActionInputHelper.fromPath("bar/qux"), "abc");
        final Directory testDirMessage = Directory.newBuilder().addFiles(FileNode.newBuilder().setName("wobble").setDigest(wobbleDigest).build()).build();
        final Digest testDigest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.compute(testDirMessage);
        final Tree barTree = Tree.newBuilder().setRoot(Directory.newBuilder().addFiles(FileNode.newBuilder().setIsExecutable(true).setName("qux").setDigest(quxDigest)).addDirectories(DirectoryNode.newBuilder().setName("test").setDigest(testDigest))).addChildren(testDirMessage).build();
        final Digest barDigest = fakeFileCache.createScratchInputDirectory(ActionInputHelper.fromPath("bar"), barTree);
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        final SimpleBlobStoreActionCache client = newClient(map);
        final Path quxFile = execRoot.getRelative("bar/qux");
        quxFile.setExecutable(true);
        final Path barDir = execRoot.getRelative("bar");
        ActionResult result = uploadDirectory(client, ImmutableList.<Path>of(barDir));
        ActionResult.Builder expectedResult = ActionResult.newBuilder();
        expectedResult.addOutputDirectoriesBuilder().setPath("bar").setTreeDigest(barDigest);
        assertThat(result).isEqualTo(expectedResult.build());
        assertThat(map.keySet()).containsAllOf(wobbleDigest.getHash(), quxDigest.getHash(), barDigest.getHash());
    }

    @Test
    public void testDownloadFailsOnDigestMismatch() {
        // Test that the download fails when a blob/file has a different content hash than expected.
        final ConcurrentMap<String, byte[]> map = new ConcurrentHashMap<>();
        Digest digest = SimpleBlobStoreActionCacheTest.DIGEST_UTIL.computeAsUtf8("hello");
        // Store content that doesn't match its digest
        map.put(digest.getHash(), "world".getBytes(Charsets.UTF_8));
        final SimpleBlobStoreActionCache client = newClient(map);
        IOException e = MoreAsserts.assertThrows(IOException.class, () -> Utils.getFromFuture(client.downloadBlob(digest)));
        assertThat(e).hasMessageThat().contains(digest.getHash());
        e = MoreAsserts.assertThrows(IOException.class, () -> Utils.getFromFuture(client.downloadFile(fs.getPath("/exec/root/foo"), digest)));
        assertThat(e).hasMessageThat().contains(digest.getHash());
    }
}

