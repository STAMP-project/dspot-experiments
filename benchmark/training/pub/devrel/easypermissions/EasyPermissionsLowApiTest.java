package pub.devrel.easypermissions;


import Manifest.permission;
import Manifest.permission.ACCESS_COARSE_LOCATION;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import pub.devrel.easypermissions.testhelper.ActivityController;
import pub.devrel.easypermissions.testhelper.FragmentController;
import pub.devrel.easypermissions.testhelper.TestActivity;
import pub.devrel.easypermissions.testhelper.TestAppCompatActivity;
import pub.devrel.easypermissions.testhelper.TestFragment;
import pub.devrel.easypermissions.testhelper.TestSupportFragmentActivity;


/**
 * Low-API (SDK = 19) tests for {@link pub.devrel.easypermissions.EasyPermissions}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 19)
public class EasyPermissionsLowApiTest {
    private static final String RATIONALE = "RATIONALE";

    private static final String[] ALL_PERMS = new String[]{ permission.READ_SMS, permission.ACCESS_FINE_LOCATION };

    private TestActivity spyActivity;

    private TestSupportFragmentActivity spySupportFragmentActivity;

    private TestAppCompatActivity spyAppCompatActivity;

    private TestFragment spyFragment;

    private FragmentController<TestFragment> fragmentController;

    private ActivityController<TestActivity> activityController;

    private ActivityController<TestSupportFragmentActivity> supportFragmentActivityController;

    private ActivityController<TestAppCompatActivity> appCompatActivityController;

    @Captor
    private ArgumentCaptor<Integer> integerCaptor;

    @Captor
    private ArgumentCaptor<ArrayList<String>> listCaptor;

    // ------ General tests ------
    @Test
    public void shouldHavePermission_whenHasPermissionsBeforeMarshmallow() {
        assertThat(EasyPermissions.hasPermissions(ApplicationProvider.getApplicationContext(), ACCESS_COARSE_LOCATION)).isTrue();
    }

    // ------ From Activity ------
    @Test
    public void shouldCallbackOnPermissionGranted_whenRequestFromActivity() {
        EasyPermissions.requestPermissions(spyActivity, EasyPermissionsLowApiTest.RATIONALE, TestActivity.REQUEST_CODE, EasyPermissionsLowApiTest.ALL_PERMS);
        Mockito.verify(spyActivity, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(EasyPermissionsLowApiTest.ALL_PERMS);
    }

    // ------ From Support Activity ------
    @Test
    public void shouldCallbackOnPermissionGranted_whenRequestFromSupportFragmentActivity() {
        EasyPermissions.requestPermissions(spySupportFragmentActivity, EasyPermissionsLowApiTest.RATIONALE, TestSupportFragmentActivity.REQUEST_CODE, EasyPermissionsLowApiTest.ALL_PERMS);
        Mockito.verify(spySupportFragmentActivity, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestSupportFragmentActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(EasyPermissionsLowApiTest.ALL_PERMS);
    }

    @Test
    public void shouldCallbackOnPermissionGranted_whenRequestFromAppCompatActivity() {
        EasyPermissions.requestPermissions(spyAppCompatActivity, EasyPermissionsLowApiTest.RATIONALE, TestAppCompatActivity.REQUEST_CODE, EasyPermissionsLowApiTest.ALL_PERMS);
        Mockito.verify(spyAppCompatActivity, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestAppCompatActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(EasyPermissionsLowApiTest.ALL_PERMS);
    }

    @Test
    public void shouldCallbackOnPermissionGranted_whenRequestFromFragment() {
        EasyPermissions.requestPermissions(spyFragment, EasyPermissionsLowApiTest.RATIONALE, TestFragment.REQUEST_CODE, EasyPermissionsLowApiTest.ALL_PERMS);
        Mockito.verify(spyFragment, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestFragment.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(EasyPermissionsLowApiTest.ALL_PERMS);
    }
}

