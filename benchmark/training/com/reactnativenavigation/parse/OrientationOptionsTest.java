package com.reactnativenavigation.parse;


import Orientation.Default;
import Orientation.Landscape;
import Orientation.Portrait;
import Orientation.PortraitLandscape.orientationCode;
import com.reactnativenavigation.BaseTest;
import org.junit.Test;


public class OrientationOptionsTest extends BaseTest {
    @Test
    public void parse() {
        OrientationOptions options = OrientationOptions.parse(create("default"));
        assertThat(options.orientations).hasSize(1);
    }

    @Test
    public void parseOrientations() {
        OrientationOptions options = OrientationOptions.parse(create("default", "landscape", "portrait"));
        assertThat(options.orientations.get(0)).isEqualTo(Default);
        assertThat(options.orientations.get(1)).isEqualTo(Landscape);
        assertThat(options.orientations.get(2)).isEqualTo(Portrait);
    }

    @Test
    public void parseSingleOrientation() {
        OrientationOptions options = OrientationOptions.parse(create("landscape"));
        assertThat(options.orientations.get(0)).isEqualTo(Landscape);
    }

    @Test
    public void landscapePortrait_regardedAsUserOrientation() {
        OrientationOptions options = OrientationOptions.parse(create("landscape", "portrait"));
        assertThat(options.getValue()).isEqualTo(orientationCode);
    }

    @Test
    public void portraitLandscape_regardedAsUserOrientation() {
        OrientationOptions options = OrientationOptions.parse(create("portrait", "landscape"));
        assertThat(options.getValue()).isEqualTo(orientationCode);
    }

    @Test
    public void unsupportedOrientationsAreIgnored() {
        OrientationOptions options = OrientationOptions.parse(create("default", "autoRotate"));
        assertThat(options.orientations).hasSize(1);
        assertThat(options.orientations.get(0)).isEqualTo(Default);
    }

    @Test
    public void getValue_returnsDefaultIfUndefined() {
        OrientationOptions options = new OrientationOptions();
        assertThat(options.getValue()).isEqualTo(Orientation.Default.orientationCode);
    }

    @Test
    public void hasValue_returnsFalseForOrientationDefault() {
        OrientationOptions options = OrientationOptions.parse(create("default"));
        assertThat(options.hasValue()).isFalse();
    }
}

