package org.robolectric.plugins;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.robolectric.pluginapi.Sdk;
import org.robolectric.pluginapi.SdkProvider;


/**
 * Test for {@link SdkCollection}.
 */
@RunWith(JUnit4.class)
public class SdkCollectionTest {
    private SdkProvider mockSdkProvider;

    private SdkCollection sdkCollection;

    private Sdk fakeSdk1234;

    private Sdk fakeSdk1235;

    private Sdk fakeSdk1236;

    private Sdk fakeUnsupportedSdk1237;

    @Test
    public void shouldComplainAboutDupes() throws Exception {
        try {
            new SdkCollection(() -> Arrays.asList(fakeSdk1234, fakeSdk1234));
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).hasMessageThat().contains("duplicate SDKs for API level 1234");
        }
    }

    @Test
    public void shouldCacheSdks() throws Exception {
        assertThat(sdkCollection.getSdk(1234)).isSameAs(fakeSdk1234);
        assertThat(sdkCollection.getSdk(1234)).isSameAs(fakeSdk1234);
        Mockito.verify(mockSdkProvider, Mockito.times(1)).getSdks();
    }

    @Test
    public void getMaxSupportedSdk() throws Exception {
        assertThat(sdkCollection.getMaxSupportedSdk()).isSameAs(fakeSdk1236);
    }

    @Test
    public void getSdk_shouldReturnNullObjectForUnknownSdks() throws Exception {
        assertThat(sdkCollection.getSdk(4321)).isNotNull();
        assertThat(sdkCollection.getSdk(4321).isKnown()).isFalse();
    }

    @Test
    public void getKnownSdks_shouldReturnAll() throws Exception {
        assertThat(sdkCollection.getKnownSdks()).containsExactly(fakeSdk1234, fakeSdk1235, fakeSdk1236, fakeUnsupportedSdk1237).inOrder();
    }

    @Test
    public void getSupportedSdks_shouldReturnOnlySupported() throws Exception {
        assertThat(sdkCollection.getSupportedSdks()).containsExactly(fakeSdk1234, fakeSdk1235, fakeSdk1236).inOrder();
    }
}

