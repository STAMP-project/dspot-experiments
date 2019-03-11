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
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.snapshot;


import FsRepository.COMPRESS_SETTING;
import FsRepository.LOCATION_SETTING;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.common.settings.Settings;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class CreateSnapshotRepositoryRequestExecutorImplTest {
    @Test
    public void testCreatePutRepositoryRequestBuilder() {
        CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest = new CreateSnapshotRepositoryRequest("name", "location");
        createSnapshotRepositoryRequest.setCompress(true);
        createSnapshotRepositoryRequest.setType("type");
        createSnapshotRepositoryRequest.setVerify(true);
        CreateSnapshotRepositoryRequestExecutorImpl createSnapshotRepositoryRequestExecutorImpl = new CreateSnapshotRepositoryRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        PutRepositoryRequestBuilder putRepositoryRequestBuilder = createSnapshotRepositoryRequestExecutorImpl.createPutRepositoryRequestBuilder(createSnapshotRepositoryRequest);
        PutRepositoryRequest putRepositoryRequest = putRepositoryRequestBuilder.request();
        Settings settings = putRepositoryRequest.settings();
        Assert.assertEquals(String.valueOf(createSnapshotRepositoryRequest.isCompress()), settings.get(COMPRESS_SETTING.getKey()));
        Assert.assertEquals(String.valueOf(createSnapshotRepositoryRequest.getLocation()), settings.get(LOCATION_SETTING.getKey()));
        Assert.assertEquals(createSnapshotRepositoryRequest.getName(), putRepositoryRequest.name());
        Assert.assertEquals(createSnapshotRepositoryRequest.getType(), putRepositoryRequest.type());
        Assert.assertEquals(createSnapshotRepositoryRequest.isVerify(), putRepositoryRequest.verify());
    }

    private ElasticsearchFixture _elasticsearchFixture;
}

