package io.github.hidroh.materialistic;


import DialogInterface.BUTTON_POSITIVE;
import Intent.ACTION_VIEW;
import Intent.EXTRA_TEXT;
import R.id.edittext_content;
import R.id.edittext_title;
import R.id.menu_guidelines;
import R.id.menu_send;
import R.string;
import R.string.confirm_submit_question;
import R.string.confirm_submit_url;
import R.string.item_exist;
import R.string.submit_failed;
import UserServices.Callback;
import UserServices.Exception;
import android.R.id.home;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import io.github.hidroh.materialistic.accounts.UserServices;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowWebView;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;

import static BuildConfig.APPLICATION_ID;


@Config(shadows = ShadowWebView.class)
@RunWith(TestRunner.class)
public class SubmitActivityTest {
    private ActivityController<SubmitActivity> controller;

    private SubmitActivity activity;

    @Inject
    UserServices userServices;

    @Captor
    ArgumentCaptor<UserServices.Callback> submitCallback;

    @Test
    public void testOnBackPressed() {
        Shadows.shadowOf(activity).clickMenuItem(home);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        alertDialog.getButton(BUTTON_POSITIVE).performClick();
        assertThat(activity).isFinishing();
    }

    @Test
    public void testMissingInput() {
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        setText("title");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        ((android.widget.EditText) (activity.findViewById(edittext_title))).setText(null);
        setText("content");
        Mockito.verify(userServices, Mockito.never()).submit(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Callback.class));
        assertThat(activity).isNotFinishing();
    }

    @Test
    public void testSubmitText() {
        setText("title");
        setText("content");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        Assert.assertEquals(activity.getString(confirm_submit_question), Shadows.shadowOf(alertDialog).getMessage());
    }

    @Test
    public void testSubmitUrl() {
        setText("title");
        setText("http://example.com");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        Assert.assertEquals(activity.getString(confirm_submit_url), Shadows.shadowOf(alertDialog).getMessage());
    }

    @Test
    public void testGuidelines() {
        Shadows.shadowOf(activity).clickMenuItem(menu_guidelines);
        Assert.assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void testSubmitError() {
        setText("title");
        setText("http://example.com");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(userServices).submit(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("title"), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.eq(true), submitCallback.capture());
        Uri redirect = Uri.parse(((APPLICATION_ID) + "://item?id=1234"));
        UserServices.Exception exception = new UserServices.Exception(string.item_exist);
        exception.data = redirect;
        submitCallback.getValue().onError(exception);
        Assert.assertEquals(activity.getString(item_exist), ShadowToast.getTextOfLatestToast());
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_VIEW).hasData(redirect);
    }

    @Test
    public void testSubmitFailed() {
        setText("title");
        setText("http://example.com");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(userServices).submit(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("title"), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.eq(true), submitCallback.capture());
        submitCallback.getValue().onDone(false);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, LoginActivity.class);
    }

    @Test
    public void testSubmitSuccessful() {
        setText("title");
        setText("http://example.com");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(userServices).submit(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("title"), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.eq(true), submitCallback.capture());
        submitCallback.getValue().onDone(true);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, NewActivity.class);
        assertThat(activity).isFinishing();
    }

    @Test
    public void testSubmitDelayedError() {
        setText("title");
        setText("http://example.com");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(userServices).submit(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("title"), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.eq(true), submitCallback.capture());
        Shadows.shadowOf(activity).clickMenuItem(home);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        submitCallback.getValue().onError(new IOException());
        Assert.assertEquals(activity.getString(submit_failed), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testSubmitDelayedFailed() {
        setText("title");
        setText("http://example.com");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(userServices).submit(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("title"), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.eq(true), submitCallback.capture());
        Shadows.shadowOf(activity).clickMenuItem(home);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        submitCallback.getValue().onDone(false);
        Assert.assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testSubmitDelayedSuccessful() {
        setText("title");
        setText("http://example.com");
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(userServices).submit(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("title"), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.eq(true), submitCallback.capture());
        Shadows.shadowOf(activity).clickMenuItem(home);
        ShadowAlertDialog.getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();
        submitCallback.getValue().onDone(true);
        Assert.assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testDeriveTitleAndUrl() {
        controller.pause().stop().destroy();
        controller = Robolectric.buildActivity(SubmitActivity.class, new Intent().putExtra(EXTRA_TEXT, "title - http://example.com"));
        activity = controller.create().start().resume().visible().get();
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_title)))).hasTextString("title");
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_content)))).hasTextString("http://example.com");
        Shadows.shadowOf(activity).recreate();
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_title)))).hasTextString("title");
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_content)))).hasTextString("http://example.com");
    }

    @Test
    public void testDeriveEmptyTitle() {
        controller.pause().stop().destroy();
        controller = Robolectric.buildActivity(SubmitActivity.class, new Intent().putExtra(EXTRA_TEXT, " : http://example.com"));
        activity = controller.create().start().resume().visible().get();
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_title)))).isEmpty();
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_content)))).hasTextString("http://example.com");
    }

    @Test
    public void testDeriveNoMatches() {
        controller.pause().stop().destroy();
        controller = Robolectric.buildActivity(SubmitActivity.class, new Intent().putExtra(EXTRA_TEXT, "title - http://example.com blah blah"));
        activity = controller.create().start().resume().visible().get();
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_title)))).isEmpty();
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_content)))).hasTextString("title - http://example.com blah blah");
    }

    @Test
    public void testDeriveTitle() {
        controller.pause().stop().destroy();
        controller = Robolectric.buildActivity(SubmitActivity.class, new Intent().putExtra(EXTRA_TEXT, "http://example.com"));
        activity = controller.create().start().resume().visible().get();
        Assert.assertEquals("http://example.com", ShadowWebView.getLastGlobalLoadedUrl());
    }
}

