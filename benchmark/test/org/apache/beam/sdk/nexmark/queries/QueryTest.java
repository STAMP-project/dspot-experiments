/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.nexmark.queries;


import NexmarkConfiguration.DEFAULT;
import org.apache.beam.sdk.nexmark.NexmarkConfiguration;
import org.apache.beam.sdk.nexmark.queries.sql.SqlQuery1;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.UsesStatefulParDo;
import org.apache.beam.sdk.testing.UsesTimersInParDo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test the various NEXMark queries yield results coherent with their models.
 */
@RunWith(JUnit4.class)
public class QueryTest {
    private static final NexmarkConfiguration CONFIG = DEFAULT.copy();

    static {
        // careful, results of tests are linked to numEventGenerators because of timestamp generation
        QueryTest.CONFIG.numEventGenerators = 1;
        QueryTest.CONFIG.numEvents = 5000;
    }

    @Rule
    public TestPipeline p = TestPipeline.create();

    @Test
    @Category(NeedsRunner.class)
    public void query0MatchesModelBatch() {
        queryMatchesModel("Query0TestBatch", new Query0(), new Query0Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query0MatchesModelStreaming() {
        queryMatchesModel("Query0TestStreaming", new Query0(), new Query0Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query1MatchesModelBatch() {
        queryMatchesModel("Query1TestBatch", new Query1(QueryTest.CONFIG), new Query1Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query1MatchesModelStreaming() {
        queryMatchesModel("Query1TestStreaming", new Query1(QueryTest.CONFIG), new Query1Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery1MatchesModelBatch() {
        queryMatchesModel("SqlQuery1TestBatch", new SqlQuery1(), new Query1Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery1MatchesModelStreaming() {
        queryMatchesModel("SqlQuery1TestStreaming", new SqlQuery1(), new Query1Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query2MatchesModelBatch() {
        queryMatchesModel("Query2TestBatch", new Query2(QueryTest.CONFIG), new Query2Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query2MatchesModelStreaming() {
        queryMatchesModel("Query2TestStreaming", new Query2(QueryTest.CONFIG), new Query2Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery2MatchesModelBatch() {
        queryMatchesModel("SqlQuery2TestBatch", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery2(QueryTest.CONFIG.auctionSkip), new Query2Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery2MatchesModelStreaming() {
        queryMatchesModel("SqlQuery2TestStreaming", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery2(QueryTest.CONFIG.auctionSkip), new Query2Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category({ NeedsRunner.class, UsesStatefulParDo.class, UsesTimersInParDo.class })
    public void query3MatchesModelBatch() {
        queryMatchesModel("Query3TestBatch", new Query3(QueryTest.CONFIG), new Query3Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category({ NeedsRunner.class, UsesStatefulParDo.class, UsesTimersInParDo.class })
    public void query3MatchesModelStreaming() {
        queryMatchesModel("Query3TestStreaming", new Query3(QueryTest.CONFIG), new Query3Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category({ NeedsRunner.class, UsesStatefulParDo.class, UsesTimersInParDo.class })
    public void sqlQuery3MatchesModelBatch() {
        queryMatchesModel("SqlQuery3TestBatch", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery3(QueryTest.CONFIG), new Query3Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category({ NeedsRunner.class, UsesStatefulParDo.class, UsesTimersInParDo.class })
    public void sqlQuery3MatchesModelStreaming() {
        queryMatchesModel("SqlQuery3TestStreaming", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery3(QueryTest.CONFIG), new Query3Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query4MatchesModelBatch() {
        queryMatchesModel("Query4TestBatch", new Query4(QueryTest.CONFIG), new Query4Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query4MatchesModelStreaming() {
        queryMatchesModel("Query4TestStreaming", new Query4(QueryTest.CONFIG), new Query4Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query5MatchesModelBatch() {
        queryMatchesModel("Query5TestBatch", new Query5(QueryTest.CONFIG), new Query5Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query5MatchesModelStreaming() {
        queryMatchesModel("Query5TestStreaming", new Query5(QueryTest.CONFIG), new Query5Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery5MatchesModelBatch() {
        queryMatchesModel("SqlQuery5TestBatch", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery5(QueryTest.CONFIG), new Query5Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery5MatchesModelStreaming() {
        queryMatchesModel("SqlQuery5TestStreaming", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery5(QueryTest.CONFIG), new Query5Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query7MatchesModelBatch() {
        queryMatchesModel("Query7TestBatch", new Query7(QueryTest.CONFIG), new Query7Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query7MatchesModelStreaming() {
        queryMatchesModel("Query7TestStreaming", new Query7(QueryTest.CONFIG), new Query7Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery7MatchesModelBatch() {
        queryMatchesModel("SqlQuery7TestBatch", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery7(QueryTest.CONFIG), new Query7Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void sqlQuery7MatchesModelStreaming() {
        queryMatchesModel("SqlQuery7TestStreaming", new org.apache.beam.sdk.nexmark.queries.sql.SqlQuery7(QueryTest.CONFIG), new Query7Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query8MatchesModelBatch() {
        queryMatchesModel("Query8TestBatch", new Query8(QueryTest.CONFIG), new Query8Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query8MatchesModelStreaming() {
        queryMatchesModel("Query8TestStreaming", new Query8(QueryTest.CONFIG), new Query8Model(QueryTest.CONFIG), true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query9MatchesModelBatch() {
        queryMatchesModel("Query9TestBatch", new Query9(QueryTest.CONFIG), new Query9Model(QueryTest.CONFIG), false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void query9MatchesModelStreaming() {
        queryMatchesModel("Query9TestStreaming", new Query9(QueryTest.CONFIG), new Query9Model(QueryTest.CONFIG), true);
    }
}

