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


import com.liferay.petra.string.StringBundler;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import java.util.Arrays;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class UpdateIndexSettingsIndexRequestExecutorTest {
    @Test
    public void testIndexRequestTranslation() {
        UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest = new UpdateIndexSettingsIndexRequest(UpdateIndexSettingsIndexRequestExecutorTest._INDEX_NAME);
        StringBundler sb = new StringBundler(10);
        sb.append("{\n");
        sb.append("    \"analysis\": {\n");
        sb.append("        \"analyzer\": {\n");
        sb.append("            \"content\": {\n");
        sb.append("                \"tokenizer\": \"whitespace\",\n");
        sb.append("                \"type\": \"custom\"\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}");
        updateIndexSettingsIndexRequest.setSettings(sb.toString());
        UpdateIndexSettingsIndexRequestExecutorImpl updateIndexSettingsIndexRequestExecutorImpl = new UpdateIndexSettingsIndexRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
                setIndicesOptionsTranslator(_indicesOptionsTranslator);
            }
        };
        UpdateSettingsRequestBuilder updateSettingsRequestBuilder = updateIndexSettingsIndexRequestExecutorImpl.createUpdateSettingsRequestBuilder(updateIndexSettingsIndexRequest);
        UpdateSettingsRequest updateSettingsRequest = updateSettingsRequestBuilder.request();
        String[] indices = updateSettingsRequest.indices();
        Assert.assertEquals(Arrays.toString(indices), 1, indices.length);
        Assert.assertEquals(UpdateIndexSettingsIndexRequestExecutorTest._INDEX_NAME, indices[0]);
    }

    private static final String _INDEX_NAME = "test_request_index";

    private ElasticsearchFixture _elasticsearchFixture;

    private IndicesOptionsTranslator _indicesOptionsTranslator;
}

