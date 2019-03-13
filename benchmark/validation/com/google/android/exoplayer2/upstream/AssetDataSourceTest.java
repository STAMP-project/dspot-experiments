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
package com.google.android.exoplayer2.upstream;


import RuntimeEnvironment.application;
import android.net.Uri;
import com.google.android.exoplayer2.testutil.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


/**
 * Unit tests for {@link AssetDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
public final class AssetDataSourceTest {
    private static final String DATA_PATH = "binary/1024_incrementing_bytes.mp3";

    @Test
    public void testReadFileUri() throws Exception {
        AssetDataSource dataSource = new AssetDataSource(RuntimeEnvironment.application);
        DataSpec dataSpec = new DataSpec(Uri.parse(("file:///android_asset/" + (AssetDataSourceTest.DATA_PATH))));
        TestUtil.assertDataSourceContent(dataSource, dataSpec, TestUtil.getByteArray(application, AssetDataSourceTest.DATA_PATH), true);
    }

    @Test
    public void testReadAssetUri() throws Exception {
        AssetDataSource dataSource = new AssetDataSource(RuntimeEnvironment.application);
        DataSpec dataSpec = new DataSpec(Uri.parse(("asset:///" + (AssetDataSourceTest.DATA_PATH))));
        TestUtil.assertDataSourceContent(dataSource, dataSpec, TestUtil.getByteArray(application, AssetDataSourceTest.DATA_PATH), true);
    }
}

