/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.source.dash.offline;


import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.upstream.DummyDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link DashDownloadAction}.
 */
@RunWith(RobolectricTestRunner.class)
public class DashDownloadActionTest {
    private Uri uri1;

    private Uri uri2;

    @Test
    public void testDownloadActionIsNotRemoveAction() {
        DownloadAction action = DashDownloadActionTest.createDownloadAction(uri1);
        assertThat(action.isRemoveAction).isFalse();
    }

    @Test
    public void testRemoveActionIsRemoveAction() {
        DownloadAction action2 = DashDownloadActionTest.createRemoveAction(uri1);
        assertThat(action2.isRemoveAction).isTrue();
    }

    @Test
    public void testCreateDownloader() {
        MockitoAnnotations.initMocks(this);
        DownloadAction action = DashDownloadActionTest.createDownloadAction(uri1);
        DownloaderConstructorHelper constructorHelper = new DownloaderConstructorHelper(Mockito.mock(Cache.class), DummyDataSource.FACTORY);
        assertThat(action.createDownloader(constructorHelper)).isNotNull();
    }

    @Test
    public void testSameUriDifferentAction_IsSameMedia() {
        DownloadAction action1 = DashDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action2 = DashDownloadActionTest.createDownloadAction(uri1);
        assertThat(action1.isSameMedia(action2)).isTrue();
    }

    @Test
    public void testDifferentUriAndAction_IsNotSameMedia() {
        DownloadAction action3 = DashDownloadActionTest.createRemoveAction(uri2);
        DownloadAction action4 = DashDownloadActionTest.createDownloadAction(uri1);
        assertThat(action3.isSameMedia(action4)).isFalse();
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEquals() {
        DownloadAction action1 = DashDownloadActionTest.createRemoveAction(uri1);
        assertThat(action1.equals(action1)).isTrue();
        DownloadAction action2 = DashDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action3 = DashDownloadActionTest.createRemoveAction(uri1);
        DashDownloadActionTest.assertEqual(action2, action3);
        DownloadAction action4 = DashDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action5 = DashDownloadActionTest.createDownloadAction(uri1);
        DashDownloadActionTest.assertNotEqual(action4, action5);
        DownloadAction action6 = DashDownloadActionTest.createDownloadAction(uri1);
        DownloadAction action7 = DashDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0, 0));
        DashDownloadActionTest.assertNotEqual(action6, action7);
        DownloadAction action8 = DashDownloadActionTest.createDownloadAction(uri1, new StreamKey(1, 1, 1));
        DownloadAction action9 = DashDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0, 0));
        DashDownloadActionTest.assertNotEqual(action8, action9);
        DownloadAction action10 = DashDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action11 = DashDownloadActionTest.createRemoveAction(uri2);
        DashDownloadActionTest.assertNotEqual(action10, action11);
        DownloadAction action12 = DashDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0, 0), new StreamKey(1, 1, 1));
        DownloadAction action13 = DashDownloadActionTest.createDownloadAction(uri1, new StreamKey(1, 1, 1), new StreamKey(0, 0, 0));
        DashDownloadActionTest.assertEqual(action12, action13);
        DownloadAction action14 = DashDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0, 0));
        DownloadAction action15 = DashDownloadActionTest.createDownloadAction(uri1, new StreamKey(1, 1, 1), new StreamKey(0, 0, 0));
        DashDownloadActionTest.assertNotEqual(action14, action15);
        DownloadAction action16 = DashDownloadActionTest.createDownloadAction(uri1);
        DownloadAction action17 = DashDownloadActionTest.createDownloadAction(uri1);
        DashDownloadActionTest.assertEqual(action16, action17);
    }

    @Test
    public void testSerializerGetType() {
        DownloadAction action = DashDownloadActionTest.createDownloadAction(uri1);
        assertThat(action.type).isNotNull();
    }

    @Test
    public void testSerializerWriteRead() throws Exception {
        DashDownloadActionTest.doTestSerializationRoundTrip(DashDownloadActionTest.createDownloadAction(uri1));
        DashDownloadActionTest.doTestSerializationRoundTrip(DashDownloadActionTest.createRemoveAction(uri1));
        DashDownloadActionTest.doTestSerializationRoundTrip(DashDownloadActionTest.createDownloadAction(uri2, new StreamKey(0, 0, 0), new StreamKey(1, 1, 1)));
    }
}

