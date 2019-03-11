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


import org.junit.Assert;
import org.junit.Test;


public class DatasetIdTest {
    private static final DatasetId DATASET = DatasetId.of("dataset");

    private static final DatasetId DATASET_COMPLETE = DatasetId.of("project", "dataset");

    @Test
    public void testOf() {
        Assert.assertEquals(null, DatasetIdTest.DATASET.getProject());
        Assert.assertEquals("dataset", DatasetIdTest.DATASET.getDataset());
        Assert.assertEquals("project", DatasetIdTest.DATASET_COMPLETE.getProject());
        Assert.assertEquals("dataset", DatasetIdTest.DATASET_COMPLETE.getDataset());
    }

    @Test
    public void testEquals() {
        compareDatasetIds(DatasetIdTest.DATASET, DatasetId.of("dataset"));
        compareDatasetIds(DatasetIdTest.DATASET_COMPLETE, DatasetId.of("project", "dataset"));
    }

    @Test
    public void testToPbAndFromPb() {
        compareDatasetIds(DatasetIdTest.DATASET, DatasetId.fromPb(DatasetIdTest.DATASET.toPb()));
        compareDatasetIds(DatasetIdTest.DATASET_COMPLETE, DatasetId.fromPb(DatasetIdTest.DATASET_COMPLETE.toPb()));
    }

    @Test
    public void testSetProjectId() {
        Assert.assertEquals(DatasetIdTest.DATASET_COMPLETE, DatasetIdTest.DATASET.setProjectId("project"));
    }
}

