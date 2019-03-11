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
import KeyedAppState.SEVERITY_INFO;
import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import java.util.Collections;
import java.util.concurrent.Executor;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Tests {@link KeyedAppStatesReporter}.
 */
@RunWith(RobolectricTestRunner.class)
@DoNotInstrument
@Config(minSdk = 21)
public class KeyedAppStatesReporterTest {
    private final ComponentName mTestComponentName = new ComponentName("test_package", "");

    private final Executor mExecutor = new TestExecutor();

    private final ContextWrapper mContext = ApplicationProvider.getApplicationContext();

    private final DevicePolicyManager mDevicePolicyManager = ((DevicePolicyManager) (mContext.getSystemService(DEVICE_POLICY_SERVICE)));

    private final PackageManager mPackageManager = mContext.getPackageManager();

    private final Application mApplication = ((Application) (mContext.getApplicationContext()));

    private final TestHandler mTestHandler = new TestHandler();

    private final KeyedAppState mState = KeyedAppState.builder().setKey("key").setSeverity(SEVERITY_INFO).build();

    @Test
    @SmallTest
    public void getInstance_nullContext_throwsNullPointerException() {
        KeyedAppStatesReporter.resetSingleton();
        try {
            KeyedAppStatesReporter.getInstance(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    @SmallTest
    public void initialize_usesExecutor() {
        KeyedAppStatesReporter.resetSingleton();
        TestExecutor testExecutor = new TestExecutor();
        KeyedAppStatesReporter.initialize(mContext, testExecutor);
        KeyedAppStatesReporter.getInstance(mContext).set(Collections.singleton(mState));
        assertThat(testExecutor.lastExecuted()).isNotNull();
    }

    @Test
    @SmallTest
    public void initialize_calledMultipleTimes_throwsIllegalStateException() {
        KeyedAppStatesReporter.resetSingleton();
        KeyedAppStatesReporter.initialize(mContext, mExecutor);
        try {
            KeyedAppStatesReporter.initialize(mContext, mExecutor);
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void initialize_calledAfterGetInstance_throwsIllegalStateException() {
        KeyedAppStatesReporter.resetSingleton();
        KeyedAppStatesReporter.getInstance(mContext);
        try {
            KeyedAppStatesReporter.initialize(mContext, mExecutor);
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    @SmallTest
    public void setIncludesAppStateBundle() {
        setTestHandlerReceivesStates();
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        Bundle appStatesBundle = KeyedAppStatesReporterTest.buildStatesBundle(Collections.singleton(mState));
        KeyedAppStatesReporterTest.assertAppStateBundlesEqual(appStatesBundle, ((Bundle) (mTestHandler.latestMessage().obj)));
    }

    @Test
    @SmallTest
    public void setEmpty_doesNotSend() {
        setTestHandlerReceivesStates();
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.<KeyedAppState>emptyList());
        assertThat(mTestHandler.latestMessage()).isNull();
    }

    @Test
    @SmallTest
    public void setNotImmediate() {
        setTestHandlerReceivesStates();
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        assertThat(mTestHandler.latestMessage().what).isEqualTo(KeyedAppStatesReporter.WHAT_STATE);
    }

    @Test
    @SmallTest
    public void setImmediate() {
        setTestHandlerReceivesStates();
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.setImmediate(Collections.singletonList(mState));
        assertThat(mTestHandler.latestMessage().what).isEqualTo(KeyedAppStatesReporter.WHAT_IMMEDIATE_STATE);
    }

    @Test
    @SmallTest
    public void set_doesNotGoToNormalApps() {
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        assertThat(mTestHandler.latestMessage()).isNull();
    }

    @Test
    @SmallTest
    public void set_goesToDeviceOwner() {
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setDeviceOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        assertThat(mTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    public void set_goesToProfileOwner() {
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        assertThat(mTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    public void set_goesToPhonesky() {
        ComponentName phoneskyComponentName = new ComponentName(KeyedAppStatesReporter.PHONESKY_PACKAGE_NAME, "");
        addComponentAsRespondingToAppStatesIntent(phoneskyComponentName);
        setComponentBindingToHandler(phoneskyComponentName, mTestHandler);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        assertThat(mTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    public void set_goesToMultiple() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        ComponentName phoneskyComponentName = new ComponentName(KeyedAppStatesReporter.PHONESKY_PACKAGE_NAME, "");
        TestHandler phoneskyTestHandler = new TestHandler();
        addComponentAsRespondingToAppStatesIntent(phoneskyComponentName);
        setComponentBindingToHandler(phoneskyComponentName, phoneskyTestHandler);
        // Act
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        // Assert
        assertThat(mTestHandler.latestMessage()).isNotNull();
        assertThat(phoneskyTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    public void set_changeProfileOwner_goesToNewProfileOwner() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        mTestHandler.reset();
        ComponentName newComponentName = new ComponentName("second_test_package", "");
        TestHandler newTestHandler = new TestHandler();
        addComponentAsRespondingToAppStatesIntent(newComponentName);
        setComponentBindingToHandler(newComponentName, newTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(newComponentName);
        // Act
        reporter.set(Collections.singletonList(mState));
        // Assert
        assertThat(mTestHandler.latestMessage()).isNull();
        assertThat(newTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    public void set_changeDeviceOwner_goesToNewDeviceOwner() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setDeviceOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        reporter.set(Collections.singletonList(mState));
        mTestHandler.reset();
        ComponentName newComponentName = new ComponentName("second_test_package", "");
        TestHandler newTestHandler = new TestHandler();
        addComponentAsRespondingToAppStatesIntent(newComponentName);
        setComponentBindingToHandler(newComponentName, newTestHandler);
        shadowOf(mDevicePolicyManager).setDeviceOwner(newComponentName);
        // Act
        reporter.set(Collections.singletonList(mState));
        // Assert
        assertThat(mTestHandler.latestMessage()).isNull();
        assertThat(newTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    @Config(minSdk = 26)
    public void set_deadConnection_reconnectsAndSendsToNewApp() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        // Set the binding to a different handler - as if the app has restarted.
        TestHandler newAppTestHandler = new TestHandler();
        setComponentBindingToHandler(mTestComponentName, newAppTestHandler);
        simulateDeadServiceConnection();
        // Act
        reporter.set(Collections.singletonList(mState));
        // Assert
        assertThat(mTestHandler.latestMessage()).isNull();
        assertThat(newAppTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    @Config(maxSdk = 25)
    public void set_connectionHasDisconnected_sdkLessThan26_reconnectsAndSendsToNewApp() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        // Set the binding to a different handler - as if the app has restarted.
        TestHandler newAppTestHandler = new TestHandler();
        setComponentBindingToHandler(mTestComponentName, newAppTestHandler);
        simulateDisconnectingServiceConnection();
        // Act
        reporter.set(Collections.singletonList(mState));
        // Assert
        assertThat(mTestHandler.latestMessage()).isNull();
        assertThat(newAppTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    @Config(minSdk = 26)
    public void set_connectionHasDisconnected_doesNotSend() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        simulateDisconnectingServiceConnection();
        // Act
        reporter.set(Collections.singletonList(mState));
        // Assert
        assertThat(mTestHandler.latestMessage()).isNull();
    }

    @Test
    @SmallTest
    @Config(minSdk = 26)
    public void set_sendsWhenReconnected() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        simulateDisconnectingServiceConnection();
        reporter.set(Collections.singletonList(mState));
        // Act
        simulateReconnectingServiceConnection();
        // Assert
        assertThat(mTestHandler.latestMessage()).isNotNull();
    }

    @Test
    @SmallTest
    public void set_connectionHasReconnected_doesSend() {
        // Arrange
        addComponentAsRespondingToAppStatesIntent(mTestComponentName);
        setComponentBindingToHandler(mTestComponentName, mTestHandler);
        shadowOf(mDevicePolicyManager).setProfileOwner(mTestComponentName);
        KeyedAppStatesReporter reporter = getReporter(mContext);
        // Change the component binding to ensure that it doesn't reconnect
        setComponentBindingToHandler(mTestComponentName, new TestHandler());
        simulateDisconnectingServiceConnection();
        simulateReconnectingServiceConnection();
        // Act
        reporter.set(Collections.singletonList(mState));
        // Assert
        assertThat(mTestHandler.latestMessage()).isNotNull();
    }
}

