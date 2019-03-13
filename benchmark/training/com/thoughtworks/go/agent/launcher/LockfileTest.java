/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.agent.launcher;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class LockfileTest {
    private static final File LOCK_FILE = new File("LockFile.txt");

    @Test
    public void shouldNotExistIfNotChangedRecently() {
        File mockfile = Mockito.mock(File.class);
        Lockfile lockfile = new Lockfile(mockfile);
        Lockfile spy = Mockito.spy(lockfile);
        Mockito.doReturn(false).when(spy).lockFileChangedWithinMinutes(10);
        Mockito.when(lockfile.exists()).thenReturn(true);
        Assert.assertThat(spy.exists(), Matchers.is(false));
    }

    @Test
    public void shouldExistIfFileExistsAndChangedRecently() {
        File mockfile = Mockito.mock(File.class);
        Lockfile lockfile = new Lockfile(mockfile);
        Lockfile spy = Mockito.spy(lockfile);
        Mockito.doReturn(true).when(spy).lockFileChangedWithinMinutes(10);
        Mockito.when(lockfile.exists()).thenReturn(true);
        Assert.assertThat(spy.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotAttemptToDeleteLockFileIfItDoesNotExist() {
        File mockfile = Mockito.mock(File.class);
        Lockfile lockfile = new Lockfile(mockfile);
        Mockito.when(mockfile.exists()).thenReturn(false);
        lockfile.delete();
        Mockito.verify(mockfile, Mockito.never()).delete();
    }

    @Test
    public void shouldSpawnTouchLoopOnSet() throws IOException {
        Lockfile lockfile = Mockito.mock(Lockfile.class);
        Mockito.doCallRealMethod().when(lockfile).setHooks();
        Mockito.doNothing().when(lockfile).touch();
        Mockito.doNothing().when(lockfile).spawnTouchLoop();
        lockfile.setHooks();
        Mockito.verify(lockfile).spawnTouchLoop();
    }

    @Test
    public void shouldReturnFalseIfLockFileAlreadyExists() throws IOException {
        File mockfile = Mockito.mock(File.class);
        Lockfile lockfile = new Lockfile(mockfile);
        Mockito.when(mockfile.exists()).thenReturn(true);
        Mockito.when(mockfile.lastModified()).thenReturn(System.currentTimeMillis());
        Assert.assertThat(lockfile.tryLock(), Matchers.is(false));
        Mockito.verify(mockfile).exists();
    }

    @Test
    public void shouldReturnFalseifUnableToSetLock() throws IOException {
        File mockfile = Mockito.mock(File.class);
        Lockfile lockfile = Mockito.spy(new Lockfile(mockfile));
        Mockito.when(mockfile.exists()).thenReturn(false);
        Mockito.when(mockfile.getAbsolutePath()).thenReturn("/abcd/dummyFile");
        Mockito.doThrow(new IOException("dummy")).when(lockfile).setHooks();
        Assert.assertThat(lockfile.tryLock(), Matchers.is(false));
        Mockito.verify(mockfile).exists();
    }

    @Test
    public void shouldReturnTrueIfCanSetLockAndDeleteLockFileWhenDeleteIsCalled() throws IOException {
        Lockfile lockfile = new Lockfile(LockfileTest.LOCK_FILE);
        Assert.assertThat(lockfile.tryLock(), Matchers.is(true));
        lockfile.delete();
        Assert.assertThat(LockfileTest.LOCK_FILE.exists(), Matchers.is(false));
    }

    @Test
    public void shouldNotDeleteLockFileIfTryLockHasFailed() throws IOException {
        FileUtils.touch(LockfileTest.LOCK_FILE);
        Lockfile lockfile = new Lockfile(LockfileTest.LOCK_FILE);
        Assert.assertThat(lockfile.tryLock(), Matchers.is(false));
        lockfile.delete();
        Assert.assertThat(LockfileTest.LOCK_FILE.exists(), Matchers.is(true));
    }
}

