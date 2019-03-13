package org.robolectric.shadows;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Robolectric test for {@link ShadowJobService}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowJobServiceTest {
    private JobService jobService;

    @Mock
    private JobParameters params;

    @Test
    public void jobFinishedInitiallyFalse() {
        assertThat(Shadows.shadowOf(jobService).getIsJobFinished()).isFalse();
    }

    @Test
    public void jobIsRescheduleNeededInitiallyFalse() {
        assertThat(Shadows.shadowOf(jobService).getIsRescheduleNeeded()).isFalse();
    }

    @Test
    public void jobFinished_updatesFieldsCorrectly() {
        /* wantsReschedule */
        jobService.jobFinished(params, true);
        ShadowJobService shadow = Shadows.shadowOf(jobService);
        assertThat(shadow.getIsRescheduleNeeded()).isTrue();
        assertThat(shadow.getIsJobFinished()).isTrue();
    }
}

