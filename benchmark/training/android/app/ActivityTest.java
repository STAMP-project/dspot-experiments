package android.app;


import R.id.button;
import R.style;
import R.style.Theme_Robolectric;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;

import static ActivityWithAnotherTheme.setThemeBeforeContentView;


@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class ActivityTest {
    @Rule
    public ActivityTestRule<ActivityWithAnotherTheme> activityWithAnotherThemeRule = new ActivityTestRule(ActivityWithAnotherTheme.class, false, false);

    @Rule
    public ActivityTestRule<ActivityWithoutTheme> activityWithoutThemeRule = new ActivityTestRule(ActivityWithoutTheme.class, false, false);

    @Test
    public void whenSetOnActivityInManifest_activityGetsThemeFromActivityInManifest() throws Exception {
        Activity activity = activityWithAnotherThemeRule.launchActivity(null);
        Button theButton = activity.findViewById(button);
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-65536);
    }

    @Test
    public void whenExplicitlySetOnActivity_afterSetContentView_activityGetsThemeFromActivityInManifest() throws Exception {
        Activity activity = activityWithAnotherThemeRule.launchActivity(null);
        activity.setTheme(Theme_Robolectric);
        Button theButton = activity.findViewById(button);
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-65536);
    }

    @Test
    public void whenExplicitlySetOnActivity_beforeSetContentView_activityUsesNewTheme() throws Exception {
        setThemeBeforeContentView = style.Theme_Robolectric;
        Activity activity = activityWithAnotherThemeRule.launchActivity(null);
        Button theButton = activity.findViewById(button);
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-16711936);
    }

    @Test
    public void whenNotSetOnActivityInManifest_activityGetsThemeFromApplicationInManifest() throws Exception {
        Activity activity = activityWithoutThemeRule.launchActivity(null);
        Button theButton = activity.findViewById(button);
        ColorDrawable background = ((ColorDrawable) (theButton.getBackground()));
        assertThat(background.getColor()).isEqualTo(-16711936);
    }
}

