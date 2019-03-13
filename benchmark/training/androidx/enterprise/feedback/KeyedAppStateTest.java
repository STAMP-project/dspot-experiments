/**
 * Copyright 2019 The Android Open Source Project
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
package androidx.enterprise.feedback;


import android.os.Bundle;
import androidx.enterprise.feedback.KeyedAppState.KeyedAppStateBuilder;
import androidx.test.filters.SmallTest;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Tests {@link KeyedAppState}.
 */
@RunWith(RobolectricTestRunner.class)
@DoNotInstrument
@Config(minSdk = 21)
public class KeyedAppStateTest {
    private static final String KEY = "key";

    private static final String MESSAGE = "message";

    private static final int SEVERITY = KeyedAppState.SEVERITY_INFO;

    private static final String DATA = "data";

    private static final int INVALID_SEVERITY = 100;

    @Test
    @SmallTest
    public void toStateBundle() {
        KeyedAppState keyedAppState = KeyedAppState.builder().setKey(KeyedAppStateTest.KEY).setMessage(KeyedAppStateTest.MESSAGE).setSeverity(KeyedAppStateTest.SEVERITY).setData(KeyedAppStateTest.DATA).build();
        Bundle bundle = keyedAppState.toStateBundle();
        assertThat(bundle.getString(KeyedAppStatesReporter.APP_STATE_KEY)).isEqualTo(KeyedAppStateTest.KEY);
        assertThat(bundle.getString(KeyedAppStatesReporter.APP_STATE_MESSAGE)).isEqualTo(KeyedAppStateTest.MESSAGE);
        assertThat(bundle.getInt(KeyedAppStatesReporter.APP_STATE_SEVERITY)).isEqualTo(KeyedAppStateTest.SEVERITY);
        assertThat(bundle.getString(KeyedAppStatesReporter.APP_STATE_DATA)).isEqualTo(KeyedAppStateTest.DATA);
    }

    @Test
    @SmallTest
    public void isValid() {
        Bundle bundle = new Bundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_KEY, KeyedAppStateTest.KEY);
        bundle.putInt(KeyedAppStatesReporter.APP_STATE_SEVERITY, KeyedAppStateTest.SEVERITY);
        assertThat(KeyedAppState.isValid(bundle)).isTrue();
    }

    @Test
    @SmallTest
    public void isValid_missingKey_isFalse() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.remove(KeyedAppStatesReporter.APP_STATE_KEY);
        assertThat(KeyedAppState.isValid(bundle)).isFalse();
    }

    @Test
    @SmallTest
    public void isValid_missingSeverity_isFalse() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.remove(KeyedAppStatesReporter.APP_STATE_SEVERITY);
        assertThat(KeyedAppState.isValid(bundle)).isFalse();
    }

    @Test
    @SmallTest
    public void isValid_invalidSeverity_isFalse() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.putInt(KeyedAppStatesReporter.APP_STATE_SEVERITY, KeyedAppStateTest.INVALID_SEVERITY);
        assertThat(KeyedAppState.isValid(bundle)).isFalse();
    }

    @Test
    @SmallTest
    public void isValid_maxKeyLength_isTrue() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_KEY, KeyedAppStateTest.buildStringOfLength(KeyedAppState.MAX_KEY_LENGTH));
        assertThat(KeyedAppState.isValid(bundle)).isTrue();
    }

    @Test
    @SmallTest
    public void isValid_tooHighKeyLength_isFalse() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_KEY, KeyedAppStateTest.buildStringOfLength(((KeyedAppState.MAX_KEY_LENGTH) + 1)));
        assertThat(KeyedAppState.isValid(bundle)).isFalse();
    }

    @Test
    @SmallTest
    public void isValid_maxMessageLength_isTrue() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_MESSAGE, KeyedAppStateTest.buildStringOfLength(KeyedAppState.MAX_MESSAGE_LENGTH));
        assertThat(KeyedAppState.isValid(bundle)).isTrue();
    }

    @Test
    @SmallTest
    public void isValid_tooHighMessageLength_isFalse() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_MESSAGE, KeyedAppStateTest.buildStringOfLength(((KeyedAppState.MAX_MESSAGE_LENGTH) + 1)));
        assertThat(KeyedAppState.isValid(bundle)).isFalse();
    }

    @Test
    @SmallTest
    public void isValid_maxDataLength_isTrue() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_DATA, KeyedAppStateTest.buildStringOfLength(KeyedAppState.MAX_DATA_LENGTH));
        assertThat(KeyedAppState.isValid(bundle)).isTrue();
    }

    @Test
    @SmallTest
    public void isValid_tooHighDataLength_isFalse() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_DATA, KeyedAppStateTest.buildStringOfLength(((KeyedAppState.MAX_DATA_LENGTH) + 1)));
        assertThat(KeyedAppState.isValid(bundle)).isFalse();
    }

    @Test
    @SmallTest
    public void fromBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_KEY, KeyedAppStateTest.KEY);
        bundle.putString(KeyedAppStatesReporter.APP_STATE_MESSAGE, KeyedAppStateTest.MESSAGE);
        bundle.putInt(KeyedAppStatesReporter.APP_STATE_SEVERITY, KeyedAppStateTest.SEVERITY);
        bundle.putString(KeyedAppStatesReporter.APP_STATE_DATA, KeyedAppStateTest.DATA);
        KeyedAppState keyedAppState = KeyedAppState.fromBundle(bundle);
        assertThat(keyedAppState.key()).isEqualTo(KeyedAppStateTest.KEY);
        assertThat(keyedAppState.message()).isEqualTo(KeyedAppStateTest.MESSAGE);
        assertThat(keyedAppState.severity()).isEqualTo(KeyedAppStateTest.SEVERITY);
        assertThat(keyedAppState.data()).isEqualTo(KeyedAppStateTest.DATA);
    }

    @Test
    @SmallTest
    public void fromBundle_invalidBundle_throwsIllegalArgumentException() {
        Bundle bundle = KeyedAppStateTest.buildTestBundle();
        bundle.remove(KeyedAppStatesReporter.APP_STATE_SEVERITY);
        try {
            KeyedAppState.fromBundle(bundle);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    @SmallTest
    public void severityDefaultsToInfo() {
        KeyedAppState keyedAppState = KeyedAppState.builder().setKey(KeyedAppStateTest.KEY).build();
        assertThat(keyedAppState.severity()).isEqualTo(KeyedAppState.SEVERITY_INFO);
    }

    @Test
    @SmallTest
    public void messageDefaultsToNull() {
        KeyedAppState keyedAppState = KeyedAppState.builder().setKey(KeyedAppStateTest.KEY).build();
        assertThat(keyedAppState.message()).isNull();
    }

    @Test
    @SmallTest
    public void dataDefaultsToNull() {
        KeyedAppState keyedAppState = KeyedAppState.builder().setKey(KeyedAppStateTest.KEY).build();
        assertThat(keyedAppState.data()).isNull();
    }

    @Test
    @SmallTest
    public void buildWithMaxKeyLength_builds() {
        KeyedAppStateTest.createDefaultKeyedAppStateBuilder().setKey(KeyedAppStateTest.buildStringOfLength(KeyedAppState.MAX_KEY_LENGTH)).build();
    }

    @Test
    @SmallTest
    public void buildWithTooHighKeyLength_throwsIllegalStateException() {
        KeyedAppStateBuilder builder = KeyedAppStateTest.createDefaultKeyedAppStateBuilder().setKey(KeyedAppStateTest.buildStringOfLength(((KeyedAppState.MAX_KEY_LENGTH) + 1)));
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void buildWithMaxMessageLength_builds() {
        KeyedAppStateTest.createDefaultKeyedAppStateBuilder().setMessage(KeyedAppStateTest.buildStringOfLength(KeyedAppState.MAX_MESSAGE_LENGTH)).build();
    }

    @Test
    @SmallTest
    public void buildWithTooHighMessageLength_throwsIllegalStateException() {
        KeyedAppStateBuilder builder = KeyedAppStateTest.createDefaultKeyedAppStateBuilder().setMessage(KeyedAppStateTest.buildStringOfLength(((KeyedAppState.MAX_MESSAGE_LENGTH) + 1)));
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void buildWithMaxDataLength_builds() {
        KeyedAppStateTest.createDefaultKeyedAppStateBuilder().setData(KeyedAppStateTest.buildStringOfLength(KeyedAppState.MAX_DATA_LENGTH)).build();
    }

    @Test
    @SmallTest
    public void buildWithTooHighDataLength_throwsIllegalStateException() {
        KeyedAppStateBuilder builder = KeyedAppStateTest.createDefaultKeyedAppStateBuilder().setData(KeyedAppStateTest.buildStringOfLength(((KeyedAppState.MAX_DATA_LENGTH) + 1)));
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void keyIsRequired() {
        KeyedAppStateBuilder builder = KeyedAppState.builder().setSeverity(KeyedAppState.SEVERITY_INFO).setMessage(KeyedAppStateTest.MESSAGE).setData(KeyedAppStateTest.DATA);
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }
}

