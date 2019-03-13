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


import Context.DEVICE_POLICY_SERVICE;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Message;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import java.util.concurrent.Executor;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowContextWrapper;


/**
 * Tests {@link BufferedServiceConnection}.
 */
@RunWith(RobolectricTestRunner.class)
@DoNotInstrument
@Config(minSdk = 21)
public class BufferedServiceConnectionTest {
    private final ContextWrapper mContext = ApplicationProvider.getApplicationContext();

    private final DevicePolicyManager mDevicePolicyManager = ((DevicePolicyManager) (mContext.getSystemService(DEVICE_POLICY_SERVICE)));

    private final Intent mBindIntent = new Intent();

    private final int mFlags = 0;

    private final Executor mExecutor = new TestExecutor();

    private final TestHandler mTestHandler = new TestHandler();

    private final BufferedServiceConnection mBufferedServiceConnection = new BufferedServiceConnection(mExecutor, mContext, mBindIntent, mFlags);

    private final ComponentName mTestComponentName = new ComponentName("test_package", "");

    private final ComponentName mNotPhoneskyComponentName = mTestComponentName;

    private final ComponentName mPhoneskyComponentName = new ComponentName("com.android.vending", "");

    @Test
    @SmallTest
    public void construct_nullExecutor_throwsNullPointerException() {
        try {
            new BufferedServiceConnection(null, mContext, mBindIntent, mFlags);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    @SmallTest
    public void construct_nullContext_throwsNullPointerException() {
        try {
            new BufferedServiceConnection(mExecutor, null, mBindIntent, mFlags);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    @SmallTest
    public void construct_nullBindIntent_throwsNullPointerException() {
        try {
            new BufferedServiceConnection(mExecutor, mContext, null, mFlags);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    @SmallTest
    public void bind_startsService() {
        ShadowContextWrapper shadowContextWrapper = Shadow.extract(mContext);
        mBufferedServiceConnection.bindService();
        Intent nextIntent = shadowContextWrapper.peekNextStartedService();
        assertThat(mBindIntent).isEqualTo(nextIntent);
    }

    @Test
    @SmallTest
    public void bind_bindingExists() {
        mBufferedServiceConnection.bindService();
        assertThat(getBoundServiceConnections()).isNotEmpty();
    }

    @Test
    @SmallTest
    public void bind_alreadyBound_throwsIllegalStateException() {
        mBufferedServiceConnection.bindService();
        try {
            mBufferedServiceConnection.bindService();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void unbind_bindingDoesNotExist() {
        mBufferedServiceConnection.bindService();
        mBufferedServiceConnection.unbind();
        assertThat(getBoundServiceConnections()).isEmpty();
    }

    @Test
    @SmallTest
    public void unbind_hasntBound_throwsIllegalStateException() {
        try {
            mBufferedServiceConnection.unbind();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void sendMessage_bound_sends() {
        mBufferedServiceConnection.bindService();
        mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        // The test message is rebuilt as it is cleared after being sent
        BufferedServiceConnectionTest.assertMessagesEqual(BufferedServiceConnectionTest.buildTestMessage(), mTestHandler.latestMessage());
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_doesNotSend() {
        Message message = BufferedServiceConnectionTest.buildTestMessage();
        mBufferedServiceConnection.send(message);
        assertThat(mTestHandler.latestMessage()).isNull();
    }

    @Test
    @SmallTest
    @Config(minSdk = 26)
    public void sendMessage_isDead_doesNotSend() {
        mBufferedServiceConnection.bindService();
        simulateDeadServiceConnection();
        mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        assertThat(mTestHandler.latestMessage()).isNull();
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_isNotDoPoOrPhonesky_doesNotSendWhenBound() {
        setComponentBindingToTestHandler(mNotPhoneskyComponentName);
        shadowOf(mDevicePolicyManager).setDeviceOwner(null);
        shadowOf(mDevicePolicyManager).setProfileOwner(null);
        mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        mBufferedServiceConnection.bindService();
        assertThat(mTestHandler.latestMessage()).isNull();
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_isNotDoPoOrPhonesky_isDeadWhenBound() {
        setComponentBindingToTestHandler(mNotPhoneskyComponentName);
        shadowOf(mDevicePolicyManager).setDeviceOwner(null);
        shadowOf(mDevicePolicyManager).setProfileOwner(null);
        mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        mBufferedServiceConnection.bindService();
        assertThat(mBufferedServiceConnection.isDead()).isTrue();
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_isDeviceOwner_sendsWhenBound() {
        shadowOf(mDevicePolicyManager).setDeviceOwner(mTestComponentName);
        mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        mBufferedServiceConnection.bindService();
        // The test message is rebuilt as it is cleared after being sent.
        BufferedServiceConnectionTest.assertMessagesEqual(BufferedServiceConnectionTest.buildTestMessage(), mTestHandler.latestMessage());
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_isProfileOwner_sendsWhenBound() {
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        mBufferedServiceConnection.bindService();
        // The test message is rebuilt as it is cleared after being sent.
        BufferedServiceConnectionTest.assertMessagesEqual(BufferedServiceConnectionTest.buildTestMessage(), mTestHandler.latestMessage());
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_isPhonesky_sendsWhenBound() {
        setComponentBindingToTestHandler(mPhoneskyComponentName);
        mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        mBufferedServiceConnection.bindService();
        // The test message is rebuilt as it is cleared after being sent.
        BufferedServiceConnectionTest.assertMessagesEqual(BufferedServiceConnectionTest.buildTestMessage(), mTestHandler.latestMessage());
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_sendToBufferLimit_sendsAll() {
        for (int i = 0; i < (BufferedServiceConnection.MAX_BUFFER_SIZE); i++) {
            mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        }
        mBufferedServiceConnection.bindService();
        assertThat(mTestHandler.messageCount()).isEqualTo(BufferedServiceConnection.MAX_BUFFER_SIZE);
    }

    @Test
    @SmallTest
    public void sendMessage_notBound_sendBeyondBufferLimit_sendsToBufferLimit() {
        for (int i = 0; i < ((BufferedServiceConnection.MAX_BUFFER_SIZE) + 1); i++) {
            mBufferedServiceConnection.send(BufferedServiceConnectionTest.buildTestMessage());
        }
        mBufferedServiceConnection.bindService();
        assertThat(mTestHandler.messageCount()).isEqualTo(BufferedServiceConnection.MAX_BUFFER_SIZE);
    }

    @Test
    @SmallTest
    public void isDead_isFalse() {
        mBufferedServiceConnection.bindService();
        assertThat(mBufferedServiceConnection.isDead()).isFalse();
    }

    @Test
    @SmallTest
    @Config(minSdk = 26)
    public void isDead_serviceHasDied_isTrue() {
        mBufferedServiceConnection.bindService();
        simulateDeadServiceConnection();
        assertThat(mBufferedServiceConnection.isDead()).isTrue();
    }

    @Test
    @SmallTest
    public void isDead_hasUnbound_isTrue() {
        mBufferedServiceConnection.bindService();
        mBufferedServiceConnection.unbind();
        assertThat(mBufferedServiceConnection.isDead()).isTrue();
    }

    @Test
    @SmallTest
    public void hasBeenDisconnected_defaultsToFalse() {
        mBufferedServiceConnection.bindService();
        assertThat(mBufferedServiceConnection.hasBeenDisconnected()).isFalse();
    }

    @Test
    @SmallTest
    public void hasBeenDisconnected_disconnected_isTrue() {
        mBufferedServiceConnection.bindService();
        simulateDisconnectingServiceConnection();
        assertThat(mBufferedServiceConnection.hasBeenDisconnected()).isTrue();
    }

    @Test
    @SmallTest
    public void hasBeenDisconnected_reconnected_isFalse() {
        mBufferedServiceConnection.bindService();
        simulateDisconnectingServiceConnection();
        simulateReconnectingServiceConnection();
        assertThat(mBufferedServiceConnection.hasBeenDisconnected()).isFalse();
    }
}

