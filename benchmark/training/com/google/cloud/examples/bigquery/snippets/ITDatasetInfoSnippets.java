/**
 * Copyright 2018 Google LLC
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
package com.google.cloud.examples.bigquery.snippets;


import com.google.cloud.bigquery.Acl;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITDatasetInfoSnippets {
    private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();

    private static BigQuery bigquery;

    private static DatasetInfoSnippets datasetInfoSnippets;

    private static ByteArrayOutputStream bout;

    private static PrintStream out;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testUpdateDatasetAccess() throws InterruptedException {
        Dataset dataset = ITDatasetInfoSnippets.bigquery.getDataset(ITDatasetInfoSnippets.DATASET);
        List<Acl> beforeAcls = ITDatasetInfoSnippets.datasetInfoSnippets.updateDatasetAccess(dataset);
        dataset = ITDatasetInfoSnippets.bigquery.getDataset(ITDatasetInfoSnippets.DATASET);
        List<Acl> afterAcls = dataset.getAcl();
        Assert.assertEquals(((beforeAcls.size()) + 1), afterAcls.size());
    }

    @Test
    public void testUpdateDatasetExpiration() throws InterruptedException {
        Dataset dataset = ITDatasetInfoSnippets.bigquery.getDataset(ITDatasetInfoSnippets.DATASET);
        Long beforeExpiration = ITDatasetInfoSnippets.datasetInfoSnippets.updateDatasetExpiration(dataset);
        dataset = ITDatasetInfoSnippets.bigquery.getDataset(ITDatasetInfoSnippets.DATASET);
        Long afterExpiration = dataset.getDefaultTableLifetime();
        Assert.assertNotEquals(beforeExpiration, afterExpiration);
    }
}

