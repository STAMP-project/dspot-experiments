package org.robolectric.shadows;


import AlertDialog.BUTTON_NEGATIVE;
import AlertDialog.BUTTON_NEUTRAL;
import AlertDialog.BUTTON_POSITIVE;
import AlertDialog.Builder;
import View.GONE;
import View.VISIBLE;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.CustomView;
import org.robolectric.annotation.Config;

import static org.robolectric.R.array.alertDialogTestItems;
import static org.robolectric.R.drawable.an_image;
import static org.robolectric.R.id.title;
import static org.robolectric.R.layout.custom_layout;
import static org.robolectric.R.layout.main;
import static org.robolectric.R.string.hello;


@RunWith(AndroidJUnit4.class)
public class ShadowAlertDialogTest {
    private Application context;

    @Test
    public void testBuilder() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setTitle("title").setMessage("message");
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
        assertThat(alert.isShowing()).isTrue();
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alert);
        Assert.assertEquals("title", shadowAlertDialog.getTitle());
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("message");
        assertThat(shadowAlertDialog.isCancelable()).isTrue();
        assertThat(Shadows.shadowOf(ShadowAlertDialog.getLatestAlertDialog())).isSameAs(shadowAlertDialog);
        assertThat(ShadowAlertDialog.getLatestAlertDialog()).isSameAs(alert);
    }

    @Test
    public void nullTitleAndMessageAreOkay() throws Exception {
        AlertDialog.Builder builder = // 
        // 
        setTitle(null).setMessage(null);
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(builder.create());
        assertThat(shadowAlertDialog.getTitle().toString()).isEqualTo("");
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("");
    }

    @Test
    public void getLatestAlertDialog_shouldReturnARealAlertDialog() throws Exception {
        assertThat(ShadowAlertDialog.getLatestAlertDialog()).isNull();
        AlertDialog dialog = show();
        assertThat(ShadowAlertDialog.getLatestAlertDialog()).isSameAs(dialog);
    }

    @Test
    public void shouldOnlyCreateRequestedButtons() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        assertThat(dialog.getButton(BUTTON_POSITIVE).getVisibility()).isEqualTo(VISIBLE);
        assertThat(dialog.getButton(BUTTON_NEGATIVE).getVisibility()).isEqualTo(GONE);
    }

    @Test
    public void shouldAllowNullButtonListeners() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        ShadowView.clickOn(dialog.getButton(BUTTON_POSITIVE));
    }

    @Test
    public void testSetMessageAfterCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setTitle("title").setMessage("message");
        AlertDialog alert = builder.create();
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alert);
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("message");
        alert.setMessage("new message");
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("new message");
        alert.setMessage(null);
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("");
    }

    @Test
    public void shouldSetMessageFromResourceId() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setTitle("title").setMessage(hello);
        AlertDialog alert = builder.create();
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alert);
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("Hello");
    }

    @Test
    public void shouldSetView() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        EditText view = new EditText(RuntimeEnvironment.application);
        builder.setView(view);
        AlertDialog alert = builder.create();
        assertThat(Shadows.shadowOf(alert).getView()).isEqualTo(view);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void shouldSetView_withLayoutId() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setView(custom_layout);
        AlertDialog alert = builder.create();
        View view = Shadows.shadowOf(alert).getView();
        assertThat(view.getClass()).isEqualTo(CustomView.class);
    }

    @Test
    public void shouldSetCustomTitleView() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        View view = new View(RuntimeEnvironment.application);
        assertThat(builder.setCustomTitle(view)).isSameAs(builder);
        AlertDialog alert = builder.create();
        assertThat(Shadows.shadowOf(alert).getCustomTitleView()).isEqualTo(view);
    }

    @Test
    public void clickingPositiveButtonDismissesDialog() throws Exception {
        AlertDialog alertDialog = create();
        alertDialog.show();
        Assert.assertTrue(alertDialog.isShowing());
        alertDialog.getButton(BUTTON_POSITIVE).performClick();
        Assert.assertFalse(alertDialog.isShowing());
    }

    @Test
    public void clickingNeutralButtonDismissesDialog() throws Exception {
        AlertDialog alertDialog = create();
        alertDialog.show();
        Assert.assertTrue(alertDialog.isShowing());
        alertDialog.getButton(BUTTON_NEUTRAL).performClick();
        Assert.assertFalse(alertDialog.isShowing());
    }

    @Test
    public void clickingNegativeButtonDismissesDialog() throws Exception {
        AlertDialog alertDialog = create();
        alertDialog.show();
        Assert.assertTrue(alertDialog.isShowing());
        alertDialog.getButton(BUTTON_NEGATIVE).performClick();
        Assert.assertFalse(alertDialog.isShowing());
    }

    @Test
    public void testBuilderWithItemArrayViaResourceId() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(new android.content.ContextWrapper(context));
        builder.setTitle("title");
        builder.setItems(alertDialogTestItems, new ShadowAlertDialogTest.TestDialogOnClickListener());
        AlertDialog alert = builder.create();
        alert.show();
        assertThat(alert.isShowing()).isTrue();
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alert);
        assertThat(shadowAlertDialog.getTitle().toString()).isEqualTo("title");
        assertThat(shadowAlertDialog.getItems().length).isEqualTo(2);
        assertThat(shadowAlertDialog.getItems()[0]).isEqualTo("Aloha");
        assertThat(Shadows.shadowOf(ShadowAlertDialog.getLatestAlertDialog())).isSameAs(shadowAlertDialog);
        assertThat(ShadowAlertDialog.getLatestAlertDialog()).isSameAs(alert);
    }

    @Test
    public void testBuilderWithAdapter() throws Exception {
        List<Integer> list = new ArrayList<>();
        list.add(99);
        list.add(88);
        list.add(77);
        ArrayAdapter<Integer> adapter = new ArrayAdapter(context, main, title, list);
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setSingleChoiceItems(adapter, (-1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Assert.assertTrue(alert.isShowing());
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alert);
        assertThat(shadowAlertDialog.getAdapter().getCount()).isEqualTo(3);
        assertThat(shadowAlertDialog.getAdapter().getItem(0)).isEqualTo(99);
    }

    @Test
    public void show_setsLatestAlertDialogAndLatestDialog() {
        AlertDialog alertDialog = create();
        Assert.assertNull(ShadowDialog.getLatestDialog());
        Assert.assertNull(ShadowAlertDialog.getLatestAlertDialog());
        alertDialog.show();
        Assert.assertEquals(alertDialog, ShadowDialog.getLatestDialog());
        Assert.assertEquals(alertDialog, ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void shouldCallTheClickListenerOfTheCheckedAdapterInASingleChoiceDialog() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(new android.content.ContextWrapper(context));
        ShadowAlertDialogTest.TestDialogOnClickListener listener = new ShadowAlertDialogTest.TestDialogOnClickListener();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter(context, main, title, list);
        builder.setSingleChoiceItems(arrayAdapter, 1, listener);
        AlertDialog alert = builder.create();
        alert.show();
        ShadowAlertDialog shadowAlertDialog = Shadows.shadowOf(alert);
        shadowAlertDialog.clickOnItem(0);
        assertThat(listener.transcript).containsExactly("clicked on 0");
        listener.transcript.clear();
        shadowAlertDialog.clickOnItem(1);
        assertThat(listener.transcript).containsExactly("clicked on 1");
    }

    @Test
    public void shouldDelegateToDialogFindViewByIdIfViewIsNull() {
        AlertDialog dialog = new AlertDialog(context) {};
        assertThat(((View) (dialog.findViewById(99)))).isNull();
        dialog.setContentView(main);
        Assert.assertNotNull(dialog.findViewById(title));
    }

    @Test
    public void shouldNotExplodeWhenNestingAlerts() throws Exception {
        final Activity activity = org.robolectric.Robolectric.buildActivity(Activity.class).create().get();
        final AlertDialog nestedDialog = create();
        final AlertDialog dialog = create();
        dialog.show();
        assertThat(ShadowDialog.getLatestDialog()).isEqualTo(dialog);
        dialog.getButton(Dialog.BUTTON_POSITIVE).performClick();
        assertThat(ShadowDialog.getLatestDialog()).isEqualTo(nestedDialog);
    }

    @Test
    public void alertControllerShouldStoreCorrectIconIdFromBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        builder.setIcon(an_image);
        AlertDialog alertDialog = builder.create();
        assertThat(Shadows.shadowOf(alertDialog).getIconId()).isEqualTo(an_image);
    }

    private static class TestDialogOnClickListener implements DialogInterface.OnClickListener {
        private final ArrayList<String> transcript = new ArrayList<>();

        @Override
        public void onClick(DialogInterface dialog, int item) {
            transcript.add(("clicked on " + item));
        }
    }
}

