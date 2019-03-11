/**
 * Copyright (C) 2016 The Android Open Source Project
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
package com.google.android.exoplayer2.text.ttml;


import Color.BLACK;
import android.graphics.Color;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link TtmlStyle}.
 */
@RunWith(RobolectricTestRunner.class)
public final class TtmlStyleTest {
    private static final String FONT_FAMILY = "serif";

    private static final String ID = "id";

    public static final int FOREGROUND_COLOR = Color.WHITE;

    public static final int BACKGROUND_COLOR = Color.BLACK;

    private TtmlStyle style;

    @Test
    public void testInheritStyle() {
        style.inherit(TtmlStyleTest.createAncestorStyle());
        assertWithMessage("id must not be inherited").that(style.getId()).isNull();
        assertThat(style.isUnderline()).isTrue();
        assertThat(style.isLinethrough()).isTrue();
        assertThat(style.getStyle()).isEqualTo(TtmlStyle.STYLE_BOLD_ITALIC);
        assertThat(style.getFontFamily()).isEqualTo(TtmlStyleTest.FONT_FAMILY);
        assertThat(style.getFontColor()).isEqualTo(WHITE);
        assertWithMessage("do not inherit backgroundColor").that(style.hasBackgroundColor()).isFalse();
    }

    @Test
    public void testChainStyle() {
        style.chain(TtmlStyleTest.createAncestorStyle());
        assertWithMessage("id must not be inherited").that(style.getId()).isNull();
        assertThat(style.isUnderline()).isTrue();
        assertThat(style.isLinethrough()).isTrue();
        assertThat(style.getStyle()).isEqualTo(TtmlStyle.STYLE_BOLD_ITALIC);
        assertThat(style.getFontFamily()).isEqualTo(TtmlStyleTest.FONT_FAMILY);
        assertThat(style.getFontColor()).isEqualTo(TtmlStyleTest.FOREGROUND_COLOR);
        // do inherit backgroundColor when chaining
        assertWithMessage("do not inherit backgroundColor when chaining").that(style.getBackgroundColor()).isEqualTo(TtmlStyleTest.BACKGROUND_COLOR);
    }

    @Test
    public void testStyle() {
        assertThat(style.getStyle()).isEqualTo(TtmlStyle.UNSPECIFIED);
        style.setItalic(true);
        assertThat(style.getStyle()).isEqualTo(TtmlStyle.STYLE_ITALIC);
        style.setBold(true);
        assertThat(style.getStyle()).isEqualTo(TtmlStyle.STYLE_BOLD_ITALIC);
        style.setItalic(false);
        assertThat(style.getStyle()).isEqualTo(TtmlStyle.STYLE_BOLD);
        style.setBold(false);
        assertThat(style.getStyle()).isEqualTo(TtmlStyle.STYLE_NORMAL);
    }

    @Test
    public void testLinethrough() {
        assertThat(style.isLinethrough()).isFalse();
        style.setLinethrough(true);
        assertThat(style.isLinethrough()).isTrue();
        style.setLinethrough(false);
        assertThat(style.isLinethrough()).isFalse();
    }

    @Test
    public void testUnderline() {
        assertThat(style.isUnderline()).isFalse();
        style.setUnderline(true);
        assertThat(style.isUnderline()).isTrue();
        style.setUnderline(false);
        assertThat(style.isUnderline()).isFalse();
    }

    @Test
    public void testFontFamily() {
        assertThat(style.getFontFamily()).isNull();
        style.setFontFamily(TtmlStyleTest.FONT_FAMILY);
        assertThat(style.getFontFamily()).isEqualTo(TtmlStyleTest.FONT_FAMILY);
        style.setFontFamily(null);
        assertThat(style.getFontFamily()).isNull();
    }

    @Test
    public void testColor() {
        assertThat(style.hasFontColor()).isFalse();
        style.setFontColor(BLACK);
        assertThat(style.getFontColor()).isEqualTo(BLACK);
        assertThat(style.hasFontColor()).isTrue();
    }

    @Test
    public void testBackgroundColor() {
        assertThat(style.hasBackgroundColor()).isFalse();
        style.setBackgroundColor(BLACK);
        assertThat(style.getBackgroundColor()).isEqualTo(BLACK);
        assertThat(style.hasBackgroundColor()).isTrue();
    }

    @Test
    public void testId() {
        assertThat(style.getId()).isNull();
        style.setId(TtmlStyleTest.ID);
        assertThat(style.getId()).isEqualTo(TtmlStyleTest.ID);
        style.setId(null);
        assertThat(style.getId()).isNull();
    }
}

