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
package com.liferay.adaptive.media.image.web.internal.html;


import CharPool.NEW_LINE;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Alejandro Tard?n
 */
@RunWith(MockitoJUnitRunner.class)
public class AMImageHTMLTagFactoryImplTest {
    @Test
    public void testCreatesAPictureTag() throws Exception {
        _addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));
        String originalImgTag = "<img src=\"originalURL\" data-fileentryid=\"1234\"/>";
        StringBundler expectedSB = new StringBundler(5);
        expectedSB.append("<picture data-fileentryid=\"1234\">");
        expectedSB.append("<source media=\"(max-width:1989px)\" ");
        expectedSB.append("srcset=\"adaptiveURL\" />");
        expectedSB.append(originalImgTag);
        expectedSB.append("</picture>");
        String pictureTag = _amImageHTMLTagFactory.create(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedSB.toString(), pictureTag);
    }

    @Test
    public void testCreatesAPictureTagWithSeveralMediaQueries() throws Exception {
        _addMediaQueries(_createMediaQuery(1986, "adaptiveURL1"), _createMediaQuery(1989, "adaptiveURL2"));
        String originalImgTag = "<img src=\"originalURL\" data-fileentryid=\"1234\"/>";
        StringBundler expectedSB = new StringBundler(7);
        expectedSB.append("<picture data-fileentryid=\"1234\">");
        expectedSB.append("<source media=\"(max-width:1986px)\" ");
        expectedSB.append("srcset=\"adaptiveURL1\" />");
        expectedSB.append("<source media=\"(max-width:1989px)\" ");
        expectedSB.append("srcset=\"adaptiveURL2\" />");
        expectedSB.append(originalImgTag);
        expectedSB.append("</picture>");
        String pictureTag = _amImageHTMLTagFactory.create(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedSB.toString(), pictureTag);
    }

    @Test
    public void testNoSourceIsCreatedIfNoConditionIsPresent() throws Exception {
        _addMediaQueries(new com.liferay.adaptive.media.image.media.query.MediaQuery(Collections.emptyList(), RandomTestUtil.randomString()));
        String originalImgTag = "<img src=\"originalURL\" data-fileentryid=\"1234\"/>";
        StringBundler expectedSB = new StringBundler(3);
        expectedSB.append("<picture data-fileentryid=\"1234\">");
        expectedSB.append(originalImgTag);
        expectedSB.append("</picture>");
        String pictureTag = _amImageHTMLTagFactory.create(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedSB.toString(), pictureTag);
    }

    @Test
    public void testReturnsTheOriginalImgTagIfThereAreNoMediaQueries() throws Exception {
        _addMediaQueries();
        String originalImgTag = "<img src=\"originalURL\" data-fileentryid=\"1234\"/>";
        String pictureTag = _amImageHTMLTagFactory.create(originalImgTag, _fileEntry);
        Assert.assertSame(originalImgTag, pictureTag);
    }

    @Test
    public void testSupportsImageTagsWithNewLineCharacters() throws Exception {
        _addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));
        StringBundler originalSB = new StringBundler(3);
        originalSB.append("<img data-fileentryid=\"1234\" ");
        originalSB.append(NEW_LINE);
        originalSB.append("src=\"adaptable\"/>");
        StringBundler expectedSB = new StringBundler(5);
        expectedSB.append("<picture data-fileentryid=\"1234\">");
        expectedSB.append("<source media=\"(max-width:1989px)\" ");
        expectedSB.append("srcset=\"adaptiveURL\" />");
        expectedSB.append(originalSB.toString());
        expectedSB.append("</picture>");
        String pictureTag = _amImageHTMLTagFactory.create(originalSB.toString(), _fileEntry);
        Assert.assertEquals(expectedSB.toString(), pictureTag);
    }

    @Test
    public void testTheAttributeIsCaseInsensitive() throws Exception {
        _addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));
        String originalImgTag = "<img src=\"originalURL\" datA-fileENTryID=\"1234\"/>";
        StringBundler expectedSB = new StringBundler(5);
        expectedSB.append("<picture data-fileentryid=\"1234\">");
        expectedSB.append("<source media=\"(max-width:1989px)\" ");
        expectedSB.append("srcset=\"adaptiveURL\" />");
        expectedSB.append(originalImgTag);
        expectedSB.append("</picture>");
        String pictureTag = _amImageHTMLTagFactory.create(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedSB.toString(), pictureTag);
    }

    private final AMImageHTMLTagFactoryImpl _amImageHTMLTagFactory = new AMImageHTMLTagFactoryImpl();

    @Mock
    private FileEntry _fileEntry;

    @Mock
    private MediaQueryProvider _mediaQueryProvider;
}

