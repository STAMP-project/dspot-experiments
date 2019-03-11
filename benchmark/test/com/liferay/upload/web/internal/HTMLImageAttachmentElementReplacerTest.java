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
package com.liferay.upload.web.internal;


import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Alejandro Tard?n
 */
public class HTMLImageAttachmentElementReplacerTest extends PowerMockito {
    @Test
    public void testKeepsCustomAttributes() throws Exception {
        String originalImgTag = String.format("<img class=\"custom\" data-image-id=\"%s\" src=\"%s\" />", HTMLImageAttachmentElementReplacerTest._IMAGE_FILE_ENTRY_ID, StringUtil.randomString());
        String expectedImgTag = String.format("<img class=\"custom\" src=\"%s\" />", HTMLImageAttachmentElementReplacerTest._FILE_ENTRY_IMAGE_URL);
        String actualTag = _htmlImageAttachmentElementReplacer.replace(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedImgTag, actualTag);
    }

    @Test
    public void testRemovesTemporalAttributeAndSetsFinalSrc() throws Exception {
        String originalImgTag = String.format("<img data-image-id=\"%s\" src=\"%s\" />", HTMLImageAttachmentElementReplacerTest._IMAGE_FILE_ENTRY_ID, StringUtil.randomString());
        String expectedImgTag = String.format("<img src=\"%s\" />", HTMLImageAttachmentElementReplacerTest._FILE_ENTRY_IMAGE_URL);
        String actualTag = _htmlImageAttachmentElementReplacer.replace(originalImgTag, _fileEntry);
        Assert.assertEquals(expectedImgTag, actualTag);
    }

    private static final String _FILE_ENTRY_IMAGE_URL = RandomTestUtil.randomString();

    private static final long _IMAGE_FILE_ENTRY_ID = RandomTestUtil.randomLong();

    private FileEntry _fileEntry;

    private HTMLImageAttachmentElementReplacer _htmlImageAttachmentElementReplacer;

    private PortletFileRepository _portletFileRepository;
}

