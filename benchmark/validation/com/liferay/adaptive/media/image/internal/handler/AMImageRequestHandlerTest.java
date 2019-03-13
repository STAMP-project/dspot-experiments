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
package com.liferay.adaptive.media.image.internal.handler;


import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.exception.AMException;
import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.processor.AMImageProcessor;
import com.liferay.adaptive.media.processor.AMAsyncProcessor;
import com.liferay.adaptive.media.processor.AMAsyncProcessorLocator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Adolfo P?rez
 * @author Alejandro Tard?n
 */
public class AMImageRequestHandlerTest {
    @Test(expected = AMRuntimeException.class)
    public void testFinderFailsWithMediaProcessorException() throws Exception {
        AMImageConfigurationEntry amImageConfigurationEntry = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 200, 500);
        HttpServletRequest request = _createRequestFor(_fileVersion, amImageConfigurationEntry);
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenThrow(AMException.class);
        _amImageRequestHandler.handleRequest(request);
    }

    @Test(expected = AMRuntimeException.class)
    public void testFinderFailsWithPortalException() throws Exception {
        AMImageConfigurationEntry getConfigurationEntryFilter = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 200, 500);
        HttpServletRequest request = _createRequestFor(_fileVersion, getConfigurationEntryFilter);
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenThrow(PortalException.class);
        _amImageRequestHandler.handleRequest(request);
    }

    @Test
    public void testInvalidPath() throws Exception {
        Mockito.when(_pathInterpreter.interpretPath(Mockito.anyString())).thenReturn(Optional.empty());
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Optional<AdaptiveMedia<AMImageProcessor>> adaptiveMediaOptional = _amImageRequestHandler.handleRequest(request);
        Assert.assertFalse(adaptiveMediaOptional.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void testNullRequest() throws Exception {
        _amImageRequestHandler.handleRequest(null);
    }

    @Test
    public void testPathInterpreterFailure() throws Exception {
        Mockito.when(_pathInterpreter.interpretPath(Mockito.anyString())).thenThrow(AMRuntimeException.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Optional<AdaptiveMedia<AMImageProcessor>> adaptiveMediaOptional = _amImageRequestHandler.handleRequest(request);
        Assert.assertFalse(adaptiveMediaOptional.isPresent());
    }

    @Test
    public void testReturnsTheClosestMatchByWidthIfNoExactMatchPresentAndRunsTheProcess() throws Exception {
        AMImageConfigurationEntry getConfigurationEntryFilter = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 200, 500);
        AMImageConfigurationEntry closestAMImageConfigurationEntry = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 201, 501);
        AMImageConfigurationEntry fartherAMImageConfigurationEntry = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 301, 501);
        AMImageConfigurationEntry farthestAMImageConfigurationEntry = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 401, 501);
        AdaptiveMedia<AMImageProcessor> closestAdaptiveMedia = _createAdaptiveMedia(_fileVersion, closestAMImageConfigurationEntry);
        AdaptiveMedia<AMImageProcessor> fartherAdaptiveMedia = _createAdaptiveMedia(_fileVersion, fartherAMImageConfigurationEntry);
        AdaptiveMedia<AMImageProcessor> farthestAdaptiveMedia = _createAdaptiveMedia(_fileVersion, farthestAMImageConfigurationEntry);
        _mockClosestMatch(_fileVersion, getConfigurationEntryFilter, Arrays.asList(farthestAdaptiveMedia, closestAdaptiveMedia, fartherAdaptiveMedia));
        HttpServletRequest request = _createRequestFor(_fileVersion, getConfigurationEntryFilter);
        Assert.assertEquals(Optional.of(closestAdaptiveMedia), _amImageRequestHandler.handleRequest(request));
        Mockito.verify(_amAsyncProcessor).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testReturnsTheExactMatchIfPresentAndDoesNotRunTheProcess() throws Exception {
        AMImageConfigurationEntry amImageConfigurationEntry = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 200, 500);
        AdaptiveMedia<AMImageProcessor> adaptiveMedia = _createAdaptiveMedia(_fileVersion, amImageConfigurationEntry);
        _mockExactMatch(_fileVersion, amImageConfigurationEntry, adaptiveMedia);
        HttpServletRequest request = _createRequestFor(_fileVersion, amImageConfigurationEntry);
        Assert.assertEquals(Optional.of(adaptiveMedia), _amImageRequestHandler.handleRequest(request));
        Mockito.verify(_amAsyncProcessor, Mockito.never()).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    @Test
    public void testReturnsTheRealImageIfThereAreNoAdaptiveMediasAndRunsTheProcess() throws Exception {
        AMImageConfigurationEntry amImageConfigurationEntry = _createAMImageConfigurationEntry(_fileVersion.getCompanyId(), 200, 500);
        HttpServletRequest request = _createRequestFor(_fileVersion, amImageConfigurationEntry);
        Mockito.when(_amImageFinder.getAdaptiveMediaStream(Mockito.any(Function.class))).thenAnswer(( invocation) -> Stream.empty());
        Optional<AdaptiveMedia<AMImageProcessor>> adaptiveMediaOptional = _amImageRequestHandler.handleRequest(request);
        Assert.assertTrue(adaptiveMediaOptional.isPresent());
        AdaptiveMedia<AMImageProcessor> adaptiveMedia = adaptiveMediaOptional.get();
        Assert.assertEquals(_fileVersion.getContentStream(false), adaptiveMedia.getInputStream());
        Assert.assertEquals(Optional.of(_fileVersion.getFileName()), adaptiveMedia.getValueOptional(AMAttribute.getFileNameAMAttribute()));
        Assert.assertEquals(Optional.of(_fileVersion.getMimeType()), adaptiveMedia.getValueOptional(AMAttribute.getContentTypeAMAttribute()));
        Optional<Long> contentLength = adaptiveMedia.getValueOptional(AMAttribute.getContentLengthAMAttribute());
        Assert.assertEquals(_fileVersion.getSize(), ((long) (contentLength.get())));
        Mockito.verify(_amAsyncProcessor).triggerProcess(_fileVersion, String.valueOf(_fileVersion.getFileVersionId()));
    }

    private final AMAsyncProcessor<FileVersion, ?> _amAsyncProcessor = Mockito.mock(AMAsyncProcessor.class);

    private final AMAsyncProcessorLocator _amAsyncProcessorLocator = Mockito.mock(AMAsyncProcessorLocator.class);

    private final AMImageConfigurationHelper _amImageConfigurationHelper = Mockito.mock(AMImageConfigurationHelper.class);

    private final AMImageFinder _amImageFinder = Mockito.mock(AMImageFinder.class);

    private final AMImageRequestHandler _amImageRequestHandler = new AMImageRequestHandler();

    private FileVersion _fileVersion;

    private final PathInterpreter _pathInterpreter = Mockito.mock(PathInterpreter.class);
}

