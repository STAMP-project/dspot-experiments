/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot;


import AnsiBackground.BLACK;
import AnsiColor.BRIGHT_WHITE;
import AnsiColor.DEFAULT;
import AnsiOutput.Enabled.NEVER;
import org.junit.Test;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;


/**
 * Tests for {@link ImageBanner}.
 *
 * @author Craig Burke
 * @author Phillip Webb
 */
public class ImageBannerTests {
    private static final char HIGH_LUMINANCE_CHARACTER = ' ';

    private static final char LOW_LUMINANCE_CHARACTER = '@';

    private static final String INVERT_TRUE = "spring.banner.image.invert=true";

    @Test
    public void printBannerShouldResetForegroundAndBackground() {
        String banner = printBanner("black-and-white.gif");
        String expected = (AnsiOutput.encode(DEFAULT)) + (AnsiOutput.encode(AnsiBackground.DEFAULT));
        assertThat(banner).startsWith(expected);
    }

    @Test
    public void printBannerWhenInvertedShouldResetForegroundAndBackground() {
        String banner = printBanner("black-and-white.gif", ImageBannerTests.INVERT_TRUE);
        String expected = (AnsiOutput.encode(DEFAULT)) + (AnsiOutput.encode(BLACK));
        assertThat(banner).startsWith(expected);
    }

    @Test
    public void printBannerShouldPrintWhiteAsBrightWhiteHighLuminance() {
        String banner = printBanner("black-and-white.gif");
        String expected = (AnsiOutput.encode(BRIGHT_WHITE)) + (ImageBannerTests.HIGH_LUMINANCE_CHARACTER);
        assertThat(banner).contains(expected);
    }

    @Test
    public void printBannerWhenInvertedShouldPrintWhiteAsBrightWhiteLowLuminance() {
        String banner = printBanner("black-and-white.gif", ImageBannerTests.INVERT_TRUE);
        String expected = (AnsiOutput.encode(BRIGHT_WHITE)) + (ImageBannerTests.LOW_LUMINANCE_CHARACTER);
        assertThat(banner).contains(expected);
    }

    @Test
    public void printBannerShouldPrintBlackAsBlackLowLuminance() {
        String banner = printBanner("black-and-white.gif");
        String expected = (AnsiOutput.encode(AnsiColor.BLACK)) + (ImageBannerTests.LOW_LUMINANCE_CHARACTER);
        assertThat(banner).contains(expected);
    }

    @Test
    public void printBannerWhenInvertedShouldPrintBlackAsBlackHighLuminance() {
        String banner = printBanner("black-and-white.gif", ImageBannerTests.INVERT_TRUE);
        String expected = (AnsiOutput.encode(AnsiColor.BLACK)) + (ImageBannerTests.HIGH_LUMINANCE_CHARACTER);
        assertThat(banner).contains(expected);
    }

    @Test
    public void printBannerWhenShouldPrintAllColors() {
        String banner = printBanner("colors.gif");
        for (AnsiColor color : AnsiColor.values()) {
            if (color != (AnsiColor.DEFAULT)) {
                assertThat(banner).contains(AnsiOutput.encode(color));
            }
        }
    }

    @Test
    public void printBannerShouldRenderGradient() {
        AnsiOutput.setEnabled(NEVER);
        String banner = printBanner("gradient.gif", "spring.banner.image.width=10", "spring.banner.image.margin=0");
        assertThat(banner).contains("@#8&o:*.  ");
    }

    @Test
    public void printBannerShouldCalculateHeight() {
        String banner = printBanner("large.gif", "spring.banner.image.width=20");
        assertThat(getBannerHeight(banner)).isEqualTo(10);
    }

    @Test
    public void printBannerWhenHasHeightPropertyShouldSetHeight() {
        String banner = printBanner("large.gif", "spring.banner.image.width=20", "spring.banner.image.height=30");
        assertThat(getBannerHeight(banner)).isEqualTo(30);
    }

    @Test
    public void printBannerShouldCapWidthAndCalculateHeight() {
        AnsiOutput.setEnabled(NEVER);
        String banner = printBanner("large.gif", "spring.banner.image.margin=0");
        assertThat(getBannerWidth(banner)).isEqualTo(76);
        assertThat(getBannerHeight(banner)).isEqualTo(37);
    }

    @Test
    public void printBannerShouldPrintMargin() {
        AnsiOutput.setEnabled(NEVER);
        String banner = printBanner("large.gif");
        String[] lines = banner.split(System.lineSeparator());
        for (int i = 2; i < ((lines.length) - 1); i++) {
            assertThat(lines[i]).startsWith("  @");
        }
    }

    @Test
    public void printBannerWhenHasMarginPropertyShouldPrintSizedMargin() {
        AnsiOutput.setEnabled(NEVER);
        String banner = printBanner("large.gif", "spring.banner.image.margin=4");
        String[] lines = banner.split(System.lineSeparator());
        for (int i = 2; i < ((lines.length) - 1); i++) {
            assertThat(lines[i]).startsWith("    @");
        }
    }

    @Test
    public void printBannerWhenAnimatesShouldPrintAllFrames() {
        AnsiOutput.setEnabled(NEVER);
        String banner = printBanner("animated.gif");
        String[] lines = banner.split(System.lineSeparator());
        int frames = 138;
        int linesPerFrame = 36;
        assertThat(banner).contains("\r");
        assertThat(lines.length).isEqualTo(((frames * linesPerFrame) - 1));
    }
}

