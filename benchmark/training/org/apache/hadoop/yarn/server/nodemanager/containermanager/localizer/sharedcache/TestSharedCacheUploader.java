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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.sharedcache;


import LocalResourceVisibility.PUBLIC;
import YarnConfiguration.SHARED_CACHE_ENABLED;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.server.api.SCMUploaderProtocol;
import org.apache.hadoop.yarn.server.api.protocolrecords.SCMUploaderNotifyRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.SCMUploaderNotifyResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestSharedCacheUploader {
    /**
     * If verifyAccess fails, the upload should fail
     */
    @Test
    public void testFailVerifyAccess() throws Exception {
        SharedCacheUploader spied = createSpiedUploader();
        Mockito.doReturn(false).when(spied).verifyAccess();
        Assert.assertFalse(spied.call());
    }

    /**
     * If rename fails, the upload should fail
     */
    @Test
    public void testRenameFail() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(SHARED_CACHE_ENABLED, true);
        LocalResource resource = Mockito.mock(LocalResource.class);
        Path localPath = Mockito.mock(Path.class);
        Mockito.when(localPath.getName()).thenReturn("foo.jar");
        String user = "joe";
        SCMUploaderProtocol scmClient = Mockito.mock(SCMUploaderProtocol.class);
        SCMUploaderNotifyResponse response = Mockito.mock(SCMUploaderNotifyResponse.class);
        Mockito.when(response.getAccepted()).thenReturn(true);
        Mockito.when(scmClient.notify(ArgumentMatchers.isA(SCMUploaderNotifyRequest.class))).thenReturn(response);
        FileSystem fs = Mockito.mock(FileSystem.class);
        // return false when rename is called
        Mockito.when(fs.rename(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(Path.class))).thenReturn(false);
        FileSystem localFs = FileSystem.getLocal(conf);
        SharedCacheUploader spied = createSpiedUploader(resource, localPath, user, conf, scmClient, fs, localFs);
        // stub verifyAccess() to return true
        Mockito.doReturn(true).when(spied).verifyAccess();
        // stub getActualPath()
        Mockito.doReturn(localPath).when(spied).getActualPath();
        // stub computeChecksum()
        Mockito.doReturn("abcdef0123456789").when(spied).computeChecksum(ArgumentMatchers.isA(Path.class));
        // stub uploadFile() to return true
        Mockito.doReturn(true).when(spied).uploadFile(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(Path.class));
        Assert.assertFalse(spied.call());
    }

    /**
     * If verifyAccess, uploadFile, rename, and notification succeed, the upload
     * should succeed
     */
    @Test
    public void testSuccess() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(SHARED_CACHE_ENABLED, true);
        LocalResource resource = Mockito.mock(LocalResource.class);
        Path localPath = Mockito.mock(Path.class);
        Mockito.when(localPath.getName()).thenReturn("foo.jar");
        String user = "joe";
        SCMUploaderProtocol scmClient = Mockito.mock(SCMUploaderProtocol.class);
        SCMUploaderNotifyResponse response = Mockito.mock(SCMUploaderNotifyResponse.class);
        Mockito.when(response.getAccepted()).thenReturn(true);
        Mockito.when(scmClient.notify(ArgumentMatchers.isA(SCMUploaderNotifyRequest.class))).thenReturn(response);
        FileSystem fs = Mockito.mock(FileSystem.class);
        // return false when rename is called
        Mockito.when(fs.rename(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(Path.class))).thenReturn(true);
        FileSystem localFs = FileSystem.getLocal(conf);
        SharedCacheUploader spied = createSpiedUploader(resource, localPath, user, conf, scmClient, fs, localFs);
        // stub verifyAccess() to return true
        Mockito.doReturn(true).when(spied).verifyAccess();
        // stub getActualPath()
        Mockito.doReturn(localPath).when(spied).getActualPath();
        // stub computeChecksum()
        Mockito.doReturn("abcdef0123456789").when(spied).computeChecksum(ArgumentMatchers.isA(Path.class));
        // stub uploadFile() to return true
        Mockito.doReturn(true).when(spied).uploadFile(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(Path.class));
        // stub notifySharedCacheManager to return true
        Mockito.doReturn(true).when(spied).notifySharedCacheManager(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(String.class));
        Assert.assertTrue(spied.call());
    }

    /**
     * If verifyAccess, uploadFile, and rename succed, but it receives a nay from
     * SCM, the file should be deleted
     */
    @Test
    public void testNotifySCMFail() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(SHARED_CACHE_ENABLED, true);
        LocalResource resource = Mockito.mock(LocalResource.class);
        Path localPath = Mockito.mock(Path.class);
        Mockito.when(localPath.getName()).thenReturn("foo.jar");
        String user = "joe";
        FileSystem fs = Mockito.mock(FileSystem.class);
        // return false when rename is called
        Mockito.when(fs.rename(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(Path.class))).thenReturn(true);
        FileSystem localFs = FileSystem.getLocal(conf);
        SharedCacheUploader spied = createSpiedUploader(resource, localPath, user, conf, null, fs, localFs);
        // stub verifyAccess() to return true
        Mockito.doReturn(true).when(spied).verifyAccess();
        // stub getActualPath()
        Mockito.doReturn(localPath).when(spied).getActualPath();
        // stub computeChecksum()
        Mockito.doReturn("abcdef0123456789").when(spied).computeChecksum(ArgumentMatchers.isA(Path.class));
        // stub uploadFile() to return true
        Mockito.doReturn(true).when(spied).uploadFile(ArgumentMatchers.isA(Path.class), ArgumentMatchers.isA(Path.class));
        // stub notifySharedCacheManager to return true
        Mockito.doReturn(false).when(spied).notifySharedCacheManager(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(String.class));
        Assert.assertFalse(spied.call());
        Mockito.verify(fs).delete(ArgumentMatchers.isA(Path.class), ArgumentMatchers.anyBoolean());
    }

    /**
     * If resource is public, verifyAccess should succeed
     */
    @Test
    public void testVerifyAccessPublicResource() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(SHARED_CACHE_ENABLED, true);
        LocalResource resource = Mockito.mock(LocalResource.class);
        // give public visibility
        Mockito.when(resource.getVisibility()).thenReturn(PUBLIC);
        Path localPath = Mockito.mock(Path.class);
        Mockito.when(localPath.getName()).thenReturn("foo.jar");
        String user = "joe";
        SCMUploaderProtocol scmClient = Mockito.mock(SCMUploaderProtocol.class);
        FileSystem fs = Mockito.mock(FileSystem.class);
        FileSystem localFs = FileSystem.getLocal(conf);
        SharedCacheUploader spied = createSpiedUploader(resource, localPath, user, conf, scmClient, fs, localFs);
        Assert.assertTrue(spied.verifyAccess());
    }

    /**
     * If the localPath does not exists, getActualPath should get to one level
     * down
     */
    @Test
    public void testGetActualPath() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(SHARED_CACHE_ENABLED, true);
        LocalResource resource = Mockito.mock(LocalResource.class);
        // give public visibility
        Mockito.when(resource.getVisibility()).thenReturn(PUBLIC);
        Path localPath = new Path("foo.jar");
        String user = "joe";
        SCMUploaderProtocol scmClient = Mockito.mock(SCMUploaderProtocol.class);
        FileSystem fs = Mockito.mock(FileSystem.class);
        FileSystem localFs = Mockito.mock(FileSystem.class);
        // stub it to return a status that indicates a directory
        FileStatus status = Mockito.mock(FileStatus.class);
        Mockito.when(status.isDirectory()).thenReturn(true);
        Mockito.when(localFs.getFileStatus(localPath)).thenReturn(status);
        SharedCacheUploader spied = createSpiedUploader(resource, localPath, user, conf, scmClient, fs, localFs);
        Path actualPath = spied.getActualPath();
        Assert.assertEquals(actualPath.getName(), localPath.getName());
        Assert.assertEquals(actualPath.getParent().getName(), localPath.getName());
    }
}

