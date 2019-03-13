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
package com.google.android.exoplayer2.testutil;


import C.RESULT_END_OF_INPUT;
import android.net.Uri;
import com.google.android.exoplayer2.C;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link FakeDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
public final class FakeDataSourceTest {
    private static final String URI_STRING = "test://test.test";

    private static final byte[] BUFFER = new byte[500];

    private static final byte[] TEST_DATA = TestUtil.buildTestData(15);

    private static final byte[] TEST_DATA_PART_1 = Arrays.copyOf(FakeDataSourceTest.TEST_DATA, 10);

    private static final byte[] TEST_DATA_PART_2 = Arrays.copyOfRange(FakeDataSourceTest.TEST_DATA, 10, 15);

    private static Uri uri;

    private static FakeDataSet fakeDataSet;

    @Test
    public void testReadFull() throws IOException {
        FakeDataSource dataSource = new FakeDataSource(FakeDataSourceTest.fakeDataSet);
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri))).isEqualTo(15);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(10);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA_PART_1);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 10, FakeDataSourceTest.BUFFER.length)).isEqualTo(5);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 15, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 20, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        dataSource.close();
    }

    @Test
    public void testReadPartialOpenEnded() throws IOException {
        FakeDataSource dataSource = new FakeDataSource(FakeDataSourceTest.fakeDataSet);
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri, 7, C.LENGTH_UNSET, null))).isEqualTo(8);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(3);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA_PART_1, 7, 3);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(5);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA_PART_2);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 15, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        dataSource.close();
    }

    @Test
    public void testReadPartialBounded() throws IOException {
        FakeDataSource dataSource = new FakeDataSource(FakeDataSourceTest.fakeDataSet);
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri, 9, 3, null))).isEqualTo(3);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(1);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA_PART_1, 9, 1);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(2);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA_PART_2, 0, 2);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        dataSource.close();
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri, 11, 4, null))).isEqualTo(4);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(4);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA_PART_2, 1, 4);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        dataSource.close();
    }

    @Test
    public void testDummyData() throws IOException {
        FakeDataSource dataSource = new FakeDataSource(new FakeDataSet().newData(FakeDataSourceTest.uri.toString()).appendReadData(100).appendReadData(FakeDataSourceTest.TEST_DATA).appendReadData(200).endData());
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri))).isEqualTo(315);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(100);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(15);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(200);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        dataSource.close();
    }

    @Test
    public void testException() throws IOException {
        String errorMessage = "error, error, error";
        IOException exception = new IOException(errorMessage);
        FakeDataSource dataSource = new FakeDataSource(new FakeDataSet().newData(FakeDataSourceTest.uri.toString()).appendReadData(FakeDataSourceTest.TEST_DATA).appendReadError(exception).appendReadData(FakeDataSourceTest.TEST_DATA).endData());
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri))).isEqualTo(30);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(15);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        try {
            dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length);
            Assert.fail("IOException expected.");
        } catch (IOException e) {
            assertThat(e).hasMessageThat().isEqualTo(errorMessage);
        }
        try {
            dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length);
            Assert.fail("IOException expected.");
        } catch (IOException e) {
            assertThat(e).hasMessageThat().isEqualTo(errorMessage);
        }
        dataSource.close();
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri, 15, 15, null))).isEqualTo(15);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(15);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        dataSource.close();
    }

    @Test
    public void testRunnable() throws IOException {
        FakeDataSourceTest.TestRunnable[] runnables = new FakeDataSourceTest.TestRunnable[3];
        for (int i = 0; i < 3; i++) {
            runnables[i] = new FakeDataSourceTest.TestRunnable();
        }
        FakeDataSource dataSource = new FakeDataSource(new FakeDataSet().newData(FakeDataSourceTest.uri.toString()).appendReadData(FakeDataSourceTest.TEST_DATA).appendReadAction(runnables[0]).appendReadData(FakeDataSourceTest.TEST_DATA).appendReadAction(runnables[1]).appendReadAction(runnables[2]).appendReadData(FakeDataSourceTest.TEST_DATA).endData());
        assertThat(dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri))).isEqualTo(45);
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(15);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        for (int i = 0; i < 3; i++) {
            assertThat(runnables[i].ran).isFalse();
        }
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(15);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        assertThat(runnables[0].ran).isTrue();
        assertThat(runnables[1].ran).isFalse();
        assertThat(runnables[2].ran).isFalse();
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(15);
        FakeDataSourceTest.assertBuffer(FakeDataSourceTest.TEST_DATA);
        for (int i = 0; i < 3; i++) {
            assertThat(runnables[i].ran).isTrue();
        }
        assertThat(dataSource.read(FakeDataSourceTest.BUFFER, 0, FakeDataSourceTest.BUFFER.length)).isEqualTo(RESULT_END_OF_INPUT);
        dataSource.close();
    }

    @Test
    public void testOpenSourceFailures() throws IOException {
        // Empty data.
        FakeDataSource dataSource = new FakeDataSource(new FakeDataSet().newData(FakeDataSourceTest.uri.toString()).endData());
        try {
            dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri));
            Assert.fail("IOException expected.");
        } catch (IOException e) {
            // Expected.
        } finally {
            dataSource.close();
        }
        // Non-existent data
        dataSource = new FakeDataSource(new FakeDataSet());
        try {
            dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri));
            Assert.fail("IOException expected.");
        } catch (IOException e) {
            // Expected.
        } finally {
            dataSource.close();
        }
        // DataSpec out of bounds.
        dataSource = new FakeDataSource(new FakeDataSet().newDefaultData().appendReadData(TestUtil.buildTestData(10)).endData());
        try {
            dataSource.open(new com.google.android.exoplayer2.upstream.DataSpec(FakeDataSourceTest.uri, 5, 10, null));
            Assert.fail("IOException expected.");
        } catch (IOException e) {
            // Expected.
        } finally {
            dataSource.close();
        }
    }

    private static final class TestRunnable implements Runnable {
        public boolean ran;

        @Override
        public void run() {
            ran = true;
        }
    }
}

