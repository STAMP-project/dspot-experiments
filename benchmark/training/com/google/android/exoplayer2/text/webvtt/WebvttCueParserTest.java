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
package com.google.android.exoplayer2.text.webvtt;


import android.graphics.Typeface;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link WebvttCueParser}.
 */
@RunWith(RobolectricTestRunner.class)
public final class WebvttCueParserTest {
    @Test
    public void testParseStrictValidClassesAndTrailingTokens() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText(("<v.first.loud Esme>" + "This <u.style1.style2 some stuff>is</u> text with <b.foo><i.bar>html</i></b> tags"));
        assertThat(text.toString()).isEqualTo("This is text with html tags");
        UnderlineSpan[] underlineSpans = WebvttCueParserTest.getSpans(text, UnderlineSpan.class);
        StyleSpan[] styleSpans = WebvttCueParserTest.getSpans(text, StyleSpan.class);
        assertThat(underlineSpans).hasLength(1);
        assertThat(styleSpans).hasLength(2);
        assertThat(styleSpans[0].getStyle()).isEqualTo(ITALIC);
        assertThat(styleSpans[1].getStyle()).isEqualTo(BOLD);
        assertThat(text.getSpanStart(underlineSpans[0])).isEqualTo(5);
        assertThat(text.getSpanEnd(underlineSpans[0])).isEqualTo(7);
        assertThat(text.getSpanStart(styleSpans[0])).isEqualTo(18);
        assertThat(text.getSpanStart(styleSpans[1])).isEqualTo(18);
        assertThat(text.getSpanEnd(styleSpans[0])).isEqualTo(22);
        assertThat(text.getSpanEnd(styleSpans[1])).isEqualTo(22);
    }

    @Test
    public void testParseStrictValidUnsupportedTagsStrippedOut() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText(("<v.first.loud Esme>This <unsupported>is</unsupported> text with " + "<notsupp><invalid>html</invalid></notsupp> tags"));
        assertThat(text.toString()).isEqualTo("This is text with html tags");
        assertThat(WebvttCueParserTest.getSpans(text, UnderlineSpan.class)).hasLength(0);
        assertThat(WebvttCueParserTest.getSpans(text, StyleSpan.class)).hasLength(0);
    }

    @Test
    public void testParseWellFormedUnclosedEndAtCueEnd() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText(("An <u some trailing stuff>unclosed u tag with " + "<i>italic</i> inside"));
        assertThat(text.toString()).isEqualTo("An unclosed u tag with italic inside");
        UnderlineSpan[] underlineSpans = WebvttCueParserTest.getSpans(text, UnderlineSpan.class);
        StyleSpan[] styleSpans = WebvttCueParserTest.getSpans(text, StyleSpan.class);
        assertThat(underlineSpans).hasLength(1);
        assertThat(styleSpans).hasLength(1);
        assertThat(styleSpans[0].getStyle()).isEqualTo(ITALIC);
        assertThat(text.getSpanStart(underlineSpans[0])).isEqualTo(3);
        assertThat(text.getSpanStart(styleSpans[0])).isEqualTo(23);
        assertThat(text.getSpanEnd(styleSpans[0])).isEqualTo(29);
        assertThat(text.getSpanEnd(underlineSpans[0])).isEqualTo(36);
    }

    @Test
    public void testParseWellFormedUnclosedEndAtParent() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("An unclosed u tag with <i><u>underline and italic</i> inside");
        assertThat(text.toString()).isEqualTo("An unclosed u tag with underline and italic inside");
        UnderlineSpan[] underlineSpans = WebvttCueParserTest.getSpans(text, UnderlineSpan.class);
        StyleSpan[] styleSpans = WebvttCueParserTest.getSpans(text, StyleSpan.class);
        assertThat(underlineSpans).hasLength(1);
        assertThat(styleSpans).hasLength(1);
        assertThat(text.getSpanStart(underlineSpans[0])).isEqualTo(23);
        assertThat(text.getSpanStart(styleSpans[0])).isEqualTo(23);
        assertThat(text.getSpanEnd(underlineSpans[0])).isEqualTo(43);
        assertThat(text.getSpanEnd(styleSpans[0])).isEqualTo(43);
        assertThat(styleSpans[0].getStyle()).isEqualTo(ITALIC);
    }

    @Test
    public void testParseMalformedNestedElements() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("<b><u>An unclosed u tag with <i>italic</u> inside</i></b>");
        assertThat(text.toString()).isEqualTo("An unclosed u tag with italic inside");
        UnderlineSpan[] underlineSpans = WebvttCueParserTest.getSpans(text, UnderlineSpan.class);
        StyleSpan[] styleSpans = WebvttCueParserTest.getSpans(text, StyleSpan.class);
        assertThat(underlineSpans).hasLength(1);
        assertThat(styleSpans).hasLength(2);
        // all tags applied until matching start tag found
        assertThat(text.getSpanStart(underlineSpans[0])).isEqualTo(0);
        assertThat(text.getSpanEnd(underlineSpans[0])).isEqualTo(29);
        if ((styleSpans[0].getStyle()) == (Typeface.BOLD)) {
            assertThat(text.getSpanStart(styleSpans[0])).isEqualTo(0);
            assertThat(text.getSpanStart(styleSpans[1])).isEqualTo(23);
            assertThat(text.getSpanEnd(styleSpans[1])).isEqualTo(29);
            assertThat(text.getSpanEnd(styleSpans[0])).isEqualTo(36);
        } else {
            assertThat(text.getSpanStart(styleSpans[1])).isEqualTo(0);
            assertThat(text.getSpanStart(styleSpans[0])).isEqualTo(23);
            assertThat(text.getSpanEnd(styleSpans[0])).isEqualTo(29);
            assertThat(text.getSpanEnd(styleSpans[1])).isEqualTo(36);
        }
    }

    @Test
    public void testParseCloseNonExistingTag() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("blah<b>blah</i>blah</b>blah");
        assertThat(text.toString()).isEqualTo("blahblahblahblah");
        StyleSpan[] spans = WebvttCueParserTest.getSpans(text, StyleSpan.class);
        assertThat(spans).hasLength(1);
        assertThat(spans[0].getStyle()).isEqualTo(BOLD);
        assertThat(text.getSpanStart(spans[0])).isEqualTo(4);
        assertThat(text.getSpanEnd(spans[0])).isEqualTo(8);// should be 12 when valid

    }

    @Test
    public void testParseEmptyTagName() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("An unclosed u tag with <>italic inside");
        assertThat(text.toString()).isEqualTo("An unclosed u tag with italic inside");
    }

    @Test
    public void testParseEntities() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("&amp; &gt; &lt; &nbsp;");
        assertThat(text.toString()).isEqualTo("& > <  ");
    }

    @Test
    public void testParseEntitiesUnsupported() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("&noway; &sure;");
        assertThat(text.toString()).isEqualTo(" ");
    }

    @Test
    public void testParseEntitiesNotTerminated() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("&amp here comes text");
        assertThat(text.toString()).isEqualTo("& here comes text");
    }

    @Test
    public void testParseEntitiesNotTerminatedUnsupported() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("&surenot here comes text");
        assertThat(text.toString()).isEqualTo(" here comes text");
    }

    @Test
    public void testParseEntitiesNotTerminatedNoSpace() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("&surenot");
        assertThat(text.toString()).isEqualTo("&surenot");
    }

    @Test
    public void testParseVoidTag() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText("here comes<br/> text<br/>");
        assertThat(text.toString()).isEqualTo("here comes text");
    }

    @Test
    public void testParseMultipleTagsOfSameKind() {
        Spanned text = WebvttCueParserTest.parseCueText("blah <b>blah</b> blah <b>foo</b>");
        assertThat(text.toString()).isEqualTo("blah blah blah foo");
        StyleSpan[] spans = WebvttCueParserTest.getSpans(text, StyleSpan.class);
        assertThat(spans).hasLength(2);
        assertThat(text.getSpanStart(spans[0])).isEqualTo(5);
        assertThat(text.getSpanEnd(spans[0])).isEqualTo(9);
        assertThat(text.getSpanStart(spans[1])).isEqualTo(15);
        assertThat(text.getSpanEnd(spans[1])).isEqualTo(18);
        assertThat(spans[0].getStyle()).isEqualTo(BOLD);
        assertThat(spans[1].getStyle()).isEqualTo(BOLD);
    }

    @Test
    public void testParseInvalidVoidSlash() {
        Spanned text = WebvttCueParserTest.parseCueText("blah <b/.st1.st2 trailing stuff> blah");
        assertThat(text.toString()).isEqualTo("blah  blah");
        StyleSpan[] spans = WebvttCueParserTest.getSpans(text, StyleSpan.class);
        assertThat(spans).hasLength(0);
    }

    @Test
    public void testParseMonkey() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText(("< u>An unclosed u tag with <<<<< i>italic</u></u></u></u    >" + "</i><u><u> inside"));
        assertThat(text.toString()).isEqualTo("An unclosed u tag with italic inside");
        text = WebvttCueParserTest.parseCueText((">>>>>>>>>An unclosed u tag with <<<<< italic</u></u></u>" + "</u  ></i><u><u> inside"));
        assertThat(text.toString()).isEqualTo(">>>>>>>>>An unclosed u tag with  inside");
    }

    @Test
    public void testParseCornerCases() throws Exception {
        Spanned text = WebvttCueParserTest.parseCueText(">");
        assertThat(text.toString()).isEqualTo(">");
        text = WebvttCueParserTest.parseCueText("<");
        assertThat(text.toString()).isEmpty();
        text = WebvttCueParserTest.parseCueText("<b.st1.st2 annotation");
        assertThat(text.toString()).isEmpty();
        text = WebvttCueParserTest.parseCueText("<<<<<<<<<<<<<<<<");
        assertThat(text.toString()).isEmpty();
        text = WebvttCueParserTest.parseCueText("<<<<<<>><<<<<<<<<<");
        assertThat(text.toString()).isEqualTo(">");
        text = WebvttCueParserTest.parseCueText("<>");
        assertThat(text.toString()).isEmpty();
        text = WebvttCueParserTest.parseCueText("&");
        assertThat(text.toString()).isEqualTo("&");
        text = WebvttCueParserTest.parseCueText("&&&&&&&");
        assertThat(text.toString()).isEqualTo("&&&&&&&");
    }
}

