/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.wiki.web.internal.translator;


import MediaWikiToCreoleTranslator.TABLE_OF_CONTENTS;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static MediaWikiToCreoleTranslator.TABLE_OF_CONTENTS;


/**
 *
 *
 * @author Jorge Ferrer
 */
public class MediaWikiToCreoleTranslatorTest {
    @Test
    public void testAngleBracketsUnscape() throws Exception {
        String content = "&lt;div&gt;";
        String expected = "<div>";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBold() throws Exception {
        String content = "This is '''bold'''.";
        String expected = "This is **bold**.";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBoldItalics() throws Exception {
        String content = "This is ''''bold and italics''''.";
        String expected = "This is **//bold and italics//**.";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCleanUnnecessaryHeaderEmphasis1() throws Exception {
        String content = "== '''title''' ==";
        String expected = "== title ==";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCleanUnnecessaryHeaderEmphasis2() throws Exception {
        String content = "== '''title''' ==";
        String expected = "== title ==";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCleanUnnecessaryHeaderEmphasis3() throws Exception {
        String content = "=== '''title''' ===";
        String expected = "=== title ===";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHeader1() throws Exception {
        String content = "= Header 1 =";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHeader1WithoutSpaces() throws Exception {
        String content = "=Header 1=";
        String expected = "=Header 1=";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHeaders1() throws Exception {
        String content = "= Header 1 =\n== Header 2 ==";
        String expected = (TABLE_OF_CONTENTS) + "== Header 1 ==\n=== Header 2 ===";
        String actual = _mediaWikiToCreoleTranslator.postProcess(_translate(content));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHeaders2() throws Exception {
        String content = "== Header 1 ==\n=== Header 2 ===";
        String expected = (TABLE_OF_CONTENTS) + content;
        String actual = _mediaWikiToCreoleTranslator.postProcess(_translate(content));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHorizontalRule() throws Exception {
        String content = "\n----";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHtmlPre() throws Exception {
        String content = "previous line\n<pre>\nmonospace\nsecond line\n</pre>\nnext line";
        String expected = "previous line\n{{{\nmonospace\nsecond line\n}}}\nnext line";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testImage() throws Exception {
        String content = "test1 [[Image:Sample1.png]] test2 [[Image:Sample2.png]] test3 " + "[[Image:Sample3.png]] test4";
        StringBundler sb = new StringBundler(4);
        sb.append(TABLE_OF_CONTENTS);
        sb.append("test1 {{SharedImages/sample1.png}} test2 ");
        sb.append("{{SharedImages/sample2.png}} test3 ");
        sb.append("{{SharedImages/sample3.png}} test4");
        Assert.assertEquals(sb.toString(), _mediaWikiToCreoleTranslator.postProcess(_translate(content)));
    }

    @Test
    public void testIndentedParagraph() throws Exception {
        String content = "\t:\tparagraph";
        String expected = "paragraph";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testItalics() throws Exception {
        String content = "This is ''italics''.";
        String expected = "This is //italics//.";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testLinkWithLabel() throws Exception {
        String content = "[[Link|This is the label]]";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testLinkWithUnderscores() throws Exception {
        String content = "[[Link_With_Underscores]]";
        String expected = (TABLE_OF_CONTENTS) + "[[Link With Underscores]]";
        String actual = _mediaWikiToCreoleTranslator.postProcess(_translate(content));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testListItem() throws Exception {
        String content = "* item";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testListSubitem() throws Exception {
        String content = "** subitem";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testListSubsubitem() throws Exception {
        String content = "*** subsubitem";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMonospace() throws Exception {
        String content = "previous line\n monospace\nnext line";
        String expected = "previous line\n{{{\n monospace\n}}}\nnext line";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMultilinePre() throws Exception {
        String content = "previous line\n monospace\n second line\nnext line";
        String expected = "previous line\n{{{\n monospace\n second line\n}}}\nnext line";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNotListItem() throws Exception {
        String content = "\t*item";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNowiki() throws Exception {
        String content = "previous line\n<pre>\nmonospace\nsecond line\n</pre>\nnext line";
        String expected = "previous line\n{{{\nmonospace\nsecond line\n}}}\nnext line";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNowikiWithFormat() throws Exception {
        DigesterUtil digesterUtil = new DigesterUtil();
        Digester digester = Mockito.mock(Digester.class);
        Mockito.when(digester.digest(Mockito.anyString())).thenReturn(RandomTestUtil.randomString());
        digesterUtil.setDigester(digester);
        String content = "previous line\n<nowiki>\nmonospace\n\'\'second\'\' " + "line\n</nowiki>\nnext line";
        String expected = ((TABLE_OF_CONTENTS) + "previous line\n{{{{\nmonospace\n\'\'second\'\' line\n}}}}\nnext ") + "line";
        String actual = _mediaWikiToCreoleTranslator.translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testOrderedListItem() throws Exception {
        String content = "# item";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testOrderedListSubitem() throws Exception {
        String content = "## subitem";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testOrderedListSubsubitem() throws Exception {
        String content = "### subsubitem";
        String expected = content;
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveCategories() throws Exception {
        String content = "[[Category:My category]]\n[[category:Other category]]";
        String expected = "";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveDisambiguation() throws Exception {
        String content = "{{OtherTopics|Upgrade Instructions}}\ntest";
        String expected = "\ntest";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveHTMLTags() throws Exception {
        String content = "text\n<br>\ntext<br><div align=\"right\">x</div>";
        String expected = (TABLE_OF_CONTENTS) + "text\n\ntextx";
        String actual = _mediaWikiToCreoleTranslator.postProcess(_translate(content));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveWorkInProgress() throws Exception {
        String content = "{{Work in progress}}\ntest";
        String expected = "\ntest";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTermDefinition() throws Exception {
        String content = "\tterm:\tdefinition";
        String expected = "**term**:\ndefinition";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testURL() throws Exception {
        String content = "text[http://www.liferay.com]text";
        String expected = "text[[http://www.liferay.com]]text";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testURLWithLabel() throws Exception {
        String content = "[http://www.liferay.com This is the label]";
        String expected = "[[http://www.liferay.com|This is the label]]";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testURLWithText1() throws Exception {
        String content = "text [http://www.liferay.com link text] text";
        String expected = "text [[http://www.liferay.com|link text]] text";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testURLWithText2() throws Exception {
        String content = "text [[http://www.liferay.com link text]] text";
        String expected = "text [[http://www.liferay.com|link text]] text";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUserReference() throws Exception {
        String content = "--[[User:User name]]";
        String expected = "User name";
        String actual = _translate(content);
        Assert.assertEquals(expected, actual);
    }

    private final MediaWikiToCreoleTranslator _mediaWikiToCreoleTranslator = new MediaWikiToCreoleTranslator();
}

