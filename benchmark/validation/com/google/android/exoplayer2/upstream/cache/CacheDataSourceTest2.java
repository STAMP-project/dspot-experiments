/**
 * Copyright (C) 2017 The Android Open Source Project
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
package com.google.android.exoplayer2.upstream.cache;


import android.net.Uri;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Additional tests for {@link CacheDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
public final class CacheDataSourceTest2 {
    private static final String EXO_CACHE_DIR = "exo";

    private static final int EXO_CACHE_MAX_FILESIZE = 128;

    private static final Uri URI = Uri.parse("http://test.com/content");

    private static final String KEY = "key";

    private static final byte[] DATA = TestUtil.buildTestData(((8 * (CacheDataSourceTest2.EXO_CACHE_MAX_FILESIZE)) + 1));

    // A DataSpec that covers the full file.
    private static final DataSpec FULL = new DataSpec(CacheDataSourceTest2.URI, 0, CacheDataSourceTest2.DATA.length, CacheDataSourceTest2.KEY);

    private static final int OFFSET_ON_BOUNDARY = CacheDataSourceTest2.EXO_CACHE_MAX_FILESIZE;

    // A DataSpec that starts at 0 and extends to a cache file boundary.
    private static final DataSpec END_ON_BOUNDARY = new DataSpec(CacheDataSourceTest2.URI, 0, CacheDataSourceTest2.OFFSET_ON_BOUNDARY, CacheDataSourceTest2.KEY);

    // A DataSpec that starts on the same boundary and extends to the end of the file.
    private static final DataSpec START_ON_BOUNDARY = new DataSpec(CacheDataSourceTest2.URI, CacheDataSourceTest2.OFFSET_ON_BOUNDARY, ((CacheDataSourceTest2.DATA.length) - (CacheDataSourceTest2.OFFSET_ON_BOUNDARY)), CacheDataSourceTest2.KEY);

    private static final int OFFSET_OFF_BOUNDARY = ((CacheDataSourceTest2.EXO_CACHE_MAX_FILESIZE) * 2) + 1;

    // A DataSpec that starts at 0 and extends to just past a cache file boundary.
    private static final DataSpec END_OFF_BOUNDARY = new DataSpec(CacheDataSourceTest2.URI, 0, CacheDataSourceTest2.OFFSET_OFF_BOUNDARY, CacheDataSourceTest2.KEY);

    // A DataSpec that starts on the same boundary and extends to the end of the file.
    private static final DataSpec START_OFF_BOUNDARY = new DataSpec(CacheDataSourceTest2.URI, CacheDataSourceTest2.OFFSET_OFF_BOUNDARY, ((CacheDataSourceTest2.DATA.length) - (CacheDataSourceTest2.OFFSET_OFF_BOUNDARY)), CacheDataSourceTest2.KEY);

    @Test
    public void testWithoutEncryption() throws IOException {
        testReads(false);
    }

    @Test
    public void testWithEncryption() throws IOException {
        testReads(true);
    }
}

