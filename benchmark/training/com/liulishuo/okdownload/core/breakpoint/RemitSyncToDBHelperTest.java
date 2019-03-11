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
package com.liulishuo.okdownload.core.breakpoint;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class RemitSyncToDBHelperTest {
    private RemitSyncToDBHelper helper;

    @Mock
    private RemitSyncExecutor executor;

    @Test
    public void shutdown() {
        helper.shutdown();
        verify(executor).shutdown();
    }

    @Test
    public void isNotFreeToDatabase() {
        Mockito.when(executor.isFreeToDatabase(1)).thenReturn(true);
        assertThat(helper.isNotFreeToDatabase(1)).isFalse();
        Mockito.when(executor.isFreeToDatabase(1)).thenReturn(false);
        assertThat(helper.isNotFreeToDatabase(1)).isTrue();
    }

    @Test
    public void onTaskStart() {
        helper.onTaskStart(1);
        verify(executor).removePostWithId(ArgumentMatchers.eq(1));
        verify(executor).postSyncInfoDelay(ArgumentMatchers.eq(1), ArgumentMatchers.eq(helper.delayMillis));
    }

    @Test
    public void endAndEnsureToDB() {
        Mockito.when(executor.isFreeToDatabase(1)).thenReturn(true);
        helper.endAndEnsureToDB(1);
        verify(executor).removePostWithId(ArgumentMatchers.eq(1));
        verify(executor).postRemoveFreeId(ArgumentMatchers.eq(1));
        Mockito.verify(executor, Mockito.never()).postSync(ArgumentMatchers.eq(1));
        Mockito.when(executor.isFreeToDatabase(2)).thenReturn(false);
        helper.endAndEnsureToDB(2);
        verify(executor).postSync(ArgumentMatchers.eq(2));
        verify(executor).postRemoveFreeId(ArgumentMatchers.eq(2));
    }

    @Test
    public void discard() {
        helper.discard(1);
        verify(executor).removePostWithId(ArgumentMatchers.eq(1));
        verify(executor).postRemoveInfo(ArgumentMatchers.eq(1));
    }
}

