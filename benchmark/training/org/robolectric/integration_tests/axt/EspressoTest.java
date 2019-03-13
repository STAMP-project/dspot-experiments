package org.robolectric.integration_tests.axt;


import R.id.button;
import R.id.text;
import android.widget.Button;
import android.widget.EditText;
import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Simple tests to verify espresso APIs can be used on both Robolectric and device.
 */
@RunWith(AndroidJUnit4.class)
public final class EspressoTest {
    @Rule
    public ActivityTestRule<EspressoActivity> activityRule = new ActivityTestRule(EspressoActivity.class, false, true);

    @Test
    public void onIdle_doesnt_block() throws Exception {
        Espresso.onIdle();
    }

    @Test
    public void launchActivityAndFindView_ById() throws Exception {
        EspressoActivity activity = activityRule.getActivity();
        EditText editText = activity.findViewById(text);
        assertThat(editText).isNotNull();
        assertThat(editText.isEnabled()).isTrue();
    }

    /**
     * Perform the equivalent of launchActivityAndFindView_ById except using espresso APIs
     */
    @Test
    public void launchActivityAndFindView_espresso() throws Exception {
        onView(withId(text)).check(matches(isCompletelyDisplayed()));
    }

    /**
     * Perform the 'traditional' mechanism of clicking a button in Robolectric using findViewById
     */
    @Test
    @UiThreadTest
    public void buttonClick() throws Exception {
        EspressoActivity activity = activityRule.getActivity();
        Button button = activity.findViewById(button);
        button.performClick();
        assertThat(activity.buttonClicked).isTrue();
    }

    /**
     * Perform the equivalent of click except using espresso APIs
     */
    @Test
    public void buttonClick_espresso() throws Exception {
        EspressoActivity activity = activityRule.getActivity();
        onView(withId(button)).perform(click());
        assertThat(activity.buttonClicked).isTrue();
    }

    /**
     * Perform the 'traditional' mechanism of setting contents of a text view using findViewById
     */
    @Test
    @UiThreadTest
    public void typeText_entersText() throws Exception {
        EspressoActivity activity = activityRule.getActivity();
        EditText editText = activity.findViewById(text);
        editText.setText("\"new TEXT!#$%&\'*+-/=?^_`{|}~@robolectric.org");
        assertThat(editText.getText().toString()).isEqualTo("\"new TEXT!#$%&\'*+-/=?^_`{|}~@robolectric.org");
    }

    /**
     * Perform the equivalent of setText except using espresso APIs
     */
    @Test
    public void typeText_espresso() throws Exception {
        onView(withId(text)).perform(typeText("\"new TEXT!#$%&\'*+-/=?^_`{|}~@robolectric.org"));
        onView(withId(text)).check(matches(withText("\"new TEXT!#$%&\'*+-/=?^_`{|}~@robolectric.org")));
    }

    @Test
    public void changeText_withCloseSoftKeyboard() {
        // Type text and then press the button.
        onView(withId(text)).perform(typeText("anything"), closeSoftKeyboard());
        // Check that the text was changed.
        onView(withId(text)).check(matches(withText("anything")));
    }
}

