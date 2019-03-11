/**
 * Copyright 2019. Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.apps.santatracker.tracker.api;


import android.content.SharedPreferences;
import com.google.android.apps.santatracker.tracker.BuildConfig;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Test for {@link FirebaseStorageFetcher}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 26, constants = BuildConfig.class)
public class FirebaseStorageFetcherTest {
    private static final String FILE_NAME = "routes/test-file.json";

    private static final String FILE_CONTENTS = "HELLO WORLD";

    private static long TASK_TIMEOUT_MS = 30 * 1000;

    private FirebaseStorage mStorage;

    private SharedPreferences mPreferences;

    private FirebaseStorageFetcher mFetcher;

    @Test
    public void testFreshDownload() throws Exception {
        StorageReference reference = getMockStorageReference(FirebaseStorageFetcherTest.FILE_NAME);
        // Mock updated time to return the current time (so cache is never hit)
        long currentTime = System.currentTimeMillis();
        stubFirebaseStorageUpdatedTime(reference, currentTime);
        // Mock file contents
        stubFirebaseStorageContents(reference, FirebaseStorageFetcherTest.FILE_CONTENTS);
        // Load the test file
        Task<String> loadTask = waitForTask(mFetcher.get(FirebaseStorageFetcherTest.FILE_NAME, 0, TimeUnit.SECONDS));
        // Sanity check for proper results
        Assert.assertEquals(loadTask.getResult(), FirebaseStorageFetcherTest.FILE_CONTENTS);
    }
}

