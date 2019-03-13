package io.github.hidroh.materialistic;


import FeedbackClient.Callback;
import R.id.button_rate;
import R.id.feedback_button;
import R.string.feedback_failed;
import R.string.feedback_sent;
import android.annotation.SuppressLint;
import io.github.hidroh.materialistic.data.FeedbackClient;
import io.github.hidroh.materialistic.test.TestRunner;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;


@SuppressWarnings("ConstantConditions")
@SuppressLint("SetTextI18n")
@RunWith(TestRunner.class)
public class FeedbackActivityTest {
    private ActivityController<FeedbackActivity> controller;

    private FeedbackActivity activity;

    @Inject
    FeedbackClient feedbackClient;

    @Captor
    ArgumentCaptor<FeedbackClient.Callback> callback;

    @Test
    public void testEmptyInput() {
        activity.findViewById(feedback_button).performClick();
        Assert.assertNotNull(getError());
        Assert.assertNotNull(getError());
        Mockito.verify(feedbackClient, Mockito.never()).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Callback.class));
        assertThat(activity).isNotFinishing();
        controller.pause().stop().destroy();
    }

    @Test
    public void testSuccessful() {
        setText("title");
        setText("body");
        activity.findViewById(feedback_button).performClick();
        Mockito.verify(feedbackClient).send(ArgumentMatchers.eq("title"), ArgumentMatchers.eq("body"), callback.capture());
        callback.getValue().onSent(true);
        assertThat(activity).isFinishing();
        Assert.assertEquals(activity.getString(feedback_sent), ShadowToast.getTextOfLatestToast());
        controller.pause().stop().destroy();
    }

    @Test
    public void testFailed() {
        setText("title");
        setText("body");
        activity.findViewById(feedback_button).performClick();
        Mockito.verify(feedbackClient).send(ArgumentMatchers.eq("title"), ArgumentMatchers.eq("body"), callback.capture());
        callback.getValue().onSent(false);
        assertThat(activity).isNotFinishing();
        Assert.assertEquals(activity.getString(feedback_failed), ShadowToast.getTextOfLatestToast());
        controller.pause().stop().destroy();
    }

    @Test
    public void testDismissBeforeResult() {
        setText("title");
        setText("body");
        activity.findViewById(feedback_button).performClick();
        Mockito.verify(feedbackClient).send(ArgumentMatchers.eq("title"), ArgumentMatchers.eq("body"), callback.capture());
        activity.finish();
        callback.getValue().onSent(true);
        controller.pause().stop().destroy();
    }

    @Test
    public void testFinishBeforeResult() {
        setText("title");
        setText("body");
        activity.findViewById(feedback_button).performClick();
        Mockito.verify(feedbackClient).send(ArgumentMatchers.eq("title"), ArgumentMatchers.eq("body"), callback.capture());
        controller.pause().stop().destroy();
        callback.getValue().onSent(true);
    }

    @Test
    public void testRate() {
        activity.findViewById(button_rate).performClick();
        Assert.assertNotNull(Shadows.shadowOf(activity).getNextStartedActivity());
        assertThat(activity).isFinishing();
    }
}

