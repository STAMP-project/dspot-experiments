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
package com.google.android.exoplayer2.source.hls.offline;


import com.google.android.exoplayer2.testutil.FakeDataSet;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link HlsDownloader}.
 */
@RunWith(RobolectricTestRunner.class)
public class HlsDownloaderTest {
    private SimpleCache cache;

    private File tempFolder;

    private FakeDataSet fakeDataSet;

    @Test
    public void testCounterMethods() throws Exception {
        HlsDownloader downloader = getHlsDownloader(HlsDownloadTestData.MASTER_PLAYLIST_URI, HlsDownloaderTest.getKeys(HlsDownloadTestData.MASTER_MEDIA_PLAYLIST_1_INDEX));
        downloader.download();
        assertThat(downloader.getDownloadedBytes()).isEqualTo(((((HlsDownloadTestData.MEDIA_PLAYLIST_DATA.length) + 10) + 11) + 12));
    }

    @Test
    public void testDownloadRepresentation() throws Exception {
        HlsDownloader downloader = getHlsDownloader(HlsDownloadTestData.MASTER_PLAYLIST_URI, HlsDownloaderTest.getKeys(HlsDownloadTestData.MASTER_MEDIA_PLAYLIST_1_INDEX));
        downloader.download();
        assertCachedData(cache, fakeDataSet, HlsDownloadTestData.MASTER_PLAYLIST_URI, HlsDownloadTestData.MEDIA_PLAYLIST_1_URI, ((HlsDownloadTestData.MEDIA_PLAYLIST_1_DIR) + "fileSequence0.ts"), ((HlsDownloadTestData.MEDIA_PLAYLIST_1_DIR) + "fileSequence1.ts"), ((HlsDownloadTestData.MEDIA_PLAYLIST_1_DIR) + "fileSequence2.ts"));
    }

    @Test
    public void testDownloadMultipleRepresentations() throws Exception {
        HlsDownloader downloader = getHlsDownloader(HlsDownloadTestData.MASTER_PLAYLIST_URI, HlsDownloaderTest.getKeys(HlsDownloadTestData.MASTER_MEDIA_PLAYLIST_1_INDEX, HlsDownloadTestData.MASTER_MEDIA_PLAYLIST_2_INDEX));
        downloader.download();
        assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testDownloadAllRepresentations() throws Exception {
        // Add data for the rest of the playlists
        fakeDataSet.setData(HlsDownloadTestData.MEDIA_PLAYLIST_0_URI, HlsDownloadTestData.MEDIA_PLAYLIST_DATA).setRandomData(((HlsDownloadTestData.MEDIA_PLAYLIST_0_DIR) + "fileSequence0.ts"), 10).setRandomData(((HlsDownloadTestData.MEDIA_PLAYLIST_0_DIR) + "fileSequence1.ts"), 11).setRandomData(((HlsDownloadTestData.MEDIA_PLAYLIST_0_DIR) + "fileSequence2.ts"), 12).setData(HlsDownloadTestData.MEDIA_PLAYLIST_3_URI, HlsDownloadTestData.MEDIA_PLAYLIST_DATA).setRandomData(((HlsDownloadTestData.MEDIA_PLAYLIST_3_DIR) + "fileSequence0.ts"), 13).setRandomData(((HlsDownloadTestData.MEDIA_PLAYLIST_3_DIR) + "fileSequence1.ts"), 14).setRandomData(((HlsDownloadTestData.MEDIA_PLAYLIST_3_DIR) + "fileSequence2.ts"), 15);
        HlsDownloader downloader = getHlsDownloader(HlsDownloadTestData.MASTER_PLAYLIST_URI, HlsDownloaderTest.getKeys());
        downloader.download();
        assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testRemove() throws Exception {
        HlsDownloader downloader = getHlsDownloader(HlsDownloadTestData.MASTER_PLAYLIST_URI, HlsDownloaderTest.getKeys(HlsDownloadTestData.MASTER_MEDIA_PLAYLIST_1_INDEX, HlsDownloadTestData.MASTER_MEDIA_PLAYLIST_2_INDEX));
        downloader.download();
        downloader.remove();
        assertCacheEmpty(cache);
    }

    @Test
    public void testDownloadMediaPlaylist() throws Exception {
        HlsDownloader downloader = getHlsDownloader(HlsDownloadTestData.MEDIA_PLAYLIST_1_URI, HlsDownloaderTest.getKeys());
        downloader.download();
        assertCachedData(cache, fakeDataSet, HlsDownloadTestData.MEDIA_PLAYLIST_1_URI, ((HlsDownloadTestData.MEDIA_PLAYLIST_1_DIR) + "fileSequence0.ts"), ((HlsDownloadTestData.MEDIA_PLAYLIST_1_DIR) + "fileSequence1.ts"), ((HlsDownloadTestData.MEDIA_PLAYLIST_1_DIR) + "fileSequence2.ts"));
    }

    @Test
    public void testDownloadEncMediaPlaylist() throws Exception {
        fakeDataSet = new FakeDataSet().setData(HlsDownloadTestData.ENC_MEDIA_PLAYLIST_URI, HlsDownloadTestData.ENC_MEDIA_PLAYLIST_DATA).setRandomData("enc.key", 8).setRandomData("enc2.key", 9).setRandomData("fileSequence0.ts", 10).setRandomData("fileSequence1.ts", 11).setRandomData("fileSequence2.ts", 12);
        HlsDownloader downloader = getHlsDownloader(HlsDownloadTestData.ENC_MEDIA_PLAYLIST_URI, HlsDownloaderTest.getKeys());
        downloader.download();
        assertCachedData(cache, fakeDataSet);
    }
}

