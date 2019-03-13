package org.robolectric.shadows;


import android.widget.ProgressBar;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowProgressBarTest {
    private int[] testValues = new int[]{ 0, 1, 2, 100 };

    private ProgressBar progressBar;

    @Test
    public void shouldInitMaxTo100() {
        assertThat(progressBar.getMax()).isEqualTo(100);
    }

    @Test
    public void testMax() {
        for (int max : testValues) {
            progressBar.setMax(max);
            assertThat(progressBar.getMax()).isEqualTo(max);
        }
    }

    @Test
    public void testProgress() {
        for (int progress : testValues) {
            progressBar.setProgress(progress);
            assertThat(progressBar.getProgress()).isEqualTo(progress);
        }
    }

    @Test
    public void testSecondaryProgress() {
        for (int progress : testValues) {
            progressBar.setSecondaryProgress(progress);
            assertThat(progressBar.getSecondaryProgress()).isEqualTo(progress);
        }
    }

    @Test
    public void testIsDeterminate() throws Exception {
        Assert.assertFalse(progressBar.isIndeterminate());
        progressBar.setIndeterminate(true);
        Assert.assertTrue(progressBar.isIndeterminate());
    }

    @Test
    public void shouldReturnZeroAsProgressWhenIndeterminate() throws Exception {
        progressBar.setProgress(10);
        progressBar.setSecondaryProgress(20);
        progressBar.setIndeterminate(true);
        Assert.assertEquals(0, progressBar.getProgress());
        Assert.assertEquals(0, progressBar.getSecondaryProgress());
        progressBar.setIndeterminate(false);
        Assert.assertEquals(10, progressBar.getProgress());
        Assert.assertEquals(20, progressBar.getSecondaryProgress());
    }

    @Test
    public void shouldNotSetProgressWhenIndeterminate() throws Exception {
        progressBar.setIndeterminate(true);
        progressBar.setProgress(10);
        progressBar.setSecondaryProgress(20);
        progressBar.setIndeterminate(false);
        Assert.assertEquals(0, progressBar.getProgress());
        Assert.assertEquals(0, progressBar.getSecondaryProgress());
    }

    @Test
    public void testIncrementProgressBy() throws Exception {
        Assert.assertEquals(0, progressBar.getProgress());
        progressBar.incrementProgressBy(1);
        Assert.assertEquals(1, progressBar.getProgress());
        progressBar.incrementProgressBy(1);
        Assert.assertEquals(2, progressBar.getProgress());
        Assert.assertEquals(0, progressBar.getSecondaryProgress());
        progressBar.incrementSecondaryProgressBy(1);
        Assert.assertEquals(1, progressBar.getSecondaryProgress());
        progressBar.incrementSecondaryProgressBy(1);
        Assert.assertEquals(2, progressBar.getSecondaryProgress());
    }

    @Test
    public void shouldRespectMax() throws Exception {
        progressBar.setMax(20);
        progressBar.setProgress(50);
        Assert.assertEquals(20, progressBar.getProgress());
    }
}

