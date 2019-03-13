package pub.devrel.easypermissions;


import Manifest.permission;
import Manifest.permission.ACCESS_FINE_LOCATION;
import Manifest.permission.READ_SMS;
import R.style.Theme_AppCompat;
import RationaleDialogFragment.TAG;
import android.R.string.cancel;
import android.R.string.ok;
import android.R.string.unknownName;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import pub.devrel.easypermissions.testhelper.ActivityController;
import pub.devrel.easypermissions.testhelper.FragmentController;
import pub.devrel.easypermissions.testhelper.TestActivity;
import pub.devrel.easypermissions.testhelper.TestAppCompatActivity;
import pub.devrel.easypermissions.testhelper.TestFragment;
import pub.devrel.easypermissions.testhelper.TestSupportFragmentActivity;


/**
 * Basic Robolectric tests for {@link pub.devrel.easypermissions.EasyPermissions}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class EasyPermissionsTest {
    private static final String RATIONALE = "RATIONALE";

    private static final String POSITIVE = "POSITIVE";

    private static final String NEGATIVE = "NEGATIVE";

    private static final String[] ONE_PERM = new String[]{ permission.READ_SMS };

    private static final String[] ALL_PERMS = new String[]{ permission.READ_SMS, permission.ACCESS_FINE_LOCATION };

    private static final int[] SMS_DENIED_RESULT = new int[]{ PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_GRANTED };

    private ShadowApplication shadowApp;

    private Application app;

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
    public void shouldNotHavePermissions_whenNoPermissionsGranted() {
        assertThat(EasyPermissions.hasPermissions(app, EasyPermissionsTest.ALL_PERMS)).isFalse();
    }

    @Test
    public void shouldNotHavePermissions_whenNotAllPermissionsGranted() {
        shadowApp.grantPermissions(EasyPermissionsTest.ONE_PERM);
        assertThat(EasyPermissions.hasPermissions(app, EasyPermissionsTest.ALL_PERMS)).isFalse();
    }

    @Test
    public void shouldHavePermissions_whenAllPermissionsGranted() {
        shadowApp.grantPermissions(EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.hasPermissions(app, EasyPermissionsTest.ALL_PERMS)).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldThrowException_whenHasPermissionsWithNullContext() {
        try {
            EasyPermissions.hasPermissions(null, EasyPermissionsTest.ALL_PERMS);
            Assert.fail("IllegalStateException expected because of null context.");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("Can't check permissions for null context");
        }
    }

    // ------ From Activity ------
    @Test
    public void shouldCorrectlyCallback_whenOnRequestPermissionResultCalledFromActivity() {
        EasyPermissions.onRequestPermissionsResult(TestActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS, EasyPermissionsTest.SMS_DENIED_RESULT, spyActivity);
        Mockito.verify(spyActivity, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(new ArrayList(Collections.singletonList(ACCESS_FINE_LOCATION)));
        Mockito.verify(spyActivity, Mockito.times(1)).onPermissionsDenied(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(new ArrayList(Collections.singletonList(READ_SMS)));
        Mockito.verify(spyActivity, Mockito.never()).afterPermissionGranted();
    }

    @Test
    public void shouldCallbackOnPermissionGranted_whenRequestAlreadyGrantedPermissionsFromActivity() {
        grantPermissions(EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyActivity, EasyPermissionsTest.RATIONALE, TestActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Mockito.verify(spyActivity, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        Mockito.verify(spyActivity, Mockito.never()).requestPermissions(ArgumentMatchers.any(String[].class), ArgumentMatchers.anyInt());
        assertThat(integerCaptor.getValue()).isEqualTo(TestActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(EasyPermissionsTest.ALL_PERMS);
    }

    @Test
    public void shouldCallbackAfterPermissionGranted_whenRequestAlreadyGrantedPermissionsFromActivity() {
        grantPermissions(EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyActivity, EasyPermissionsTest.RATIONALE, TestActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        // Called 2 times because this is a spy and library implementation invokes super classes annotated methods as well
        Mockito.verify(spyActivity, Mockito.times(2)).afterPermissionGranted();
    }

    @Test
    public void shouldNotCallbackAfterPermissionGranted_whenRequestNotGrantedPermissionsFromActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        EasyPermissions.requestPermissions(spyActivity, EasyPermissionsTest.RATIONALE, TestActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Mockito.verify(spyActivity, Mockito.never()).afterPermissionGranted();
    }

    @Test
    public void shouldRequestPermissions_whenMissingPermissionAndNotShowRationaleFromActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyActivity, EasyPermissionsTest.RATIONALE, TestActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        requestPermissions(EasyPermissionsTest.ALL_PERMS, TestActivity.REQUEST_CODE);
    }

    @Test
    public void shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyActivity, EasyPermissionsTest.RATIONALE, TestActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Fragment dialogFragment = getFragmentManager().findFragmentByTag(TAG);
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragment.class);
        Dialog dialog = getDialog();
        assertThatHasExpectedRationale(dialog, EasyPermissionsTest.RATIONALE);
    }

    @Test
    public void shouldShowCorrectDialogUsingRequest_whenMissingPermissionsAndShowRationaleFromActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        PermissionRequest request = new PermissionRequest.Builder(spyActivity, TestActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS).setPositiveButtonText(ok).setNegativeButtonText(cancel).setRationale(unknownName).setTheme(Theme_AppCompat).build();
        EasyPermissions.requestPermissions(request);
        Fragment dialogFragment = getFragmentManager().findFragmentByTag(TAG);
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragment.class);
        Dialog dialog = getDialog();
        assertThatHasExpectedButtonsAndRationale(dialog, unknownName, ok, cancel);
    }

    @Test
    public void shouldHaveSomePermissionDenied_whenShowRationaleFromActivity() {
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionDenied(spyActivity, EasyPermissionsTest.ALL_PERMS)).isTrue();
    }

    @Test
    public void shouldNotHaveSomePermissionDenied_whenNotShowRationaleFromActivity() {
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionDenied(spyActivity, EasyPermissionsTest.ALL_PERMS)).isFalse();
    }

    @Test
    public void shouldHaveSomePermissionPermanentlyDenied_whenNotShowRationaleFromActivity() {
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionPermanentlyDenied(spyActivity, Arrays.asList(EasyPermissionsTest.ALL_PERMS))).isTrue();
    }

    @Test
    public void shouldNotHaveSomePermissionPermanentlyDenied_whenShowRationaleFromActivity() {
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionPermanentlyDenied(spyActivity, Arrays.asList(EasyPermissionsTest.ALL_PERMS))).isFalse();
    }

    @Test
    public void shouldHavePermissionPermanentlyDenied_whenNotShowRationaleFromActivity() {
        showRationale(false, READ_SMS);
        assertThat(EasyPermissions.permissionPermanentlyDenied(spyActivity, READ_SMS)).isTrue();
    }

    @Test
    public void shouldNotHavePermissionPermanentlyDenied_whenShowRationaleFromActivity() {
        showRationale(true, READ_SMS);
        assertThat(EasyPermissions.permissionPermanentlyDenied(spyActivity, READ_SMS)).isFalse();
    }

    @Test
    public void shouldCorrectlyCallback_whenOnRequestPermissionResultCalledFromAppCompatActivity() {
        EasyPermissions.onRequestPermissionsResult(TestAppCompatActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS, EasyPermissionsTest.SMS_DENIED_RESULT, spyAppCompatActivity);
        Mockito.verify(spyAppCompatActivity, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestAppCompatActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(new ArrayList(Collections.singletonList(ACCESS_FINE_LOCATION)));
        Mockito.verify(spyAppCompatActivity, Mockito.times(1)).onPermissionsDenied(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestAppCompatActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(new ArrayList(Collections.singletonList(READ_SMS)));
        Mockito.verify(spyAppCompatActivity, Mockito.never()).afterPermissionGranted();
    }

    @Test
    public void shouldCallbackOnPermissionGranted_whenRequestAlreadyGrantedPermissionsFromAppCompatActivity() {
        grantPermissions(EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyAppCompatActivity, EasyPermissionsTest.RATIONALE, TestAppCompatActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Mockito.verify(spyAppCompatActivity, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        Mockito.verify(spyAppCompatActivity, Mockito.never()).requestPermissions(ArgumentMatchers.any(String[].class), ArgumentMatchers.anyInt());
        assertThat(integerCaptor.getValue()).isEqualTo(TestAppCompatActivity.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(EasyPermissionsTest.ALL_PERMS);
    }

    @Test
    public void shouldCallbackAfterPermissionGranted_whenRequestAlreadyGrantedPermissionsFromAppCompatActivity() {
        grantPermissions(EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyAppCompatActivity, EasyPermissionsTest.RATIONALE, TestAppCompatActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        // Called 2 times because this is a spy and library implementation invokes super classes annotated methods as well
        Mockito.verify(spyAppCompatActivity, Mockito.times(2)).afterPermissionGranted();
    }

    @Test
    public void shouldNotCallbackAfterPermissionGranted_whenRequestNotGrantedPermissionsFromAppCompatActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        EasyPermissions.requestPermissions(spyAppCompatActivity, EasyPermissionsTest.RATIONALE, TestAppCompatActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Mockito.verify(spyAppCompatActivity, Mockito.never()).afterPermissionGranted();
    }

    @Test
    public void shouldRequestPermissions_whenMissingPermissionAndNotShowRationaleFromAppCompatActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyAppCompatActivity, EasyPermissionsTest.RATIONALE, TestAppCompatActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        requestPermissions(EasyPermissionsTest.ALL_PERMS, TestAppCompatActivity.REQUEST_CODE);
    }

    @Test
    public void shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromAppCompatActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyAppCompatActivity, EasyPermissionsTest.RATIONALE, TestAppCompatActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        androidx.fragment.app.Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(RationaleDialogFragmentCompat.TAG);
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat.class);
        Dialog dialog = getDialog();
        assertThatHasExpectedRationale(dialog, EasyPermissionsTest.RATIONALE);
    }

    @Test
    public void shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromSupportFragmentActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spySupportFragmentActivity, EasyPermissionsTest.RATIONALE, TestSupportFragmentActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Fragment dialogFragment = getFragmentManager().findFragmentByTag(TAG);
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragment.class);
        Dialog dialog = getDialog();
        assertThatHasExpectedRationale(dialog, EasyPermissionsTest.RATIONALE);
    }

    @Test
    public void shouldShowCorrectDialogUsingRequest_whenMissingPermissionsAndShowRationaleFromAppCompatActivity() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        PermissionRequest request = new PermissionRequest.Builder(spyAppCompatActivity, TestAppCompatActivity.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS).setPositiveButtonText(ok).setNegativeButtonText(cancel).setRationale(unknownName).setTheme(Theme_AppCompat).build();
        EasyPermissions.requestPermissions(request);
        androidx.fragment.app.Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(RationaleDialogFragmentCompat.TAG);
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat.class);
        Dialog dialog = getDialog();
        assertThatHasExpectedButtonsAndRationale(dialog, unknownName, ok, cancel);
    }

    @Test
    public void shouldHaveSomePermissionDenied_whenShowRationaleFromAppCompatActivity() {
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionDenied(spyAppCompatActivity, EasyPermissionsTest.ALL_PERMS)).isTrue();
    }

    @Test
    public void shouldNotHaveSomePermissionDenied_whenNotShowRationaleFromAppCompatActivity() {
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionDenied(spyAppCompatActivity, EasyPermissionsTest.ALL_PERMS)).isFalse();
    }

    @Test
    public void shouldHaveSomePermissionPermanentlyDenied_whenNotShowRationaleFromAppCompatActivity() {
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionPermanentlyDenied(spyAppCompatActivity, Arrays.asList(EasyPermissionsTest.ALL_PERMS))).isTrue();
    }

    @Test
    public void shouldNotHaveSomePermissionPermanentlyDenied_whenShowRationaleFromAppCompatActivity() {
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionPermanentlyDenied(spyAppCompatActivity, Arrays.asList(EasyPermissionsTest.ALL_PERMS))).isFalse();
    }

    @Test
    public void shouldHavePermissionPermanentlyDenied_whenNotShowRationaleFromAppCompatActivity() {
        showRationale(false, READ_SMS);
        assertThat(EasyPermissions.permissionPermanentlyDenied(spyAppCompatActivity, READ_SMS)).isTrue();
    }

    @Test
    public void shouldNotHavePermissionPermanentlyDenied_whenShowRationaleFromAppCompatActivity() {
        showRationale(true, READ_SMS);
        assertThat(EasyPermissions.permissionPermanentlyDenied(spyAppCompatActivity, READ_SMS)).isFalse();
    }

    @Test
    public void shouldCorrectlyCallback_whenOnRequestPermissionResultCalledFromFragment() {
        EasyPermissions.onRequestPermissionsResult(TestFragment.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS, EasyPermissionsTest.SMS_DENIED_RESULT, spyFragment);
        Mockito.verify(spyFragment, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestFragment.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(new ArrayList(Collections.singletonList(ACCESS_FINE_LOCATION)));
        Mockito.verify(spyFragment, Mockito.times(1)).onPermissionsDenied(integerCaptor.capture(), listCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(TestFragment.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(new ArrayList(Collections.singletonList(READ_SMS)));
        Mockito.verify(spyFragment, Mockito.never()).afterPermissionGranted();
    }

    @Test
    public void shouldCallbackOnPermissionGranted_whenRequestAlreadyGrantedPermissionsFromFragment() {
        grantPermissions(EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyFragment, EasyPermissionsTest.RATIONALE, TestFragment.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Mockito.verify(spyFragment, Mockito.times(1)).onPermissionsGranted(integerCaptor.capture(), listCaptor.capture());
        Mockito.verify(spyFragment, Mockito.never()).requestPermissions(ArgumentMatchers.any(String[].class), ArgumentMatchers.anyInt());
        assertThat(integerCaptor.getValue()).isEqualTo(TestFragment.REQUEST_CODE);
        assertThat(listCaptor.getValue()).containsAllIn(EasyPermissionsTest.ALL_PERMS);
    }

    @Test
    public void shouldCallbackAfterPermissionGranted_whenRequestAlreadyGrantedPermissionsFragment() {
        grantPermissions(EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyFragment, EasyPermissionsTest.RATIONALE, TestFragment.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        // Called 2 times because this is a spy and library implementation invokes super classes annotated methods as well
        Mockito.verify(spyFragment, Mockito.times(2)).afterPermissionGranted();
    }

    @Test
    public void shouldNotCallbackAfterPermissionGranted_whenRequestNotGrantedPermissionsFromFragment() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        EasyPermissions.requestPermissions(spyFragment, EasyPermissionsTest.RATIONALE, TestFragment.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        Mockito.verify(spyFragment, Mockito.never()).afterPermissionGranted();
    }

    @Test
    public void shouldRequestPermissions_whenMissingPermissionsAndNotShowRationaleFromFragment() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyFragment, EasyPermissionsTest.RATIONALE, TestFragment.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        requestPermissions(EasyPermissionsTest.ALL_PERMS, TestFragment.REQUEST_CODE);
    }

    @Test
    public void shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromFragment() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        EasyPermissions.requestPermissions(spyFragment, EasyPermissionsTest.RATIONALE, TestFragment.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS);
        androidx.fragment.app.Fragment dialogFragment = getChildFragmentManager().findFragmentByTag(RationaleDialogFragmentCompat.TAG);
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat.class);
        Dialog dialog = getDialog();
        assertThatHasExpectedRationale(dialog, EasyPermissionsTest.RATIONALE);
    }

    @Test
    public void shouldShowCorrectDialogUsingRequest_whenMissingPermissionsAndShowRationaleFromFragment() {
        grantPermissions(EasyPermissionsTest.ONE_PERM);
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        PermissionRequest request = new PermissionRequest.Builder(spyFragment, TestFragment.REQUEST_CODE, EasyPermissionsTest.ALL_PERMS).setPositiveButtonText(EasyPermissionsTest.POSITIVE).setNegativeButtonText(EasyPermissionsTest.NEGATIVE).setRationale(EasyPermissionsTest.RATIONALE).setTheme(Theme_AppCompat).build();
        EasyPermissions.requestPermissions(request);
        androidx.fragment.app.Fragment dialogFragment = getChildFragmentManager().findFragmentByTag(RationaleDialogFragmentCompat.TAG);
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat.class);
        Dialog dialog = getDialog();
        assertThatHasExpectedButtonsAndRationale(dialog, EasyPermissionsTest.RATIONALE, EasyPermissionsTest.POSITIVE, EasyPermissionsTest.NEGATIVE);
    }

    @Test
    public void shouldHaveSomePermissionDenied_whenShowRationaleFromFragment() {
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionDenied(spyFragment, EasyPermissionsTest.ALL_PERMS)).isTrue();
    }

    @Test
    public void shouldNotHaveSomePermissionDenied_whenNotShowRationaleFromFragment() {
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionDenied(spyFragment, EasyPermissionsTest.ALL_PERMS)).isFalse();
    }

    @Test
    public void shouldHaveSomePermissionPermanentlyDenied_whenNotShowRationaleFromFragment() {
        showRationale(false, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionPermanentlyDenied(spyFragment, Arrays.asList(EasyPermissionsTest.ALL_PERMS))).isTrue();
    }

    @Test
    public void shouldNotHaveSomePermissionPermanentlyDenied_whenShowRationaleFromFragment() {
        showRationale(true, EasyPermissionsTest.ALL_PERMS);
        assertThat(EasyPermissions.somePermissionPermanentlyDenied(spyFragment, Arrays.asList(EasyPermissionsTest.ALL_PERMS))).isFalse();
    }

    @Test
    public void shouldHavePermissionPermanentlyDenied_whenNotShowRationaleFromFragment() {
        showRationale(false, READ_SMS);
        assertThat(EasyPermissions.permissionPermanentlyDenied(spyFragment, READ_SMS)).isTrue();
    }

    @Test
    public void shouldNotHavePermissionPermanentlyDenied_whenShowRationaleFromFragment() {
        showRationale(true, READ_SMS);
        assertThat(EasyPermissions.permissionPermanentlyDenied(spyFragment, READ_SMS)).isFalse();
    }
}

