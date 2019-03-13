/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import Acl.Group;
import Acl.Role.READER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DatasetInfoTest {
    private static final List<Acl> ACCESS_RULES = ImmutableList.of(Acl.of(Group.ofAllAuthenticatedUsers(), READER), Acl.of(new Acl.View(TableId.of("dataset", "table"))));

    private static final List<Acl> ACCESS_RULES_COMPLETE = ImmutableList.of(Acl.of(Group.ofAllAuthenticatedUsers(), READER), Acl.of(new Acl.View(TableId.of("project", "dataset", "table"))));

    private static final Map<String, String> LABELS = ImmutableMap.of("example-label1", "example-value1", "example-label2", "example-value2");

    private static final Long CREATION_TIME = System.currentTimeMillis();

    private static final Long DEFAULT_TABLE_EXPIRATION = (DatasetInfoTest.CREATION_TIME) + 100;

    private static final String DESCRIPTION = "description";

    private static final String ETAG = "0xFF00";

    private static final String FRIENDLY_NAME = "friendlyDataset";

    private static final String GENERATED_ID = "P/D:1";

    private static final Long LAST_MODIFIED = (DatasetInfoTest.CREATION_TIME) + 50;

    private static final String LOCATION = "";

    private static final String SELF_LINK = "http://bigquery/p/d";

    private static final DatasetId DATASET_ID = DatasetId.of("dataset");

    private static final DatasetId DATASET_ID_COMPLETE = DatasetId.of("project", "dataset");

    private static final DatasetInfo DATASET_INFO = DatasetInfo.newBuilder(DatasetInfoTest.DATASET_ID).setAcl(DatasetInfoTest.ACCESS_RULES).setCreationTime(DatasetInfoTest.CREATION_TIME).setDefaultTableLifetime(DatasetInfoTest.DEFAULT_TABLE_EXPIRATION).setDescription(DatasetInfoTest.DESCRIPTION).setEtag(DatasetInfoTest.ETAG).setFriendlyName(DatasetInfoTest.FRIENDLY_NAME).setGeneratedId(DatasetInfoTest.GENERATED_ID).setLastModified(DatasetInfoTest.LAST_MODIFIED).setLocation(DatasetInfoTest.LOCATION).setSelfLink(DatasetInfoTest.SELF_LINK).setLabels(DatasetInfoTest.LABELS).build();

    private static final DatasetInfo DATASET_INFO_COMPLETE = DatasetInfoTest.DATASET_INFO.toBuilder().setDatasetId(DatasetInfoTest.DATASET_ID_COMPLETE).setAcl(DatasetInfoTest.ACCESS_RULES_COMPLETE).build();

    @Test
    public void testToBuilder() {
        compareDatasets(DatasetInfoTest.DATASET_INFO, DatasetInfoTest.DATASET_INFO.toBuilder().build());
        DatasetInfo datasetInfo = DatasetInfoTest.DATASET_INFO.toBuilder().setDatasetId(DatasetId.of("dataset2")).setDescription("description2").build();
        Assert.assertEquals(DatasetId.of("dataset2"), datasetInfo.getDatasetId());
        Assert.assertEquals("description2", datasetInfo.getDescription());
        datasetInfo = datasetInfo.toBuilder().setDatasetId(DatasetInfoTest.DATASET_ID).setDescription("description").build();
        compareDatasets(DatasetInfoTest.DATASET_INFO, datasetInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        DatasetInfo datasetInfo = DatasetInfo.newBuilder(DatasetInfoTest.DATASET_ID).build();
        Assert.assertEquals(datasetInfo, datasetInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertNull(DatasetInfoTest.DATASET_INFO.getDatasetId().getProject());
        Assert.assertEquals(DatasetInfoTest.DATASET_ID, DatasetInfoTest.DATASET_INFO.getDatasetId());
        Assert.assertEquals(DatasetInfoTest.ACCESS_RULES, DatasetInfoTest.DATASET_INFO.getAcl());
        Assert.assertEquals(DatasetInfoTest.CREATION_TIME, DatasetInfoTest.DATASET_INFO.getCreationTime());
        Assert.assertEquals(DatasetInfoTest.DEFAULT_TABLE_EXPIRATION, DatasetInfoTest.DATASET_INFO.getDefaultTableLifetime());
        Assert.assertEquals(DatasetInfoTest.DESCRIPTION, DatasetInfoTest.DATASET_INFO.getDescription());
        Assert.assertEquals(DatasetInfoTest.ETAG, DatasetInfoTest.DATASET_INFO.getEtag());
        Assert.assertEquals(DatasetInfoTest.FRIENDLY_NAME, DatasetInfoTest.DATASET_INFO.getFriendlyName());
        Assert.assertEquals(DatasetInfoTest.GENERATED_ID, DatasetInfoTest.DATASET_INFO.getGeneratedId());
        Assert.assertEquals(DatasetInfoTest.LAST_MODIFIED, DatasetInfoTest.DATASET_INFO.getLastModified());
        Assert.assertEquals(DatasetInfoTest.LOCATION, DatasetInfoTest.DATASET_INFO.getLocation());
        Assert.assertEquals(DatasetInfoTest.SELF_LINK, DatasetInfoTest.DATASET_INFO.getSelfLink());
        Assert.assertEquals(DatasetInfoTest.DATASET_ID_COMPLETE, DatasetInfoTest.DATASET_INFO_COMPLETE.getDatasetId());
        Assert.assertEquals(DatasetInfoTest.ACCESS_RULES_COMPLETE, DatasetInfoTest.DATASET_INFO_COMPLETE.getAcl());
        Assert.assertEquals(DatasetInfoTest.CREATION_TIME, DatasetInfoTest.DATASET_INFO_COMPLETE.getCreationTime());
        Assert.assertEquals(DatasetInfoTest.DEFAULT_TABLE_EXPIRATION, DatasetInfoTest.DATASET_INFO_COMPLETE.getDefaultTableLifetime());
        Assert.assertEquals(DatasetInfoTest.DESCRIPTION, DatasetInfoTest.DATASET_INFO_COMPLETE.getDescription());
        Assert.assertEquals(DatasetInfoTest.ETAG, DatasetInfoTest.DATASET_INFO_COMPLETE.getEtag());
        Assert.assertEquals(DatasetInfoTest.FRIENDLY_NAME, DatasetInfoTest.DATASET_INFO_COMPLETE.getFriendlyName());
        Assert.assertEquals(DatasetInfoTest.GENERATED_ID, DatasetInfoTest.DATASET_INFO_COMPLETE.getGeneratedId());
        Assert.assertEquals(DatasetInfoTest.LAST_MODIFIED, DatasetInfoTest.DATASET_INFO_COMPLETE.getLastModified());
        Assert.assertEquals(DatasetInfoTest.LOCATION, DatasetInfoTest.DATASET_INFO_COMPLETE.getLocation());
        Assert.assertEquals(DatasetInfoTest.SELF_LINK, DatasetInfoTest.DATASET_INFO_COMPLETE.getSelfLink());
        Assert.assertEquals(DatasetInfoTest.LABELS, DatasetInfoTest.DATASET_INFO_COMPLETE.getLabels());
    }

    @Test
    public void testOf() {
        DatasetInfo datasetInfo = DatasetInfo.of(DatasetInfoTest.DATASET_ID.getDataset());
        Assert.assertEquals(DatasetInfoTest.DATASET_ID, datasetInfo.getDatasetId());
        Assert.assertNull(datasetInfo.getAcl());
        Assert.assertNull(datasetInfo.getCreationTime());
        Assert.assertNull(datasetInfo.getDefaultTableLifetime());
        Assert.assertNull(datasetInfo.getDescription());
        Assert.assertNull(datasetInfo.getEtag());
        Assert.assertNull(datasetInfo.getFriendlyName());
        Assert.assertNull(datasetInfo.getGeneratedId());
        Assert.assertNull(datasetInfo.getLastModified());
        Assert.assertNull(datasetInfo.getLocation());
        Assert.assertNull(datasetInfo.getSelfLink());
        Assert.assertTrue(datasetInfo.getLabels().isEmpty());
        datasetInfo = DatasetInfo.of(DatasetInfoTest.DATASET_ID);
        Assert.assertEquals(DatasetInfoTest.DATASET_ID, datasetInfo.getDatasetId());
        Assert.assertNull(datasetInfo.getAcl());
        Assert.assertNull(datasetInfo.getCreationTime());
        Assert.assertNull(datasetInfo.getDefaultTableLifetime());
        Assert.assertNull(datasetInfo.getDescription());
        Assert.assertNull(datasetInfo.getEtag());
        Assert.assertNull(datasetInfo.getFriendlyName());
        Assert.assertNull(datasetInfo.getGeneratedId());
        Assert.assertNull(datasetInfo.getLastModified());
        Assert.assertNull(datasetInfo.getLocation());
        Assert.assertNull(datasetInfo.getSelfLink());
        Assert.assertTrue(datasetInfo.getLabels().isEmpty());
    }

    @Test
    public void testToPbAndFromPb() {
        compareDatasets(DatasetInfoTest.DATASET_INFO_COMPLETE, DatasetInfo.fromPb(DatasetInfoTest.DATASET_INFO_COMPLETE.toPb()));
        DatasetInfo datasetInfo = DatasetInfo.newBuilder("project", "dataset").build();
        compareDatasets(datasetInfo, DatasetInfo.fromPb(datasetInfo.toPb()));
    }

    @Test
    public void testSetProjectId() {
        Assert.assertEquals(DatasetInfoTest.DATASET_INFO_COMPLETE, DatasetInfoTest.DATASET_INFO.setProjectId("project"));
    }
}

