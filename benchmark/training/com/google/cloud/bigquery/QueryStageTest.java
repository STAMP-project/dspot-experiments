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


import com.google.api.services.bigquery.model.ExplainQueryStep;
import com.google.cloud.bigquery.QueryStage.QueryStep;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class QueryStageTest {
    private static final List<String> SUBSTEPS1 = ImmutableList.of("substep1", "substep2");

    private static final List<String> SUBSTEPS2 = ImmutableList.of("substep3", "substep4");

    private static final QueryStep QUERY_STEP1 = new QueryStep("KIND", QueryStageTest.SUBSTEPS1);

    private static final QueryStep QUERY_STEP2 = new QueryStep("KIND", QueryStageTest.SUBSTEPS2);

    private static final long COMPLETED_PARALLEL_INPUTS = 3;

    private static final long COMPUTE_MS_AVG = 1234;

    private static final long COMPUTE_MS_MAX = 2345;

    private static final double COMPUTE_RATIO_AVG = 1.1;

    private static final double COMPUTE_RATIO_MAX = 2.2;

    private static final long END_MS = 1522540860000L;

    private static final long ID = 42L;

    private static final List<Long> INPUT_STAGES = ImmutableList.of(Long.valueOf(7), Long.valueOf(9));

    private static final String NAME = "StageName";

    private static final long PARALLEL_INPUTS = 4;

    private static final long READ_MS_AVG = 3456;

    private static final long READ_MS_MAX = 4567;

    private static final double READ_RATIO_AVG = 3.3;

    private static final double READ_RATIO_MAX = 4.4;

    private static final long RECORDS_READ = 5L;

    private static final long RECORDS_WRITTEN = 6L;

    private static final long SHUFFLE_OUTPUT_BYTES = 4096;

    private static final long SHUFFLE_OUTPUT_BYTES_SPILLED = 0;

    private static final long START_MS = 1522540800000L;

    private static final String STATUS = "COMPLETE";

    private static final List<QueryStep> STEPS = ImmutableList.of(QueryStageTest.QUERY_STEP1, QueryStageTest.QUERY_STEP2);

    private static final long WAIT_MS_AVG = 3333;

    private static final long WAIT_MS_MAX = 3344;

    private static final double WAIT_RATIO_AVG = 7.7;

    private static final double WAIT_RATIO_MAX = 8.8;

    private static final long WRITE_MS_AVG = 44;

    private static final long WRITE_MS_MAX = 50;

    private static final double WRITE_RATIO_AVG = 9.9;

    private static final double WRITE_RATIO_MAX = 10.1;

    private static final QueryStage QUERY_STAGE = QueryStage.newBuilder().setCompletedParallelInputs(QueryStageTest.COMPLETED_PARALLEL_INPUTS).setComputeMsAvg(QueryStageTest.COMPUTE_MS_AVG).setComputeMsMax(QueryStageTest.COMPUTE_MS_MAX).setComputeRatioAvg(QueryStageTest.COMPUTE_RATIO_AVG).setComputeRatioMax(QueryStageTest.COMPUTE_RATIO_MAX).setEndMs(QueryStageTest.END_MS).setGeneratedId(QueryStageTest.ID).setInputStages(QueryStageTest.INPUT_STAGES).setName(QueryStageTest.NAME).setParallelInputs(QueryStageTest.PARALLEL_INPUTS).setReadMsAvg(QueryStageTest.READ_MS_AVG).setReadMsMax(QueryStageTest.READ_MS_MAX).setReadRatioAvg(QueryStageTest.READ_RATIO_AVG).setReadRatioMax(QueryStageTest.READ_RATIO_MAX).setRecordsRead(QueryStageTest.RECORDS_READ).setRecordsWritten(QueryStageTest.RECORDS_WRITTEN).setShuffleOutputBytes(QueryStageTest.SHUFFLE_OUTPUT_BYTES).setShuffleOutputBytesSpilled(QueryStageTest.SHUFFLE_OUTPUT_BYTES_SPILLED).setStartMs(QueryStageTest.START_MS).setStatus(QueryStageTest.STATUS).setSteps(QueryStageTest.STEPS).setWaitMsAvg(QueryStageTest.WAIT_MS_AVG).setWaitMsMax(QueryStageTest.WAIT_MS_MAX).setWaitRatioAvg(QueryStageTest.WAIT_RATIO_AVG).setWaitRatioMax(QueryStageTest.WAIT_RATIO_MAX).setWriteMsAvg(QueryStageTest.WRITE_MS_AVG).setWriteMsMax(QueryStageTest.WRITE_MS_MAX).setWriteRatioAvg(QueryStageTest.WRITE_RATIO_AVG).setWriteRatioMax(QueryStageTest.WRITE_RATIO_MAX).build();

    @Test
    public void testQueryStepConstructor() {
        Assert.assertEquals("KIND", QueryStageTest.QUERY_STEP1.getName());
        Assert.assertEquals("KIND", QueryStageTest.QUERY_STEP2.getName());
        Assert.assertEquals(QueryStageTest.SUBSTEPS1, QueryStageTest.QUERY_STEP1.getSubsteps());
        Assert.assertEquals(QueryStageTest.SUBSTEPS2, QueryStageTest.QUERY_STEP2.getSubsteps());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(QueryStageTest.COMPLETED_PARALLEL_INPUTS, QueryStageTest.QUERY_STAGE.getCompletedParallelInputs());
        Assert.assertEquals(QueryStageTest.COMPUTE_MS_AVG, QueryStageTest.QUERY_STAGE.getComputeMsAvg());
        Assert.assertEquals(QueryStageTest.COMPUTE_MS_MAX, QueryStageTest.QUERY_STAGE.getComputeMsMax());
        Assert.assertEquals(QueryStageTest.COMPUTE_RATIO_AVG, QueryStageTest.QUERY_STAGE.getComputeRatioAvg(), 0);
        Assert.assertEquals(QueryStageTest.COMPUTE_RATIO_MAX, QueryStageTest.QUERY_STAGE.getComputeRatioMax(), 0);
        Assert.assertEquals(QueryStageTest.END_MS, QueryStageTest.QUERY_STAGE.getEndMs());
        Assert.assertEquals(QueryStageTest.ID, QueryStageTest.QUERY_STAGE.getGeneratedId());
        Assert.assertEquals(QueryStageTest.INPUT_STAGES, QueryStageTest.QUERY_STAGE.getInputStages());
        Assert.assertEquals(QueryStageTest.PARALLEL_INPUTS, QueryStageTest.QUERY_STAGE.getParallelInputs());
        Assert.assertEquals(QueryStageTest.NAME, QueryStageTest.QUERY_STAGE.getName());
        Assert.assertEquals(QueryStageTest.READ_RATIO_AVG, QueryStageTest.QUERY_STAGE.getReadRatioAvg(), 0);
        Assert.assertEquals(QueryStageTest.READ_RATIO_MAX, QueryStageTest.QUERY_STAGE.getReadRatioMax(), 0);
        Assert.assertEquals(QueryStageTest.RECORDS_READ, QueryStageTest.QUERY_STAGE.getRecordsRead());
        Assert.assertEquals(QueryStageTest.RECORDS_WRITTEN, QueryStageTest.QUERY_STAGE.getRecordsWritten());
        Assert.assertEquals(QueryStageTest.SHUFFLE_OUTPUT_BYTES, QueryStageTest.QUERY_STAGE.getShuffleOutputBytes());
        Assert.assertEquals(QueryStageTest.SHUFFLE_OUTPUT_BYTES_SPILLED, QueryStageTest.QUERY_STAGE.getShuffleOutputBytesSpilled());
        Assert.assertEquals(QueryStageTest.START_MS, QueryStageTest.QUERY_STAGE.getStartMs());
        Assert.assertEquals(QueryStageTest.STATUS, QueryStageTest.QUERY_STAGE.getStatus());
        Assert.assertEquals(QueryStageTest.STEPS, QueryStageTest.QUERY_STAGE.getSteps());
        Assert.assertEquals(QueryStageTest.WAIT_MS_AVG, QueryStageTest.QUERY_STAGE.getWaitMsAvg());
        Assert.assertEquals(QueryStageTest.WAIT_MS_MAX, QueryStageTest.QUERY_STAGE.getWaitMsMax());
        Assert.assertEquals(QueryStageTest.WAIT_RATIO_AVG, QueryStageTest.QUERY_STAGE.getWaitRatioAvg(), 0);
        Assert.assertEquals(QueryStageTest.WAIT_RATIO_MAX, QueryStageTest.QUERY_STAGE.getWaitRatioMax(), 0);
        Assert.assertEquals(QueryStageTest.WRITE_MS_AVG, QueryStageTest.QUERY_STAGE.getWriteMsAvg());
        Assert.assertEquals(QueryStageTest.WRITE_MS_MAX, QueryStageTest.QUERY_STAGE.getWriteMsMax());
        Assert.assertEquals(QueryStageTest.WRITE_RATIO_AVG, QueryStageTest.QUERY_STAGE.getWriteRatioAvg(), 0);
        Assert.assertEquals(QueryStageTest.WRITE_RATIO_MAX, QueryStageTest.QUERY_STAGE.getWriteRatioMax(), 0);
    }

    @Test
    public void testToAndFromPb() {
        compareQueryStep(QueryStageTest.QUERY_STEP1, QueryStep.fromPb(QueryStageTest.QUERY_STEP1.toPb()));
        compareQueryStep(QueryStageTest.QUERY_STEP2, QueryStep.fromPb(QueryStageTest.QUERY_STEP2.toPb()));
        compareQueryStage(QueryStageTest.QUERY_STAGE, QueryStage.fromPb(QueryStageTest.QUERY_STAGE.toPb()));
        ExplainQueryStep stepPb = new ExplainQueryStep();
        stepPb.setKind("KIND");
        stepPb.setSubsteps(null);
        compareQueryStep(new QueryStep("KIND", ImmutableList.<String>of()), QueryStep.fromPb(stepPb));
    }

    @Test
    public void testEquals() {
        compareQueryStep(QueryStageTest.QUERY_STEP1, QueryStageTest.QUERY_STEP1);
        compareQueryStep(QueryStageTest.QUERY_STEP2, QueryStageTest.QUERY_STEP2);
        compareQueryStage(QueryStageTest.QUERY_STAGE, QueryStageTest.QUERY_STAGE);
    }
}

