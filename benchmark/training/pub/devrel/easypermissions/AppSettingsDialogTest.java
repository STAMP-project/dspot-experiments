package pub.devrel.easypermissions;


import AlertDialog.BUTTON_NEGATIVE;
import AlertDialog.BUTTON_POSITIVE;
import DialogInterface.OnClickListener;
import R.style.Theme_AppCompat;
import android.R.string.cancel;
import android.R.string.dialog_alert_title;
import android.R.string.ok;
import android.R.string.unknownName;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowIntent;
import pub.devrel.easypermissions.testhelper.ActivityController;
import pub.devrel.easypermissions.testhelper.FragmentController;
import pub.devrel.easypermissions.testhelper.TestActivity;
import pub.devrel.easypermissions.testhelper.TestFragment;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class AppSettingsDialogTest {
    private static final String TITLE = "TITLE";

    private static final String RATIONALE = "RATIONALE";

    private static final String NEGATIVE = "NEGATIVE";

    private static final String POSITIVE = "POSITIVE";

    private ShadowApplication shadowApp;

    private TestActivity spyActivity;

    private TestFragment spyFragment;

    private FragmentController<TestFragment> fragmentController;

    private ActivityController<TestActivity> activityController;

    @Mock
    private OnClickListener positiveListener;

    @Mock
    private OnClickListener negativeListener;

    @Captor
    private ArgumentCaptor<Integer> integerCaptor;

    @Captor
    private ArgumentCaptor<Intent> intentCaptor;

    // ------ From Activity ------
    @Test
    public void shouldShowExpectedSettingsDialog_whenBuildingFromActivity() {
        new AppSettingsDialog.Builder(spyActivity).setTitle(dialog_alert_title).setRationale(unknownName).setPositiveButton(ok).setNegativeButton(cancel).setThemeResId(Theme_AppCompat).build().show();
        Mockito.verify(spyActivity, Mockito.times(1)).startActivityForResult(intentCaptor.capture(), integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE);
        assertThat(Objects.requireNonNull(intentCaptor.getValue().getComponent()).getClassName()).isEqualTo(AppSettingsDialogHolderActivity.class.getName());
        Intent startedIntent = shadowApp.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getIntentClass()).isEqualTo(AppSettingsDialogHolderActivity.class);
    }

    @Test
    public void shouldPositiveListener_whenClickingPositiveButtonFromActivity() {
        AlertDialog alertDialog = new AppSettingsDialog.Builder(spyActivity).setTitle(AppSettingsDialogTest.TITLE).setRationale(AppSettingsDialogTest.RATIONALE).setPositiveButton(AppSettingsDialogTest.POSITIVE).setNegativeButton(AppSettingsDialogTest.NEGATIVE).setThemeResId(Theme_AppCompat).build().showDialog(positiveListener, negativeListener);
        Button positive = alertDialog.getButton(BUTTON_POSITIVE);
        positive.performClick();
        Mockito.verify(positiveListener, Mockito.times(1)).onClick(ArgumentMatchers.any(DialogInterface.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldNegativeListener_whenClickingPositiveButtonFromActivity() {
        AlertDialog alertDialog = new AppSettingsDialog.Builder(spyActivity).setTitle(AppSettingsDialogTest.TITLE).setRationale(AppSettingsDialogTest.RATIONALE).setPositiveButton(AppSettingsDialogTest.POSITIVE).setNegativeButton(AppSettingsDialogTest.NEGATIVE).setThemeResId(Theme_AppCompat).build().showDialog(positiveListener, negativeListener);
        Button positive = alertDialog.getButton(BUTTON_NEGATIVE);
        positive.performClick();
        Mockito.verify(negativeListener, Mockito.times(1)).onClick(ArgumentMatchers.any(DialogInterface.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldShowExpectedSettingsDialog_whenBuildingFromSupportFragment() {
        new AppSettingsDialog.Builder(spyFragment).setTitle(dialog_alert_title).setRationale(unknownName).setPositiveButton(ok).setNegativeButton(cancel).setThemeResId(Theme_AppCompat).build().show();
        Mockito.verify(spyFragment, Mockito.times(1)).startActivityForResult(intentCaptor.capture(), integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE);
        assertThat(Objects.requireNonNull(intentCaptor.getValue().getComponent()).getClassName()).isEqualTo(AppSettingsDialogHolderActivity.class.getName());
        Intent startedIntent = shadowApp.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getIntentClass()).isEqualTo(AppSettingsDialogHolderActivity.class);
    }

    @Test
    public void shouldPositiveListener_whenClickingPositiveButtonFromSupportFragment() {
        AlertDialog alertDialog = new AppSettingsDialog.Builder(spyFragment).setTitle(AppSettingsDialogTest.TITLE).setRationale(AppSettingsDialogTest.RATIONALE).setPositiveButton(AppSettingsDialogTest.POSITIVE).setNegativeButton(AppSettingsDialogTest.NEGATIVE).setThemeResId(Theme_AppCompat).build().showDialog(positiveListener, negativeListener);
        Button positive = alertDialog.getButton(BUTTON_POSITIVE);
        positive.performClick();
        Mockito.verify(positiveListener, Mockito.times(1)).onClick(ArgumentMatchers.any(DialogInterface.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldNegativeListener_whenClickingPositiveButtonFromSupportFragment() {
        AlertDialog alertDialog = new AppSettingsDialog.Builder(spyFragment).setTitle(AppSettingsDialogTest.TITLE).setRationale(AppSettingsDialogTest.RATIONALE).setPositiveButton(AppSettingsDialogTest.POSITIVE).setNegativeButton(AppSettingsDialogTest.NEGATIVE).setThemeResId(Theme_AppCompat).build().showDialog(positiveListener, negativeListener);
        Button positive = alertDialog.getButton(BUTTON_NEGATIVE);
        positive.performClick();
        Mockito.verify(negativeListener, Mockito.times(1)).onClick(ArgumentMatchers.any(DialogInterface.class), ArgumentMatchers.anyInt());
    }
}

