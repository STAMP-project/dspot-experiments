/**
 * Copyright (C) 2018 The Android Open Source Project
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
package com.google.android.exoplayer2.source.smoothstreaming.offline;


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
 * Unit tests for {@link SsDownloadAction}.
 */
@RunWith(RobolectricTestRunner.class)
public class SsDownloadActionTest {
    private Uri uri1;

    private Uri uri2;

    @Test
    public void testDownloadActionIsNotRemoveAction() {
        DownloadAction action = SsDownloadActionTest.createDownloadAction(uri1);
        assertThat(action.isRemoveAction).isFalse();
    }

    @Test
    public void testRemoveActionIsRemoveAction() {
        DownloadAction action2 = SsDownloadActionTest.createRemoveAction(uri1);
        assertThat(action2.isRemoveAction).isTrue();
    }

    @Test
    public void testCreateDownloader() {
        MockitoAnnotations.initMocks(this);
        DownloadAction action = SsDownloadActionTest.createDownloadAction(uri1);
        DownloaderConstructorHelper constructorHelper = new DownloaderConstructorHelper(Mockito.mock(Cache.class), DummyDataSource.FACTORY);
        assertThat(action.createDownloader(constructorHelper)).isNotNull();
    }

    @Test
    public void testSameUriDifferentAction_IsSameMedia() {
        DownloadAction action1 = SsDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action2 = SsDownloadActionTest.createDownloadAction(uri1);
        assertThat(action1.isSameMedia(action2)).isTrue();
    }

    @Test
    public void testDifferentUriAndAction_IsNotSameMedia() {
        DownloadAction action3 = SsDownloadActionTest.createRemoveAction(uri2);
        DownloadAction action4 = SsDownloadActionTest.createDownloadAction(uri1);
        assertThat(action3.isSameMedia(action4)).isFalse();
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEquals() {
        DownloadAction action1 = SsDownloadActionTest.createRemoveAction(uri1);
        assertThat(action1.equals(action1)).isTrue();
        DownloadAction action2 = SsDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action3 = SsDownloadActionTest.createRemoveAction(uri1);
        SsDownloadActionTest.assertEqual(action2, action3);
        DownloadAction action4 = SsDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action5 = SsDownloadActionTest.createDownloadAction(uri1);
        SsDownloadActionTest.assertNotEqual(action4, action5);
        DownloadAction action6 = SsDownloadActionTest.createDownloadAction(uri1);
        DownloadAction action7 = SsDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0));
        SsDownloadActionTest.assertNotEqual(action6, action7);
        DownloadAction action8 = SsDownloadActionTest.createDownloadAction(uri1, new StreamKey(1, 1));
        DownloadAction action9 = SsDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0));
        SsDownloadActionTest.assertNotEqual(action8, action9);
        DownloadAction action10 = SsDownloadActionTest.createRemoveAction(uri1);
        DownloadAction action11 = SsDownloadActionTest.createRemoveAction(uri2);
        SsDownloadActionTest.assertNotEqual(action10, action11);
        DownloadAction action12 = SsDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0), new StreamKey(1, 1));
        DownloadAction action13 = SsDownloadActionTest.createDownloadAction(uri1, new StreamKey(1, 1), new StreamKey(0, 0));
        SsDownloadActionTest.assertEqual(action12, action13);
        DownloadAction action14 = SsDownloadActionTest.createDownloadAction(uri1, new StreamKey(0, 0));
        DownloadAction action15 = SsDownloadActionTest.createDownloadAction(uri1, new StreamKey(1, 1), new StreamKey(0, 0));
        SsDownloadActionTest.assertNotEqual(action14, action15);
        DownloadAction action16 = SsDownloadActionTest.createDownloadAction(uri1);
        DownloadAction action17 = SsDownloadActionTest.createDownloadAction(uri1);
        SsDownloadActionTest.assertEqual(action16, action17);
    }

    @Test
    public void testSerializerGetType() {
        DownloadAction action = SsDownloadActionTest.createDownloadAction(uri1);
        assertThat(action.type).isNotNull();
    }

    @Test
    public void testSerializerWriteRead() throws Exception {
        SsDownloadActionTest.doTestSerializationRoundTrip(SsDownloadActionTest.createDownloadAction(uri1));
        SsDownloadActionTest.doTestSerializationRoundTrip(SsDownloadActionTest.createRemoveAction(uri1));
        SsDownloadActionTest.doTestSerializationRoundTrip(SsDownloadActionTest.createDownloadAction(uri2, new StreamKey(0, 0), new StreamKey(1, 1)));
    }

    @Test
    public void testSerializerVersion0() throws Exception {
        SsDownloadActionTest.doTestSerializationV0RoundTrip(SsDownloadActionTest.createDownloadAction(uri1));
        SsDownloadActionTest.doTestSerializationV0RoundTrip(SsDownloadActionTest.createRemoveAction(uri1));
        SsDownloadActionTest.doTestSerializationV0RoundTrip(SsDownloadActionTest.createDownloadAction(uri2, new StreamKey(0, 0), new StreamKey(1, 1)));
    }
}

