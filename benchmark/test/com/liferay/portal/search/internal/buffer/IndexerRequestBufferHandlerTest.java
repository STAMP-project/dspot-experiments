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
package com.liferay.portal.search.internal.buffer;


import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.search.buffer.IndexerRequest;
import com.liferay.portal.search.buffer.IndexerRequestBuffer;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Bryan Engler
 * @author Andr? de Oliveira
 */
public class IndexerRequestBufferHandlerTest {
    public IndexerRequestBufferHandlerTest() throws Exception {
        _method = Indexer.class.getDeclaredMethod("reindex", String.class, long.class);
    }

    @Test
    public void testDeepReindexMustNotOverflow() throws Exception {
        int maxBufferSize = 5;
        _indexerRequestBufferHandler = new IndexerRequestBufferHandler(createIndexerRequestBufferOverflowHandler(), createIndexerRegistryConfiguration(maxBufferSize));
        _indexerRequestBuffer = IndexerRequestBuffer.create();
        Indexer<?> indexer = createIndexerWithDeepReindex();
        List<IndexerRequest> indexerRequests = createIndexerRequests(indexer, (maxBufferSize + 3));
        for (IndexerRequest indexerRequest : indexerRequests) {
            _indexerRequestBufferHandler.bufferRequest(indexerRequest, _indexerRequestBuffer);
        }
    }

    private final Indexer<?> _indexer = Mockito.mock(Indexer.class);

    private IndexerRequestBuffer _indexerRequestBuffer;

    private IndexerRequestBufferHandler _indexerRequestBufferHandler;

    private final Method _method;
}

