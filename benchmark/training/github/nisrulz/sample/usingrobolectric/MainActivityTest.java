package github.nisrulz.sample.usingrobolectric;


import Build.VERSION_CODES;
import R.id.nameentry;
import R.id.textview;
import R.string.HINT_BTN_TEXT;
import R.string.HINT_TEXT;
import R.string.LOGIN_BTN_TEXT;
import R.string.NAME_ENTRY_HINT;
import R.string.Welcome_text;
import R.string.app_name;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import github.nisrulz.sample.usingrobolectric.support.ResourceLocator;
import github.nisrulz.sample.usingrobolectric.support.ViewLocator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static github.nisrulz.sample.usingrobolectric.support.Assert.assertViewIsVisible;
import static junit.framework.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = VERSION_CODES.LOLLIPOP)
public class MainActivityTest {
    private MainActivity activity;

    private Button hintbutton;

    private Button loginButton;

    @Test
    public void shouldNotBeNull() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void shouldHaveTitle() throws Exception {
        Assert.assertThat(activity.getTitle().toString(), CoreMatchers.equalTo(ResourceLocator.getString(app_name)));
    }

    @Test
    public void shouldHaveWelcomeText() throws Exception {
        TextView textView = ViewLocator.getTextView(activity, textview);
        assertViewIsVisible(textView);
        Assert.assertThat(textView.getText().toString(), CoreMatchers.equalTo(ResourceLocator.getString(Welcome_text)));
    }

    @Test
    public void shouldHaveNameEntry() throws Exception {
        EditText nameEntry = ViewLocator.getEditText(activity, nameentry);
        assertViewIsVisible(nameEntry);
        Assert.assertThat(nameEntry.getHint().toString(), CoreMatchers.equalTo(ResourceLocator.getString(NAME_ENTRY_HINT)));
    }

    @Test
    public void shouldHaveLoginButton() throws Exception {
        assertViewIsVisible(loginButton);
        Assert.assertThat(loginButton.getText().toString(), CoreMatchers.equalTo(ResourceLocator.getString(LOGIN_BTN_TEXT)));
    }

    @Test
    public void shouldHaveHintButton() throws Exception {
        assertViewIsVisible(hintbutton);
        Assert.assertThat(hintbutton.getText().toString(), CoreMatchers.equalTo(ResourceLocator.getString(HINT_BTN_TEXT)));
    }

    @Test
    public void shouldShowHintWhenClicked() throws Exception {
        hintbutton.performClick();
        Assert.assertThat(getTextOfLatestToast(), CoreMatchers.equalTo(ResourceLocator.getString(HINT_TEXT)));
    }

    @Test
    public void shouldLoginWhenClicked() throws Exception {
        loginButton.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent intent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(intent);
        assertEquals(shadowIntent.getIntentClass().getSimpleName(), SecondActivity.class.getSimpleName());
    }
}

