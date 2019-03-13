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


import Color.YELLOW;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link TtmlRenderUtil}.
 */
@RunWith(RobolectricTestRunner.class)
public final class TtmlRenderUtilTest {
    @Test
    public void testResolveStyleNoStyleAtAll() {
        assertThat(TtmlRenderUtil.resolveStyle(null, null, null)).isNull();
    }

    @Test
    public void testResolveStyleSingleReferentialStyle() {
        Map<String, TtmlStyle> globalStyles = TtmlRenderUtilTest.getGlobalStyles();
        String[] styleIds = new String[]{ "s0" };
        assertThat(TtmlRenderUtil.resolveStyle(null, styleIds, globalStyles)).isSameAs(globalStyles.get("s0"));
    }

    @Test
    public void testResolveStyleMultipleReferentialStyles() {
        Map<String, TtmlStyle> globalStyles = TtmlRenderUtilTest.getGlobalStyles();
        String[] styleIds = new String[]{ "s0", "s1" };
        TtmlStyle resolved = TtmlRenderUtil.resolveStyle(null, styleIds, globalStyles);
        assertThat(resolved).isNotSameAs(globalStyles.get("s0"));
        assertThat(resolved).isNotSameAs(globalStyles.get("s1"));
        assertThat(resolved.getId()).isNull();
        // inherited from s0
        assertThat(resolved.getBackgroundColor()).isEqualTo(BLACK);
        // inherited from s1
        assertThat(resolved.getFontColor()).isEqualTo(RED);
        // merged from s0 and s1
        assertThat(resolved.getStyle()).isEqualTo(TtmlStyle.STYLE_BOLD_ITALIC);
    }

    @Test
    public void testResolveMergeSingleReferentialStyleIntoInlineStyle() {
        Map<String, TtmlStyle> globalStyles = TtmlRenderUtilTest.getGlobalStyles();
        String[] styleIds = new String[]{ "s0" };
        TtmlStyle style = new TtmlStyle();
        style.setBackgroundColor(YELLOW);
        TtmlStyle resolved = TtmlRenderUtil.resolveStyle(style, styleIds, globalStyles);
        assertThat(resolved).isSameAs(style);
        // inline attribute not overridden
        assertThat(resolved.getBackgroundColor()).isEqualTo(YELLOW);
        // inherited from referential style
        assertThat(resolved.getStyle()).isEqualTo(TtmlStyle.STYLE_BOLD);
    }

    @Test
    public void testResolveMergeMultipleReferentialStylesIntoInlineStyle() {
        Map<String, TtmlStyle> globalStyles = TtmlRenderUtilTest.getGlobalStyles();
        String[] styleIds = new String[]{ "s0", "s1" };
        TtmlStyle style = new TtmlStyle();
        style.setBackgroundColor(YELLOW);
        TtmlStyle resolved = TtmlRenderUtil.resolveStyle(style, styleIds, globalStyles);
        assertThat(resolved).isSameAs(style);
        // inline attribute not overridden
        assertThat(resolved.getBackgroundColor()).isEqualTo(YELLOW);
        // inherited from both referential style
        assertThat(resolved.getStyle()).isEqualTo(TtmlStyle.STYLE_BOLD_ITALIC);
    }

    @Test
    public void testResolveStyleOnlyInlineStyle() {
        TtmlStyle inlineStyle = new TtmlStyle();
        assertThat(TtmlRenderUtil.resolveStyle(inlineStyle, null, null)).isSameAs(inlineStyle);
    }
}

