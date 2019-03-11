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
package com.liulishuo.okdownload.core.breakpoint;


import RemitSyncExecutor.RemitAgent;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class RemitSyncExecutorTest {
    private RemitSyncExecutor executor;

    private Set<Integer> freeToDBIdList = new HashSet<>();

    @Mock
    RemitAgent agent;

    @Mock
    Handler handler;

    private ArgumentCaptor<Message> messageCaptor;

    private List<Integer> idList;

    @Test
    public void shutdown() {
        executor.shutdown();
        Mockito.verify(handler.getLooper()).quit();
    }

    @Test
    public void isFreeToDatabase() {
        freeToDBIdList.add(1);
        assertThat(executor.isFreeToDatabase(1)).isTrue();
        freeToDBIdList.remove(1);
        assertThat(executor.isFreeToDatabase(1)).isFalse();
    }

    @Test
    public void postSyncInfoDelay() {
        executor.postSyncInfoDelay(1, 10);
        Mockito.verify(handler).sendEmptyMessageDelayed(ArgumentMatchers.eq(1), ArgumentMatchers.eq(10L));
    }

    @Test
    public void postSync() {
        executor.postSync(1);
        Mockito.verify(handler).sendEmptyMessage(ArgumentMatchers.eq(1));
        executor.postSync(idList);
        Mockito.verify(handler).sendMessage(messageCaptor.capture());
        final Message message = messageCaptor.getValue();
        assertThat(message.obj).isEqualTo(idList);
        assertThat(message.what).isEqualTo(RemitSyncExecutor.WHAT_SYNC_BUNCH_ID);
    }

    @Test
    public void postRemoveInfo() {
        executor.postRemoveInfo(1);
        Mockito.verify(handler).sendMessage(messageCaptor.capture());
        final Message message = messageCaptor.getValue();
        assertThat(message.arg1).isEqualTo(1);
        assertThat(message.what).isEqualTo(RemitSyncExecutor.WHAT_REMOVE_INFO);
    }

    @Test
    public void postRemoveFreeIds() {
        executor.postRemoveFreeIds(idList);
        Mockito.verify(handler).sendMessage(messageCaptor.capture());
        final Message message = messageCaptor.getValue();
        assertThat(message.obj).isEqualTo(idList);
        assertThat(message.what).isEqualTo(RemitSyncExecutor.WHAT_REMOVE_FREE_BUNCH_ID);
    }

    @Test
    public void postRemoveFreeId() {
        executor.postRemoveFreeId(1);
        Mockito.verify(handler).sendMessage(messageCaptor.capture());
        final Message message = messageCaptor.getValue();
        assertThat(message.arg1).isEqualTo(1);
        assertThat(message.what).isEqualTo(RemitSyncExecutor.WHAT_REMOVE_FREE_ID);
    }

    @Test
    public void removePostWithId() {
        executor.removePostWithId(1);
        Mockito.verify(handler).removeMessages(ArgumentMatchers.eq(1));
    }

    @Test
    public void removePostWithIds() {
        final int[] ids = new int[3];
        ids[0] = 1;
        ids[1] = 2;
        ids[2] = 3;
        executor.removePostWithIds(ids);
        Mockito.verify(handler).removeMessages(ArgumentMatchers.eq(1));
        Mockito.verify(handler).removeMessages(ArgumentMatchers.eq(2));
        Mockito.verify(handler).removeMessages(ArgumentMatchers.eq(3));
    }

    @Test
    public void handleMessage_removeInfo() {
        final Message message = new Message();
        message.what = RemitSyncExecutor.WHAT_REMOVE_INFO;
        message.arg1 = 1;
        freeToDBIdList.add(1);
        executor.handleMessage(message);
        Mockito.verify(agent).removeInfo(ArgumentMatchers.eq(1));
        assertThat(freeToDBIdList).isEmpty();
    }

    @Test
    public void handleMessage_removeFreeBunchId() {
        final Message message = new Message();
        message.what = RemitSyncExecutor.WHAT_REMOVE_FREE_BUNCH_ID;
        message.obj = idList;
        freeToDBIdList.addAll(idList);
        executor.handleMessage(message);
        assertThat(freeToDBIdList).isEmpty();
    }

    @Test
    public void handleMessage_removeFreeId() {
        final Message message = new Message();
        message.what = RemitSyncExecutor.WHAT_REMOVE_FREE_ID;
        message.arg1 = 1;
        freeToDBIdList.add(1);
        executor.handleMessage(message);
        assertThat(freeToDBIdList).isEmpty();
    }

    @Test
    public void handleMessage_syncBunchId() throws IOException {
        final Message message = new Message();
        message.what = RemitSyncExecutor.WHAT_SYNC_BUNCH_ID;
        message.obj = idList;
        executor.handleMessage(message);
        Mockito.verify(agent).syncCacheToDB(ArgumentMatchers.eq(idList));
        assertThat(freeToDBIdList).containsExactly(1, 2, 3);
    }

    @Test
    public void handleMessage_syncId() throws IOException {
        final Message message = new Message();
        message.what = 1;
        executor.handleMessage(message);
        Mockito.verify(agent).syncCacheToDB(ArgumentMatchers.eq(1));
        assertThat(freeToDBIdList).containsExactly(1);
    }
}

