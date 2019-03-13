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
package com.liferay.wiki.engine.creole;


import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.wiki.engine.creole.internal.parser.parser.Creole10Parser;
import com.liferay.wiki.engine.creole.internal.parser.visitor.XhtmlTranslationVisitor;
import com.liferay.wiki.engine.creole.internal.util.WikiEngineCreoleComponentProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Miguel Pastor
 * @author Manuel de la Pe?a
 */
public class TranslationToXHTMLTest {
    @Test
    public void testEscapedEscapedCharacter() throws Exception {
        Assert.assertEquals("<p>~&#34;~ is escaped&#34; </p>", translate("escape-2.creole"));
    }

    @Test
    public void testInterwikiC2() throws Exception {
        Assert.assertEquals("<p><a href=\"http://c2.com/cgi/wiki?Liferay\">Liferay</a> </p>", translate("interwikic2.creole"));
    }

    @Test
    public void testInterwikiDokuWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://wiki.splitbrain.org/wiki:Liferay\">" + "Liferay</a> </p>"), translate("interwikidokuwiki.creole"));
    }

    @Test
    public void testInterwikiFlickr() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.flickr.com/search/?w=all&amp;m=text" + "&amp;q=Liferay\">Liferay</a> </p>"), translate("interwikiflickr.creole"));
    }

    @Test
    public void testInterwikiGoogle() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.google.com/search?q=Liferay\">" + "Liferay</a> </p>"), translate("interwikigoogle.creole"));
    }

    @Test
    public void testInterwikiJspWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.jspwiki.org/Wiki.jsp?page=Liferay\">" + "Liferay</a> </p>"), translate("interwikijspwiki.creole"));
    }

    @Test
    public void testInterwikiMeatBall() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://usemod.com/cgi-bin/mb.pl?Liferay\">" + "Liferay</a> </p>"), translate("interwikimeatball.creole"));
    }

    @Test
    public void testInterwikiMediaWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.mediawiki.org/wiki/Liferay\">" + "Liferay</a> </p>"), translate("interwikimediawiki.creole"));
    }

    @Test
    public void testInterwikiMoinMoin() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://moinmoin.wikiwikiweb.de/Liferay\">" + "Liferay</a> </p>"), translate("interwikimoinmoin.creole"));
    }

    @Test
    public void testInterwikiOddMuse() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.oddmuse.org/cgi-bin/wiki/Liferay\">" + "Liferay</a> </p>"), translate("interwikioddmuse.creole"));
    }

    @Test
    public void testInterwikiOhana() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://wikiohana.net/cgi-bin/wiki.pl/Liferay\">" + "Liferay</a> </p>"), translate("interwikiohana.creole"));
    }

    @Test
    public void testInterwikiPmWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.pmwiki.com/wiki/PmWiki/Liferay\">" + "Liferay</a> </p>"), translate("interwikipmwiki.creole"));
    }

    @Test
    public void testInterwikiPukiWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://pukiwiki.sourceforge.jp/?Liferay\">" + "Liferay</a> </p>"), translate("interwikipukiwiki.creole"));
    }

    @Test
    public void testInterwikiPurpleWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://purplewiki.blueoxen.net/cgi-bin/wiki.pl" + "?Liferay\">Liferay</a> </p>"), translate("interwikipurplewiki.creole"));
    }

    @Test
    public void testInterwikiRadeox() throws Exception {
        Assert.assertEquals("<p><a href=\"http://radeox.org/space/Liferay\">Liferay</a> </p>", translate("interwikiradeox.creole"));
    }

    @Test
    public void testInterwikiSnipSnap() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.snipsnap.org/space/Liferay\">" + "Liferay</a> </p>"), translate("interwikisnipsnap.creole"));
    }

    @Test
    public void testInterwikiTiddlyWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://www.tiddlywiki.com/#Liferay\">" + "Liferay</a> </p>"), translate("interwikitiddlywiki.creole"));
    }

    @Test
    public void testInterwikiTWiki() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://twiki.org/cgi-bin/view/TWiki/Liferay\">" + "Liferay</a> </p>"), translate("interwikitwiki.creole"));
    }

    @Test
    public void testInterwikiUsemod() throws Exception {
        Assert.assertEquals(("<p><a href=\"http://http://www.usemod.com/cgi-bin/wiki.pl" + "?Liferay\">Liferay</a> </p>"), translate("interwikiusemod.creole"));
    }

    @Test
    public void testInterwikiWikipedia() throws Exception {
        Assert.assertEquals("<p><a href=\"http://wikipedia.org/wiki/Liferay\">Liferay</a> </p>", translate("interwikiwikipedia.creole"));
    }

    @Test
    public void testParseCorrectlyBoldContentInListItems() throws Exception {
        Assert.assertEquals("<ul><li> <strong>abcdefg</strong></li></ul>", translate("list-6.creole"));
    }

    @Test
    public void testParseCorrectlyComplexNestedList() throws Exception {
        Assert.assertEquals(("<ul><li>a<ul><li>a.1</li></ul></li><li>b<ul><li>b.1</li>" + "<li>b.2</li><li>b.3</li></ul></li><li>c</li></ul>"), translate("list-4.creole"));
    }

    @Test
    public void testParseCorrectlyItalicContentInListItems() throws Exception {
        Assert.assertEquals("<ul><li> <em>abcdefg</em></li></ul>", translate("list-5.creole"));
    }

    @Test
    public void testParseCorrectlyMixedHorizontalBlocks() throws Exception {
        Assert.assertEquals(("<h1>Before Horizontal section</h1><hr/><pre>\tNo wiki section " + "after Horizontal section</pre>"), translate("horizontal-3.creole"));
    }

    @Test
    public void testParseCorrectlyMultipleHeadingBlocks() throws Exception {
        Assert.assertEquals("<h1>Level 1</h1><h2>Level 2</h2><h3>Level 3</h3>", translate("heading-10.creole"));
    }

    @Test
    public void testParseCorrectlyNoClosedFirstHeadingBlock() throws Exception {
        Assert.assertEquals("<h1>This is a non closed heading</h1>", translate("heading-3.creole"));
    }

    @Test
    public void testParseCorrectlyNoClosedSecondHeadingBlock() throws Exception {
        Assert.assertEquals("<h2>This is a non closed heading</h2>", translate("heading-6.creole"));
    }

    @Test
    public void testParseCorrectlyNoClosedThirdHeadingBlock() throws Exception {
        Assert.assertEquals("<h3>Level 3</h3>", translate("heading-7.creole"));
    }

    @Test
    public void testParseCorrectlyNoWikiBlockInline() throws Exception {
        Assert.assertEquals("<p><tt> Inline </tt></p>", translate("nowikiblock-10.creole"));
    }

    @Test
    public void testParseCorrectlyNoWikiBlockWithBraces() throws Exception {
        Assert.assertEquals((((((("<pre>{" + (TranslationToXHTMLTest._NEW_LINE)) + "foo") + (TranslationToXHTMLTest._NEW_LINE)) + "}") + (TranslationToXHTMLTest._NEW_LINE)) + "</pre>"), toUnix(translate("nowikiblock-7.creole")));
    }

    @Test
    public void testParseCorrectlyNoWikiBlockWithMultipleAndText() throws Exception {
        Assert.assertEquals((((((("<pre>public interface Foo {" + (TranslationToXHTMLTest._NEW_LINE)) + "void foo();") + (TranslationToXHTMLTest._NEW_LINE)) + "}") + (TranslationToXHTMLTest._NEW_LINE)) + "</pre><p>Outside preserve </p>"), toUnix(translate("nowikiblock-9.creole")));
    }

    @Test
    public void testParseCorrectlyNoWikiBlockWithMultipleBraces() throws Exception {
        Assert.assertEquals((((((("<pre>public interface Foo {" + (TranslationToXHTMLTest._NEW_LINE)) + "void foo();") + (TranslationToXHTMLTest._NEW_LINE)) + "}") + (TranslationToXHTMLTest._NEW_LINE)) + "</pre>"), toUnix(translate("nowikiblock-8.creole")));
    }

    @Test
    public void testParseCorrectlyOneEmptyFirstHeadingBlock() throws Exception {
        Assert.assertEquals("<h1>  </h1>", translate("heading-2.creole"));
    }

    @Test
    public void testParseCorrectlyOneEmptyNoWikiBlock() throws Exception {
        Assert.assertEquals("<pre></pre>", translate("nowikiblock-3.creole"));
    }

    @Test
    public void testParseCorrectlyOneEmptySecondHeadingBlock() throws Exception {
        Assert.assertEquals("<h2>  </h2>", translate("heading-5.creole"));
    }

    @Test
    public void testParseCorrectlyOneEmptyThirdHeadingBlock() throws Exception {
        Assert.assertEquals("<h3>  </h3>", translate("heading-8.creole"));
    }

    @Test
    public void testParseCorrectlyOneHorizontalBlocks() throws Exception {
        Assert.assertEquals("<hr/>", translate("horizontal-1.creole"));
    }

    @Test
    public void testParseCorrectlyOneItemFirstLevel() throws Exception {
        Assert.assertEquals("<ul><li>ABCDEFG</li></ul>", translate("list-1.creole"));
    }

    @Test
    public void testParseCorrectlyOneNonemptyFirstHeadingBlock() throws Exception {
        Assert.assertEquals("<h1> Level 1 (largest) </h1>", translate("heading-1.creole"));
    }

    @Test
    public void testParseCorrectlyOneNonemptyNoWikiBlock() throws Exception {
        Assert.assertEquals("<pre>This is a non \\empty\\ block</pre>", translate("nowikiblock-4.creole"));
    }

    @Test
    public void testParseCorrectlyOneNonemptyNoWikiBlockWithBraces() throws Exception {
        Assert.assertEquals("<p>Preserving </p><pre>.lfr-helper{span}</pre>", translate("nowikiblock-6.creole"));
    }

    @Test
    public void testParseCorrectlyOneNonemptyNoWikiBlockWithMultipleLines() throws Exception {
        Assert.assertEquals((("<pre>Multiple" + (TranslationToXHTMLTest._NEW_LINE)) + "lines</pre>"), toUnix(translate("nowikiblock-5.creole")));
    }

    @Test
    public void testParseCorrectlyOneNonemptySecondHeadingBlock() throws Exception {
        Assert.assertEquals("<h2>Level 2</h2>", translate("heading-4.creole"));
    }

    @Test
    public void testParseCorrectlyOneNonemptyThirdHeadingBlock() throws Exception {
        Assert.assertEquals("<h3>This is a non closed heading</h3>", translate("heading-9.creole"));
    }

    @Test
    public void testParseCorrectlyOneOrderedItemFirstLevel() throws Exception {
        Assert.assertEquals("<ol><li>ABCDEFG</li></ol>", translate("list-7.creole"));
    }

    @Test
    public void testParseCorrectlyOrderedNestedLevels() throws Exception {
        Assert.assertEquals(("<ol><li>a<ol><li>a.1</li></ol></li><li>b<ol><li>b.1</li>" + "<li>b.2</li><li>b.3</li></ol></li><li>c</li></ol>"), translate("list-10.creole"));
    }

    @Test
    public void testParseCorrectlyThreeItemFirstLevel() throws Exception {
        Assert.assertEquals("<pre>1111</pre><pre>2222</pre><pre>3333</pre>", translate("nowikiblock-2.creole"));
    }

    @Test
    public void testParseCorrectlyThreeOrderedItemFirstLevel() throws Exception {
        Assert.assertEquals("<ol><li>1</li><li>2</li><li>3</li></ol>", translate("list-9.creole"));
    }

    @Test
    public void testParseCorrectlyTwoHorizontalBlocks() throws Exception {
        Assert.assertEquals("<hr/><hr/>", translate("horizontal-2.creole"));
    }

    @Test
    public void testParseCorrectlyTwoItemFirstLevel() throws Exception {
        Assert.assertEquals("<ul><li>1</li><li>2</li></ul>", translate("list-2.creole"));
    }

    @Test
    public void testParseCorrectlyTwoOrderedItemFirstLevel() throws Exception {
        Assert.assertEquals("<ol><li>1</li><li>2</li></ol>", translate("list-8.creole"));
    }

    @Test
    public void testParseEmpyImageTag() throws Exception {
        Assert.assertEquals("<p><img src=\"\" /> </p>", translate("image-4.creole"));
    }

    @Test
    public void testParseImageAndTextInListItem() throws Exception {
        Assert.assertEquals("<ul><li><img src=\"imageLink\" alt=\"altText\"/> end.</li></ul>", translate("list-17.creole"));
    }

    @Test
    public void testParseImageInListItem() throws Exception {
        Assert.assertEquals("<ul><li><img src=\"imageLink\" alt=\"altText\"/></li></ul>", translate("list-16.creole"));
    }

    @Test
    public void testParseLinkEmpty() throws Exception {
        Assert.assertEquals("<p></p>", translate("link-8.creole"));
    }

    @Test
    public void testParseLinkEmptyInHeader() throws Exception {
        Assert.assertEquals("<h2>  </h2>", translate("link-9.creole"));
    }

    @Test
    public void testParseLinkEscapedBracket() throws Exception {
        Assert.assertEquals("<p>link:<a href=\"http://liferay.com\">[1]</a> </p>", translate("link-15.creole"));
    }

    @Test
    public void testParseLinkFtp() throws Exception {
        Assert.assertEquals("<p><a href=\"ftp://liferay.com\">Liferay</a> </p>", translate("link-12.creole"));
    }

    @Test
    public void testParseLinkHttp() throws Exception {
        Assert.assertEquals("<p><a href=\"http://liferay.com\">Liferay</a> </p>", translate("link-10.creole"));
    }

    @Test
    public void testParseLinkHttps() throws Exception {
        Assert.assertEquals("<p><a href=\"https://liferay.com\">Liferay</a> </p>", translate("link-11.creole"));
    }

    @Test
    public void testParseLinkInListItem() throws Exception {
        Assert.assertEquals("<ul><li><a href=\"l\">a</a></li></ul>", translate("list-13.creole"));
    }

    @Test
    public void testParseLinkInListItemMixedText() throws Exception {
        Assert.assertEquals(("<ul><li>This is an item with a link <a href=\"l\">a</a> inside " + "text</li></ul>"), translate("list-12.creole"));
    }

    @Test
    public void testParseLinkInListItemWithPreText() throws Exception {
        Assert.assertEquals("<ul><li>This is an item with a link <a href=\"l\">a</a></li></ul>", translate("list-11.creole"));
    }

    @Test
    public void testParseLinkMailTo() throws Exception {
        Assert.assertEquals("<p><a href=\"mailto:liferay@liferay.com\">Liferay Mail</a> </p>", translate("link-13.creole"));
    }

    @Test
    public void testParseLinkMMS() throws Exception {
        Assert.assertEquals("<p><a href=\"mms://liferay.com/file\">Liferay File</a> </p>", translate("link-14.creole"));
    }

    @Test
    public void testParseLinkWithNoAlt() throws Exception {
        Assert.assertEquals("<p><a href=\"Link\">Link</a> </p>", translate("link-7.creole"));
    }

    @Test
    public void testParseMixedList1() throws Exception {
        Assert.assertEquals("<ul><li> U1</li></ul><ol><li> O1</li></ol>", translate("mixed-list-1.creole"));
    }

    @Test
    public void testParseMixedList2() throws Exception {
        Assert.assertEquals(("<ol><li> 1<ol><li> 1.1</li><li> 1.2</li><li> 1.3</li></ol></li>" + "</ol><ul><li> A<ul><li> A.A</li><li> A.B</li></ul></li></ul>"), translate("mixed-list-2.creole"));
    }

    @Test
    public void testParseMixedList3() throws Exception {
        StringBundler sb = new StringBundler(4);
        sb.append("<ol><li> T1<ol><li> T1.1</li></ol></li><li> T2</li><li> T3");
        sb.append("</li></ol><ul><li> Divider 1<ul><li> Divider 2a</li><li> ");
        sb.append("Divider 2b<ul><li> Divider 3</li></ul></li></ul></li></ul>");
        sb.append("<ol><li> T3.2</li><li> T3.3</li></ol>");
        Assert.assertEquals(sb.toString(), translate("mixed-list-3.creole"));
    }

    @Test
    public void testParseMultilineTextParagraph() throws Exception {
        Assert.assertEquals(("<p>Simple P0 Simple P1 Simple P2 Simple P3 Simple P4 Simple P5 " + "Simple P6 Simple P7 Simple P8 Simple P9 </p>"), translate("text-2.creole"));
    }

    @Test
    public void testParseMultipleImageTags() throws Exception {
        Assert.assertEquals(("<p><img src=\"L1\" alt=\"A1\"/><img src=\"L2\" alt=\"A2\"/><img " + ("src=\"L3\" alt=\"A3\"/><img src=\"L4\" alt=\"A4\"/><img " + "src=\"L5\" alt=\"A5\"/> </p>")), translate("image-5.creole"));
    }

    @Test
    public void testParseMultipleLinkTags() throws Exception {
        Assert.assertEquals(("<p><a href=\"L\">A</a> <a href=\"L\">A</a> <a href=\"L\">A</a> " + "</p>"), translate("link-3.creole"));
    }

    @Test
    public void testParseNestedLists() throws Exception {
        StringBundler sb = new StringBundler(4);
        sb.append("<ul><li> 1</li><li> 2<ul><li> 2.1<ul><li> 2.1.1<ul><li> ");
        sb.append("2.1.1.1</li><li> 2.1.1.2</li></ul></li><li> 2.1.2</li>");
        sb.append("<li> 2.1.3</li></ul></li><li> 2.2</li><li> 2.3</li></ul>");
        sb.append("</li><li>3</li></ul>");
        Assert.assertEquals(sb.toString(), translate("list-18.creole"));
    }

    @Test
    public void testParseNoWikiAndTextInListItem() throws Exception {
        Assert.assertEquals(("<ul><li><tt>This is nowiki inside a list item</tt> and <em>" + "italics</em></li></ul>"), translate("list-15.creole"));
    }

    @Test
    public void testParseNoWikiInListItem() throws Exception {
        Assert.assertEquals("<ul><li><tt>This is nowiki inside a list item</tt></li></ul>", translate("list-14.creole"));
    }

    @Test
    public void testParseOnlySpacesContentInImageTag() throws Exception {
        Assert.assertEquals(("<p><img src=\"L1\" alt=\"A1\"/><img src=\"L2\" alt=\"A2\"/>" + ("<img src=\"L3\" alt=\"A3\"/><img src=\"L4\" alt=\"A4\"/>" + "<img src=\"L5\" alt=\"A5\"/> </p>")), translate("image-5.creole"));
    }

    @Test
    public void testParseSimpleImageTag() throws Exception {
        Assert.assertEquals("<p><img src=\"link\" alt=\"alternative text\"/> </p>", translate("image-1.creole"));
    }

    @Test
    public void testParseSimpleImageTagWithNoAlternative() throws Exception {
        Assert.assertEquals("<p><img src=\"link\" /> </p>", translate("image-2.creole"));
    }

    @Test
    public void testParseSimpleLinkTag() throws Exception {
        Assert.assertEquals("<p><a href=\"link\">alternative text</a> </p>", translate("link-1.creole"));
    }

    @Test
    public void testParseSimpleLinkTagWithoutDescription() throws Exception {
        Assert.assertEquals("<p><a href=\"link\">link</a> </p>", translate("link-2.creole"));
    }

    @Test
    public void testParseSimpleTextBoldAndItalics() throws Exception {
        Assert.assertEquals("<p>Text <strong><em>ItalicAndBold</em></strong> </p>", translate("text-6.creole"));
    }

    @Test
    public void testParseSimpleTextParagraph() throws Exception {
        Assert.assertEquals("<p>Simple paragraph </p>", translate("text-1.creole"));
    }

    @Test
    public void testParseSimpleTextWithBold() throws Exception {
        Assert.assertEquals("<p>Text with some content in <strong>bold</strong> </p>", translate("text-4.creole"));
    }

    @Test
    public void testParseSimpleTextWithBoldAndItalics() throws Exception {
        Assert.assertEquals(("<p>Text with some content in <strong>bold</strong> and with " + "some content in <em>italic</em> </p>"), translate("text-5.creole"));
    }

    @Test
    public void testParseSimpleTextWithForcedEndline() throws Exception {
        Assert.assertEquals("<p>Text with <br/>forced line break </p>", translate("text-7.creole"));
    }

    @Test
    public void testParseSimpleTextWithItalics() throws Exception {
        Assert.assertEquals("<p>Text with some content in <em>italic</em> </p>", translate("text-3.creole"));
    }

    @Test
    public void testParseTableImagesNested() throws Exception {
        Assert.assertEquals(("<table><tr><th>H1</th></tr><tr><td><img src=\"image.png\" " + "alt=\"Image\"/></td></tr></table>"), translate("table-4.creole"));
    }

    @Test
    public void testParseTableLinksNested() throws Exception {
        Assert.assertEquals(("<table><tr><th>H1</th></tr><tr><td><a " + ("href=\"http://www.liferay.com \"> Liferay</a></td></tr>" + "</table>")), translate("table-3.creole"));
    }

    @Test
    public void testParseTableMultipleRowsAndColumns() throws Exception {
        StringBundler sb = new StringBundler(5);
        sb.append("<table><tr><th>H1</th><th>H2</th><th>H3</th><th>H4</th>");
        sb.append("</tr><tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td></tr>");
        sb.append("<tr><td>C5</td><td>C6</td><td>C7</td><td>C8</td></tr><tr>");
        sb.append("<td>C9</td><td>C10</td><td>C11</td><td>C12</td></tr>");
        sb.append("</table>");
        Assert.assertEquals(sb.toString(), translate("table-2.creole"));
    }

    @Test
    public void testParseTableOfContents() throws Exception {
        Assert.assertEquals("<h2> Level 1  </h2><h2> Level 2 </h2>", translate("tableofcontents-1.creole"));
    }

    @Test
    public void testParseTableOfContentsWithTitle() throws Exception {
        Assert.assertEquals(("<h2> Level 1 (largest) </h2><p><strong>L1 text</strong> </p>" + "<h2> Level 2 </h2><h3> Level 3 </h3>"), translate("tableofcontents-2.creole"));
    }

    @Test
    public void testParseTableOneRowOneColumn() throws Exception {
        Assert.assertEquals("<table><tr><th>H1</th></tr><tr><td>C1.1</td></tr></table>", translate("table-1.creole"));
    }

    @Test
    public void testSimpleEscapedCharacter() throws Exception {
        Assert.assertEquals("<p>ESCAPED1 This is not escaped </p>", translate("escape-1.creole"));
    }

    @Test
    public void testTranslateOneNoWikiBlock() throws Exception {
        Assert.assertEquals("<pre>\t//This// does **not** get [[formatted]]</pre>", translate("nowikiblock-1.creole"));
    }

    private static final String _NEW_LINE = StringPool.NEW_LINE;

    private Creole10Parser _creole10Parser;

    private WikiEngineCreoleComponentProvider _wikiEngineCreoleComponentProvider;

    private final XhtmlTranslationVisitor _xhtmlTranslationVisitor = new XhtmlTranslationVisitor();
}

