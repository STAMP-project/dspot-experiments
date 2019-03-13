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


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link DataSchemeDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
public final class DataSchemeDataSourceTest {
    private DataSource schemeDataDataSource;

    @Test
    public void testBase64Data() throws IOException {
        DataSpec dataSpec = DataSchemeDataSourceTest.buildDataSpec(("data:text/plain;base64,eyJwcm92aWRlciI6IndpZGV2aW5lX3Rlc3QiL" + ("CJjb250ZW50X2lkIjoiTWpBeE5WOTBaV0Z5Y3c9PSIsImtleV9pZHMiOlsiMDAwMDAwMDAwMDAwMDAwMDAwMDAwM" + "DAwMDAwMDAwMDAiXX0=")));
        DataSourceAsserts.assertDataSourceContent(schemeDataDataSource, dataSpec, Util.getUtf8Bytes(("{\"provider\":\"widevine_test\",\"content_id\":\"MjAxNV90ZWFycw==\",\"key_ids\":" + "[\"00000000000000000000000000000000\"]}")));
    }

    @Test
    public void testAsciiData() throws IOException {
        DataSourceAsserts.assertDataSourceContent(schemeDataDataSource, DataSchemeDataSourceTest.buildDataSpec("data:,A%20brief%20note"), Util.getUtf8Bytes("A brief note"));
    }

    @Test
    public void testPartialReads() throws IOException {
        byte[] buffer = new byte[18];
        DataSpec dataSpec = DataSchemeDataSourceTest.buildDataSpec("data:,012345678901234567");
        assertThat(schemeDataDataSource.open(dataSpec)).isEqualTo(18);
        assertThat(schemeDataDataSource.read(buffer, 0, 9)).isEqualTo(9);
        assertThat(schemeDataDataSource.read(buffer, 3, 0)).isEqualTo(0);
        assertThat(schemeDataDataSource.read(buffer, 9, 15)).isEqualTo(9);
        assertThat(schemeDataDataSource.read(buffer, 1, 0)).isEqualTo(0);
        assertThat(schemeDataDataSource.read(buffer, 1, 1)).isEqualTo(C.RESULT_END_OF_INPUT);
        assertThat(Util.fromUtf8Bytes(buffer, 0, 18)).isEqualTo("012345678901234567");
    }

    @Test
    public void testIncorrectScheme() {
        try {
            schemeDataDataSource.open(DataSchemeDataSourceTest.buildDataSpec("http://www.google.com"));
            Assert.fail();
        } catch (IOException e) {
            // Expected.
        }
    }

    @Test
    public void testMalformedData() {
        try {
            schemeDataDataSource.open(DataSchemeDataSourceTest.buildDataSpec("data:text/plain;base64,,This%20is%20Content"));
            Assert.fail();
        } catch (IOException e) {
            // Expected.
        }
        try {
            schemeDataDataSource.open(DataSchemeDataSourceTest.buildDataSpec("data:text/plain;base64,IncorrectPadding=="));
            Assert.fail();
        } catch (IOException e) {
            // Expected.
        }
    }
}

