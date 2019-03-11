package org.robolectric.shadows;


import Context.INPUT_METHOD_SERVICE;
import android.os.ServiceManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for {@link ShadowServiceManager}.
 */
@RunWith(AndroidJUnit4.class)
public final class ShadowServiceManagerTest {
    @Test
    public void getService_available_shouldReturnNonNull() {
        assertThat(ServiceManager.getService(INPUT_METHOD_SERVICE)).isNotNull();
    }

    @Test
    public void getService_unavailableService_shouldReturnNull() {
        ShadowServiceManager.setServiceAvailability(INPUT_METHOD_SERVICE, false);
        assertThat(ServiceManager.getService(INPUT_METHOD_SERVICE)).isNull();
    }
}

