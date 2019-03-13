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
package com.liferay.adaptive.media.document.library.thumbnails.internal.processor;


import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.adaptive.media.processor.AMAsyncProcessor;
import com.liferay.adaptive.media.processor.AMAsyncProcessorLocator;
import com.liferay.portal.kernel.repository.model.FileVersion;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Sergio Gonz?lez
 */
public class AMImageEntryProcessorTest {
    @Test
    public void testGetPreviewAsStreamDoesNotTriggerAMProcessorWhenAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.of(_adaptiveMedia));
        _amImageEntryProcessor.getPreviewAsStream(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetPreviewAsStreamDoesNotTriggerAMProcessorWhenInvalidSize() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(false);
        _amImageEntryProcessor.getPreviewAsStream(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetPreviewAsStreamDoesNotTriggerAMProcessorWhenNotSupported() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(false);
        _amImageEntryProcessor.getPreviewAsStream(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetPreviewAsStreamReturnsTheOriginalStreamWhenNoAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(true);
        InputStream originalInputStream = Mockito.mock(InputStream.class);
        Mockito.when(_fileVersion.getContentStream(false)).thenReturn(originalInputStream);
        Assert.assertEquals(originalInputStream, _amImageEntryProcessor.getPreviewAsStream(_fileVersion));
    }

    @Test
    public void testGetPreviewAsStreamTriggersAMProcessorWhenNoAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(true);
        _amImageEntryProcessor.getPreviewAsStream(_fileVersion);
        Mockito.verify(_amAsyncProcessor).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetPreviewFileSizeDoesNotTriggerAMProcessorWhenAMImageExists() throws Exception {
        Mockito.when(_adaptiveMedia.getValueOptional(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.of(_adaptiveMedia));
        _amImageEntryProcessor.getPreviewFileSize(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetPreviewFileSizeDoesNotTriggerAMProcessorWhenInvalidSize() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(false);
        _amImageEntryProcessor.getPreviewFileSize(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetPreviewFileSizeDoesNotTriggerAMProcessorWhenNotSupported() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(false);
        _amImageEntryProcessor.getPreviewFileSize(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetPreviewFileSizeTriggersAMProcessorWhenNoAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(true);
        _amImageEntryProcessor.getPreviewFileSize(_fileVersion);
        Mockito.verify(_amAsyncProcessor).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailAsStreamDoesNotTriggerAMProcessorWhenAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.of(_adaptiveMedia));
        _amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailAsStreamDoesNotTriggerAMProcessorWhenInvalidSize() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(false);
        _amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailAsStreamDoesNotTriggerAMProcessorWhenNotSupported() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(false);
        _amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailAsStreamTriggersAMProcessorWhenNoAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(true);
        _amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailFileSizeDoesNotTriggerAMProcessorWhenAMImageExists() throws Exception {
        Mockito.when(_adaptiveMedia.getValueOptional(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.of(_adaptiveMedia));
        _amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailFileSizeDoesNotTriggerAMProcessorWhenInvalidSize() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(false);
        _amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailFileSizeDoesNotTriggerAMProcessorWhenNotSupported() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(false);
        _amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testGetThumbnailFileSizeTriggersAMProcessorWhenNoAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(true);
        _amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);
        Mockito.verify(_amAsyncProcessor).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testHasImagesDoesNotTriggerAMProcessorWhenAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.of(_adaptiveMedia));
        _amImageEntryProcessor.hasImages(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testHasImagesDoesNotTriggerAMProcessorWhenInvalidSize() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(false);
        _amImageEntryProcessor.hasImages(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testHasImagesDoesNotTriggerAMProcessorWhenNotSupported() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(false);
        _amImageEntryProcessor.hasImages(_fileVersion);
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testHasImagesTriggersAMProcessorWhenNoAMImageExists() throws Exception {
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Mockito.when(_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())).thenReturn(true);
        Mockito.when(_amImageValidator.isValid(_fileVersion)).thenReturn(true);
        _amImageEntryProcessor.hasImages(_fileVersion);
        Mockito.verify(_amAsyncProcessor).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    private final AdaptiveMedia _adaptiveMedia = Mockito.mock(AdaptiveMedia.class);

    private final AMAsyncProcessor<FileVersion, ?> _amAsyncProcessor = Mockito.mock(AMAsyncProcessor.class);

    private final AMAsyncProcessorLocator _amAsyncProcessorLocator = Mockito.mock(AMAsyncProcessorLocator.class);

    private final AMImageEntryProcessor _amImageEntryProcessor = new AMImageEntryProcessor();

    private final AMImageFinder _amImageFinder = Mockito.mock(AMImageFinder.class);

    private final AMImageMimeTypeProvider _amImageMimeTypeProvider = Mockito.mock(AMImageMimeTypeProvider.class);

    private final AMImageValidator _amImageValidator = Mockito.mock(AMImageValidator.class);

    private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);
}

