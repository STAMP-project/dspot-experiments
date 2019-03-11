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
package alluxio.master.meta;


import alluxio.underfs.UnderFileSystem;
import alluxio.util.executor.ControllableScheduler;
import java.io.Closeable;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests the {@link DailyMetadataBackup}.
 */
public class DailyMetadataBackupTest {
    private MetaMaster mMetaMaster;

    private ControllableScheduler mScheduler;

    private UnderFileSystem mUfs;

    private Random mRandom;

    private String mBackupDir;

    @Test
    public void test() throws Exception {
        int fileToRetain = 1;
        try (Closeable c = toResource()) {
            DailyMetadataBackup dailyBackup = new DailyMetadataBackup(mMetaMaster, mScheduler, mUfs);
            dailyBackup.start();
            int backUpFileNum = 0;
            Mockito.when(mUfs.listStatus(mBackupDir)).thenReturn(generateUfsStatuses((++backUpFileNum)));
            mScheduler.jumpAndExecute(1, TimeUnit.DAYS);
            Mockito.verify(mMetaMaster, Mockito.times(backUpFileNum)).backup(ArgumentMatchers.any());
            int deleteFileNum = getNumOfDeleteFile(backUpFileNum, fileToRetain);
            Mockito.verify(mUfs, Mockito.times(deleteFileNum)).deleteFile(ArgumentMatchers.any());
            Mockito.when(mUfs.listStatus(mBackupDir)).thenReturn(generateUfsStatuses((++backUpFileNum)));
            mScheduler.jumpAndExecute(1, TimeUnit.DAYS);
            Mockito.verify(mMetaMaster, Mockito.times(backUpFileNum)).backup(ArgumentMatchers.any());
            deleteFileNum += getNumOfDeleteFile(backUpFileNum, fileToRetain);
            Mockito.verify(mUfs, Mockito.times(deleteFileNum)).deleteFile(ArgumentMatchers.any());
            Mockito.when(mUfs.listStatus(mBackupDir)).thenReturn(generateUfsStatuses((++backUpFileNum)));
            mScheduler.jumpAndExecute(1, TimeUnit.DAYS);
            Mockito.verify(mMetaMaster, Mockito.times(backUpFileNum)).backup(ArgumentMatchers.any());
            deleteFileNum += getNumOfDeleteFile(backUpFileNum, fileToRetain);
            Mockito.verify(mUfs, Mockito.times(deleteFileNum)).deleteFile(ArgumentMatchers.any());
        }
    }
}

