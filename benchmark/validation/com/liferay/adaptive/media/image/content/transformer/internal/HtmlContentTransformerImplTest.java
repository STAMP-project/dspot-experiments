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
package com.liferay.adaptive.media.image.content.transformer.internal;


import CharPool.NEW_LINE;
import ContentTransformerContentTypes.HTML;
import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Alejandro Tard?n
 * @author Sergio Gonz?lez
 */
@RunWith(MockitoJUnitRunner.class)
public class HtmlContentTransformerImplTest {
    @Test
    public void testAlsoReplacesSeveralImagesInAMultilineString() throws Exception {
        Mockito.when(_amImageHTMLTagFactory.create("<img data-fileentryid=\"1989\" src=\"adaptable\"/>", _fileEntry)).thenReturn("<whatever></whatever>");
        StringBundler expectedSB = new StringBundler(3);
        expectedSB.append("<div><div>");
        expectedSB.append("<whatever></whatever>");
        expectedSB.append("</div></div><br/>");
        StringBundler originalSB = new StringBundler(4);
        originalSB.append("<div><div>");
        originalSB.append("<img data-fileentryid=\"1989\" ");
        originalSB.append("src=\"adaptable\"/>");
        originalSB.append("</div></div><br/>");
        Assert.assertEquals(_duplicateWithNewLine(expectedSB.toString()), _htmlContentTransformer.transform(_duplicateWithNewLine(originalSB.toString())));
    }

    @Test
    public void testContentTypeIsHTML() throws Exception {
        Assert.assertEquals(HTML, _htmlContentTransformer.getContentTransformerContentType());
    }

    @Test
    public void testReplacesAnAdaptableImgAfterANonadaptableOne() throws Exception {
        Mockito.when(_amImageHTMLTagFactory.create("<img data-fileentryid=\"1989\" src=\"adaptable\"/>", _fileEntry)).thenReturn("<whatever></whatever>");
        Assert.assertEquals("<img src=\"not-adaptable\"/><whatever></whatever>", _htmlContentTransformer.transform(("<img src=\"not-adaptable\"/>" + "<img data-fileentryid=\"1989\" src=\"adaptable\"/>")));
    }

    @Test
    public void testReplacesTheAdaptableImagesWithTheAdaptiveTag() throws Exception {
        Mockito.when(_amImageHTMLTagFactory.create("<img data-fileentryid=\"1989\" src=\"adaptable\"/>", _fileEntry)).thenReturn("<whatever></whatever>");
        Assert.assertEquals("<whatever></whatever>", _htmlContentTransformer.transform("<img data-fileentryid=\"1989\" src=\"adaptable\"/>"));
    }

    @Test
    public void testReplacesTwoConsecutiveImageTags() throws Exception {
        Mockito.when(_amImageHTMLTagFactory.create("<img data-fileentryid=\"1989\" src=\"adaptable\"/>", _fileEntry)).thenReturn("<whatever></whatever>");
        Assert.assertEquals("<whatever></whatever><whatever></whatever>", _htmlContentTransformer.transform(("<img data-fileentryid=\"1989\" src=\"adaptable\"/>" + "<img data-fileentryid=\"1989\" src=\"adaptable\"/>")));
    }

    @Test
    public void testReturnsNullForNullContent() throws Exception {
        Assert.assertNull(_htmlContentTransformer.transform(null));
    }

    @Test
    public void testReturnsTheSameHTMLIfNoImagesArePresent() throws Exception {
        Assert.assertEquals("<div><div>some <a>stuff</a></div></div>", _htmlContentTransformer.transform("<div><div>some <a>stuff</a></div></div>"));
    }

    @Test
    public void testReturnsTheSameHTMLIfThereAreNoAdaptableImagesPresent() throws Exception {
        Assert.assertEquals("<div><div><img src=\"no.adaptable\"/></div></div>", _htmlContentTransformer.transform("<div><div><img src=\"no.adaptable\"/></div></div>"));
    }

    @Test
    public void testSupportsImageTagsWithNewLineCharacters() throws Exception {
        Mockito.when(_amImageHTMLTagFactory.create("<img data-fileentryid=\"1989\" \nsrc=\"adaptable\"/>", _fileEntry)).thenReturn("<whatever></whatever>");
        StringBundler originalSB = new StringBundler(3);
        originalSB.append("<img data-fileentryid=\"1989\" ");
        originalSB.append(NEW_LINE);
        originalSB.append("src=\"adaptable\"/>");
        Assert.assertEquals("<whatever></whatever>", _htmlContentTransformer.transform(originalSB.toString()));
    }

    @Test
    public void testTheAttributeIsCaseInsensitive() throws Exception {
        Mockito.when(_amImageHTMLTagFactory.create("<img data-fileentryid=\"1989\" src=\"adaptable\"/>", _fileEntry)).thenReturn("<whatever></whatever>");
        StringBundler expectedSB = new StringBundler(1);
        expectedSB.append("<div><div><whatever></whatever></div></div><br/>");
        StringBundler originalSB = new StringBundler(4);
        originalSB.append("<div><div>");
        originalSB.append("<img data-fileentryid=\"1989\" ");
        originalSB.append("src=\"adaptable\"/>");
        originalSB.append("</div></div><br/>");
        Assert.assertEquals(expectedSB.toString(), _htmlContentTransformer.transform(StringUtil.toLowerCase(originalSB.toString())));
    }

    @Mock
    private AMImageHTMLTagFactory _amImageHTMLTagFactory;

    @Mock
    private DLAppLocalService _dlAppLocalService;

    @Mock
    private FileEntry _fileEntry;

    private final HtmlContentTransformerImpl _htmlContentTransformer = new HtmlContentTransformerImpl();
}

