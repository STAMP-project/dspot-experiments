/**
 * Copyright 2013 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.tests;


import JnaUtils.Filestat;
import JnaUtils.S_IFDIR;
import JnaUtils.S_IFREG;
import JnaUtils.S_ISGID;
import com.gitblit.utils.JGitUtils;
import com.gitblit.utils.JnaUtils;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.jgit.lib.RepositoryCache.FileKey.resolve;


/**
 *
 *
 * @author Florian Zschocke
 */
public class JnaUtilsTest extends GitblitUnitTest {
    @Test
    public void testGetgid() {
        if (JnaUtils.isWindows()) {
            try {
                JnaUtils.getFilemode(GitBlitSuite.REPOSITORIES);
            } catch (UnsupportedOperationException e) {
            }
        } else {
            int gid = JnaUtils.getgid();
            Assert.assertTrue((gid >= 0));
            int egid = JnaUtils.getegid();
            Assert.assertTrue((egid >= 0));
            Assert.assertTrue("Really? You're running unit tests as root?!", (gid > 0));
            System.out.println(((("gid: " + gid) + "  egid: ") + egid));
        }
    }

    @Test
    public void testGetFilemode() throws IOException {
        if (JnaUtils.isWindows()) {
            try {
                JnaUtils.getFilemode(GitBlitSuite.REPOSITORIES);
            } catch (UnsupportedOperationException e) {
            }
        } else {
            String repositoryName = "NewJnaTestRepository.git";
            Repository repository = JGitUtils.createRepository(GitBlitSuite.REPOSITORIES, repositoryName);
            File folder = resolve(new File(GitBlitSuite.REPOSITORIES, repositoryName), FS.DETECTED);
            Assert.assertTrue(folder.exists());
            int mode = JnaUtils.getFilemode(folder);
            Assert.assertTrue((mode > 0));
            Assert.assertEquals(S_IFDIR, (mode & (JnaUtils.S_IFMT)));// directory

            Assert.assertEquals((((JnaUtils.S_IRUSR) | (JnaUtils.S_IWUSR)) | (JnaUtils.S_IXUSR)), (mode & (JnaUtils.S_IRWXU)));// owner full access

            mode = JnaUtils.getFilemode(((folder.getAbsolutePath()) + "/config"));
            Assert.assertTrue((mode > 0));
            Assert.assertEquals(S_IFREG, (mode & (JnaUtils.S_IFMT)));// directory

            Assert.assertEquals(((JnaUtils.S_IRUSR) | (JnaUtils.S_IWUSR)), (mode & (JnaUtils.S_IRWXU)));// owner full access

            repository.close();
            RepositoryCache.close(repository);
            FileUtils.deleteDirectory(repository.getDirectory());
        }
    }

    @Test
    public void testSetFilemode() throws IOException {
        if (JnaUtils.isWindows()) {
            try {
                JnaUtils.getFilemode(GitBlitSuite.REPOSITORIES);
            } catch (UnsupportedOperationException e) {
            }
        } else {
            String repositoryName = "NewJnaTestRepository.git";
            Repository repository = JGitUtils.createRepository(GitBlitSuite.REPOSITORIES, repositoryName);
            File folder = resolve(new File(GitBlitSuite.REPOSITORIES, repositoryName), FS.DETECTED);
            Assert.assertTrue(folder.exists());
            File path = new File(folder, "refs");
            int mode = JnaUtils.getFilemode(path);
            Assert.assertTrue((mode > 0));
            Assert.assertEquals(S_IFDIR, (mode & (JnaUtils.S_IFMT)));// directory

            Assert.assertEquals((((JnaUtils.S_IRUSR) | (JnaUtils.S_IWUSR)) | (JnaUtils.S_IXUSR)), (mode & (JnaUtils.S_IRWXU)));// owner full access

            mode |= JnaUtils.S_ISGID;
            mode |= JnaUtils.S_IRWXG;
            int ret = JnaUtils.setFilemode(path, mode);
            Assert.assertEquals(0, ret);
            mode = JnaUtils.getFilemode(path);
            Assert.assertTrue((mode > 0));
            Assert.assertEquals(S_ISGID, (mode & (JnaUtils.S_ISGID)));// set-gid-bit set

            Assert.assertEquals((((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)) | (JnaUtils.S_IXGRP)), (mode & (JnaUtils.S_IRWXG)));// group full access

            path = new File(folder, "config");
            mode = JnaUtils.getFilemode(path.getAbsolutePath());
            Assert.assertTrue((mode > 0));
            Assert.assertEquals(S_IFREG, (mode & (JnaUtils.S_IFMT)));// directory

            Assert.assertEquals(((JnaUtils.S_IRUSR) | (JnaUtils.S_IWUSR)), (mode & (JnaUtils.S_IRWXU)));// owner full access

            mode |= (JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP);
            ret = JnaUtils.setFilemode(path.getAbsolutePath(), mode);
            Assert.assertEquals(0, ret);
            mode = JnaUtils.getFilemode(path.getAbsolutePath());
            Assert.assertTrue((mode > 0));
            Assert.assertEquals(((JnaUtils.S_IRGRP) | (JnaUtils.S_IWGRP)), (mode & (JnaUtils.S_IRWXG)));// group full access

            repository.close();
            RepositoryCache.close(repository);
            FileUtils.deleteDirectory(repository.getDirectory());
        }
    }

    @Test
    public void testGetFilestat() {
        if (JnaUtils.isWindows()) {
            try {
                JnaUtils.getFilemode(GitBlitSuite.REPOSITORIES);
            } catch (UnsupportedOperationException e) {
            }
        } else {
            JnaUtils.Filestat stat = JnaUtils.getFilestat(GitBlitSuite.REPOSITORIES);
            Assert.assertNotNull(stat);
            Assert.assertTrue(((stat.mode) > 0));
            Assert.assertTrue(((stat.uid) > 0));
            Assert.assertTrue(((stat.gid) > 0));
        }
    }
}

