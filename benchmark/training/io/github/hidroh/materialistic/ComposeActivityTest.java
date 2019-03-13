package io.github.hidroh.materialistic;


import ComposeActivity.EXTRA_PARENT_ID;
import DialogInterface.BUTTON_NEGATIVE;
import DialogInterface.BUTTON_POSITIVE;
import R.id.edittext_body;
import R.id.empty;
import R.id.menu_discard_draft;
import R.id.menu_guidelines;
import R.id.menu_quote;
import R.id.menu_save_draft;
import R.id.menu_send;
import R.id.quote;
import R.id.text;
import R.id.toggle;
import R.string.comment_failed;
import R.string.comment_required;
import R.string.comment_successful;
import android.R.id.home;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import io.github.hidroh.materialistic.accounts.UserServices;
import io.github.hidroh.materialistic.test.TestRunner;
import java.io.IOException;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertNull;


@RunWith(TestRunner.class)
public class ComposeActivityTest {
    private ActivityController<ComposeActivity> controller;

    private ComposeActivity activity;

    @Inject
    UserServices userServices;

    @Captor
    ArgumentCaptor<UserServices.Callback> replyCallback;

    @Test
    public void testNoId() {
        controller = Robolectric.buildActivity(ComposeActivity.class).create().start().resume().visible();
        activity = controller.get();
        assertThat(activity).isFinishing();
    }

    @Test
    public void testToggle() {
        assertThat(((View) (activity.findViewById(toggle)))).isVisible();
        assertThat(((View) (activity.findViewById(text)))).isNotVisible();
        activity.findViewById(toggle).performClick();
        assertThat(((View) (activity.findViewById(text)))).isVisible();
        assertThat(((android.widget.TextView) (activity.findViewById(text)))).hasTextString("Paragraph 1\n\nParagraph 2");
        activity.findViewById(toggle).performClick();
        assertThat(((View) (activity.findViewById(text)))).isNotVisible();
    }

    @Test
    public void testHomeButtonClick() {
        Shadows.shadowOf(activity).clickMenuItem(home);
        assertThat(activity).isFinishing();
    }

    @Test
    public void testExitSaveDraft() {
        setText("Reply");
        Shadows.shadowOf(activity).clickMenuItem(home);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        alertDialog.getButton(BUTTON_POSITIVE).performClick();
        assertThat(activity).isFinishing();
        assertThat(Preferences.getDraft(activity, "1")).contains("Reply");
    }

    @Test
    public void testExitDiscardDraft() {
        setText("Reply");
        Shadows.shadowOf(activity).clickMenuItem(home);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        alertDialog.getButton(BUTTON_NEGATIVE).performClick();
        assertThat(activity).isFinishing();
        assertThat(Preferences.getDraft(activity, "1")).isNullOrEmpty();
    }

    @Test
    public void testSendEmpty() {
        Shadows.shadowOf(activity).clickMenuItem(menu_send);
        Assert.assertEquals(activity.getString(comment_required), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testQuote() {
        Assert.assertTrue(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_quote).isVisible());
        setText("Reply");
        Shadows.shadowOf(activity).clickMenuItem(menu_quote);
        assertThat(((android.widget.EditText) (activity.findViewById(edittext_body)))).hasTextString("> Paragraph 1\n\n> Paragraph 2\n\nReply");
    }

    @Test
    public void testSaveDiscardDraft() {
        setText("Reply");
        Shadows.shadowOf(activity).clickMenuItem(menu_save_draft);
        assertThat(Preferences.getDraft(activity, "1")).contains("Reply");
        Shadows.shadowOf(activity).clickMenuItem(menu_discard_draft);
        assertThat(Preferences.getDraft(activity, "1")).isNullOrEmpty();
    }

    @Test
    public void testClickEmptyFocusEditText() {
        View editText = activity.findViewById(edittext_body);
        editText.clearFocus();
        assertThat(editText).isNotFocused();
        activity.findViewById(empty).performClick();
        assertThat(editText).isFocused();
    }

    @Test
    public void testGuidelines() {
        Shadows.shadowOf(activity).clickMenuItem(menu_guidelines);
        Assert.assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void testEmptyQuote() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PARENT_ID, "1");
        controller = Robolectric.buildActivity(ComposeActivity.class, intent).create().start().resume().visible();
        activity = controller.get();
        assertThat(((View) (activity.findViewById(quote)))).isNotVisible();
        Assert.assertFalse(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_quote).isVisible());
    }

    @Test
    public void testSendPromptToLogin() {
        doSend();
        replyCallback.getValue().onDone(false);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, LoginActivity.class);
    }

    @Test
    public void testSendSuccessful() {
        doSend();
        assertThat(Preferences.getDraft(activity, "1")).isNotEmpty();
        replyCallback.getValue().onDone(true);
        assertThat(activity).isFinishing();
        assertThat(Preferences.getDraft(activity, "1")).isNullOrEmpty();
    }

    @Test
    public void testSendFailed() {
        doSend();
        Assert.assertFalse(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_send).isEnabled());
        Assert.assertFalse(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_quote).isVisible());
        Assert.assertFalse(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_save_draft).isEnabled());
        Assert.assertFalse(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_discard_draft).isEnabled());
        replyCallback.getValue().onError(new IOException());
        Assert.assertTrue(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_send).isEnabled());
        Assert.assertTrue(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_quote).isVisible());
        Assert.assertTrue(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_save_draft).isEnabled());
        Assert.assertTrue(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_discard_draft).isEnabled());
        assertThat(activity).isNotFinishing();
        Assert.assertEquals(activity.getString(comment_failed), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testDelayedSuccessfulResponse() {
        doSend();
        Shadows.shadowOf(activity).clickMenuItem(home);
        assertThat(activity).isFinishing();
        replyCallback.getValue().onDone(true);
        Assert.assertEquals(activity.getString(comment_successful), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testDelayedFailedResponse() {
        doSend();
        Shadows.shadowOf(activity).clickMenuItem(home);
        replyCallback.getValue().onDone(false);
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testDelayedError() {
        doSend();
        Shadows.shadowOf(activity).clickMenuItem(home);
        replyCallback.getValue().onError(new IOException());
        Assert.assertEquals(activity.getString(comment_failed), ShadowToast.getTextOfLatestToast());
    }
}

