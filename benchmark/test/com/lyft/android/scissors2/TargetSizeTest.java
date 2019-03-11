package com.lyft.android.scissors2;


import android.graphics.Rect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TargetSizeTest {
    final Rect viewport;

    final String orientation;

    public TargetSizeTest(int viewportW, int viewportH, String orientation) {
        this.viewport = new Rect(0, 0, viewportW, viewportH);
        this.orientation = orientation;
    }

    @Test
    public void targetFitsViewport() {
        final int sourceW = viewport.width();
        final int sourceH = viewport.height();
        Rect target = CropViewExtensions.computeTargetSize(sourceW, sourceH, viewport.width(), viewport.height());
        assertThat(target.width()).isEqualTo(viewport.width());
        assertThat(target.height()).isEqualTo(viewport.height());
    }

    @Test
    public void targetScalesUpToViewport() {
        final int sourceW = (viewport.width()) - 11;
        final int sourceH = (viewport.height()) - 11;
        Rect target = CropViewExtensions.computeTargetSize(sourceW, sourceH, viewport.width(), viewport.height());
        switch (orientation) {
            case TargetSizeTest.LANDSCAPE :
                assertThat(target.width()).isGreaterThan(viewport.width());
                assertThat(target.height()).isEqualTo(viewport.height());
                break;
            case TargetSizeTest.PORTRAIT :
                assertThat(target.width()).isEqualTo(viewport.width());
                assertThat(target.height()).isGreaterThan(viewport.height());
                break;
            case TargetSizeTest.SQUARED :
                assertThat(target.width()).isEqualTo(viewport.width());
                assertThat(target.height()).isEqualTo(viewport.height());
                break;
            default :
                fail(("Unexpected orientation " + (orientation)));
        }
    }

    @Test
    public void targetScalesDownIfBiggerThanViewport() {
        final int sourceW = (viewport.width()) + 101;
        final int sourceH = (viewport.height()) + 101;
        Rect target = CropViewExtensions.computeTargetSize(sourceW, sourceH, viewport.width(), viewport.height());
        switch (orientation) {
            case TargetSizeTest.LANDSCAPE :
                assertThat(target.width()).isEqualTo(viewport.width());
                assertThat(target.height()).isGreaterThan(viewport.height());
                break;
            case TargetSizeTest.PORTRAIT :
                assertThat(target.width()).isGreaterThan(viewport.width());
                assertThat(target.height()).isEqualTo(viewport.height());
                break;
            case TargetSizeTest.SQUARED :
                assertThat(target.width()).isEqualTo(viewport.width());
                assertThat(target.height()).isEqualTo(viewport.height());
                break;
            default :
                fail(("Unexpected orientation " + (orientation)));
        }
    }

    static final String SQUARED = "Squared";

    static final String LANDSCAPE = "Landscape";

    static final String PORTRAIT = "Portrait";
}

