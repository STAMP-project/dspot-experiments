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
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.index;


import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.index.AnalyzeIndexRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class AnalyzeIndexRequestExecutorTest {
    @Test
    public void testIndexRequestTranslation() {
        AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();
        analyzeIndexRequest.setIndexName(AnalyzeIndexRequestExecutorTest._INDEX_NAME);
        AnalyzeIndexRequestExecutorImpl analyzeIndexRequestExecutorImpl = new AnalyzeIndexRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        AnalyzeRequestBuilder analyzeRequestBuilder = analyzeIndexRequestExecutorImpl.createAnalyzeRequestBuilder(analyzeIndexRequest);
        AnalyzeRequest analyzeRequest = analyzeRequestBuilder.request();
        Assert.assertEquals(AnalyzeIndexRequestExecutorTest._INDEX_NAME, analyzeRequest.index());
    }

    private static final String _INDEX_NAME = "test_request_index";

    private ElasticsearchFixture _elasticsearchFixture;
}

