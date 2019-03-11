package pl.droidsonroids.gif;


import Build.VERSION_CODES;
import TypedValue.DENSITY_NONE;
import android.content.res.Resources;
import androidx.annotation.RequiresApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RequiresApi(VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(MockitoJUnitRunner.class)
public class GifViewUtilsDensityTest {
    @Mock
    Resources resources;

    @Test
    public void testHighResourceDensities() throws Exception {
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, DENSITY_HIGH), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, DENSITY_LOW), 0)).isEqualTo(0.5F);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, DENSITY_MEDIUM), 0)).isEqualTo((2.0F / 3));
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, DENSITY_XHIGH), 0)).isEqualTo((4.0F / 3));
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, DENSITY_XXHIGH), 0)).isEqualTo(2);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, DENSITY_XXXHIGH), 0)).isEqualTo((8.0F / 3));
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, DENSITY_TV), 0)).isEqualTo((213.0F / 240));
    }

    @Test
    public void testLowHighDensity() throws Exception {
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_LOW, DENSITY_HIGH), 0)).isEqualTo(2);
    }

    @Test
    public void testNoneResourceDensities() throws Exception {
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_NONE, DENSITY_LOW), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_NONE, DENSITY_MEDIUM), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_NONE, DENSITY_DEFAULT), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_NONE, DENSITY_TV), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_NONE, DENSITY_HIGH), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_NONE, DENSITY_XXHIGH), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_NONE, DENSITY_XXXHIGH), 0)).isEqualTo(1);
    }

    @Test
    public void testNoneDisplayDensities() throws Exception {
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_LOW, Bitmap.DENSITY_NONE), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_MEDIUM, Bitmap.DENSITY_NONE), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(TypedValue.DENSITY_DEFAULT, Bitmap.DENSITY_NONE), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, Bitmap.DENSITY_NONE), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_XXHIGH, Bitmap.DENSITY_NONE), 0)).isEqualTo(1);
    }

    @Test
    public void testInvalidDensities() throws Exception {
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, (-1)), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources((-1), DENSITY_HIGH), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources((-1), (-1)), 0)).isEqualTo(1);
        assertThat(GifViewUtils.getDensityScale(getMockedResources(DENSITY_HIGH, 0), 0)).isEqualTo(1);
    }
}

