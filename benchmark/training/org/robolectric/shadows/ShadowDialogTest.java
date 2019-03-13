package org.robolectric.shadows;


import DialogInterface.OnCancelListener;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import static org.robolectric.R.id.title;
import static org.robolectric.R.layout.main;


@RunWith(AndroidJUnit4.class)
public class ShadowDialogTest {
    private Application context;

    @Test
    public void shouldCallOnDismissListener() throws Exception {
        final List<String> transcript = new ArrayList<>();
        final Dialog dialog = new Dialog(context);
        dialog.show();
        dialog.setOnDismissListener(( dialogInListener) -> {
            assertThat(dialogInListener).isSameAs(dialog);
            transcript.add("onDismiss called!");
        });
        dialog.dismiss();
        assertThat(transcript).containsExactly("onDismiss called!");
    }

    @Test
    public void setContentViewWithViewAllowsFindById() throws Exception {
        final int viewId = 1234;
        final Dialog dialog = new Dialog(context);
        final View view = new View(context);
        view.setId(viewId);
        dialog.setContentView(view);
        Assert.assertSame(view, dialog.findViewById(viewId));
    }

    @Test
    public void shouldGetLayoutInflater() {
        Dialog dialog = new Dialog(context);
        Assert.assertNotNull(dialog.getLayoutInflater());
    }

    @Test
    public void shouldCallOnStartFromShow() {
        ShadowDialogTest.TestDialog dialog = new ShadowDialogTest.TestDialog(context);
        show();
        Assert.assertTrue(dialog.onStartCalled);
    }

    @Test
    public void shouldSetCancelable() {
        Dialog dialog = new Dialog(context);
        ShadowDialog shadow = Shadows.shadowOf(dialog);
        dialog.setCancelable(false);
        assertThat(shadow.isCancelable()).isFalse();
    }

    @Test
    public void shouldDismissTheRealDialogWhenCancelled() throws Exception {
        ShadowDialogTest.TestDialog dialog = new ShadowDialogTest.TestDialog(context);
        cancel();
        assertThat(dialog.wasDismissed).isTrue();
    }

    @Test
    public void shouldDefaultCancelableToTrueAsTheSDKDoes() throws Exception {
        Dialog dialog = new Dialog(context);
        ShadowDialog shadow = Shadows.shadowOf(dialog);
        assertThat(shadow.isCancelable()).isTrue();
    }

    @Test
    public void shouldOnlyCallOnCreateOnce() {
        final List<String> transcript = new ArrayList<>();
        Dialog dialog = new Dialog(context) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                transcript.add("onCreate called");
            }
        };
        dialog.show();
        assertThat(transcript).containsExactly("onCreate called");
        transcript.clear();
        dialog.dismiss();
        dialog.show();
        assertThat(transcript).isEmpty();
    }

    @Test
    public void show_setsLatestDialog() {
        Dialog dialog = new Dialog(context);
        Assert.assertNull(ShadowDialog.getLatestDialog());
        dialog.show();
        Assert.assertSame(dialog, ShadowDialog.getLatestDialog());
        Assert.assertNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void getLatestDialog_shouldReturnARealDialog() throws Exception {
        assertThat(ShadowDialog.getLatestDialog()).isNull();
        Dialog dialog = new Dialog(context);
        dialog.show();
        assertThat(ShadowDialog.getLatestDialog()).isSameAs(dialog);
    }

    @Test
    public void shouldKeepListOfOpenedDialogs() throws Exception {
        Assert.assertEquals(0, ShadowDialog.getShownDialogs().size());
        ShadowDialogTest.TestDialog dialog = new ShadowDialogTest.TestDialog(context);
        show();
        Assert.assertEquals(1, ShadowDialog.getShownDialogs().size());
        Assert.assertEquals(dialog, ShadowDialog.getShownDialogs().get(0));
        ShadowDialogTest.TestDialog dialog2 = new ShadowDialogTest.TestDialog(context);
        show();
        Assert.assertEquals(2, ShadowDialog.getShownDialogs().size());
        Assert.assertEquals(dialog2, ShadowDialog.getShownDialogs().get(1));
        dialog.dismiss();
        Assert.assertEquals(2, ShadowDialog.getShownDialogs().size());
        ShadowDialog.reset();
        Assert.assertEquals(0, ShadowDialog.getShownDialogs().size());
    }

    @Test
    public void shouldPopulateListOfRecentDialogsInCorrectOrder() throws Exception {
        show();
        Assert.assertEquals(ShadowDialogTest.TestDialog.class, ShadowDialog.getLatestDialog().getClass());
    }

    @Test
    public void shouldFindViewsWithinAContentViewThatWasPreviouslySet() throws Exception {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialog.getLayoutInflater().inflate(main, null));
        assertThat(dialog.<TextView>findViewById(title)).isInstanceOf(((Class<? extends TextView>) (TextView.class)));
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void show_shouldWorkWithAPI19() {
        Dialog dialog = new Dialog(context);
        dialog.show();
    }

    @Test
    public void canSetAndGetOnCancelListener() {
        Dialog dialog = new Dialog(context);
        DialogInterface.OnCancelListener onCancelListener = ( dialog1) -> {
        };
        dialog.setOnCancelListener(onCancelListener);
        assertThat(onCancelListener).isSameAs(Shadows.shadowOf(dialog).getOnCancelListener());
    }

    private static class TestDialog extends Dialog {
        boolean onStartCalled = false;

        boolean wasDismissed = false;

        public TestDialog(Context context) {
            super(context);
        }

        @Override
        protected void onStart() {
            onStartCalled = true;
        }

        @Override
        public void dismiss() {
            super.dismiss();
            wasDismissed = true;
        }
    }

    private static class NestingTestDialog extends Dialog {
        public NestingTestDialog() {
            super(ApplicationProvider.getApplicationContext());
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            show();
        }
    }
}

