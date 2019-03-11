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


import KeyedAppState.SEVERITY_INFO;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Tests {@link KeyedAppStatesService}.
 */
@RunWith(RobolectricTestRunner.class)
@DoNotInstrument
@Config(minSdk = 21)
public class KeyedAppStatesServiceTest {
    private static class TestKeyedAppStatesService extends KeyedAppStatesService {
        Collection<ReceivedKeyedAppState> mStates;

        boolean mRequestSync;

        @Override
        public void onReceive(Collection<ReceivedKeyedAppState> states, boolean requestSync) {
            this.mStates = Collections.unmodifiableCollection(states);
            this.mRequestSync = requestSync;
        }
    }

    private static final KeyedAppState STATE = KeyedAppState.builder().setKey("key1").setMessage("message1").setSeverity(SEVERITY_INFO).setData("data1").build();

    private static final KeyedAppState STATE2 = KeyedAppState.builder().setKey("key2").setMessage("message2").setSeverity(SEVERITY_INFO).setData("data2").build();

    private final KeyedAppStatesServiceTest.TestKeyedAppStatesService mKeyedAppStatesService = Robolectric.setupService(KeyedAppStatesServiceTest.TestKeyedAppStatesService.class);

    private final IBinder mBinder = mKeyedAppStatesService.onBind(new Intent());

    private final Messenger mMessenger = new Messenger(mBinder);

    private final ContextWrapper mContext = ApplicationProvider.getApplicationContext();

    private final PackageManager mPackageManager = mContext.getPackageManager();

    private static final int DEFAULT_SENDING_UID = -1;

    private static final long CURRENT_TIME_MILLIS = 1234567;

    @Test
    @SmallTest
    public void receivesStates() throws RemoteException {
        Collection<KeyedAppState> keyedAppStates = Arrays.asList(KeyedAppStatesServiceTest.STATE, KeyedAppStatesServiceTest.STATE2);
        Bundle appStatesBundle = KeyedAppStatesServiceTest.buildStatesBundle(keyedAppStates);
        Message message = KeyedAppStatesServiceTest.createStateMessage(appStatesBundle);
        mMessenger.send(message);
        assertReceivedStatesMatch(mKeyedAppStatesService.mStates, Arrays.asList(KeyedAppStatesServiceTest.STATE, KeyedAppStatesServiceTest.STATE2));
    }

    @Test
    @SmallTest
    public void receivesTimestamp() throws RemoteException {
        SystemClock.setCurrentTimeMillis(KeyedAppStatesServiceTest.CURRENT_TIME_MILLIS);
        mMessenger.send(KeyedAppStatesServiceTest.createTestStateMessage());
        ReceivedKeyedAppState receivedState = mKeyedAppStatesService.mStates.iterator().next();
        long timestamp = receivedState.timestamp();
        assertThat(timestamp).isEqualTo(KeyedAppStatesServiceTest.CURRENT_TIME_MILLIS);
    }

    @Test
    @SmallTest
    public void receivesPackageName() throws RemoteException {
        final String packageName = "test.package.name";
        shadowOf(mPackageManager).setNameForUid(KeyedAppStatesServiceTest.DEFAULT_SENDING_UID, packageName);
        mMessenger.send(KeyedAppStatesServiceTest.createTestStateMessage());
        ReceivedKeyedAppState receivedState = mKeyedAppStatesService.mStates.iterator().next();
        assertThat(receivedState.packageName()).isEqualTo(packageName);
    }

    @Test
    @SmallTest
    public void receivesDoesNotRequestSync() throws RemoteException {
        mMessenger.send(KeyedAppStatesServiceTest.createTestStateMessage());
        assertThat(mKeyedAppStatesService.mRequestSync).isFalse();
    }

    @Test
    @SmallTest
    public void receivesRequestSync() throws RemoteException {
        Message message = KeyedAppStatesServiceTest.createStateMessageImmediate(KeyedAppStatesServiceTest.buildStatesBundle(Collections.singleton(KeyedAppStatesServiceTest.STATE)));
        mMessenger.send(message);
        assertThat(mKeyedAppStatesService.mRequestSync).isTrue();
    }

    @Test
    @SmallTest
    public void deduplicatesStates() throws RemoteException {
        // Arrange
        Collection<KeyedAppState> keyedAppStates = Arrays.asList(KeyedAppState.builder().setKey("key").setSeverity(SEVERITY_INFO).build(), KeyedAppState.builder().setKey("key").setSeverity(SEVERITY_INFO).build(), KeyedAppState.builder().setKey("key").setSeverity(SEVERITY_INFO).setMessage("message").build());
        Bundle appStatesBundle = KeyedAppStatesServiceTest.buildStatesBundle(keyedAppStates);
        Message message = KeyedAppStatesServiceTest.createStateMessage(appStatesBundle);
        // Act
        mMessenger.send(message);
        // Assert
        assertThat(mKeyedAppStatesService.mStates).hasSize(1);
    }

    @Test
    @SmallTest
    public void send_emptyStates_doesNotCallback() throws RemoteException {
        Bundle appStatesBundle = KeyedAppStatesServiceTest.buildStatesBundle(Collections.<KeyedAppState>emptyList());
        Message message = KeyedAppStatesServiceTest.createStateMessage(appStatesBundle);
        mMessenger.send(message);
        assertThat(mKeyedAppStatesService.mStates).isNull();
    }

    @Test
    @SmallTest
    public void send_messageWithoutWhat_doesNotCallback() throws RemoteException {
        Message message = Message.obtain();
        mMessenger.send(message);
        assertThat(mKeyedAppStatesService.mStates).isNull();
    }

    @Test
    @SmallTest
    public void send_messageWithoutBundle_doesNotCallback() throws RemoteException {
        mMessenger.send(KeyedAppStatesServiceTest.createStateMessage(null));
        assertThat(mKeyedAppStatesService.mStates).isNull();
    }

    @Test
    @SmallTest
    public void send_messageWithIncorrectObj_doesNotCallback() throws RemoteException {
        Message message = KeyedAppStatesServiceTest.createStateMessage(null);
        message.obj = "";
        mMessenger.send(message);
        assertThat(mKeyedAppStatesService.mStates).isNull();
    }

    @Test
    @SmallTest
    public void send_messageWithEmptyBundle_doesNotCallback() throws RemoteException {
        mMessenger.send(KeyedAppStatesServiceTest.createStateMessage(new Bundle()));
        assertThat(mKeyedAppStatesService.mStates).isNull();
    }

    @Test
    @SmallTest
    public void send_messsageWithInvalidState_doesNotCallback() throws RemoteException {
        Bundle invalidStateBundle = KeyedAppStatesServiceTest.createDefaultStateBundle();
        invalidStateBundle.remove(KeyedAppStatesReporter.APP_STATE_KEY);
        Bundle bundle = KeyedAppStatesServiceTest.buildStatesBundleFromBundles(Collections.singleton(invalidStateBundle));
        Message message = KeyedAppStatesServiceTest.createStateMessage(bundle);
        mMessenger.send(message);
        assertThat(mKeyedAppStatesService.mStates).isNull();
    }

    @Test
    @SmallTest
    public void send_messageWithBothInvalidAndValidStates_callsBackWithOnlyValidStates() throws RemoteException {
        Bundle invalidStateBundle = KeyedAppStatesServiceTest.createDefaultStateBundle();
        invalidStateBundle.remove(KeyedAppStatesReporter.APP_STATE_KEY);
        Bundle bundle = KeyedAppStatesServiceTest.buildStatesBundleFromBundles(Arrays.asList(KeyedAppStatesServiceTest.createDefaultStateBundle(), invalidStateBundle));
        Message message = KeyedAppStatesServiceTest.createStateMessage(bundle);
        mMessenger.send(message);
        assertThat(mKeyedAppStatesService.mStates).hasSize(1);
    }
}

