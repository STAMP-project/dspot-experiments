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
import androidx.enterprise.feedback.ReceivedKeyedAppState.ReceivedKeyedAppStateBuilder;
import androidx.test.filters.SmallTest;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Tests {@link ReceivedKeyedAppState}.
 */
@RunWith(RobolectricTestRunner.class)
@DoNotInstrument
@Config(minSdk = 21)
public class ReceivedKeyedAppStateTest {
    private static final String KEY = "key";

    private static final String MESSAGE = "message";

    private static final int SEVERITY = KeyedAppState.SEVERITY_INFO;

    private static final String DATA = "data";

    private static final String PACKAGE_NAME = "com.package";

    private static final long TIMESTAMP = 12345;

    @Test
    @SmallTest
    public void fromBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_KEY, ReceivedKeyedAppStateTest.KEY);
        bundle.putString(KeyedAppStatesReporter.APP_STATE_MESSAGE, ReceivedKeyedAppStateTest.MESSAGE);
        bundle.putInt(KeyedAppStatesReporter.APP_STATE_SEVERITY, ReceivedKeyedAppStateTest.SEVERITY);
        bundle.putString(KeyedAppStatesReporter.APP_STATE_DATA, ReceivedKeyedAppStateTest.DATA);
        ReceivedKeyedAppState state = ReceivedKeyedAppState.fromBundle(bundle, ReceivedKeyedAppStateTest.PACKAGE_NAME, ReceivedKeyedAppStateTest.TIMESTAMP);
        assertThat(state.key()).isEqualTo(ReceivedKeyedAppStateTest.KEY);
        assertThat(state.message()).isEqualTo(ReceivedKeyedAppStateTest.MESSAGE);
        assertThat(state.severity()).isEqualTo(ReceivedKeyedAppStateTest.SEVERITY);
        assertThat(state.data()).isEqualTo(ReceivedKeyedAppStateTest.DATA);
        assertThat(state.packageName()).isEqualTo(ReceivedKeyedAppStateTest.PACKAGE_NAME);
        assertThat(state.timestamp()).isEqualTo(ReceivedKeyedAppStateTest.TIMESTAMP);
    }

    @Test
    @SmallTest
    public void fromBundle_invalidBundle_throwsIllegalArgumentException() {
        Bundle bundle = new Bundle();
        bundle.putString(KeyedAppStatesReporter.APP_STATE_KEY, ReceivedKeyedAppStateTest.KEY);
        bundle.putString(KeyedAppStatesReporter.APP_STATE_MESSAGE, ReceivedKeyedAppStateTest.MESSAGE);
        bundle.putString(KeyedAppStatesReporter.APP_STATE_DATA, ReceivedKeyedAppStateTest.DATA);
        try {
            ReceivedKeyedAppState.fromBundle(bundle, ReceivedKeyedAppStateTest.PACKAGE_NAME, ReceivedKeyedAppStateTest.TIMESTAMP);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    @SmallTest
    public void keyIsRequired() {
        ReceivedKeyedAppStateBuilder builder = ReceivedKeyedAppState.builder().setPackageName(ReceivedKeyedAppStateTest.PACKAGE_NAME).setTimestamp(ReceivedKeyedAppStateTest.TIMESTAMP).setSeverity(KeyedAppState.SEVERITY_INFO).setMessage(ReceivedKeyedAppStateTest.MESSAGE).setData(ReceivedKeyedAppStateTest.DATA);
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void severityIsRequired() {
        ReceivedKeyedAppStateBuilder builder = ReceivedKeyedAppState.builder().setPackageName(ReceivedKeyedAppStateTest.PACKAGE_NAME).setTimestamp(ReceivedKeyedAppStateTest.TIMESTAMP).setKey(ReceivedKeyedAppStateTest.KEY).setMessage(ReceivedKeyedAppStateTest.MESSAGE).setData(ReceivedKeyedAppStateTest.DATA);
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void packageNameIsRequired() {
        ReceivedKeyedAppStateBuilder builder = ReceivedKeyedAppState.builder().setTimestamp(ReceivedKeyedAppStateTest.TIMESTAMP).setKey(ReceivedKeyedAppStateTest.KEY).setSeverity(ReceivedKeyedAppStateTest.SEVERITY).setMessage(ReceivedKeyedAppStateTest.MESSAGE).setData(ReceivedKeyedAppStateTest.DATA);
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void timestampIsRequired() {
        ReceivedKeyedAppStateBuilder builder = ReceivedKeyedAppState.builder().setPackageName(ReceivedKeyedAppStateTest.PACKAGE_NAME).setKey(ReceivedKeyedAppStateTest.KEY).setSeverity(ReceivedKeyedAppStateTest.SEVERITY).setMessage(ReceivedKeyedAppStateTest.MESSAGE).setData(ReceivedKeyedAppStateTest.DATA);
        try {
            builder.build();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }
}

