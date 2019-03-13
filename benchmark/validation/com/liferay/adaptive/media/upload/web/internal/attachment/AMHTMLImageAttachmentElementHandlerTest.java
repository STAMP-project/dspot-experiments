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
package com.liferay.adaptive.media.upload.web.internal.attachment;


import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.upload.AttachmentElementReplacer;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Alejandro Tard?n
 */
public class AMHTMLImageAttachmentElementHandlerTest extends PowerMockito {
    @Test
    public void testGetBlogsEntryAttachmentFileEntryImgTag() throws Exception {
        String originalImgTag = String.format("<img src=\"%s\" />", AMHTMLImageAttachmentElementHandlerTest._FILE_ENTRY_IMAGE_URL);
        String expectedImgTag = String.format("<img src=\"%s\" data-fileentryid=\"%s\" />", AMHTMLImageAttachmentElementHandlerTest._FILE_ENTRY_IMAGE_URL, AMHTMLImageAttachmentElementHandlerTest._IMAGE_FILE_ENTRY_ID);
        String actualTag = _amHTMLImageAttachmentElementReplacer.replace(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedImgTag, actualTag);
    }

    @Test
    public void testGetBlogsEntryAttachmentFileEntryImgTagWithCustomAttribute() throws Exception {
        String originalImgTag = String.format("<img class=\"custom\" src=\"%s\" />", AMHTMLImageAttachmentElementHandlerTest._FILE_ENTRY_IMAGE_URL);
        String expectedImgTag = String.format("<img class=\"custom\" src=\"%s\" data-fileentryid=\"%s\" />", AMHTMLImageAttachmentElementHandlerTest._FILE_ENTRY_IMAGE_URL, AMHTMLImageAttachmentElementHandlerTest._IMAGE_FILE_ENTRY_ID);
        String actualTag = _amHTMLImageAttachmentElementReplacer.replace(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedImgTag, actualTag);
    }

    private static final String _FILE_ENTRY_IMAGE_URL = RandomTestUtil.randomString();

    private static final long _IMAGE_FILE_ENTRY_ID = RandomTestUtil.randomLong();

    private AMHTMLImageAttachmentElementReplacer _amHTMLImageAttachmentElementReplacer;

    private AttachmentElementReplacer _defaultAttachmentElementReplacer;

    private FileEntry _fileEntry;
}

