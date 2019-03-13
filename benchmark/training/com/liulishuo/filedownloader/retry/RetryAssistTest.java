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
package com.liulishuo.filedownloader.retry;


import com.liulishuo.okdownload.DownloadTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RetryAssistTest {
    private RetryAssist retryAssist;

    private int retryTimes = 2;

    @Test
    public void constructor() {
        assertThat(retryAssist.retryTimes).isEqualTo(retryTimes);
        assertThat(retryAssist.getRetriedTimes()).isEqualTo(0);
    }

    @Test
    public void doRetry() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        retryAssist.doRetry(task);
        Mockito.verify(task).enqueue(null);
        retryAssist.doRetry(task);
        Mockito.verify(task, Mockito.times(2)).enqueue(null);
    }

    @Test(expected = RuntimeException.class)
    public void doRetry_error() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        retryAssist.doRetry(task);
        retryAssist.doRetry(task);
        // will throw error
        retryAssist.doRetry(task);
    }

    @Test
    public void canRetry() {
        retryAssist.retriedTimes.set(0);
        assertThat(retryAssist.canRetry()).isTrue();
        retryAssist.retriedTimes.set(1);
        assertThat(retryAssist.canRetry()).isTrue();
        retryAssist.retriedTimes.set(2);
        assertThat(retryAssist.canRetry()).isFalse();
    }
}

