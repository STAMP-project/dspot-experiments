/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.fuse;


import PropertyKey.FUSE_CACHED_PATHS_MAX;
import PropertyKey.FUSE_USER_GROUP_TRANSLATION_ENABLED;
import alluxio.AlluxioURI;
import alluxio.ConfigurationRule;
import alluxio.ConfigurationTestUtils;
import alluxio.Constants;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.URIStatus;
import alluxio.conf.InstancedConfiguration;
import alluxio.grpc.SetAttributePOptions;
import alluxio.security.authorization.Mode;
import alluxio.wire.FileInfo;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import jnr.constants.platform.OpenFlags;
import jnr.ffi.Pointer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import static AlluxioFuseFileSystem.ID_NOT_SET_VALUE;
import static AlluxioFuseFileSystem.ID_NOT_SET_VALUE_UNSIGNED;


/**
 * Isolation tests for {@link AlluxioFuseFileSystem}.
 */
public class AlluxioFuseFileSystemTest {
    private static final String TEST_ROOT_PATH = "/t/root";

    private static final AlluxioURI BASE_EXPECTED_URI = new AlluxioURI(AlluxioFuseFileSystemTest.TEST_ROOT_PATH);

    private AlluxioFuseFileSystem mFuseFs;

    private FileSystem mFileSystem;

    private FuseFileInfo mFileInfo;

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Rule
    public ConfigurationRule mConfiguration = new ConfigurationRule(ImmutableMap.of(FUSE_CACHED_PATHS_MAX, "0", FUSE_USER_GROUP_TRANSLATION_ENABLED, "true"), mConf);

    @Test
    public void chmod() throws Exception {
        long mode = 123;
        mFuseFs.chmod("/foo/bar", mode);
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        SetAttributePOptions options = SetAttributePOptions.newBuilder().setMode(new Mode(((short) (mode))).toProto()).build();
        Mockito.verify(mFileSystem).setAttribute(expectedPath, options);
    }

    @Test
    public void chown() throws Exception {
        long uid = AlluxioFuseUtils.getUid(System.getProperty("user.name"));
        long gid = AlluxioFuseUtils.getGid(System.getProperty("user.name"));
        mFuseFs.chown("/foo/bar", uid, gid);
        String userName = System.getProperty("user.name");
        String groupName = AlluxioFuseUtils.getGroupName(gid);
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        SetAttributePOptions options = SetAttributePOptions.newBuilder().setGroup(groupName).setOwner(userName).build();
        Mockito.verify(mFileSystem).setAttribute(expectedPath, options);
    }

    @Test
    public void chownWithoutValidGid() throws Exception {
        long uid = AlluxioFuseUtils.getUid(System.getProperty("user.name"));
        long gid = ID_NOT_SET_VALUE;
        mFuseFs.chown("/foo/bar", uid, gid);
        String userName = System.getProperty("user.name");
        String groupName = AlluxioFuseUtils.getGroupName(userName);
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        SetAttributePOptions options = SetAttributePOptions.newBuilder().setGroup(groupName).setOwner(userName).build();
        Mockito.verify(mFileSystem).setAttribute(expectedPath, options);
        gid = ID_NOT_SET_VALUE_UNSIGNED;
        mFuseFs.chown("/foo/bar", uid, gid);
        Mockito.verify(mFileSystem, Mockito.times(2)).setAttribute(expectedPath, options);
    }

    @Test
    public void chownWithoutValidUid() throws Exception {
        String userName = System.getProperty("user.name");
        long uid = ID_NOT_SET_VALUE;
        long gid = AlluxioFuseUtils.getGid(userName);
        mFuseFs.chown("/foo/bar", uid, gid);
        String groupName = AlluxioFuseUtils.getGroupName(userName);
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        SetAttributePOptions options = SetAttributePOptions.newBuilder().setGroup(groupName).build();
        Mockito.verify(mFileSystem).setAttribute(expectedPath, options);
        uid = ID_NOT_SET_VALUE_UNSIGNED;
        mFuseFs.chown("/foo/bar", uid, gid);
        Mockito.verify(mFileSystem, Mockito.times(2)).setAttribute(expectedPath, options);
    }

    @Test
    public void chownWithoutValidUidAndGid() throws Exception {
        long uid = ID_NOT_SET_VALUE;
        long gid = ID_NOT_SET_VALUE;
        mFuseFs.chown("/foo/bar", uid, gid);
        Mockito.verify(mFileSystem, Mockito.never()).setAttribute(ArgumentMatchers.any());
        uid = ID_NOT_SET_VALUE_UNSIGNED;
        gid = ID_NOT_SET_VALUE_UNSIGNED;
        mFuseFs.chown("/foo/bar", uid, gid);
        Mockito.verify(mFileSystem, Mockito.never()).setAttribute(ArgumentMatchers.any());
    }

    @Test
    public void create() throws Exception {
        mFileInfo.flags.set(OpenFlags.O_WRONLY.intValue());
        mFuseFs.create("/foo/bar", 0, mFileInfo);
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        Mockito.verify(mFileSystem).createFile(expectedPath);
    }

    @Test
    public void flush() throws Exception {
        FileOutStream fos = Mockito.mock(FileOutStream.class);
        AlluxioURI anyURI = ArgumentMatchers.any();
        Mockito.when(mFileSystem.createFile(anyURI)).thenReturn(fos);
        // open a file
        mFileInfo.flags.set(OpenFlags.O_WRONLY.intValue());
        mFuseFs.create("/foo/bar", 0, mFileInfo);
        // then call flush into it
        mFuseFs.flush("/foo/bar", mFileInfo);
        Mockito.verify(fos).flush();
    }

    @Test
    public void getattr() throws Exception {
        // set up status
        FileInfo info = new FileInfo();
        info.setLength(((4 * (Constants.KB)) + 1));
        info.setLastModificationTimeMs(1000);
        String userName = System.getProperty("user.name");
        info.setOwner(userName);
        info.setGroup(AlluxioFuseUtils.getGroupName(userName));
        info.setFolder(true);
        info.setMode(123);
        info.setCompleted(true);
        URIStatus status = new URIStatus(info);
        // mock fs
        Mockito.when(mFileSystem.getStatus(ArgumentMatchers.any(AlluxioURI.class))).thenReturn(status);
        FileStat stat = new FileStat(getSystemRuntime());
        Assert.assertEquals(0, mFuseFs.getattr("/foo", stat));
        Assert.assertEquals(status.getLength(), stat.st_size.longValue());
        Assert.assertEquals(9, stat.st_blocks.intValue());
        Assert.assertEquals(((status.getLastModificationTimeMs()) / 1000), stat.st_ctim.tv_sec.get());
        Assert.assertEquals((((status.getLastModificationTimeMs()) % 1000) * 1000), stat.st_ctim.tv_nsec.longValue());
        Assert.assertEquals(((status.getLastModificationTimeMs()) / 1000), stat.st_mtim.tv_sec.get());
        Assert.assertEquals((((status.getLastModificationTimeMs()) % 1000) * 1000), stat.st_mtim.tv_nsec.longValue());
        Assert.assertEquals(AlluxioFuseUtils.getUid(System.getProperty("user.name")), stat.st_uid.get());
        Assert.assertEquals(AlluxioFuseUtils.getGid(System.getProperty("user.name")), stat.st_gid.get());
        Assert.assertEquals((123 | (FileStat.S_IFDIR)), stat.st_mode.intValue());
    }

    @Test
    public void getattrWithDelay() throws Exception {
        String path = "/foo/bar";
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        // set up status
        FileInfo info = new FileInfo();
        info.setLength(0);
        info.setCompleted(false);
        URIStatus status = new URIStatus(info);
        // mock fs
        Mockito.when(mFileSystem.getStatus(ArgumentMatchers.any(AlluxioURI.class))).thenReturn(status);
        FileStat stat = new FileStat(getSystemRuntime());
        // Use another thread to open file so that
        // we could change the file status when opening it
        Thread t = new Thread(() -> mFuseFs.getattr(path, stat));
        t.start();
        Thread.sleep(1000);
        // If the file is not being written and is not completed,
        // we will wait for the file to complete
        Mockito.verify(mFileSystem, Mockito.atLeast(10)).getStatus(expectedPath);
        Assert.assertEquals(0, stat.st_size.longValue());
        info.setCompleted(true);
        info.setLength(1000);
        t.join();
        Assert.assertEquals(1000, stat.st_size.longValue());
    }

    @Test
    public void getattrWhenWriting() throws Exception {
        String path = "/foo/bar";
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join(path);
        FileOutStream fos = Mockito.mock(FileOutStream.class);
        Mockito.when(mFileSystem.createFile(expectedPath)).thenReturn(fos);
        mFuseFs.create(path, 0, mFileInfo);
        // Prepare file status
        FileInfo info = new FileInfo();
        info.setLength(0);
        info.setCompleted(false);
        URIStatus status = new URIStatus(info);
        Mockito.when(mFileSystem.exists(ArgumentMatchers.any(AlluxioURI.class))).thenReturn(true);
        Mockito.when(mFileSystem.getStatus(ArgumentMatchers.any(AlluxioURI.class))).thenReturn(status);
        FileStat stat = new FileStat(getSystemRuntime());
        // getattr() will not be blocked when writing
        mFuseFs.getattr(path, stat);
        // If getattr() is blocking, it will continuously get status of the file
        Mockito.verify(mFileSystem, Mockito.atMost(2)).getStatus(expectedPath);
        Assert.assertEquals(0, stat.st_size.longValue());
        mFuseFs.release(path, mFileInfo);
        // getattr() will be blocked waiting for the file to be completed
        // If release() is called (returned) but does not finished
        Thread t = new Thread(() -> mFuseFs.getattr(path, stat));
        t.start();
        Thread.sleep(1000);
        Mockito.verify(mFileSystem, Mockito.atLeast(10)).getStatus(expectedPath);
        Assert.assertEquals(0, stat.st_size.longValue());
        info.setCompleted(true);
        info.setLength(1000);
        t.join();
        // getattr() completed and set the file size
        Assert.assertEquals(1000, stat.st_size.longValue());
    }

    @Test
    public void mkDir() throws Exception {
        mFuseFs.mkdir("/foo/bar", (-1));
        Mockito.verify(mFileSystem).createDirectory(AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar"));
    }

    @Test
    public void openWithoutDelay() throws Exception {
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        setUpOpenMock(expectedPath);
        mFuseFs.open("/foo/bar", mFileInfo);
        Mockito.verify(mFileSystem).getStatus(expectedPath);
        Mockito.verify(mFileSystem).openFile(expectedPath);
    }

    @Test
    public void incompleteFileCannotOpen() throws Exception {
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        FileInfo fi = setUpOpenMock(expectedPath);
        fi.setCompleted(false);
        mFuseFs.open("/foo/bar", mFileInfo);
        Mockito.verify(mFileSystem, Mockito.atLeast(100)).getStatus(expectedPath);
        Mockito.verify(mFileSystem, Mockito.never()).openFile(expectedPath);
    }

    @Test
    public void openWithDelay() throws Exception {
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        FileInfo fi = setUpOpenMock(expectedPath);
        fi.setCompleted(false);
        // Use another thread to open file so that
        // we could change the file status when opening it
        Thread t = new Thread(() -> mFuseFs.open("/foo/bar", mFileInfo));
        t.start();
        Thread.sleep(1000);
        // If the file exists but is not completed, we will wait for the file to complete
        Mockito.verify(mFileSystem, Mockito.atLeast(10)).getStatus(expectedPath);
        Mockito.verify(mFileSystem, Mockito.never()).openFile(expectedPath);
        fi.setCompleted(true);
        t.join();
        Mockito.verify(mFileSystem).openFile(expectedPath);
    }

    @Test
    public void read() throws Exception {
        // mocks set-up
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        setUpOpenMock(expectedPath);
        FileInStream fakeInStream = Mockito.mock(FileInStream.class);
        Mockito.when(fakeInStream.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).then(((Answer<Integer>) (( invocationOnMock) -> {
            byte[] myDest = ((byte[]) (invocationOnMock.getArguments()[0]));
            for (byte i = 0; i < 4; i++) {
                myDest[i] = i;
            }
            return 4;
        })));
        Mockito.when(mFileSystem.openFile(expectedPath)).thenReturn(fakeInStream);
        mFileInfo.flags.set(OpenFlags.O_RDONLY.intValue());
        // prepare something to read to it
        Runtime r = Runtime.getSystemRuntime();
        Pointer ptr = getMemoryManager().allocateTemporary(4, true);
        // actual test
        mFuseFs.open("/foo/bar", mFileInfo);
        mFuseFs.read("/foo/bar", ptr, 4, 0, mFileInfo);
        final byte[] dst = new byte[4];
        ptr.get(0, dst, 0, 4);
        final byte[] expected = new byte[]{ 0, 1, 2, 3 };
        Assert.assertArrayEquals("Source and dst data should be equal", expected, dst);
    }

    @Test
    public void rename() throws Exception {
        AlluxioURI oldPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/old");
        AlluxioURI newPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/new");
        Mockito.doNothing().when(mFileSystem).rename(oldPath, newPath);
        mFuseFs.rename("/old", "/new");
        Mockito.verify(mFileSystem).rename(oldPath, newPath);
    }

    @Test
    public void renameOldNotExist() throws Exception {
        AlluxioURI oldPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/old");
        AlluxioURI newPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/new");
        Mockito.doThrow(new alluxio.exception.FileDoesNotExistException("File /old does not exist")).when(mFileSystem).rename(oldPath, newPath);
        Assert.assertEquals((-(ErrorCodes.ENOENT())), mFuseFs.rename("/old", "/new"));
    }

    @Test
    public void renameNewExist() throws Exception {
        AlluxioURI oldPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/old");
        AlluxioURI newPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/new");
        Mockito.doThrow(new alluxio.exception.FileAlreadyExistsException("File /new already exists")).when(mFileSystem).rename(oldPath, newPath);
        Assert.assertEquals((-(ErrorCodes.EEXIST())), mFuseFs.rename("/old", "/new"));
    }

    @Test
    public void rmdir() throws Exception {
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        Mockito.doNothing().when(mFileSystem).delete(expectedPath);
        mFuseFs.rmdir("/foo/bar");
        Mockito.verify(mFileSystem).delete(expectedPath);
    }

    @Test
    public void write() throws Exception {
        FileOutStream fos = Mockito.mock(FileOutStream.class);
        AlluxioURI anyURI = ArgumentMatchers.any();
        Mockito.when(mFileSystem.createFile(anyURI)).thenReturn(fos);
        // open a file
        mFileInfo.flags.set(OpenFlags.O_WRONLY.intValue());
        mFuseFs.create("/foo/bar", 0, mFileInfo);
        // prepare something to write into it
        Runtime r = Runtime.getSystemRuntime();
        Pointer ptr = getMemoryManager().allocateTemporary(4, true);
        byte[] expected = new byte[]{ 42, -128, 1, 3 };
        ptr.put(0, expected, 0, 4);
        mFuseFs.write("/foo/bar", ptr, 4, 0, mFileInfo);
        Mockito.verify(fos).write(expected);
        // the second write is no-op because the writes must be sequential and overwriting is supported
        mFuseFs.write("/foo/bar", ptr, 4, 0, mFileInfo);
        Mockito.verify(fos, Mockito.times(1)).write(expected);
    }

    @Test
    public void unlink() throws Exception {
        AlluxioURI expectedPath = AlluxioFuseFileSystemTest.BASE_EXPECTED_URI.join("/foo/bar");
        Mockito.doNothing().when(mFileSystem).delete(expectedPath);
        mFuseFs.unlink("/foo/bar");
        Mockito.verify(mFileSystem).delete(expectedPath);
    }

    @Test
    public void pathTranslation() throws Exception {
        final LoadingCache<String, AlluxioURI> resolver = mFuseFs.getPathResolverCache();
        AlluxioURI expected = new AlluxioURI(AlluxioFuseFileSystemTest.TEST_ROOT_PATH);
        AlluxioURI actual = resolver.apply("/");
        Assert.assertEquals(("/ should resolve to " + expected), expected, actual);
        expected = new AlluxioURI(((AlluxioFuseFileSystemTest.TEST_ROOT_PATH) + "/home/foo"));
        actual = resolver.apply("/home/foo");
        Assert.assertEquals(("/home/foo should resolve to " + expected), expected, actual);
    }
}

