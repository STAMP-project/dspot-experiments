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
package com.liferay.adaptive.media.content.transformer.internal;


import com.liferay.adaptive.media.content.transformer.ContentTransformer;
import com.liferay.adaptive.media.content.transformer.ContentTransformerContentType;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alejandro Tard?n
 */
public class ContentTransformerHandlerImplTest {
    @Test
    public void testIgnoresTheContentTransformersForDifferentContentTypes() throws Exception {
        ContentTransformerContentType<String> contentTransformerContentTypeA = new ContentTransformerHandlerImplTest.TestContentTransformerContentType();
        String transformedContentA = RandomTestUtil.randomString();
        _registerContentTransformer(contentTransformerContentTypeA, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT, transformedContentA);
        ContentTransformerContentType<String> contentTransformerContentTypeB = new ContentTransformerHandlerImplTest.TestContentTransformerContentType();
        String transformedContentB = RandomTestUtil.randomString();
        _registerContentTransformer(contentTransformerContentTypeB, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT, transformedContentB);
        Assert.assertEquals(transformedContentA, _contentTransformerHandlerImpl.transform(contentTransformerContentTypeA, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT));
        Assert.assertEquals(transformedContentB, _contentTransformerHandlerImpl.transform(contentTransformerContentTypeB, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT));
    }

    @Test
    public void testReturnsTheContentTransformedByAChainOfContentTransformers() throws Exception {
        String intermediateTransformedContent = RandomTestUtil.randomString();
        _registerContentTransformer(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT, intermediateTransformedContent);
        String finalTransformedContent = RandomTestUtil.randomString();
        _registerContentTransformer(_contentTransformerContentType, intermediateTransformedContent, finalTransformedContent);
        Assert.assertEquals(finalTransformedContent, _contentTransformerHandlerImpl.transform(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT));
    }

    @Test
    public void testReturnsTheContentTransformedByAContentTransformerForAContentType() throws Exception {
        String transformedContent = RandomTestUtil.randomString();
        _registerContentTransformer(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT, transformedContent);
        Assert.assertEquals(transformedContent, _contentTransformerHandlerImpl.transform(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT));
    }

    @Test
    public void testReturnsTheSameContentIfAContentTransformerThrowsAnException() throws Exception {
        _registerInvalidContentTransformer(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT);
        Assert.assertSame(ContentTransformerHandlerImplTest._ORIGINAL_CONTENT, _contentTransformerHandlerImpl.transform(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT));
    }

    @Test
    public void testReturnsTheSameContentIfThereAreNoContentTransformers() {
        Assert.assertSame(ContentTransformerHandlerImplTest._ORIGINAL_CONTENT, _contentTransformerHandlerImpl.transform(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT));
    }

    @Test
    public void testRunsTheOtherContentTransformersEvenIfOneOfThemFails() throws Exception {
        _registerInvalidContentTransformer(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT);
        String transformedContent = RandomTestUtil.randomString();
        _registerContentTransformer(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT, transformedContent);
        Assert.assertEquals(transformedContent, _contentTransformerHandlerImpl.transform(_contentTransformerContentType, ContentTransformerHandlerImplTest._ORIGINAL_CONTENT));
    }

    private static final String _ORIGINAL_CONTENT = RandomTestUtil.randomString();

    private final ContentTransformerContentType<String> _contentTransformerContentType = new ContentTransformerHandlerImplTest.TestContentTransformerContentType();

    private final ContentTransformerHandlerImpl _contentTransformerHandlerImpl = new ContentTransformerHandlerImpl();

    private final ContentTransformerHandlerImplTest.MockServiceTrackerMap _mockServiceTrackerMap = new ContentTransformerHandlerImplTest.MockServiceTrackerMap();

    private static class TestContentTransformerContentType<T> implements ContentTransformerContentType<T> {
        @Override
        public String getKey() {
            return "test";
        }
    }

    private final class MockServiceTrackerMap implements ServiceTrackerMap<ContentTransformerContentType, List<ContentTransformer>> {
        @Override
        public void close() {
            _contentTransformers.clear();
        }

        @Override
        public boolean containsKey(ContentTransformerContentType contentTransformerContentType) {
            return _contentTransformers.containsKey(contentTransformerContentType);
        }

        @Override
        public List<ContentTransformer> getService(ContentTransformerContentType contentTransformerContentType) {
            return _contentTransformers.get(contentTransformerContentType);
        }

        @Override
        public Set<ContentTransformerContentType> keySet() {
            return _contentTransformers.keySet();
        }

        public void register(ContentTransformer contentTransformer) {
            List<ContentTransformer> contentTransformers = _contentTransformers.computeIfAbsent(contentTransformer.getContentTransformerContentType(), ( key) -> new ArrayList<>());
            contentTransformers.add(contentTransformer);
        }

        @Override
        public Collection<List<ContentTransformer>> values() {
            return _contentTransformers.values();
        }

        private final Map<ContentTransformerContentType, List<ContentTransformer>> _contentTransformers = new HashMap<>();
    }
}

