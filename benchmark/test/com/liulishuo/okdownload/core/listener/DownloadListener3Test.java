/**
 * Copyright (c) 2017 LingoChamp Inc.
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
package com.liulishuo.okdownload.core.listener;


import EndCause.CANCELED;
import EndCause.COMPLETED;
import EndCause.ERROR;
import EndCause.FILE_BUSY;
import EndCause.PRE_ALLOCATE_FAILED;
import EndCause.SAME_TASK_BUSY;
import com.liulishuo.okdownload.DownloadTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DownloadListener3Test {
    private DownloadListener3 listener3;

    @Mock
    private DownloadTask task;

    @Mock
    private Exception realCause;

    @Test
    public void end() {
        listener3.taskStart(task);
        Mockito.verify(listener3).started(ArgumentMatchers.eq(task));
        listener3.taskEnd(task, COMPLETED, realCause);
        Mockito.verify(listener3).completed(ArgumentMatchers.eq(task));
        listener3.taskEnd(task, CANCELED, realCause);
        Mockito.verify(listener3).canceled(ArgumentMatchers.eq(task));
        listener3.taskEnd(task, ERROR, realCause);
        Mockito.verify(listener3).error(ArgumentMatchers.eq(task), ArgumentMatchers.eq(realCause));
        listener3.taskEnd(task, PRE_ALLOCATE_FAILED, realCause);
        Mockito.verify(listener3, VerificationModeFactory.times(2)).error(ArgumentMatchers.eq(task), ArgumentMatchers.eq(realCause));
        listener3.taskEnd(task, FILE_BUSY, realCause);
        Mockito.verify(listener3).warn(ArgumentMatchers.eq(task));
        listener3.taskEnd(task, SAME_TASK_BUSY, realCause);
        Mockito.verify(listener3, VerificationModeFactory.times(2)).warn(ArgumentMatchers.eq(task));
    }
}

