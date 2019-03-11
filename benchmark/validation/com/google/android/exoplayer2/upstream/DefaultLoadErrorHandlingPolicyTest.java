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
package com.google.android.exoplayer2.upstream;


import C.TIME_UNSET;
import DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS;
import android.net.Uri;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import java.io.FileNotFoundException;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link DefaultLoadErrorHandlingPolicy}.
 */
@RunWith(RobolectricTestRunner.class)
public final class DefaultLoadErrorHandlingPolicyTest {
    @Test
    public void getBlacklistDurationMsFor_blacklist404() {
        InvalidResponseCodeException exception = new InvalidResponseCodeException(404, "Not Found", Collections.emptyMap(), new DataSpec(Uri.EMPTY));
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyBlacklistOutputFor(exception)).isEqualTo(DEFAULT_TRACK_BLACKLIST_MS);
    }

    @Test
    public void getBlacklistDurationMsFor_blacklist410() {
        InvalidResponseCodeException exception = new InvalidResponseCodeException(410, "Gone", Collections.emptyMap(), new DataSpec(Uri.EMPTY));
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyBlacklistOutputFor(exception)).isEqualTo(DEFAULT_TRACK_BLACKLIST_MS);
    }

    @Test
    public void getBlacklistDurationMsFor_dontBlacklistUnexpectedHttpCodes() {
        InvalidResponseCodeException exception = new InvalidResponseCodeException(500, "Internal Server Error", Collections.emptyMap(), new DataSpec(Uri.EMPTY));
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyBlacklistOutputFor(exception)).isEqualTo(TIME_UNSET);
    }

    @Test
    public void getBlacklistDurationMsFor_dontBlacklistUnexpectedExceptions() {
        FileNotFoundException exception = new FileNotFoundException();
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyBlacklistOutputFor(exception)).isEqualTo(TIME_UNSET);
    }

    @Test
    public void getRetryDelayMsFor_dontRetryParserException() {
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyRetryDelayOutputFor(new ParserException(), 1)).isEqualTo(TIME_UNSET);
    }

    @Test
    public void getRetryDelayMsFor_successiveRetryDelays() {
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyRetryDelayOutputFor(new FileNotFoundException(), 3)).isEqualTo(2000);
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyRetryDelayOutputFor(new FileNotFoundException(), 5)).isEqualTo(4000);
        assertThat(DefaultLoadErrorHandlingPolicyTest.getDefaultPolicyRetryDelayOutputFor(new FileNotFoundException(), 9)).isEqualTo(5000);
    }
}

