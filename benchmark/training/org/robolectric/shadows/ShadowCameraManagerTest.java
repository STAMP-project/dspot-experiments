package org.robolectric.shadows;


import Context.CAMERA_SERVICE;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link ShadowCameraManager}.
 */
@Config(minSdk = VERSION_CODES.LOLLIPOP)
@RunWith(AndroidJUnit4.class)
public class ShadowCameraManagerTest {
    private static final String CAMERA_ID_0 = "cameraId0";

    private static final String CAMERA_ID_1 = "cameraId1";

    private static final boolean ENABLE = true;

    private final CameraManager cameraManager = ((CameraManager) (ApplicationProvider.getApplicationContext().getSystemService(CAMERA_SERVICE)));

    private final CameraCharacteristics characteristics = ShadowCameraCharacteristics.newCameraCharacteristics();

    @Test
    public void testAddCameraNullCameraId() {
        try {
            Shadows.shadowOf(cameraManager).addCamera(null, characteristics);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testAddCameraNullCharacteristics() {
        try {
            Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testAddCameraExistingId() {
        Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, characteristics);
        try {
            Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, characteristics);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testGetCameraIdListNoCameras() throws CameraAccessException {
        assertThat(cameraManager.getCameraIdList()).isEmpty();
    }

    @Test
    public void testGetCameraIdListSingleCamera() throws CameraAccessException {
        Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, characteristics);
        assertThat(cameraManager.getCameraIdList()).asList().containsExactly(ShadowCameraManagerTest.CAMERA_ID_0);
    }

    @Test
    public void testGetCameraIdListInOrderOfAdd() throws CameraAccessException {
        Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, characteristics);
        Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_1, characteristics);
        assertThat(cameraManager.getCameraIdList()[0]).isEqualTo(ShadowCameraManagerTest.CAMERA_ID_0);
        assertThat(cameraManager.getCameraIdList()[1]).isEqualTo(ShadowCameraManagerTest.CAMERA_ID_1);
    }

    @Test
    public void testGetCameraCharacteristicsNullCameraId() throws CameraAccessException {
        try {
            cameraManager.getCameraCharacteristics(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testGetCameraCharacteristicsUnrecognizedCameraId() throws CameraAccessException {
        try {
            cameraManager.getCameraCharacteristics(ShadowCameraManagerTest.CAMERA_ID_0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testGetCameraCharacteristicsRecognizedCameraId() throws CameraAccessException {
        Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, characteristics);
        assertThat(cameraManager.getCameraCharacteristics(ShadowCameraManagerTest.CAMERA_ID_0)).isSameAs(characteristics);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testSetTorchModeInvalidCameraId() throws CameraAccessException {
        try {
            cameraManager.setTorchMode(ShadowCameraManagerTest.CAMERA_ID_0, ShadowCameraManagerTest.ENABLE);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testGetTorchModeNullCameraId() {
        try {
            Shadows.shadowOf(cameraManager).getTorchMode(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testGetTorchModeInvalidCameraId() {
        try {
            Shadows.shadowOf(cameraManager).getTorchMode(ShadowCameraManagerTest.CAMERA_ID_0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testGetTorchModeCameraTorchModeNotSet() throws CameraAccessException {
        try {
            Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, characteristics);
            Shadows.shadowOf(cameraManager).getTorchMode(ShadowCameraManagerTest.CAMERA_ID_0);
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testGetTorchModeCameraTorchModeSet() throws CameraAccessException {
        Shadows.shadowOf(cameraManager).addCamera(ShadowCameraManagerTest.CAMERA_ID_0, characteristics);
        cameraManager.setTorchMode(ShadowCameraManagerTest.CAMERA_ID_0, ShadowCameraManagerTest.ENABLE);
        assertThat(Shadows.shadowOf(cameraManager).getTorchMode(ShadowCameraManagerTest.CAMERA_ID_0)).isEqualTo(ShadowCameraManagerTest.ENABLE);
    }
}

