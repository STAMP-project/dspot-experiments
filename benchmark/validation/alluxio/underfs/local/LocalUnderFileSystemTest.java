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
package alluxio.underfs.local;


import AlluxioURI.SEPARATOR;
import PropertyKey.NETWORK_HOST_RESOLUTION_TIMEOUT_MS;
import UfsMode.NO_ACCESS;
import UfsMode.READ_WRITE;
import alluxio.conf.AlluxioConfiguration;
import alluxio.underfs.UfsMode;
import alluxio.underfs.UnderFileSystem;
import alluxio.underfs.options.DeleteOptions;
import alluxio.underfs.options.MkdirsOptions;
import alluxio.util.ConfigurationUtils;
import alluxio.util.io.PathUtils;
import alluxio.util.network.NetworkAddressUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for the {@link LocalUnderFileSystem}.
 */
public class LocalUnderFileSystemTest {
    private String mLocalUfsRoot;

    private UnderFileSystem mLocalUfs;

    private static AlluxioConfiguration sConf = new alluxio.conf.InstancedConfiguration(ConfigurationUtils.defaults());

    @Rule
    public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void exists() throws IOException {
        String filepath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        mLocalUfs.create(filepath).close();
        Assert.assertTrue(mLocalUfs.isFile(filepath));
        mLocalUfs.deleteFile(filepath);
        Assert.assertFalse(mLocalUfs.isFile(filepath));
    }

    @Test
    public void create() throws IOException {
        String filepath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        OutputStream os = mLocalUfs.create(filepath);
        os.close();
        Assert.assertTrue(mLocalUfs.isFile(filepath));
        File file = new File(filepath);
        Assert.assertTrue(file.exists());
    }

    @Test
    public void deleteFile() throws IOException {
        String filepath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        mLocalUfs.create(filepath).close();
        mLocalUfs.deleteFile(filepath);
        Assert.assertFalse(mLocalUfs.isFile(filepath));
        File file = new File(filepath);
        Assert.assertFalse(file.exists());
    }

    @Test
    public void recursiveDelete() throws IOException {
        String dirpath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        mLocalUfs.mkdirs(dirpath);
        String filepath = PathUtils.concatPath(dirpath, getUniqueFileName());
        mLocalUfs.create(filepath).close();
        mLocalUfs.deleteDirectory(dirpath, DeleteOptions.defaults().setRecursive(true));
        Assert.assertFalse(mLocalUfs.isDirectory(dirpath));
        File file = new File(filepath);
        Assert.assertFalse(file.exists());
    }

    @Test
    public void nonRecursiveDelete() throws IOException {
        String dirpath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        mLocalUfs.mkdirs(dirpath);
        String filepath = PathUtils.concatPath(dirpath, getUniqueFileName());
        mLocalUfs.create(filepath).close();
        mLocalUfs.deleteDirectory(dirpath, DeleteOptions.defaults().setRecursive(false));
        Assert.assertTrue(mLocalUfs.isDirectory(dirpath));
        File file = new File(filepath);
        Assert.assertTrue(file.exists());
    }

    @Test
    public void mkdirs() throws IOException {
        String parentPath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        String dirpath = PathUtils.concatPath(parentPath, getUniqueFileName());
        mLocalUfs.mkdirs(dirpath);
        Assert.assertTrue(mLocalUfs.isDirectory(dirpath));
        File file = new File(dirpath);
        Assert.assertTrue(file.exists());
    }

    @Test
    public void mkdirsWithCreateParentEqualToFalse() throws IOException {
        String parentPath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        String dirpath = PathUtils.concatPath(parentPath, getUniqueFileName());
        mLocalUfs.mkdirs(dirpath, MkdirsOptions.defaults(LocalUnderFileSystemTest.sConf).setCreateParent(false));
        Assert.assertFalse(mLocalUfs.isDirectory(dirpath));
        File file = new File(dirpath);
        Assert.assertFalse(file.exists());
    }

    @Test
    public void open() throws IOException {
        byte[] bytes = getBytes();
        String filepath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        OutputStream os = mLocalUfs.create(filepath);
        os.write(bytes);
        os.close();
        InputStream is = mLocalUfs.open(filepath);
        byte[] bytes1 = new byte[bytes.length];
        is.read(bytes1);
        is.close();
        Assert.assertArrayEquals(bytes, bytes1);
    }

    @Test
    public void getFileLocations() throws IOException {
        byte[] bytes = getBytes();
        String filepath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        OutputStream os = mLocalUfs.create(filepath);
        os.write(bytes);
        os.close();
        List<String> fileLocations = mLocalUfs.getFileLocations(filepath);
        Assert.assertEquals(1, fileLocations.size());
        Assert.assertEquals(NetworkAddressUtils.getLocalHostName(((int) (LocalUnderFileSystemTest.sConf.getMs(NETWORK_HOST_RESOLUTION_TIMEOUT_MS)))), fileLocations.get(0));
    }

    @Test
    public void getOperationMode() throws IOException {
        Map<String, UfsMode> physicalUfsState = new Hashtable<>();
        // Check default
        Assert.assertEquals(READ_WRITE, mLocalUfs.getOperationMode(physicalUfsState));
        // Check NO_ACCESS mode
        physicalUfsState.put(SEPARATOR, NO_ACCESS);
        Assert.assertEquals(NO_ACCESS, mLocalUfs.getOperationMode(physicalUfsState));
    }

    @Test
    public void isFile() throws IOException {
        String dirpath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        mLocalUfs.mkdirs(dirpath);
        Assert.assertFalse(mLocalUfs.isFile(dirpath));
        String filepath = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        mLocalUfs.create(filepath).close();
        Assert.assertTrue(mLocalUfs.isFile(filepath));
    }

    @Test
    public void renameFile() throws IOException {
        byte[] bytes = getBytes();
        String filepath1 = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        OutputStream os = mLocalUfs.create(filepath1);
        os.write(bytes);
        os.close();
        String filepath2 = PathUtils.concatPath(mLocalUfsRoot, getUniqueFileName());
        mLocalUfs.renameFile(filepath1, filepath2);
        InputStream is = mLocalUfs.open(filepath2);
        byte[] bytes1 = new byte[bytes.length];
        is.read(bytes1);
        is.close();
        Assert.assertArrayEquals(bytes, bytes1);
    }
}

