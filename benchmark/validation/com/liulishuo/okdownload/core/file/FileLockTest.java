/**
 * Copyright (c) 2018 LingoChamp Inc.
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
package com.liulishuo.okdownload.core.file;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FileLockTest {
    private FileLock fileLock;

    private final Map<String, AtomicInteger> fileLockCountMap = new HashMap<>();

    private final Map<String, Thread> waitThreadForFileLockMap = new HashMap<>();

    private String filePath1 = "filepath1";

    private String filePath2 = "filepath2";

    @Mock
    private Thread lockedThread;

    @Test
    public void increaseLock() {
        fileLock.increaseLock(filePath1);
        assertThat(fileLockCountMap.get(filePath1).get()).isOne();
        fileLock.increaseLock(filePath1);
        assertThat(fileLockCountMap.get(filePath1).get()).isEqualTo(2);
        fileLock.increaseLock(filePath2);
        assertThat(fileLockCountMap.get(filePath2).get()).isOne();
        fileLock.increaseLock(filePath2);
        assertThat(fileLockCountMap.get(filePath2).get()).isEqualTo(2);
    }

    @Test
    public void decreaseLock() {
        waitThreadForFileLockMap.put(filePath1, lockedThread);
        fileLock.decreaseLock(filePath1);
        assertThat(fileLockCountMap.get(filePath1)).isNull();
        fileLockCountMap.put(filePath1, new AtomicInteger(2));
        fileLock.decreaseLock(filePath1);// 1

        assertThat(fileLockCountMap.get(filePath1).get()).isOne();
        fileLock.decreaseLock(filePath1);// null

        assertThat(fileLockCountMap.get(filePath1)).isNull();
        Mockito.verify(fileLock).unpark(ArgumentMatchers.eq(lockedThread));
        assertThat(waitThreadForFileLockMap.isEmpty()).isTrue();
        fileLock.decreaseLock(filePath2);
        assertThat(fileLockCountMap.get(filePath2)).isNull();
    }

    @Test
    public void waitForRelease() {
        fileLock.waitForRelease(filePath1);
        Mockito.verify(fileLock, Mockito.never()).park();// no lock

        assertThat(waitThreadForFileLockMap.isEmpty()).isTrue();
        fileLockCountMap.put(filePath1, new AtomicInteger(1));
        Mockito.doReturn(false, true).when(fileLock).isNotLocked(ArgumentMatchers.any(AtomicInteger.class));
        fileLock.waitForRelease(filePath1);
        assertThat(waitThreadForFileLockMap.get(filePath1)).isEqualTo(Thread.currentThread());
        Mockito.verify(fileLock).park();
    }
}

