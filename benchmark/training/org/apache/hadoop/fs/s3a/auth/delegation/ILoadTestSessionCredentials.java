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
package org.apache.hadoop.fs.s3a.auth.delegation;


import ContractTestUtils.NanoTimer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.s3a.scale.S3AScaleTestBase;
import org.apache.hadoop.util.concurrent.HadoopExecutors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test has a unique name as it is designed to do something special:
 * generate enough load on the AWS STS service to get some
 * statistics on its throttling.
 * This isn't documented anywhere, and for DT support it's
 * important to know how much effort it takes to overload the service.
 *
 * <b>Important</b>
 *
 * If this test does trigger STS throttling, then all users in the same
 * AWS account will experience throttling. This may be observable,
 * in delays and, if the applications in use are not resilient to
 * throttling events in STS, from application failures.
 *
 * Use with caution.
 * <ol>
 *   <li>Don't run it on an AWS endpoint which other users in a
 *   shared AWS account are actively using. </li>
 *   <li>Don't run it on the same AWS account which is being used for
 *   any production service.</li>
 *   <li>And choose a time (weekend, etc) where the account is under-used.</li>
 *   <li>Warn your fellow users.</li>
 * </ol>
 *
 * In experiments, the throttling recovers fast and appears restricted
 * to the single STS service which the test overloads.
 *
 * @see <a href="https://github.com/steveloughran/datasets/releases/tag/tag_2018-09-17-aws">
AWS STS login throttling statistics</a>
 */
public class ILoadTestSessionCredentials extends S3AScaleTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ILoadTestSessionCredentials.class);

    protected static final int THREADS = 100;

    private final ExecutorService executor = HadoopExecutors.newFixedThreadPool(ILoadTestSessionCredentials.THREADS, new ThreadFactoryBuilder().setNameFormat("DelegationTokenFetcher #%d").build());

    private final CompletionService<ILoadTestSessionCredentials.Outcome> completionService = new ExecutorCompletionService<>(executor);

    private File dataDir;

    @Test
    public void testCreate10Tokens() throws Throwable {
        File file = fetchTokens(10);
        String csv = FileUtils.readFileToString(file, "UTF-8");
        ILoadTestSessionCredentials.LOG.info("CSV data\n{}", csv);
    }

    @Test
    public void testCreateManyTokens() throws Throwable {
        fetchTokens(50000);
    }

    /**
     * Outcome of one of the load operations.
     */
    private static class Outcome {
        private final int id;

        private final long startTime;

        private final NanoTimer timer;

        private final Exception exception;

        Outcome(final int id, final long startTime, final ContractTestUtils.NanoTimer timer, final Exception exception) {
            this.id = id;
            this.startTime = startTime;
            this.timer = timer;
            this.exception = exception;
        }

        /**
         * Write this record.
         *
         * @param out
         * 		the csvout to write through.
         * @return the csvout instance
         * @throws IOException
         * 		IO failure.
         */
        public Csvout writeln(Csvout out) throws IOException {
            return out.write(id, startTime, ((exception) == null ? 1 : 0), timer.getStartTime(), timer.getEndTime(), timer.duration(), (('"' + ((exception) == null ? "" : exception.getMessage())) + '"')).newline();
        }

        /**
         * Write the schema of the outcome records.
         *
         * @param out
         * 		CSV destinatin
         * @throws IOException
         * 		IO failure.
         */
        public static void writeSchema(Csvout out) throws IOException {
            out.write("id", "starttime", "success", "started", "ended", "duration", "error");
        }
    }
}

